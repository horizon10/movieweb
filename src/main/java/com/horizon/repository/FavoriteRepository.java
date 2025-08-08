package com.horizon.repository;

import com.horizon.entity.Favorite;
import com.horizon.entity.Comment;
import com.horizon.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    Optional<Favorite> findByUserIdAndImdbId(Long userId, String imdbId);
    boolean existsByUserIdAndImdbId(Long userId, String imdbId);
}