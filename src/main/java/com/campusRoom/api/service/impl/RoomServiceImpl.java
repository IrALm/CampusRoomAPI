package com.campusRoom.api.service.impl;

import com.campusRoom.api.dto.formDto.RoomFormDto;
import com.campusRoom.api.dto.outPutDto.CampusDto;
import com.campusRoom.api.dto.outPutDto.RoomDto;
import com.campusRoom.api.entity.Campus;
import com.campusRoom.api.entity.Room;
import com.campusRoom.api.exception.CampusRoomBusinessException;
import com.campusRoom.api.mapper.CampusMapper;
import com.campusRoom.api.mapper.RoomMapper;
import com.campusRoom.api.repository.RoomRepository;
import com.campusRoom.api.service.CampusService;
import com.campusRoom.api.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final CampusService campusService;
    private final CampusMapper campusMapper;
    private final RoomMapper roomMapper;

    @Override
    public boolean verifyIfRoomExist(String name){

        return roomRepository.existsByName(name);
    }

    @Override
    public RoomDto getByRoomName(String name){

        Room room = roomRepository.findByName(name)
                .orElseThrow(() ->
                        new CampusRoomBusinessException("Aucune salle n'existe avec le nom : "
                                + name , HttpStatus.NOT_FOUND));

        return roomMapper.toDTO(room);
    }

    @Override
    public void createRoom(RoomFormDto roomFormDto){

        CampusDto campusDto = campusService.getCampusById(roomFormDto.campusId());
        Campus campus = campusMapper.toEntity(campusDto);

        boolean roomExist = verifyIfRoomExist(roomFormDto.name());

        if(roomExist){
            throw new CampusRoomBusinessException("La salle : " + roomFormDto.name()
                    + " existe déjà sur le campus : " + campus.getName()
                    + " et ne peut plus être rajouté" , HttpStatus.CONFLICT);
        }

        Room room = Room.builder()
                .name(roomFormDto.name())
                .capacity(roomFormDto.capacity())
                .location(roomFormDto.location())
                .campus(campus)
                .build();
        campus.getRooms().add(room);
        campusService.save(campus);
    }
}
