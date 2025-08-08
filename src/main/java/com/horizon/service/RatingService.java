package com.horizon.service;

import com.horizon.dto.RatingDTO;
import com.horizon.entity.User;
import com.horizon.entity.Rating;
import com.horizon.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;


    public List<RatingDTO> getUserRatings(User user) {
        return ratingRepository.findByUser(user).stream()
                .map(r-> new RatingDTO(
                        r.getUser().getUsername(),
                        r.getScore(),
                        r.getImdbId(),
                        r.getId()
                )).collect(Collectors.toList());
    }

    public void deleteRating(User user,Long ratingId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(()->new RuntimeException("Rating not found"));
        ratingRepository.delete(rating);
    }

    public void rateMovie(User user, String imdbId, int score) {
        Optional<Rating> existing = ratingRepository.findByUserIdAndImdbId(user.getId(), imdbId);
        if (existing.isPresent()) {
            Rating rating = existing.get();
            rating.setScore(score);
            ratingRepository.save(rating);
        } else {
            Rating rating = new Rating();
            rating.setUser(user);
            rating.setImdbId(imdbId);
            rating.setScore(score);
            ratingRepository.save(rating);
        }
    }

    public List<RatingDTO> getRatings(String imdbId) {
        return ratingRepository.findByImdbId(imdbId).stream()
                .map(r -> new RatingDTO(r.getUser().getUsername(), r.getScore()))
                .collect(Collectors.toList());
    }
    public Double getAverageRating(String imdbId) {
        List<Rating> ratings = ratingRepository.findByImdbId(imdbId);
        if (ratings.isEmpty()) return 0.0;
        return ratings.stream().mapToInt(Rating::getScore).average().orElse(0.0);
    }

    public Integer getUserRating(User user, String imdbId) {
        Optional<Rating> rating = ratingRepository.findByUserAndImdbId(user, imdbId);
        return rating.map(Rating::getScore).orElse(null);
    }

}
