package com.horizon.repository;

import com.horizon.entity.Watched;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WatchedRepository extends JpaRepository<Watched, Long> {
    List<Watched> findByUserId(Long userId);
    Optional<Watched> findByUserIdAndImdbId(Long userId, String imdbId);
    boolean existsByUserIdAndImdbId(Long userId, String imdbId);
    @Query("SELECT f.imdbId FROM Favorite f GROUP BY f.imdbId ORDER BY COUNT(f.id) DESC")
    List<String> findMostWatchedMovies(@Param("limit") int limit);
    Long countByImdbId(String imdbId);
}
