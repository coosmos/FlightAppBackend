package com.flightapp.service;

import com.flightapp.dto.AirlineRequest;
import com.flightapp.dto.AirlineResponse;
import com.flightapp.entity.Airline;
import com.flightapp.exceptions.DuplicateResourceException;
import com.flightapp.repository.AirlineRepository;
import com.flightapp.service.AirlineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AirlineServiceImpl implements AirlineService {

    private final AirlineRepository airlineRepository;

    @Override
    public AirlineResponse addAirline(AirlineRequest request) {
        log.info("Adding new airline: {}", request.getAirlineCode());

        // Check if airline code already exists
        if (airlineRepository.existsByAirlineCode(request.getAirlineCode())) {
            throw new DuplicateResourceException(
                "Airline", "airline code", request.getAirlineCode());
        }

        // Create and save airline
        Airline airline = Airline.builder()
                .airlineName(request.getAirlineName())
                .airlineCode(request.getAirlineCode())
                .contactNumber(request.getContactNumber())
                .isActive(true)
                .build();

        Airline savedAirline = airlineRepository.save(airline);
        log.info("Airline added successfully with ID: {}", savedAirline.getId());

        return mapToResponse(savedAirline);
    }

    @Override
    @Transactional(readOnly = true)
    public AirlineResponse getAirlineById(Long id) {
        log.info("Fetching airline with ID: {}", id);
        Airline airline = airlineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Airline not found with ID: " + id));
        return mapToResponse(airline);
    }

    @Override
    @Transactional(readOnly = true)
    public AirlineResponse getAirlineByCode(String airlineCode) {
        log.info("Fetching airline with code: {}", airlineCode);
        Airline airline = airlineRepository.findByAirlineCode(airlineCode)
                .orElseThrow(() -> new IllegalArgumentException("Airline not found with code: " + airlineCode));
        return mapToResponse(airline);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AirlineResponse> getAllActiveAirlines() {
        log.info("Fetching all active airlines");
        return airlineRepository.findByIsActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AirlineResponse updateAirline(Long id, AirlineRequest request) {
        log.info("Updating airline with ID: {}", id);
        
        Airline airline = airlineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Airline not found with ID: " + id));

        // Update fields
        airline.setAirlineName(request.getAirlineName());
        airline.setContactNumber(request.getContactNumber());

        Airline updatedAirline = airlineRepository.save(airline);
        log.info("Airline updated successfully");

        return mapToResponse(updatedAirline);
    }

    @Override
    public void deactivateAirline(Long id) {
        log.info("Deactivating airline with ID: {}", id);
        
        Airline airline = airlineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Airline not found with ID: " + id));

        airline.setIsActive(false);
        airlineRepository.save(airline);
        
        log.info("Airline deactivated successfully");
    }

    // Helper method to map Entity to DTO
    private AirlineResponse mapToResponse(Airline airline) {
        return AirlineResponse.builder()
                .id(airline.getId())
                .airlineName(airline.getAirlineName())
                .airlineCode(airline.getAirlineCode())
                .contactNumber(airline.getContactNumber())
                .isActive(airline.getIsActive())
                .build();
    }
}