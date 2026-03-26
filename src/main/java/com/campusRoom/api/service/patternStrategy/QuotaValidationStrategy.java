package com.campusRoom.api.service.patternStrategy;

import com.campusRoom.api.entity.Reservation;
import com.campusRoom.api.entity.Role;
import com.campusRoom.api.entity.User;
import com.campusRoom.api.exception.CampusRoomBusinessException;
import com.campusRoom.api.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static com.campusRoom.api.service.ConstantReservation.STUDENT_MONTHLY_LIMIT;

/**
 * Stratégie 3 : quota mensuel
 */
@Component
@RequiredArgsConstructor
public class QuotaValidationStrategy implements ValidationStrategy {

    private final ReservationRepository reservationRepository;


    @Override
    public void validate(Reservation reservation, User user)throws CampusRoomBusinessException {
        if (user.getRole() == Role.STUDENT) {
            long count = reservationRepository.countByUserAndMonth(
                    user.getId(),
                    reservation.getStartTime().getMonthValue(),
                    reservation.getStartTime().getYear()
            );
            if (count >= STUDENT_MONTHLY_LIMIT) {
                throw new CampusRoomBusinessException(
                        "Quota mensuel atteint (" + STUDENT_MONTHLY_LIMIT + " réservations max).",
                        HttpStatus.CONFLICT
                );
            }
        }
    }
}
