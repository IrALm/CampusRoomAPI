package com.campusRoom.api.service.research.impl;

import com.campusRoom.api.dto.outPutDto.ReservationDto;
import com.campusRoom.api.dto.researchDto.ReservationPageDto;
import com.campusRoom.api.dto.researchDto.ReservationSearchDto;
import com.campusRoom.api.entity.Reservation;
import com.campusRoom.api.entity.ReservationType;
import com.campusRoom.api.mapper.ReservationMapper;
import com.campusRoom.api.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DisplayName("Tests unitaires pour ReservationResearchServiceImpl")
class ReservationResearchServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationMapper reservationMapper;

    private ReservationResearchServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new ReservationResearchServiceImpl(reservationRepository, reservationMapper);
    }

    @Test
    @DisplayName("search doit renvoyer une page avec contenu")
    void shouldReturnPageWithContent() {
        // Création du DTO de recherche
        ReservationSearchDto searchDto = ReservationSearchDto.builder()
                .type(ReservationType.COURSE)
                .roomId(10L)
                .userId(5L)
                .startTime(LocalDateTime.of(2026, 3, 25, 10, 0))
                .endTime(LocalDateTime.of(2026, 3, 25, 12, 0))
                .page(0)
                .size(10)
                .sortBy("startTime")
                .sortDirection(Sort.Direction.ASC)
                .build();

        Pageable pageable = searchDto.toPagination().toPageable();

        // Mock des entités Reservation
        Reservation reservation1 = Reservation.builder().id(1L).build();
        Reservation reservation2 = Reservation.builder().id(2L).build();

        Page<Reservation> reservationPage = new PageImpl<>(List.of(reservation1, reservation2), pageable, 2);

        // Mock des DTO
        ReservationDto dto1 = ReservationDto.builder().id(1L).build();
        ReservationDto dto2 = ReservationDto.builder().id(2L).build();

        // Mock du repository et du mapper
        when(reservationRepository.findAll(
                (org.springframework.data.jpa.domain.Specification<Reservation>) any(),
                eq(pageable)
        )).thenReturn(reservationPage);
        when(reservationMapper.toDTO(reservation1)).thenReturn(dto1);
        when(reservationMapper.toDTO(reservation2)).thenReturn(dto2);

        // Exécution
        ReservationPageDto result = service.search(searchDto);

        // Assertions
        assertNotNull(result);
        assertEquals(2, result.contenu().size());
        assertEquals(1L, result.contenu().get(0).id());
        assertEquals(2L, result.contenu().get(1).id());
        assertEquals(2, result.totalElements());
    }

    @Test
    @DisplayName("search doit renvoyer une page vide si aucun résultat")
    void shouldReturnEmptyPage() {
        ReservationSearchDto searchDto = ReservationSearchDto.builder()
                .page(0)
                .size(10)
                .sortBy("startTime")
                .sortDirection(Sort.Direction.ASC)
                .build();

        Pageable pageable = searchDto.toPagination().toPageable();

        Page<Reservation> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(reservationRepository.findAll(
                (org.springframework.data.jpa.domain.Specification<Reservation>) any(),
                eq(pageable)
        )).thenReturn(emptyPage);

        ReservationPageDto result = service.search(searchDto);

        assertNotNull(result);
        assertTrue(result.contenu().isEmpty());
        assertEquals(0, result.totalElements());
        assertEquals(0, result.totalPages());
    }
}