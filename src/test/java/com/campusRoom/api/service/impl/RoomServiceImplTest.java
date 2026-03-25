package com.campusRoom.api.service.impl;

import com.campusRoom.api.dto.formDto.RoomFormDto;
import com.campusRoom.api.dto.outPutDto.RoomDto;
import com.campusRoom.api.entity.Campus;
import com.campusRoom.api.entity.Room;
import com.campusRoom.api.exception.CampusRoomBusinessException;
import com.campusRoom.api.mapper.RoomMapper;
import com.campusRoom.api.repository.RoomRepository;
import com.campusRoom.api.service.CampusService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private CampusService campusService;

    @Mock
    private RoomMapper roomMapper;

    @InjectMocks
    private RoomServiceImpl roomService;

    private Campus buildCampus(Long id) {
        return Campus.builder()
                .id(id).name("ESGI").city("Paris").rooms(new ArrayList<>()).build();
    }

    private Room buildRoom(Long id, Campus campus) {
        return Room.builder()
                .id(id).name("Salle A").capacity(30).location("Bâtiment A")
                .campus(campus).reservations(new ArrayList<>()).equipment(new ArrayList<>()).build();
    }

    // ==================== verifyIfRoomExist ====================

    @Test
    @DisplayName("verifyIfRoomExist - doit retourner true quand la salle existe déjà")
    void should_returnTrue_when_roomExists() {
        when(roomRepository.existsByName("Salle A")).thenReturn(true);

        boolean result = roomService.verifyIfRoomExist("Salle A");

        assertThat(result).isTrue();
        verify(roomRepository).existsByName("Salle A");
    }

    @Test
    @DisplayName("verifyIfRoomExist - doit retourner false quand la salle n'existe pas")
    void should_returnFalse_when_roomDoesNotExist() {
        when(roomRepository.existsByName("Salle Z")).thenReturn(false);

        boolean result = roomService.verifyIfRoomExist("Salle Z");

        assertThat(result).isFalse();
        verify(roomRepository).existsByName("Salle Z");
    }

    // ==================== getByRoomName ====================

    @Test
    @DisplayName("getByRoomName - doit retourner le RoomDto quand la salle existe")
    void should_returnRoomDto_when_roomNameExists() {
        Campus campus = buildCampus(1L);
        Room room = buildRoom(1L, campus);
        RoomDto expectedDto = new RoomDto(1L, "Salle A", 30, "Bâtiment A",
                new ArrayList<>(), null, new ArrayList<>());
        when(roomRepository.findByName("Salle A")).thenReturn(Optional.of(room));
        when(roomMapper.toDTO(room)).thenReturn(expectedDto);

        RoomDto result = roomService.getByRoomName("Salle A");

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Salle A");
        verify(roomRepository).findByName("Salle A");
        verify(roomMapper).toDTO(room);
    }

    @Test
    @DisplayName("getByRoomName - doit lever CampusRoomBusinessException quand la salle est introuvable")
    void should_throwNotFoundException_when_roomNameNotFound() {
        when(roomRepository.findByName("Inconnue")).thenReturn(Optional.empty());

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> roomService.getByRoomName("Inconnue"));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ex.getMessage()).contains("Inconnue");
        verify(roomRepository).findByName("Inconnue");
        verifyNoInteractions(roomMapper);
    }

    // ==================== getRoomById ====================

    @Test
    @DisplayName("getRoomById - doit retourner la salle quand l'id existe")
    void should_returnRoom_when_roomIdExists() {
        Campus campus = buildCampus(1L);
        Room room = buildRoom(1L, campus);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        Room result = roomService.getRoomById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(roomRepository).findById(1L);
    }

    @Test
    @DisplayName("getRoomById - doit lever CampusRoomBusinessException quand l'id est introuvable")
    void should_throwNotFoundException_when_roomIdNotFound() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> roomService.getRoomById(99L));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(roomRepository).findById(99L);
    }

    // ==================== createRoom ====================

    @Test
    @DisplayName("createRoom - doit créer et persister la salle quand le campus existe et la salle n'existe pas encore")
    void should_createRoom_when_campusExistsAndRoomDoesNotExist() {
        RoomFormDto dto = new RoomFormDto("Salle B", 25, "Bâtiment B", 1L);
        Campus campus = buildCampus(1L);
        when(campusService.getCampusById(1L)).thenReturn(campus);
        when(roomRepository.existsByName("Salle B")).thenReturn(false);

        roomService.createRoom(dto);

        verify(campusService).getCampusById(1L);
        verify(roomRepository).existsByName("Salle B");
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    @DisplayName("createRoom - doit lever CampusRoomBusinessException quand le campus n'existe pas")
    void should_throwNotFoundException_when_campusNotFound() {
        RoomFormDto dto = new RoomFormDto("Salle B", 25, "Bâtiment B", 99L);
        when(campusService.getCampusById(99L)).thenThrow(
                new CampusRoomBusinessException("Campus introuvable", HttpStatus.NOT_FOUND));

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> roomService.createRoom(dto));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(roomRepository, never()).save(any());
    }

    @Test
    @DisplayName("createRoom - doit lever CampusRoomBusinessException quand la salle existe déjà")
    void should_throwConflictException_when_roomAlreadyExists() {
        RoomFormDto dto = new RoomFormDto("Salle A", 30, "Bâtiment A", 1L);
        Campus campus = buildCampus(1L);
        when(campusService.getCampusById(1L)).thenReturn(campus);
        when(roomRepository.existsByName("Salle A")).thenReturn(true);

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> roomService.createRoom(dto));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getMessage()).contains("Salle A");
        verify(roomRepository, never()).save(any());
    }

    // ==================== updateRoomCapacity ====================

    @Test
    @DisplayName("updateRoomCapacity - doit mettre à jour la capacité quand la salle existe")
    void should_updateCapacity_when_roomExists() {
        when(roomRepository.existsById(1L)).thenReturn(true);

        roomService.updateRoomCapacity(1L, 50);

        verify(roomRepository).existsById(1L);
        verify(roomRepository).updateRoomCapacity(1L, 50);
    }

    @Test
    @DisplayName("updateRoomCapacity - doit lever CampusRoomBusinessException quand la salle n'existe pas")
    void should_throwNotFoundException_when_roomNotFoundForCapacityUpdate() {
        when(roomRepository.existsById(99L)).thenReturn(false);

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> roomService.updateRoomCapacity(99L, 50));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(roomRepository, never()).updateRoomCapacity(any(), anyInt());
    }

    @Test
    @DisplayName("updateRoomCapacity - doit accepter une capacité minimale (1 place)")
    void should_updateCapacity_when_capacityIsOne() {
        when(roomRepository.existsById(1L)).thenReturn(true);

        roomService.updateRoomCapacity(1L, 1);

        verify(roomRepository).updateRoomCapacity(1L, 1);
    }

    // ==================== updateRoomName ====================

    @Test
    @DisplayName("updateRoomName - doit mettre à jour le nom quand la salle et le campus existent et le nom est disponible")
    void should_updateRoomName_when_noConflict() {
        Campus campus = buildCampus(1L);
        Room room = buildRoom(1L, campus);
        when(campusService.getCampusById(1L)).thenReturn(campus);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.existsByName("NouveauNom")).thenReturn(false);

        roomService.updateRoomName(1L, 1L, "NouveauNom");

        verify(roomRepository).updateRoomName(1L, "NouveauNom");
    }

    @Test
    @DisplayName("updateRoomName - doit lever CampusRoomBusinessException quand le campus est introuvable")
    void should_throwNotFoundException_when_campusNotFoundForRoomNameUpdate() {
        when(campusService.getCampusById(99L)).thenThrow(
                new CampusRoomBusinessException("Campus introuvable", HttpStatus.NOT_FOUND));

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> roomService.updateRoomName(99L, 1L, "NouveauNom"));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(roomRepository, never()).updateRoomName(any(), any());
    }

    @Test
    @DisplayName("updateRoomName - doit lever CampusRoomBusinessException quand la salle est introuvable")
    void should_throwNotFoundException_when_roomNotFoundForNameUpdate() {
        Campus campus = buildCampus(1L);
        when(campusService.getCampusById(1L)).thenReturn(campus);
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> roomService.updateRoomName(1L, 99L, "NouveauNom"));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(roomRepository, never()).updateRoomName(any(), any());
    }

    @Test
    @DisplayName("updateRoomName - doit lever CampusRoomBusinessException quand le nouveau nom est déjà pris sur le même campus")
    void should_throwConflictException_when_nameAlreadyTakenOnSameCampus() {
        Campus campus = buildCampus(1L);
        Room room = buildRoom(1L, campus);
        when(campusService.getCampusById(1L)).thenReturn(campus);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.existsByName("Salle B")).thenReturn(true);

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> roomService.updateRoomName(1L, 1L, "Salle B"));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getMessage()).contains("Salle B");
        verify(roomRepository, never()).updateRoomName(any(), any());
    }

    @Test
    @DisplayName("updateRoomName - ne doit pas lever d'exception quand la salle appartient à un campus différent (pas de conflit)")
    void should_updateRoomName_when_roomBelongsToDifferentCampus() {
        Campus campusA = buildCampus(1L);
        Campus campusB = buildCampus(2L);
        Room roomOnCampusB = buildRoom(5L, campusB);
        when(campusService.getCampusById(1L)).thenReturn(campusA);
        when(roomRepository.findById(5L)).thenReturn(Optional.of(roomOnCampusB));

        roomService.updateRoomName(1L, 5L, "NouveauNom");

        verify(roomRepository).updateRoomName(5L, "NouveauNom");
    }
}
