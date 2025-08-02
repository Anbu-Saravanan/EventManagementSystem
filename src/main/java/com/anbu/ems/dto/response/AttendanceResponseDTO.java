package com.anbu.ems.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttendanceResponseDTO {
    private Long id;
    private String username;
    private String eventTitle;
    private boolean present;
}
