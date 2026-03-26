package com.campusRoom.api.service.patternStrategy;

import com.campusRoom.api.entity.Reservation;
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
@DisplayName("Tests unitaires pour ConflictValidationStrategy")
class ConflictValidationStrategyTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ConflictValidationStrategy strategy;

    @Test
    @DisplayName("ne lance rien si pas de conflit")
    void shouldPassWhenNoConflict() {
        Reservation reservation = new Reservation();
        Room room = new Room();
        room.setId(1L);
        reservation.setRoom(room);
        reservation.setStartTime(LocalDateTime.now().plusHours(1));
        reservation.setEndTime(LocalDateTime.now().plusHours(2));
        reservation.setId(10L);

        User user = new User();

        when(reservationRepository.existsConflict(
                reservation.getRoom().getId(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getId()
        )).thenReturn(false);

        assertDoesNotThrow(() -> strategy.validate(reservation, user));

        verify(reservationRepository).existsConflict(
                reservation.getRoom().getId(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getId()
        );
    }

    @Test
    @DisplayName("lance exception si conflit")
    void shouldThrowExceptionWhenConflict() {
        Reservation reservation = new Reservation();
        Room room = new Room();
        room.setId(1L);
        reservation.setRoom(room);
        reservation.setStartTime(LocalDateTime.now().plusHours(1));
        reservation.setEndTime(LocalDateTime.now().plusHours(2));
        reservation.setId(10L);

        User user = new User();

        when(reservationRepository.existsConflict(
                reservation.getRoom().getId(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getId()
        )).thenReturn(true);

        CampusRoomBusinessException ex = assertThrows(
                CampusRoomBusinessException.class,
                () -> strategy.validate(reservation, user)
        );

        assertEquals("Ce créneau est déjà réservé pour cette salle.", ex.getMessage());
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());

        verify(reservationRepository).existsConflict(
                reservation.getRoom().getId(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getId()
        );
    }
}