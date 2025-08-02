package com.anbu.ems.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class SpeakerRequestDTO {
    private String name;
    private String bio;
    private String expertise;
    private List<Long> speakerIds;

}