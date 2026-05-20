package com.stellarjets.service;

import com.stellarjets.dto.CharacteristicDTO;
import com.stellarjets.entity.Characteristic;
import com.stellarjets.exception.ConflictException;
import com.stellarjets.exception.ResourceNotFoundException;
import com.stellarjets.repository.CharacteristicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CharacteristicService {

    private final CharacteristicRepository characteristicRepository;

    public List<CharacteristicDTO> findAll() {
        return characteristicRepository.findAllByOrderByNameAsc().stream()
                .map(this::toDTO).toList();
    }

    @Transactional
    public CharacteristicDTO create(CharacteristicDTO dto) {
        if (characteristicRepository.existsByNameIgnoreCase(dto.getName().trim()))
            throw new ConflictException("Ya existe una característica con ese nombre.");
        Characteristic c = Characteristic.builder()
                .name(dto.getName().trim())
                .iconName(dto.getIconName())
                .build();
        return toDTO(characteristicRepository.save(c));
    }

    @Transactional
    public CharacteristicDTO update(Long id, CharacteristicDTO dto) {
        Characteristic c = getOrThrow(id);
        c.setName(dto.getName().trim());
        c.setIconName(dto.getIconName());
        return toDTO(characteristicRepository.save(c));
    }

    @Transactional
    public void delete(Long id) {
        getOrThrow(id);
        characteristicRepository.deleteById(id);
    }

    public CharacteristicDTO toDTO(Characteristic c) {
        return CharacteristicDTO.builder()
                .id(c.getId())
                .name(c.getName())
                .iconName(c.getIconName())
                .build();
    }

    private Characteristic getOrThrow(Long id) {
        return characteristicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Característica", id));
    }
}
