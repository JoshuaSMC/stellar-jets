package com.stellarjets.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AirportDTO {
    private String city;
    private String country;
    private String iataCode;
}
