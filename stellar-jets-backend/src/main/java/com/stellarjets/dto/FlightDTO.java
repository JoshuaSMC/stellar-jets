package com.stellarjets.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightDTO {
    private Long id;
    private String flightNumber;
    private String name;
    private String description;
    private BigDecimal price;
    private AirportDTO origin;
    private AirportDTO destination;
    private int availableSeats;
    private Integer durationMinutes;
    private BigDecimal rating;
    private boolean active;
    private CategoryDTO category;
    private List<FlightImageDTO> images;
    private List<CharacteristicDTO> characteristics;
    private int reviewCount;

    /** URL de portada calculada, null-safe. Útil para tarjetas en listados. */
    public String getCoverImageUrl() {
        if (images == null || images.isEmpty()) return null;
        return images.stream()
                .filter(FlightImageDTO::isCover)
                .map(FlightImageDTO::getUrl)
                .findFirst()
                .orElse(images.get(0).getUrl());
    }
}
