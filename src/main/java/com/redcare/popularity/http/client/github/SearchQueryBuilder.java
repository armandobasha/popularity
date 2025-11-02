package com.redcare.popularity.http.client.github;

import java.time.LocalDate;

public class SearchQueryBuilder {
    StringBuilder query = new StringBuilder();

    public SearchQueryBuilder addCreatedDate(LocalDate localDate) {
        this.append("created:>="+localDate.toString());
        return this;
    }

    public SearchQueryBuilder addLanguage(String language) {
        this.append("language:"+language);
        return this;
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
