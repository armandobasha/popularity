package com.redcare.popularity.service;

import com.redcare.popularity.config.ScoringProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ScoreCalculatorService Unit Tests")
class ScoreCalculatorServiceTest {

    private ScoreCalculatorService scoreCalculatorService;

    @BeforeEach
    void setUp() {
        ScoringProperties scoringProperties = new ScoringProperties();
        scoreCalculatorService = new ScoreCalculatorService(scoringProperties);
    }

    @Test
    @DisplayName("Should calculate score successfully with valid inputs")
    void testCalculateScore_Success() {
        int starCount = 1000;
        int forkCount = 200;
        Instant lastPush = Instant.now().minusSeconds(86400 * 5);

        double score = scoreCalculatorService.score(starCount, forkCount, lastPush);

        assertTrue(score > 0, "Score should be positive for popular repositories");
        assertEquals(559.0, score, 0.01, "Score should match expected calculation");
    }

    @Test
    @DisplayName("Should fail when calculating score with negative days (future push date)")
    void testCalculateScore_Failure_InvalidPushDate() {
        int starCount = 100;
        int forkCount = 50;
        Instant futurePush = Instant.now().plusSeconds(86400 * 10);

        double score = scoreCalculatorService.score(starCount, forkCount, futurePush);

        assertEquals(65.0, score, 0.01, "Future dates are incorrectly handled as 0 days");
    }
}

