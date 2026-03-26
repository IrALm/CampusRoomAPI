package com.campusRoom.api.service.patternFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.campusRoom.api.service.constantReservation.DESCRIPTION_EXAM_RESERVATION;
import static com.campusRoom.api.service.constantReservation.MAX_DURATION_OF_EXAM_IN_HOUR;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Tests unitaires pour ExamReservation")
class ExamReservationTest {

    @Test
    @DisplayName("doit retourner la bonne description")
    void shouldReturnCorrectDescription() {
        ExamReservation examReservation = new ExamReservation();

        String description = examReservation.getDescription();

        assertEquals(DESCRIPTION_EXAM_RESERVATION, description);
    }

    @Test
    @DisplayName("doit retourner la bonne durée max")
    void shouldReturnCorrectMaxDuration() {
        ExamReservation examReservation = new ExamReservation();

        int maxDuration = examReservation.getMaxDurationHours();

        assertEquals(MAX_DURATION_OF_EXAM_IN_HOUR, maxDuration);
    }
}