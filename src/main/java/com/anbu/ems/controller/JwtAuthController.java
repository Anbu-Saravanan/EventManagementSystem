package com.anbu.ems.controller;

import com.anbu.ems.dto.request.LoginRequestDTO;
import com.anbu.ems.dto.request.RefreshTokenRequestDTO;
import com.anbu.ems.dto.request.UserRequestDTO;
import com.anbu.ems.dto.response.AuthenticationResponseDTO;
import com.anbu.ems.model.User;
import com.anbu.ems.repository.UserRepository;
import com.anbu.ems.security.jwt.service.AuthService;
import com.anbu.ems.security.jwt.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
 public class JwtAuthController{

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDTO> register(@RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(authService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO request) {
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("No user with email " + request.getEmail()));

        // VALIDATE the refresh token!
        boolean valid = jwtService.isTokenValid(request.getRefreshToken(), user.getEmail());
        if (!valid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String accessToken = jwtService.generateAccessToken(
                user.getEmail(),
                Map.of("role", user.getRole().name())
        );
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        System.out.println("Refresh request: email=" + request.getEmail());
        System.out.println("Token subject: " + jwtService.extractUsername(request.getRefreshToken()));
        System.out.println("Token expiry: " + jwtService.extractClaim(request.getRefreshToken(), Claims::getExpiration));
        System.out.println("Current time: " + new Date());

        return ResponseEntity.ok(AuthenticationResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());
    }


}
