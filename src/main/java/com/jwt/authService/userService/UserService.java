package com.jwt.authService.userService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.authService.Reposiotry.UserRepository;
import com.jwt.authService.dto.RegisterRequest;
import com.jwt.authService.dto.UserDto;
import com.jwt.authService.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
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
                .role(request.getRole())
                .build();

        userRepository.save(user);

    }

    public UserDto getUser(long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent() && userOpt.get().getEmail().equals(email)) {
            User user = userOpt.get();
            return objectMapper.convertValue(user, UserDto.class);
        }
        return null;
    }

    public List<UserDto> getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"ROLE_ADMIN".equals(currentUser.getRole())) {
            return (List<UserDto>) ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        List<UserDto> users = userRepository.findAll().stream()
                .map(user -> new UserDto(user.getId(), user.getEmail(), user.getRole()))
                .toList();
        return users;

    }
}