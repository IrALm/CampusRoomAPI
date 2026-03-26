package com.campusRoom.api.researchDto;

import com.campusRoom.api.dto.outPutDto.ReservationDto;
import com.campusRoom.api.dto.researchDto.ReservationPageDto;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservationPageDtoTest {

    // Page normale avec plusieurs éléments
    @Test
    void testFrom_withNormalPage() {
        List<ReservationDto> contenu = List.of(
                ReservationDto.builder().id(1L).startTime(LocalDateTime.now()).endTime(LocalDateTime.now().plusHours(1)).build(),
                ReservationDto.builder().id(2L).startTime(LocalDateTime.now()).endTime(LocalDateTime.now().plusHours(2)).build()
        );

        Page<ReservationDto> page = new PageImpl<>(contenu, PageRequest.of(0, 2), 5);

        ReservationPageDto dto = ReservationPageDto.from(page);

        assertEquals(contenu, dto.contenu());
        assertEquals(0, dto.pageActuelle());
        assertEquals(3, dto.totalPages()); // 5 éléments / 2 par page => 3 pages
        assertEquals(5, dto.totalElements());
        assertFalse(dto.dernierePage());
        assertTrue(dto.premierePage());
    }

    // Page vide
    @Test
    void testFrom_withEmptyPage() {
        Page<ReservationDto> page = Page.empty(PageRequest.of(0, 5));

        ReservationPageDto dto = ReservationPageDto.from(page);

        assertTrue(dto.contenu().isEmpty());
        assertEquals(0, dto.pageActuelle());
        assertEquals(0, dto.totalPages()); // Forcé à 1 pour front cohérent
        assertEquals(0, dto.totalElements());
        assertTrue(dto.dernierePage());
        assertTrue(dto.premierePage());
    }

    // Dernière page
    @Test
    void testFrom_withLastPage() {
        List<ReservationDto> contenu = List.of(
                ReservationDto.builder().id(5L).startTime(LocalDateTime.now()).endTime(LocalDateTime.now().plusHours(1)).build()
        );

        Page<ReservationDto> page = new PageImpl<>(contenu, PageRequest.of(2, 2), 5);

        ReservationPageDto dto = ReservationPageDto.from(page);

        assertEquals(contenu, dto.contenu());
        assertEquals(2, dto.pageActuelle());
        assertEquals(3, dto.totalPages());
        assertEquals(5, dto.totalElements());
        assertTrue(dto.dernierePage());
        assertFalse(dto.premierePage());
    }

    // Première page
    @Test
    void testFrom_withFirstPage() {
        List<ReservationDto> contenu = List.of(
                ReservationDto.builder().id(1L).startTime(LocalDateTime.now()).endTime(LocalDateTime.now().plusHours(1)).build()
        );

        Page<ReservationDto> page = new PageImpl<>(contenu, PageRequest.of(0, 2), 5);

        ReservationPageDto dto = ReservationPageDto.from(page);

        assertEquals(contenu, dto.contenu());
        assertEquals(0, dto.pageActuelle());
        assertEquals(3, dto.totalPages());
        assertEquals(5, dto.totalElements());
        assertFalse(dto.dernierePage() && dto.premierePage() == false); // Premier mais pas dernier
        assertTrue(dto.premierePage());
    }

    // Page intermédiaire
    @Test
    void testFrom_withMiddlePage() {
        List<ReservationDto> contenu = List.of(
                ReservationDto.builder().id(3L).startTime(LocalDateTime.now()).endTime(LocalDateTime.now().plusHours(1)).build()
        );

        Page<ReservationDto> page = new PageImpl<>(contenu, PageRequest.of(1, 2), 5);

        ReservationPageDto dto = ReservationPageDto.from(page);

        assertEquals(contenu, dto.contenu());
        assertEquals(1, dto.pageActuelle());
        assertEquals(3, dto.totalPages());
        assertEquals(5, dto.totalElements());
        assertFalse(dto.dernierePage());
        assertFalse(dto.premierePage());
    }
}
