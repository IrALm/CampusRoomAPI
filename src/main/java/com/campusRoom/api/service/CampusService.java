package com.campusRoom.api.service;

import com.campusRoom.api.dto.formDto.CampusFormDto;
import com.campusRoom.api.dto.outPutDto.CampusDto;
import com.campusRoom.api.entity.Campus;

public interface CampusService {

    /**
     * rétourne un campus existant
     * @param id id du campus
     * @return le campus
     */
    CampusDto getCampusById(Long id);

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
     */
    void createCampus(CampusFormDto campusFormDto);

    /**
     * Enregistrer un campus
     * @param campus le campus
     */
    void save(Campus campus);

    /**
     * Met à jour le nom du Campus et sa ville.
     * @param id id du campus
     * @param name nom du campus
     */
    void updateNameAndCity(Long id , String name , String city);

}
