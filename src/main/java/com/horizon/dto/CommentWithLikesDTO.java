package com.horizon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentWithLikesDTO {
    private String username;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;
    private String imdbId;
    private Long id;
    private String userImage;
    private int likeCount;
    private boolean isLikedByCurrentUser;
    private List<CommentLikeDTO> likes;
    private List<CommentWithLikesDTO> replies; // Yanıtlar için eklendi

    // Ana constructor - replies olmadan
    public CommentWithLikesDTO(String username, Long userId, String content,
                               LocalDateTime createdAt, String imdbId, Long id,
                               String userImage, int likeCount,
                               boolean isLikedByCurrentUser, List<CommentLikeDTO> likes) {
        this.username = username;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
        this.imdbId = imdbId;
        this.id = id;
        this.userImage = userImage;
        this.likeCount = likeCount;
        this.isLikedByCurrentUser = isLikedByCurrentUser;
        this.likes = likes;
    }
}