package com.campusRoom.api.service;

import com.campusRoom.api.dto.formDto.ReservationFormDto;
import com.campusRoom.api.entity.Reservation;

public interface ReservationService {

    /**
     * Créer une réservation
     * @param dto formulaire de réservation
     * @param currentUserId id utilisateur existant
     * @return reservation créer
     */
    Reservation create(ReservationFormDto dto, Long currentUserId);
}
