package net.salim.api_motus.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Clé secrète pour signer les tokens (doit faire au moins 256 bits)
    @Value("${jwt.secret:motusSecretKeyForJwtTokenGeneration2024VeryLongSecretKeyMinimum256BitsRequiredForHS256Algorithm}")
    private String secretKey;

    // Durée de validité du token en millisecondes (24 heures par défaut)
    @Value("${jwt.expiration:86400000}")
    private Long jwtExpiration;

    // Générer une clé sécurisée pour JJWT 0.12.5
    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Générer un token JWT pour un utilisateur
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    // Générer un token avec des claims personnalisés
    public String generateToken(String username, Map<String, Object> extraClaims) {
        Map<String, Object> claims = new HashMap<>(extraClaims);
        return createToken(claims, username);
    }

    // Créer le token JWT - VERSION JJWT 0.12.5
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .claims(claims)  // Nouvelle méthode dans 0.12.5
                .subject(subject)
                .issuedAt(now)
                .expiration(expirationDate)  // Nouvelle méthode dans 0.12.5
                .signWith(getSigningKey())   // Algorithme automatiquement détecté
                .compact();
    }

    // Extraire le nom d'utilisateur du token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extraire la date d'expiration
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extraire une claim spécifique
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extraire toutes les claims - VERSION JJWT 0.12.5
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()  // parser() existe toujours dans 0.12.5
                    .verifyWith(getSigningKey())  // Nouvelle méthode dans 0.12.5
                    .build()
                    .parseSignedClaims(token)  // Nouvelle méthode dans 0.12.5
                    .getPayload();  // Nouvelle méthode dans 0.12.5
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token: " + e.getMessage());
        }
    }

    // Vérifier si le token est expiré
    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return true; // Si erreur, considérer comme expiré
        }
    }

    // Valider le token
    public boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    // Obtenir le temps restant avant expiration (en millisecondes)
    public long getExpirationTime(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            return 0;
        }
    }

    // Vérifier si le token est valide (non expiré et bien formé)
    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // Obtenir les informations du token sous forme lisible
    public TokenInfo getTokenInfo(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return new TokenInfo(
                    claims.getSubject(),
                    claims.getIssuedAt(),
                    claims.getExpiration(),
                    !isTokenExpired(token)
            );
        } catch (Exception e) {
            return new TokenInfo(null, null, null, false);
        }
    }

    // Classe pour les informations du token
    public static class TokenInfo {
        private final String username;
        private final Date issuedAt;
        private final Date expiresAt;
        private final boolean valid;

        public TokenInfo(String username, Date issuedAt, Date expiresAt, boolean valid) {
            this.username = username;
            this.issuedAt = issuedAt;
            this.expiresAt = expiresAt;
            this.valid = valid;
        }

        // Getters
        public String getUsername() { return username; }
        public Date getIssuedAt() { return issuedAt; }
        public Date getExpiresAt() { return expiresAt; }
        public boolean isValid() { return valid; }

        @Override
        public String toString() {
            return String.format("TokenInfo{username='%s', issuedAt=%s, expiresAt=%s, valid=%s}",
                    username, issuedAt, expiresAt, valid);
        }
    }
}

// ===== APPLICATION.PROPERTIES POUR JJWT 0.12.5 =====
/*
# Clé JWT - DOIT faire au moins 256 bits (32 caractères)
jwt.secret=motusSecretKeyForJwtTokenGeneration2024VeryLongSecretKeyMinimum256BitsRequiredForHS256Algorithm
jwt.expiration=86400000

# Si votre clé est trop courte, utilisez cette clé générée :
# jwt.secret=3F4A9D2E8B7C1A5F6E9D3B8A4C7E2F1D9A8B5C3E7F2A6D9C8E1B4F7A2D5C8E1B4
*/