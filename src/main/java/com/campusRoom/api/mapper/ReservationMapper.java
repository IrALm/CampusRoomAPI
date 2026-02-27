package com.campusRoom.api.mapper;

import com.campusRoom.api.dto.outPutDto.ReservationDto;
import com.campusRoom.api.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {


    @Mapping(source ="user" , target = "userDto")
    @Mapping(source ="room" , target = "roomDto")
    ReservationDto toDTO(Reservation reservation);
}
