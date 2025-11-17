package com.flightapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "airlines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Airline extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "airline_name", nullable = false, unique = true, length = 100)
    private String airlineName;

    @Column(name = "airline_code", nullable = false, unique = true, length = 10)
    private String airlineCode;
 
    @Column(name = "contact_number", length = 15)
    private String contactNumber;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}