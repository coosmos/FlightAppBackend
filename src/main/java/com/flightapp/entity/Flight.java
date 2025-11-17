package com.flightapp.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "flights", indexes = {
    @Index(name = "idx_flight_route_date", columnList = "from_location, to_location, departure_time"),
    @Index(name = "idx_departure_time", columnList = "departure_time")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flight extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flight_number", nullable = false, unique = true, length = 20)
    private String flightNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "airline_id", nullable = false)
    private Airline airline;

    @Column(name = "from_location", nullable = false, length = 100)
    private String fromLocation;

    @Column(name = "to_location", nullable = false, length = 100)
    private String toLocation;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "flight_status", nullable = false, length = 20)
    private FlightStatus flightStatus = FlightStatus.SCHEDULED;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public enum FlightStatus {
        SCHEDULED,
        DELAYED,
        CANCELLED,
        COMPLETED
    }
}