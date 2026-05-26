package com.stellarjets.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacteristicDTO {
    private Long id;

    @NotBlank(message = "El nombre de la característica es obligatorio.")
    @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres.")
    private String name;

    @NotBlank(message = "El ícono es obligatorio.")
    private String iconName;
}
