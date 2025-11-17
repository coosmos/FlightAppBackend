package com.flightapp.repository;

import com.flightapp.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    
    Optional<Flight> findByFlightNumber(String flightNumber);

    
    @Query("SELECT f FROM Flight f WHERE " +
           "f.fromLocation = :fromLocation AND " +
           "f.toLocation = :toLocation AND " +
           "f.departureTime >= :startDate AND " +
           "f.departureTime < :endDate AND " +
           "f.availableSeats >= :requiredSeats AND " +
           "f.isActive = true AND " +
           "f.flightStatus = 'SCHEDULED' " +
           "ORDER BY f.departureTime ASC")
    List<Flight> searchFlights(
        @Param("fromLocation") String fromLocation,
        @Param("toLocation") String toLocation,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("requiredSeats") Integer requiredSeats
    );

    
    @Query("SELECT f FROM Flight f WHERE f.airline.id = :airlineId AND f.isActive = true")
    List<Flight> findByAirlineId(@Param("airlineId") Long airlineId);

    List<Flight> findByDepartureTimeAfterAndIsActiveTrue(LocalDateTime departureTime);

    boolean existsByFlightNumber(String flightNumber);

    List<Flight> findByFlightStatusAndIsActiveTrue(Flight.FlightStatus flightStatus);
}