package com.campusRoom.api.dto.researchDto;

import com.campusRoom.api.dto.outPutDto.CampusDto;
import org.springframework.data.domain.Page;

import java.util.List;

// DTO de page
public record CampusPageDto(
        List<CampusDto> contenu,
        int             pageActuelle,
        int             totalPages,
        long            totalElements,
        boolean         dernierePage,
        boolean         premierePage
) {
    public static CampusPageDto from(Page<CampusDto> page) {
        return new CampusPageDto(
                page.getContent(),
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isLast(),
                page.isFirst()
        );
    }
}
