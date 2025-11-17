package com.flightapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {

    private Long bookingId;
    private String pnr;
    private String contactName;
    private String email;
    private Integer numberOfSeats;
    private BigDecimal totalAmount;
    private String bookingStatus;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bookingDate;

    // Flight details
    private FlightDetailsDto flight;

    // Passenger details
    private List<PassengerDetailsDto> passengers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FlightDetailsDto {
        private Long flightId;
        private String flightNumber;
        private String airlineName;
        private String fromLocation;
        private String toLocation;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime departureTime;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime arrivalTime;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PassengerDetailsDto {
        private String passengerName;
        private String gender;
        private Integer age;
        private String seatNumber;
        private String mealPreference;
    }
}