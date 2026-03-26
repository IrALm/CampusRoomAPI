package com.campusRoom.api.dto.outPutDto;

import lombok.Builder;

import java.util.List;

@Builder
public record CampusDto(

        Long id,
        String name,
        String city,
        List<RoomDto> roomDtoList
) {
}
