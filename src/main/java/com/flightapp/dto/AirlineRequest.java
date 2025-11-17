package com.flightapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirlineRequest {

    @NotBlank(message = "Airline name is required")
    @Size(min = 2, max = 100, message = "Airline name must be between 2 and 100 characters")
    private String airlineName;

    @NotBlank(message = "Airline code is required")
    @Size(min = 2, max = 10, message = "Airline code must be between 2 and 10 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Airline code must contain only uppercase letters and numbers")
    private String airlineCode;

    @Pattern(regexp = "^[0-9]{10,10}$", message = "Contact number must be 10 digits")
    private String contactNumber;
}