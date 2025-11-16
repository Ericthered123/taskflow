package com.eric.taskflow.dto.auth;

import com.eric.taskflow.dto.user.UserResponse;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data //Esta clase debe devolver el JWT + datos básicos del usuario.
@Builder//automaticamente genera un patron builder para las Java classes
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;  // Bearer
    private Instant expiresAt;
    private UserResponse user;


}
