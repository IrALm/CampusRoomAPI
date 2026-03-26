package com.campusRoom.api.service.patternStrategy;

import com.campusRoom.api.entity.Reservation;
import com.campusRoom.api.entity.Role;
import com.campusRoom.api.entity.Room;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires pour PriorityValidationStrategy")
class PriorityValidationStrategyTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private PriorityValidationStrategy strategy;

    @Test
    @DisplayName("ne lance rien si l'utilisateur n'est pas un étudiant")
    void shouldPassIfUserIsNotStudent() {
        Reservation reservation = new Reservation();
        Room room = new Room();
        room.setId(1L);
        reservation.setRoom(room);
        reservation.setStartTime(LocalDateTime.now().plusHours(1));
        reservation.setEndTime(LocalDateTime.now().plusHours(2));

        User teacher = new User();
        teacher.setRole(Role.TEACHER);

        assertDoesNotThrow(() -> strategy.validate(reservation, teacher));
        verifyNoInteractions(reservationRepository); // le repo ne doit pas être appelé
    }

    @Test
    @DisplayName("ne lance rien si l'étudiant n'a pas de conflit avec prof")
    void shouldPassIfStudentNoTeacherConflict() {
        Reservation reservation = new Reservation();
        Room room = new Room();
        room.setId(1L);
        reservation.setRoom(room);
        reservation.setStartTime(LocalDateTime.now().plusHours(1));
        reservation.setEndTime(LocalDateTime.now().plusHours(2));

        User student = new User();
        student.setRole(Role.STUDENT);

        when(reservationRepository.existsTeacherReservation(
                reservation.getRoom().getId(),
                reservation.getStartTime(),
                reservation.getEndTime()
        )).thenReturn(false);

        assertDoesNotThrow(() -> strategy.validate(reservation, student));

        verify(reservationRepository).existsTeacherReservation(
                reservation.getRoom().getId(),
                reservation.getStartTime(),
                reservation.getEndTime()
        );
    }

    @Test
    @DisplayName("lance exception si l'étudiant a un conflit avec prof")
    void shouldThrowIfStudentTeacherConflict() {
        Reservation reservation = new Reservation();
        Room room = new Room();
        room.setId(1L);
        reservation.setRoom(room);
        reservation.setStartTime(LocalDateTime.now().plusHours(1));
        reservation.setEndTime(LocalDateTime.now().plusHours(2));

        User student = new User();
        student.setRole(Role.STUDENT);

        when(reservationRepository.existsTeacherReservation(
                reservation.getRoom().getId(),
                reservation.getStartTime(),
                reservation.getEndTime()
        )).thenReturn(true);

        CampusRoomBusinessException ex = assertThrows(
                CampusRoomBusinessException.class,
                () -> strategy.validate(reservation, student)
        );

        assertEquals("Ce créneau est réservé à un enseignant. Les étudiants ont une priorité inférieure.", ex.getMessage());
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());

        verify(reservationRepository).existsTeacherReservation(
                reservation.getRoom().getId(),
                reservation.getStartTime(),
                reservation.getEndTime()
        );
    }
}