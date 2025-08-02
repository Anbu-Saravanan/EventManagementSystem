package com.anbu.ems.service;

import com.anbu.ems.dto.request.AttendanceRequestDTO;
import com.anbu.ems.dto.response.AttendanceResponseDTO;
import com.anbu.ems.model.Attendance;
import com.anbu.ems.model.Event;
import com.anbu.ems.model.User;
import com.anbu.ems.repository.AttendanceRepository;
import com.anbu.ems.repository.EventRepository;
import com.anbu.ems.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepo;
    private final EventRepository eventRepo;

    public AttendanceResponseDTO markAttendance(AttendanceRequestDTO dto) {
        Attendance attendance = new Attendance();

        // Fetch user and event
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Event event = eventRepo.findById(dto.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        attendance.setUser(user);
        attendance.setEvent(event);
        attendance.setPresent(dto.isPresent());

        System.out.println("Saved attendance for user: " + user.getId() + ", event: " + event.getId());
        return mapToDTO(attendanceRepository.save(attendance));
    }

    public List<AttendanceResponseDTO> getAllAttendances() {
        return attendanceRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public AttendanceResponseDTO mapToDTO(Attendance attendance) {
        AttendanceResponseDTO dto = new AttendanceResponseDTO();
        dto.setId(attendance.getId());

        if (attendance.getUser() != null) {
            dto.setUsername(attendance.getUser().getUsername());
        } else {
            dto.setUsername("Unknown User");
        }

        if (attendance.getEvent() != null) {
            dto.setEventTitle(attendance.getEvent().getTitle());
        } else {
            dto.setEventTitle("Unknown Event");
        }
        dto.setPresent(attendance.isPresent());

        return dto;
    }

    public List<AttendanceResponseDTO> getAttendancesByEvent(Long eventId) {
        return attendanceRepository.findByEventId(eventId)
                .stream()
                .filter(a ->a.getUser() != null && a.getEvent() != null)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AttendanceResponseDTO> getAttendancesByUser(Long userId) {
        return attendanceRepository.findByUserId(userId)
                .stream()
                .filter(a -> a.getUser() != null && a.getEvent() != null)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}
