package com.stellarjets.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import lombok.*;

@Embeddable
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirportInfo {

    private String city;

    private String country;

    /** Código IATA de 3 letras, ej: "EZE", "MAD", "NRT" */
    @Size(min = 3, max = 3)
    private String iataCode;
}
