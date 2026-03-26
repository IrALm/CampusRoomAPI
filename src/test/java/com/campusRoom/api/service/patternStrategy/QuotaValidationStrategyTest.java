package com.campusRoom.api.service.patternStrategy;

import com.campusRoom.api.entity.Reservation;
import com.campusRoom.api.entity.Role;
import com.campusRoom.api.entity.User;
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

import static com.campusRoom.api.service.ConstantReservation.STUDENT_MONTHLY_LIMIT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires pour QuotaValidationStrategy")
class QuotaValidationStrategyTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private QuotaValidationStrategy strategy;

    @Test
    @DisplayName("ne lance rien si l'utilisateur n'est pas un étudiant")
    void shouldPassIfUserIsNotStudent() {
        Reservation reservation = new Reservation();
        User teacher = new User();
        teacher.setRole(Role.TEACHER);
        reservation.setStartTime(LocalDateTime.now());

        assertDoesNotThrow(() -> strategy.validate(reservation, teacher));
        verifyNoInteractions(reservationRepository);
    }

    @Test
    @DisplayName("ne lance rien si l'étudiant n'a pas atteint le quota")
    void shouldPassIfStudentQuotaNotReached() {
        Reservation reservation = new Reservation();
        User student = new User();
        student.setRole(Role.STUDENT);
        student.setId(1L);
        reservation.setStartTime(LocalDateTime.of(2026, 3, 25, 10, 0));

        when(reservationRepository.countByUserAndMonth(
                student.getId(),
                reservation.getStartTime().getMonthValue(),
                reservation.getStartTime().getYear()
        )).thenReturn((long) (STUDENT_MONTHLY_LIMIT - 1));

        assertDoesNotThrow(() -> strategy.validate(reservation, student));

        verify(reservationRepository).countByUserAndMonth(
                student.getId(),
                reservation.getStartTime().getMonthValue(),
                reservation.getStartTime().getYear()
        );
    }

    @Test
    @DisplayName("lance exception si l'étudiant a atteint le quota")
    void shouldThrowIfStudentQuotaReached() {
        Reservation reservation = new Reservation();
        User student = new User();
        student.setRole(Role.STUDENT);
        student.setId(1L);
        reservation.setStartTime(LocalDateTime.of(2026, 3, 25, 10, 0));

        when(reservationRepository.countByUserAndMonth(
                student.getId(),
                reservation.getStartTime().getMonthValue(),
                reservation.getStartTime().getYear()
        )).thenReturn((long) STUDENT_MONTHLY_LIMIT);

        CampusRoomBusinessException ex = assertThrows(
                CampusRoomBusinessException.class,
                () -> strategy.validate(reservation, student)
        );

        assertEquals("Quota mensuel atteint (" + STUDENT_MONTHLY_LIMIT + " réservations max).", ex.getMessage());
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());

        verify(reservationRepository).countByUserAndMonth(
                student.getId(),
                reservation.getStartTime().getMonthValue(),
                reservation.getStartTime().getYear()
        );
    }
}