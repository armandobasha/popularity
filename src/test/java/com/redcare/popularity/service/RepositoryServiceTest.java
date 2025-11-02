package com.redcare.popularity.service;

import com.redcare.popularity.dto.ScoredRepositoryDto;
import com.redcare.popularity.http.client.github.GithubHttpClient;
import com.redcare.popularity.http.client.github.GithubRepositoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RepositoryService Unit Tests")
class RepositoryServiceTest {

    @Mock
    private GithubHttpClient githubClient;

    @Mock
    private ScoreCalculatorService scoreCalculatorService;

    @InjectMocks
    private RepositoryService repositoryService;

    private GithubRepositoryDto sampleRepository;

    @BeforeEach
    void setUp() {
        sampleRepository = new GithubRepositoryDto(
                12345L,
                "spring-projects/spring-boot",
                "Java",
                50000,
                25000,
                Instant.parse("2023-01-01T00:00:00Z"),
                Instant.now().minusSeconds(86400 * 2),
                Instant.now()
        );
    }

    @Test
    @DisplayName("Should successfully map repository to scored DTO")
    void testMapScoredRepository_Success() {
        var expectedScore = 125.5;
        when(scoreCalculatorService.score(
                sampleRepository.stargazersCount(),
                sampleRepository.forksCount(),
                sampleRepository.pushedAt()
        )).thenReturn(expectedScore);

        ScoredRepositoryDto result = repositoryService.mapScoredRepository(sampleRepository);

        assertNotNull(result, "Scored repository DTO should not be null");
        assertEquals("spring-projects/spring-boot", result.name(), "Name should match");
        assertEquals("Java", result.language(), "Language should match");
        assertEquals(expectedScore, result.popularityScore(), 0.01, "Score should match");
        assertNotNull(result.createdAt(), "Created at should not be null");
        
        verify(scoreCalculatorService, times(1)).score(
                sampleRepository.stargazersCount(),
                sampleRepository.forksCount(),
                sampleRepository.pushedAt()
        );
    }

    @Test
    @DisplayName("Should fail when repository has null required fields")
    void testMapScoredRepository_Failure_NullFields() {
        // Given: Repository with null language (simulating API inconsistency)
        GithubRepositoryDto invalidRepository = new GithubRepositoryDto(
                12345L,
                null,
                null,
                100,
                50,
                Instant.now(),
                Instant.now(),
                Instant.now()
        );

        assertDoesNotThrow(() -> {
            when(scoreCalculatorService.score(anyInt(), anyInt(), any(Instant.class))).thenReturn(10.0);
            ScoredRepositoryDto result = repositoryService.mapScoredRepository(invalidRepository);

            assertNull(result.name(), "Language is null - this is a data quality issue");
        });
    }
}

