package com.campusRoom.api.dto.outPutDto;

import com.campusRoom.api.entity.ReservationType;
import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record ReservationDto(

    Long id,
    ReservationType type,
    LocalDateTime startTime,
    LocalDateTime endTime,
    RoomDto roomDto,
    UserDto userDto,
    String description,
    int maxDurationHours

){}
