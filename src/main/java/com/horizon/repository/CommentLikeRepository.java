package com.horizon.repository;

import com.horizon.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    List<CommentLike> findByCommentId(Long commentId);
    Optional<CommentLike> findByCommentIdAndUserId(Long commentId, Long userId);
    int countByCommentId(Long commentId);
    List<CommentLike> findByUserId(Long userId);
}