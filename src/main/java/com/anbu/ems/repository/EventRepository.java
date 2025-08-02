package com.anbu.ems.repository;

import com.anbu.ems.model.Attendance;
import com.anbu.ems.model.Event;
import com.anbu.ems.model.Registration;
import com.anbu.ems.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event,Long> {
    List<Event> findByLocationContainingIgnoreCase(String location);

    List<Event> findByEventDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Event> findByCategoryIgnoreCase(String category);

    List<Event> findByCreatedBy(User creator);
}
