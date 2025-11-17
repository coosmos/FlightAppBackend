package com.flightapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
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
public class FlightInventoryRequest {

    @NotBlank(message = "Flight number is required")
    @Pattern(regexp = "^[A-Z0-9]{4,10}$", message = "Flight number must be 4-10 uppercase alphanumeric characters")
    private String flightNumber;

    @NotBlank(message = "Airline code is required")
    private String airlineCode;

    @NotBlank(message = "From location is required")
    @Size(min = 3, max = 100, message = "From location must be between 3 and 100 characters")
    private String fromLocation;

    @NotBlank(message = "To location is required")
    @Size(min = 3, max = 100, message = "To location must be between 3 and 100 characters")
    private String toLocation;

    @NotNull(message = "Departure time is required")
    @Future(message = "Departure time must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime departureTime;

    @NotNull(message = "Arrival time is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime arrivalTime;

    @NotNull(message = "Total seats is required")
    @Min(value = 1, message = "Total seats must be at least 1")
    @Max(value = 500, message = "Total seats cannot exceed 500")
    private Integer totalSeats;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.01", message = "Base price must be greater than 0")
    @DecimalMax(value = "1000000.00", message = "Base price cannot exceed 1,000,000")
    private BigDecimal basePrice;
}