package com.stellarjets.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ReviewRequestDTO {
    @Min(1) @Max(5)
    private int stars;
    private String comment;
}
