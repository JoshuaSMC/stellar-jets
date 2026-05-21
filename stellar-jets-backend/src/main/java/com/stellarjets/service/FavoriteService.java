package com.stellarjets.service;

import com.stellarjets.dto.FlightDTO;
import com.stellarjets.entity.Favorite;
import com.stellarjets.entity.Flight;
import com.stellarjets.entity.User;
import com.stellarjets.exception.ResourceNotFoundException;
import com.stellarjets.repository.FavoriteRepository;
import com.stellarjets.repository.FlightRepository;
import com.stellarjets.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final FlightRepository flightRepository;
    private final FlightService flightService;

    @Transactional(readOnly = true)
    public List<FlightDTO> getFavorites() {
        User user = currentUser();
        return favoriteRepository.findByUserId(user.getId()).stream()
                .map(f -> flightService.toDTO(f.getFlight()))
                .toList();
    }

    public void addFavorite(Long flightId) {
        User user = currentUser();
        if (favoriteRepository.existsByUserIdAndFlightId(user.getId(), flightId)) return;
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Vuelo no encontrado"));
        favoriteRepository.save(Favorite.builder().user(user).flight(flight).build());
    }

    public void removeFavorite(Long flightId) {
        User user = currentUser();
        favoriteRepository.findByUserIdAndFlightId(user.getId(), flightId)
                .ifPresent(favoriteRepository::delete);
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}
