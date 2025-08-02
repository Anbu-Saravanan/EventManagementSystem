package com.anbu.ems.dto.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventRequestDTO {
    private String title;
    private String description;
    private String location;
    private LocalDateTime eventDate;
    private String category;
    private List<Long> speakerIds;  // for assigning speakers
}
