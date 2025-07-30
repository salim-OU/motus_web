package net.salim.api_motus.web;

import net.salim.api_motus.JwtUtil.JwtUtil;
import net.salim.api_motus.dto.*;
import net.salim.api_motus.service.AuthService;
import net.salim.api_motus.service.AuthService.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    // Connexion par nom d'utilisateur
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.authenticate(request.getUsername());

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Connexion par email
    @PostMapping("/login/email")
    public ResponseEntity<AuthResponse> loginByEmail(@RequestBody EmailLoginRequest request) {
        AuthResponse response = authService.authenticateByEmail(request.getEmail());

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Inscription
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request.getUsername(), request.getEmail());

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Valider un token
    @PostMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestBody TokenRequest request) {
        try {
            String username = jwtUtil.extractUsername(request.getToken());
            boolean isValid = jwtUtil.validateToken(request.getToken(), username);

            TokenValidationResponse response = new TokenValidationResponse();
            response.setValid(isValid);
            response.setUsername(username);
            response.setExpired(jwtUtil.isTokenExpired(request.getToken()));

            if (isValid) {
                response.setMessage("Token is valid");
                return ResponseEntity.ok(response);
            } else {
                response.setMessage("Token is invalid or expired");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            TokenValidationResponse response = new TokenValidationResponse();
            response.setValid(false);
            response.setMessage("Token validation failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Rafraîchir un token
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody TokenRequest request) {
        try {
            String username = jwtUtil.extractUsername(request.getToken());

            if (!jwtUtil.isTokenExpired(request.getToken())) {
                // Si le token n'est pas encore expiré, créer un nouveau token
                AuthResponse response = authService.authenticate(username);
                return ResponseEntity.ok(response);
            } else {
                AuthResponse response = AuthResponse.builder()
                        .success(false)
                        .message("Token is expired, please login again")
                        .build();
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            AuthResponse response = AuthResponse.builder()
                    .success(false)
                    .message("Token refresh failed: " + e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Obtenir les informations du token
    @PostMapping("/token-info")
    public ResponseEntity<JwtUtil.TokenInfo> getTokenInfo(@RequestBody TokenRequest request) {
        try {
            JwtUtil.TokenInfo tokenInfo = jwtUtil.getTokenInfo(request.getToken());
            return ResponseEntity.ok(tokenInfo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}