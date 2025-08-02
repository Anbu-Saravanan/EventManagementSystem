package com.anbu.ems.service;

import com.anbu.ems.dto.request.EventRequestDTO;
import com.anbu.ems.dto.response.EventResponseDTO;
import com.anbu.ems.dto.response.SpeakerResponseDTO;
import com.anbu.ems.model.Event;
import com.anbu.ems.model.Speaker;
import com.anbu.ems.model.User;
import com.anbu.ems.repository.EventRepository;
import com.anbu.ems.repository.SpeakerRepository;
import com.anbu.ems.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final SpeakerService speakerService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final SpeakerRepository speakerRepository;


    // Inside UserService class
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }



    // CREATE new Event
    public EventResponseDTO createEvent(EventRequestDTO dto) {
        Event event = new Event();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setEventDate(dto.getEventDate());
        event.setCategory(dto.getCategory());
        event.setCreatedBy(userService.getCurrentUser());
        List<Speaker> speakers = speakerService.findByIds(dto.getSpeakerIds());
        event.setSpeakers(speakers);


        if (dto.getSpeakerIds() != null && !dto.getSpeakerIds().isEmpty()) {
            event.setSpeakers(speakerService.findByIds(dto.getSpeakerIds()));
        } else {
            event.setSpeakers(List.of()); // or Collections.emptyList()
        }
        System.out.println("Received speakerIds: " + dto.getSpeakerIds());

        return mapToDTO(eventRepository.save(event));
    }

    // GET all Events
    public List<EventResponseDTO> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<EventResponseDTO> getEventsByCreator(User creator) {
        List<Event> events = eventRepository.findByCreatedBy(creator);
        return events.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // GET event by ID
    public Optional<EventResponseDTO> getEventById(Long id) {
        return eventRepository.findById(id).map(this::mapToDTO);
    }

    // DELETE event
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    // UPDATE existing event manually
    public EventResponseDTO saveUpdatedEvent(Event event) {
        return mapToDTO(eventRepository.save(event));
    }

    // UPDATE event using DTO (recommended)
    public Optional<EventResponseDTO> updateEventById(Long id, EventRequestDTO dto) {
        return eventRepository.findById(id).map(event -> {
            event.setTitle(dto.getTitle());
            event.setDescription(dto.getDescription());
            event.setLocation(dto.getLocation());
            event.setEventDate(dto.getEventDate());
            event.setCategory(dto.getCategory());
            event.setSpeakers(speakerService.findByIds(dto.getSpeakerIds()));

            return mapToDTO(eventRepository.save(event));
        });
    }

    // Convert Entity to ResponseDTO
    private EventResponseDTO mapToDTO(Event event) {
        return EventResponseDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                .eventDate(event.getEventDate())
                .category(event.getCategory())
                .speakers( // assuming this is already mapped
                        event.getSpeakers().stream()
                                .map(speakerService::mapToDTO)
                                .collect(Collectors.toList())
                )
                .createdBy(event.getCreatedBy() != null ? event.getCreatedBy().getUsername() : "Unknown")
                .build();
    }

    // Filter or Search Events by relevant info
    public List<EventResponseDTO> filterByLocation(String location) {
        return eventRepository.findByLocationContainingIgnoreCase(location)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<EventResponseDTO> filterByCategory(String category) {
        return eventRepository.findByCategoryIgnoreCase(category)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<EventResponseDTO> filterByDateRange(LocalDateTime start, LocalDateTime end) {
        return eventRepository.findByEventDateBetween(start, end)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public void removeSpeaker(Long eventId, Long speakerId) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        Speaker speaker = speakerRepository.findById(speakerId).orElseThrow();

        event.getSpeakers().remove(speaker);
        speaker.getEvents().remove(event);
    }
    public void addSpeakersToEvent(Long eventId, List<Long> speakerIds) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        List<Speaker> newSpeakers = speakerService.findByIds(speakerIds);

        if (event.getSpeakers() == null) {
            event.setSpeakers(new ArrayList<>());
        }

        // Avoid duplicates
        for (Speaker speaker : newSpeakers) {
            if (!event.getSpeakers().contains(speaker)) {
                event.getSpeakers().add(speaker);
            }
        }

        eventRepository.save(event); // persists join table
    }


}
