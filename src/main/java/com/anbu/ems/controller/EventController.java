package com.anbu.ems.controller;


import com.anbu.ems.dto.request.EventRequestDTO;
import com.anbu.ems.dto.response.EventResponseDTO;
import com.anbu.ems.model.Event;
import com.anbu.ems.model.User;
import com.anbu.ems.service.EventService;
import com.anbu.ems.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final UserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponseDTO> createEvent(@RequestBody EventRequestDTO dto) {
        return ResponseEntity.ok(eventService.createEvent(dto));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponseDTO> updateEvent(@PathVariable Long id, @RequestBody EventRequestDTO dto) {
        return eventService.updateEventById(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<EventResponseDTO>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    //Search or Filter Event methods
    @GetMapping("/search/location")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<EventResponseDTO>> searchByLocation(@RequestParam String location) {
        return ResponseEntity.ok(eventService.filterByLocation(location));
    }

    @GetMapping("/search/category")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<EventResponseDTO>> searchByCategory(@RequestParam String category) {
        return ResponseEntity.ok(eventService.filterByCategory(category));
    }

    @GetMapping("/search/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<EventResponseDTO>> searchByDateRange(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(eventService.filterByDateRange(start, end));
    }
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EventResponseDTO>> getEventsCreatedByAdmin() {
        User currentAdmin = userService.getCurrentUser();
        return ResponseEntity.ok(eventService.getEventsByCreator(currentAdmin));
    }

    @PutMapping("/{eventId}/add-speakers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addSpeakersToEvent(
            @PathVariable Long eventId,
            @RequestBody List<Long> speakerIds) {
        eventService.addSpeakersToEvent(eventId, speakerIds);
        return ResponseEntity.ok("Speakers added successfully");
    }




}
