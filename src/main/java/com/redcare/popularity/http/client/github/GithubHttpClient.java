package com.redcare.popularity.http.client.github;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        value = "github",
        url = "${http.github.api.url}"
)
public interface GithubHttpClient {
    @GetMapping(
            value = "/search/repositories",
            consumes = "application/vnd.github+json"
    )
    SearchResponse search(
            @SpringQueryMap RepositorySearchRequestParams params
    );
}
