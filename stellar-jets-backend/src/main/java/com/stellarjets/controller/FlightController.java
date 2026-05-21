package com.stellarjets.controller;

import com.stellarjets.dto.*;
import com.stellarjets.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping
    public ResponseEntity<PagedResponseDTO<FlightDTO>> list(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(flightService.findAllActive(page, size));
    }

    /**
     * GET /api/flights/search?query=tokio&categoryIds=2,3&checkIn=2026-07-01&checkOut=2026-07-10
     */
    @GetMapping("/search")
    public ResponseEntity<PagedResponseDTO<FlightDTO>> search(
            @RequestParam(name = "query",       required = false) String query,
            @RequestParam(name = "categoryIds", required = false) List<Long> categoryIds,
            @RequestParam(name = "checkIn",     required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(name = "checkOut",    required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(name = "page",        defaultValue = "0") int page,
            @RequestParam(name = "size",        defaultValue = "10") int size) {
        return ResponseEntity.ok(flightService.search(query, categoryIds, checkIn, checkOut, page, size));
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<FlightDTO>> recommended() {
        return ResponseEntity.ok(flightService.findTopRated());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(flightService.findById(id));
    }
}
