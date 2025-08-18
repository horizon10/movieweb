package com.horizon.repository;

import com.horizon.entity.Favorite;
import com.horizon.entity.Comment;
import com.horizon.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    Optional<Favorite> findByUserIdAndImdbId(Long userId, String imdbId);
    boolean existsByUserIdAndImdbId(Long userId, String imdbId);
    @Query("SELECT f.imdbId FROM Favorite f GROUP BY f.imdbId ORDER BY COUNT(f.id) DESC")
    List<String> findMostFavoritedMovies(@Param("limit") int limit);
}