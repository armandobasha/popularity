package com.redcare.popularity.http.client.github;

import java.time.LocalDate;
public class SearchQueryBuilder {
    private LocalDate createdDate;
    private String language;

    public SearchQueryBuilder addCreatedDate(LocalDate localDate) {
        this.createdDate = localDate;
        return this;
    }

    public SearchQueryBuilder addLanguage(String language) {
        this.language = language;
        return this;
    }

    public String build() {
        StringBuilder query = new StringBuilder();
        if (createdDate != null) {
            query.append("created:>=").append(createdDate);
        }
        if (language != null && !language.isBlank()) {
            if (!query.isEmpty()) query.append(" ");
            query.append("language:").append(language);
        }
        return query.toString();
    }
}