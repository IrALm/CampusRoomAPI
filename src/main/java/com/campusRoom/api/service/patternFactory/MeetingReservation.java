package com.campusRoom.api.service.patternFactory;

import static com.campusRoom.api.service.constantReservation.DESCRIPTION_MEETING_RESERVATION;
import static com.campusRoom.api.service.constantReservation.MAX_DURATION_OF_MEETING_IN_HOUR;

public class MeetingReservation implements ReservationBehavior {

    @Override
    public String getDescription() {
        return DESCRIPTION_MEETING_RESERVATION;
    }
    @Override
    public int getMaxDurationHours() {
        return MAX_DURATION_OF_MEETING_IN_HOUR;
    }
}
