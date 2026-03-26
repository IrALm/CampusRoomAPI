package com.campusRoom.api.dto.researchDto;

import com.campusRoom.api.service.research.sort.RoomSortEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.springframework.data.domain.Sort;
@Builder
public record RoomSearchDto(

        // ─── Filtres ────────────────────────────────────────────────────────
        @NotBlank(message = "Le nom du campus est obligatoire")
        String  campusName,

        String  name,
        String  location,
        Integer capacityMin,
        Integer capacityMax,
        String  equipment,     // recherche si la salle contient cet équipement

        // ─── Pagination + tri ───────────────────────────────────────────────
        Integer        page,
        Integer        size,
        String         sortBy,
        Sort.Direction sortDirection
) {
    public Pagination toPagination() {
        return Pagination.of(page, size,
                RoomSortEnum.resolveField(sortBy),
                sortDirection);
    }
}
