package com.campusRoom.api.dto.researchDto;

import com.campusRoom.api.entity.ReservationType;
import com.campusRoom.api.service.research.sort.ReservationSortEnum;
import lombok.Builder;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

@Builder
public record ReservationSearchDto(

        // ─── Filtres ────────────────────────────────────────────────────────
        ReservationType type,
        Long            roomId,
        Long            userId,
        LocalDateTime   startTime,
        LocalDateTime   endTime,

        // ─── Pagination + tri ───────────────────────────────────────────────
        Integer        page,
        Integer        size,
        String         sortBy,
        Sort.Direction sortDirection
) {
    public Pagination toPagination() {
        return Pagination.of(page, size, ReservationSortEnum.resolveField(sortBy), sortDirection);
    }
}
