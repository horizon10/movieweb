package com.horizon.repository;

import com.horizon.entity.Role;
import com.horizon.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    // Username dışında email ile de kullanıcı bulabilmek için
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Long countByRole(Role role);

    List<User> findTop5ByOrderByCreatedAtDesc();

    @Query(value = "SELECT TO_CHAR(created_at, 'YYYY-MM') as month_year, COUNT(*) as user_count " +
            "FROM users " +
            "WHERE created_at >= NOW() - INTERVAL '6 months' " +
            "GROUP BY TO_CHAR(created_at, 'YYYY-MM') " +
            "ORDER BY month_year DESC", nativeQuery = true)
    List<Object[]> findMonthlyUserGrowth();

}


