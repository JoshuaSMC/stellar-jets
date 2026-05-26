package com.stellarjets.controller;

import com.stellarjets.dto.*;
import com.stellarjets.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final FlightService flightService;
    private final CategoryService categoryService;
    private final CharacteristicService characteristicService;
    private final UserService userService;

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
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO dto) {
        CategoryDTO created = categoryService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id,
                                                       @Valid @RequestBody CategoryDTO dto) {
        return ResponseEntity.ok(categoryService.update(id, dto));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Características ----

    @GetMapping("/characteristics")
    public ResponseEntity<List<CharacteristicDTO>> listCharacteristics() {
        return ResponseEntity.ok(characteristicService.findAll());
    }

    @PostMapping("/characteristics")
    public ResponseEntity<CharacteristicDTO> createCharacteristic(@Valid @RequestBody CharacteristicDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(characteristicService.create(dto));
    }

    @PutMapping("/characteristics/{id}")
    public ResponseEntity<CharacteristicDTO> updateCharacteristic(@PathVariable Long id,
                                                                   @Valid @RequestBody CharacteristicDTO dto) {
        return ResponseEntity.ok(characteristicService.update(id, dto));
    }

    @DeleteMapping("/characteristics/{id}")
    public ResponseEntity<Void> deleteCharacteristic(@PathVariable Long id) {
        characteristicService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Usuarios ----

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> listUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PatchMapping("/users/{id}/toggle-role")
    public ResponseEntity<UserDTO> toggleUserRole(@PathVariable Long id) {
        return ResponseEntity.ok(userService.toggleRole(id));
    }
}
