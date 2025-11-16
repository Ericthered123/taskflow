package com.eric.taskflow.service.implementations;

import com.eric.taskflow.dto.auth.AuthResponse;
import com.eric.taskflow.dto.auth.LoginRequest;
import com.eric.taskflow.dto.auth.RegisterRequest;
import com.eric.taskflow.exception.ApiException;
import com.eric.taskflow.mapper.UserMapper;
import com.eric.taskflow.model.Role;
import com.eric.taskflow.model.User;
import com.eric.taskflow.repository.UserRepository;
import com.eric.taskflow.security.JwtService;
import com.eric.taskflow.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.Instant;

/*Registra usuarios (/register) y genera JWT.
* Login de usuarios (/login) con validación de password y emisión de JWT.*/

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ApiException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Email already in use");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresAt(Instant.now().plus(jwtService.getAccessExpiration()))
                .user(userMapper.toUserResponse(user))
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ApiException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException("Invalid username or password");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresAt(Instant.now().plus(jwtService.getAccessExpiration()))
                .user(userMapper.toUserResponse(user))
                .build();
    }


    @Override
    public AuthResponse refresh(String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ApiException("Refresh token is required");
        }

        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            throw new ApiException("Invalid or expired refresh token");
        }

        // Extraer username del refresh token
        String username = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("User not found"));

        // Generar nuevos tokens
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresAt(Instant.now().plus(jwtService.getAccessExpiration()))
                .user(userMapper.toUserResponse(user))
                .build();
    }
}



