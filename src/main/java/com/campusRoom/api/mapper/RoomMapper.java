package com.campusRoom.api.mapper;

import com.campusRoom.api.dto.outPutDto.RoomDto;
import com.campusRoom.api.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomMapper {


    @Mapping(source = "campus" , target = "campusDto")
    @Mapping(source = "reservations" , target = "reservationDtoList")
    RoomDto toDTO(Room room);
}
