package com.campusRoom.api.dto.outPutDto;

import com.campusRoom.api.entity.ReservationType;

import java.time.LocalDateTime;

public record ReservationDto(

        Long id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        ReservationType type,
        UserDto userDto,
        RoomDto roomDto
) {
}
