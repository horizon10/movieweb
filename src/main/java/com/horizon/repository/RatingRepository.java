package com.horizon.repository;

import com.horizon.entity.Rating;
import com.horizon.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByUserIdAndImdbId(Long userId, String imdbId);
    List<Rating> findByImdbId(String imdbId);
    Optional<Rating> findByUserAndImdbId(User user, String imdbId);
    List<Rating> findByUser(User user);
}