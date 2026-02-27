package com.campusRoom.api.mapper;

import com.campusRoom.api.dto.outPutDto.CampusDto;
import com.campusRoom.api.entity.Campus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CampusMapper {

    @Mapping(source ="roomDtoList" , target = "rooms")
    Campus toEntity(CampusDto campusDto);

    @Mapping(source="rooms" , target = "roomDtoList")
    CampusDto toDTO(Campus campus);
}
