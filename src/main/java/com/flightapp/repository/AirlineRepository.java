package com.flightapp.repository;

import com.flightapp.entity.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, Long> {

  
    Optional<Airline> findByAirlineCode(String airlineCode);

    Optional<Airline> findByAirlineName(String airlineName);

    List<Airline> findByIsActiveTrue();

    boolean existsByAirlineCode(String airlineCode);
}