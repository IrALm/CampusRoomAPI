package com.campusRoom.api.dto.outPutDto;

import lombok.Builder;

import java.util.List;

@Builder
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
