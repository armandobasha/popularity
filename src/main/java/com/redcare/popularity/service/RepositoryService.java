package com.redcare.popularity.service;

import com.redcare.popularity.dto.ScoredRepositoryDto;
import com.redcare.popularity.http.client.github.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class RepositoryService {
    private final GithubHttpClient githubClient;
    private final ScoreCalculatorService scoreCalculatorService;

    public SearchResponse getRepos(LocalDate createdAfter, String language, int page) {
        var query = new SearchQueryBuilder()
                .addCreatedDate(createdAfter)
                .addLanguage(language)
                .build();

        log.info("Query: {} Language: {} Page: {}", query, language, page);

        var requestParameters = RepositorySearchRequestParams.builder()
                .q(query)
                .sort("stars")
                .order("desc")
                .per_page(100)
                .page(1)
                .build();
        return githubClient.search(requestParameters);
    }

    @Cacheable(value = "repositories", key = "#createdAfter.toString() + '|' + #language + '|' + #page")
    public List<ScoredRepositoryDto> getPopularScoredRepositories(LocalDate createdAfter, String language, int page) {
        return getRepos(createdAfter, language, page).items()
                .stream()
                .map(this::mapScoredRepository)
                .sorted(Comparator.comparingDouble(ScoredRepositoryDto::popularityScore).reversed())
                .toList();
    }

    public ScoredRepositoryDto mapScoredRepository(GithubRepositoryDto repository) {
        return ScoredRepositoryDto.builder()
                .name(repository.fullName())
                .popularityScore(scoreCalculatorService.score(repository.stargazersCount(), repository.forksCount(), repository.pushedAt()))
                .language(repository.language())
                .createdAt(repository.createdAt().toString())
                .build();
    }
}
