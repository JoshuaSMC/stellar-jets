package com.stellarjets.service;

import com.stellarjets.dto.*;
import com.stellarjets.entity.*;
import com.stellarjets.exception.ConflictException;
import com.stellarjets.exception.ResourceNotFoundException;
import com.stellarjets.repository.*;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FlightService {

    private final FlightRepository flightRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;
    private final CharacteristicRepository characteristicRepository;
    private final CharacteristicService characteristicService;

    public PagedResponseDTO<FlightDTO> findAllActive(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return toPagedResponse(flightRepository.findByActiveTrue(pageable));
    }

    public PagedResponseDTO<FlightDTO> search(String query, List<Long> categoryIds,
                                               LocalDate checkIn, LocalDate checkOut,
                                               int page, int size) {
        Page<Flight> result;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        boolean hasDates  = checkIn != null && checkOut != null;
        boolean hasQuery  = StringUtils.hasText(query);
        boolean hasCats   = categoryIds != null && !categoryIds.isEmpty();

        if (hasDates && hasQuery) {
            result = flightRepository.searchActiveInDateRange(query.trim(), checkIn, checkOut, pageable);
        } else if (hasDates) {
            result = flightRepository.findAvailableInDateRange(checkIn, checkOut, pageable);
        } else if (hasQuery) {
            result = flightRepository.searchActive(query.trim(), pageable);
        } else if (hasCats) {
            result = categoryIds.size() == 1
                    ? flightRepository.findByActiveTrueAndCategoryId(categoryIds.get(0), pageable)
                    : flightRepository.findByActiveTrueAndCategoryIdIn(categoryIds, pageable);
        } else {
            result = flightRepository.findByActiveTrueRandom(PageRequest.of(page, size));
        }

        return toPagedResponse(result);
    }

    public List<FlightDTO> findTopRated() {
        return flightRepository.findTopRated(PageRequest.of(0, 10))
                .getContent()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public FlightDTO findById(Long id) {
        return toDTO(getOrThrow(id));
    }

    public PagedResponseDTO<FlightDTO> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return toPagedResponse(flightRepository.findAll(pageable));
    }

    @Transactional
    public FlightDTO create(FlightRequestDTO dto) {
        if (flightRepository.existsByNameIgnoreCase(dto.getName().trim()))
            throw new ConflictException("El nombre del vuelo ya está en uso. Ingresá uno diferente.");
        if (dto.getFlightNumber() != null && !dto.getFlightNumber().isBlank()
                && flightRepository.existsByFlightNumberIgnoreCase(dto.getFlightNumber().trim()))
            throw new ConflictException("El código de vuelo ya está en uso. Ingresá uno diferente.");
        return toDTO(flightRepository.save(applyRequest(new Flight(), dto)));
    }

    @Transactional
    public FlightDTO update(Long id, FlightRequestDTO dto) {
        Flight flight = getOrThrow(id);
        if (flightRepository.existsByNameIgnoreCaseAndIdNot(dto.getName().trim(), id))
            throw new ConflictException("El nombre del vuelo ya está en uso. Ingresá uno diferente.");
        if (dto.getFlightNumber() != null && !dto.getFlightNumber().isBlank()
                && flightRepository.existsByFlightNumberIgnoreCaseAndIdNot(dto.getFlightNumber().trim(), id))
            throw new ConflictException("El código de vuelo ya está en uso. Ingresá uno diferente.");
        return toDTO(flightRepository.save(applyRequest(flight, dto)));
    }

    @Transactional
    public void delete(Long id) {
        getOrThrow(id);
        flightRepository.deleteById(id);
    }

    @Transactional
    public FlightDTO toggleActive(Long id) {
        Flight flight = getOrThrow(id);
        flight.setActive(!flight.isActive());
        return toDTO(flightRepository.save(flight));
    }

    // ---- helpers privados ----

    private Flight applyRequest(Flight flight, FlightRequestDTO dto) {
        flight.setFlightNumber(dto.getFlightNumber());
        flight.setName(dto.getName());
        flight.setDescription(dto.getDescription());
        flight.setPrice(dto.getPrice());
        flight.setAvailableSeats(dto.getAvailableSeats());
        flight.setDurationMinutes(dto.getDurationMinutes());
        flight.setRating(dto.getRating());

        if (dto.getOrigin() != null) {
            flight.setOrigin(AirportInfo.builder()
                    .city(dto.getOrigin().getCity())
                    .country(dto.getOrigin().getCountry())
                    .iataCode(dto.getOrigin().getIataCode())
                    .build());
        }

        if (dto.getDestination() != null) {
            flight.setDestination(AirportInfo.builder()
                    .city(dto.getDestination().getCity())
                    .country(dto.getDestination().getCountry())
                    .iataCode(dto.getDestination().getIataCode())
                    .build());
        }

        if (dto.getCategoryId() != null) {
            Category cat = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría", dto.getCategoryId()));
            flight.setCategory(cat);
        }

        if (dto.getCharacteristicIds() != null) {
            flight.getCharacteristics().clear();
            if (!dto.getCharacteristicIds().isEmpty()) {
                flight.getCharacteristics().addAll(
                        characteristicRepository.findAllById(dto.getCharacteristicIds())
                );
            }
        }

        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            flight.getImages().clear();
            for (int i = 0; i < dto.getImageUrls().size(); i++) {
                FlightImage img = FlightImage.builder()
                        .url(dto.getImageUrls().get(i))
                        .altText(flight.getName() + " - imagen " + (i + 1))
                        .cover(i == 0)
                        .build();
                flight.addImage(img);
            }
        }

        return flight;
    }

    private Flight getOrThrow(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vuelo", id));
    }

    private PagedResponseDTO<FlightDTO> toPagedResponse(Page<Flight> page) {
        return PagedResponseDTO.<FlightDTO>builder()
                .content(page.getContent().stream().map(this::toDTO).toList())
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .pageSize(page.getSize())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    public FlightDTO toDTO(Flight f) {
        List<FlightImageDTO> images = f.getImages() == null ? List.of()
                : f.getImages().stream()
                    .map(img -> FlightImageDTO.builder()
                            .id(img.getId())
                            .url(img.getUrl())
                            .altText(img.getAltText())
                            .cover(img.isCover())
                            .build())
                    .toList();

        return FlightDTO.builder()
                .id(f.getId())
                .flightNumber(f.getFlightNumber())
                .name(f.getName())
                .description(f.getDescription())
                .price(f.getPrice())
                .origin(toAirportDTO(f.getOrigin()))
                .destination(toAirportDTO(f.getDestination()))
                .availableSeats(f.getAvailableSeats())
                .durationMinutes(f.getDurationMinutes())
                .rating(f.getRating())
                .active(f.isActive())
                .category(f.getCategory() != null ? categoryService.toDTO(f.getCategory()) : null)
                .images(images)
                .characteristics(f.getCharacteristics() == null ? List.of()
                        : f.getCharacteristics().stream().map(characteristicService::toDTO).toList())
                .reviewCount(f.getReviewCount())
                .build();
    }

    private AirportDTO toAirportDTO(AirportInfo info) {
        if (info == null) return null;
        return AirportDTO.builder()
                .city(info.getCity())
                .country(info.getCountry())
                .iataCode(info.getIataCode())
                .build();
    }
}
