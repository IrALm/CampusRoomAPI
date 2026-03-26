package com.campusRoom.api.dto.outPutDto;

import com.campusRoom.api.entity.Role;
import lombok.Builder;

import java.util.List;

@Builder
public record UserDto(

        Long id,
        String firstName,
        String lastName,
        String email,
        Role role,
        List<ReservationDto> reservationDtoList
) {
}
