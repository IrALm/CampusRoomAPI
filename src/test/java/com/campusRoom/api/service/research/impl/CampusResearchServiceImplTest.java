package com.campusRoom.api.service.research.impl;

import com.campusRoom.api.dto.outPutDto.CampusDto;
import com.campusRoom.api.dto.researchDto.CampusPageDto;
import com.campusRoom.api.dto.researchDto.CampusSearchDto;
import com.campusRoom.api.entity.Campus;
import com.campusRoom.api.mapper.CampusMapper;
import com.campusRoom.api.repository.CampusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Tests unitaires pour CampusResearchServiceImpl")
class CampusResearchServiceImplTest {

    private CampusRepository campusRepository;
    private CampusMapper campusMapper;
    private CampusResearchServiceImpl service;

    @BeforeEach
    void setUp() {
        campusRepository = mock(CampusRepository.class);
        campusMapper = mock(CampusMapper.class);
        service = new CampusResearchServiceImpl(campusRepository, campusMapper);
    }

    @Test
    @DisplayName("search doit renvoyer une page vide si aucun campus")
    void shouldReturnEmptyPage() {
        CampusSearchDto searchDto = CampusSearchDto.builder()
                .name("Main Campus")
                .page(0)
                .size(10)
                .sortBy("name")
                .sortDirection(Sort.Direction.ASC)
                .build();

        Pageable pageable = searchDto.toPagination().toPageable();

        Page<Campus> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        // Cast explicite pour résoudre l'ambiguïté de Mockito
        when(campusRepository.findAll(
                (org.springframework.data.jpa.domain.Specification<Campus>) any(),
                eq(pageable)
        )).thenReturn(emptyPage);

        CampusPageDto result = service.search(searchDto);

        assertNotNull(result);
        assertTrue(result.contenu().isEmpty());
        assertEquals(0, result.totalElements());
        assertEquals(0, result.totalPages());
    }

    @Test
    @DisplayName("search doit renvoyer une page avec contenu")
    void shouldReturnPageWithContent() {
        CampusSearchDto searchDto = CampusSearchDto.builder()
                .name("Main Campus")
                .page(0)
                .size(10)
                .sortBy("name")
                .sortDirection(Sort.Direction.ASC)
                .build();

        Pageable pageable = searchDto.toPagination().toPageable();

        // Campus mockés
        Campus campus1 = Campus.builder().id(1L).name("Main Campus A").build();
        Campus campus2 = Campus.builder().id(2L).name("Main Campus B").build();

        CampusDto campusDto1 = CampusDto.builder()
                .id(1L)
                .name("Main Campus A")
                .city("City A")
                .roomDtoList(List.of()) // vide pour le test
                .build();

        CampusDto campusDto2 = CampusDto.builder()
                .id(2L)
                .name("Main Campus B")
                .city("City B")
                .roomDtoList(List.of())
                .build();

        Page<Campus> campusPage = new PageImpl<>(List.of(campus1, campus2), pageable, 2);

        when(campusRepository.findAll(
                (org.springframework.data.jpa.domain.Specification<Campus>) any(),
                eq(pageable)
        )).thenReturn(campusPage);

        // Mapper mocké
        when(campusMapper.toDTO(campus1)).thenReturn(campusDto1);
        when(campusMapper.toDTO(campus2)).thenReturn(campusDto2);

        CampusPageDto result = service.search(searchDto);

        assertNotNull(result);
        assertEquals(2, result.contenu().size());
        assertEquals("Main Campus A", result.contenu().get(0).name());
        assertEquals("Main Campus B", result.contenu().get(1).name());
        assertEquals(2, result.totalElements());
    }

    @Test
    @DisplayName("search doit utiliser correctement la specification")
    void shouldUseSpecification() {
        CampusSearchDto searchDto = CampusSearchDto.builder()
                .name("Main Campus")
                .page(0)
                .size(10)
                .sortBy("name")
                .sortDirection(Sort.Direction.ASC)
                .build();

        Pageable pageable = searchDto.toPagination().toPageable();

        Page<Campus> campusPage = new PageImpl<>(List.of(), pageable, 0);

        ArgumentCaptor<org.springframework.data.jpa.domain.Specification<Campus>> specCaptor =
                ArgumentCaptor.forClass(org.springframework.data.jpa.domain.Specification.class);

        when(campusRepository.findAll(specCaptor.capture(), eq(pageable))).thenReturn(campusPage);

        service.search(searchDto);

        assertNotNull(specCaptor.getValue(), "La Specification ne doit pas être null");
    }
}