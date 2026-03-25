package com.campusRoom.api.dto.researchDto;

import com.campusRoom.api.entity.ReservationType;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

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
        return Pagination.of(page, size, sortBy, sortDirection);
    }
}
