package com.anbu.ems.controller;

import com.anbu.ems.dto.request.LoginRequestDTO;
import com.anbu.ems.dto.request.RefreshTokenRequestDTO;
import com.anbu.ems.dto.request.UserRequestDTO;
import com.anbu.ems.dto.response.AuthenticationResponseDTO;
import com.anbu.ems.model.Role;
import com.anbu.ems.model.User;
import com.anbu.ems.repository.UserRepository;
import com.anbu.ems.security.jwt.service.AuthService;
import com.anbu.ems.security.jwt.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class JwtAuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JwtAuthController jwtAuthController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(jwtAuthController).build();
    }

    @Test
    void register() throws Exception {
        UserRequestDTO dto = new UserRequestDTO();
        AuthenticationResponseDTO responseDTO = AuthenticationResponseDTO.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build();

        given(authService.register(any(UserRequestDTO.class))).willReturn(responseDTO);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void login() throws Exception {
        LoginRequestDTO dto = new LoginRequestDTO();
        AuthenticationResponseDTO responseDTO = AuthenticationResponseDTO.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build();

        given(authService.login(any(LoginRequestDTO.class))).willReturn(responseDTO);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void refreshToken() throws Exception {
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO();
        dto.setEmail("user@example.com");
        dto.setRefreshToken("refresh-token");

        User user = new User();
        user.setEmail("user@example.com");
        user.setRole(Role.ROLE_USER);

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
        given(jwtService.isTokenValid(anyString(), anyString())).willReturn(true);
        given(jwtService.generateAccessToken(anyString(), anyMap())).willReturn("new-access-token");
        given(jwtService.generateRefreshToken(anyString())).willReturn("new-refresh-token");
        given(jwtService.extractUsername(anyString())).willReturn("user@example.com");
        given(jwtService.extractClaim(anyString(), any())).willReturn(new Date(System.currentTimeMillis() + 3600000));

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void refreshToken_InvalidToken() throws Exception {
        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO();
        dto.setEmail("user@example.com");
        dto.setRefreshToken("invalid-refresh-token");

        User user = new User();
        user.setEmail("user@example.com");

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
        given(jwtService.isTokenValid(anyString(), anyString())).willReturn(false);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

//    @Test
//    void refreshToken_UserNotFound() throws Exception {
//        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO();
//        dto.setEmail("nonexistent@example.com");
//
//        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
//
//        mockMvc.perform(post("/api/auth/refresh")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isNotFound());
//    }
}
