package net.salim.api_motus.service;


import net.salim.api_motus.JwtUtil.JwtUtil;
import net.salim.api_motus.model.User;
import net.salim.api_motus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    // Authentifier un utilisateur et générer un token JWT
    public AuthResponse authenticate(String username) {
        try {
            User user = userDetailsService.getUserByUsername(username);
            String token = jwtUtil.generateToken(username);

            return AuthResponse.builder()
                    .token(token)
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .userId(user.getId())
                    .success(true)
                    .message("Authentication successful")
                    .build();

        } catch (Exception e) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Authentication failed: " + e.getMessage())
                    .build();
        }
    }

    // Authentifier par email
    public AuthResponse authenticateByEmail(String email) {
        try {
            User user = userDetailsService.getUserByEmail(email);
            String token = jwtUtil.generateToken(user.getUsername());

            return AuthResponse.builder()
                    .token(token)
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .userId(user.getId())
                    .success(true)
                    .message("Authentication successful")
                    .build();

        } catch (Exception e) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Authentication failed: " + e.getMessage())
                    .build();
        }
    }

    // Enregistrer un nouvel utilisateur
    public AuthResponse register(String username, String email) {
        try {
            if (userRepository.existsByUsername(username)) {
                return AuthResponse.builder()
                        .success(false)
                        .message("Username already exists")
                        .build();
            }

            if (userRepository.existsByEmail(email)) {
                return AuthResponse.builder()
                        .success(false)
                        .message("Email already exists")
                        .build();
            }

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            User savedUser = userRepository.save(user);

            String token = jwtUtil.generateToken(username);

            return AuthResponse.builder()
                    .token(token)
                    .username(savedUser.getUsername())
                    .email(savedUser.getEmail())
                    .userId(savedUser.getId())
                    .success(true)
                    .message("Registration successful")
                    .build();

        } catch (Exception e) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Registration failed: " + e.getMessage())
                    .build();
        }
    }

    // Valider un token JWT
    public boolean validateToken(String token, String username) {
        return jwtUtil.validateToken(token, username);
    }

    // Extraire le nom d'utilisateur du token
    public String getUsernameFromToken(String token) {
        return jwtUtil.extractUsername(token);
    }

    // Vérifier si le token est expiré
    public boolean isTokenExpired(String token) {
        return jwtUtil.isTokenExpired(token);
    }

    // DTO pour les réponses d'authentification
    public static class AuthResponse {
        private String token;
        private String username;
        private String email;
        private Long userId;
        private boolean success;
        private String message;

        // Builder pattern
        public static AuthResponseBuilder builder() {
            return new AuthResponseBuilder();
        }

        // Constructeurs, getters et setters
        public AuthResponse() {}

        public AuthResponse(String token, String username, String email, Long userId, boolean success, String message) {
            this.token = token;
            this.username = username;
            this.email = email;
            this.userId = userId;
            this.success = success;
            this.message = message;
        }

        // Getters et Setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        // Builder class
        public static class AuthResponseBuilder {
            private String token;
            private String username;
            private String email;
            private Long userId;
            private boolean success;
            private String message;

            public AuthResponseBuilder token(String token) { this.token = token; return this; }
            public AuthResponseBuilder username(String username) { this.username = username; return this; }
            public AuthResponseBuilder email(String email) { this.email = email; return this; }
            public AuthResponseBuilder userId(Long userId) { this.userId = userId; return this; }
            public AuthResponseBuilder success(boolean success) { this.success = success; return this; }
            public AuthResponseBuilder message(String message) { this.message = message; return this; }

            public AuthResponse build() {
                return new AuthResponse(token, username, email, userId, success, message);
            }
        }
    }
}