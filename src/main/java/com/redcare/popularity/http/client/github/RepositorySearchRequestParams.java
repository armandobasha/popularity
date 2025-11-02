package com.redcare.popularity.http.client.github;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RepositorySearchRequestParams (String q, String sort, String order, int per_page, int page) {
}
