package com.anbu.ems.controller;

import com.anbu.ems.dto.request.SpeakerRequestDTO;
import com.anbu.ems.dto.response.SpeakerResponseDTO;
import com.anbu.ems.service.EventService;
import com.anbu.ems.service.SpeakerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/speakers")
@RequiredArgsConstructor
public class SpeakerController {

    private final SpeakerService speakerService;
    private final EventService eventService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SpeakerResponseDTO> addSpeaker(@RequestBody SpeakerRequestDTO speaker) {
        return ResponseEntity.ok(speakerService.addSpeaker(speaker));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SpeakerResponseDTO>> getAllSpeakers() {
        return ResponseEntity.ok(speakerService.getAllSpeakers());
    }

    @DeleteMapping("/events/{eventId}/speakers/{speakerId}")
    public ResponseEntity<Void> removeSpeakerFromEvent(@PathVariable Long eventId, @PathVariable Long speakerId) {
        eventService.removeSpeaker(eventId, speakerId);
        return ResponseEntity.noContent().build();
    }
}
