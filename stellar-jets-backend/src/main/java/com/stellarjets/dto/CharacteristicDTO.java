package com.stellarjets.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacteristicDTO {
    private Long id;
    private String name;
    private String iconName;
}
