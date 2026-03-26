package com.campusRoom.api.mapper;

import com.campusRoom.api.dto.outPutDto.UserDto;
import com.campusRoom.api.entity.Reservation;
import com.campusRoom.api.entity.ReservationType;
import com.campusRoom.api.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitaires pour UserMapper")
class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    @DisplayName("toDTO doit mapper correctement User vers UserDto")
    void shouldMapUserToDto() {
        // GIVEN
        Reservation reservation = Reservation.builder()
                .id(1L)
                .type(ReservationType.COURSE)
                .startTime(LocalDateTime.of(2026, 3, 25, 10, 0))
                .endTime(LocalDateTime.of(2026, 3, 25, 12, 0))
                .description("Cours Java")
                .maxDurationHours(3)
                .build();

        User user = User.builder()
                .id(10L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .reservations(List.of(reservation))
                .build();

        // WHEN
        UserDto dto = mapper.toDTO(user);

        // THEN
        assertNotNull(dto);
        assertEquals(10L, dto.id());
        assertEquals("John", dto.firstName());
        assertEquals("Doe", dto.lastName());
        assertEquals("john.doe@test.com", dto.email());

        // mapping liste reservations
        assertNotNull(dto.reservationDtoList());
        assertEquals(1, dto.reservationDtoList().size());
        assertEquals(ReservationType.COURSE, dto.reservationDtoList().get(0).type());
    }

    @Test
    @DisplayName("toDTO doit gérer reservations null")
    void shouldHandleNullReservations() {
        User user = User.builder()
                .id(1L)
                .firstName("Jane")
                .build();

        UserDto dto = mapper.toDTO(user);

        assertNotNull(dto);
        assertNull(dto.reservationDtoList());
    }

    @Test
    @DisplayName("toDTO doit gérer liste vide de reservations")
    void shouldHandleEmptyReservations() {
        User user = User.builder()
                .id(1L)
                .reservations(List.of())
                .build();

        UserDto dto = mapper.toDTO(user);

        assertNotNull(dto);
        assertNotNull(dto.reservationDtoList());
        assertTrue(dto.reservationDtoList().isEmpty());
    }

    @Test
    @DisplayName("toDTO doit gérer user null")
    void shouldHandleNullUser() {
        UserDto dto = mapper.toDTO(null);
        assertNull(dto);
    }
}