package com.campusRoom.api.controller;

import com.campusRoom.api.dto.formDto.CampusFormDto;
import com.campusRoom.api.dto.outPutDto.CampusDto;
import com.campusRoom.api.exception.CampusRoomBusinessException;
import com.campusRoom.api.service.CampusService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CampusController.class)
class CampusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CampusService campusService;

    @MockBean
    private EntityManagerFactory entityManagerFactory;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ==================== Builders ====================

    private CampusFormDto buildCampusFormDto() {
        return new CampusFormDto("ESGI Paris", "Paris");
    }

    private CampusDto buildCampusDto() {
        return new CampusDto(1L, "ESGI Paris", "Paris", new ArrayList<>());
    }

    // ==================== POST /campus ====================

    @Test
    @DisplayName("POST /campus - doit retourner 204 quand le campus est créé avec succès")
    void should_return204_when_campusIsCreatedSuccessfully() throws Exception {
        doNothing().when(campusService).createCampus(any());

        mockMvc.perform(post("/campus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildCampusFormDto())))
                .andExpect(status().isNoContent());

        verify(campusService).createCampus(any(CampusFormDto.class));
    }

    @Test
    @DisplayName("POST /campus - doit retourner 400 quand le body JSON est vide (aucun champ)")
    void should_return400_when_bodyIsEmpty() throws Exception {
        mockMvc.perform(post("/campus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("POST /campus - doit retourner 400 et les erreurs de validation quand name est blank")
    void should_return400WithValidationError_when_nameIsBlank() throws Exception {
        CampusFormDto invalidDto = new CampusFormDto("", "Paris");

        mockMvc.perform(post("/campus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.name").exists());
    }

    @Test
    @DisplayName("POST /campus - doit retourner 400 et les erreurs de validation quand city est blank")
    void should_return400WithValidationError_when_cityIsBlank() throws Exception {
        CampusFormDto invalidDto = new CampusFormDto("ESGI", "");

        mockMvc.perform(post("/campus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.city").exists());
    }

    @Test
    @DisplayName("POST /campus - doit retourner 409 quand le campus existe déjà")
    void should_return409_when_campusAlreadyExists() throws Exception {
        doThrow(new CampusRoomBusinessException("Campus déjà existant", HttpStatus.CONFLICT))
                .when(campusService).createCampus(any());

        mockMvc.perform(post("/campus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildCampusFormDto())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Campus déjà existant"));

        verify(campusService).createCampus(any(CampusFormDto.class));
    }

    @Test
    @DisplayName("POST /campus - doit retourner 415 quand le Content-Type n'est pas JSON")
    void should_return415_when_contentTypeIsNotJson() throws Exception {
        mockMvc.perform(post("/campus")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("not json"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("POST /campus - doit retourner 400 quand le body est absent")
    void should_return400_when_bodyIsAbsent() throws Exception {
        mockMvc.perform(post("/campus")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // ==================== GET /campus ====================

    @Test
    @DisplayName("GET /campus - doit retourner 200 avec le CampusDto quand le nom existe")
    void should_return200WithCampusDto_when_nameExists() throws Exception {
        CampusDto campusDto = buildCampusDto();
        when(campusService.getCampusByName("ESGI Paris")).thenReturn(campusDto);

        mockMvc.perform(get("/campus")
                        .param("name", "ESGI Paris"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("ESGI Paris"))
                .andExpect(jsonPath("$.city").value("Paris"))
                .andExpect(jsonPath("$.roomDtoList").isArray());

        verify(campusService).getCampusByName("ESGI Paris");
    }

    @Test
    @DisplayName("GET /campus - doit retourner 200 avec roomDtoList vide quand le campus n'a pas de salles")
    void should_return200WithEmptyRoomList_when_campusHasNoRooms() throws Exception {
        when(campusService.getCampusByName(anyString()))
                .thenReturn(new CampusDto(2L, "ESGI Lyon", "Lyon", new ArrayList<>()));

        mockMvc.perform(get("/campus")
                        .param("name", "ESGI Lyon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomDtoList").isEmpty());
    }

    @Test
    @DisplayName("GET /campus - doit retourner 404 quand le nom de campus est introuvable")
    void should_return404_when_campusNameNotFound() throws Exception {
        when(campusService.getCampusByName(anyString()))
                .thenThrow(new CampusRoomBusinessException("Campus introuvable", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/campus")
                        .param("name", "Inconnu"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Campus introuvable"))
                .andExpect(jsonPath("$.path").value("/campus"));
    }

    @Test
    @DisplayName("GET /campus - doit retourner 400 quand le paramètre name est absent")
    void should_return400_when_nameParamIsMissing() throws Exception {
        mockMvc.perform(get("/campus"))
                .andExpect(status().isBadRequest());
    }

    // ==================== PATCH /campus/{campusId}/update ====================

    @Test
    @DisplayName("PATCH /campus/{campusId}/update - doit retourner 204 quand la mise à jour réussit")
    void should_return204_when_updateNameAndCitySucceeds() throws Exception {
        doNothing().when(campusService).updateNameAndCity(anyLong(), anyString(), anyString());

        mockMvc.perform(patch("/campus/1/update")
                        .param("name", "Nouveau Nom")
                        .param("city", "Lyon"))
                .andExpect(status().isNoContent());

        verify(campusService).updateNameAndCity(1L, "Nouveau Nom", "Lyon");
    }

    @Test
    @DisplayName("PATCH /campus/{campusId}/update - doit retourner 404 quand le campus est introuvable")
    void should_return404_when_campusNotFoundForUpdate() throws Exception {
        doThrow(new CampusRoomBusinessException("Campus introuvable", HttpStatus.NOT_FOUND))
                .when(campusService).updateNameAndCity(anyLong(), anyString(), anyString());

        mockMvc.perform(patch("/campus/99/update")
                        .param("name", "Test")
                        .param("city", "Lyon"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Campus introuvable"));
    }

    @Test
    @DisplayName("PATCH /campus/{campusId}/update - doit retourner 409 quand le nouveau nom est déjà utilisé")
    void should_return409_when_newNameAlreadyTaken() throws Exception {
        doThrow(new CampusRoomBusinessException("Nom déjà pris", HttpStatus.CONFLICT))
                .when(campusService).updateNameAndCity(anyLong(), anyString(), anyString());

        mockMvc.perform(patch("/campus/1/update")
                        .param("name", "ESGI Lyon")
                        .param("city", "Lyon"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Nom déjà pris"));
    }

    @Test
    @DisplayName("PATCH /campus/{campusId}/update - doit retourner 400 quand le paramètre name est absent")
    void should_return400_when_nameParamMissingForUpdate() throws Exception {
        mockMvc.perform(patch("/campus/1/update")
                        .param("city", "Lyon"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /campus/{campusId}/update - doit retourner 400 quand le paramètre city est absent")
    void should_return400_when_cityParamMissingForUpdate() throws Exception {
        mockMvc.perform(patch("/campus/1/update")
                        .param("name", "Test"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /campus/{campusId}/update - doit transmettre les bons paramètres au service")
    void should_passCorrectParams_to_service_when_updating() throws Exception {
        doNothing().when(campusService).updateNameAndCity(anyLong(), anyString(), anyString());

        mockMvc.perform(patch("/campus/42/update")
                        .param("name", "Campus Final")
                        .param("city", "Bordeaux"))
                .andExpect(status().isNoContent());

        verify(campusService).updateNameAndCity(42L, "Campus Final", "Bordeaux");
    }
}
