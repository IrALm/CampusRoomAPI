package com.campusRoom.api.service;

import com.campusRoom.api.dto.formDto.RoomFormDto;
import com.campusRoom.api.dto.outPutDto.RoomDto;

public interface RoomService {

    /**
     * Vérifie si une salle existe dans un campus
     * @param name nom de salle
     * @return true or false.
     */
    boolean verifyIfRoomExist(String name);

    /**
     * Créer une salle
     * @param roomFormDto salle à créer
     */
    void createRoom(RoomFormDto roomFormDto);

    /**
     * Récupère une salle
     * @param name nom de la salle.
     * @return la salle
     */
    RoomDto getByRoomName(String name);
}
