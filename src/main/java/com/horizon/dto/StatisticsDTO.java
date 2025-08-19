package com.horizon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsDTO {

    // Genel İstatistikler
    private Long totalUsers;
    private Long totalComments;
    private Long totalFavorites;
    private Long totalContactMessages;

    // Rol Dağılımı
    private Long adminCount;
    private Long moderatorCount;
    private Long userCount;

    // En Çok Yorum Alan Filmler (Film ID ve Yorum Sayısı)
    private List<MovieCommentStats> mostCommentedMovies;

    // En Çok Favorilenen Filmler
    private List<MovieFavoriteStats> mostFavoritedMovies;

    // Son Kayıt Olan Kullanıcılar
    private List<RecentUserDTO> recentUsers;

    // Son Yorumlar
    private List<RecentCommentDTO> recentComments;

    // Aylık Kullanıcı Artışı (Son 6 ay)
    private List<MonthlyUserStats> monthlyUserGrowth;

    // İstatistik Alt Sınıfları
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MovieCommentStats {
        private String imdbId;
        private Long commentCount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MovieFavoriteStats {
        private String imdbId;
        private Long favoriteCount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecentUserDTO {
        private Long id;
        private String username;
        private String email;
        private String role;
        private String createdAt;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecentCommentDTO {
        private Long id;
        private String content;
        private String username;
        private String imdbId;
        private String createdAt;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyUserStats {
        private String monthYear; // "2024-01" formatında
        private Long userCount;
    }
}