package com.horizon.controller;

import com.horizon.entity.OmdbResponse;
import com.horizon.entity.OmdbSearchResponse;
import com.horizon.service.OmdbService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/omdb")
@RequiredArgsConstructor
public class OmdbController {
    private final OmdbService omdbService;

    // Tek film arama (tam başlık)
    @GetMapping("/movie")
    public ResponseEntity<OmdbResponse> searchByTitle(@RequestParam String title) {
        return ResponseEntity.ok(omdbService.searchMovieByTitle(title));
    }

    // ID ile arama
    @GetMapping("/searchId")
    public ResponseEntity<OmdbResponse> searchById(@RequestParam String imdbId) {
        return ResponseEntity.ok(omdbService.searchMovieById(imdbId));
    }

    // Arama listesi (eski search endpoint'ini güncelle)
    @GetMapping("/search")
    public ResponseEntity<OmdbSearchResponse> search(@RequestParam String title) {
        return ResponseEntity.ok(omdbService.searchMovies(title));
    }

    // Tür filtreli arama
    @GetMapping("/search/type")
    public ResponseEntity<OmdbSearchResponse> searchByType(
            @RequestParam String title,
            @RequestParam String type) {
        return ResponseEntity.ok(omdbService.searchMoviesByType(title, type));
    }

    // Yıl filtreli arama
    @GetMapping("/search/year")
    public ResponseEntity<OmdbSearchResponse> searchByYear(
            @RequestParam String title,
            @RequestParam String year) {
        return ResponseEntity.ok(omdbService.searchMoviesByYear(title, year));
    }

    // Sayfa bazlı arama
    @GetMapping("/search/page")
    public ResponseEntity<OmdbSearchResponse> searchWithPage(
            @RequestParam String title,
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(omdbService.searchMoviesWithPage(title, page));
    }

    // Gelişmiş arama
    @GetMapping("/search/advanced")
    public ResponseEntity<OmdbSearchResponse> advancedSearch(
            @RequestParam String title,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String year,
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(omdbService.advancedSearch(title, type, year, page));
    }

    // Benzer filmleri getir
    @GetMapping("/similar")
    public ResponseEntity<OmdbSearchResponse> getSimilarMovies(
            @RequestParam String title,
            @RequestParam(defaultValue = "4") int count) {
        return ResponseEntity.ok(omdbService.getSimilarMovies(title, count));
    }

    // Tür bazlı benzer filmler
    @GetMapping("/similar/genre")
    public ResponseEntity<OmdbSearchResponse> getSimilarMoviesByGenre(
            @RequestParam String genre,
            @RequestParam(defaultValue = "4") int count) {
        return ResponseEntity.ok(omdbService.getSimilarMoviesByGenre(genre, count));
    }

    // Gelişmiş benzer film önerisi
    @GetMapping("/similar/advanced")
    public ResponseEntity<OmdbSearchResponse> getAdvancedSimilarMovies(
            @RequestParam String imdbId,
            @RequestParam(defaultValue = "4") int count) {
        OmdbSearchResponse response = omdbService.getAdvancedSimilarMovies(imdbId, count);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}