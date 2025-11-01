package com.redcare.popularity.dto;

import lombok.Builder;

@Builder
public record ScoredRepository(
        String name,
        String createdAt,
        double popularityScore,
        String language
) {
}
