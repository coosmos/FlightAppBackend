package com.flightapp.service;

import com.flightapp.dto.AirlineRequest;
import com.flightapp.dto.AirlineResponse;

import java.util.List;

public interface AirlineService {

    AirlineResponse addAirline(AirlineRequest request);

    AirlineResponse getAirlineById(Long id);

    AirlineResponse getAirlineByCode(String airlineCode);

    List<AirlineResponse> getAllActiveAirlines();

    AirlineResponse updateAirline(Long id, AirlineRequest request);

    void deactivateAirline(Long id);
}