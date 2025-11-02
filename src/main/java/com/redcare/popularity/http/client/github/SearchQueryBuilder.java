package com.redcare.popularity.http.client.github;

import java.time.LocalDate;

public class SearchQueryBuilder {
    StringBuilder query = new StringBuilder();
    private boolean hasCreatedDate = false;
    private boolean hasLanguage = false;

    public SearchQueryBuilder addCreatedDate(LocalDate localDate) {
        if (hasCreatedDate) {
            removeCreatedDate();
        }
        this.append("created:>="+localDate.toString());
        hasCreatedDate = true;
        return this;
    }

    public SearchQueryBuilder addLanguage(String language) {
        if(language == null || language.isBlank()) {
            return this;
        }
        if (hasLanguage) {
            removeLanguage();
        }
        this.append("language:"+language);
        hasLanguage = true;
        return this;
    }

    private void removeCreatedDate() {
        String currentQuery = query.toString();
        query.setLength(0);
        String[] parts = currentQuery.split(" ");
        for (String part : parts) {
            if (!part.startsWith("created:>=")) {
                append(part);
            }
        }
    }

    private void removeLanguage() {
        String currentQuery = query.toString();
        query.setLength(0);
        String[] parts = currentQuery.split(" ");
        for (String part : parts) {
            if (!part.startsWith("language:")) {
                append(part);
            }
        }
    }

    private void append(String str) {
        if(!query.isEmpty()) {
            query.append(" ");
        }
        query.append(str);
    }

    public String build() {
        return query.toString();
    }
}
