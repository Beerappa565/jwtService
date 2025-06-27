package com.jwt.authService.controller;


import com.jwt.authService.config.JwtService;
import com.jwt.authService.dto.*;
import com.jwt.authService.userService.AuthService;
import com.jwt.authService.userService.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request) {
        System.out.println("Register endpoint hit");
        userService.registerUser(request);
        return ResponseEntity.ok(new ApiResponse("User registered successfully", true));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String refreshToken = authHeader.substring(7); // remove "Bearer "
        String username = jwtService.extractUsername(refreshToken);

        if (!jwtService.isTokenValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String newAccessToken = jwtService.generateToken(username, 15 * 60 * 1000); // 15 min
        String newRefreshToken = jwtService.generateToken(username, 15 * 60 * 1000); // optional

        return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken));
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserDetails(@PathVariable long id) {
        System.out.println("Register endpoint hit");

        return ResponseEntity.ok(userService.getUser(id));
    }




}