package com.campusRoom.api.dto.outPutDto;

import com.campusRoom.api.entity.Role;

import java.util.List;

public record UserDto(

        Long id,
        String firstName,
        String lastName,
        String email,
        Role role,
        List<ReservationDto> reservationDtoList
) {
}
