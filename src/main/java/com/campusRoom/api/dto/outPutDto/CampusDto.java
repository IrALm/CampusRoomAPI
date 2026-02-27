package com.campusRoom.api.dto.outPutDto;

import java.util.List;

public record CampusDto(

        Long id,
        String name,
        String city,
        List<RoomDto> roomDtoList
) {
}
