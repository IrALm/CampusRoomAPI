package com.campusRoom.api.service.patternStrategy;

import com.campusRoom.api.entity.Reservation;
import com.campusRoom.api.entity.User;
import com.campusRoom.api.exception.CampusRoomBusinessException;

/**
 * Interface du pattern Strategy pour la validation.
 * Permet de varier dynamiquement les règles de validation
 * sans modifier le code client.
 */
public interface ValidationStrategy {

    /**
     * Valide une réservation pour un utilisateur donné.
     *
     * @param reservation la réservation à valider
     * @param user l’utilisateur effectuant la réservation
     * @throws CampusRoomBusinessException si la réservation ne respecte pas les règles métier
     */
    void validate(Reservation reservation, User user) throws CampusRoomBusinessException;
}
