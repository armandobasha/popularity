package com.redcare.popularity.service;

import com.redcare.popularity.dto.ScoredRepository;
import com.redcare.popularity.http.client.github.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class RepositoryService {
    private final GithubHttpClient githubClient;
    private final ScoreCalculatorService scoreCalculatorService;

    @Cacheable(value = "repositories")
    public SearchResponse getRepos(LocalDate createdSince, String language) {
        var query = new SearchQueryBuilder()
                .addCreatedDate(createdSince)
                .addLanguage(language)
                .build();

        log.info("Query: {}", query);

        var requestParameters = new RepositorySearchRequestParams(query, "stars", "desc", 100 ,1);
        return githubClient.search(requestParameters);
    }

    public List<ScoredRepository> getPopularScoredRepositories(LocalDate createdSince, String language) {
        return getRepos(createdSince, language).items().stream().map(this::mapScoredRepository).toList();
    }

    public ScoredRepository mapScoredRepository(GithubRepository repository) {
        return ScoredRepository.builder()
                .name(repository.fullName())
                .popularityScore(scoreCalculatorService.score(repository.stargazersCount(), repository.forksCount(), repository.pushedAt()))
                .language(repository.language())
                .createdAt(repository.createdAt().toString())
                .build();
    }
}
