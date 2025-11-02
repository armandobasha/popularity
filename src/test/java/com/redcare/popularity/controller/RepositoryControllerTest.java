package com.redcare.popularity.controller;

import com.redcare.popularity.dto.ScoredRepositoryDto;
import com.redcare.popularity.service.RepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;


import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RepositoryController Unit Tests")
class RepositoryControllerTest {

    @Mock
    private RepositoryService repositoryService;

    @InjectMocks
    private RepositoryController repositoryController;

    private ScoredRepositoryDto sampleScoredRepository;

    @BeforeEach
    void setUp() {
        sampleScoredRepository = ScoredRepositoryDto.builder()
                .name("spring-projects/spring-boot")
                .language("Java")
                .popularityScore(95.5)
                .createdAt("2023-01-01T00:00:00Z")
                .build();
    }

    @Test
    @DisplayName("Should successfully return popular repositories with valid parameters")
    void testPopularRepositories_Success() {
        // Given: Valid request parameters and mock service response
        var createdAfter = LocalDate.of(2024, 1, 1);
        var language = "Java";
        var page = 1;

        var mockRepositories = List.of(sampleScoredRepository);
        when(repositoryService.getPopularScoredRepositories(createdAfter, language, page))
                .thenReturn(mockRepositories);

        var response = repositoryController.popularRepositories(
                createdAfter, language, page
        );

        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK");
        var body = response.getBody();
        assertNotNull(body, "Response body should not be null");
        assertEquals(1, body.size(), "Should return one repository");
        assertEquals("spring-projects/spring-boot", body.get(0).name());
        
        verify(repositoryService, times(1)).getPopularScoredRepositories(createdAfter, language, page);
    }

    @Test
    @DisplayName("Should fail when service throws exception for invalid date")
    void testPopularRepositories_Failure_InvalidDate() {
        var invalidDate = LocalDate.of(1900, 1, 1);
        var language = "Java";
        var page = 1;

        when(repositoryService.getPopularScoredRepositories(invalidDate, language, page))
                .thenThrow(new RuntimeException("Invalid date range or API error"));

        assertThrows(RuntimeException.class, () -> {
            repositoryController.popularRepositories(invalidDate, language, page);
        }, "Should throw exception for invalid date");

        verify(repositoryService, times(1)).getPopularScoredRepositories(invalidDate, language, page);
    }
}

