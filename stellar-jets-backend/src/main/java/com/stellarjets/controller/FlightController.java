package com.stellarjets.controller;

import com.stellarjets.dto.*;
import com.stellarjets.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    /**
     * GET /api/flights?page=0&size=10
     * Lista paginada de vuelos activos para el home.
     */
    @GetMapping
    public ResponseEntity<PagedResponseDTO<FlightDTO>> list(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(flightService.findAllActive(page, size));
    }

    /**
     * GET /api/flights/search?query=tokio&categoryId=2&page=0&size=10
     * Búsqueda por texto libre o filtro por categoría. Ambos son opcionales.
     */
    @GetMapping("/search")
    public ResponseEntity<PagedResponseDTO<FlightDTO>> search(
            @RequestParam(name = "query",      required = false) String query,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "page",       defaultValue = "0") int page,
            @RequestParam(name = "size",       defaultValue = "10") int size) {
        return ResponseEntity.ok(flightService.search(query, categoryId, page, size));
    }

    /**
     * GET /api/flights/recommended
     * Top 10 vuelos ordenados por rating descendente. Usado en sección Home.
     */
    @GetMapping("/recommended")
    public ResponseEntity<List<FlightDTO>> recommended() {
        return ResponseEntity.ok(flightService.findTopRated());
    }

    /**
     * GET /api/flights/{id}
     * Detalle completo con galería de imágenes.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FlightDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(flightService.findById(id));
    }
}
