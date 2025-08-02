package com.anbu.ems.dto.request;

import lombok.Data;

@Data
public class RefreshTokenRequestDTO {
    private String refreshToken;
    private String email;
}
