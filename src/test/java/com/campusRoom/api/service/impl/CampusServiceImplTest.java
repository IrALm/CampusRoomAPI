package com.campusRoom.api.service.impl;

import com.campusRoom.api.dto.formDto.CampusFormDto;
import com.campusRoom.api.dto.outPutDto.CampusDto;
import com.campusRoom.api.entity.Campus;
import com.campusRoom.api.exception.CampusRoomBusinessException;
import com.campusRoom.api.mapper.CampusMapper;
import com.campusRoom.api.repository.CampusRepository;
import com.campusRoom.api.service.ReservationChecker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service CampusServiceImpl")
class CampusServiceImplTest {

    @Mock
    private CampusRepository campusRepository;

    @Mock
    private CampusMapper campusMapper;

    @Mock
    private ReservationChecker reservationChecker;

    @InjectMocks
    private CampusServiceImpl campusService;

    // =========================
    // getCampusById
    // =========================

    @Test
    @DisplayName("getCampusById - retourne le campus si trouvé")
    void shouldReturnCampus_whenCampusExists() {
        Long id = 1L;
        Campus campus = Campus.builder().id(id).name("Campus A").city("Paris").build();

        when(campusRepository.findById(id)).thenReturn(Optional.of(campus));

        Campus result = campusService.getCampusById(id);

        assertEquals(campus, result);
        verify(campusRepository).findById(id);
    }

    @Test
    @DisplayName("getCampusById - lance exception si campus inexistant")
    void shouldThrowException_whenCampusNotFound() {
        Long id = 1L;

        when(campusRepository.findById(id)).thenReturn(Optional.empty());

        CampusRoomBusinessException ex = assertThrows(
                CampusRoomBusinessException.class,
                () -> campusService.getCampusById(id)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        verify(campusRepository).findById(id);
    }

    // =========================
    // getCampusByName
    // =========================

    @Test
    @DisplayName("getCampusByName - retourne DTO si campus trouvé")
    void shouldReturnDto_whenCampusExists() {
        String name = "Campus A";
        Campus campus = Campus.builder().name(name).city("Paris").build();
        CampusDto dto = mock(CampusDto.class);

        when(campusRepository.findByName(name)).thenReturn(Optional.of(campus));
        when(campusMapper.toDTO(campus)).thenReturn(dto);

        CampusDto result = campusService.getCampusByName(name);

        assertEquals(dto, result);
        verify(campusRepository).findByName(name);
        verify(campusMapper).toDTO(campus);
    }

    @Test
    @DisplayName("getCampusByName - lance exception si non trouvé")
    void shouldThrowException_whenCampusNameNotFound() {
        String name = "Unknown";

        when(campusRepository.findByName(name)).thenReturn(Optional.empty());

        assertThrows(CampusRoomBusinessException.class,
                () -> campusService.getCampusByName(name));

        verify(campusRepository).findByName(name);
        verify(campusMapper, never()).toDTO(any());
    }

    // =========================
    // verifyIfCampusExist
    // =========================

    @Test
    @DisplayName("verifyIfCampusExist - retourne true si existe")
    void shouldReturnTrue_whenCampusExists() {
        when(campusRepository.existsByName("Campus A")).thenReturn(true);

        boolean result = campusService.verifyIfCampusExist("Campus A", "Paris");

        assertTrue(result);
    }

    @Test
    @DisplayName("verifyIfCampusExist - retourne false si inexistant")
    void shouldReturnFalse_whenCampusNotExists() {
        when(campusRepository.existsByName("Campus A")).thenReturn(false);

        boolean result = campusService.verifyIfCampusExist("Campus A", "Paris");

        assertFalse(result);
    }

    // =========================
    // createCampus
    // =========================

    @Test
    @DisplayName("createCampus - crée un campus si inexistant")
    void shouldCreateCampus_whenNotExists() {
        CampusFormDto form = new CampusFormDto("Campus A", "Paris");

        when(campusRepository.existsByName("Campus A")).thenReturn(false);

        campusService.createCampus(form);

        verify(campusRepository).save(any(Campus.class));
    }

    @Test
    @DisplayName("createCampus - lance exception si campus existe déjà")
    void shouldThrowException_whenCampusAlreadyExists() {
        CampusFormDto form = new CampusFormDto("Campus A", "Paris");

        when(campusRepository.existsByName("Campus A")).thenReturn(true);

        assertThrows(CampusRoomBusinessException.class,
                () -> campusService.createCampus(form));

        verify(campusRepository, never()).save(any());
    }

    // =========================
    // save
    // =========================

    @Test
    @DisplayName("save - sauvegarde un campus")
    void shouldSaveCampus() {
        Campus campus = Campus.builder().name("Campus A").city("Paris").build();

        campusService.save(campus);

        verify(campusRepository).save(campus);
    }

    // =========================
    // updateNameAndCity
    // =========================

    @Test
    @DisplayName("updateNameAndCity - met à jour si nom unique")
    void shouldUpdate_whenNameIsUnique() {
        when(campusRepository.existsByName("Campus A")).thenReturn(false);

        campusService.updateNameAndCity(1L, "Campus A", "Paris");

        verify(campusRepository).updateNameAndCity(1L, "Campus A", "Paris");
    }

    @Test
    @DisplayName("updateNameAndCity - lance exception si nom déjà utilisé")
    void shouldThrowException_whenNameAlreadyExists() {
        when(campusRepository.existsByName("Campus A")).thenReturn(true);

        assertThrows(CampusRoomBusinessException.class,
                () -> campusService.updateNameAndCity(1L, "Campus A", "Paris"));

        verify(campusRepository, never()).updateNameAndCity(any(), any(), any());
    }

    // =========================
    // deleteById
    // =========================

    @Test
    @DisplayName("deleteById - supprime si aucune réservation future")
    void shouldDeleteCampus_whenNoFutureReservations() {
        Long id = 1L;
        Campus campus = Campus.builder().id(id).name("Campus A").build();

        when(campusRepository.findById(id)).thenReturn(Optional.of(campus));
        when(reservationChecker.existsByRoomCampusIdAndStartTimeAfter(eq(id), any(LocalDateTime.class)))
                .thenReturn(false);

        campusService.deleteById(id);

        verify(campusRepository).delete(campus);
    }

    @Test
    @DisplayName("deleteById - lance exception si campus inexistant")
    void shouldThrowException_whenCampusNotFound_delete() {
        Long id = 1L;

        when(campusRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CampusRoomBusinessException.class,
                () -> campusService.deleteById(id));

        verify(campusRepository, never()).delete((Campus) any());
    }

    @Test
    @DisplayName("deleteById - lance exception si réservations futures")
    void shouldThrowException_whenFutureReservationsExist() {
        Long id = 1L;
        Campus campus = Campus.builder().id(id).name("Campus A").build();

        when(campusRepository.findById(id)).thenReturn(Optional.of(campus));
        when(reservationChecker.existsByRoomCampusIdAndStartTimeAfter(eq(id), any()))
                .thenReturn(true);

        assertThrows(CampusRoomBusinessException.class,
                () -> campusService.deleteById(id));

        verify(campusRepository, never()).delete((Campus) any());
    }
}