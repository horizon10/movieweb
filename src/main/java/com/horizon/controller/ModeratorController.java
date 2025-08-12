package com.horizon.controller;

import com.horizon.dto.CommentAdminDTO;
import com.horizon.dto.CommentDTO;
import com.horizon.entity.Comment;
import com.horizon.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moderator")
@PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
@RequiredArgsConstructor
public class ModeratorController {

    private final CommentService commentService;

    @GetMapping("/comments")
    public ResponseEntity<List<CommentAdminDTO>> getAllComments() {
        List<CommentAdminDTO> comments = commentService.getAllCommentsForAdmin();
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
