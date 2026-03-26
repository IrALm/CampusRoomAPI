package com.campusRoom.api.dto.researchDto;

import com.campusRoom.api.dto.outPutDto.UserDto;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record UserPageDto(
        List<UserDto> contenu,
        int           pageActuelle,
        int           totalPages,
        long          totalElements,
        boolean       dernierePage,
        boolean       premierePage
) {
    public static UserPageDto from(Page<UserDto> page) {
        return new UserPageDto(
                page.getContent(),
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isLast(),
                page.isFirst()
        );
    }
}
