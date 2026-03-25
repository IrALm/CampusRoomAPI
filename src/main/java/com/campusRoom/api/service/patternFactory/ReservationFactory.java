package com.campusRoom.api.service.patternFactory;

import com.campusRoom.api.entity.ReservationType;
import com.campusRoom.api.exception.CampusRoomBusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * LA FACTORY : responsable de l’instanciation des différents comportements de réservation.
 */
@Component
public class ReservationFactory {

    public ReservationBehavior create(ReservationType type) {
        return switch (type) {
            case COURSE  -> new CourseReservation();
            case MEETING -> new MeetingReservation();
            case EXAM    -> new ExamReservation();
            default      -> throw new CampusRoomBusinessException("Type de réservation inconnu" ,
                    HttpStatus.NOT_FOUND);
        };
    }
}
