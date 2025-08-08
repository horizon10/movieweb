package com.horizon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private String username;
    private String userId;
    private String content;
    private LocalDateTime createdAt;
    private String userImage;
    private String imdbId;
    private Long id;


    // Tüm alanları içeren constructor
    public CommentDTO(String username, String content, LocalDateTime createdAt, String imdbId, Long id, String userImage) {
        this.username = username;
        this.content = content;
        this.createdAt = createdAt;
        this.imdbId = imdbId;
        this.userImage = userImage;
        this.id = id;
    }
}

