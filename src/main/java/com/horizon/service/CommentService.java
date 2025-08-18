package com.horizon.service;

import com.horizon.dto.CommentAdminDTO;
import com.horizon.dto.CommentDTO;
import com.horizon.dto.CommentLikeDTO;
import com.horizon.dto.CommentWithLikesDTO;
import com.horizon.entity.CommentLike;
import com.horizon.entity.User;
import com.horizon.entity.Comment;
import com.horizon.repository.CommentLikeRepository;
import com.horizon.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserService userService;

    public List<CommentDTO> getUserComments(User user) {
        return commentRepository.findByUser(user).stream()
                .map(c -> new CommentDTO(
                        c.getUser().getUsername(),
                        c.getContent(),
                        c.getCreatedAt(),
                        c.getImdbId(),
                        c.getId(),
                        c.getUser().getImage(),
                        c.getUser().getId()))
                .collect(Collectors.toList());
    }

    public void deleteComment(User user, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Eğer bu yorum bir yanıta sahipse, sadece içeriğini sil
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            comment.setContent("[DELETED]");
            comment.setDeleted(true);
            commentRepository.save(comment);
        } else {
            // Yanıtı yoksa tamamen sil
            commentRepository.delete(comment);
        }
    }

    public void addComment(User user, String imdbId, String content) {
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setImdbId(imdbId);
        comment.setContent(content);
        comment.setDeleted(false);
        commentRepository.save(comment);
    }

    public List<CommentDTO> getComments(String imdbId) {
        return commentRepository.findByImdbId(imdbId).stream()
                .map(c -> new CommentDTO(
                        c.getUser().getUsername(),
                        c.getContent(),
                        c.getCreatedAt(),
                        c.getImdbId(),
                        c.getId(),
                        c.getUser().getImage(),
                        c.getUser().getId()))
                .collect(Collectors.toList());
    }

    public List<CommentAdminDTO> getAllCommentsForAdmin() {
        return commentRepository.findAll().stream()
                .map(c -> new CommentAdminDTO(
                        c.getUser().getUsername(),
                        c.getContent(),
                        c.getCreatedAt(),
                        c.getImdbId(),
                        c.getId()
                ))
                .collect(Collectors.toList());
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    public void updateComment(User user, Long commentId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Yorumun kullanıcıya ait olduğunu kontrol et
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only update your own comments");
        }

        comment.setContent(newContent);
        commentRepository.save(comment);
    }

    public void likeComment(User user, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        commentLikeRepository.findByCommentIdAndUserId(commentId, user.getId())
                .ifPresentOrElse(like -> {
                    // Zaten beğenmiş -> unlike yap
                    commentLikeRepository.delete(like);
                }, () -> {
                    // Daha önce beğenmemiş -> like ekle
                    CommentLike newLike = new CommentLike();
                    newLike.setComment(comment);
                    newLike.setUser(user);
                    commentLikeRepository.save(newLike);
                });
    }

    public void unlikeComment(User user, Long commentId) {
        CommentLike like = commentLikeRepository.findByCommentIdAndUserId(commentId, user.getId())
                .orElseThrow(() -> new RuntimeException("Like not found"));
        commentLikeRepository.delete(like);
    }

    public List<CommentLikeDTO> getLikesForComment(Long commentId) {
        return commentLikeRepository.findByCommentId(commentId).stream()
                .map(like -> new CommentLikeDTO(
                        like.getId(),
                        like.getComment().getId(),
                        like.getUser().getId(),
                        like.getUser().getUsername(),
                        like.getLikedAt(),
                        like.getComment().getContent(),
                        like.getComment().getImdbId(),
                        like.getComment().getCreatedAt()))
                .collect(Collectors.toList());
    }

    public List<CommentWithLikesDTO> getCommentsWithLikes(String imdbId, User currentUser) {
        // Sadece ana yorumları (parent comment'i null olan) getir
        List<Comment> topLevelComments = commentRepository.findByImdbIdAndParentCommentIsNull(imdbId);

        return topLevelComments.stream()
                .map(comment -> convertToCommentWithLikesDTO(comment, currentUser))
                .collect(Collectors.toList());
    }

    private CommentWithLikesDTO convertToCommentWithLikesDTO(Comment comment, User currentUser) {
        int likeCount = commentLikeRepository.countByCommentId(comment.getId());
        boolean isLiked = currentUser != null &&
                commentLikeRepository.findByCommentIdAndUserId(comment.getId(), currentUser.getId()).isPresent();

        // Yanıtları da işle
        List<CommentWithLikesDTO> replies = comment.getReplies().stream()
                .map(reply -> convertToCommentWithLikesDTO(reply, currentUser))
                .collect(Collectors.toList());

        CommentWithLikesDTO dto = new CommentWithLikesDTO(
                comment.getUser().getUsername(),
                comment.getUser().getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getImdbId(),
                comment.getId(),
                comment.getUser().getImage(),
                likeCount,
                isLiked,
                isLiked ? getLikesForComment(comment.getId()) : null
        );

        dto.setReplies(replies);
        return dto;
    }

    public List<CommentLikeDTO> getUserLikes(User user) {
        return commentLikeRepository.findByUserId(user.getId()).stream()
                .map(like -> {
                    Comment comment = like.getComment();
                    return new CommentLikeDTO(
                            like.getId(),
                            comment.getId(),
                            like.getUser().getId(),
                            like.getUser().getUsername(),
                            like.getLikedAt(),
                            comment.getContent(),
                            comment.getImdbId(),
                            comment.getCreatedAt()
                    );
                })
                .collect(Collectors.toList());
    }

    public CommentDTO getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        return new CommentDTO(
                comment.getUser().getUsername(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getImdbId(),
                comment.getId(),
                comment.getUser().getImage(),
                comment.getUser().getId()
        );
    }

    public CommentDTO addReply(User user, Long parentCommentId, String content) {
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new RuntimeException("Parent comment not found"));

        Comment reply = new Comment();
        reply.setUser(user);
        reply.setImdbId(parentComment.getImdbId());
        reply.setContent(content);
        reply.setParentComment(parentComment);
        reply.setDeleted(false);

        commentRepository.save(reply);

        return convertToDTO(reply);
    }

    private CommentDTO convertToDTO(Comment comment) {
        return new CommentDTO(
                comment.getUser().getUsername(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getImdbId(),
                comment.getId(),
                comment.getUser().getImage(),
                comment.getUser().getId(),
                comment.getParentComment() != null ? comment.getParentComment().getId() : null
        );
    }

    public List<CommentDTO> getCommentsWithReplies(String imdbId) {
        List<Comment> topLevelComments = commentRepository.findByImdbIdAndParentCommentIsNull(imdbId);
        return topLevelComments.stream()
                .map(this::convertToDTOWithReplies)
                .collect(Collectors.toList());
    }

    private CommentDTO convertToDTOWithReplies(Comment comment) {
        CommentDTO dto = convertToDTO(comment);
        dto.setReplies(comment.getReplies().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        return dto;
    }
}