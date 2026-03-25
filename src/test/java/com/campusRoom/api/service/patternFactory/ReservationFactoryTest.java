package com.campusRoom.api.service.patternFactory;

import com.campusRoom.api.entity.ReservationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.campusRoom.api.service.constantReservation.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ReservationFactoryTest {

    private final ReservationFactory factory = new ReservationFactory();

    // ==================== create — COURSE ====================

    @Test
    @DisplayName("create - doit retourner un comportement COURSE avec la description correcte")
    void should_returnCourseDescription_when_typeCourse() {
        ReservationBehavior behavior = factory.create(ReservationType.COURSE);

        assertThat(behavior).isNotNull();
        assertThat(behavior.getDescription()).isEqualTo(DESCRIPTION_COURSE_RESERVATION);
    }

    @Test
    @DisplayName("create - doit retourner un comportement COURSE avec la durée maximale correcte (3h)")
    void should_returnCourseMaxDuration_when_typeCourse() {
        ReservationBehavior behavior = factory.create(ReservationType.COURSE);

        assertThat(behavior.getMaxDurationHours()).isEqualTo(MAX_DURATION_OF_COURSE_IN_HOUR);
    }

    @Test
    @DisplayName("create - doit retourner une instance de CourseReservation pour le type COURSE")
    void should_returnCourseReservationInstance_when_typeCourse() {
        ReservationBehavior behavior = factory.create(ReservationType.COURSE);

        assertThat(behavior).isInstanceOf(CourseReservation.class);
    }

    // ==================== create — MEETING ====================

    @Test
    @DisplayName("create - doit retourner un comportement MEETING avec la description correcte")
    void should_returnMeetingDescription_when_typeMeeting() {
        ReservationBehavior behavior = factory.create(ReservationType.MEETING);

        assertThat(behavior).isNotNull();
        assertThat(behavior.getDescription()).isEqualTo(DESCRIPTION_MEETING_RESERVATION);
    }

    @Test
    @DisplayName("create - doit retourner un comportement MEETING avec la durée maximale correcte (2h)")
    void should_returnMeetingMaxDuration_when_typeMeeting() {
        ReservationBehavior behavior = factory.create(ReservationType.MEETING);

        assertThat(behavior.getMaxDurationHours()).isEqualTo(MAX_DURATION_OF_MEETING_IN_HOUR);
    }

    @Test
    @DisplayName("create - doit retourner une instance de MeetingReservation pour le type MEETING")
    void should_returnMeetingReservationInstance_when_typeMeeting() {
        ReservationBehavior behavior = factory.create(ReservationType.MEETING);

        assertThat(behavior).isInstanceOf(MeetingReservation.class);
    }

    // ==================== create — EXAM ====================

    @Test
    @DisplayName("create - doit retourner un comportement EXAM avec la description correcte")
    void should_returnExamDescription_when_typeExam() {
        ReservationBehavior behavior = factory.create(ReservationType.EXAM);

        assertThat(behavior).isNotNull();
        assertThat(behavior.getDescription()).isEqualTo(DESCRIPTION_EXAM_RESERVATION);
    }

    @Test
    @DisplayName("create - doit retourner un comportement EXAM avec la durée maximale correcte (4h)")
    void should_returnExamMaxDuration_when_typeExam() {
        ReservationBehavior behavior = factory.create(ReservationType.EXAM);

        assertThat(behavior.getMaxDurationHours()).isEqualTo(MAX_DURATION_OF_EXAM_IN_HOUR);
    }

    @Test
    @DisplayName("create - doit retourner une instance de ExamReservation pour le type EXAM")
    void should_returnExamReservationInstance_when_typeExam() {
        ReservationBehavior behavior = factory.create(ReservationType.EXAM);

        assertThat(behavior).isInstanceOf(ExamReservation.class);
    }

    // ==================== cohérence des constantes ====================

    @Test
    @DisplayName("create - doit retourner des durées maximales différentes selon le type de réservation")
    void should_returnDifferentMaxDurations_for_eachReservationType() {
        int courseDuration  = factory.create(ReservationType.COURSE).getMaxDurationHours();
        int meetingDuration = factory.create(ReservationType.MEETING).getMaxDurationHours();
        int examDuration    = factory.create(ReservationType.EXAM).getMaxDurationHours();

        assertThat(courseDuration).isNotEqualTo(meetingDuration);
        assertThat(examDuration).isGreaterThan(courseDuration);
        assertThat(courseDuration).isGreaterThan(meetingDuration);
    }
}
