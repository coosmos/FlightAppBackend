package com.flightapp.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PNRGeneratorService {

    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();

    
     // Generate a unique PNR number
     // Format: YYMMDD + 4 random characters
     
    public String generatePNR() {
        String datePart = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyMMdd"));
        StringBuilder randomPart = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            randomPart.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
        }

        return datePart + randomPart;
    }
}