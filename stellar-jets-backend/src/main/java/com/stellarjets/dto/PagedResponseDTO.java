package com.stellarjets.dto;

import lombok.*;
import java.util.List;

/** Wrapper genérico de paginación para todas las listas del frontend */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagedResponseDTO<T> {
    private List<T> content;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int pageSize;
    private boolean first;
    private boolean last;
}
