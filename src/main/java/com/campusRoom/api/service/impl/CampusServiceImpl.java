package com.campusRoom.api.service.impl;

import com.campusRoom.api.dto.formDto.CampusFormDto;
import com.campusRoom.api.dto.outPutDto.CampusDto;
import com.campusRoom.api.entity.Campus;
import com.campusRoom.api.exception.CampusRoomBusinessException;
import com.campusRoom.api.mapper.CampusMapper;
import com.campusRoom.api.repository.CampusRepository;
import com.campusRoom.api.service.CampusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampusServiceImpl implements CampusService {

    private final CampusRepository campusRepository;

    @Override
    public CampusDto getCampusByName(String name){

        Campus campus = campusRepository.findByName(name)
                .orElseThrow(() ->
                        new CampusRoomBusinessException("Aucun campus n'existe pour ce nom." , HttpStatus.NOT_FOUND));

        return CampusMapper.INSTANCE.toDTO(campus);
    }

    @Override
    public boolean verifyIfCampusExist(String name , String city){

        return campusRepository.existsByNameAndCity(name , city);
    }

    @Override
    public CampusDto createCampus(CampusFormDto campusFormDto){

        boolean campusExist = verifyIfCampusExist(campusFormDto.name() , campusFormDto.city());
        if(campusExist){
            throw new CampusRoomBusinessException("Un campus existe déjà sous le nom : "
                    + campusFormDto.name() + "dans la ville : "
                    + campusFormDto.city() , HttpStatus.CONFLICT);
        }

        Campus campus = Campus.builder()
                .name(campusFormDto.name())
                .city(campusFormDto.city())
                .build();

        campusRepository.save(campus);

        return CampusMapper.INSTANCE.toDTO(campus);
    }
}
