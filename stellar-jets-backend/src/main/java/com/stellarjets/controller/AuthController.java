package com.stellarjets.controller;

import com.stellarjets.dto.*;
import com.stellarjets.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO dto) {
        LoginResponseDTO response = authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/resend-confirmation")
    public ResponseEntity<?> resendConfirmation(@RequestBody Map<String, String> body) {
        String email = body.getOrDefault("email", "");
        if (email.isBlank()) return ResponseEntity.badRequest().body(Map.of("message", "El correo es obligatorio."));
        authService.resendConfirmation(email);
        return ResponseEntity.ok(Collections.singletonMap("message", "Correo de confirmación reenviado."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
