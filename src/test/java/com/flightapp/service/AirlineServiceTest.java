package com.flightapp.service;

import com.flightapp.dto.AirlineRequest;
import com.flightapp.dto.AirlineResponse;
import com.flightapp.entity.Airline;
import com.flightapp.exceptions.DuplicateResourceException;
import com.flightapp.exceptions.ResourceNotFoundException;
import com.flightapp.repository.AirlineRepository;
import com.flightapp.service.AirlineServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AirlineServiceTest {

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private AirlineServiceImpl airlineService;

    private Airline airline;
    private AirlineRequest airlineRequest;

    @BeforeEach
    void setUp() {
        airline = Airline.builder()
                .id(1L)
                .airlineName("IndiGo")
                .airlineCode("6E")
                .contactNumber("1234567890")
                .isActive(true)
                .build();

        airlineRequest = AirlineRequest.builder()
                .airlineName("IndiGo")
                .airlineCode("6E")
                .contactNumber("1234567890")
                .build();
    }

    @Test
    void testAddAirline_Success() {
        // Arrange
        when(airlineRepository.existsByAirlineCode(airlineRequest.getAirlineCode())).thenReturn(false);
        when(airlineRepository.save(any(Airline.class))).thenReturn(airline);

        // Act
        AirlineResponse response = airlineService.addAirline(airlineRequest);

        // Assert
        assertNotNull(response);
        assertEquals("IndiGo", response.getAirlineName());
        assertEquals("6E", response.getAirlineCode());
        verify(airlineRepository, times(1)).save(any(Airline.class));
    }

    @Test
    void testAddAirline_DuplicateCode_ThrowsException() {
        // Arrange
        when(airlineRepository.existsByAirlineCode(airlineRequest.getAirlineCode())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> {
            airlineService.addAirline(airlineRequest);
        });
        verify(airlineRepository, never()).save(any(Airline.class));
    }

    @Test
    void testGetAirlineById_Success() {
        // Arrange
        when(airlineRepository.findById(1L)).thenReturn(Optional.of(airline));

        // Act
        AirlineResponse response = airlineService.getAirlineById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("IndiGo", response.getAirlineName());
    }

    @Test
    void testGetAirlineById_NotFound_ThrowsException() {
        // Arrange
        when(airlineRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            airlineService.getAirlineById(999L);
        });
    }

    @Test
    void testGetAirlineByCode_Success() {
        // Arrange
        when(airlineRepository.findByAirlineCode("6E")).thenReturn(Optional.of(airline));

        // Act
        AirlineResponse response = airlineService.getAirlineByCode("6E");

        // Assert
        assertNotNull(response);
        assertEquals("6E", response.getAirlineCode());
    }

    @Test
    void testGetAllActiveAirlines_Success() {
        // Arrange
        List<Airline> airlines = Arrays.asList(airline);
        when(airlineRepository.findByIsActiveTrue()).thenReturn(airlines);

        // Act
        List<AirlineResponse> responses = airlineService.getAllActiveAirlines();

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("IndiGo", responses.get(0).getAirlineName());
    }

    @Test
    void testUpdateAirline_Success() {
        // Arrange
        when(airlineRepository.findById(1L)).thenReturn(Optional.of(airline));
        when(airlineRepository.save(any(Airline.class))).thenReturn(airline);

        AirlineRequest updateRequest = AirlineRequest.builder()
                .airlineName("IndiGo Airlines")
                .airlineCode("6E")
                .contactNumber("9876543210")
                .build();

        // Act
        AirlineResponse response = airlineService.updateAirline(1L, updateRequest);

        // Assert
        assertNotNull(response);
        verify(airlineRepository, times(1)).save(any(Airline.class));
    }

    @Test
    void testDeactivateAirline_Success() {
        // Arrange
        when(airlineRepository.findById(1L)).thenReturn(Optional.of(airline));
        when(airlineRepository.save(any(Airline.class))).thenReturn(airline);

        // Act
        airlineService.deactivateAirline(1L);

        // Assert
        verify(airlineRepository, times(1)).save(any(Airline.class));
    }
}