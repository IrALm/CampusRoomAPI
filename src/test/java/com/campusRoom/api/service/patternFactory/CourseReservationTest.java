package com.campusRoom.api.service.patternFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.campusRoom.api.service.constantReservation.DESCRIPTION_COURSE_RESERVATION;
import static com.campusRoom.api.service.constantReservation.MAX_DURATION_OF_COURSE_IN_HOUR;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Tests unitaires pour CourseReservation")
class CourseReservationTest {

    @Test
    @DisplayName("doit retourner la bonne description")
    void shouldReturnCorrectDescription() {
        CourseReservation courseReservation = new CourseReservation();

        String description = courseReservation.getDescription();

        assertEquals(DESCRIPTION_COURSE_RESERVATION, description);
    }

    @Test
    @DisplayName("doit retourner la bonne durée max")
    void shouldReturnCorrectMaxDuration() {
        CourseReservation courseReservation = new CourseReservation();

        int maxDuration = courseReservation.getMaxDurationHours();

        assertEquals(MAX_DURATION_OF_COURSE_IN_HOUR, maxDuration);
    }
}