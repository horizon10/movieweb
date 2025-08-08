package com.horizon.service;

import com.horizon.entity.OmdbResponse;
import com.horizon.entity.OmdbSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class OmdbService {

    @Value("${omdb.api.url}")
    private String apiUrl;

    @Value("${omdb.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // Tek film arama (tam başlık)
    public OmdbResponse searchMovieByTitle(String title) {
        String uri = UriComponentsBuilder
                .fromHttpUrl(apiUrl)
                .queryParam("apikey", apiKey)
                .queryParam("t", title)
                .toUriString();

        return restTemplate.getForObject(uri, OmdbResponse.class);
    }

    // ID ile arama
    public OmdbResponse searchMovieById(String omdbId) {
        String uri = UriComponentsBuilder
                .fromHttpUrl(apiUrl)
                .queryParam("apikey", apiKey)
                .queryParam("i", omdbId)
                .toUriString();

        return restTemplate.getForObject(uri, OmdbResponse.class);
    }

    // Arama listesi (s parametresi ile)
    public OmdbSearchResponse searchMovies(String searchTerm) {
        String uri = UriComponentsBuilder
                .fromHttpUrl(apiUrl)
                .queryParam("apikey", apiKey)
                .queryParam("s", searchTerm) // "s" parametresi liste döner
                .toUriString();

        return restTemplate.getForObject(uri, OmdbSearchResponse.class);
    }

    // Tür filtreli arama
    public OmdbSearchResponse searchMoviesByType(String searchTerm, String type) {
        String uri = UriComponentsBuilder
                .fromHttpUrl(apiUrl)
                .queryParam("apikey", apiKey)
                .queryParam("s", searchTerm)
                .queryParam("type", type) // movie, series, episode
                .toUriString();

        return restTemplate.getForObject(uri, OmdbSearchResponse.class);
    }

    // Yıl filtreli arama
    public OmdbSearchResponse searchMoviesByYear(String searchTerm, String year) {
        String uri = UriComponentsBuilder
                .fromHttpUrl(apiUrl)
                .queryParam("apikey", apiKey)
                .queryParam("s", searchTerm)
                .queryParam("y", year)
                .toUriString();

        return restTemplate.getForObject(uri, OmdbSearchResponse.class);
    }

    // Sayfa bazlı arama (pagination)
    public OmdbSearchResponse searchMoviesWithPage(String searchTerm, int page) {
        String uri = UriComponentsBuilder
                .fromHttpUrl(apiUrl)
                .queryParam("apikey", apiKey)
                .queryParam("s", searchTerm)
                .queryParam("page", page)
                .toUriString();

        return restTemplate.getForObject(uri, OmdbSearchResponse.class);
    }

    // Gelişmiş arama (tüm parametrelerle)
    public OmdbSearchResponse advancedSearch(String searchTerm, String type, String year, int page) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(apiUrl)
                .queryParam("apikey", apiKey)
                .queryParam("s", searchTerm);

        if (type != null && !type.isEmpty()) {
            builder.queryParam("type", type);
        }
        if (year != null && !year.isEmpty()) {
            builder.queryParam("y", year);
        }
        if (page > 0) {
            builder.queryParam("page", page);
        }

        String uri = builder.toUriString();
        return restTemplate.getForObject(uri, OmdbSearchResponse.class);
    }
}