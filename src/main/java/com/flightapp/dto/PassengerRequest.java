package com.flightapp.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerRequest {

    @NotBlank(message = "Passenger name is required")
    @Size(min = 2, max = 100, message = "Passenger name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Passenger name must contain only letters and spaces")
    private String passengerName;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Gender must be MALE, FEMALE, or OTHER")
    private String gender;

    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age must be at least 0")
    @Max(value = 120, message = "Age cannot exceed 120")
    private Integer age;

    @Pattern(regexp = "^(VEG|NON_VEG|NONE)$", message = "Meal preference must be VEG, NON_VEG, or NONE")
    private String mealPreference = "NONE";

    @NotBlank(message = "Seat number is required")
    @Pattern(regexp = "^[A-Z0-9]{2,5}$", message = "Invalid seat number format (e.g., 12A, 5B)")
    private String seatNumber;
}