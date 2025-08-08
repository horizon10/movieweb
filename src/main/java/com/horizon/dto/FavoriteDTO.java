package com.horizon.dto;

import lombok.Data;

@Data
public class FavoriteDTO {
    private Long id;
    private String imdbId;

    public FavoriteDTO(Long id, String imdbId) {
        this.id = id;
        this.imdbId = imdbId;
    }
}