package com.campusRoom.api.mapper;

import com.campusRoom.api.dto.formDto.UserFormDto;
import com.campusRoom.api.dto.outPutDto.UserDto;
import com.campusRoom.api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {
        ReservationMapper.class
})
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

     User toEntity(UserFormDto userFormDto);

     @Mapping(source = "reservations" , target = "reservationDtoList")
     UserDto toDTO(User user);
}
