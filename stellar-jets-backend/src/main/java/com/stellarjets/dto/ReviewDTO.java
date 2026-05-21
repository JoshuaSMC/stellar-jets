package com.stellarjets.dto;

import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ReviewDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private int stars;
    private String comment;
    private String createdAt;
}
