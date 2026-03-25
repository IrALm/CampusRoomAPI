package com.campusRoom.api.service.impl;

import com.campusRoom.api.dto.formDto.CampusFormDto;
import com.campusRoom.api.dto.outPutDto.CampusDto;
import com.campusRoom.api.entity.Campus;
import com.campusRoom.api.exception.CampusRoomBusinessException;
import com.campusRoom.api.mapper.CampusMapper;
import com.campusRoom.api.repository.CampusRepository;
import com.campusRoom.api.service.CampusService;
import com.campusRoom.api.service.ReservationChecker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CampusServiceImpl implements CampusService {

    private final CampusRepository campusRepository;
    private final CampusMapper campusMapper;
    private final ReservationChecker reservationChecker;

    @Override
    public Campus getCampusById(Long id){

         return campusRepository.findById(id)
                .orElseThrow(() ->
                        new CampusRoomBusinessException("Erreur, le campus n'existe pas.", HttpStatus.NOT_FOUND));

    }

    @Override
    public CampusDto getCampusByName(String name){

        Campus campus = campusRepository.findByName(name)
                .orElseThrow(() ->
                        new CampusRoomBusinessException("Aucun campus n'existe pour ce nom." , HttpStatus.NOT_FOUND));

        return campusMapper.toDTO(campus);
    }

    @Override
    public boolean verifyIfCampusExist(String name , String city){

        return campusRepository.existsByName(name);
    }

    @Override
    public void createCampus(CampusFormDto campusFormDto){

        boolean campusExist = verifyIfCampusExist(campusFormDto.name() , campusFormDto.city());
        if(campusExist){
            throw new CampusRoomBusinessException("Un campus existe déjà sous le nom : "
                    + campusFormDto.name() + " dans la ville de : "
                    + campusFormDto.city() + " ou dans une autre ville" +
                    ". Le nom du campus doit être unique. " , HttpStatus.CONFLICT);
        }

        Campus campus = Campus.builder()
                .name(campusFormDto.name())
                .city(campusFormDto.city())
                .build();

        save(campus);
    }

    @Override
    public void save(Campus campus){

        campusRepository.save(campus);
    }


    @Override
    @Transactional
    public void updateNameAndCity(Long id , String name , String city){

        boolean campusExist = verifyIfCampusExist(name , city);
        if(campusExist){
            throw new CampusRoomBusinessException("Un campus existe déjà sous le nom : "
                    + name + " dans la ville de : "
                    + city + " ou dans une autre ville" +
                    ". Le nom du campus doit être unique. " , HttpStatus.CONFLICT);
        }

        campusRepository.updateNameAndCity(id , name , city);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Campus campus = campusRepository.findById(id)
                .orElseThrow(() -> new CampusRoomBusinessException(
                        "Aucun campus trouvé pour l'id : " + id,
                        HttpStatus.NOT_FOUND
                ));

        // Vérifie les réservations futures sur toutes les rooms du campus
        boolean hasFutureReservations = reservationChecker
                .existsByRoomCampusIdAndStartTimeAfter(id, LocalDateTime.now());

        if (hasFutureReservations) {
            throw new CampusRoomBusinessException(
                    "Impossible de supprimer le campus \"" + campus.getName()
                            + "\" : des réservations futures sont rattachées à ses salles.",
                    HttpStatus.CONFLICT
            );
        }

        campusRepository.delete(campus);
    }
}
