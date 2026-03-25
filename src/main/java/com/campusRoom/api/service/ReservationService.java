package com.campusRoom.api.service;

import com.campusRoom.api.dto.formDto.ReservationFormDto;
import com.campusRoom.api.dto.outPutDto.ReservationDto;

public interface ReservationService {

    /**
     * Créer une réservation
     * @param dto formulaire de réservation
     */
    void create(ReservationFormDto dto);

    /**
     * Retourne une reservation avec toutes ses informations.
     * @param reservationId id de la réservation
     * @return reservationDto
     */
    ReservationDto getReservationWithAllProperties(Long reservationId);
}
