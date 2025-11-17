package com.flightapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flightapp.dto.FlightInventoryRequest;
import com.flightapp.dto.FlightSearchRequest;
import com.flightapp.dto.FlightSearchResponse;
import com.flightapp.service.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FlightController.class)
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FlightService flightService;

    private FlightInventoryRequest inventoryRequest;
    private FlightSearchRequest searchRequest;
    private FlightSearchResponse searchResponse;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

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

        searchRequest = FlightSearchRequest.builder()
                .fromLocation("Delhi")
                .toLocation("Mumbai")
                .travelDate(LocalDate.of(2025, 11, 20))
                .numberOfPassengers(2)
                .build();

        searchResponse = FlightSearchResponse.builder()
                .flightId(1L)
                .flightNumber("6E2001")
                .airlineName("IndiGo")
                .airlineCode("6E")
                .fromLocation("Delhi")
                .toLocation("Mumbai")
                .departureTime(LocalDateTime.of(2025, 11, 20, 10, 0))
                .arrivalTime(LocalDateTime.of(2025, 11, 20, 12, 30))
                .availableSeats(180)
                .basePrice(new BigDecimal("5000.00"))
                .duration("2h 30m")
                .build();
    }

    @Test
    void testAddFlightInventory_Success() throws Exception {
        // Arrange
        when(flightService.addFlightInventory(any(FlightInventoryRequest.class)))
                .thenReturn(searchResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1.0/flight/airline/inventory/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventoryRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.flightNumber").value("6E2001"));
    }

    @Test
    void testSearchFlights_Success() throws Exception {
        // Arrange
        List<FlightSearchResponse> flights = Arrays.asList(searchResponse);
        when(flightService.searchFlights(any(FlightSearchRequest.class))).thenReturn(flights);

        // Act & Assert
        mockMvc.perform(post("/api/v1.0/flight/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].flightNumber").value("6E2001"));
    }

    @Test
    void testSearchFlights_NoResults() throws Exception {
        // Arrange
        when(flightService.searchFlights(any(FlightSearchRequest.class))).thenReturn(Arrays.asList());
        mockMvc.perform(post("/api/v1.0/flight/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testGetFlightById_Success() throws Exception {
        // Arrange
        when(flightService.getFlightById(anyLong())).thenReturn(searchResponse);

        mockMvc.perform(get("/api/v1.0/flight/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.flightId").value(1));
    }
}