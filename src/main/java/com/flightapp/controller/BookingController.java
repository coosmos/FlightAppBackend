package com.flightapp.controller;

import com.flightapp.dto.ApiResponse;
import com.flightapp.dto.BookingRequest;
import com.flightapp.dto.BookingResponse;
import com.flightapp.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/flight")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/booking/{flightId}")
    public ResponseEntity<ApiResponse<BookingResponse>> bookFlight(
            @PathVariable Long flightId,
            @Valid @RequestBody BookingRequest request) {
        
        log.info("REST request to book flight ID: {} for email: {}", flightId, request.getEmail());
        
        BookingResponse response = bookingService.bookFlight(flightId, request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Flight booked successfully. PNR: " + response.getPnr(), response));
    }

    
    @GetMapping("/ticket/{pnr}")
    public ResponseEntity<ApiResponse<BookingResponse>> getTicketByPNR(
            @PathVariable String pnr) {
        
        // Trim whitespace/newlines , caused issues in apis endpoint because of newline insertion
        String trimmedPnr = pnr.trim();
        
        log.info("REST request to get ticket details for PNR: {}", trimmedPnr);
        
        BookingResponse response = bookingService.getBookingByPNR(trimmedPnr);
        
        return ResponseEntity.ok(ApiResponse.success("Ticket details retrieved successfully", response));
    }

    @GetMapping("/booking/history/{emailId}")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingHistory(
            @PathVariable String emailId) {
        
        log.info("REST request to get booking history for email: {}", emailId);
        
        List<BookingResponse> bookings = bookingService.getBookingHistory(emailId);
        
        if (bookings.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success("No bookings found for this email", bookings));
        }
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("Found %d booking(s)", bookings.size()), bookings));
    }

    @DeleteMapping("/booking/cancel/{pnr}")
    public ResponseEntity<ApiResponse<Void>> cancelBooking(
            @PathVariable String pnr) {
        
        log.info("REST request to cancel booking with PNR: {}", pnr);
        
        bookingService.cancelBooking(pnr);
        
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully. PNR: " + pnr, null));
    }
}