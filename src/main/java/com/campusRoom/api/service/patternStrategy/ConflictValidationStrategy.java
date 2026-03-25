package com.campusRoom.api.service.patternStrategy;

import com.campusRoom.api.entity.Reservation;
import com.campusRoom.api.entity.User;
import com.campusRoom.api.exception.CampusRoomBusinessException;
import com.campusRoom.api.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Stratégie 1 : vérification des conflits horaires
 */
@Component
@RequiredArgsConstructor
public class ConflictValidationStrategy implements ValidationStrategy {

    private final ReservationRepository reservationRepository;


    @Override
    public void validate(Reservation reservation, User user) throws CampusRoomBusinessException {

        boolean conflict = reservationRepository.existsConflict(
                reservation.getRoom().getId(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getId()
        );
        if (conflict) {
            throw new CampusRoomBusinessException("Ce créneau est déjà réservé pour cette salle.",
                    HttpStatus.CONFLICT);
        }
    }
}
