package com.anbu.ems.service;

import com.anbu.ems.dto.request.SpeakerRequestDTO;
import com.anbu.ems.dto.response.SpeakerResponseDTO;
import com.anbu.ems.model.Speaker;
import com.anbu.ems.repository.SpeakerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpeakerService {

    private final SpeakerRepository speakerRepository;

    public SpeakerResponseDTO addSpeaker(SpeakerRequestDTO dto) {
        Speaker speaker = new Speaker(null, dto.getName(), dto.getBio(), dto.getExpertise(), null);
        return mapToDTO(speakerRepository.save(speaker));
    }

    public List<SpeakerResponseDTO> getAllSpeakers() {
        return speakerRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public void deleteSpeaker(Long id) {
        speakerRepository.deleteById(id);
    }

    public SpeakerResponseDTO mapToDTO(Speaker speaker) {
        SpeakerResponseDTO dto = new SpeakerResponseDTO();
        dto.setId(speaker.getId());
        dto.setName(speaker.getName());
        dto.setBio(speaker.getBio());
        dto.setExpertise(speaker.getExpertise());
        return dto;
    }

    public List<Speaker> findByIds(List<Long> ids) {
        return speakerRepository.findAllById(ids);
    }
}
