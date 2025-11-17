package com.flightapp.controller;

import com.flightapp.dto.AirlineRequest;
import com.flightapp.dto.AirlineResponse;
import com.flightapp.dto.ApiResponse;
import com.flightapp.service.AirlineService;
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
public class AirlineController {

    private final AirlineService airlineService;

    @PostMapping("/airline/register")
    public ResponseEntity<ApiResponse<AirlineResponse>> registerAirline(
            @Valid @RequestBody AirlineRequest request) {
        
        log.info("REST request to register airline: {}", request.getAirlineCode());
        
        AirlineResponse response = airlineService.addAirline(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Airline registered successfully", response));
    }

    
    @GetMapping("/airlines")
    public ResponseEntity<ApiResponse<List<AirlineResponse>>> getAllAirlines() {
        
        log.info("REST request to get all airlines");
        
        List<AirlineResponse> airlines = airlineService.getAllActiveAirlines();
        
        return ResponseEntity.ok(ApiResponse.success("Airlines retrieved successfully", airlines));
    }

    @GetMapping("/airline/{airlineCode}")
    public ResponseEntity<ApiResponse<AirlineResponse>> getAirlineByCode(
            @PathVariable String airlineCode) {
        
        log.info("REST request to get airline by code: {}", airlineCode);
        
        AirlineResponse response = airlineService.getAirlineByCode(airlineCode);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}