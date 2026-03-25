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
import java.time.Month;
import java.util.ArrayList;

import static com.campusRoom.api.service.constantReservation.STUDENT_MONTHLY_LIMIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuotaValidationStrategyTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private QuotaValidationStrategy strategy;

    private Reservation buildReservation(int year, Month month) {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setStartTime(LocalDateTime.of(year, month, 15, 10, 0));
        reservation.setEndTime(LocalDateTime.of(year, month, 15, 12, 0));
        reservation.setType(ReservationType.COURSE);
        reservation.setDescription("Cours");
        reservation.setMaxDurationHours(2);
        reservation.setUser(buildStudent(1L));
        return reservation;
    }

    private User buildStudent(Long id) {
        return User.builder().id(id).role(Role.STUDENT).reservations(new ArrayList<>()).build();
    }

    private User buildTeacher() {
        return User.builder().id(99L).role(Role.TEACHER).reservations(new ArrayList<>()).build();
    }

    // ==================== validate — étudiant sous quota ====================

    @Test
    @DisplayName("validate - ne doit pas lever d'exception quand l'étudiant est sous le quota mensuel")
    void should_notThrowException_when_studentUnderMonthlyQuota() {
        Reservation reservation = buildReservation(2026, Month.APRIL);
        User student = buildStudent(1L);
        when(reservationRepository.countByUserAndMonth(1L, Month.APRIL, 2026))
                .thenReturn((long) STUDENT_MONTHLY_LIMIT - 1);

        assertDoesNotThrow(() -> strategy.validate(reservation, student));

        verify(reservationRepository).countByUserAndMonth(1L, Month.APRIL, 2026);
    }

    @Test
    @DisplayName("validate - ne doit pas lever d'exception quand l'étudiant n'a aucune réservation ce mois")
    void should_notThrowException_when_studentHasZeroReservationsThisMonth() {
        Reservation reservation = buildReservation(2026, Month.APRIL);
        User student = buildStudent(1L);
        when(reservationRepository.countByUserAndMonth(1L, Month.APRIL, 2026)).thenReturn(0L);

        assertDoesNotThrow(() -> strategy.validate(reservation, student));

        verify(reservationRepository).countByUserAndMonth(1L, Month.APRIL, 2026);
    }

    // ==================== validate — étudiant quota atteint ====================

    @Test
    @DisplayName("validate - doit lever CampusRoomBusinessException quand l'étudiant a exactement atteint son quota mensuel")
    void should_throwConflictException_when_studentReachesExactMonthlyQuota() {
        Reservation reservation = buildReservation(2026, Month.APRIL);
        User student = buildStudent(1L);
        when(reservationRepository.countByUserAndMonth(1L, Month.APRIL, 2026))
                .thenReturn((long) STUDENT_MONTHLY_LIMIT);

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> strategy.validate(reservation, student));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getMessage()).contains(String.valueOf(STUDENT_MONTHLY_LIMIT));
    }

    @Test
    @DisplayName("validate - doit lever CampusRoomBusinessException quand l'étudiant a dépassé son quota mensuel")
    void should_throwConflictException_when_studentExceedsMonthlyQuota() {
        Reservation reservation = buildReservation(2026, Month.APRIL);
        User student = buildStudent(1L);
        when(reservationRepository.countByUserAndMonth(1L, Month.APRIL, 2026))
                .thenReturn((long) STUDENT_MONTHLY_LIMIT + 3);

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> strategy.validate(reservation, student));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("validate - doit inclure la limite dans le message d'erreur")
    void should_includeLimitInErrorMessage_when_quotaExceeded() {
        Reservation reservation = buildReservation(2026, Month.APRIL);
        User student = buildStudent(1L);
        when(reservationRepository.countByUserAndMonth(1L, Month.APRIL, 2026))
                .thenReturn((long) STUDENT_MONTHLY_LIMIT);

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> strategy.validate(reservation, student));

        assertThat(ex.getMessage()).contains("5");
    }

    @Test
    @DisplayName("validate - doit utiliser le bon mois et la bonne année pour le comptage")
    void should_useCorrectMonthAndYear_when_counting() {
        Reservation reservation = buildReservation(2026, Month.DECEMBER);
        User student = buildStudent(2L);
        when(reservationRepository.countByUserAndMonth(2L, Month.DECEMBER, 2026)).thenReturn(0L);

        strategy.validate(reservation, student);

        verify(reservationRepository).countByUserAndMonth(2L, Month.DECEMBER, 2026);
    }

    // ==================== validate — professeur ====================

    @Test
    @DisplayName("validate - ne doit pas consulter le repository quand l'utilisateur est un professeur")
    void should_notCallRepository_when_userIsTeacher() {
        Reservation reservation = buildReservation(2026, Month.APRIL);
        User teacher = buildTeacher();

        assertDoesNotThrow(() -> strategy.validate(reservation, teacher));

        verifyNoInteractions(reservationRepository);
    }

    @Test
    @DisplayName("validate - ne doit pas lever d'exception pour un professeur sans limite de quota")
    void should_notThrowException_when_teacherHasNoQuotaLimit() {
        Reservation reservation = buildReservation(2026, Month.APRIL);
        User teacher = buildTeacher();

        assertDoesNotThrow(() -> strategy.validate(reservation, teacher));
    }
}
