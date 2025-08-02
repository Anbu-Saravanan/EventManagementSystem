package com.anbu.ems.dto.response;

import lombok.Data;

@Data
public class RegisterResponseDTO {
    private String username;
    private String email;
    private String password;
}
