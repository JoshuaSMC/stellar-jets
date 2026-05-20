package com.stellarjets.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightRequestDTO {

    /** Código de vuelo opcional, ej: "SJ-007". Si se omite queda null. */
    @Size(max = 10, message = "El código de vuelo no puede superar 10 caracteres")
    private String flightNumber;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150)
    private String name;

    private String description;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal price;

    @NotNull(message = "El origen es obligatorio")
    @Valid
    private AirportRequestDTO origin;

    @NotNull(message = "El destino es obligatorio")
    @Valid
    private AirportRequestDTO destination;

    @Min(value = 1, message = "Debe haber al menos 1 asiento disponible")
    private int availableSeats;

    @Min(value = 1, message = "La duración debe ser mayor a 0 minutos")
    private Integer durationMinutes;

    @DecimalMin("1.0") @DecimalMax("5.0")
    private BigDecimal rating;

    private Long categoryId;

    private List<Long> characteristicIds;

    /** URLs de imágenes; la primera se convierte automáticamente en portada */
    private List<String> imageUrls;
}
