package com.campusRoom.api.dto.researchDto;

import com.campusRoom.api.dto.outPutDto.RoomDto;
import org.springframework.data.domain.Page;

import java.util.List;

public record RoomPageDto(
        List<RoomDto> contenu,
        int           pageActuelle,
        int           totalPages,
        long          totalElements,
        boolean       dernierePage,
        boolean       premierePage
) {
    public static RoomPageDto from(Page<RoomDto> page) {
        return new RoomPageDto(
                page.getContent(),
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isLast(),
                page.isFirst()
        );
    }
}
