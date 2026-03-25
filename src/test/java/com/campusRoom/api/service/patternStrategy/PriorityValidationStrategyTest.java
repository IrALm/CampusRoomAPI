package com.campusRoom.api.service.patternStrategy;

import com.campusRoom.api.entity.*;
import com.campusRoom.api.exception.CampusRoomBusinessException;
import com.campusRoom.api.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriorityValidationStrategyTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private PriorityValidationStrategy strategy;

    private Reservation buildReservation(Long roomId) {
        Campus campus = Campus.builder().id(1L).name("ESGI").city("Paris").rooms(new ArrayList<>()).build();
        Room room = Room.builder().id(roomId).name("Salle A").capacity(30)
                .location("Bâtiment A").campus(campus).reservations(new ArrayList<>())
                .equipment(new ArrayList<>()).build();
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setRoom(room);
        reservation.setStartTime(LocalDateTime.of(2026, 4, 1, 9, 0));
        reservation.setEndTime(LocalDateTime.of(2026, 4, 1, 11, 0));
        reservation.setType(ReservationType.COURSE);
        reservation.setDescription("Cours");
        reservation.setMaxDurationHours(2);
        return reservation;
    }

    // ==================== validate — étudiant ====================

    @Test
    @DisplayName("validate - ne doit pas lever d'exception quand l'étudiant réserve sans conflit avec un professeur")
    void should_notThrowException_when_studentReservesWithNoTeacherConflict() {
        Reservation reservation = buildReservation(1L);
        User student = User.builder().id(1L).role(Role.STUDENT).reservations(new ArrayList<>()).build();
        when(reservationRepository.existsTeacherReservation(
                1L, reservation.getStartTime(), reservation.getEndTime()))
                .thenReturn(false);

        assertDoesNotThrow(() -> strategy.validate(reservation, student));

        verify(reservationRepository).existsTeacherReservation(
                1L, reservation.getStartTime(), reservation.getEndTime());
    }

    @Test
    @DisplayName("validate - doit lever CampusRoomBusinessException quand un professeur a déjà ce créneau et l'utilisateur est étudiant")
    void should_throwConflictException_when_teacherHasSlotAndUserIsStudent() {
        Reservation reservation = buildReservation(1L);
        User student = User.builder().id(1L).role(Role.STUDENT).reservations(new ArrayList<>()).build();
        when(reservationRepository.existsTeacherReservation(
                1L, reservation.getStartTime(), reservation.getEndTime()))
                .thenReturn(true);

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> strategy.validate(reservation, student));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getMessage()).contains("enseignant");
    }

    @Test
    @DisplayName("validate - doit vérifier le repository quand l'utilisateur est un étudiant")
    void should_callRepository_when_userIsStudent() {
        Reservation reservation = buildReservation(1L);
        User student = User.builder().id(1L).role(Role.STUDENT).reservations(new ArrayList<>()).build();
        when(reservationRepository.existsTeacherReservation(
                1L, reservation.getStartTime(), reservation.getEndTime()))
                .thenReturn(false);

        strategy.validate(reservation, student);

        verify(reservationRepository, times(1)).existsTeacherReservation(
                1L, reservation.getStartTime(), reservation.getEndTime());
    }

    // ==================== validate — professeur ====================

    @Test
    @DisplayName("validate - ne doit pas consulter le repository quand l'utilisateur est un professeur")
    void should_notCallRepository_when_userIsTeacher() {
        Reservation reservation = buildReservation(1L);
        User teacher = User.builder().id(2L).role(Role.TEACHER).reservations(new ArrayList<>()).build();

        assertDoesNotThrow(() -> strategy.validate(reservation, teacher));

        verifyNoInteractions(reservationRepository);
    }

    @Test
    @DisplayName("validate - ne doit pas lever d'exception pour un professeur même si un autre professeur a le créneau")
    void should_notThrowException_when_teacherReservesEvenWithAnotherTeacherSlot() {
        Reservation reservation = buildReservation(1L);
        User teacher = User.builder().id(2L).role(Role.TEACHER).reservations(new ArrayList<>()).build();

        assertDoesNotThrow(() -> strategy.validate(reservation, teacher));

        verifyNoInteractions(reservationRepository);
    }
}
