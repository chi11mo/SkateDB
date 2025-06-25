package com.chillmo.skatedb.user.repository;

import com.chillmo.skatedb.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameOrEmail(String username, String email);

    // Optimized query for login with single parameter
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByIdentifier(@Param("identifier") String identifier);

    // For better performance with frequent queries
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username OR u.email = :email")
    boolean existsByUsernameOrEmail(@Param("username") String username, @Param("email") String email);
}