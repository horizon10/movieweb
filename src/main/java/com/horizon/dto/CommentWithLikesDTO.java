package com.horizon.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentWithLikesDTO {
    private String username;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;
    private String userImage;
    private String imdbId;
    private Long id;
    private int likeCount;
    private boolean isLikedByCurrentUser;
    private List<CommentLikeDTO> likes;

    public CommentWithLikesDTO(String username, Long userId, String content, LocalDateTime createdAt, String imdbId, Long id, String image, int likeCount, boolean isLiked, List<CommentLikeDTO> commentLikeDTOS) {
        this.username = username;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
        this.imdbId = imdbId;
        this.id = id;
        this.likeCount = likeCount;
        this.userImage = image;
        this.isLikedByCurrentUser = isLiked;
        this.likes = commentLikeDTOS;
    }
}