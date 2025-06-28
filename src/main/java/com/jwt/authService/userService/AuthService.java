package com.jwt.authService.userService;

import com.jwt.authService.Reposiotry.UserRepository;
import com.jwt.authService.config.JwtService;
import com.jwt.authService.dto.AuthRequest;
import com.jwt.authService.dto.AuthResponse;
import com.jwt.authService.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
        return new AuthResponse(refreshToken);
    }
    }