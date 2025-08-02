package com.anbu.ems.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data

public class RegistrationResponseDTO {
    private Long registrationId;
    private String username;
    private String email;
    private Long eventId;
    private String eventTitle;
    private LocalDateTime registeredAt;
    private List<SpeakerResponseDTO> speakers;
}
