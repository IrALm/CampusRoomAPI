package com.campusRoom.api.researchDto;

import com.campusRoom.api.dto.outPutDto.RoomDto;
import com.campusRoom.api.dto.researchDto.RoomPageDto;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoomPageDtoTest {

    @Test
    void testFrom_withNormalPage() {
        List<RoomDto> contenu = List.of(
                RoomDto.builder().id(1L).name("Room A").capacity(10).build(),
                RoomDto.builder().id(2L).name("Room B").capacity(20).build()
        );

        Page<RoomDto> page = new PageImpl<>(contenu, PageRequest.of(0, 2), 5);

        RoomPageDto dto = RoomPageDto.from(page);

        assertEquals(contenu, dto.contenu());
        assertEquals(0, dto.pageActuelle());
        assertEquals(3, dto.totalPages()); // 5 éléments / 2 par page => 3 pages
        assertEquals(5, dto.totalElements());
        assertFalse(dto.dernierePage());
        assertTrue(dto.premierePage());
    }

    @Test
    void testFrom_withEmptyPage() {
        Page<RoomDto> page = Page.empty(PageRequest.of(0, 5));

        RoomPageDto dto = RoomPageDto.from(page);

        assertTrue(dto.contenu().isEmpty());
        assertEquals(0, dto.pageActuelle());
        assertEquals(0, dto.totalPages()); // Forcé à 1 pour cohérence front
        assertEquals(0, dto.totalElements());
        assertTrue(dto.dernierePage());
        assertTrue(dto.premierePage());
    }

    @Test
    void testFrom_withLastPage() {
        List<RoomDto> contenu = List.of(
                RoomDto.builder().id(5L).name("Room E").capacity(15).build()
        );

        Page<RoomDto> page = new PageImpl<>(contenu, PageRequest.of(2, 2), 5);

        RoomPageDto dto = RoomPageDto.from(page);

        assertEquals(contenu, dto.contenu());
        assertEquals(2, dto.pageActuelle());
        assertEquals(3, dto.totalPages());
        assertEquals(5, dto.totalElements());
        assertTrue(dto.dernierePage());
        assertFalse(dto.premierePage());
    }

    @Test
    void testFrom_withFirstPage() {
        List<RoomDto> contenu = List.of(
                RoomDto.builder().id(1L).name("Room A").capacity(10).build()
        );

        Page<RoomDto> page = new PageImpl<>(contenu, PageRequest.of(0, 2), 5);

        RoomPageDto dto = RoomPageDto.from(page);

        assertEquals(contenu, dto.contenu());
        assertEquals(0, dto.pageActuelle());
        assertEquals(3, dto.totalPages());
        assertEquals(5, dto.totalElements());
        assertFalse(dto.dernierePage());
        assertTrue(dto.premierePage());
    }

    @Test
    void testFrom_withMiddlePage() {
        List<RoomDto> contenu = List.of(
                RoomDto.builder().id(3L).name("Room C").capacity(12).build()
        );

        Page<RoomDto> page = new PageImpl<>(contenu, PageRequest.of(1, 2), 5);

        RoomPageDto dto = RoomPageDto.from(page);

        assertEquals(contenu, dto.contenu());
        assertEquals(1, dto.pageActuelle());
        assertEquals(3, dto.totalPages());
        assertEquals(5, dto.totalElements());
        assertFalse(dto.dernierePage());
        assertFalse(dto.premierePage());
    }
}