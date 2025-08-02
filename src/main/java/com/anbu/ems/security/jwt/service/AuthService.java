package com.anbu.ems.security.jwt.service;

import com.anbu.ems.dto.request.LoginRequestDTO;
import com.anbu.ems.dto.request.RefreshTokenRequestDTO;
import com.anbu.ems.dto.request.UserRequestDTO;
import com.anbu.ems.dto.response.AuthenticationResponseDTO;
import com.anbu.ems.model.Role;
import com.anbu.ems.model.User;
import com.anbu.ems.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticationResponseDTO register(UserRequestDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.valueOf(dto.getRole()));
        user.setEnabled(true);
        userRepository.save(user);


        return generateTokens(user);
    }

    public AuthenticationResponseDTO login(LoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return generateTokens(user);
    }

    public AuthenticationResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        String email = jwtService.extractUsername(request.getRefreshToken());
        User user = userRepository.findByEmail(email).orElseThrow();

        if (!jwtService.isTokenValid(request.getRefreshToken(), email)) {
            throw new RuntimeException("Refresh token invalid");
        }

        return generateTokens(user);
    }

    private AuthenticationResponseDTO generateTokens(User user) {
        Map<String, Object> claims = Map.of("role", user.getRole().name());
        String accessToken = jwtService.generateAccessToken(user.getEmail(), claims);
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
        return new AuthenticationResponseDTO(
                accessToken,
                refreshToken,
                user.getEmail(),
                user.getUsername(),
                user.getRole()
        );
    }
}