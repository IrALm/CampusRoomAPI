package com.campusRoom.api.service.research.impl;

import com.campusRoom.api.dto.outPutDto.RoomDto;
import com.campusRoom.api.dto.researchDto.RoomPageDto;
import com.campusRoom.api.dto.researchDto.RoomSearchDto;
import com.campusRoom.api.entity.Room;
import com.campusRoom.api.mapper.RoomMapper;
import com.campusRoom.api.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DisplayName("Tests unitaires pour RoomSearchServiceImpl")
class RoomSearchServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomMapper roomMapper;

    private RoomSearchServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new RoomSearchServiceImpl(roomRepository, roomMapper);
    }

    @Test
    @DisplayName("search doit renvoyer une page avec contenu")
    void shouldReturnPageWithContent() {
        // Création du DTO de recherche
        RoomSearchDto searchDto = RoomSearchDto.builder()
                .campusName("Main Campus")
                .name("Salle A")
                .location("1er étage")
                .capacityMin(10)
                .capacityMax(50)
                .equipment("Projector")
                .page(0)
                .size(10)
                .sortBy("name")
                .sortDirection(Sort.Direction.ASC)
                .build();

        Pageable pageable = searchDto.toPagination().toPageable();

        // Mock des entités Room
        Room room1 = Room.builder().id(1L).build();
        Room room2 = Room.builder().id(2L).build();

        Page<Room> roomPage = new PageImpl<>(List.of(room1, room2), pageable, 2);

        // Mock des DTO
        RoomDto dto1 = RoomDto.builder().id(1L).build();
        RoomDto dto2 = RoomDto.builder().id(2L).build();

        // Mock du repository et du mapper
        when(roomRepository.findAll(
                (org.springframework.data.jpa.domain.Specification<Room>) any(),
                eq(pageable)
        )).thenReturn(roomPage);
        when(roomMapper.toDTO(room1)).thenReturn(dto1);
        when(roomMapper.toDTO(room2)).thenReturn(dto2);

        // Exécution
        RoomPageDto result = service.search(searchDto);

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
        RoomSearchDto searchDto = RoomSearchDto.builder()
                .campusName("Main Campus")
                .page(0)
                .size(10)
                .sortBy("name")
                .sortDirection(Sort.Direction.ASC)
                .build();

        Pageable pageable = searchDto.toPagination().toPageable();

        Page<Room> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(roomRepository.findAll(
                (org.springframework.data.jpa.domain.Specification<Room>) any(),
                eq(pageable)
        )).thenReturn(emptyPage);

        RoomPageDto result = service.search(searchDto);

        assertNotNull(result);
        assertTrue(result.contenu().isEmpty());
        assertEquals(0, result.totalElements());
        assertEquals(0, result.totalPages());
    }
}