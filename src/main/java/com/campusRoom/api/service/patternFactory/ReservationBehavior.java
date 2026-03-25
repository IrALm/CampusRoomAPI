package com.campusRoom.api.service.patternFactory;

/**
 *  Interface commune pour gérer les différents types
 *  de réservation et leur comportement spécifique.
 */

public interface ReservationBehavior {

    /**
     * Créer une description pour la reservation.
     * @return la description de la réservation.
     */
    String getDescription();

    /**
     * Défini une durée maximale de réservation en fonction du type de réservation
     * @return la durée maximale en heure.
     */
    int getMaxDurationHours();
}
