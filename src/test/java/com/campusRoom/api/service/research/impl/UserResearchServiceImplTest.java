package com.campusRoom.api.service.research.impl;

import com.campusRoom.api.dto.outPutDto.UserDto;
import com.campusRoom.api.dto.researchDto.UserPageDto;
import com.campusRoom.api.dto.researchDto.UserSearchDto;
import com.campusRoom.api.entity.Role;
import com.campusRoom.api.entity.User;
import com.campusRoom.api.mapper.UserMapper;
import com.campusRoom.api.repository.UserRepository;
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

@DisplayName("Tests unitaires pour UserResearchServiceImpl")
class UserResearchServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private UserResearchServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new UserResearchServiceImpl(userRepository, userMapper);
    }

    @Test
    @DisplayName("search doit renvoyer une page avec contenu")
    void shouldReturnPageWithContent() {
        // Création du DTO de recherche
        UserSearchDto searchDto = UserSearchDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .role(Role.STUDENT)
                .page(0)
                .size(10)
                .sortBy("firstName")
                .sortDirection(Sort.Direction.ASC)
                .build();

        Pageable pageable = searchDto.toPagination().toPageable();

        // Mock des entités User
        User user1 = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();

        Page<User> userPage = new PageImpl<>(List.of(user1, user2), pageable, 2);

        // Mock des DTO
        UserDto dto1 = UserDto.builder().id(1L).build();
        UserDto dto2 = UserDto.builder().id(2L).build();

        // Mock du repository et du mapper
        when(userRepository.findAll(
                (org.springframework.data.jpa.domain.Specification<User>) any(),
                eq(pageable)
        )).thenReturn(userPage);
        when(userMapper.toDTO(user1)).thenReturn(dto1);
        when(userMapper.toDTO(user2)).thenReturn(dto2);

        // Exécution
        UserPageDto result = service.search(searchDto);

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
        UserSearchDto searchDto = UserSearchDto.builder()
                .page(0)
                .size(10)
                .sortBy("firstName")
                .sortDirection(Sort.Direction.ASC)
                .build();

        Pageable pageable = searchDto.toPagination().toPageable();

        Page<User> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(userRepository.findAll(
                (org.springframework.data.jpa.domain.Specification<User>) any(),
                eq(pageable)
        )).thenReturn(emptyPage);

        UserPageDto result = service.search(searchDto);

        assertNotNull(result);
        assertTrue(result.contenu().isEmpty());
        assertEquals(0, result.totalElements());
        assertEquals(0, result.totalPages());
    }
}