package com.campusRoom.api.researchDto;

import com.campusRoom.api.dto.outPutDto.CampusDto;
import com.campusRoom.api.dto.researchDto.CampusPageDto;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CampusPageDtoTest {

    @Test
    void testFrom_withNormalPage() {
        List<CampusDto> contenu = List.of(
                CampusDto.builder().id(1L).name("Campus A").build(),
                CampusDto.builder().id(2L).name("Campus B").build()
        );
        Page<CampusDto> page = new PageImpl<>(contenu, PageRequest.of(0, 2), 5);

        CampusPageDto dto = CampusPageDto.from(page);

        assertEquals(contenu, dto.contenu());
        assertEquals(0, dto.pageActuelle());
        assertEquals(3, dto.totalPages());
        assertEquals(5, dto.totalElements());
        assertFalse(dto.dernierePage());
        assertTrue(dto.premierePage());
    }

    @Test
    void testFrom_withEmptyPage() {
        Page<CampusDto> page = Page.empty(PageRequest.of(0, 5));

        CampusPageDto dto = CampusPageDto.from(page);

        assertTrue(dto.contenu().isEmpty());
        assertEquals(0, dto.pageActuelle());
        assertEquals(0, dto.totalPages());
        assertEquals(0, dto.totalElements());
        assertTrue(dto.dernierePage());
        assertTrue(dto.premierePage());
    }

    @Test
    void testFrom_withLastPage() {
        List<CampusDto> contenu = List.of(
                CampusDto.builder().id(3L).name("Campus C").build()
        );
        Page<CampusDto> page = new PageImpl<>(contenu, PageRequest.of(2, 2), 5);

        CampusPageDto dto = CampusPageDto.from(page);

        assertEquals(contenu, dto.contenu());
        assertEquals(2, dto.pageActuelle());
        assertEquals(3, dto.totalPages());
        assertEquals(5, dto.totalElements());
        assertTrue(dto.dernierePage());
        assertFalse(dto.premierePage());
    }

    @Test
    void testFrom_withMiddlePage() {
        List<CampusDto> contenu = List.of(
                CampusDto.builder().id(2L).name("Campus B").build()
        );
        Page<CampusDto> page = new PageImpl<>(contenu, PageRequest.of(1, 2), 5);

        CampusPageDto dto = CampusPageDto.from(page);

        assertEquals(contenu, dto.contenu());
        assertEquals(1, dto.pageActuelle());
        assertEquals(3, dto.totalPages());
        assertEquals(5, dto.totalElements());
        assertFalse(dto.dernierePage());
        assertFalse(dto.premierePage());
    }
}