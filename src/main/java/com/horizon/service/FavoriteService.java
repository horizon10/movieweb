package com.horizon.service;

import com.horizon.entity.User;
import com.horizon.entity.Favorite;
import com.horizon.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    public boolean isMovieFavorite(User user, String imdbId) {
        return favoriteRepository.existsByUserIdAndImdbId(user.getId(), imdbId);
    }

    public List<String> getMostFavoritedMovies(int limit) {
        return favoriteRepository.findMostFavoritedMovies(limit);
    }
    public Favorite addFavorite(User user, String imdbId) {
        if (isMovieFavorite(user, imdbId)) {
            throw new IllegalStateException("Film zaten favorilerinizde");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setImdbId(imdbId);
        return favoriteRepository.save(favorite);
    }

    public void removeFavorite(User user, String imdbId) {
        favoriteRepository.findByUserIdAndImdbId(user.getId(), imdbId)
                .ifPresent(favoriteRepository::delete);
    }

    public List<Favorite> getFavorites(User user) {
        return favoriteRepository.findByUserId(user.getId());
    }
}
