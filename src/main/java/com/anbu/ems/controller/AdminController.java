package com.anbu.ems.controller;

import com.anbu.ems.dto.response.EventResponseDTO;
import com.anbu.ems.dto.response.RegisterResponseDTO;
import com.anbu.ems.dto.response.RegistrationResponseDTO;
import com.anbu.ems.dto.response.UserResponseDTO;
import com.anbu.ems.model.Registration;
import com.anbu.ems.service.EventService;
import com.anbu.ems.service.RegistrationService;
import com.anbu.ems.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Protect all methods by ADMIN role
public class AdminController {

    private final UserService userService;
    private final RegistrationService registrationService;
    private final EventService eventService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }


    @GetMapping("/registrations")
    public ResponseEntity<List<RegistrationResponseDTO>> getAllRegistrations() {
        return ResponseEntity.ok(registrationService.getAllRegistrationDTOs());
    }


    @GetMapping("/events")
    public ResponseEntity<List<EventResponseDTO>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }
}
