package com.campusRoom.api.mapper;

import com.campusRoom.api.dto.formDto.ReservationFormDto;
import com.campusRoom.api.dto.outPutDto.ReservationDto;
import com.campusRoom.api.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
        UserMapper.class,
        RoomMapper.class
})
public interface ReservationMapper {

    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

    Reservation toEntity(ReservationFormDto reservationFormDto);

    @Mapping(source ="user" , target = "userDto")
    @Mapping(source ="room" , target = "roomDto")
    ReservationDto toDTO(Reservation reservation);
}
