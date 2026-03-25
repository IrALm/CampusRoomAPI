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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConflictValidationStrategyTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ConflictValidationStrategy strategy;

    private Reservation buildReservation(Long reservationId, Long roomId) {
        Campus campus = Campus.builder().id(1L).name("ESGI").city("Paris").rooms(new ArrayList<>()).build();
        Room room = Room.builder().id(roomId).name("Salle A").capacity(30)
                .location("Bâtiment A").campus(campus).reservations(new ArrayList<>())
                .equipment(new ArrayList<>()).build();
        Reservation reservation = new Reservation();
        reservation.setId(reservationId);
        reservation.setRoom(room);
        reservation.setStartTime(LocalDateTime.of(2026, 4, 1, 9, 0));
        reservation.setEndTime(LocalDateTime.of(2026, 4, 1, 11, 0));
        reservation.setType(ReservationType.COURSE);
        reservation.setDescription("Cours");
        reservation.setMaxDurationHours(2);
        reservation.setUser(buildUser());
        return reservation;
    }

    private User buildUser() {
        return User.builder().id(1L).role(Role.STUDENT).reservations(new ArrayList<>()).build();
    }

    // ==================== validate ====================

    @Test
    @DisplayName("validate - ne doit pas lever d'exception quand aucun conflit horaire n'existe pour la salle")
    void should_notThrowException_when_noTimeConflictForRoom() {
        Reservation reservation = buildReservation(null, 1L);
        User user = buildUser();
        when(reservationRepository.existsConflict(
                1L, reservation.getStartTime(), reservation.getEndTime(), null))
                .thenReturn(false);

        assertDoesNotThrow(() -> strategy.validate(reservation, user));

        verify(reservationRepository).existsConflict(
                1L, reservation.getStartTime(), reservation.getEndTime(), null);
    }

    @Test
    @DisplayName("validate - doit lever CampusRoomBusinessException quand un conflit horaire est détecté")
    void should_throwConflictException_when_timeConflictDetected() {
        Reservation reservation = buildReservation(null, 1L);
        User user = buildUser();
        when(reservationRepository.existsConflict(
                1L, reservation.getStartTime(), reservation.getEndTime(), null))
                .thenReturn(true);

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> strategy.validate(reservation, user));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getMessage()).contains("déjà réservé");
    }

    @Test
    @DisplayName("validate - doit transmettre l'id de la réservation existante pour exclure les auto-conflits")
    void should_passReservationId_to_excludeSelfFromConflictCheck() {
        Reservation reservation = buildReservation(42L, 1L);
        User user = buildUser();
        when(reservationRepository.existsConflict(
                1L, reservation.getStartTime(), reservation.getEndTime(), 42L))
                .thenReturn(false);

        assertDoesNotThrow(() -> strategy.validate(reservation, user));

        verify(reservationRepository).existsConflict(
                1L, reservation.getStartTime(), reservation.getEndTime(), 42L);
    }

    @Test
    @DisplayName("validate - doit lever l'exception même si l'utilisateur est un professeur (conflit universel)")
    void should_throwConflictException_when_conflictDetectedForTeacher() {
        Reservation reservation = buildReservation(null, 1L);
        User teacher = User.builder().id(2L).role(Role.TEACHER).reservations(new ArrayList<>()).build();
        when(reservationRepository.existsConflict(
                1L, reservation.getStartTime(), reservation.getEndTime(), null))
                .thenReturn(true);

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> strategy.validate(reservation, teacher));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
    }
}
