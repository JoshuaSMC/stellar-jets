package com.stellarjets.controller;

import com.stellarjets.dto.*;
import com.stellarjets.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * Endpoints de administración — Sprint 2 agregará autenticación con @PreAuthorize("ROLE_ADMIN")
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final FlightService flightService;
    private final CategoryService categoryService;

    // ---- Vuelos ----

    @GetMapping("/flights")
    public ResponseEntity<PagedResponseDTO<FlightDTO>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(flightService.findAll(page, size));
    }

    @PostMapping("/flights")
    public ResponseEntity<FlightDTO> create(@Valid @RequestBody FlightRequestDTO dto) {
        FlightDTO created = flightService.create(dto);
        URI location = URI.create("/api/flights/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/flights/{id}")
    public ResponseEntity<FlightDTO> update(@PathVariable Long id,
                                             @Valid @RequestBody FlightRequestDTO dto) {
        return ResponseEntity.ok(flightService.update(id, dto));
    }

    @DeleteMapping("/flights/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        flightService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/flights/{id}/toggle")
    public ResponseEntity<FlightDTO> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(flightService.toggleActive(id));
    }

    // ---- Categorías ----

    @PostMapping("/categories")
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO dto) {
        CategoryDTO created = categoryService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id,
                                                       @RequestBody CategoryDTO dto) {
        return ResponseEntity.ok(categoryService.update(id, dto));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
