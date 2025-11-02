package com.redcare.popularity.service;

import com.redcare.popularity.config.ScoringProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ScoreCalculatorService {
    private final ScoringProperties scoringProperties;

    /**
     * The scoring will use the weights to calculate the final score.
     * The star count and fork count will positively impact the score,
     * while the time since last push will negatively impact the score.
     *
     * @param starCount Number of stars
     * @param forkCount Number of counts
     * @param lastPush Date of the last push
     * @return
     */
    public double score(int starCount, int forkCount, Instant lastPush) {
        var daysSinceLastPush = Math.max(0, Duration.between(lastPush, Instant.now()).toDays());

        var weights = scoringProperties.getWeights();
        var score = weights.getStar() * starCount + weights.getFork() * forkCount - weights.getRecency() * daysSinceLastPush;
        return Math.round(score * 100.0) / 100.0;
    }
}
