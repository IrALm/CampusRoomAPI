package com.campusRoom.api.service.patternFactory;


import static com.campusRoom.api.service.ConstantReservation.DESCRIPTION_COURSE_RESERVATION;
import static com.campusRoom.api.service.ConstantReservation.MAX_DURATION_OF_COURSE_IN_HOUR;

public class CourseReservation implements ReservationBehavior {

    @Override
    public String getDescription() {
        return DESCRIPTION_COURSE_RESERVATION;
    }
    @Override
    public int getMaxDurationHours() {
        return MAX_DURATION_OF_COURSE_IN_HOUR;
    }
}
