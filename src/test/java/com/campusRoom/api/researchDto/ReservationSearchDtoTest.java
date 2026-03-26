package com.campusRoom.api.researchDto;

import com.campusRoom.api.dto.researchDto.Pagination;
import com.campusRoom.api.dto.researchDto.ReservationSearchDto;
import com.campusRoom.api.entity.ReservationType;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservationSearchDtoTest {

    @Test
    void testToPagination_withAllNulls_shouldUseDefaults() {
        ReservationSearchDto searchDto = ReservationSearchDto.builder()
                .type(null)
                .roomId(null)
                .userId(null)
                .startTime(null)
                .endTime(null)
                .page(null)
                .size(null)
                .sortBy(null)
                .sortDirection(null)
                .build();

        Pagination pagination = searchDto.toPagination();

        assertEquals(0, pagination.page());
        assertEquals(10, pagination.size());
        assertEquals("id" , pagination.sortBy());
        assertEquals(Sort.Direction.ASC, pagination.direction());
    }

    @Test
    void testToPagination_withCustomValues() {
        ReservationSearchDto searchDto = ReservationSearchDto.builder()
                .type(ReservationType.MEETING)
                .roomId(5L)
                .userId(2L)
                .startTime(LocalDateTime.of(2026, 3, 26, 9, 0))
                .endTime(LocalDateTime.of(2026, 3, 26, 10, 0))
                .page(1)
                .size(5)
                .sortBy("startTime")
                .sortDirection(Sort.Direction.DESC)
                .build();

        Pagination pagination = searchDto.toPagination();

        assertEquals(1, pagination.page());
        assertEquals(5, pagination.size());
        assertEquals("startTime", pagination.sortBy());
        assertEquals(Sort.Direction.DESC, pagination.direction());
    }

    @Test
    void testFilters_arePreserved() {
        LocalDateTime start = LocalDateTime.of(2026, 3, 26, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 3, 26, 10, 0);

        ReservationSearchDto searchDto = ReservationSearchDto.builder()
                .type(ReservationType.MEETING)
                .roomId(3L)
                .userId(7L)
                .startTime(start)
                .endTime(end)
                .page(0)
                .size(10)
                .sortBy("endTime")
                .sortDirection(Sort.Direction.ASC)
                .build();

        assertEquals(ReservationType.MEETING, searchDto.type());
        assertEquals(3L, searchDto.roomId());
        assertEquals(7L, searchDto.userId());
        assertEquals(start, searchDto.startTime());
        assertEquals(end, searchDto.endTime());
        assertEquals(0, searchDto.page());
        assertEquals(10, searchDto.size());
        assertEquals("endTime", searchDto.sortBy());
        assertEquals(Sort.Direction.ASC, searchDto.sortDirection());
    }
}