package com.redcare.popularity.http.client.github;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SearchResponse(
        int totalCount,
        boolean incompleteResults,
        List<GithubRepository> items
) {
}
