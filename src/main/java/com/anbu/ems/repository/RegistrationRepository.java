package com.anbu.ems.repository;

import com.anbu.ems.model.Registration;
import com.anbu.ems.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration,Long> {

    List<Registration> findByUser(User user);
    List<Registration> findByEventId(Long eventId);


}
