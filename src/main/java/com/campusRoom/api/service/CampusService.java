package com.campusRoom.api.service;

import com.campusRoom.api.dto.formDto.CampusFormDto;
import com.campusRoom.api.dto.outPutDto.CampusDto;

public interface CampusService {

    /**
     * rétourne un campus existant
     * @param name nom du campus
     * @return le campus
     */
    CampusDto getCampusByName(String name);

    /**
     * Vérifie si un campus existe.
     * @param name nom du campus
     * @return vrai ou false.
     */
    boolean verifyIfCampusExist(String name , String city);

    /**
     * Créer un campus
     * @param campusFormDto campus à créer
     * @return campus crée
     */
    CampusDto createCampus(CampusFormDto campusFormDto);
}
