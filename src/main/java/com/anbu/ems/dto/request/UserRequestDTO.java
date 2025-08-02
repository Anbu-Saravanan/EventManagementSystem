package com.anbu.ems.dto.request;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class UserRequestDTO {
    @NotBlank(message = "Username is required")
    private String username;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String role; // e.g. ROLE_USER or ROLE_ADMIN
}
