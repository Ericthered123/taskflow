package com.eric.taskflow.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.eric.taskflow.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/*Crea, firma y valida tokens JWT.
* Extrae claims como username, role y email del token.
* Mantiene token Stateless*/


@Service
public class JwtService {

    private final Algorithm algorithm;
    private final long accessExpirationMillis;
    private final long refreshExpirationMillis;


    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration}") long accessExpirationMillis,
            @Value("${jwt.refresh-expiration}") long refreshExpirationMillis) {

        this.algorithm = Algorithm.HMAC256(secret.getBytes());
        this.accessExpirationMillis = accessExpirationMillis;
        this.refreshExpirationMillis = refreshExpirationMillis;
    }

    // --------------------------
    // ACCESS TOKEN
    // --------------------------
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant exp = now.plusMillis(accessExpirationMillis);

        return JWT.create()
                .withSubject(user.getUsername())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(exp))
                .withJWTId(java.util.UUID.randomUUID().toString())
                .withClaim("role", user.getRole().name())
                .withClaim("email", user.getEmail())
                .withClaim("type", "access")
                .sign(algorithm);
    }


    // --------------------------
    // REFRESH TOKEN
    // --------------------------
    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        Instant exp = now.plusMillis(refreshExpirationMillis);

        return JWT.create()
                .withSubject(user.getUsername())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(exp))
                .withJWTId(java.util.UUID.randomUUID().toString())
                .withClaim("type", "refresh") // identifica que es refresh
                .sign(algorithm);
    }


    // --------------------------
    // REFRESH WORKFLOW
    // --------------------------
    public boolean isRefreshToken(String token) {
        return decodeToken(token)
                .map(jwt -> "refresh".equals(jwt.getClaim("type").asString()))
                .orElse(false);
    }

    /** Devuelve true si es un refresh token y está válido (firma + expiración) */
    public boolean isRefreshTokenValid(String token) {
        return isRefreshToken(token) && validateToken(token);
    }



    /**
     * Toma un refresh token válido → devuelve un nuevo access token
     */
    public String refreshAccessToken(String refreshToken, User user) {
        if (isRefreshTokenValid(refreshToken)) return null;

        return generateAccessToken(user);
    }

    public String extractUsername(String token) {
        return decodeToken(token)
                .map(DecodedJWT::getSubject)
                .orElse(null);
    }

    public String extractEmail(String token) {
        return decodeToken(token)
                .map(jwt -> jwt.getClaim("email").asString())
                .orElse(null);
    }

    public String extractRole(String token) {
        return decodeToken(token)
                .map(jwt -> jwt.getClaim("role").asString())
                .orElse(null);
    }

    // Metodo interno para decodificar el token con verificacion de firma
    public Optional<DecodedJWT> decodeToken(String token) {
        try {
            return Optional.of(JWT.require(algorithm).build().verify(token));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    // Verifica que el token sea válido y no esté expirado
    public boolean validateToken(String token) {
        return decodeToken(token)
                .map(jwt -> jwt.getExpiresAt().toInstant().isAfter(Instant.now()))
                .orElse(false);
    }


    /** Devuelve la duración de expiración como Duration (útil para issuedAt/expiresAt). */
    /** Expiración del ACCESS token */
    public Duration getAccessExpiration() {
        return Duration.ofMillis(accessExpirationMillis);
    }

    /** Expiración del REFRESH token */
    public Duration getRefreshExpiration() {
        return Duration.ofMillis(refreshExpirationMillis);
    }

}
