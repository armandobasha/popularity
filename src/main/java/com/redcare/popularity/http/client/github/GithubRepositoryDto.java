package com.redcare.popularity.http.client.github;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.Instant;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GithubRepositoryDto(
        long id,
        String fullName,
        String language,
        int stargazersCount,
        int forksCount,
        Instant createdAt,
        Instant pushedAt,
        Instant updatedAt

) {
}
