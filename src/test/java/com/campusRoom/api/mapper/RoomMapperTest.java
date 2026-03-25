package com.campusRoom.api.mapper;

import com.campusRoom.api.dto.outPutDto.RoomDto;
import com.campusRoom.api.entity.Campus;
import com.campusRoom.api.entity.Room;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RoomMapperTest {

    private final RoomMapper mapper = Mappers.getMapper(RoomMapper.class);

    // ==================== Builders ====================

    private Campus buildCampus(Long id) {
        return Campus.builder()
                .id(id).name("ESGI Paris").city("Paris")
                .rooms(new ArrayList<>())
                .build();
    }

    private Room buildRoom(Long id, Campus campus) {
        return Room.builder()
                .id(id).name("Salle A101").capacity(30).location("Bâtiment A - 1er étage")
                .equipment(new ArrayList<>()).reservations(new ArrayList<>())
                .campus(campus)
                .build();
    }

    // ==================== toDTO ====================

    @Test
    @DisplayName("toDTO - doit mapper id, name, capacity et location correctement")
    void should_mapScalarFields_when_toDTO() {
        Campus campus = buildCampus(1L);
        Room room = buildRoom(10L, campus);

        RoomDto result = mapper.toDTO(room);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.name()).isEqualTo("Salle A101");
        assertThat(result.capacity()).isEqualTo(30);
        assertThat(result.location()).isEqualTo("Bâtiment A - 1er étage");
    }

    @Test
    @DisplayName("toDTO - doit mapper la liste des équipements")
    void should_mapEquipmentList_when_toDTO() {
        Campus campus = buildCampus(1L);
        Room room = Room.builder()
                .id(1L).name("Salle B").capacity(20).location("Bâtiment B")
                .equipment(List.of("Projecteur", "Tableau blanc"))
                .reservations(new ArrayList<>())
                .campus(campus)
                .build();

        RoomDto result = mapper.toDTO(room);

        assertThat(result.equipment()).containsExactly("Projecteur", "Tableau blanc");
    }

    @Test
    @DisplayName("toDTO - doit mapper campus vers campusDto")
    void should_mapCampusToCampusDto_when_toDTO() {
        Campus campus = buildCampus(1L);
        Room room = buildRoom(10L, campus);

        RoomDto result = mapper.toDTO(room);

        assertThat(result.campusDto()).isNotNull();
        assertThat(result.campusDto().id()).isEqualTo(1L);
        assertThat(result.campusDto().name()).isEqualTo("ESGI Paris");
        assertThat(result.campusDto().city()).isEqualTo("Paris");
    }

    @Test
    @DisplayName("toDTO - campusDto.roomDtoList doit être null ou vide (MapStruct brise le cycle Campus→Room→Campus)")
    void should_returnNullOrEmptyRoomDtoList_in_campusDto_to_avoidCircularRef() {
        Campus campus = buildCampus(1L);
        Room room = buildRoom(10L, campus);

        RoomDto result = mapper.toDTO(room);

        // MapStruct brise le cycle Room→Campus→rooms→Room en mettant null sur la liste arrière
        assertThat(result.campusDto().roomDtoList()).isNullOrEmpty();
    }

    @Test
    @DisplayName("toDTO - doit retourner une liste de réservations vide quand la salle n'a pas de réservations")
    void should_returnEmptyReservationDtoList_when_noReservations() {
        Campus campus = buildCampus(1L);
        Room room = buildRoom(10L, campus);

        RoomDto result = mapper.toDTO(room);

        assertThat(result.reservationDtoList()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("toDTO - doit retourner null quand la salle est null")
    void should_returnNull_when_roomIsNull() {
        RoomDto result = mapper.toDTO(null);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("toDTO - doit mapper une liste d'équipements vide sans erreur")
    void should_mapEmptyEquipment_without_error() {
        Campus campus = buildCampus(1L);
        Room room = buildRoom(5L, campus);

        RoomDto result = mapper.toDTO(room);

        assertThat(result.equipment()).isNotNull().isEmpty();
    }
}
