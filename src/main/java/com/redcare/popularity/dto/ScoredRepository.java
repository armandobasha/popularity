package com.redcare.popularity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Repository with calculated popularity score")
public record ScoredRepository(
        @Schema(description = "Full name of the repository (e.g., owner/repository)", example = "spring-projects/spring-boot")
        String name,
        @Schema(description = "Creation date of the repository", example = "2024-01-15T10:30:00Z")
        String createdAt,
        @Schema(description = "Calculated popularity score", example = "85.5")
        double popularityScore,
        @Schema(description = "Primary programming language of the repository", example = "Java")
        String language
) {
}
