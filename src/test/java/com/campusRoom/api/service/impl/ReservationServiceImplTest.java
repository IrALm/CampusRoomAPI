package com.campusRoom.api.service.impl;

import com.campusRoom.api.dto.formDto.ReservationFormDto;
import com.campusRoom.api.entity.*;
import com.campusRoom.api.exception.CampusRoomBusinessException;
import com.campusRoom.api.repository.ReservationRepository;
import com.campusRoom.api.service.RoomService;
import com.campusRoom.api.service.UserService;
import com.campusRoom.api.service.patternFactory.ReservationBehavior;
import com.campusRoom.api.service.patternFactory.ReservationFactory;
import com.campusRoom.api.service.patternStrategy.ValidationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationFactory reservationFactory;

    @Mock
    private ValidationStrategy validationStrategy;

    @Mock
    private UserService userService;

    @Mock
    private RoomService roomService;

    // Construction manuelle car List<ValidationStrategy> n'est pas injectable via @InjectMocks
    private ReservationServiceImpl reservationService;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationServiceImpl(
                reservationRepository,
                reservationFactory,
                List.of(validationStrategy),
                userService,
                roomService
        );
    }

    private User buildStudent() {
        return User.builder().id(1L).firstName("Alice").lastName("Dupont")
                .email("alice@esgi.fr").role(Role.STUDENT).reservations(new ArrayList<>()).build();
    }

    private User buildTeacher() {
        return User.builder().id(2L).firstName("Prof").lastName("Martin")
                .email("prof@esgi.fr").role(Role.TEACHER).reservations(new ArrayList<>()).build();
    }

    private Room buildRoom() {
        Campus campus = Campus.builder().id(1L).name("ESGI").city("Paris").rooms(new ArrayList<>()).build();
        return Room.builder().id(1L).name("Salle A").capacity(30).location("Bâtiment A")
                .campus(campus).reservations(new ArrayList<>()).equipment(new ArrayList<>()).build();
    }

    private ReservationBehavior fakeBehavior(String description, int maxHours) {
        return new ReservationBehavior() {
            @Override
            public String getDescription() { return description; }
            @Override
            public int getMaxDurationHours() { return maxHours; }
        };
    }

    // ==================== create — happy paths ====================

    @Test
    @DisplayName("create - doit créer et retourner la réservation COURSE quand la durée est dans la limite")
    void should_createAndReturnReservation_when_courseTypeWithValidDuration() {
        LocalDateTime start = LocalDateTime.of(2026, 4, 1, 9, 0);
        LocalDateTime end   = LocalDateTime.of(2026, 4, 1, 11, 0); // 2h ≤ 3h max
        ReservationFormDto dto = ReservationFormDto.builder()
                .type(ReservationType.COURSE).startTime(start).endTime(end)
                .roomId(1L).userId(1L).build();
        User user = buildStudent();
        Room room = buildRoom();
        Reservation saved = new Reservation();
        saved.setId(1L);

        when(reservationFactory.create(ReservationType.COURSE)).thenReturn(fakeBehavior("Cours", 3));
        when(userService.getUserById(1L)).thenReturn(user);
        when(roomService.getRoomById(1L)).thenReturn(room);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(saved);

        Reservation result = reservationService.create(dto, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(reservationFactory).create(ReservationType.COURSE);
        verify(userService).getUserById(1L);
        verify(roomService).getRoomById(1L);
        verify(validationStrategy).validate(any(Reservation.class), eq(user));
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    @DisplayName("create - doit créer et retourner la réservation MEETING quand la durée est dans la limite")
    void should_createAndReturnReservation_when_meetingTypeWithValidDuration() {
        LocalDateTime start = LocalDateTime.of(2026, 4, 1, 14, 0);
        LocalDateTime end   = LocalDateTime.of(2026, 4, 1, 15, 0); // 1h ≤ 2h max
        ReservationFormDto dto = ReservationFormDto.builder()
                .type(ReservationType.MEETING).startTime(start).endTime(end)
                .roomId(1L).userId(1L).build();
        User user = buildStudent();
        Room room = buildRoom();
        Reservation saved = new Reservation();
        saved.setId(2L);

        when(reservationFactory.create(ReservationType.MEETING)).thenReturn(fakeBehavior("Réunion", 2));
        when(userService.getUserById(1L)).thenReturn(user);
        when(roomService.getRoomById(1L)).thenReturn(room);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(saved);

        Reservation result = reservationService.create(dto, 1L);

        assertThat(result).isNotNull();
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    @DisplayName("create - doit créer et retourner la réservation EXAM quand la durée est dans la limite")
    void should_createAndReturnReservation_when_examTypeWithValidDuration() {
        LocalDateTime start = LocalDateTime.of(2026, 4, 1, 8, 0);
        LocalDateTime end   = LocalDateTime.of(2026, 4, 1, 11, 0); // 3h ≤ 4h max
        ReservationFormDto dto = ReservationFormDto.builder()
                .type(ReservationType.EXAM).startTime(start).endTime(end)
                .roomId(1L).userId(1L).build();
        User user = buildStudent();
        Room room = buildRoom();
        Reservation saved = new Reservation();
        saved.setId(3L);

        when(reservationFactory.create(ReservationType.EXAM)).thenReturn(fakeBehavior("Examen", 4));
        when(userService.getUserById(1L)).thenReturn(user);
        when(roomService.getRoomById(1L)).thenReturn(room);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(saved);

        Reservation result = reservationService.create(dto, 1L);

        assertThat(result).isNotNull();
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    @DisplayName("create - doit accepter une durée exactement égale à la durée maximale autorisée")
    void should_createReservation_when_durationEqualsMaxAllowed() {
        LocalDateTime start = LocalDateTime.of(2026, 4, 1, 9, 0);
        LocalDateTime end   = LocalDateTime.of(2026, 4, 1, 12, 0); // 3h == 3h max
        ReservationFormDto dto = ReservationFormDto.builder()
                .type(ReservationType.COURSE).startTime(start).endTime(end)
                .roomId(1L).userId(1L).build();
        User user = buildStudent();
        Room room = buildRoom();

        when(reservationFactory.create(ReservationType.COURSE)).thenReturn(fakeBehavior("Cours", 3));
        when(userService.getUserById(1L)).thenReturn(user);
        when(roomService.getRoomById(1L)).thenReturn(room);
        when(reservationRepository.save(any())).thenReturn(new Reservation());

        reservationService.create(dto, 1L);

        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    @DisplayName("create - doit appeler toutes les stratégies de validation enregistrées")
    void should_callAllValidationStrategies_when_allStrategiesPass() {
        LocalDateTime start = LocalDateTime.of(2026, 4, 1, 9, 0);
        LocalDateTime end   = LocalDateTime.of(2026, 4, 1, 10, 0);
        ReservationFormDto dto = ReservationFormDto.builder()
                .type(ReservationType.COURSE).startTime(start).endTime(end)
                .roomId(1L).userId(1L).build();
        User user = buildStudent();
        Room room = buildRoom();

        when(reservationFactory.create(ReservationType.COURSE)).thenReturn(fakeBehavior("Cours", 3));
        when(userService.getUserById(1L)).thenReturn(user);
        when(roomService.getRoomById(1L)).thenReturn(room);
        when(reservationRepository.save(any())).thenReturn(new Reservation());

        reservationService.create(dto, 1L);

        verify(validationStrategy, times(1)).validate(any(Reservation.class), eq(user));
        verify(reservationRepository).save(any(Reservation.class));
    }

    // ==================== create — durée dépassée ====================

    @Test
    @DisplayName("create - doit lever CampusRoomBusinessException quand la durée dépasse le maximum autorisé")
    void should_throwConflictException_when_durationExceedsMaxAllowed() {
        LocalDateTime start = LocalDateTime.of(2026, 4, 1, 9, 0);
        LocalDateTime end   = LocalDateTime.of(2026, 4, 1, 14, 0); // 5h > 3h max
        ReservationFormDto dto = ReservationFormDto.builder()
                .type(ReservationType.COURSE).startTime(start).endTime(end)
                .roomId(1L).userId(1L).build();
        User user = buildStudent();
        Room room = buildRoom();

        when(reservationFactory.create(ReservationType.COURSE)).thenReturn(fakeBehavior("Cours", 3));
        when(userService.getUserById(1L)).thenReturn(user);
        when(roomService.getRoomById(1L)).thenReturn(room);

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> reservationService.create(dto, 1L));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getMessage()).contains("3h");
        verify(reservationRepository, never()).save(any());
        verifyNoInteractions(validationStrategy);
    }

    @Test
    @DisplayName("create - doit lever CampusRoomBusinessException quand la durée d'examen est dépassée")
    void should_throwConflictException_when_examDurationExceedsMax() {
        LocalDateTime start = LocalDateTime.of(2026, 4, 1, 8, 0);
        LocalDateTime end   = LocalDateTime.of(2026, 4, 1, 14, 0); // 6h > 4h max
        ReservationFormDto dto = ReservationFormDto.builder()
                .type(ReservationType.EXAM).startTime(start).endTime(end)
                .roomId(1L).userId(1L).build();
        User user = buildStudent();
        Room room = buildRoom();

        when(reservationFactory.create(ReservationType.EXAM)).thenReturn(fakeBehavior("Examen", 4));
        when(userService.getUserById(1L)).thenReturn(user);
        when(roomService.getRoomById(1L)).thenReturn(room);

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> reservationService.create(dto, 1L));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getMessage()).contains("4h");
        verify(reservationRepository, never()).save(any());
    }

    // ==================== create — entités introuvables ====================

    @Test
    @DisplayName("create - doit propager l'exception quand l'utilisateur est introuvable")
    void should_propagateException_when_userNotFound() {
        LocalDateTime start = LocalDateTime.of(2026, 4, 1, 9, 0);
        LocalDateTime end   = LocalDateTime.of(2026, 4, 1, 11, 0);
        ReservationFormDto dto = ReservationFormDto.builder()
                .type(ReservationType.COURSE).startTime(start).endTime(end)
                .roomId(1L).userId(99L).build();

        when(reservationFactory.create(ReservationType.COURSE)).thenReturn(fakeBehavior("Cours", 3));
        when(userService.getUserById(99L)).thenThrow(
                new CampusRoomBusinessException("Utilisateur introuvable", HttpStatus.NOT_FOUND));

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> reservationService.create(dto, 99L));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("create - doit propager l'exception quand la salle est introuvable")
    void should_propagateException_when_roomNotFound() {
        LocalDateTime start = LocalDateTime.of(2026, 4, 1, 9, 0);
        LocalDateTime end   = LocalDateTime.of(2026, 4, 1, 11, 0);
        ReservationFormDto dto = ReservationFormDto.builder()
                .type(ReservationType.COURSE).startTime(start).endTime(end)
                .roomId(99L).userId(1L).build();
        User user = buildStudent();

        when(reservationFactory.create(ReservationType.COURSE)).thenReturn(fakeBehavior("Cours", 3));
        when(userService.getUserById(1L)).thenReturn(user);
        when(roomService.getRoomById(99L)).thenThrow(
                new CampusRoomBusinessException("Salle introuvable", HttpStatus.NOT_FOUND));

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> reservationService.create(dto, 1L));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(reservationRepository, never()).save(any());
    }

    // ==================== create — stratégie échoue ====================

    @Test
    @DisplayName("create - doit propager l'exception de validation quand une stratégie détecte un conflit")
    void should_propagateException_when_validationStrategyThrowsConflict() {
        LocalDateTime start = LocalDateTime.of(2026, 4, 1, 9, 0);
        LocalDateTime end   = LocalDateTime.of(2026, 4, 1, 11, 0);
        ReservationFormDto dto = ReservationFormDto.builder()
                .type(ReservationType.COURSE).startTime(start).endTime(end)
                .roomId(1L).userId(1L).build();
        User user = buildStudent();
        Room room = buildRoom();

        when(reservationFactory.create(ReservationType.COURSE)).thenReturn(fakeBehavior("Cours", 3));
        when(userService.getUserById(1L)).thenReturn(user);
        when(roomService.getRoomById(1L)).thenReturn(room);
        doThrow(new CampusRoomBusinessException("Créneau déjà réservé", HttpStatus.CONFLICT))
                .when(validationStrategy).validate(any(Reservation.class), eq(user));

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> reservationService.create(dto, 1L));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getMessage()).contains("déjà réservé");
        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("create - doit propager l'exception de quota quand la stratégie quota rejette la réservation")
    void should_propagateException_when_quotaStrategyRejectsReservation() {
        LocalDateTime start = LocalDateTime.of(2026, 4, 1, 9, 0);
        LocalDateTime end   = LocalDateTime.of(2026, 4, 1, 10, 0);
        ReservationFormDto dto = ReservationFormDto.builder()
                .type(ReservationType.COURSE).startTime(start).endTime(end)
                .roomId(1L).userId(1L).build();
        User user = buildStudent();
        Room room = buildRoom();

        when(reservationFactory.create(ReservationType.COURSE)).thenReturn(fakeBehavior("Cours", 3));
        when(userService.getUserById(1L)).thenReturn(user);
        when(roomService.getRoomById(1L)).thenReturn(room);
        doThrow(new CampusRoomBusinessException("Quota mensuel atteint (5 réservations max).", HttpStatus.CONFLICT))
                .when(validationStrategy).validate(any(Reservation.class), eq(user));

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> reservationService.create(dto, 1L));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("create - doit correctement définir les champs de la réservation avant de sauvegarder")
    void should_setAllReservationFields_before_saving() {
        LocalDateTime start = LocalDateTime.of(2026, 4, 1, 9, 0);
        LocalDateTime end   = LocalDateTime.of(2026, 4, 1, 11, 0); // 2h
        ReservationFormDto dto = ReservationFormDto.builder()
                .type(ReservationType.COURSE).startTime(start).endTime(end)
                .roomId(1L).userId(1L).build();
        User user = buildStudent();
        Room room = buildRoom();
        String expectedDescription = "Réservation de cours";

        when(reservationFactory.create(ReservationType.COURSE)).thenReturn(fakeBehavior(expectedDescription, 3));
        when(userService.getUserById(1L)).thenReturn(user);
        when(roomService.getRoomById(1L)).thenReturn(room);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        Reservation result = reservationService.create(dto, 1L);

        assertThat(result.getStartTime()).isEqualTo(start);
        assertThat(result.getEndTime()).isEqualTo(end);
        assertThat(result.getType()).isEqualTo(ReservationType.COURSE);
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getRoom()).isEqualTo(room);
        assertThat(result.getDescription()).isEqualTo(expectedDescription);
        assertThat(result.getMaxDurationHours()).isEqualTo(2);
    }
}
