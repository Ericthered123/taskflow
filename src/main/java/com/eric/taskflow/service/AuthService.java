package com.eric.taskflow.service;


import com.eric.taskflow.dto.auth.AuthResponse;
import com.eric.taskflow.dto.auth.LoginRequest;
import com.eric.taskflow.dto.auth.RegisterRequest;


public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);


    AuthResponse refresh(String refreshToken);
}
