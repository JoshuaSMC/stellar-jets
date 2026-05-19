package com.stellarjets.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirportRequestDTO {

    @NotBlank(message = "La ciudad es obligatoria")
    private String city;

    @NotBlank(message = "El país es obligatorio")
    private String country;

    @Size(min = 3, max = 3, message = "El código IATA debe tener exactamente 3 caracteres")
    private String iataCode;
}
