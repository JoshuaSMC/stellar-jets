package com.stellarjets.service;

import com.stellarjets.dto.CategoryDTO;
import com.stellarjets.entity.Category;
import com.stellarjets.exception.ResourceNotFoundException;
import com.stellarjets.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public List<CategoryDTO> findWithActiveFlights() {
        return categoryRepository.findCategoriesWithActiveFlights().stream()
                .map(this::toDTO)
                .toList();
    }

    public CategoryDTO findById(Long id) {
        return toDTO(getOrThrow(id));
    }

    @Transactional
    public CategoryDTO create(CategoryDTO dto) {
        Category category = Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .iconName(dto.getIconName())
                .build();
        return toDTO(categoryRepository.save(category));
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto) {
        Category category = getOrThrow(id);
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setIconName(dto.getIconName());
        return toDTO(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        getOrThrow(id);
        categoryRepository.deleteById(id);
    }

    private Category getOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", id));
    }

    public CategoryDTO toDTO(Category c) {
        long count = c.getFlights() == null ? 0L
                : c.getFlights().stream().filter(f -> f.isActive()).count();
        return CategoryDTO.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .iconName(c.getIconName())
                .flightCount(count)
                .build();
    }
}
