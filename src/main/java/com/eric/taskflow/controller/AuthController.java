package com.eric.taskflow.controller;

import com.eric.taskflow.dto.auth.AuthResponse;
import com.eric.taskflow.dto.auth.LoginRequest;
import com.eric.taskflow.dto.auth.RefreshRequest;
import com.eric.taskflow.dto.auth.RegisterRequest;
import com.eric.taskflow.exception.ApiException;
import com.eric.taskflow.security.BlackListService;
import com.eric.taskflow.security.JwtService;
import com.eric.taskflow.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final BlackListService blacklistService;
    /**
     * Registro de nuevos usuarios.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse resp = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }


    /**
     * Login de usuario: devuelve token JWT firmado.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshRequest request
    ) {
        return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ApiException("Invalid Authorization header");
        }

        String token = authHeader.substring(7);

        var decoded = jwtService.decodeToken(token)
                .orElseThrow(() -> new ApiException("Invalid token"));

        String jti = decoded.getId();
        Instant expiresAt = decoded.getExpiresAt().toInstant();

        blacklistService.blacklist(jti, expiresAt);

        return ResponseEntity.noContent().build();
    }


}
