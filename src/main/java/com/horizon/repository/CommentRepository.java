package com.horizon.repository;

import com.horizon.entity.Comment;
import com.horizon.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByImdbId(String imdbId);
    List<Comment> findByUser(User user);
    List<Comment> findByImdbIdAndParentCommentIsNull(String imdbId);
    List<Comment> findByImdbIdOrderByCreatedAtDesc(String imdbId);

    // En son yapılan 5 yorum
    List<Comment> findTop5ByOrderByCreatedAtDesc();

    // En çok yorum alan filmleri getir (native query)
    @Query("SELECT c.imdbId, COUNT(c) as commentCount " +
            "FROM Comment c " +
            "GROUP BY c.imdbId " +
            "ORDER BY COUNT(c) DESC")
    List<Object[]> findMostCommentedMovies(org.springframework.data.domain.Pageable pageable);

}
