package com.stellarjets.service;

import com.stellarjets.dto.OccupiedDateRangeDTO;
import com.stellarjets.dto.ReservationRequestDTO;
import com.stellarjets.dto.ReservationResponseDTO;
import com.stellarjets.entity.Flight;
import com.stellarjets.entity.Reservation;
import com.stellarjets.repository.FlightRepository;
import com.stellarjets.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final FlightRepository flightRepository;

    public List<OccupiedDateRangeDTO> getOccupiedDates(Long flightId) {
        return reservationRepository.findByFlightId(flightId).stream()
                .map(r -> OccupiedDateRangeDTO.builder()
                        .checkIn(r.getCheckIn().toString())
                        .checkOut(r.getCheckOut().toString())
                        .build())
                .toList();
    }

    @Transactional
    public ReservationResponseDTO createReservation(Long flightId, ReservationRequestDTO req, String userEmail) {
        if (!req.getCheckOut().isAfter(req.getCheckIn())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha de salida debe ser posterior a la de entrada");
        }

        if (reservationRepository.existsOverlap(flightId, req.getCheckIn(), req.getCheckOut())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Las fechas seleccionadas se solapan con una reserva existente");
        }

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vuelo no encontrado"));

        Reservation saved = reservationRepository.save(Reservation.builder()
                .flight(flight)
                .checkIn(req.getCheckIn())
                .checkOut(req.getCheckOut())
                .userEmail(userEmail)
                .build());

        return ReservationResponseDTO.builder()
                .id(saved.getId())
                .flightId(flightId)
                .flightName(flight.getName())
                .checkIn(saved.getCheckIn().toString())
                .checkOut(saved.getCheckOut().toString())
                .userEmail(userEmail)
                .build();
    }
}
