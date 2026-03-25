package com.campusRoom.api.service;

import java.time.LocalDateTime;

public interface ReservationChecker {


    /**
     * Verifie s'il existe des réservations pour une salle
     * @param roomId id de la salle.
     * @param date la date concerné
     * @return true or false
     */
    boolean existsByRoomIdAndStartTimeAfter(Long roomId, LocalDateTime date);

    /**
     * Vérifie s'il existe des réservations futures pour un campus donné
     * @param campusId id du campus
     * @param date date de réservation
     * @return true or false.
     */
    boolean existsByRoomCampusIdAndStartTimeAfter(Long campusId, LocalDateTime date);
}
