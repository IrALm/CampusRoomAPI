package com.campusRoom.api.service.patternFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.campusRoom.api.service.ConstantReservation.DESCRIPTION_MEETING_RESERVATION;
import static com.campusRoom.api.service.ConstantReservation.MAX_DURATION_OF_MEETING_IN_HOUR;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Tests unitaires pour MeetingReservation")
class MeetingReservationTest {

    @Test
    @DisplayName("doit retourner la bonne description")
    void shouldReturnCorrectDescription() {
        MeetingReservation meetingReservation = new MeetingReservation();

        String description = meetingReservation.getDescription();

        assertEquals(DESCRIPTION_MEETING_RESERVATION, description);
    }

    @Test
    @DisplayName("doit retourner la bonne durée max")
    void shouldReturnCorrectMaxDuration() {
        MeetingReservation meetingReservation = new MeetingReservation();

        int maxDuration = meetingReservation.getMaxDurationHours();

        assertEquals(MAX_DURATION_OF_MEETING_IN_HOUR, maxDuration);
    }
}