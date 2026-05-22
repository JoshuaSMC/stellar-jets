package com.stellarjets.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ReservationRequestDTO {

    @NotNull
    private LocalDate checkIn;

    @NotNull
    private LocalDate checkOut;
}
