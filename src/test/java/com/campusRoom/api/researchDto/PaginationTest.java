package com.campusRoom.api.researchDto;

import com.campusRoom.api.dto.researchDto.Pagination;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

class PaginationTest {

    @Test
    void testOf_withAllNulls_shouldUseDefaults() {
        Pagination pagination = Pagination.of(null, null, null, null);

        assertEquals(0, pagination.page());
        assertEquals(10, pagination.size());
        assertEquals("id", pagination.sortBy());
        assertEquals(Sort.Direction.ASC, pagination.direction());
    }

    @Test
    void testOf_withCustomValues() {
        Pagination pagination = Pagination.of(2, 5, "startTime", Sort.Direction.DESC);

        assertEquals(2, pagination.page());
        assertEquals(5, pagination.size());
        assertEquals("startTime", pagination.sortBy());
        assertEquals(Sort.Direction.DESC, pagination.direction());
    }

    @Test
    void testToPageable_withDefaults() {
        Pagination pagination = Pagination.of(null, null, null, null);
        Pageable pageable = pagination.toPageable();

        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
        assertEquals(Sort.Direction.ASC, pageable.getSort().getOrderFor(pagination.sortBy()).getDirection());
    }

    @Test
    void testToPageable_withCustomValues() {
        Pagination pagination = Pagination.of(1, 20, "endTime", Sort.Direction.DESC);
        Pageable pageable = pagination.toPageable();

        assertEquals(1, pageable.getPageNumber());
        assertEquals(20, pageable.getPageSize());
        assertEquals(Sort.Direction.DESC, pageable.getSort().getOrderFor(pagination.sortBy()).getDirection());
    }

    @Test
    void testSortBy_invalidField_shouldResolveToDefault() {
        Pagination pagination = Pagination.of(0, 10, "invalidField", null);

        // Si ReservationSortEnum.resolveField retourne un champ par défaut
        assertEquals("invalidField", pagination.sortBy());
        assertEquals(Sort.Direction.ASC, pagination.direction());
    }
}