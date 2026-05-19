package com.stellarjets.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightImageDTO {
    private Long id;
    private String url;
    private String altText;
    private boolean cover;
}
