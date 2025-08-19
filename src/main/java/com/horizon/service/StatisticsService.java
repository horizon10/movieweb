package com.horizon.service;

import com.horizon.dto.StatisticsDTO;
import com.horizon.entity.Role;
import com.horizon.repository.UserRepository;
import com.horizon.repository.CommentRepository;
import com.horizon.repository.FavoriteRepository;
import com.horizon.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final FavoriteRepository favoriteRepository;
    private final ContactMessageRepository contactMessageRepository;

    public StatisticsDTO getAdminStatistics() {
        StatisticsDTO stats = new StatisticsDTO();

        // Genel istatistikler
        stats.setTotalUsers(userRepository.count());
        stats.setTotalComments(commentRepository.count());
        stats.setTotalFavorites(favoriteRepository.count());
        stats.setTotalContactMessages(contactMessageRepository.count());

        // Rol dağılımı
        stats.setAdminCount(userRepository.countByRole(Role.ROLE_ADMIN));
        stats.setModeratorCount(userRepository.countByRole(Role.ROLE_MODERATOR));
        stats.setUserCount(userRepository.countByRole(Role.ROLE_USER));

        // En çok yorum alan filmler (Top 5)
        Pageable top5 = PageRequest.of(0, 5);
        List<Object[]> commentStats = commentRepository.findMostCommentedMovies(top5);
        List<StatisticsDTO.MovieCommentStats> mostCommented = commentStats.stream()
                .map(row -> new StatisticsDTO.MovieCommentStats((String) row[0], (Long) row[1]))
                .collect(Collectors.toList());
        stats.setMostCommentedMovies(mostCommented);

        // En çok favorilenen filmler (Top 5)
        List<String> favMovies = favoriteRepository.findMostFavoritedMovies(5);
        List<StatisticsDTO.MovieFavoriteStats> mostFavorited = favMovies.stream()
                .map(imdbId -> {
                    Long count = favoriteRepository.countByImdbId(imdbId);
                    return new StatisticsDTO.MovieFavoriteStats(imdbId, count);
                })
                .collect(Collectors.toList());
        stats.setMostFavoritedMovies(mostFavorited);

        // Son kayıt olan kullanıcılar (Son 5)
        var recentUsers = userRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(user -> new StatisticsDTO.RecentUserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole().name(),
                        user.getCreatedAt() != null ? user.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) : ""
                ))
                .collect(Collectors.toList());
        stats.setRecentUsers(recentUsers);

        // Son yorumlar (Son 5)
        var recentComments = commentRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(comment -> new StatisticsDTO.RecentCommentDTO(
                        comment.getId(),
                        comment.getContent().length() > 50 ?
                                comment.getContent().substring(0, 50) + "..." :
                                comment.getContent(),
                        comment.getUser() != null ? comment.getUser().getUsername() : "Silinmiş Kullanıcı",
                        comment.getImdbId(),
                        comment.getCreatedAt() != null ? comment.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) : ""
                ))
                .collect(Collectors.toList());
        stats.setRecentComments(recentComments);

        // Aylık kullanıcı artışı (Son 6 ay)
        List<Object[]> monthlyStats = userRepository.findMonthlyUserGrowth();
        List<StatisticsDTO.MonthlyUserStats> monthlyGrowth = monthlyStats.stream()
                .map(row -> new StatisticsDTO.MonthlyUserStats((String) row[0], (Long) row[1]))
                .collect(Collectors.toList());
        stats.setMonthlyUserGrowth(monthlyGrowth);

        return stats;
    }
}