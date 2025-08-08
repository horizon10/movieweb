package com.horizon.controller;


import com.horizon.dto.CommentDTO;
import com.horizon.dto.FavoriteDTO;
import com.horizon.dto.RatingDTO;
import com.horizon.entity.Favorite;
import com.horizon.entity.User;
import com.horizon.service.CommentService;
import com.horizon.service.FavoriteService;
import com.horizon.service.RatingService;
import com.horizon.service.UserService;
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
}
