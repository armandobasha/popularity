package com.redcare.popularity.http.client.github;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RepositorySearchRequestParams (String q, String sort, String order, int per_page, int page) {
}
