package com.stellarjets.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OccupiedDateRangeDTO {
    private String checkIn;
    private String checkOut;
}
