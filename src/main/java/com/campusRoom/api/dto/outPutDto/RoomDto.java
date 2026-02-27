package com.campusRoom.api.dto.outPutDto;

import java.util.List;

public record RoomDto(

        Long id,
        String name,
        Integer capacity,
        String location,
        List<String> equipment,
        CampusDto campusDto,
        List<ReservationDto> reservationDtoList
) {
}
