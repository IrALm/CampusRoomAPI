package com.campusRoom.api.researchDto;

import com.campusRoom.api.dto.outPutDto.UserDto;
import com.campusRoom.api.dto.researchDto.UserPageDto;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserPageDtoTest {

    @Test
    void testFrom_withNormalPage() {
        List<UserDto> contenu = List.of(
                UserDto.builder().id(1L).firstName("user1").email("user1@example.com").build(),
                UserDto.builder().id(2L).firstName("user2").email("user2@example.com").build()
        );

        Page<UserDto> page = new PageImpl<>(contenu, PageRequest.of(0, 2), 5);

        UserPageDto dto = UserPageDto.from(page);

        assertEquals(contenu, dto.contenu());
        assertEquals(0, dto.pageActuelle());
        assertEquals(3, dto.totalPages()); // 5 éléments / 2 par page => 3 pages
        assertEquals(5, dto.totalElements());
        assertFalse(dto.dernierePage());
        assertTrue(dto.premierePage());
    }

    @Test
    void testFrom_withEmptyPage() {
        Page<UserDto> page = Page.empty(PageRequest.of(0, 5));

        UserPageDto dto = UserPageDto.from(page);

        assertTrue(dto.contenu().isEmpty());
        assertEquals(0, dto.pageActuelle());
        assertEquals(0, dto.totalPages()); // Forcé à 1 pour front cohérent
        assertEquals(0, dto.totalElements());
        assertTrue(dto.dernierePage());
        assertTrue(dto.premierePage());
    }

    @Test
    void testFrom_withLastPage() {
        List<UserDto> contenu = List.of(
                UserDto.builder().id(5L).firstName("user5").email("user5@example.com").build()
        );

        Page<UserDto> page = new PageImpl<>(contenu, PageRequest.of(2, 2), 5);

        UserPageDto dto = UserPageDto.from(page);

        assertEquals(contenu, dto.contenu());
        assertEquals(2, dto.pageActuelle());
        assertEquals(3, dto.totalPages());
        assertEquals(5, dto.totalElements());
        assertTrue(dto.dernierePage());
        assertFalse(dto.premierePage());
    }

    @Test
    void testFrom_withFirstPage() {
        List<UserDto> contenu = List.of(
                UserDto.builder().id(1L).firstName("user1").email("user1@example.com").build()
        );

        Page<UserDto> page = new PageImpl<>(contenu, PageRequest.of(0, 2), 5);

        UserPageDto dto = UserPageDto.from(page);

        assertEquals(contenu, dto.contenu());
        assertEquals(0, dto.pageActuelle());
        assertEquals(3, dto.totalPages());
        assertEquals(5, dto.totalElements());
        assertFalse(dto.dernierePage());
        assertTrue(dto.premierePage());
    }

    @Test
    void testFrom_withMiddlePage() {
        List<UserDto> contenu = List.of(
                UserDto.builder().id(3L).firstName("user3").email("user3@example.com").build()
        );

        Page<UserDto> page = new PageImpl<>(contenu, PageRequest.of(1, 2), 5);

        UserPageDto dto = UserPageDto.from(page);

        assertEquals(contenu, dto.contenu());
        assertEquals(1, dto.pageActuelle());
        assertEquals(3, dto.totalPages());
        assertEquals(5, dto.totalElements());
        assertFalse(dto.dernierePage());
        assertFalse(dto.premierePage());
    }
}