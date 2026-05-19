package com.stellarjets.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "categories")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    /** Nombre del icono (ej: "plane", "star", "globe") para el frontend */
    @Column(name = "icon_name", length = 50)
    private String iconName;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Flight> flights;
}
