package com.campusRoom.api.mapper;

import com.campusRoom.api.dto.outPutDto.UserDto;
import com.campusRoom.api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

     @Mapping(source = "reservations" , target = "reservationDtoList")
     UserDto toDTO(User user);
}
