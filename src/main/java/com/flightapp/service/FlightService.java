package com.flightapp.service;

import com.flightapp.dto.FlightInventoryRequest;
import com.flightapp.dto.FlightSearchRequest;
import com.flightapp.dto.FlightSearchResponse;

import java.util.List;

public interface FlightService {

   
    FlightSearchResponse addFlightInventory(FlightInventoryRequest request);

    List<FlightSearchResponse> searchFlights(FlightSearchRequest request);

    FlightSearchResponse getFlightById(Long flightId);

    void updateAvailableSeats(Long flightId, Integer seatsToAdjust);
}