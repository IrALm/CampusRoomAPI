package com.campusRoom.api.researchDto;

import com.campusRoom.api.dto.researchDto.Pagination;
import com.campusRoom.api.dto.researchDto.RoomSearchDto;
import com.campusRoom.api.service.research.sort.RoomSortEnum;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

class RoomSearchDtoTest {

    @Test
    void testToPagination_withAllNulls_shouldUseDefaults() {
        RoomSearchDto searchDto = RoomSearchDto.builder()
                .campusName("Main Campus") // Obligatoire
                .name(null)
                .location(null)
                .capacityMin(null)
                .capacityMax(null)
                .equipment(null)
                .page(null)
                .size(null)
                .sortBy(null)
                .sortDirection(null)
                .build();

        Pagination pagination = searchDto.toPagination();

        assertEquals(0, pagination.page());
        assertEquals(10, pagination.size());
        assertEquals(RoomSortEnum.ID.getFieldName(), pagination.sortBy());
        assertEquals(Sort.Direction.ASC, pagination.direction());
    }

    @Test
    void testToPagination_withCustomValues() {
        RoomSearchDto searchDto = RoomSearchDto.builder()
                .campusName("Campus A")
                .name("Room 101")
                .location("Building B")
                .capacityMin(5)
                .capacityMax(50)
                .equipment("Projector")
                .page(2)
                .size(5)
                .sortBy("name")
                .sortDirection(Sort.Direction.DESC)
                .build();

        Pagination pagination = searchDto.toPagination();

        assertEquals(2, pagination.page());
        assertEquals(5, pagination.size());
        assertEquals("name", pagination.sortBy());
        assertEquals(Sort.Direction.DESC, pagination.direction());
    }

    @Test
    void testFilters_arePreserved() {
        RoomSearchDto searchDto = RoomSearchDto.builder()
                .campusName("Campus B")
                .name("Room 202")
                .location("Building C")
                .capacityMin(10)
                .capacityMax(100)
                .equipment("Whiteboard")
                .page(1)
                .size(20)
                .sortBy("capacity")
                .sortDirection(Sort.Direction.ASC)
                .build();

        assertEquals("Campus B", searchDto.campusName());
        assertEquals("Room 202", searchDto.name());
        assertEquals("Building C", searchDto.location());
        assertEquals(10, searchDto.capacityMin());
        assertEquals(100, searchDto.capacityMax());
        assertEquals("Whiteboard", searchDto.equipment());
        assertEquals(1, searchDto.page());
        assertEquals(20, searchDto.size());
        assertEquals("capacity", searchDto.sortBy());
        assertEquals(Sort.Direction.ASC, searchDto.sortDirection());
    }

    @Test
    void testSortBy_invalidField_shouldResolveToDefault() {
        RoomSearchDto searchDto = RoomSearchDto.builder()
                .campusName("Campus X")
                .page(null)
                .size(null)
                .sortBy("invalidField")
                .sortDirection(null)
                .build();

        Pagination pagination = searchDto.toPagination();

        assertEquals(RoomSortEnum.ID.getFieldName(), pagination.sortBy());
        assertEquals(Sort.Direction.ASC, pagination.direction());
    }
}
