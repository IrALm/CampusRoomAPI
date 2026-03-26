package com.campusRoom.api.dto.researchDto;

import com.campusRoom.api.service.research.sort.CampusSortEnum;
import lombok.Builder;
import org.springframework.data.domain.Sort;

@Builder
public record CampusSearchDto(

        // ─── Filtres ────────────────────────────────────────────────────────
        String name,
        String city,

        // ─── Pagination + tri ───────────────────────────────────────────────
        Integer        page,
        Integer        size,
        String         sortBy,
        Sort.Direction sortDirection
) {
    public Pagination toPagination() {
        return Pagination.of(page, size,
                CampusSortEnum.resolveField(sortBy),
                sortDirection);
    }
}
