package com.flightapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flightapp.dto.BookingRequest;
import com.flightapp.dto.BookingResponse;
import com.flightapp.dto.PassengerRequest;
import com.flightapp.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private BookingRequest bookingRequest;
    private BookingResponse bookingResponse;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

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

        BookingResponse.FlightDetailsDto flightDetails = BookingResponse.FlightDetailsDto.builder()
                .flightId(1L)
                .flightNumber("6E2001")
                .airlineName("IndiGo")
                .fromLocation("Delhi")
                .toLocation("Mumbai")
                .departureTime(LocalDateTime.now().plusDays(5))
                .arrivalTime(LocalDateTime.now().plusDays(5).plusHours(2))
                .build();

        BookingResponse.PassengerDetailsDto passengerDetails = BookingResponse.PassengerDetailsDto.builder()
                .passengerName("John Doe")
                .gender("MALE")
                .age(30)
                .seatNumber("12A")
                .mealPreference("VEG")
                .build();

        bookingResponse = BookingResponse.builder()
                .bookingId(1L)
                .pnr("251116ABCD")
                .contactName("John Doe")
                .email("john@example.com")
                .numberOfSeats(1)
                .totalAmount(new BigDecimal("5000.00"))
                .bookingStatus("CONFIRMED")
                .bookingDate(LocalDateTime.now())
                .flight(flightDetails)
                .passengers(Arrays.asList(passengerDetails))
                .build();
    }

    @Test
    void testBookFlight_Success() throws Exception {
        when(bookingService.bookFlight(anyLong(), any(BookingRequest.class)))
                .thenReturn(bookingResponse);

        mockMvc.perform(post("/api/v1.0/flight/booking/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.pnr").value("251116ABCD"))
                .andExpect(jsonPath("$.data.email").value("john@example.com"));
    }

    @Test
    void testBookFlight_InvalidEmail() throws Exception {
        bookingRequest.setEmail("invalid-email");

        mockMvc.perform(post("/api/v1.0/flight/booking/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testGetTicketByPNR_Success() throws Exception {
    
        when(bookingService.getBookingByPNR(anyString())).thenReturn(bookingResponse);

        mockMvc.perform(get("/api/v1.0/flight/ticket/251116ABCD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.pnr").value("251116ABCD"));
    }

    @Test
    void testGetBookingHistory_Success() throws Exception {
     
        List<BookingResponse> bookings = Arrays.asList(bookingResponse);
        when(bookingService.getBookingHistory(anyString())).thenReturn(bookings);

        mockMvc.perform(get("/api/v1.0/flight/booking/history/john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].email").value("john@example.com"));
    }

    @Test
    void testCancelBooking_Success() throws Exception {
    
        doNothing().when(bookingService).cancelBooking(anyString());

        mockMvc.perform(delete("/api/v1.0/flight/booking/cancel/251116ABCD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}