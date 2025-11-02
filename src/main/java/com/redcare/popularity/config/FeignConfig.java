package com.redcare.popularity.config;

import com.redcare.popularity.error.RateLimitExceededException;
import feign.Logger;
import feign.RequestInterceptor;
import feign.Response;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Configuration
@Slf4j
public class FeignConfig {
    @Value("${http.github.api.token}")
    private String token;

    @Value("${http.github.api.retry.maxAttempts:3}")
    private int maxAttempts;

    @Value("${http.github.api.retry.period:1000}")
    private long retryPeriod;

    @Value("${http.github.api.retry.maxPeriod:5000}")
    private long maxRetryPeriod;

    @Value("${http.github.api.rateLimit.limitForPeriod:30}")
    private int limitForPeriod;

    @Value("${http.github.api.rateLimit.limitRefreshPeriod:60}")
    private int limitRefreshPeriodSeconds;

    @PostConstruct
    void adjustRateLimitIfNoToken() {
        if (token == null || token.isBlank()) {
            this.limitForPeriod = 10;
        }
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(
                retryPeriod,
                maxRetryPeriod,
                maxAttempts
        );
    }

    @Bean
    public RateLimiter rateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(limitForPeriod)
                .limitRefreshPeriod(Duration.ofSeconds(limitRefreshPeriodSeconds))
                .timeoutDuration(Duration.ofMillis(0)) // Fail immediately if rate limit is exceeded
                .build();

        return RateLimiter.of("github-api-rate-limiter", config);
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            if (token != null && !token.isEmpty()) {
                requestTemplate.header("Authorization", "Bearer " + token);
                requestTemplate.header("X-GitHub-Api-Version", " 2022-11-28");
            }
        };
    }

    @Bean
    public RequestInterceptor rateLimitingInterceptor(RateLimiter rateLimiter) {
        return requestTemplate -> {
            try {
                if (!rateLimiter.acquirePermission()) {
                    log.warn("Rate limit exceeded for GitHub API request");
                    throw new RateLimitExceededException("GitHub API rate limit exceeded. Please try again later.");
                }
            } catch (RequestNotPermitted e) {
                log.warn("Rate limit exceeded for GitHub API request: {}", e.getMessage());
                throw new RateLimitExceededException("GitHub API rate limit exceeded. Please try again later.", e);
            }
        };
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ErrorDecoder() {
            private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();

            @Override
            public Exception decode(String methodKey, Response response) {
                if (List.of(429, 403).contains(response.status())) {
                    log.warn("GitHub API returned 429 rate limit exceeded for method: {}", methodKey);
                    return new RateLimitExceededException("GitHub API rate limit exceeded. Please try again later.");
                }
                return defaultErrorDecoder.decode(methodKey, response);
            }
        };
    }
}