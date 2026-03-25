package com.campusRoom.api.mapper;

import com.campusRoom.api.dto.outPutDto.ReservationDto;
import com.campusRoom.api.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationMapperTest {

    private final ReservationMapper mapper = Mappers.getMapper(ReservationMapper.class);

    // ==================== Builders ====================

    private User buildUser(Long id) {
        return User.builder()
                .id(id).firstName("Alice").lastName("Dupont")
                .email("alice@esgi.fr").role(Role.STUDENT)
                .reservations(new ArrayList<>())
                .build();
    }

    private Campus buildCampus(Long id) {
        return Campus.builder()
                .id(id).name("ESGI Paris").city("Paris")
                .rooms(new ArrayList<>())
                .build();
    }

    private Room buildRoom(Long id) {
        Campus campus = buildCampus(1L);
        return Room.builder()
                .id(id).name("Salle A").capacity(30).location("Bâtiment A")
                .equipment(new ArrayList<>()).reservations(new ArrayList<>())
                .campus(campus)
                .build();
    }

    private Reservation buildReservation(Long id, ReservationType type) {
        return Reservation.builder()
                .id(id)
                .type(type)
                .startTime(LocalDateTime.of(2026, Month.APRIL, 10, 9, 0))
                .endTime(LocalDateTime.of(2026, Month.APRIL, 10, 11, 0))
                .description("Cours de mathématiques")
                .maxDurationHours(3)
                .user(buildUser(1L))
                .room(buildRoom(5L))
                .build();
    }

    // ==================== toDTO ====================

    @Test
    @DisplayName("toDTO - doit mapper id, type, startTime, endTime, description et maxDurationHours")
    void should_mapScalarFields_when_toDTO() {
        Reservation reservation = buildReservation(1L, ReservationType.COURSE);

        ReservationDto result = mapper.toDTO(reservation);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.type()).isEqualTo(ReservationType.COURSE);
        assertThat(result.startTime()).isEqualTo(LocalDateTime.of(2026, Month.APRIL, 10, 9, 0));
        assertThat(result.endTime()).isEqualTo(LocalDateTime.of(2026, Month.APRIL, 10, 11, 0));
        assertThat(result.description()).isEqualTo("Cours de mathématiques");
        assertThat(result.maxDurationHours()).isEqualTo(3);
    }

    @Test
    @DisplayName("toDTO - doit mapper user vers userDto")
    void should_mapUserToUserDto_when_toDTO() {
        Reservation reservation = buildReservation(1L, ReservationType.COURSE);

        ReservationDto result = mapper.toDTO(reservation);

        assertThat(result.userDto()).isNotNull();
        assertThat(result.userDto().id()).isEqualTo(1L);
        assertThat(result.userDto().firstName()).isEqualTo("Alice");
        assertThat(result.userDto().lastName()).isEqualTo("Dupont");
        assertThat(result.userDto().email()).isEqualTo("alice@esgi.fr");
        assertThat(result.userDto().role()).isEqualTo(Role.STUDENT);
    }

    @Test
    @DisplayName("toDTO - userDto.reservationDtoList doit être null ou vide (MapStruct brise le cycle Reservation→User→reservations→Reservation)")
    void should_returnNullOrEmptyReservationListInUserDto_to_avoidCircularRef() {
        Reservation reservation = buildReservation(1L, ReservationType.COURSE);

        ReservationDto result = mapper.toDTO(reservation);

        // MapStruct brise le cycle en mettant null sur la référence arrière
        assertThat(result.userDto().reservationDtoList()).isNullOrEmpty();
    }

    @Test
    @DisplayName("toDTO - doit mapper room vers roomDto")
    void should_mapRoomToRoomDto_when_toDTO() {
        Reservation reservation = buildReservation(1L, ReservationType.MEETING);

        ReservationDto result = mapper.toDTO(reservation);

        assertThat(result.roomDto()).isNotNull();
        assertThat(result.roomDto().id()).isEqualTo(5L);
        assertThat(result.roomDto().name()).isEqualTo("Salle A");
        assertThat(result.roomDto().capacity()).isEqualTo(30);
    }

    @Test
    @DisplayName("toDTO - roomDto doit contenir les champs scalaires de la salle (campusDto peut être null par cycle detection)")
    void should_mapRoomScalarFields_in_roomDto_when_toDTO() {
        Reservation reservation = buildReservation(1L, ReservationType.EXAM);

        ReservationDto result = mapper.toDTO(reservation);

        // MapStruct brise le cycle Reservation→Room→Campus→rooms→Room en mettant campusDto à null
        // dans le contexte inline de ReservationMapper → on ne vérifie que les champs scalaires de roomDto
        assertThat(result.roomDto().id()).isEqualTo(5L);
        assertThat(result.roomDto().name()).isEqualTo("Salle A");
        assertThat(result.roomDto().capacity()).isEqualTo(30);
    }

    @Test
    @DisplayName("toDTO - doit mapper correctement le type MEETING")
    void should_mapMeetingType_when_toDTO() {
        Reservation reservation = buildReservation(2L, ReservationType.MEETING);

        ReservationDto result = mapper.toDTO(reservation);

        assertThat(result.type()).isEqualTo(ReservationType.MEETING);
    }

    @Test
    @DisplayName("toDTO - doit mapper correctement le type EXAM")
    void should_mapExamType_when_toDTO() {
        Reservation reservation = buildReservation(3L, ReservationType.EXAM);

        ReservationDto result = mapper.toDTO(reservation);

        assertThat(result.type()).isEqualTo(ReservationType.EXAM);
    }

    @Test
    @DisplayName("toDTO - doit retourner null quand la réservation est null")
    void should_returnNull_when_reservationIsNull() {
        ReservationDto result = mapper.toDTO(null);

        assertThat(result).isNull();
    }
}
