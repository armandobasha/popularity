package com.redcare.popularity.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.redcare.popularity.dto.ErrorResponseDto;
import com.redcare.popularity.dto.ScoredRepositoryDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDate;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("RepositoryController Integration Tests")
class RepositoryControllerIntegrationTest {

    private static final int WIREMOCK_PORT = 8089;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static WireMockServer wireMockServer;

    static {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(WIREMOCK_PORT));
        wireMockServer.start();
        WireMock.configureFor(WIREMOCK_PORT);
    }

    @BeforeEach
    void setUp() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.resetAll();
        }
    }

    @AfterEach
    void tearDown() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.resetAll();
        }
    }

    @AfterAll
    static void tearDownAll() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("http.github.api.url", () -> "http://localhost:" + WIREMOCK_PORT);
    }

    @Test
    @DisplayName("Should successfully retrieve popular repositories - Succesful Request")
    void testGetPopularRepositories_Success() {
        var mockGitHubResponse = """
                {
                    "total_count": 4316334,
                    "incomplete_results": false,
                    "items": [
                        {
                            "id": 1075431749,
                            "full_name": "karpathy/nanochat",
                            "language": "Python",
                            "stargazers_count": 35049,
                            "forks_count": 3983,
                            "created_at": "2025-10-13T13:46:35Z",
                            "pushed_at": "2025-11-01T16:04:42Z",
                            "updated_at": "2025-11-02T13:20:25Z"
                        }
                    ]
                }
                """;

        wireMockServer.stubFor(get(urlPathEqualTo("/search/repositories"))
                .withQueryParam("q", WireMock.matching(".+"))
                .withQueryParam("sort", WireMock.equalTo("stars"))
                .withQueryParam("order", WireMock.equalTo("desc"))
                .withQueryParam("per_page", WireMock.equalTo("100"))
                .withQueryParam("page", WireMock.equalTo("1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockGitHubResponse)));

        var createdAfter = LocalDate.of(2024, 1, 1);
        var language = "Python";
        var page = 1;

        var url = String.format("http://localhost:%d/api/repositories/popular?createdAfter=%s&language=%s&page=%d",
                port, createdAfter, language, page);

        var response = restTemplate.getForEntity(url, ScoredRepositoryDto[].class);

        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK");
        assertNotNull(response.getBody(), "Response body should not be null");

        var repositories = List.of(response.getBody());
        assertEquals(1, repositories.size(), "Should return one repository");

        var repository = repositories.getFirst();
        assertEquals("karpathy/nanochat", repository.name(), "Repository name should match");
        assertEquals("Python", repository.language(), "Language should match");
        assertNotNull(repository.createdAt(), "Created at should not be null");
        assertTrue(repository.popularityScore() > 0, "Popularity score should be positive");

        wireMockServer.verify(getRequestedFor(urlPathEqualTo("/search/repositories"))
                .withQueryParam("q", WireMock.matching(".+"))
                .withQueryParam("sort", WireMock.equalTo("stars"))
                .withQueryParam("order", WireMock.equalTo("desc"))
                .withQueryParam("per_page", WireMock.equalTo("100"))
                .withQueryParam("page", WireMock.equalTo("1")));
    }

    @Test
    @DisplayName("Should handle GitHub server being down - Service Unavailable")
    void testGetPopularRepositories_ServiceUnavailable() {
        wireMockServer.stop();

        var createdAfter = LocalDate.of(2024, 1, 1);
        var language = "Python";
        var page = 1;

        var url = String.format("http://localhost:%d/api/repositories/popular?createdAfter=%s&language=%s&page=%d",
                port, createdAfter, language, page);

        var response = restTemplate.getForEntity(url, ErrorResponseDto.class);

        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), 
                "Status should be INTERNAL_SERVER_ERROR when server is down");
        assertNotNull(response.getBody(), "Error response body should not be null");
        
        var errorResponse = response.getBody();
        assertNotNull(errorResponse, "Error response should not be null");
        assertEquals(500, errorResponse.status(), "Status code should be 500");
        assertNotNull(errorResponse.error(), "Error message should not be null");
        assertNotNull(errorResponse.message(), "Error details should not be null");
        assertNotNull(errorResponse.timestamp(), "Timestamp should not be null");

        wireMockServer.start();
        WireMock.configureFor(WIREMOCK_PORT);
    }

    @Test
    @DisplayName("Should handle GitHub rate limit exceeded - 429 Response")
    void testGetPopularRepositories_RateLimitExceeded() {
        var rateLimitErrorResponse = """
                {
                    "message": "API rate limit exceeded for xxx.xxx.xxx.xxx. (But here's the good news: Authenticated requests get a higher rate limit. Check out the documentation for more details.)",
                    "documentation_url": "https://docs.github.com/rest/overview/resources-in-the-rest-api#rate-limiting"
                }
                """;

        wireMockServer.stubFor(get(urlPathEqualTo("/search/repositories"))
                .withQueryParam("q", WireMock.matching(".+"))
                .withQueryParam("sort", WireMock.equalTo("stars"))
                .withQueryParam("order", WireMock.equalTo("desc"))
                .withQueryParam("per_page", WireMock.equalTo("100"))
                .withQueryParam("page", WireMock.equalTo("1"))
                .willReturn(aResponse()
                        .withStatus(403)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("X-RateLimit-Limit", "60")
                        .withHeader("X-RateLimit-Remaining", "0")
                        .withHeader("X-RateLimit-Reset", "1735689600")
                        .withHeader("X-RateLimit-Used", "60")
                        .withHeader("Retry-After", "60")
                        .withBody(rateLimitErrorResponse)));

        // When: Making request to the controller endpoint
        var createdAfter = LocalDate.of(2024, 1, 1);
        var language = "Python";
        var page = 1;

        var url = String.format("http://localhost:%d/api/repositories/popular?createdAfter=%s&language=%s&page=%d",
                port, createdAfter, language, page);

        var response = restTemplate.getForEntity(url, ErrorResponseDto.class);

        // Then: Should return 429 TOO_MANY_REQUESTS
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode(), 
                "Status should be TOO_MANY_REQUESTS when GitHub returns 429");
        
        assertNotNull(response.getBody(), "Error response body should not be null");
        
        var errorResponse = response.getBody();
        assertNotNull(errorResponse, "Error response should not be null");
        assertEquals(429, errorResponse.status(), "Status code should be 429");
        assertEquals("Rate limit exceeded", errorResponse.error(), "Error type should match");
        assertNotNull(errorResponse.message(), "Error details should not be null");
        assertTrue(errorResponse.message().contains("rate limit"), 
                "Error message should mention rate limit");
        assertNotNull(errorResponse.timestamp(), "Timestamp should not be null");

        wireMockServer.verify(getRequestedFor(urlPathEqualTo("/search/repositories"))
                .withQueryParam("q", WireMock.matching(".+"))
                .withQueryParam("sort", WireMock.equalTo("stars"))
                .withQueryParam("order", WireMock.equalTo("desc"))
                .withQueryParam("per_page", WireMock.equalTo("100"))
                .withQueryParam("page", WireMock.equalTo("1")));
    }
}

