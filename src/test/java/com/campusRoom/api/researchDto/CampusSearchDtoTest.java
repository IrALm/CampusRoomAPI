package com.campusRoom.api.researchDto;

import com.campusRoom.api.dto.researchDto.CampusSearchDto;
import com.campusRoom.api.dto.researchDto.Pagination;
import com.campusRoom.api.service.research.sort.CampusSortEnum;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

class CampusSearchDtoTest {

    @Test
    void testToPagination_withAllNulls_shouldUseDefaults() {
        CampusSearchDto searchDto = CampusSearchDto.builder()
                .name(null)
                .city(null)
                .page(null)
                .size(null)
                .sortBy(null)
                .sortDirection(null)
                .build();

        Pagination pagination = searchDto.toPagination();

        assertEquals(0, pagination.page());
        assertEquals(10, pagination.size());
        assertEquals(CampusSortEnum.ID.getFieldName(), pagination.sortBy());
        assertEquals(Sort.Direction.ASC, pagination.direction());
    }

    @Test
    void testToPagination_withCustomValues() {
        CampusSearchDto searchDto = CampusSearchDto.builder()
                .name("Campus A")
                .city("Paris")
                .page(2)
                .size(5)
                .sortBy("id")
                .sortDirection(Sort.Direction.DESC)
                .build();

        Pagination pagination = searchDto.toPagination();

        assertEquals(2, pagination.page());
        assertEquals(5, pagination.size());
        assertEquals("id", pagination.sortBy());
        assertEquals(Sort.Direction.DESC, pagination.direction());
    }

    @Test
    void testFilters_arePreserved() {
        CampusSearchDto searchDto = CampusSearchDto.builder()
                .name("Campus B")
                .city("Lyon")
                .page(1)
                .size(20)
                .sortBy("city")
                .sortDirection(Sort.Direction.ASC)
                .build();

        assertEquals("Campus B", searchDto.name());
        assertEquals("Lyon", searchDto.city());
        assertEquals(1, searchDto.page());
        assertEquals(20, searchDto.size());
        assertEquals("city", searchDto.sortBy());
        assertEquals(Sort.Direction.ASC, searchDto.sortDirection());
    }

    @Test
    void testSortBy_invalidField_shouldResolveToDefault() {
        CampusSearchDto searchDto = CampusSearchDto.builder()
                .name(null)
                .city(null)
                .page(null)
                .size(null)
                .sortBy("invalidField")
                .sortDirection(null)
                .build();

        Pagination pagination = searchDto.toPagination();

        assertEquals(CampusSortEnum.ID.getFieldName(), pagination.sortBy());
        assertEquals(Sort.Direction.ASC, pagination.direction());
    }
}