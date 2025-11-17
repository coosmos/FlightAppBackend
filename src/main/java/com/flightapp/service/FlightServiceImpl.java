package com.flightapp.service;

import com.flightapp.dto.FlightInventoryRequest;
import com.flightapp.dto.FlightSearchRequest;
import com.flightapp.dto.FlightSearchResponse;
import com.flightapp.entity.Airline;
import com.flightapp.entity.Flight;
import com.flightapp.exceptions.BusinessException;
import com.flightapp.exceptions.DuplicateResourceException;
import com.flightapp.repository.AirlineRepository;
import com.flightapp.repository.FlightRepository;
import com.flightapp.service.FlightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final AirlineRepository airlineRepository;

    @Override
    public FlightSearchResponse addFlightInventory(FlightInventoryRequest request) {
        log.info("Adding flight inventory: {}", request.getFlightNumber());

        // Validate arrival time is after departure time
        if (!request.getArrivalTime().isAfter(request.getDepartureTime())) {
            throw new BusinessException(
                "Arrival time must be after departure time");
        }

        // Check if flight number already exists
        if (flightRepository.existsByFlightNumber(request.getFlightNumber())) {
            throw new DuplicateResourceException(
                "Flight", "flight number", request.getFlightNumber());
        }

        // Find airline
        Airline airline = airlineRepository.findByAirlineCode(request.getAirlineCode())
                .orElseThrow(() -> new com.flightapp.exceptions.ResourceNotFoundException(
                    "Airline", "airline code", request.getAirlineCode()));

        // Create flight
        Flight flight = Flight.builder()
                .flightNumber(request.getFlightNumber())
                .airline(airline)
                .fromLocation(request.getFromLocation())
                .toLocation(request.getToLocation())
                .departureTime(request.getDepartureTime())
                .arrivalTime(request.getArrivalTime())
                .totalSeats(request.getTotalSeats())
                .availableSeats(request.getTotalSeats()) // Initially all seats available
                .basePrice(request.getBasePrice())
                .flightStatus(Flight.FlightStatus.SCHEDULED)
                .isActive(true)
                .build();

        Flight savedFlight = flightRepository.save(flight);
        log.info("Flight inventory added successfully with ID: {}", savedFlight.getId());

        return mapToSearchResponse(savedFlight);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlightSearchResponse> searchFlights(FlightSearchRequest request) {
        log.info("Searching flights from {} to {} on {}", 
                request.getFromLocation(), request.getToLocation(), request.getTravelDate());

        // Create date range for the entire day
        LocalDateTime startDate = request.getTravelDate().atStartOfDay();
        LocalDateTime endDate = request.getTravelDate().atTime(LocalTime.MAX);

        // Search flights
        List<Flight> flights = flightRepository.searchFlights(
                request.getFromLocation(),
                request.getToLocation(),
                startDate,
                endDate,
                request.getNumberOfPassengers()
        );

        log.info("Found {} flights matching search criteria", flights.size());

        return flights.stream()
                .map(this::mapToSearchResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FlightSearchResponse getFlightById(Long flightId) {
        log.info("Fetching flight with ID: {}", flightId);
        
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new IllegalArgumentException("Flight not found with ID: " + flightId));

        return mapToSearchResponse(flight);
    }

    @Override
    public void updateAvailableSeats(Long flightId, Integer seatsToAdjust) {
        log.info("Updating available seats for flight ID: {}, adjustment: {}", flightId, seatsToAdjust);

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new IllegalArgumentException("Flight not found with ID: " + flightId));

        int newAvailableSeats = flight.getAvailableSeats() + seatsToAdjust;

        // Validate seat availability
        if (newAvailableSeats < 0) {
            throw new IllegalArgumentException("Not enough seats available");
        }

        if (newAvailableSeats > flight.getTotalSeats()) {
            throw new IllegalArgumentException("Available seats cannot exceed total seats");
        }

        flight.setAvailableSeats(newAvailableSeats);
        flightRepository.save(flight);

        log.info("Available seats updated. New count: {}", newAvailableSeats);
    }

    // Helper method to map Entity to DTO
    private FlightSearchResponse mapToSearchResponse(Flight flight) {
        // Calculate duration
        Duration duration = Duration.between(flight.getDepartureTime(), flight.getArrivalTime());
        String durationStr = String.format("%dh %dm", duration.toHours(), duration.toMinutesPart());

        return FlightSearchResponse.builder()
                .flightId(flight.getId())
                .flightNumber(flight.getFlightNumber())
                .airlineName(flight.getAirline().getAirlineName())
                .airlineCode(flight.getAirline().getAirlineCode())
                .fromLocation(flight.getFromLocation())
                .toLocation(flight.getToLocation())
                .departureTime(flight.getDepartureTime())
                .arrivalTime(flight.getArrivalTime())
                .availableSeats(flight.getAvailableSeats())
                .basePrice(flight.getBasePrice())
                .duration(durationStr)
                .build();
    }
}