package com.anbu.ems.controller;

import com.anbu.ems.dto.request.AttendanceRequestDTO;
import com.anbu.ems.dto.response.AttendanceResponseDTO;
import com.anbu.ems.model.Attendance;
import com.anbu.ems.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/mark")
    public ResponseEntity<AttendanceResponseDTO> markAttendance(@RequestBody AttendanceRequestDTO dto) {
        return ResponseEntity.ok(attendanceService.markAttendance(dto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<AttendanceResponseDTO>> getAll() {
        return ResponseEntity.ok(attendanceService.getAllAttendances());
    }

    @GetMapping("/event/{eventId}")

    public ResponseEntity<List<AttendanceResponseDTO>> getAttendancesByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(attendanceService.getAttendancesByEvent(eventId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AttendanceResponseDTO>> getAttendancesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(attendanceService.getAttendancesByUser(userId));
    }
}
