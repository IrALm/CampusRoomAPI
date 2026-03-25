package com.campusRoom.api.dto.researchDto;

import com.campusRoom.api.service.research.sort.ReservationSortEnum;
import lombok.Builder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Builder
public record Pagination(
        int page,
        int size,
        String sortBy,
        Sort.Direction direction
) {
    // Valeurs par défaut si non renseignées
    public static Pagination of(Integer page, Integer size,
                                String sortBy, Sort.Direction direction) {
        return Pagination.builder()
                .page(page != null ? page : 0)
                .size(size != null ? size : 10)
                .sortBy(ReservationSortEnum.resolveField(sortBy))
                .direction(direction != null ? direction : Sort.Direction.ASC)
                .build();
    }

    public Pageable toPageable() {
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }
}
