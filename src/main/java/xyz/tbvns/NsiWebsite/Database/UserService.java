package xyz.tbvns.NsiWebsite.Database;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.tbvns.NsiWebsite.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private static final int TOKEN_VALIDITY_HOURS = 24;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void createUser(String username, String email, String password) {
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already exists");
        }

        String salt = RandomStringUtils.randomAlphanumeric(16);
        String passwordHash = StringUtils.hashPassword(salt + password);
        
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setSalt(salt);
        user.setPasswordHash(passwordHash);
        userRepository.save(user);
    }

    @Transactional
    public String verifyUser(String identifier, String password) {
        User user = userRepository.findByIdentifier(identifier).get();
        if (user != null && StringUtils.verifyPassword(user.getSalt() + password, user.getPasswordHash())) {
            String token = generateSecureToken();
            user.setToken(token);
            user.setTokenExpiration(LocalDateTime.now().plusHours(TOKEN_VALIDITY_HOURS));
            userRepository.save(user);
            return token;
        }
        return null;
    }

    @Transactional
    public void logout(String token) {
        User user = userRepository.findByToken(token).get();
        if (user != null) {
            user.setToken(null);
            user.setTokenExpiration(null);
            userRepository.save(user);
        }
    }

    public User validateToken(String token) {
        if (token == null) return null;

        Optional<User> userOptional = userRepository.findByToken(token);
        if (!userOptional.isPresent()) return null;
        User user = userOptional.get();

        if (user.getTokenExpiration() == null ||
                user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            return null;
        }

        // Optional: Auto-extend token validity on validation
        user.setTokenExpiration(LocalDateTime.now().plusHours(TOKEN_VALIDITY_HOURS));
        userRepository.save(user);

        return user;
    }

    private String generateSecureToken() {
        return UUID.randomUUID().toString() + "-" +
                RandomStringUtils.randomAlphanumeric(16);
    }

    @Transactional
    public void cleanupExpiredTokens() {
        userRepository.clearExpiredTokens(LocalDateTime.now());
    }
}