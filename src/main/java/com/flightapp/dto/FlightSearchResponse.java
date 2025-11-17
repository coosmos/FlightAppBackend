package com.flightapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightSearchResponse {

    private Long flightId;
    private String flightNumber;
    private String airlineName;
    private String airlineCode;
    private String fromLocation;
    private String toLocation;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime departureTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime arrivalTime;

    private Integer availableSeats;
    private BigDecimal basePrice;
    private String duration; // e.g., "2h 30m"
}