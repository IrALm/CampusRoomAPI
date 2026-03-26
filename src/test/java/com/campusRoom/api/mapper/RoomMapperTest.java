package com.campusRoom.api.mapper;

import com.campusRoom.api.dto.outPutDto.RoomDto;
import com.campusRoom.api.entity.Campus;
import com.campusRoom.api.entity.Reservation;
import com.campusRoom.api.entity.ReservationType;
import com.campusRoom.api.entity.Room;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitaires pour RoomMapper")
class RoomMapperTest {

    private final RoomMapper mapper = Mappers.getMapper(RoomMapper.class);

    @Test
    @DisplayName("toDTO doit mapper correctement Room vers RoomDto")
    void shouldMapRoomToDto() {
        // GIVEN
        Campus campus = Campus.builder()
                .id(1L)
                .name("Campus Nantes")
                .city("Nantes")
                .build();

        Reservation reservation = Reservation.builder()
                .id(100L)
                .type(ReservationType.MEETING)
                .startTime(LocalDateTime.of(2026, 3, 25, 10, 0))
                .endTime(LocalDateTime.of(2026, 3, 25, 12, 0))
                .description("Meeting équipe")
                .maxDurationHours(2)
                .build();

        Room room = Room.builder()
                .id(10L)
                .name("Salle A")
                .capacity(20)
                .campus(campus)
                .reservations(List.of(reservation))
                .build();

        // WHEN
        RoomDto dto = mapper.toDTO(room);

        // THEN
        assertNotNull(dto);
        assertEquals(10L, dto.id());
        assertEquals("Salle A", dto.name());
        assertEquals(20, dto.capacity());

        // campus mapping
        assertNotNull(dto.campusDto());
        assertEquals("Campus Nantes", dto.campusDto().name());

        // reservations mapping
        assertNotNull(dto.reservationDtoList());
        assertEquals(1, dto.reservationDtoList().size());
        assertEquals(ReservationType.MEETING, dto.reservationDtoList().get(0).type());
    }

    @Test
    @DisplayName("toDTO doit gérer campus null")
    void shouldHandleNullCampus() {
        Room room = Room.builder()
                .id(1L)
                .name("Salle X")
                .capacity(10)
                .campus(null)
                .build();

        RoomDto dto = mapper.toDTO(room);

        assertNotNull(dto);
        assertNull(dto.campusDto());
    }

    @Test
    @DisplayName("toDTO doit gérer reservations null")
    void shouldHandleNullReservations() {
        Room room = Room.builder()
                .id(1L)
                .name("Salle Y")
                .capacity(15)
                .reservations(null)
                .build();

        RoomDto dto = mapper.toDTO(room);

        assertNotNull(dto);
        assertNull(dto.reservationDtoList());
    }

    @Test
    @DisplayName("toDTO doit gérer liste vide de reservations")
    void shouldHandleEmptyReservations() {
        Room room = Room.builder()
                .id(1L)
                .name("Salle Z")
                .capacity(15)
                .reservations(List.of())
                .build();

        RoomDto dto = mapper.toDTO(room);

        assertNotNull(dto);
        assertNotNull(dto.reservationDtoList());
        assertTrue(dto.reservationDtoList().isEmpty());
    }

    @Test
    @DisplayName("toDTO doit gérer room null")
    void shouldHandleNullRoom() {
        RoomDto dto = mapper.toDTO(null);
        assertNull(dto);
    }
}