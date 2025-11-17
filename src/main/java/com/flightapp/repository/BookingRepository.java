package com.flightapp.repository;

import com.flightapp.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

  
    Optional<Booking> findByPnr(String pnr);

    
    @Query("SELECT b FROM Booking b WHERE b.email = :email ORDER BY b.createdAt DESC")
    List<Booking> findByEmailOrderByCreatedAtDesc(@Param("email") String email);

    List<Booking> findByEmailAndBookingStatus(String email, Booking.BookingStatus bookingStatus);

    @Query("SELECT b FROM Booking b WHERE b.flight.id = :flightId")
    List<Booking> findByFlightId(@Param("flightId") Long flightId);

    
    boolean existsByPnr(String pnr);

   
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.flight.id = :flightId AND b.bookingStatus = 'CONFIRMED'")
    Long countConfirmedBookingsByFlightId(@Param("flightId") Long flightId);
}