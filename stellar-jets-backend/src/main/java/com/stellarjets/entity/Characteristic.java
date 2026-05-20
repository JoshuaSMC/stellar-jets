package com.stellarjets.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "characteristics")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Characteristic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /** Emoji o nombre de icono, ej: "wifi", "🍽️" */
    @Column(name = "icon_name", length = 50)
    private String iconName;
}
