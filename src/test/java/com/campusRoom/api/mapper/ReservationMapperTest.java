package com.campusRoom.api.mapper;

import com.campusRoom.api.dto.outPutDto.ReservationDto;
import com.campusRoom.api.entity.Reservation;
import com.campusRoom.api.entity.ReservationType;
import com.campusRoom.api.entity.Room;
import com.campusRoom.api.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitaires pour ReservationMapper")
class ReservationMapperTest {

    private final ReservationMapper mapper = Mappers.getMapper(ReservationMapper.class);

    @Test
    @DisplayName("toDTO doit mapper correctement Reservation vers ReservationDto")
    void shouldMapReservationToDto() {
        // GIVEN
        User user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .build();

        Room room = Room.builder()
                .id(10L)
                .name("Salle A")
                .capacity(20)
                .build();

        Reservation reservation = Reservation.builder()
                .id(100L)
                .type(ReservationType.COURSE)
                .startTime(LocalDateTime.of(2026, 3, 25, 10, 0))
                .endTime(LocalDateTime.of(2026, 3, 25, 12, 0))
                .description("Cours Java")
                .maxDurationHours(3)
                .user(user)
                .room(room)
                .build();

        // WHEN
        ReservationDto dto = mapper.toDTO(reservation);

        // THEN
        assertNotNull(dto);
        assertEquals(100L, dto.id());
        assertEquals(ReservationType.COURSE, dto.type());
        assertEquals("Cours Java", dto.description());
        assertEquals(3, dto.maxDurationHours());

        // Vérifie mapping imbriqué
        assertNotNull(dto.userDto());
        assertEquals("John", dto.userDto().firstName());

        assertNotNull(dto.roomDto());
        assertEquals("Salle A", dto.roomDto().name());
    }

    @Test
    @DisplayName("toDTO doit gérer user null")
    void shouldHandleNullUser() {
        Reservation reservation = Reservation.builder()
                .id(1L)
                .room(Room.builder().name("Salle X").build())
                .build();

        ReservationDto dto = mapper.toDTO(reservation);

        assertNotNull(dto);
        assertNull(dto.userDto());
        assertNotNull(dto.roomDto());
    }

    @Test
    @DisplayName("toDTO doit gérer room null")
    void shouldHandleNullRoom() {
        Reservation reservation = Reservation.builder()
                .id(1L)
                .user(User.builder().firstName("John").build())
                .build();

        ReservationDto dto = mapper.toDTO(reservation);

        assertNotNull(dto);
        assertNotNull(dto.userDto());
        assertNull(dto.roomDto());
    }

    @Test
    @DisplayName("toDTO doit gérer reservation null")
    void shouldHandleNullReservation() {
        ReservationDto dto = mapper.toDTO(null);
        assertNull(dto);
    }
}