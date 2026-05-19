package com.stellarjets.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private int status;
    private String error;
    private String message;
    private String path;

    /** Detalle de errores de validación campo por campo */
    private List<FieldError> fieldErrors;

    @Getter @Setter
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
    }
}
