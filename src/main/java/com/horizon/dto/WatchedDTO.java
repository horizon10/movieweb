package com.horizon.dto;

import lombok.Data;

@Data
public class WatchedDTO {
    private Long id;
    private String imdbId;

    public WatchedDTO(Long id, String imdbId) {
        this.id = id;
        this.imdbId = imdbId;
    }
}
