package com.redcare.popularity.controller;

import com.redcare.popularity.dto.ScoredRepository;
import com.redcare.popularity.service.RepositoryService;
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
public class RepositoryController {
    private final RepositoryService repositoryService;

    @GetMapping("/popular")
    public ResponseEntity<List<ScoredRepository>> popularRepositories(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdAfter,
            @RequestParam String language
    ) {
        var result = repositoryService.getPopularScoredRepositories(createdAfter, language);
        log.info("Score: {}", result.size());
        return ResponseEntity.ok(repositoryService.getPopularScoredRepositories(createdAfter, language));
    }
}
