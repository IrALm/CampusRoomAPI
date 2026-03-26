package com.campusRoom.api.mapper;

import com.campusRoom.api.dto.outPutDto.CampusDto;
import com.campusRoom.api.dto.outPutDto.RoomDto;
import com.campusRoom.api.entity.Campus;
import com.campusRoom.api.entity.Room;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitaires pour CampusMapper")
class CampusMapperTest {

    private final CampusMapper mapper = Mappers.getMapper(CampusMapper.class);

    @Test
    @DisplayName("toDTO doit mapper correctement Campus vers CampusDto")
    void shouldMapCampusToDto() {
        // GIVEN
        Room room = Room.builder()
                .id(1L)
                .name("Salle A")
                .capacity(20)
                .build();

        Campus campus = Campus.builder()
                .id(10L)
                .name("Campus Nantes")
                .city("Nantes")
                .rooms(List.of(room))
                .build();

        // WHEN
        CampusDto dto = mapper.toDTO(campus);

        // THEN
        assertNotNull(dto);
        assertEquals(10L, dto.id());
        assertEquals("Campus Nantes", dto.name());
        assertEquals("Nantes", dto.city());

        assertNotNull(dto.roomDtoList());
        assertEquals(1, dto.roomDtoList().size());
        assertEquals("Salle A", dto.roomDtoList().get(0).name());
    }

    @Test
    @DisplayName("toEntity doit mapper correctement CampusDto vers Campus")
    void shouldMapDtoToCampus() {
        // GIVEN
        RoomDto roomDto = RoomDto.builder()
                .id(1L)
                .name("Salle B")
                .capacity(30)
                .build();

        CampusDto dto = CampusDto.builder()
                .id(20L)
                .name("Campus Paris")
                .city("Paris")
                .roomDtoList(List.of(roomDto))
                .build();

        // WHEN
        Campus campus = mapper.toEntity(dto);

        // THEN
        assertNotNull(campus);
        assertEquals(20L, campus.getId());
        assertEquals("Campus Paris", campus.getName());
        assertEquals("Paris", campus.getCity());

        assertNotNull(campus.getRooms());
        assertEquals(1, campus.getRooms().size());
        assertEquals("Salle B", campus.getRooms().get(0).getName());
    }

    @Test
    @DisplayName("toDTO doit gérer les listes null")
    void shouldHandleNullRooms() {
        Campus campus = Campus.builder()
                .id(1L)
                .name("Campus Vide")
                .city("Lyon")
                .rooms(null)
                .build();

        CampusDto dto = mapper.toDTO(campus);

        assertNotNull(dto);
        assertNull(dto.roomDtoList());
    }

    @Test
    @DisplayName("toEntity doit gérer les listes null")
    void shouldHandleNullRoomDtoList() {
        CampusDto dto = CampusDto.builder()
                .id(1L)
                .name("Campus Vide")
                .city("Lyon")
                .roomDtoList(null)
                .build();

        Campus campus = mapper.toEntity(dto);

        assertNotNull(campus);
        assertNull(campus.getRooms());
    }
}