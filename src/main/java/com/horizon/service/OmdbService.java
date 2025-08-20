package com.horizon.service;

import com.horizon.entity.OmdbResponse;
import com.horizon.entity.OmdbSearchResponse;
import com.horizon.entity.OmdbSearchItem;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public OmdbResponse searchMovieById(String imdbId) {
        String uri = UriComponentsBuilder
                .fromHttpUrl(apiUrl)
                .queryParam("apikey", apiKey)
                .queryParam("i", imdbId)
                .toUriString();

        return restTemplate.getForObject(uri, OmdbResponse.class);
    }

    // Arama listesi (s parametresi ile)
    public OmdbSearchResponse searchMovies(String searchTerm) {
        String uri = UriComponentsBuilder
                .fromHttpUrl(apiUrl)
                .queryParam("apikey", apiKey)
                .queryParam("s", searchTerm)
                .toUriString();

        return restTemplate.getForObject(uri, OmdbSearchResponse.class);
    }

    // Tür filtreli arama
    public OmdbSearchResponse searchMoviesByType(String searchTerm, String type) {
        String uri = UriComponentsBuilder
                .fromHttpUrl(apiUrl)
                .queryParam("apikey", apiKey)
                .queryParam("s", searchTerm)
                .queryParam("type", type)
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

    // Benzer filmleri öner
    public OmdbSearchResponse getSimilarMovies(String title, int count) {
        String searchTerm = extractKeywords(title);

        String uri = UriComponentsBuilder
                .fromHttpUrl(apiUrl)
                .queryParam("apikey", apiKey)
                .queryParam("s", searchTerm)
                .queryParam("page", 1)
                .toUriString();

        OmdbSearchResponse response = restTemplate.getForObject(uri, OmdbSearchResponse.class);

        if (response != null && response.getSearch() != null) {
            List<OmdbSearchItem> similarMovies = response.getSearch().stream()
                    .filter(movie -> !movie.getTitle().equalsIgnoreCase(title))
                    .limit(count)
                    .collect(Collectors.toList());

            response.setSearch(similarMovies);
            response.setTotalResults(String.valueOf(similarMovies.size()));
        }

        return response;
    }

    // Tür bazlı benzer film önerisi
    public OmdbSearchResponse getSimilarMoviesByGenre(String genre, int count) {
        String[] genres = genre.split(",");
        String primaryGenre = genres[0].trim();

        String uri = UriComponentsBuilder
                .fromHttpUrl(apiUrl)
                .queryParam("apikey", apiKey)
                .queryParam("s", primaryGenre)
                .queryParam("type", "movie")
                .queryParam("page", 1)
                .toUriString();

        OmdbSearchResponse response = restTemplate.getForObject(uri, OmdbSearchResponse.class);

        if (response != null && response.getSearch() != null) {
            List<OmdbSearchItem> similarMovies = response.getSearch().stream()
                    .limit(count)
                    .collect(Collectors.toList());

            response.setSearch(similarMovies);
            response.setTotalResults(String.valueOf(similarMovies.size()));
        }

        return response;
    }

    // Yönetmen bazlı benzer film önerisi
    public OmdbSearchResponse getSimilarMoviesByDirector(String director, int count) {
        String uri = UriComponentsBuilder
                .fromHttpUrl(apiUrl)
                .queryParam("apikey", apiKey)
                .queryParam("s", director)
                .queryParam("type", "movie")
                .queryParam("page", 1)
                .toUriString();

        OmdbSearchResponse response = restTemplate.getForObject(uri, OmdbSearchResponse.class);

        if (response != null && response.getSearch() != null) {
            List<OmdbSearchItem> similarMovies = response.getSearch().stream()
                    .limit(count)
                    .collect(Collectors.toList());

            response.setSearch(similarMovies);
            response.setTotalResults(String.valueOf(similarMovies.size()));
        }

        return response;
    }

    // Gelişmiş benzer film öneri sistemi
    public OmdbSearchResponse getAdvancedSimilarMovies(String imdbId, int count) {
        // Önce film detaylarını al
        OmdbResponse movie = searchMovieById(imdbId);
        if (movie == null) {
            return null;
        }

        OmdbSearchResponse similarMovies = null;

        // 1. Tür bazlı arama
        if (movie.getGenre() != null && !movie.getGenre().isEmpty()) {
            similarMovies = getSimilarMoviesByGenre(movie.getGenre(), count);
            if (similarMovies != null && similarMovies.getSearch() != null &&
                    similarMovies.getSearch().size() >= Math.min(3, count)) {
                return similarMovies;
            }
        }

        // 2. Yönetmen bazlı arama
        if (movie.getDirector() != null && !movie.getDirector().isEmpty()) {
            String director = movie.getDirector().split(",")[0].trim();
            similarMovies = getSimilarMoviesByDirector(director, count);
            if (similarMovies != null && similarMovies.getSearch() != null &&
                    similarMovies.getSearch().size() > 0) {
                return similarMovies;
            }
        }

        // 3. Anahtar kelime bazlı arama
        return getSimilarMovies(movie.getTitle(), count);
    }

    // Film adından anahtar kelimeler çıkarma
    private String extractKeywords(String title) {
        // Özel durumlar için ön işleme
        Map<String, String> specialCases = new HashMap<>();
        specialCases.put("The Lord of the Rings", "fantasy adventure");
        specialCases.put("Star Wars", "sci-fi space");
        specialCases.put("Harry Potter", "magic wizard");

        if (specialCases.containsKey(title)) {
            return specialCases.get(title);
        }

        // Roman rakamlarını sayılara çevir
        title = title.replaceAll("\\bII\\b", "2")
                .replaceAll("\\bIII\\b", "3")
                .replaceAll("\\bIV\\b", "4");

        // Yaygın kelimeleri kaldır
        String[] commonWords = {"the", "a", "an", "and", "or", "but", "in", "on", "at", "to",
                "for", "of", "with", "by", "movie", "film"};

        String cleanedTitle = title.toLowerCase();
        for (String word : commonWords) {
            cleanedTitle = cleanedTitle.replaceAll("\\b" + word + "\\b", "").trim();
        }

        // Sayıları ve özel karakterleri kaldır
        cleanedTitle = cleanedTitle.replaceAll("[0-9():]", "").trim();

        // Birden fazla boşluğu tek boşluğa indirge
        cleanedTitle = cleanedTitle.replaceAll("\\s+", " ");

        return cleanedTitle.isEmpty() ? title : cleanedTitle;
    }
}