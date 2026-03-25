package com.campusRoom.api.dto.researchDto;

import com.campusRoom.api.dto.outPutDto.ReservationDto;
import org.springframework.data.domain.Page;

import java.util.List;

public record ReservationPageDto(
        List<ReservationDto> contenu,
        int                  pageActuelle,
        int                  totalPages,
        long                 totalElements,
        boolean              dernierePage,
        boolean              premierePage
) {
    public static ReservationPageDto from(Page<ReservationDto> page) {
        return new ReservationPageDto(
                page.getContent(),
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isLast(),
                page.isFirst()
        );
    }
}
