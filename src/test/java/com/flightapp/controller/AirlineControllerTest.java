package com.flightapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightapp.dto.AirlineRequest;
import com.flightapp.dto.AirlineResponse;
import com.flightapp.service.AirlineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AirlineController.class)
class AirlineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AirlineService airlineService;

    private AirlineRequest airlineRequest;
    private AirlineResponse airlineResponse;

    @BeforeEach
    void setUp() {
        airlineRequest = AirlineRequest.builder()
                .airlineName("IndiGo")
                .airlineCode("6E")
                .contactNumber("1234567890")
                .build();

        airlineResponse = AirlineResponse.builder()
                .id(1L)
                .airlineName("IndiGo")
                .airlineCode("6E")
                .contactNumber("1234567890")
                .isActive(true)
                .build();
    }

    @Test
    void testRegisterAirline_Success() throws Exception {
        // Arrange
        when(airlineService.addAirline(any(AirlineRequest.class))).thenReturn(airlineResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1.0/flight/airline/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(airlineRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.airlineName").value("IndiGo"))
                .andExpect(jsonPath("$.data.airlineCode").value("6E"));
    }

    @Test
    void testRegisterAirline_InvalidInput() throws Exception {
        // Arrange - invalid airline code (lowercase)
        AirlineRequest invalidRequest = AirlineRequest.builder()
                .airlineName("IndiGo")
                .airlineCode("abc")
                .contactNumber("1234567890")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1.0/flight/airline/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testGetAllAirlines_Success() throws Exception {
        List<AirlineResponse> airlines = Arrays.asList(airlineResponse);
        when(airlineService.getAllActiveAirlines()).thenReturn(airlines);

        mockMvc.perform(get("/api/v1.0/flight/airlines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].airlineName").value("IndiGo"));
    }

    @Test
    void testGetAirlineByCode_Success() throws Exception {
        // Arrange
        when(airlineService.getAirlineByCode(anyString())).thenReturn(airlineResponse);

        mockMvc.perform(get("/api/v1.0/flight/airline/6E"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.airlineCode").value("6E"));
    }
}