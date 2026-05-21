package com.stellarjets.controller;

import com.stellarjets.dto.OccupiedDateRangeDTO;
import com.stellarjets.dto.ReservationRequestDTO;
import com.stellarjets.dto.ReservationResponseDTO;
import com.stellarjets.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/{flightId}/occupied-dates")
    public ResponseEntity<List<OccupiedDateRangeDTO>> getOccupiedDates(@PathVariable Long flightId) {
        return ResponseEntity.ok(reservationService.getOccupiedDates(flightId));
    }

    @PostMapping("/{flightId}")
    public ResponseEntity<ReservationResponseDTO> createReservation(
            @PathVariable Long flightId,
            @Valid @RequestBody ReservationRequestDTO req,
            Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.createReservation(flightId, req, auth.getName()));
    }
}
