package com.flightapp.service;

import com.flightapp.dto.FlightInventoryRequest;
import com.flightapp.dto.FlightSearchRequest;
import com.flightapp.dto.FlightSearchResponse;
import com.flightapp.entity.Airline;
import com.flightapp.entity.Flight;
import com.flightapp.exceptions.BusinessException;
import com.flightapp.exceptions.DuplicateResourceException;
import com.flightapp.exceptions.ResourceNotFoundException;
import com.flightapp.repository.AirlineRepository;
import com.flightapp.repository.FlightRepository;
import com.flightapp.service.FlightServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private FlightServiceImpl flightService;

    private Airline airline;
    private Flight flight;
    private FlightInventoryRequest inventoryRequest;

    @BeforeEach
    void setUp() {
        airline = Airline.builder()
                .id(1L)
                .airlineName("IndiGo")
                .airlineCode("6E")
                .isActive(true)
                .build();

        flight = Flight.builder()
                .id(1L)
                .flightNumber("6E2001")
                .airline(airline)
                .fromLocation("Delhi")
                .toLocation("Mumbai")
                .departureTime(LocalDateTime.of(2025, 11, 20, 10, 0))
                .arrivalTime(LocalDateTime.of(2025, 11, 20, 12, 30))
                .totalSeats(180)
                .availableSeats(180)
                .basePrice(new BigDecimal("5000.00"))
                .flightStatus(Flight.FlightStatus.SCHEDULED)
                .isActive(true)
                .build();

        inventoryRequest = FlightInventoryRequest.builder()
                .flightNumber("6E2001")
                .airlineCode("6E")
                .fromLocation("Delhi")
                .toLocation("Mumbai")
                .departureTime(LocalDateTime.of(2025, 11, 20, 10, 0))
                .arrivalTime(LocalDateTime.of(2025, 11, 20, 12, 30))
                .totalSeats(180)
                .basePrice(new BigDecimal("5000.00"))
                .build();
    }

    @Test
    void testAddFlightInventory_Success() {
        // Arrange
        when(flightRepository.existsByFlightNumber(inventoryRequest.getFlightNumber())).thenReturn(false);
        when(airlineRepository.findByAirlineCode("6E")).thenReturn(Optional.of(airline));
        when(flightRepository.save(any(Flight.class))).thenReturn(flight);
        
        FlightSearchResponse response = flightService.addFlightInventory(inventoryRequest);
        assertNotNull(response);
        assertEquals("6E2001", response.getFlightNumber());
        assertEquals(180, response.getAvailableSeats());
        verify(flightRepository, times(1)).save(any(Flight.class));
    }

    @Test
    void testAddFlightInventory_InvalidTime_ThrowsException() {
        inventoryRequest.setArrivalTime(LocalDateTime.of(2025, 11, 20, 9, 0));
        assertThrows(BusinessException.class, () -> {
            flightService.addFlightInventory(inventoryRequest);
        });
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void testAddFlightInventory_DuplicateFlightNumber_ThrowsException() {
        // Arrange
        when(flightRepository.existsByFlightNumber(inventoryRequest.getFlightNumber())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> {
            flightService.addFlightInventory(inventoryRequest);
        });
        verify(flightRepository, never()).save(any(Flight.class));
    }

    @Test
    void testAddFlightInventory_AirlineNotFound_ThrowsException() {
        // Arrange
        when(flightRepository.existsByFlightNumber(inventoryRequest.getFlightNumber())).thenReturn(false);
        when(airlineRepository.findByAirlineCode("6E")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            flightService.addFlightInventory(inventoryRequest);
        });
    }

    @Test
    void testSearchFlights_Success() {
        // Arrange
        FlightSearchRequest searchRequest = FlightSearchRequest.builder()
                .fromLocation("Delhi")
                .toLocation("Mumbai")
                .travelDate(LocalDate.of(2025, 11, 20))
                .numberOfPassengers(2)
                .build();

        LocalDateTime startDate = searchRequest.getTravelDate().atStartOfDay();
        LocalDateTime endDate = searchRequest.getTravelDate().atTime(LocalTime.MAX);

        when(flightRepository.searchFlights("Delhi", "Mumbai", startDate, endDate, 2))
                .thenReturn(Arrays.asList(flight));

        // Act
        List<FlightSearchResponse> responses = flightService.searchFlights(searchRequest);

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("6E2001", responses.get(0).getFlightNumber());
    }

    @Test
    void testSearchFlights_NoResults() {
        // Arrange
        FlightSearchRequest searchRequest = FlightSearchRequest.builder()
                .fromLocation("Delhi")
                .toLocation("Chennai")
                .travelDate(LocalDate.of(2025, 11, 20))
                .numberOfPassengers(2)
                .build();

        LocalDateTime startDate = searchRequest.getTravelDate().atStartOfDay();
        LocalDateTime endDate = searchRequest.getTravelDate().atTime(LocalTime.MAX);

        when(flightRepository.searchFlights("Delhi", "Chennai", startDate, endDate, 2))
                .thenReturn(Arrays.asList());

        // Act
        List<FlightSearchResponse> responses = flightService.searchFlights(searchRequest);

        // Assert
        assertNotNull(responses);
        assertEquals(0, responses.size());
    }

    @Test
    void testGetFlightById_Success() {
        // Arrange
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));

        // Act
        FlightSearchResponse response = flightService.getFlightById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getFlightId());
        assertEquals("6E2001", response.getFlightNumber());
    }

    @Test
    void testGetFlightById_NotFound_ThrowsException() {
        // Arrange
        when(flightRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            flightService.getFlightById(999L);
        });
    }

    @Test
    void testUpdateAvailableSeats_Success() {
        // Arrange
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(flightRepository.save(any(Flight.class))).thenReturn(flight);

        flightService.updateAvailableSeats(1L, -2);
        verify(flightRepository, times(1)).save(any(Flight.class));
    }

    @Test
    void testUpdateAvailableSeats_NotEnoughSeats_ThrowsException() {
        // Arrange
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));

        // trying to reduce more seats than available
        assertThrows(BusinessException.class, () -> {
            flightService.updateAvailableSeats(1L, -200);
        });
    }

    @Test
    void testUpdateAvailableSeats_ExceedsTotalSeats_ThrowsException() {
        // Arrange
        flight.setAvailableSeats(170);
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));

        // trying to increase beyond total seats
        assertThrows(BusinessException.class, () -> {
            flightService.updateAvailableSeats(1L, 20);
        });
    }
}