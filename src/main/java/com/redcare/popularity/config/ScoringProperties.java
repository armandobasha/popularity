package com.redcare.popularity.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "scoring")
@Data
public class ScoringProperties {
    private Weights weights = new Weights();

    @Data
    public static class Weights {
        private double star = 0.5;
        private double fork = 0.3;
        private double recency = 0.2;
    }
}

