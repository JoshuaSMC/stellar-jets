package com.stellarjets.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "flights")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Código de vuelo único, ej: "SJ-001". Opcional en Sprint 1. */
    @Column(name = "flight_number", unique = true, length = 10)
    private String flightNumber;

    /** Nombre comercial del vuelo, ej: "Patagonia Extrema" */
    @NotBlank
    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "city",     column = @Column(name = "origin_city",     length = 100)),
        @AttributeOverride(name = "country",  column = @Column(name = "origin_country",  length = 100)),
        @AttributeOverride(name = "iataCode", column = @Column(name = "origin_iata_code", length = 3))
    })
    private AirportInfo origin;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "city",     column = @Column(name = "dest_city",     length = 100)),
        @AttributeOverride(name = "country",  column = @Column(name = "dest_country",  length = 100)),
        @AttributeOverride(name = "iataCode", column = @Column(name = "dest_iata_code", length = 3))
    })
    private AirportInfo destination;

    @Min(1)
    @Column(name = "available_seats", nullable = false)
    private int availableSeats;

    /** Duración estimada del vuelo en minutos */
    @Min(1)
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    /** Rating promedio 1-5 */
    @DecimalMin("1.0") @DecimalMax("5.0")
    @Column(precision = 3, scale = 1)
    private BigDecimal rating;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FlightImage> images = new ArrayList<>();

    public void addImage(FlightImage image) {
        image.setFlight(this);
        this.images.add(image);
    }
}
