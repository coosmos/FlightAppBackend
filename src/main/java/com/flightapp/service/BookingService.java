package com.flightapp.service;

import com.flightapp.dto.BookingRequest;
import com.flightapp.dto.BookingResponse;

import java.util.List;

public interface BookingService {

  
    BookingResponse bookFlight(Long flightId, BookingRequest request);

    
    BookingResponse getBookingByPNR(String pnr);

   
    List<BookingResponse> getBookingHistory(String email);

   //can only cancel if the cancellation date is before 24 hrs 
    void cancelBooking(String pnr);
}