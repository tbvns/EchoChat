package xyz.tbvns.NsiWebsite.Database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    User findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByIdentifier(@Param("identifier") String identifier);

    Optional<User> findByToken(String token);

    @Modifying
    @Query("UPDATE User u SET u.token = null, u.tokenExpiration = null " +
            "WHERE u.tokenExpiration < :now")
    int clearExpiredTokens(@Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE User u SET u.token = :token, u.tokenExpiration = :expiration " +
            "WHERE u.id = :userId")
    int updateUserToken(@Param("userId") Long userId,
                        @Param("token") String token,
                        @Param("expiration") LocalDateTime expiration);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    long count();

    @Query("SELECT u FROM User u WHERE u.token = :token AND u.tokenExpiration > :now")
    Optional<User> findValidTokenUser(@Param("token") String token,
                                      @Param("now") LocalDateTime now);
}