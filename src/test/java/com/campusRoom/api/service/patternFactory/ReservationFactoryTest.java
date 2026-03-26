package com.campusRoom.api.service.patternFactory;

import com.campusRoom.api.entity.ReservationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitaires pour ReservationFactory")
class ReservationFactoryTest {

    private final ReservationFactory factory = new ReservationFactory();

    @Test
    @DisplayName("doit retourner CourseReservation pour type COURSE")
    void shouldReturnCourseReservation() {
        ReservationBehavior behavior = factory.create(ReservationType.COURSE);
        assertInstanceOf(CourseReservation.class, behavior);
    }

    @Test
    @DisplayName("doit retourner MeetingReservation pour type MEETING")
    void shouldReturnMeetingReservation() {
        ReservationBehavior behavior = factory.create(ReservationType.MEETING);
        assertInstanceOf(MeetingReservation.class, behavior);
    }

    @Test
    @DisplayName("doit retourner ExamReservation pour type EXAM")
    void shouldReturnExamReservation() {
        ReservationBehavior behavior = factory.create(ReservationType.EXAM);
        assertInstanceOf(ExamReservation.class, behavior);
    }
}