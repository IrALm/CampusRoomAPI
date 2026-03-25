package com.campusRoom.api.mapper;

import com.campusRoom.api.dto.outPutDto.CampusDto;
import com.campusRoom.api.dto.outPutDto.RoomDto;
import com.campusRoom.api.entity.Campus;
import com.campusRoom.api.entity.Room;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CampusMapperTest {

    private final CampusMapper mapper = Mappers.getMapper(CampusMapper.class);

    // ==================== Builders ====================

    private Campus buildCampus(Long id, String name, String city) {
        return Campus.builder()
                .id(id).name(name).city(city)
                .rooms(new ArrayList<>())
                .build();
    }

    private CampusDto buildCampusDto(Long id, String name, String city) {
        return new CampusDto(id, name, city, new ArrayList<>());
    }

    // ==================== toDTO ====================

    @Test
    @DisplayName("toDTO - doit mapper id, name et city correctement")
    void should_mapIdNameCity_when_toDTO() {
        Campus campus = buildCampus(1L, "ESGI Paris", "Paris");

        CampusDto result = mapper.toDTO(campus);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("ESGI Paris");
        assertThat(result.city()).isEqualTo("Paris");
    }

    @Test
    @DisplayName("toDTO - doit retourner une liste de salles vide quand le campus n'a pas de salles")
    void should_returnEmptyRoomDtoList_when_campusHasNoRooms() {
        Campus campus = buildCampus(2L, "ESGI Lyon", "Lyon");

        CampusDto result = mapper.toDTO(campus);

        assertThat(result.roomDtoList()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("toDTO - doit mapper rooms vers roomDtoList quand le campus a des salles")
    void should_mapRoomsToRoomDtoList_when_campusHasRooms() {
        Campus campus = buildCampus(1L, "ESGI Paris", "Paris");
        Room room = Room.builder()
                .id(10L).name("Salle A").capacity(30).location("Bâtiment A")
                .equipment(new ArrayList<>()).reservations(new ArrayList<>())
                .campus(campus)
                .build();
        campus.getRooms().add(room);

        CampusDto result = mapper.toDTO(campus);

        assertThat(result.roomDtoList()).hasSize(1);
        RoomDto roomDto = result.roomDtoList().get(0);
        assertThat(roomDto.id()).isEqualTo(10L);
        assertThat(roomDto.name()).isEqualTo("Salle A");
        assertThat(roomDto.capacity()).isEqualTo(30);
    }

    @Test
    @DisplayName("toDTO - doit retourner null quand le campus est null")
    void should_returnNull_when_campusIsNull() {
        CampusDto result = mapper.toDTO(null);

        assertThat(result).isNull();
    }

    // ==================== toEntity ====================

    @Test
    @DisplayName("toEntity - doit mapper id, name et city correctement")
    void should_mapIdNameCity_when_toEntity() {
        CampusDto dto = buildCampusDto(5L, "ESGI Bordeaux", "Bordeaux");

        Campus result = mapper.toEntity(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getName()).isEqualTo("ESGI Bordeaux");
        assertThat(result.getCity()).isEqualTo("Bordeaux");
    }

    @Test
    @DisplayName("toEntity - doit retourner une liste de rooms vide quand roomDtoList est vide")
    void should_returnEmptyRooms_when_roomDtoListIsEmpty() {
        CampusDto dto = buildCampusDto(1L, "ESGI Paris", "Paris");

        Campus result = mapper.toEntity(dto);

        assertThat(result.getRooms()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("toEntity - doit mapper roomDtoList vers rooms quand la liste contient des salles")
    void should_mapRoomDtoListToRooms_when_dtosProvided() {
        RoomDto roomDto = new RoomDto(10L, "Salle B", 20, "Bâtiment B",
                new ArrayList<>(), null, new ArrayList<>());
        CampusDto dto = new CampusDto(1L, "ESGI Paris", "Paris", List.of(roomDto));

        Campus result = mapper.toEntity(dto);

        assertThat(result.getRooms()).hasSize(1);
        assertThat(result.getRooms().get(0).getId()).isEqualTo(10L);
        assertThat(result.getRooms().get(0).getName()).isEqualTo("Salle B");
    }

    @Test
    @DisplayName("toEntity - doit retourner null quand le DTO est null")
    void should_returnNull_when_dtoCampusIsNull() {
        Campus result = mapper.toEntity(null);

        assertThat(result).isNull();
    }
}
