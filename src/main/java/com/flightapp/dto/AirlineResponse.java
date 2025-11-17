package com.flightapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirlineResponse {
    private Long id;
    private String airlineName;
    private String airlineCode;
    private String contactNumber;
    private Boolean isActive;
}