package com.horizon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// RatingDTO.java
import lombok.Data;

// RatingDTO.java
import lombok.Data;

@Data
public class RatingDTO {
    private String username;
    private int score;
    private String imdbId;
    private Long id;

    // Temel constructor
    public RatingDTO(String username, int score) {
        this.username = username;
        this.score = score;
    }

    // Tüm alanları kabul eden constructor
    public RatingDTO(String username, int score, String imdbId, Long id) {
        this.username = username;
        this.score = score;
        this.imdbId = imdbId;
        this.id = id;
    }
}
