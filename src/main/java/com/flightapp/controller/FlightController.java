package com.flightapp.controller;

import com.flightapp.dto.ApiResponse;
import com.flightapp.dto.FlightInventoryRequest;
import com.flightapp.dto.FlightSearchRequest;
import com.flightapp.dto.FlightSearchResponse;
import com.flightapp.service.FlightService;
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
public class FlightController {

    private final FlightService flightService;

    @PostMapping("/airline/inventory/add")
    public ResponseEntity<ApiResponse<FlightSearchResponse>> addFlightInventory(
            @Valid @RequestBody FlightInventoryRequest request) {
        
        log.info("REST request to add flight inventory: {}", request.getFlightNumber());
        
        FlightSearchResponse response = flightService.addFlightInventory(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Flight inventory added successfully", response));
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<FlightSearchResponse>>> searchFlights(
            @Valid @RequestBody FlightSearchRequest request) {
        
        log.info("REST request to search flights from {} to {} on {}", 
                request.getFromLocation(), request.getToLocation(), request.getTravelDate());
        
        List<FlightSearchResponse> flights = flightService.searchFlights(request);
        
        if (flights.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success("No flights found matching your search criteria", flights));
        }
        
        return ResponseEntity.ok(ApiResponse.success(
                String.format("Found %d flight(s) matching your search", flights.size()), flights));
    }

    @GetMapping("/{flightId}")
    public ResponseEntity<ApiResponse<FlightSearchResponse>> getFlightById(
            @PathVariable Long flightId) {
        
        log.info("REST request to get flight details for ID: {}", flightId);
        
        FlightSearchResponse response = flightService.getFlightById(flightId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}