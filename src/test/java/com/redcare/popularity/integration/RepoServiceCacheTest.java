package com.redcare.popularity.integration;

import com.redcare.popularity.http.client.github.GithubHttpClient;
import com.redcare.popularity.http.client.github.SearchResponse;
import com.redcare.popularity.service.RepositoryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
class RepoServiceCacheTest {
  @Autowired
  RepositoryService service;
  @MockitoBean
  GithubHttpClient githubClient;

  @Test
  void cachesByDateAndLanguage() {
    var date = LocalDate.parse("2025-11-01");
    var resp = new SearchResponse(100, true, List.of());

    Mockito.when(githubClient.search(Mockito.any())).thenReturn(resp);

    service.getPopularScoredRepositories(date, "java", 1);
    service.getPopularScoredRepositories(date, "java", 1); // should be a cache HIT

    Mockito.verify(githubClient, Mockito.times(1)).search(Mockito.any());
  }
}