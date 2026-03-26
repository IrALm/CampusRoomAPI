package com.campusRoom.api.service.impl;

import com.campusRoom.api.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service ReservationCheckerImpl")
class ReservationCheckerImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationCheckerImpl reservationChecker;

    @Nested
    @DisplayName("existsByRoomIdAndStartTimeAfter")
    class ExistsByRoomIdTests {

        @Test
        @DisplayName("retourne true si réservation future existe pour une room")
        void shouldReturnTrue_whenReservationExists() {
            Long roomId = 1L;
            LocalDateTime date = LocalDateTime.now();

            when(reservationRepository.existsByRoomIdAndStartTimeAfter(roomId, date))
                    .thenReturn(true);

            boolean result = reservationChecker.existsByRoomIdAndStartTimeAfter(roomId, date);

            assertTrue(result);
            verify(reservationRepository).existsByRoomIdAndStartTimeAfter(roomId, date);
        }

        @Test
        @DisplayName("retourne false si aucune réservation future pour une room")
        void shouldReturnFalse_whenNoReservationExists() {
            Long roomId = 1L;
            LocalDateTime date = LocalDateTime.now();

            when(reservationRepository.existsByRoomIdAndStartTimeAfter(roomId, date))
                    .thenReturn(false);

            boolean result = reservationChecker.existsByRoomIdAndStartTimeAfter(roomId, date);

            assertFalse(result);
            verify(reservationRepository).existsByRoomIdAndStartTimeAfter(roomId, date);
        }
    }

    @Nested
    @DisplayName("existsByRoomCampusIdAndStartTimeAfter")
    class ExistsByCampusIdTests {

        @Test
        @DisplayName("retourne true si réservation future existe pour un campus")
        void shouldReturnTrue_whenReservationExists() {
            Long campusId = 1L;
            LocalDateTime date = LocalDateTime.now();

            when(reservationRepository.existsByRoomCampusIdAndStartTimeAfter(campusId, date))
                    .thenReturn(true);

            boolean result = reservationChecker.existsByRoomCampusIdAndStartTimeAfter(campusId, date);

            assertTrue(result);
            verify(reservationRepository)
                    .existsByRoomCampusIdAndStartTimeAfter(campusId, date);
        }

        @Test
        @DisplayName("retourne false si aucune réservation future pour un campus")
        void shouldReturnFalse_whenNoReservationExists() {
            Long campusId = 1L;
            LocalDateTime date = LocalDateTime.now();

            when(reservationRepository.existsByRoomCampusIdAndStartTimeAfter(campusId, date))
                    .thenReturn(false);

            boolean result = reservationChecker.existsByRoomCampusIdAndStartTimeAfter(campusId, date);

            assertFalse(result);
            verify(reservationRepository)
                    .existsByRoomCampusIdAndStartTimeAfter(campusId, date);
        }
    }
}