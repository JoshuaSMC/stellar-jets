package com.stellarjets.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ReservationRequestDTO {

    @NotNull
    @Future(message = "La fecha de check-in debe ser futura")
    private LocalDate checkIn;

    @NotNull
    private LocalDate checkOut;
}
