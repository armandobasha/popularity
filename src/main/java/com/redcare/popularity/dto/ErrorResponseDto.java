package com.redcare.popularity.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error response containing error details")
public record ErrorResponseDto(
        @Schema(description = "Error type or category", example = "Invalid request parameter")
        String error,
        @Schema(description = "Detailed error message", example = "Parameter 'createdAfter' has invalid value 'invalid-date'. Expected type: LocalDate.")
        String message,
        @Schema(description = "HTTP status code", example = "400")
        int status,
        @Schema(description = "Timestamp when the error occurred", example = "2024-01-15T10:30:00Z")
        String timestamp
) {
}

