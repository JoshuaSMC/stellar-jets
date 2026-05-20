package com.stellarjets.service;

import com.stellarjets.dto.UserDTO;
import com.stellarjets.entity.*;
import com.stellarjets.exception.ResourceNotFoundException;
import com.stellarjets.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public List<UserDTO> findAll() {
        return userRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional
    public UserDTO toggleRole(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
        user.setRole(user.getRole() == Role.ADMIN ? Role.USER : Role.ADMIN);
        return toDTO(userRepository.save(user));
    }

    public UserDTO toDTO(User u) {
        return UserDTO.builder()
                .id(u.getId())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .email(u.getEmail())
                .role(u.getRole().name())
                .active(u.isActive())
                .build();
    }
}
