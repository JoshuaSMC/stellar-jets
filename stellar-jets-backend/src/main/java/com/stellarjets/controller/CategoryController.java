package com.stellarjets.controller;

import com.stellarjets.dto.CategoryDTO;
import com.stellarjets.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /** GET /api/categories - todas las categorías */
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> list() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    /** GET /api/categories/active - solo las que tienen vuelos activos (para filtros del home) */
    @GetMapping("/active")
    public ResponseEntity<List<CategoryDTO>> listActive() {
        return ResponseEntity.ok(categoryService.findWithActiveFlights());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }
}
