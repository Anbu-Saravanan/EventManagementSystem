package com.anbu.ems.dto.response;

import com.anbu.ems.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AuthenticationResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String email;
    private String username;
    private Role role;
}
