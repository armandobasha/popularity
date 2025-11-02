package com.redcare.popularity.controller;

import com.redcare.popularity.dto.ErrorResponseDto;
import com.redcare.popularity.dto.ScoredRepositoryDto;
import com.redcare.popularity.service.RepositoryService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/repositories")
@AllArgsConstructor
@Slf4j
@Tag(name = "Repositories", description = "API for retrieving popular GitHub repositories")
public class RepositoryController {
    private final RepositoryService repositoryService;

    @Timed(value = "controller.repositories.get")
    @Operation(
            summary = "Get popular repositories",
            description = "Retrieves a list of popular GitHub repositories filtered by creation date and programming language, sorted by popularity score"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved popular repositories",
                    content = @Content(schema = @Schema(implementation = ScoredRepositoryDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Rate limit exceeded - GitHub API rate limit has been exceeded. Please try again later.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
            )
    })
    @GetMapping("/popular")
    public ResponseEntity<List<ScoredRepositoryDto>> popularRepositories(
            @Parameter(
                    description = "Filter repositories created after this date (ISO format: YYYY-MM-DD)",
                    required = true,
                    example = "2024-01-01"
            )
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate createdAfter,
            @Parameter(
                    description = "Programming language to filter by",
                    example = "Java"
            )
            @RequestParam(value = "language", required = false, defaultValue = "") String language,
            @Parameter(
                    description = "Page number for pagination (default: 1)",
                    example = "1"
            )
            @RequestParam(value = "page", required = false, defaultValue = "1") int page
    ) {
        var result = repositoryService.getPopularScoredRepositories(createdAfter, language, page);
        log.info("Results: {}", result.size());
        return ResponseEntity.ok(result);
    }
}
