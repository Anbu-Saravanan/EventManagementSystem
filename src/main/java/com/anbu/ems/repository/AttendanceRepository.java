package com.anbu.ems.repository;

import com.anbu.ems.dto.response.AttendanceResponseDTO;
import com.anbu.ems.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByUserId(Long userId);
    List<Attendance> findByEventId(Long eventId);
}

