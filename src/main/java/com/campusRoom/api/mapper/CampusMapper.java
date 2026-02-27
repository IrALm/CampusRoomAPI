package com.campusRoom.api.mapper;

import com.campusRoom.api.dto.formDto.CampusFormDto;
import com.campusRoom.api.dto.outPutDto.CampusDto;
import com.campusRoom.api.entity.Campus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = RoomMapper.class)
public interface CampusMapper {

    CampusMapper INSTANCE = Mappers.getMapper(CampusMapper.class);

    Campus toEntity(CampusFormDto campusFormDto);

    @Mapping(source="rooms" , target = "roomDtoList")
    CampusDto toDTO(Campus campus);
}
