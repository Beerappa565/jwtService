package com.jwt.authService.userService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.authService.Reposiotry.UserRepository;
import com.jwt.authService.dto.RegisterRequest;
import com.jwt.authService.dto.UserDto;
import com.jwt.authService.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    public void registerUser(RegisterRequest request) {
        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Username already exists");
                });

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();

        userRepository.save(user);

    }

    public UserDto getUser(long id) {
        Optional<User> user=userRepository.findById(id);
        return objectMapper.convertValue(user,UserDto.class);
    }
}