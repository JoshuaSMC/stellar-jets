package com.stellarjets.service;

import com.stellarjets.dto.OccupiedDateRangeDTO;
import com.stellarjets.dto.ReservationRequestDTO;
import com.stellarjets.dto.ReservationResponseDTO;
import com.stellarjets.entity.Flight;
import com.stellarjets.entity.Reservation;
import com.stellarjets.entity.User;
import com.stellarjets.repository.FlightRepository;
import com.stellarjets.repository.ReservationRepository;
import com.stellarjets.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final EmailService emailService;

    public List<OccupiedDateRangeDTO> getOccupiedDates(Long flightId) {
        return reservationRepository.findByFlightId(flightId).stream()
                .map(r -> OccupiedDateRangeDTO.builder()
                        .checkIn(r.getCheckIn().toString())
                        .checkOut(r.getCheckOut().toString())
                        .build())
                .toList();
    }

    public List<ReservationResponseDTO> getMyReservations(String userEmail) {
        return reservationRepository.findByUserEmailOrderByCreatedAtDesc(userEmail).stream()
                .map(this::toDTO)
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

        ReservationResponseDTO dto = toDTO(saved);

        userRepository.findByEmail(userEmail).ifPresent(user ->
            emailService.sendReservationConfirmation(
                userEmail,
                user.getFirstName(),
                user.getLastName(),
                dto.getId(),
                dto.getFlightName(),
                dto.getFlightOrigin(),
                dto.getFlightDestination(),
                dto.getCheckIn(),
                dto.getCheckOut()
            )
        );

        return dto;
    }

    private ReservationResponseDTO toDTO(Reservation r) {
        Flight f = r.getFlight();
        String cover = f.getImages() == null ? null :
                f.getImages().stream().filter(i -> i.isCover()).map(i -> i.getUrl()).findFirst()
                .or(() -> f.getImages().stream().map(i -> i.getUrl()).findFirst())
                .orElse(null);
        return ReservationResponseDTO.builder()
                .id(r.getId())
                .flightId(f.getId())
                .flightName(f.getName())
                .flightOrigin(f.getOrigin() != null ? f.getOrigin().getCity() + " (" + f.getOrigin().getIataCode() + ")" : null)
                .flightDestination(f.getDestination() != null ? f.getDestination().getCity() + " (" + f.getDestination().getIataCode() + ")" : null)
                .coverImageUrl(cover)
                .checkIn(r.getCheckIn().toString())
                .checkOut(r.getCheckOut().toString())
                .userEmail(r.getUserEmail())
                .createdAt(r.getCreatedAt() != null ? r.getCreatedAt().toLocalDate().toString() : null)
                .build();
    }
}
