package com.flightapp.exceptions;

/**
 * Exception thrown when business rules are violated
 * HTTP Status: 400 BAD REQUEST
 * Examples: Not enough seats, cancellation time violation, etc.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}