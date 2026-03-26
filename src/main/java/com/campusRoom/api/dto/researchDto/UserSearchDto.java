package com.campusRoom.api.dto.researchDto;

import com.campusRoom.api.entity.Role;
import com.campusRoom.api.service.research.sort.UserSortEnum;
import lombok.Builder;
import org.springframework.data.domain.Sort;
@Builder
public record UserSearchDto(

        // ─── Filtres ────────────────────────────────────────────────────────
        String firstName,
        String lastName,
        String email,
        Role role,

        // ─── Pagination + tri ───────────────────────────────────────────────
        Integer        page,
        Integer        size,
        String         sortBy,
        Sort.Direction sortDirection
) {
    public Pagination toPagination() {
        return Pagination.of(page, size,
                UserSortEnum.resolveField(sortBy),
                sortDirection);
    }
}
