package com.anbu.ems.dto.request;

import lombok.Data;

@Data
public class AttendanceRequestDTO {
    private Long userId;
    private Long eventId;
    private boolean present;
}
