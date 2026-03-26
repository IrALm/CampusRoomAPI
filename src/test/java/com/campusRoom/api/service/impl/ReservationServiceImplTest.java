package com.campusRoom.api.service.impl;

import com.campusRoom.api.dto.formDto.ReservationFormDto;
import com.campusRoom.api.dto.outPutDto.ReservationDto;
import com.campusRoom.api.entity.Reservation;
import com.campusRoom.api.entity.Room;
import com.campusRoom.api.entity.User;
import com.campusRoom.api.exception.CampusRoomBusinessException;
import com.campusRoom.api.mapper.ReservationMapper;
import com.campusRoom.api.repository.ReservationRepository;
import com.campusRoom.api.service.RoomService;
import com.campusRoom.api.service.UserService;
import com.campusRoom.api.service.patternFactory.ReservationBehavior;
import com.campusRoom.api.service.patternFactory.ReservationFactory;
import com.campusRoom.api.service.patternStrategy.ValidationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service ReservationServiceImpl")
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationFactory reservationFactory;

    @Mock
    private UserService userService;

    @Mock
    private RoomService roomService;

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private ValidationStrategy validationStrategy1;

    @Mock
    private ValidationStrategy validationStrategy2;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private List<ValidationStrategy> strategies;

    private ReservationBehavior behavior;

    @BeforeEach
    void setup() {
        strategies = List.of(validationStrategy1, validationStrategy2);
        reservationService = new ReservationServiceImpl(
                reservationRepository,
                reservationFactory,
                strategies,
                userService,
                roomService,
                reservationMapper
        );

        behavior = mock(ReservationBehavior.class);
    }

    // =========================
    // CREATE
    // =========================

    @Nested
    @DisplayName("create")
    class CreateTests {

        @Test
        @DisplayName("crée une réservation valide")
        void shouldCreateReservationSuccessfully() {
            ReservationFormDto dto = ReservationFormDto.builder()
                    .userId(1L)
                    .roomId(1L)
                    .startTime(LocalDateTime.now().plusHours(1))
                    .endTime(LocalDateTime.now().plusHours(3))
                    .type(com.campusRoom.api.entity.ReservationType.MEETING)
                    .build();

            User user = new User();
            Room room = new Room();

            when(reservationFactory.create(dto.type())).thenReturn(behavior);
            when(userService.getUserById(dto.userId())).thenReturn(user);
            when(roomService.getRoomById(dto.roomId())).thenReturn(room);
            when(behavior.getMaxDurationHours()).thenReturn(5);
            when(behavior.getDescription()).thenReturn("Meeting room");

            reservationService.create(dto);

            verify(validationStrategy1).validate(any(Reservation.class), eq(user));
            verify(validationStrategy2).validate(any(Reservation.class), eq(user));
            verify(reservationRepository).save(any(Reservation.class));
        }

        @Test
        @DisplayName("lance exception si durée dépasse le max")
        void shouldThrowException_whenDurationExceedsMax() {
            ReservationFormDto dto = ReservationFormDto.builder()
                    .userId(1L)
                    .roomId(1L)
                    .startTime(LocalDateTime.now())
                    .endTime(LocalDateTime.now().plusHours(10))
                    .type(com.campusRoom.api.entity.ReservationType.MEETING)
                    .build();

            when(reservationFactory.create(dto.type())).thenReturn(behavior);
            when(userService.getUserById(dto.userId())).thenReturn(new User());
            when(roomService.getRoomById(dto.roomId())).thenReturn(new Room());
            when(behavior.getMaxDurationHours()).thenReturn(2);

            CampusRoomBusinessException ex = assertThrows(
                    CampusRoomBusinessException.class,
                    () -> reservationService.create(dto)
            );

            assertEquals(HttpStatus.CONFLICT, ex.getStatus());
            verify(reservationRepository, never()).save(any());
        }

        @Test
        @DisplayName("exécute toutes les stratégies de validation")
        void shouldExecuteAllValidationStrategies() {
            ReservationFormDto dto = ReservationFormDto.builder()
                    .userId(1L)
                    .roomId(1L)
                    .startTime(LocalDateTime.now().plusHours(1))
                    .endTime(LocalDateTime.now().plusHours(2))
                    .type(com.campusRoom.api.entity.ReservationType.MEETING)
                    .build();

            User user = new User();

            when(reservationFactory.create(any())).thenReturn(behavior);
            when(userService.getUserById(any())).thenReturn(user);
            when(roomService.getRoomById(any())).thenReturn(new Room());
            when(behavior.getMaxDurationHours()).thenReturn(5);
            when(behavior.getDescription()).thenReturn("desc");

            reservationService.create(dto);

            verify(validationStrategy1, times(1)).validate(any(), eq(user));
            verify(validationStrategy2, times(1)).validate(any(), eq(user));
        }
    }

    // =========================
    // getReservationWithAllProperties
    // =========================

    @Nested
    @DisplayName("getReservationWithAllProperties")
    class GetReservationTests {

        @Test
        @DisplayName("retourne DTO si réservation trouvée")
        void shouldReturnDto_whenReservationExists() {
            Reservation reservation = new Reservation();
            ReservationDto dto = mock(ReservationDto.class);

            when(reservationRepository.findReservationWithAllProperties(1L))
                    .thenReturn(reservation);
            when(reservationMapper.toDTO(reservation)).thenReturn(dto);

            ReservationDto result = reservationService.getReservationWithAllProperties(1L);

            assertEquals(dto, result);
            verify(reservationMapper).toDTO(reservation);
        }

        @Test
        @DisplayName("lance exception si réservation inexistante")
        void shouldThrowException_whenReservationNotFound() {
            when(reservationRepository.findReservationWithAllProperties(1L))
                    .thenReturn(null);

            assertThrows(CampusRoomBusinessException.class,
                    () -> reservationService.getReservationWithAllProperties(1L));

            verify(reservationMapper, never()).toDTO(any());
        }
    }

    // =========================
    // deleteById
    // =========================

    @Nested
    @DisplayName("deleteById")
    class DeleteTests {

        @Test
        @DisplayName("supprime la réservation si elle existe")
        void shouldDeleteReservation_whenExists() {
            when(reservationRepository.existsById(1L)).thenReturn(true);

            reservationService.deleteById(1L);

            verify(reservationRepository).deleteById(1L);
        }

        @Test
        @DisplayName("lance exception si réservation inexistante")
        void shouldThrowException_whenReservationNotFound() {
            when(reservationRepository.existsById(1L)).thenReturn(false);

            assertThrows(CampusRoomBusinessException.class,
                    () -> reservationService.deleteById(1L));

            verify(reservationRepository, never()).deleteById(any());
        }
    }
}