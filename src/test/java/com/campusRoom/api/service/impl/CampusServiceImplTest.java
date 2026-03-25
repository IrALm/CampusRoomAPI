package com.campusRoom.api.service.impl;

import com.campusRoom.api.dto.formDto.CampusFormDto;
import com.campusRoom.api.dto.outPutDto.CampusDto;
import com.campusRoom.api.entity.Campus;
import com.campusRoom.api.exception.CampusRoomBusinessException;
import com.campusRoom.api.mapper.CampusMapper;
import com.campusRoom.api.repository.CampusRepository;
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
class CampusServiceImplTest {

    @Mock
    private CampusRepository campusRepository;

    @Mock
    private CampusMapper campusMapper;

    @InjectMocks
    private CampusServiceImpl campusService;

    // ==================== getCampusById ====================

    @Test
    @DisplayName("getCampusById - doit retourner le campus quand l'id existe")
    void should_returnCampus_when_idExists() {
        Campus campus = Campus.builder()
                .id(1L).name("ESGI").city("Paris").rooms(new ArrayList<>()).build();
        when(campusRepository.findById(1L)).thenReturn(Optional.of(campus));

        Campus result = campusService.getCampusById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("ESGI");
        verify(campusRepository).findById(1L);
    }

    @Test
    @DisplayName("getCampusById - doit lever CampusRoomBusinessException quand l'id est introuvable")
    void should_throwNotFoundException_when_idNotFound() {
        when(campusRepository.findById(99L)).thenReturn(Optional.empty());

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> campusService.getCampusById(99L));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(campusRepository).findById(99L);
    }

    // ==================== getCampusByName ====================

    @Test
    @DisplayName("getCampusByName - doit retourner le CampusDto quand le nom existe")
    void should_returnCampusDto_when_nameExists() {
        Campus campus = Campus.builder()
                .id(1L).name("ESGI").city("Paris").rooms(new ArrayList<>()).build();
        CampusDto expectedDto = new CampusDto(1L, "ESGI", "Paris", new ArrayList<>());
        when(campusRepository.findByName("ESGI")).thenReturn(Optional.of(campus));
        when(campusMapper.toDTO(campus)).thenReturn(expectedDto);

        CampusDto result = campusService.getCampusByName("ESGI");

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("ESGI");
        verify(campusRepository).findByName("ESGI");
        verify(campusMapper).toDTO(campus);
    }

    @Test
    @DisplayName("getCampusByName - doit lever CampusRoomBusinessException quand le nom est introuvable")
    void should_throwNotFoundException_when_nameNotFound() {
        when(campusRepository.findByName("Inconnu")).thenReturn(Optional.empty());

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> campusService.getCampusByName("Inconnu"));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(campusRepository).findByName("Inconnu");
        verifyNoInteractions(campusMapper);
    }

    @Test
    @DisplayName("getCampusByName - doit lever CampusRoomBusinessException pour une chaîne vide")
    void should_throwNotFoundException_when_nameIsEmpty() {
        when(campusRepository.findByName("")).thenReturn(Optional.empty());

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> campusService.getCampusByName(""));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // ==================== verifyIfCampusExist ====================

    @Test
    @DisplayName("verifyIfCampusExist - doit retourner true quand le campus existe déjà")
    void should_returnTrue_when_campusAlreadyExists() {
        when(campusRepository.existsByName("ESGI")).thenReturn(true);

        boolean result = campusService.verifyIfCampusExist("ESGI", "Paris");

        assertThat(result).isTrue();
        verify(campusRepository).existsByName("ESGI");
    }

    @Test
    @DisplayName("verifyIfCampusExist - doit retourner false quand le campus n'existe pas encore")
    void should_returnFalse_when_campusDoesNotExist() {
        when(campusRepository.existsByName("Nouveau")).thenReturn(false);

        boolean result = campusService.verifyIfCampusExist("Nouveau", "Lyon");

        assertThat(result).isFalse();
        verify(campusRepository).existsByName("Nouveau");
    }

    @Test
    @DisplayName("verifyIfCampusExist - doit vérifier uniquement par le nom, indépendamment de la ville")
    void should_checkByNameOnly_ignoringCity() {
        when(campusRepository.existsByName("ESGI")).thenReturn(true);

        boolean result = campusService.verifyIfCampusExist("ESGI", "Lyon");

        assertThat(result).isTrue();
        verify(campusRepository).existsByName("ESGI");
    }

    // ==================== createCampus ====================

    @Test
    @DisplayName("createCampus - doit persister le campus quand il n'existe pas encore")
    void should_saveCampus_when_campusDoesNotExist() {
        CampusFormDto dto = new CampusFormDto("Nouveau Campus", "Lyon");
        when(campusRepository.existsByName("Nouveau Campus")).thenReturn(false);

        campusService.createCampus(dto);

        verify(campusRepository).existsByName("Nouveau Campus");
        verify(campusRepository).save(any(Campus.class));
    }

    @Test
    @DisplayName("createCampus - doit lever CampusRoomBusinessException quand le campus existe déjà")
    void should_throwConflictException_when_campusAlreadyExists() {
        CampusFormDto dto = new CampusFormDto("ESGI", "Paris");
        when(campusRepository.existsByName("ESGI")).thenReturn(true);

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> campusService.createCampus(dto));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getMessage()).contains("ESGI");
        verify(campusRepository, never()).save(any());
    }

    // ==================== save ====================

    @Test
    @DisplayName("save - doit déléguer la persistance au repository")
    void should_delegateSave_toRepository() {
        Campus campus = Campus.builder()
                .name("Test").city("Paris").rooms(new ArrayList<>()).build();

        campusService.save(campus);

        verify(campusRepository).save(campus);
    }

    // ==================== updateNameAndCity ====================

    @Test
    @DisplayName("updateNameAndCity - doit mettre à jour quand le nouveau nom n'est pas encore utilisé")
    void should_updateNameAndCity_when_nameIsAvailable() {
        when(campusRepository.existsByName("NouveauNom")).thenReturn(false);

        campusService.updateNameAndCity(1L, "NouveauNom", "Lyon");

        verify(campusRepository).existsByName("NouveauNom");
        verify(campusRepository).updateNameAndCity(1L, "NouveauNom", "Lyon");
    }

    @Test
    @DisplayName("updateNameAndCity - doit lever CampusRoomBusinessException quand le nouveau nom est déjà pris")
    void should_throwConflictException_when_newNameAlreadyTaken() {
        when(campusRepository.existsByName("ESGI")).thenReturn(true);

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> campusService.updateNameAndCity(1L, "ESGI", "Paris"));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getMessage()).contains("ESGI");
        verify(campusRepository, never()).updateNameAndCity(any(), any(), any());
    }
}
