package com.flightapp.repository;

import com.flightapp.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    @Query("SELECT p FROM Passenger p WHERE p.booking.id = :bookingId")
    List<Passenger> findByBookingId(@Param("bookingId") Long bookingId);

    @Query("SELECT p FROM Passenger p WHERE p.booking.flight.id = :flightId")
    List<Passenger> findByFlightId(@Param("flightId") Long flightId);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
           "FROM Passenger p " +
           "WHERE p.booking.flight.id = :flightId " +
           "AND p.seatNumber = :seatNumber " +
           "AND p.booking.bookingStatus = 'CONFIRMED'")
    boolean isSeatBookedForFlight(@Param("flightId") Long flightId, @Param("seatNumber") String seatNumber);

    @Query("SELECT p.seatNumber FROM Passenger p " +
           "WHERE p.booking.flight.id = :flightId " +
           "AND p.booking.bookingStatus = 'CONFIRMED'")
    List<String> findBookedSeatsByFlightId(@Param("flightId") Long flightId);
}