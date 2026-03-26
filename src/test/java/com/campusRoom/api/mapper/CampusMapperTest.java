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

}