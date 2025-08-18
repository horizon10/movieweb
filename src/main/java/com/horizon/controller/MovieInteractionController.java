package com.horizon.controller;


import com.horizon.dto.*;
import com.horizon.entity.ContactMessage;
import com.horizon.entity.Favorite;
import com.horizon.entity.User;
import com.horizon.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/home/")
@RequiredArgsConstructor
public class MovieInteractionController {

    private final UserService userService;
    private final FavoriteService favoriteService;
    private final CommentService commentService;
    private final RatingService ratingService;
    private final ContactMessageService contactMessageService;


    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Giriş yapan kullanıcının username'i
        System.out.println("Giriş yapan kullanıcı: " + username);
        return userService.getByUsername(username);
    }


    // FAVORITE

    @PostMapping("/favorite/{imdbId}")
    public ResponseEntity<Void> favoriteMovie(@PathVariable String imdbId) {
        favoriteService.addFavorite(getCurrentUser(), imdbId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/favorite/{imdbId}")
    public ResponseEntity<Void> unfavoriteMovie(@PathVariable String imdbId) {
        favoriteService.removeFavorite(getCurrentUser(), imdbId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/favorite")
    public ResponseEntity<List<FavoriteDTO>> getFavorites() {
        List<Favorite> favorites = favoriteService.getFavorites(getCurrentUser());

        List<FavoriteDTO> dtos = favorites.stream()
                .map(fav -> new FavoriteDTO(fav.getId(), fav.getImdbId()))
                .toList();

        return ResponseEntity.ok(dtos);
    }
    @GetMapping("/favorite/most-favorited")
    public ResponseEntity<List<String>> getMostFavoritedMovies() {
        return ResponseEntity.ok(favoriteService.getMostFavoritedMovies(10));
    }

    // COMMENT

    @PostMapping("/comment/{imdbId}")
    public ResponseEntity<Void> comment(@PathVariable String imdbId, @RequestBody String content) {
        commentService.addComment(getCurrentUser(), imdbId, content);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/comment/{imdbId}")
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable String imdbId) {
        return ResponseEntity.ok(commentService.getComments(imdbId));
    }


    @GetMapping("/comment/user")
    public ResponseEntity<List<CommentDTO>> getCommentsByUser() {
        return ResponseEntity.ok(commentService.getUserComments(getCurrentUser()));
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(getCurrentUser(),commentId);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/comment/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long commentId,
            @RequestBody String newContent) {
        commentService.updateComment(getCurrentUser(), commentId, newContent);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comment/{parentCommentId}/reply")
    public ResponseEntity<CommentDTO> addReply(
            @PathVariable Long parentCommentId,
            @RequestBody String content) {
        CommentDTO reply = commentService.addReply(getCurrentUser(), parentCommentId, content);
        return ResponseEntity.ok(reply);
    }

    @GetMapping("/comment/{imdbId}/with-replies")
    public ResponseEntity<List<CommentDTO>> getCommentsWithReplies(@PathVariable String imdbId) {
        return ResponseEntity.ok(commentService.getCommentsWithReplies(imdbId));
    }


    // RATING

    @PostMapping("/rate/{imdbId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> rate(@PathVariable String imdbId, @RequestBody Integer score) {
        try {
            if (score < 1 || score > 5) {
                return ResponseEntity.badRequest().body("Puan 1-5 arasında olmalıdır");
            }

            User user = getCurrentUser();
            ratingService.rateMovie(user, imdbId, score);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Puanlama yapılırken hata oluştu");
        }
    }

    @GetMapping("/rate/{imdbId}")
    public ResponseEntity<List<RatingDTO>> getRatings(@PathVariable String imdbId) {
        return ResponseEntity.ok(ratingService.getRatings(imdbId));
    }

    @GetMapping("/rate/{imdbId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable String imdbId) {
        Double average = ratingService.getAverageRating(imdbId);
        return ResponseEntity.ok(average != null ? average : 0.0);
    }

    @GetMapping("/rate/user")
    public ResponseEntity<List<RatingDTO>> getUserRatings() {
        return ResponseEntity.ok(ratingService.getUserRatings(getCurrentUser()));
    }

    @DeleteMapping("/rate/{ratingId}")
    public ResponseEntity<String> deleteRating(@PathVariable Long ratingId) {
        ratingService.deleteRating(getCurrentUser(),ratingId);
        return ResponseEntity.ok("Rating başarıyla silindi");
    }


    @PostMapping("/contact")
    public ResponseEntity<String> sendMessage(@RequestBody ContactMessage message) {
        contactMessageService.saveMessage(message);
        return ResponseEntity.ok("Mesajınız başarıyla gönderildi!");
    }

    // Yorum beğenme endpointleri
    @PostMapping("/comment/{commentId}/like")
    public ResponseEntity<Void> likeComment(@PathVariable Long commentId) {
        commentService.likeComment(getCurrentUser(), commentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comment/{commentId}/like")
    public ResponseEntity<Void> unlikeComment(@PathVariable Long commentId) {
        commentService.unlikeComment(getCurrentUser(), commentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/comment/{commentId}/likes")
    public ResponseEntity<List<CommentLikeDTO>> getCommentLikes(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getLikesForComment(commentId));
    }

    @GetMapping("/comment/{imdbId}/with-likes")
    public ResponseEntity<List<CommentWithLikesDTO>> getCommentsWithLikes(@PathVariable String imdbId) {
        User currentUser = getCurrentUserSafely();
        return ResponseEntity.ok(commentService.getCommentsWithLikes(imdbId, currentUser));
    }

    @GetMapping("/comment/likes/user")
    public ResponseEntity<List<CommentLikeDTO>> getUserLikes() {
        User currentUser = getCurrentUser();
        List<CommentLikeDTO> userLikes = commentService.getUserLikes(currentUser);
        return ResponseEntity.ok(userLikes);
    }
    @GetMapping("/comment/{commentId}/like-details")
    public ResponseEntity<CommentDTO> getCommentDetails(@PathVariable Long commentId) {
        CommentDTO comment = commentService.getCommentById(commentId);
        return ResponseEntity.ok(comment);
    }


    @GetMapping("/comment/{imdbId}/with-likes-and-replies")
    public ResponseEntity<List<CommentWithLikesDTO>> getCommentsWithLikesAndReplies(@PathVariable String imdbId) {
        User currentUser = getCurrentUserSafely();
        return ResponseEntity.ok(commentService.getCommentsWithLikes(imdbId, currentUser));
    }

    private User getCurrentUserSafely() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() ||
                    "anonymousUser".equals(authentication.getName())) {
                return null;
            }
            String username = authentication.getName();
            return userService.getByUsername(username);
        } catch (Exception e) {
            return null;
        }
    }
}
