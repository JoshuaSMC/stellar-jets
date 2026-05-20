package com.stellarjets.service;

import com.stellarjets.dto.*;
import com.stellarjets.entity.*;
import com.stellarjets.exception.ConflictException;
import com.stellarjets.repository.UserRepository;
import com.stellarjets.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Transactional
    public LoginResponseDTO register(RegisterRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail().toLowerCase().trim()))
            throw new ConflictException("El correo electrónico ya está registrado.");

        User user = User.builder()
                .firstName(dto.getFirstName().trim())
                .lastName(dto.getLastName().trim())
                .email(dto.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.USER)
                .active(true)
                .build();

        user = userRepository.save(user);
        emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName(), user.getLastName());
        return toLoginResponse(user);
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getEmail().toLowerCase().trim(),
                            dto.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Credenciales inválidas. Revisá el correo y la contraseña.");
        }

        User user = userRepository.findByEmail(dto.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new BadCredentialsException("Usuario no encontrado."));

        return toLoginResponse(user);
    }

    public void resendConfirmation(String email) {
        User user = userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new BadCredentialsException("Usuario no encontrado."));
        emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName(), user.getLastName());
    }

    private LoginResponseDTO toLoginResponse(User user) {
        return LoginResponseDTO.builder()
                .token(jwtUtil.generateToken(user))
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
