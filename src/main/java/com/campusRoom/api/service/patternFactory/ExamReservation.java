package com.campusRoom.api.service.patternFactory;

import static com.campusRoom.api.service.ConstantReservation.DESCRIPTION_EXAM_RESERVATION;
import static com.campusRoom.api.service.ConstantReservation.MAX_DURATION_OF_EXAM_IN_HOUR;

public class ExamReservation implements ReservationBehavior {

    @Override
    public String getDescription() {
        return DESCRIPTION_EXAM_RESERVATION;
    }
    @Override
    public int getMaxDurationHours() {
        return MAX_DURATION_OF_EXAM_IN_HOUR;
    }
}
