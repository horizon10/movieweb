package com.horizon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentLikeDTO {
    private Long id;
    private Long commentId;
    private Long userId;
    private String username;
    private LocalDateTime likedAt;

    // Ek alanlar - yorumun detayları için
    private String content;
    private String imdbId;
    private LocalDateTime createdAt;

    // Eski constructor'ı koruyalım - backward compatibility için
    public CommentLikeDTO(Long id, Long commentId, Long userId, String username, LocalDateTime likedAt) {
        this.id = id;
        this.commentId = commentId;
        this.userId = userId;
        this.username = username;
        this.likedAt = likedAt;
    }
}