package com.jwt.authService.userService;

import com.jwt.authService.config.JwtService;
import com.jwt.authService.dto.AuthRequest;
import com.jwt.authService.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse login(AuthRequest request) {

        String accessToken = jwtService.generateToken(request.getEmail(), 15 * 60 * 1000); // 15 min
        String refreshToken = jwtService.generateToken(request.getEmail(), 15 * 60 * 1000); // 15 min
        return new AuthResponse(accessToken, refreshToken);
    }
    }