package com.stellarjets.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationResponseDTO {
    private Long id;
    private Long flightId;
    private String flightName;
    private String checkIn;
    private String checkOut;
    private String userEmail;
}
