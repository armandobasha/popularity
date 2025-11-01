package com.redcare.popularity.service;

import com.redcare.popularity.dto.ScoredRepository;
import com.redcare.popularity.http.client.github.GithubHttpClient;
import com.redcare.popularity.http.client.github.GithubRepository;
import com.redcare.popularity.http.client.github.RepositorySearchRequestParams;
import com.redcare.popularity.http.client.github.SearchResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class RepositoryService {
    private final GithubHttpClient githubClient;
    private final ScoreCalculatorService scoreCalculatorService;

    public SearchResponse getRepos(LocalDate createdSince, String language) {
        var query = "created:>=" + "2025-10-31";
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
