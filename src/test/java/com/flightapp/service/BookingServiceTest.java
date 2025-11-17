package com.flightapp.service;

import com.flightapp.dto.BookingRequest;
import com.flightapp.dto.BookingResponse;
import com.flightapp.dto.PassengerRequest;
import com.flightapp.entity.Airline;
import com.flightapp.entity.Booking;
import com.flightapp.entity.Flight;
import com.flightapp.entity.Passenger;
import com.flightapp.exceptions.BusinessException;
import com.flightapp.exceptions.ResourceNotFoundException;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.FlightRepository;
import com.flightapp.repository.PassengerRepository;
import com.flightapp.service.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private FlightService flightService;

    @Mock
    private PNRGeneratorService pnrGeneratorService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Airline airline;
    private Flight flight;
    private Booking booking;
    private BookingRequest bookingRequest;

    @BeforeEach
    void setUp() {
        airline = Airline.builder()
                .id(1L)
                .airlineName("IndiGo")
                .airlineCode("6E")
                .build();

        flight = Flight.builder()
                .id(1L)
                .flightNumber("6E2001")
                .airline(airline)
                .fromLocation("Delhi")
                .toLocation("Mumbai")
                .departureTime(LocalDateTime.now().plusDays(5))
                .arrivalTime(LocalDateTime.now().plusDays(5).plusHours(2))
                .totalSeats(180)
                .availableSeats(180)
                .basePrice(new BigDecimal("5000.00"))
                .flightStatus(Flight.FlightStatus.SCHEDULED)
                .isActive(true)
                .build();

        PassengerRequest passengerRequest = PassengerRequest.builder()
                .passengerName("John Doe")
                .gender("MALE")
                .age(30)
                .seatNumber("12A")
                .mealPreference("VEG")
                .build();

        bookingRequest = BookingRequest.builder()
                .contactName("John Doe")
                .email("john@example.com")
                .passengers(Arrays.asList(passengerRequest))
                .build();

        booking = Booking.builder()
                .id(1L)
                .pnr("251116ABCD")
                .flight(flight)
                .email("john@example.com")
                .contactName("John Doe")
                .numberOfSeats(1)
                .totalAmount(new BigDecimal("5000.00"))
                .bookingStatus(Booking.BookingStatus.CONFIRMED)
                .build();

        Passenger passenger = Passenger.builder()
                .id(1L)
                .booking(booking)
                .passengerName("John Doe")
                .gender(Passenger.Gender.MALE)
                .age(30)
                .seatNumber("12A")
                .mealPreference(Passenger.MealPreference.VEG)
                .build();

        booking.setPassengers(Arrays.asList(passenger));
    }

    @Test
    void testBookFlight_Success() {
        // Arrange
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(passengerRepository.findBookedSeatsByFlightId(1L)).thenReturn(Arrays.asList());
        when(pnrGeneratorService.generatePNR()).thenReturn("251116ABCD");
        when(bookingRepository.existsByPnr("251116ABCD")).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        doNothing().when(flightService).updateAvailableSeats(1L, -1);

        // Act
        BookingResponse response = bookingService.bookFlight(1L, bookingRequest);

        // Assert
        assertNotNull(response);
        assertEquals("251116ABCD", response.getPnr());
        assertEquals("john@example.com", response.getEmail());
        assertEquals(1, response.getNumberOfSeats());
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(flightService, times(1)).updateAvailableSeats(1L, -1);
    }

    @Test
    void testBookFlight_FlightNotFound_ThrowsException() {
        // Arrange
        when(flightRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            bookingService.bookFlight(999L, bookingRequest);
        });
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testBookFlight_FlightNotActive_ThrowsException() {
        // Arrange
        flight.setIsActive(false);
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            bookingService.bookFlight(1L, bookingRequest);
        });
    }

    @Test
    void testBookFlight_NotEnoughSeats_ThrowsException() {
        // Arrange
        flight.setAvailableSeats(0);
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            bookingService.bookFlight(1L, bookingRequest);
        });
    }

    @Test
    void testBookFlight_DuplicateSeatInRequest_ThrowsException() {
        // Arrange
        PassengerRequest passenger1 = PassengerRequest.builder()
                .passengerName("John Doe")
                .gender("MALE")
                .age(30)
                .seatNumber("12A")
                .mealPreference("VEG")
                .build();

        PassengerRequest passenger2 = PassengerRequest.builder()
                .passengerName("Jane Doe")
                .gender("FEMALE")
                .age(28)
                .seatNumber("12A")
                .mealPreference("VEG")
                .build();

        bookingRequest.setPassengers(Arrays.asList(passenger1, passenger2));

        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            bookingService.bookFlight(1L, bookingRequest);
        });
    }

    @Test
    void testBookFlight_SeatAlreadyBooked_ThrowsException() {
        // Arrange
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(passengerRepository.findBookedSeatsByFlightId(1L)).thenReturn(Arrays.asList("12A"));

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            bookingService.bookFlight(1L, bookingRequest);
        });
    }

    @Test
    void testGetBookingByPNR_Success() {
        // Arrange
        when(bookingRepository.findByPnr("251116ABCD")).thenReturn(Optional.of(booking));

        // Act
        BookingResponse response = bookingService.getBookingByPNR("251116ABCD");

        // Assert
        assertNotNull(response);
        assertEquals("251116ABCD", response.getPnr());
        assertEquals("john@example.com", response.getEmail());
    }

    @Test
    void testGetBookingByPNR_NotFound_ThrowsException() {
        // Arrange
        when(bookingRepository.findByPnr("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            bookingService.getBookingByPNR("INVALID");
        });
    }

    @Test
    void testGetBookingHistory_Success() {
        // Arrange
        when(bookingRepository.findByEmailOrderByCreatedAtDesc("john@example.com"))
                .thenReturn(Arrays.asList(booking));

        // Act
        List<BookingResponse> responses = bookingService.getBookingHistory("john@example.com");

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("john@example.com", responses.get(0).getEmail());
    }

    @Test
    void testGetBookingHistory_NoBookings() {
        // Arrange
        when(bookingRepository.findByEmailOrderByCreatedAtDesc("nobookings@example.com"))
                .thenReturn(Arrays.asList());

        // Act
        List<BookingResponse> responses = bookingService.getBookingHistory("nobookings@example.com");

        // Assert
        assertNotNull(responses);
        assertEquals(0, responses.size());
    }

    @Test
    void testCancelBooking_Success() {
        // Arrange
        when(bookingRepository.findByPnr("251116ABCD")).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        doNothing().when(flightService).updateAvailableSeats(1L, 1);

        // Act
        bookingService.cancelBooking("251116ABCD");

        // Assert
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(flightService, times(1)).updateAvailableSeats(1L, 1);
    }

    @Test
    void testCancelBooking_NotFound_ThrowsException() {
        // Arrange
        when(bookingRepository.findByPnr("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            bookingService.cancelBooking("INVALID");
        });
    }

    @Test
    void testCancelBooking_AlreadyCancelled_ThrowsException() {
        // Arrange
        booking.setBookingStatus(Booking.BookingStatus.CANCELLED);
        when(bookingRepository.findByPnr("251116ABCD")).thenReturn(Optional.of(booking));

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            bookingService.cancelBooking("251116ABCD");
        });
    }

    @Test
    void testCancelBooking_Within24Hours_ThrowsException() {
        // Arrange
        flight.setDepartureTime(LocalDateTime.now().plusHours(12)); // Less than 24 hours
        when(bookingRepository.findByPnr("251116ABCD")).thenReturn(Optional.of(booking));

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            bookingService.cancelBooking("251116ABCD");
        });
    }
}