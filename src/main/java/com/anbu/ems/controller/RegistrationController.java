package com.anbu.ems.controller;

import com.anbu.ems.dto.request.RegistrationRequestDTO;
import com.anbu.ems.dto.request.UserRegistrationDTO;
import com.anbu.ems.model.User;
import com.anbu.ems.service.RegistrationService;
import com.anbu.ems.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerForEvent(@RequestBody RegistrationRequestDTO request,
                                                   HttpServletRequest httpRequest) {
        User user = userService.getCurrentUser();
        registrationService.register(request.getEventId(), user);
        return ResponseEntity.ok("Registration successful. Check your email for confirmation.");
    }

    @GetMapping("/my-registrations")
    public ResponseEntity<?> getUserRegisteredEvents() {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(registrationService.getRegisteredEventsForUser(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/event/{eventId}/users")
    public ResponseEntity<List<UserRegistrationDTO>> getUsersRegisteredForEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(registrationService.getUsersRegisteredForEvent(eventId));
    }


}
