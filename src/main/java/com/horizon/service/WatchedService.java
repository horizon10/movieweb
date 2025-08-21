package com.horizon.service;

import com.horizon.entity.Favorite;
import com.horizon.entity.User;
import com.horizon.entity.Watched;
import com.horizon.repository.FavoriteRepository;
import com.horizon.repository.WatchedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WatchedService {
    private final WatchedRepository watchedRepository;
    public boolean isMovieWatched(User user, String imdbId) {
        return watchedRepository.existsByUserIdAndImdbId(user.getId(), imdbId);
    }

    public List<String> getMostWatchedMovies(int limit) {
        return watchedRepository.findMostWatchedMovies(limit);
    }
    public Watched addWatched(User user, String imdbId) {
        if (isMovieWatched(user, imdbId)) {
            throw new IllegalStateException("Film zaten izlenenlerinizde");
        }

        Watched watched = new Watched();
        watched.setUser(user);
        watched.setImdbId(imdbId);
        return watchedRepository.save(watched);
    }

    public void removeWatched(User user, String imdbId) {
        watchedRepository.findByUserIdAndImdbId(user.getId(), imdbId)
                .ifPresent(watchedRepository::delete);
    }

    public List<Watched> getWatched(User user) {
        return watchedRepository.findByUserId(user.getId());
    }
}