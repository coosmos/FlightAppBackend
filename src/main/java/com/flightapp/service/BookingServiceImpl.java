package com.flightapp.service;

import com.flightapp.dto.BookingRequest;
import com.flightapp.dto.BookingResponse;
import com.flightapp.dto.PassengerRequest;
import com.flightapp.entity.Booking;
import com.flightapp.entity.Flight;
import com.flightapp.entity.Passenger;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.FlightRepository;
import com.flightapp.repository.PassengerRepository;
import com.flightapp.service.BookingService;
import com.flightapp.service.FlightService;
import com.flightapp.service.PNRGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;
    private final FlightService flightService;
    private final PNRGeneratorService pnrGeneratorService;

    @Override
    public BookingResponse bookFlight(Long flightId, BookingRequest request) {
        log.info("Booking flight ID: {} for email: {}", flightId, request.getEmail());

        // Fetch flight
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new com.flightapp.exceptions.ResourceNotFoundException(
                    "Flight", "id", flightId));

        // Validate flight is active and scheduled
        if (!flight.getIsActive() || flight.getFlightStatus() != Flight.FlightStatus.SCHEDULED) {
            throw new com.flightapp.exceptions.BusinessException(
                "Flight is not available for booking");
        }

        // Validate passenger count matches
        int passengerCount = request.getPassengers().size();
        if (passengerCount < 1 || passengerCount > 9) {
            throw new com.flightapp.exceptions.BusinessException(
                "Number of passengers must be between 1 and 9");
        }

        // Check seat availability
        if (flight.getAvailableSeats() < passengerCount) {
            throw new com.flightapp.exceptions.BusinessException(
                "Not enough seats available. Only " + flight.getAvailableSeats() + " seats remaining");
        }

        // Validate seat numbers are unique and not already booked
        validateSeatNumbers(flightId, request.getPassengers());

        // Generate unique PNR
        String pnr = generateUniquePNR();

        // Calculate total amount
        BigDecimal totalAmount = flight.getBasePrice().multiply(BigDecimal.valueOf(passengerCount));

        // Create booking
        Booking booking = Booking.builder()
                .pnr(pnr)
                .flight(flight)
                .email(request.getEmail())
                .contactName(request.getContactName())
                .numberOfSeats(passengerCount)
                .totalAmount(totalAmount)
                .bookingStatus(Booking.BookingStatus.CONFIRMED)
                .build();

        // Add passengers
        for (PassengerRequest passengerReq : request.getPassengers()) {
            Passenger passenger = Passenger.builder()
                    .passengerName(passengerReq.getPassengerName())
                    .gender(Passenger.Gender.valueOf(passengerReq.getGender()))
                    .age(passengerReq.getAge())
                    .mealPreference(Passenger.MealPreference.valueOf(passengerReq.getMealPreference()))
                    .seatNumber(passengerReq.getSeatNumber())
                    .build();
            
            booking.addPassenger(passenger);
        }

        // Save booking (cascades to passengers)
        Booking savedBooking = bookingRepository.save(booking);

        // Update available seats
        flightService.updateAvailableSeats(flightId, -passengerCount);

        log.info("Booking successful. PNR: {}", pnr);

        return mapToResponse(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingByPNR(String pnr) {
        log.info("Fetching booking with PNR: {}", pnr);

        Booking booking = bookingRepository.findByPnr(pnr)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with PNR: " + pnr));

        return mapToResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingHistory(String email) {
        log.info("Fetching booking history for email: {}", email);

        List<Booking> bookings = bookingRepository.findByEmailOrderByCreatedAtDesc(email);

        return bookings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelBooking(String pnr) {
        log.info("Cancelling booking with PNR: {}", pnr);

        Booking booking = bookingRepository.findByPnr(pnr)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with PNR: " + pnr));

        // Check if already cancelled
        if (booking.getBookingStatus() == Booking.BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("Booking is already cancelled");
        }

        // Validate 24-hour cancellation rule
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime departureTime = booking.getFlight().getDepartureTime();
        Duration timeUntilDeparture = Duration.between(now, departureTime);

        if (timeUntilDeparture.toHours() < 24) {
            throw new IllegalArgumentException("Cannot cancel booking. Cancellation is only allowed 24 hours before departure");
        }

        // Update booking status
        booking.setBookingStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Release seats
        flightService.updateAvailableSeats(booking.getFlight().getId(), booking.getNumberOfSeats());

        log.info("Booking cancelled successfully. PNR: {}", pnr);
    }

    // Helper methods

    private void validateSeatNumbers(Long flightId, List<PassengerRequest> passengers) {
        // Check for duplicate seat numbers in request
        Set<String> seatNumbers = new HashSet<>();
        for (PassengerRequest passenger : passengers) {
            if (!seatNumbers.add(passenger.getSeatNumber())) {
                throw new IllegalArgumentException("Duplicate seat number: " + passenger.getSeatNumber());
            }
        }

        // Check if seats are already booked
        List<String> bookedSeats = passengerRepository.findBookedSeatsByFlightId(flightId);
        for (String seatNumber : seatNumbers) {
            if (bookedSeats.contains(seatNumber)) {
                throw new IllegalArgumentException("Seat " + seatNumber + " is already booked");
            }
        }
    }

    private String generateUniquePNR() {
        String pnr;
        int attempts = 0;
        do {
            pnr = pnrGeneratorService.generatePNR();
            attempts++;
            if (attempts > 10) {
                throw new RuntimeException("Failed to generate unique PNR after 10 attempts");
            }
        } while (bookingRepository.existsByPnr(pnr));
        
        return pnr;
    }

    private BookingResponse mapToResponse(Booking booking) {
        Flight flight = booking.getFlight();

        // Map flight details
        BookingResponse.FlightDetailsDto flightDetails = BookingResponse.FlightDetailsDto.builder()
                .flightId(flight.getId())
                .flightNumber(flight.getFlightNumber())
                .airlineName(flight.getAirline().getAirlineName())
                .fromLocation(flight.getFromLocation())
                .toLocation(flight.getToLocation())
                .departureTime(flight.getDepartureTime())
                .arrivalTime(flight.getArrivalTime())
                .build();

        // Map passenger details
        List<BookingResponse.PassengerDetailsDto> passengerDetails = booking.getPassengers().stream()
                .map(p -> BookingResponse.PassengerDetailsDto.builder()
                        .passengerName(p.getPassengerName())
                        .gender(p.getGender().name())
                        .age(p.getAge())
                        .seatNumber(p.getSeatNumber())
                        .mealPreference(p.getMealPreference().name())
                        .build())
                .collect(Collectors.toList());

        return BookingResponse.builder()
                .bookingId(booking.getId())
                .pnr(booking.getPnr())
                .contactName(booking.getContactName())
                .email(booking.getEmail())
                .numberOfSeats(booking.getNumberOfSeats())
                .totalAmount(booking.getTotalAmount())
                .bookingStatus(booking.getBookingStatus().name())
                .bookingDate(booking.getCreatedAt())
                .flight(flightDetails)
                .passengers(passengerDetails)
                .build();
    }
}