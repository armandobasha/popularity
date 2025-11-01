package com.redcare.popularity.service;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class ScoreCalculatorService {
    private  final double starWeight = 0.5;
    private  final double forkWeight = 0.3;
    private  final double recencyWeight = 0.2;

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

        var score = starWeight*starCount + forkWeight*forkCount - recencyWeight*daysSinceLastPush;
        return Math.round(score * 100.0) / 100.0;
    }
}
