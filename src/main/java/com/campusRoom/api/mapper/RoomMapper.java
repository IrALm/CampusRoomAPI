package com.campusRoom.api.mapper;

import com.campusRoom.api.dto.formDto.RoomFormDto;
import com.campusRoom.api.dto.outPutDto.RoomDto;
import com.campusRoom.api.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {CampusMapper.class,
        ReservationMapper.class
})
public interface RoomMapper {

    RoomMapper INSTANCE = Mappers.getMapper(RoomMapper.class);

    Room toEntity(RoomFormDto roomFormDto);

    @Mapping(source = "campus" , target = "campusDto")
    @Mapping(source = "reservations" , target = "reservationDtoList")
    RoomDto toDTO(Room room);
}
