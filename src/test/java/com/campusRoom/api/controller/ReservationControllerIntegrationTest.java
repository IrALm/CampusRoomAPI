package com.campusRoom.api.controller;

import com.campusRoom.api.dto.formDto.ReservationFormDto;
import com.campusRoom.api.dto.outPutDto.ReservationDto;
import com.campusRoom.api.dto.outPutDto.RoomDto;
import com.campusRoom.api.dto.outPutDto.UserDto;
import com.campusRoom.api.dto.researchDto.ReservationPageDto;
import com.campusRoom.api.dto.researchDto.ReservationSearchDto;
import com.campusRoom.api.entity.ReservationType;
import com.campusRoom.api.service.ReservationService;
import com.campusRoom.api.service.research.ReservationResearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
@DisplayName("Tests d'intégration - ReservationController")
class ReservationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private ReservationResearchService reservationResearchService;

    @Autowired
    private ObjectMapper objectMapper;

    // ================================
    // CREATE
    // ================================
    @Test
    @DisplayName("POST /reservations -> 204 OK")
    void shouldCreateReservation() throws Exception {

        ReservationFormDto dto = ReservationFormDto.builder()
                .type(ReservationType.COURSE)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .roomId(1L)
                .userId(1L)
                .build();

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        verify(reservationService).create(any(ReservationFormDto.class));
    }

    @Test
    @DisplayName("POST /reservations -> 400 si invalide")
    void shouldFailCreateReservationIfInvalid() throws Exception {

        ReservationFormDto dto = ReservationFormDto.builder()
                .type(null) // invalide
                .build();

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // ================================
    // GET BY ID
    // ================================
    @Test
    @DisplayName("GET /reservations/{id} -> 200")
    void shouldGetReservationById() throws Exception {

        ReservationDto dto = ReservationDto.builder()
                .id(1L)
                .type(ReservationType.COURSE)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(2))
                .description("Réservation de cours")
                .maxDurationHours(3)
                .roomDto(
                        RoomDto.builder()
                                .id(1L)
                                .name("Salle A")
                                .build()
                )
                .userDto(
                        UserDto.builder()
                                .id(1L)
                                .firstName("John")
                                .lastName("Doe")
                                .email("john.doe@mail.com")
                                .build()
                )
                .build();

        when(reservationService.getReservationWithAllProperties(1L)).thenReturn(dto);

        mockMvc.perform(get("/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.roomDto.name").value("Salle A"));

        verify(reservationService).getReservationWithAllProperties(1L);
    }

    // ================================
    // SEARCH
    // ================================
    @Test
    @DisplayName("POST /reservations/search -> 200 avec contenu")
    void shouldSearchReservations() throws Exception {

        ReservationSearchDto searchDto = ReservationSearchDto.builder()
                .page(0)
                .size(10)
                .build();

        ReservationDto dto = ReservationDto.builder()
                .id(1L)
                .build();

        ReservationPageDto pageDto = ReservationPageDto.builder()
                .contenu(List.of(dto))
                .pageActuelle(0)
                .totalPages(1)
                .totalElements(1)
                .premierePage(true)
                .dernierePage(true)
                .build();

        when(reservationResearchService.search(any())).thenReturn(pageDto);

        mockMvc.perform(post("/reservations/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenu.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(reservationResearchService).search(any());
    }

    // ================================
    // DELETE
    // ================================
    @Test
    @DisplayName("DELETE /reservations/{id} -> 204")
    void shouldDeleteReservation() throws Exception {

        mockMvc.perform(delete("/reservations/1"))
                .andExpect(status().isNoContent());

        verify(reservationService).deleteById(1L);
    }
}