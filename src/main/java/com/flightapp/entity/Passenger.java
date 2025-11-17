package com.flightapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "passengers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Passenger extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "passenger_name", nullable = false, length = 100)
    private String passengerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private Gender gender;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_preference", length = 20)
    private MealPreference mealPreference;

    @Column(name = "seat_number", nullable = false, length = 10)
    private String seatNumber;

 
    public enum Gender {
        MALE,
        FEMALE,
        OTHER
    }

    public enum MealPreference {
        VEG,
        NON_VEG,
        NONE
    }
}