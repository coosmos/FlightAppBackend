package com.flightapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequest {

    @NotBlank(message = "Contact name is required")
    @Size(min = 2, max = 100, message = "Contact name must be between 2 and 100 characters")
    private String contactName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Passengers list is required")
    @Size(min = 1, max = 9, message = "Booking must have between 1 and 9 passengers")
    @Valid
    private List<PassengerRequest> passengers;
}