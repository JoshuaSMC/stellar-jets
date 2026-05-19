package com.stellarjets.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "flight_images")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String url;

    @Column(length = 255)
    private String altText;

    /** true = imagen de portada que aparece en la tarjeta */
    @Column(nullable = false)
    private boolean cover;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;
}
