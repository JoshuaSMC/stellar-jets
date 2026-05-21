package com.stellarjets.controller;

import com.stellarjets.dto.FlightDTO;
import com.stellarjets.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<List<FlightDTO>> getFavorites() {
        return ResponseEntity.ok(favoriteService.getFavorites());
    }

    @PostMapping("/{flightId}")
    public ResponseEntity<Void> addFavorite(@PathVariable Long flightId) {
        favoriteService.addFavorite(flightId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{flightId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long flightId) {
        favoriteService.removeFavorite(flightId);
        return ResponseEntity.noContent().build();
    }
}
