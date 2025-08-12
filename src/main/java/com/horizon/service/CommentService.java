package com.horizon.service;

import com.horizon.dto.CommentAdminDTO;
import com.horizon.dto.CommentDTO;
import com.horizon.entity.User;
import com.horizon.entity.Comment;
import com.horizon.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public List<CommentDTO> getUserComments(User user) {
        return commentRepository.findByUser(user).stream()
                .map(c -> new CommentDTO(
                        c.getUser().getUsername(),
                        c.getContent(),
                        c.getCreatedAt(),
                        c.getImdbId(),
                        c.getId(),
                        c.getUser().getImage()))
                .collect(Collectors.toList());
    }

public void deleteComment(User user, Long commentId) {
    Comment comment=commentRepository.findById(commentId)
            .orElseThrow(()->new RuntimeException("Comment not found"));

    commentRepository.delete(comment);
}

    public void addComment(User user, String imdbId, String content) {
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setImdbId(imdbId);
        comment.setContent(content);
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
                        c.getUser().getImage()))
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

}
