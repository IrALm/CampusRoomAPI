package com.campusRoom.api.service.patternStrategy;

import com.campusRoom.api.entity.Reservation;
import com.campusRoom.api.entity.Role;
import com.campusRoom.api.entity.User;
import com.campusRoom.api.exception.CampusRoomBusinessException;
import com.campusRoom.api.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Stratégie 2 : priorité prof > étudiant
 */
@Component
@RequiredArgsConstructor
public class PriorityValidationStrategy implements ValidationStrategy {

    private final ReservationRepository reservationRepository;

    @Override
    public void validate(Reservation reservation, User user)throws CampusRoomBusinessException {
        if (user.getRole() == Role.STUDENT) {
            // Un étudiant ne peut pas réserver si un prof a une réservation
            // chevauchante en attente ou confirmée
            boolean teacherHasSlot = reservationRepository.existsTeacherReservation(
                    reservation.getRoom().getId(),
                    reservation.getStartTime(),
                    reservation.getEndTime()
            );
            if (teacherHasSlot) {
                throw new CampusRoomBusinessException(
                        "Ce créneau est réservé à un enseignant. Les étudiants ont une priorité inférieure.",
                        HttpStatus.CONFLICT
                );
            }
        }
    }
}
