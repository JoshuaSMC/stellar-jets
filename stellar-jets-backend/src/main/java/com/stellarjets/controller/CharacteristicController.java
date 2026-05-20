package com.stellarjets.controller;

import com.stellarjets.dto.CharacteristicDTO;
import com.stellarjets.service.CharacteristicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/characteristics")
@RequiredArgsConstructor
public class CharacteristicController {

    private final CharacteristicService characteristicService;

    @GetMapping
    public ResponseEntity<List<CharacteristicDTO>> getAll() {
        return ResponseEntity.ok(characteristicService.findAll());
    }
}
