package com.anbu.ems.service;

import com.anbu.ems.dto.request.UserRegistrationDTO;
import com.anbu.ems.dto.response.EventResponseDTO;
import com.anbu.ems.dto.response.RegisterResponseDTO;
import com.anbu.ems.dto.response.RegistrationResponseDTO;
import com.anbu.ems.dto.response.SpeakerResponseDTO;
import com.anbu.ems.model.Event;
import com.anbu.ems.model.Registration;
import com.anbu.ems.model.Speaker;
import com.anbu.ems.model.User;
import com.anbu.ems.repository.EventRepository;
import com.anbu.ems.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final EmailService emailService;

    public List<RegistrationResponseDTO> getAllRegistrationDTOs() {
        List<Registration> registrations = registrationRepository.findAll();

        return registrations.stream().map(reg -> {
            RegistrationResponseDTO dto = new RegistrationResponseDTO();
            dto.setRegistrationId(reg.getId());
            dto.setUsername(reg.getUser().getUsername());
            dto.setEmail(reg.getUser().getEmail());

            Event event = reg.getEvent();
            dto.setEventId(event.getId());
            dto.setEventTitle(event.getTitle());
            dto.setRegisteredAt(reg.getRegisteredAt());

            dto.setSpeakers(
                    event.getSpeakers().stream().map(speaker -> {
                        SpeakerResponseDTO sDto = new SpeakerResponseDTO();
                        sDto.setId(speaker.getId());
                        sDto.setName(speaker.getName());
                        sDto.setBio(speaker.getBio());
                        sDto.setExpertise(speaker.getExpertise());
                        return sDto;
                    }).collect(Collectors.toList())
            );

            return dto;
        }).collect(Collectors.toList());
    }


    @Transactional
    public void register(Long eventId, User user) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Registration registration = new Registration();
        registration.setUser(user);
        registration.setEvent(event);

        registrationRepository.save(registration);

        // Send confirmation email
        emailService.sendEventConfirmation(user.getEmail(),
                user.getUsername(),
                event.getTitle(),
                event.getEventDate().toString());
    }

    public List<EventResponseDTO> getRegisteredEventsForUser(User user) {
        List<Registration> registrations = registrationRepository.findByUser(user);
        return registrations.stream()
                .map(reg -> toResponseDTO(reg.getEvent()))
                .collect(Collectors.toList());
    }

    public EventResponseDTO toResponseDTO(Event event) {
        return EventResponseDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                .eventDate(event.getEventDate())
                .category(event.getCategory())
                .speakers(
                        event.getSpeakers().stream()
                                .map(speaker -> {
                                    SpeakerResponseDTO dto = new SpeakerResponseDTO();
                                    dto.setId(speaker.getId());
                                    dto.setName(speaker.getName());
                                    dto.setBio(speaker.getBio());
                                    dto.setExpertise(speaker.getExpertise());
                                    return dto;
                                })
                                .collect(Collectors.toList())
                ).build();
    }

    public List<UserRegistrationDTO> getUsersRegisteredForEvent(Long eventId) {
        List<Registration> registrations = registrationRepository.findByEventId(eventId);

        return registrations.stream()
                .map(r -> new UserRegistrationDTO(
                        r.getUser().getId(),
                        r.getUser().getUsername()
                ))
                .collect(Collectors.toList());
    }


}
