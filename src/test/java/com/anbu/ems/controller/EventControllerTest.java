package com.anbu.ems.controller;

import com.anbu.ems.dto.request.EventRequestDTO;
import com.anbu.ems.dto.response.EventResponseDTO;
import com.anbu.ems.model.User;
import com.anbu.ems.service.EventService;
import com.anbu.ems.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private EventService eventService;

    @Mock
    private UserService userService;

    @InjectMocks
    private EventController eventController;

    private MockMvc mockMvc;

    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createEvent() throws Exception {
        setup();
        EventRequestDTO requestDTO = new EventRequestDTO();
        EventResponseDTO responseDTO = new EventResponseDTO();
        given(eventService.createEvent(any(EventRequestDTO.class))).willReturn(responseDTO);

        mockMvc.perform(post("/api/events/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateEvent() throws Exception {
        setup();
        EventRequestDTO requestDTO = new EventRequestDTO();
        EventResponseDTO responseDTO = new EventResponseDTO();
        given(eventService.updateEventById(eq(1L), any(EventRequestDTO.class)))
                .willReturn(Optional.of(responseDTO));

        mockMvc.perform(put("/api/events/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void getEventById() throws Exception {
        setup();
        EventResponseDTO responseDTO = new EventResponseDTO();
        given(eventService.getEventById(1L)).willReturn(Optional.of(responseDTO));

        mockMvc.perform(get("/api/events/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllEvents() throws Exception {
        setup();
        given(eventService.getAllEvents()).willReturn(List.of(new EventResponseDTO()));

        mockMvc.perform(get("/api/events/all"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteEvent() throws Exception {
        setup();
        doNothing().when(eventService).deleteEvent(1L);

        mockMvc.perform(delete("/api/events/delete/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void searchByLocation() throws Exception {
        setup();
        given(eventService.filterByLocation("Chennai")).willReturn(List.of(new EventResponseDTO()));

        mockMvc.perform(get("/api/events/search/location").param("location", "Chennai"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void searchByCategory() throws Exception {
        setup();
        given(eventService.filterByCategory("Tech")).willReturn(List.of(new EventResponseDTO()));

        mockMvc.perform(get("/api/events/search/category").param("category", "Tech"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void searchByDateRange() throws Exception {
        setup();
        given(eventService.filterByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(List.of(new EventResponseDTO()));

        mockMvc.perform(get("/api/events/search/date-range")
                        .param("start", LocalDateTime.now().toString())
                        .param("end", LocalDateTime.now().plusDays(10).toString()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEventsCreatedByAdmin() throws Exception {
        setup();
        User admin = new User();
        given(userService.getCurrentUser()).willReturn(admin);
        given(eventService.getEventsByCreator(admin)).willReturn(List.of(new EventResponseDTO()));

        mockMvc.perform(get("/api/events/admin"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addSpeakersToEvent() throws Exception {
        setup();
        doNothing().when(eventService).addSpeakersToEvent(eq(1L), any(List.class));

        mockMvc.perform(put("/api/events/1/add-speakers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(2L, 3L))))
                .andExpect(status().isOk());
    }
}
