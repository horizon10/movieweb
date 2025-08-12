package com.horizon.controller;

import com.horizon.dto.CommentAdminDTO;
import com.horizon.dto.CommentDTO;
import com.horizon.dto.RoleUpdateRequest;
import com.horizon.dto.UserDto;
import com.horizon.entity.Comment;
import com.horizon.entity.Role;
import com.horizon.entity.User;
import com.horizon.repository.CommentRepository;
import com.horizon.service.CommentService;
import com.horizon.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final CommentService commentService;
    private final CommentRepository commentRepository;

    @GetMapping("/users")
    public ResponseEntity<UserDto> getAllUsers() {
        List<UserDto> users=userService.getAll();
        return ResponseEntity.ok().body(users.get(0));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/users/{id}/role")
    public User updateUserRole(@PathVariable Long id, @RequestBody RoleUpdateRequest request) {
        return userService.updateRole(id, request.getRole());
    }

    @GetMapping("/comments")
    public ResponseEntity<List<CommentAdminDTO>> getAllComments() {
        List<CommentAdminDTO> comments = commentService.getAllCommentsForAdmin();
        return ResponseEntity.ok(comments);
    }


    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok().build();
    }
}
