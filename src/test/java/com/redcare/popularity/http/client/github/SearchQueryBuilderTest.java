package com.redcare.popularity.http.client.github;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SearchQueryBuilder Unit Tests")
class SearchQueryBuilderTest {

    private SearchQueryBuilder searchQueryBuilder;

    @BeforeEach
    void setUp() {
        searchQueryBuilder = new SearchQueryBuilder();
    }

    @Test
    @DisplayName("Should build query with created date only")
    void testBuildQuery_WithCreatedDateOnly() {
        var date = LocalDate.of(2023, 1, 1);
        
        var result = searchQueryBuilder
                .addCreatedDate(date)
                .build();

        assertEquals("created:>=2023-01-01", result, "Query should contain created date");
    }

    @Test
    @DisplayName("Should build query with language only")
    void testBuildQuery_WithLanguageOnly() {
        var result = searchQueryBuilder
                .addLanguage("Java")
                .build();

        assertEquals("language:Java", result, "Query should contain language");
    }

    @Test
    @DisplayName("Should build query with both created date and language")
    void testBuildQuery_WithCreatedDateAndLanguage() {
        var date = LocalDate.of(2023, 1, 1);
        
        var result = searchQueryBuilder
                .addCreatedDate(date)
                .addLanguage("Java")
                .build();

        assertEquals("created:>=2023-01-01 language:Java", result, 
                "Query should contain both created date and language separated by space");
    }

    @Test
    @DisplayName("Should ignore null language")
    void testBuildQuery_WithNullLanguage() {
        var date = LocalDate.of(2023, 1, 1);
        
        var result = searchQueryBuilder
                .addCreatedDate(date)
                .addLanguage(null)
                .build();

        assertEquals("created:>=2023-01-01", result, 
                "Query should not include language when null");
    }

    @Test
    @DisplayName("Should ignore blank language")
    void testBuildQuery_WithBlankLanguage() {
        var date = LocalDate.of(2023, 1, 1);
        
        var result = searchQueryBuilder
                .addCreatedDate(date)
                .addLanguage("")
                .build();

        assertEquals("created:>=2023-01-01", result, 
                "Query should not include language when blank");
    }

    @Test
    @DisplayName("Should ignore whitespace-only language")
    void testBuildQuery_WithWhitespaceOnlyLanguage() {
        var date = LocalDate.of(2023, 1, 1);
        
        var result = searchQueryBuilder
                .addCreatedDate(date)
                .addLanguage("   ")
                .build();

        assertEquals("created:>=2023-01-01", result, 
                "Query should not include language when only whitespace");
    }

    @Test
    @DisplayName("Should build empty query when no parameters added")
    void testBuildQuery_WithNoParameters() {
        var result = searchQueryBuilder.build();

        assertEquals("", result, "Query should be empty when no parameters added");
    }

    @Test
    @DisplayName("Should support method chaining (fluent interface)")
    void testBuildQuery_MethodChaining() {
        var date = LocalDate.of(2023, 1, 1);
        
        var builder = new SearchQueryBuilder();
        var returnedBuilder = builder
                .addCreatedDate(date)
                .addLanguage("Python");

        assertSame(builder, returnedBuilder, "Method chaining should return same instance");
        
        var result = builder.build();
        assertEquals("created:>=2023-01-01 language:Python", result, 
                "Chained methods should work correctly");
    }

    @Test
    @DisplayName("Should only keep the last language when multiple language calls are made")
    void testBuildQuery_MultipleLanguageCalls() {
        var date = LocalDate.of(2023, 1, 1);
        
        var result = searchQueryBuilder
                .addCreatedDate(date)
                .addLanguage("Java")
                .addLanguage("Python")
                .build();

        assertEquals("created:>=2023-01-01 language:Python", result, 
                "Query should contain only the last language parameter");
    }

    @Test
    @DisplayName("Should only keep the last date when multiple date calls are made")
    void testBuildQuery_MultipleDateCalls() {
        var date1 = LocalDate.of(2023, 1, 1);
        var date2 = LocalDate.of(2024, 1, 1);
        
        var result = searchQueryBuilder
                .addCreatedDate(date1)
                .addCreatedDate(date2)
                .build();

        assertEquals("created:>=2024-01-01", result, 
                "Query should contain only the last date parameter");
    }

    @Test
    @DisplayName("Should build query with different date formats")
    void testBuildQuery_WithDifferentDateFormats() {
        var date = LocalDate.of(2024, 12, 31);
        
        var result = searchQueryBuilder
                .addCreatedDate(date)
                .build();

        assertEquals("created:>=2024-12-31", result, 
                "Query should format date correctly");
    }

    @Test
    @DisplayName("Should replace created date when both created and language are present")
    void testBuildQuery_ReplaceCreatedDateWhenBothPresent() {
        var date1 = LocalDate.of(2023, 1, 1);
        var date2 = LocalDate.of(2024, 1, 1);
        
        var result = searchQueryBuilder
                .addCreatedDate(date1)
                .addLanguage("Java")
                .addCreatedDate(date2)
                .build();

        assertEquals("created:>=2024-01-01 language:Java", result,
                "Query should replace created date while keeping language");
    }

    @Test
    @DisplayName("Should replace language when both created and language are present")
    void testBuildQuery_ReplaceLanguageWhenBothPresent() {
        var date = LocalDate.of(2023, 1, 1);
        
        var result = searchQueryBuilder
                .addCreatedDate(date)
                .addLanguage("Java")
                .addLanguage("Python")
                .build();

        assertEquals("created:>=2023-01-01 language:Python", result, 
                "Query should replace language while keeping created date");
    }
}
