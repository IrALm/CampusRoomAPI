package com.campusRoom.api.service.impl;

import com.campusRoom.api.dto.formDto.RoomFormDto;
import com.campusRoom.api.dto.outPutDto.CampusDto;
import com.campusRoom.api.dto.outPutDto.RoomDto;
import com.campusRoom.api.entity.Campus;
import com.campusRoom.api.entity.Room;
import com.campusRoom.api.exception.CampusRoomBusinessException;
import com.campusRoom.api.mapper.RoomMapper;
import com.campusRoom.api.repository.RoomRepository;
import com.campusRoom.api.service.CampusService;
import com.campusRoom.api.service.ReservationChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service RoomServiceImpl")
class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomMapper roomMapper;

    @Mock
    private CampusService campusService;

    @Mock
    private ReservationChecker reservationChecker;

    @InjectMocks
    private RoomServiceImpl roomService;

    private Campus campus;

    @BeforeEach
    void setup() {
        campus = new Campus();
        campus.setId(1L);
        campus.setName("Campus A");
        campus.setRooms(new ArrayList<>());
    }

    // =========================
    // verifyIfRoomExist
    // =========================

    @Nested
    @DisplayName("verifyIfRoomExist")
    class VerifyIfRoomExistTests {

        @Test
        @DisplayName("retourne true si room existe")
        void shouldReturnTrue() {
            when(roomRepository.existsByName("Room1")).thenReturn(true);
            assertTrue(roomService.verifyIfRoomExist("Room1"));
        }

        @Test
        @DisplayName("retourne false si room n'existe pas")
        void shouldReturnFalse() {
            when(roomRepository.existsByName("Room1")).thenReturn(false);
            assertFalse(roomService.verifyIfRoomExist("Room1"));
        }
    }

    // =========================
    // getByRoomName
    // =========================

    @Nested
    @DisplayName("getByRoomName")
    class GetByRoomNameTests {

        @Test
        @DisplayName("retourne DTO si room trouvée")
        void shouldReturnDto() {
            Room room = new Room();
            RoomDto dto = RoomDto.builder()
                    .id(1L)
                    .name("Room A")
                    .capacity(10)
                    .location("First Floor")
                    .equipment(List.of("Projector", "Whiteboard"))
                    .campusDto(CampusDto.builder().id(1L).name("Campus A").build())
                    .reservationDtoList(List.of())
                    .build();

            when(roomRepository.findByName("Room1")).thenReturn(Optional.of(room));
            when(roomMapper.toDTO(room)).thenReturn(dto);

            assertEquals(dto, roomService.getByRoomName("Room1"));
        }

        @Test
        @DisplayName("lance exception si room non trouvée")
        void shouldThrowExceptionIfNotFound() {
            when(roomRepository.findByName("Room1")).thenReturn(Optional.empty());

            assertThrows(CampusRoomBusinessException.class, () -> roomService.getByRoomName("Room1"));
        }
    }

    // =========================
    // getRoomById
    // =========================

    @Nested
    @DisplayName("getRoomById")
    class GetRoomByIdTests {

        @Test
        @DisplayName("retourne Room si trouvée")
        void shouldReturnRoom() {
            Room room = new Room();
            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

            assertEquals(room, roomService.getRoomById(1L));
        }

        @Test
        @DisplayName("lance exception si room non trouvée")
        void shouldThrowExceptionIfNotFound() {
            when(roomRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(CampusRoomBusinessException.class, () -> roomService.getRoomById(1L));
        }
    }

    // =========================
    // createRoom
    // =========================

    @Nested
    @DisplayName("createRoom")
    class CreateRoomTests {

        @Test
        @DisplayName("crée une room valide")
        void shouldCreateRoom() {
            RoomFormDto dto = RoomFormDto.builder()
                    .name("Room1")
                    .capacity(10)
                    .location("First Floor")
                    .campusId(campus.getId())
                    .build();

            when(campusService.getCampusById(campus.getId())).thenReturn(campus);
            when(roomRepository.existsByName(dto.name())).thenReturn(false);

            roomService.createRoom(dto);

            assertEquals(1, campus.getRooms().size());
            verify(roomRepository).save(any(Room.class));
        }

        @Test
        @DisplayName("lance exception si room existe déjà")
        void shouldThrowExceptionIfRoomExists() {
            RoomFormDto dto = RoomFormDto.builder()
                    .name("Room1")
                    .capacity(10)
                    .location("First Floor")
                    .campusId(campus.getId())
                    .build();

            when(campusService.getCampusById(campus.getId())).thenReturn(campus);
            when(roomRepository.existsByName(dto.name())).thenReturn(true);

            assertThrows(CampusRoomBusinessException.class, () -> roomService.createRoom(dto));
            verify(roomRepository, never()).save(any());
        }
    }

    // =========================
    // updateRoomCapacity
    // =========================

    @Nested
    @DisplayName("updateRoomCapacity")
    class UpdateRoomCapacityTests {

        @Test
        @DisplayName("update capacity si room existe")
        void shouldUpdateCapacity() {
            when(roomRepository.existsById(1L)).thenReturn(true);

            roomService.updateRoomCapacity(1L, 50);

            verify(roomRepository).updateRoomCapacity(1L, 50);
        }

        @Test
        @DisplayName("lance exception si room non existante")
        void shouldThrowExceptionIfNotFound() {
            when(roomRepository.existsById(1L)).thenReturn(false);

            assertThrows(CampusRoomBusinessException.class, () -> roomService.updateRoomCapacity(1L, 50));
        }
    }

    // =========================
    // updateRoomName
    // =========================

    @Nested
    @DisplayName("updateRoomName")
    class UpdateRoomNameTests {

        @Test
        @DisplayName("update name si room valide et pas de conflit")
        void shouldUpdateName() {
            Room room = new Room();
            room.setId(1L);
            room.setCampus(campus);

            when(campusService.getCampusById(campus.getId())).thenReturn(campus);
            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
            when(roomRepository.existsByName("Room2")).thenReturn(false);

            roomService.updateRoomName(campus.getId(), 1L, "Room2");

            verify(roomRepository).updateRoomName(1L, "Room2");
        }

        @Test
        @DisplayName("lance exception si room non trouvée")
        void shouldThrowExceptionIfRoomNotFound() {
            when(campusService.getCampusById(campus.getId())).thenReturn(campus);
            when(roomRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(CampusRoomBusinessException.class, () -> roomService.updateRoomName(campus.getId(), 1L, "Room2"));
        }

        @Test
        @DisplayName("lance exception si nom existe déjà sur campus")
        void shouldThrowExceptionIfNameExists() {
            Room room = new Room();
            room.setId(1L);
            room.setCampus(campus);

            when(campusService.getCampusById(campus.getId())).thenReturn(campus);
            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
            when(roomRepository.existsByName("Room1")).thenReturn(true);

            assertThrows(CampusRoomBusinessException.class, () -> roomService.updateRoomName(campus.getId(), 1L, "Room1"));
        }
    }

    // =========================
    // deleteById
    // =========================

    @Nested
    @DisplayName("deleteById")
    class DeleteByIdTests {

        @Test
        @DisplayName("supprime la room si elle existe et pas de reservations")
        void shouldDeleteRoom() {
            Room room = new Room();
            room.setId(1L);
            room.setName("Room1");

            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
            when(reservationChecker.existsByRoomIdAndStartTimeAfter(eq(1L), any(LocalDateTime.class)))
                    .thenReturn(false);

            roomService.deleteById(1L);

            verify(roomRepository).delete(room);
        }

        @Test
        @DisplayName("lance exception si room non trouvée")
        void shouldThrowExceptionIfNotFound() {
            when(roomRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(CampusRoomBusinessException.class, () -> roomService.deleteById(1L));
        }

        @Test
        @DisplayName("lance exception si reservations futures")
        void shouldThrowExceptionIfFutureReservationsExist() {
            Room room = new Room();
            room.setId(1L);
            room.setName("Room1");

            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
            when(reservationChecker.existsByRoomIdAndStartTimeAfter(eq(1L), any(LocalDateTime.class)))
                    .thenReturn(true);

            assertThrows(CampusRoomBusinessException.class, () -> roomService.deleteById(1L));
            verify(roomRepository, never()).delete((Room) any());
        }
    }
}