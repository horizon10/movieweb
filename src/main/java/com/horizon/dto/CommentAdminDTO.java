package com.horizon.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentAdminDTO {
    private String username;
    private String userId;
    private String content;
    private LocalDateTime createdAt;
    private String imdbId;
    private Long id;

    public CommentAdminDTO(String username, String content, LocalDateTime createdAt,
                      String imdbId, Long id) {
        this.username = username;
        this.content = content;
        this.createdAt = createdAt;
        this.imdbId = imdbId;
        this.id = id;
    }
}
