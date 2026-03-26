package com.campusRoom.api.researchDto;

import com.campusRoom.api.dto.researchDto.Pagination;
import com.campusRoom.api.dto.researchDto.UserSearchDto;
import com.campusRoom.api.entity.Role;
import com.campusRoom.api.service.research.sort.UserSortEnum;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

class UserSearchDtoTest {

    @Test
    void testToPagination_withDefaults() {
        UserSearchDto searchDto = UserSearchDto.builder()
                .firstName(null)
                .lastName(null)
                .email(null)
                .role(null)
                .page(null)
                .size(null)
                .sortBy(null)
                .sortDirection(null)
                .build();

        Pagination pagination = searchDto.toPagination();

        assertEquals(0, pagination.page());
        assertEquals(10, pagination.size());
        assertEquals(UserSortEnum.ID.getFieldName(), pagination.sortBy()); // valeur par défaut
        assertEquals(Sort.Direction.ASC, pagination.direction());
    }

    @Test
    void testToPagination_withCustomValues() {
        UserSearchDto searchDto = UserSearchDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .role(Role.STUDENT)
                .page(2)
                .size(5)
                .sortBy("email")
                .sortDirection(Sort.Direction.DESC)
                .build();

        Pagination pagination = searchDto.toPagination();

        assertEquals(2, pagination.page());
        assertEquals(5, pagination.size());
        assertEquals("email", pagination.sortBy());
        assertEquals(Sort.Direction.DESC, pagination.direction());
    }

    @Test
    void testFilters_arePreserved() {
        UserSearchDto searchDto = UserSearchDto.builder()
                .firstName("Alice")
                .lastName("Smith")
                .email("alice.smith@example.com")
                .role(Role.STUDENT)
                .page(1)
                .size(20)
                .sortBy("lastName")
                .sortDirection(Sort.Direction.ASC)
                .build();

        assertEquals("Alice", searchDto.firstName());
        assertEquals("Smith", searchDto.lastName());
        assertEquals("alice.smith@example.com", searchDto.email());
        assertEquals(Role.STUDENT, searchDto.role());
        assertEquals(1, searchDto.page());
        assertEquals(20, searchDto.size());
        assertEquals("lastName", searchDto.sortBy());
        assertEquals(Sort.Direction.ASC, searchDto.sortDirection());
    }

    @Test
    void testSortBy_invalidField_shouldResolveToDefault() {
        UserSearchDto searchDto = UserSearchDto.builder()
                .firstName(null)
                .lastName(null)
                .email(null)
                .role(null)
                .page(null)
                .size(null)
                .sortBy("invalidField")
                .sortDirection(null)
                .build();

        Pagination pagination = searchDto.toPagination();

        assertEquals(UserSortEnum.ID.getFieldName(), pagination.sortBy());
        assertEquals(Sort.Direction.ASC, pagination.direction());
    }
}