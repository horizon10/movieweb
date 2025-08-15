package com.horizon.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentDTO {
    private String username;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;
    private String userImage;
    private String imdbId;
    private Long id;
    private Long parentCommentId; // Yanıtlanan yorumun ID'si
    private List<CommentDTO> replies; // Yanıtlar listesi

    public CommentDTO(String username, String content, LocalDateTime createdAt,
                      String imdbId, Long id, String userImage, Long userId) {
        this(username, content, createdAt, imdbId, id, userImage, userId, null);
    }

    public CommentDTO(String username, String content, LocalDateTime createdAt,
                      String imdbId, Long id, String userImage, Long userId, Long parentCommentId) {
        this.username = username;
        this.content = content;
        this.createdAt = createdAt;
        this.imdbId = imdbId;
        this.id = id;
        this.userImage = userImage;
        this.userId = userId;
        this.parentCommentId = parentCommentId;
    }
}