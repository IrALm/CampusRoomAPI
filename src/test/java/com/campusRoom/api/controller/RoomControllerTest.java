package com.campusRoom.api.controller;

import com.campusRoom.api.dto.formDto.RoomFormDto;
import com.campusRoom.api.dto.outPutDto.CampusDto;
import com.campusRoom.api.dto.outPutDto.RoomDto;
import com.campusRoom.api.exception.CampusRoomBusinessException;
import com.campusRoom.api.service.RoomService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomController.class)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @MockBean
    private EntityManagerFactory entityManagerFactory;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ==================== Builders ====================

    private RoomFormDto buildRoomFormDto() {
        return new RoomFormDto("Salle A101", 30, "Bâtiment A - 1er étage", 1L);
    }

    private RoomDto buildRoomDto() {
        CampusDto campusDto = new CampusDto(1L, "ESGI Paris", "Paris", new ArrayList<>());
        return new RoomDto(
                1L, "Salle A101", 30, "Bâtiment A - 1er étage",
                List.of("Projecteur", "Tableau blanc"),
                campusDto,
                new ArrayList<>()
        );
    }

    // ==================== POST /room ====================

    @Test
    @DisplayName("POST /room - doit retourner 204 quand la salle est créée avec succès")
    void should_return204_when_roomIsCreatedSuccessfully() throws Exception {
        doNothing().when(roomService).createRoom(any());

        mockMvc.perform(post("/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRoomFormDto())))
                .andExpect(status().isNoContent());

        verify(roomService).createRoom(any(RoomFormDto.class));
    }

    @Test
    @DisplayName("POST /room - doit retourner 400 quand le body JSON est vide (aucun champ)")
    void should_return400_when_bodyIsEmpty() throws Exception {
        mockMvc.perform(post("/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("POST /room - doit retourner 400 et l'erreur de validation quand name est blank")
    void should_return400WithValidationError_when_roomNameIsBlank() throws Exception {
        RoomFormDto invalidDto = new RoomFormDto("", 30, "Bâtiment A", 1L);

        mockMvc.perform(post("/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.name").exists());
    }

    @Test
    @DisplayName("POST /room - doit retourner 400 et l'erreur de validation quand location est blank")
    void should_return400WithValidationError_when_locationIsBlank() throws Exception {
        RoomFormDto invalidDto = new RoomFormDto("Salle A", 30, "", 1L);

        mockMvc.perform(post("/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.location").exists());
    }

    @Test
    @DisplayName("POST /room - doit retourner 400 quand capacity est null")
    void should_return400_when_capacityIsNull() throws Exception {
        String jsonWithNullCapacity =
                "{\"name\":\"Salle A\",\"capacity\":null,\"location\":\"Bâtiment A\",\"campusId\":1}";

        mockMvc.perform(post("/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithNullCapacity))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.capacity").exists());
    }

    @Test
    @DisplayName("POST /room - doit retourner 400 quand campusId est null")
    void should_return400_when_campusIdIsNull() throws Exception {
        String jsonWithNullCampusId =
                "{\"name\":\"Salle A\",\"capacity\":30,\"location\":\"Bâtiment A\",\"campusId\":null}";

        mockMvc.perform(post("/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithNullCampusId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.campusId").exists());
    }

    @Test
    @DisplayName("POST /room - doit retourner 409 quand la salle existe déjà")
    void should_return409_when_roomAlreadyExists() throws Exception {
        doThrow(new CampusRoomBusinessException("Salle déjà existante", HttpStatus.CONFLICT))
                .when(roomService).createRoom(any());

        mockMvc.perform(post("/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRoomFormDto())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Salle déjà existante"));

        verify(roomService).createRoom(any(RoomFormDto.class));
    }

    @Test
    @DisplayName("POST /room - doit retourner 404 quand le campus associé n'existe pas")
    void should_return404_when_associatedCampusDoesNotExist() throws Exception {
        doThrow(new CampusRoomBusinessException("Campus introuvable", HttpStatus.NOT_FOUND))
                .when(roomService).createRoom(any());

        mockMvc.perform(post("/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRoomFormDto())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Campus introuvable"));
    }

    @Test
    @DisplayName("POST /room - doit retourner 400 quand le body est absent")
    void should_return400_when_bodyIsAbsent() throws Exception {
        mockMvc.perform(post("/room")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // ==================== GET /room ====================

    @Test
    @DisplayName("GET /room - doit retourner 200 avec le RoomDto quand la salle existe")
    void should_return200WithRoomDto_when_roomNameExists() throws Exception {
        RoomDto roomDto = buildRoomDto();
        when(roomService.getByRoomName("Salle A101")).thenReturn(roomDto);

        mockMvc.perform(get("/room")
                        .param("name", "Salle A101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Salle A101"))
                .andExpect(jsonPath("$.capacity").value(30))
                .andExpect(jsonPath("$.location").value("Bâtiment A - 1er étage"))
                .andExpect(jsonPath("$.equipment[0]").value("Projecteur"))
                .andExpect(jsonPath("$.campusDto.id").value(1))
                .andExpect(jsonPath("$.campusDto.name").value("ESGI Paris"));

        verify(roomService).getByRoomName("Salle A101");
    }

    @Test
    @DisplayName("GET /room - doit retourner 404 quand la salle est introuvable par son nom")
    void should_return404_when_roomNameNotFound() throws Exception {
        when(roomService.getByRoomName(anyString()))
                .thenThrow(new CampusRoomBusinessException("Salle introuvable", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/room")
                        .param("name", "Inconnue"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Salle introuvable"))
                .andExpect(jsonPath("$.path").value("/room"));
    }

    @Test
    @DisplayName("GET /room - doit retourner 400 quand le paramètre name est absent")
    void should_return400_when_nameParamIsMissing() throws Exception {
        mockMvc.perform(get("/room"))
                .andExpect(status().isBadRequest());
    }

    // ==================== PATCH /room/{roomId}/capacity ====================

    @Test
    @DisplayName("PATCH /room/{roomId}/capacity - doit retourner 204 quand la capacité est mise à jour")
    void should_return204_when_capacityIsUpdatedSuccessfully() throws Exception {
        doNothing().when(roomService).updateRoomCapacity(anyLong(), anyInt());

        mockMvc.perform(patch("/room/1/capacity")
                        .param("capacity", "50"))
                .andExpect(status().isNoContent());

        verify(roomService).updateRoomCapacity(1L, 50);
    }

    @Test
    @DisplayName("PATCH /room/{roomId}/capacity - doit retourner 404 quand la salle est introuvable")
    void should_return404_when_roomNotFoundForCapacityUpdate() throws Exception {
        doThrow(new CampusRoomBusinessException("Salle introuvable", HttpStatus.NOT_FOUND))
                .when(roomService).updateRoomCapacity(anyLong(), anyInt());

        mockMvc.perform(patch("/room/99/capacity")
                        .param("capacity", "50"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Salle introuvable"));
    }

    @Test
    @DisplayName("PATCH /room/{roomId}/capacity - doit retourner 400 quand le paramètre capacity est absent")
    void should_return400_when_capacityParamIsMissing() throws Exception {
        mockMvc.perform(patch("/room/1/capacity"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /room/{roomId}/capacity - doit transmettre le bon roomId et la bonne capacité au service")
    void should_passCorrectParams_to_service_for_capacityUpdate() throws Exception {
        doNothing().when(roomService).updateRoomCapacity(anyLong(), anyInt());

        mockMvc.perform(patch("/room/7/capacity")
                        .param("capacity", "100"))
                .andExpect(status().isNoContent());

        verify(roomService).updateRoomCapacity(7L, 100);
    }

    // ==================== PATCH /room/{roomId}/name ====================

    @Test
    @DisplayName("PATCH /room/{roomId}/name - doit retourner 204 quand le nom est mis à jour avec succès")
    void should_return204_when_roomNameIsUpdatedSuccessfully() throws Exception {
        doNothing().when(roomService).updateRoomName(anyLong(), anyLong(), anyString());

        mockMvc.perform(patch("/room/1/name")
                        .param("campusId", "1")
                        .param("name", "Nouveau Nom"))
                .andExpect(status().isNoContent());

        // Le controller appelle roomService.updateRoomName(campusId, roomId, name)
        verify(roomService).updateRoomName(1L, 1L, "Nouveau Nom");
    }

    @Test
    @DisplayName("PATCH /room/{roomId}/name - doit retourner 404 quand le campus est introuvable")
    void should_return404_when_campusNotFoundForRoomNameUpdate() throws Exception {
        doThrow(new CampusRoomBusinessException("Campus introuvable", HttpStatus.NOT_FOUND))
                .when(roomService).updateRoomName(anyLong(), anyLong(), anyString());

        mockMvc.perform(patch("/room/1/name")
                        .param("campusId", "99")
                        .param("name", "Test"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Campus introuvable"));
    }

    @Test
    @DisplayName("PATCH /room/{roomId}/name - doit retourner 404 quand la salle est introuvable")
    void should_return404_when_roomNotFoundForNameUpdate() throws Exception {
        doThrow(new CampusRoomBusinessException("Salle introuvable", HttpStatus.NOT_FOUND))
                .when(roomService).updateRoomName(anyLong(), anyLong(), anyString());

        mockMvc.perform(patch("/room/99/name")
                        .param("campusId", "1")
                        .param("name", "Test"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("PATCH /room/{roomId}/name - doit retourner 409 quand le nouveau nom est déjà pris")
    void should_return409_when_newRoomNameAlreadyTaken() throws Exception {
        doThrow(new CampusRoomBusinessException("Nom déjà utilisé", HttpStatus.CONFLICT))
                .when(roomService).updateRoomName(anyLong(), anyLong(), anyString());

        mockMvc.perform(patch("/room/1/name")
                        .param("campusId", "1")
                        .param("name", "Salle Dupliquée"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Nom déjà utilisé"));
    }

    @Test
    @DisplayName("PATCH /room/{roomId}/name - doit retourner 400 quand le paramètre campusId est absent")
    void should_return400_when_campusIdParamIsMissing() throws Exception {
        mockMvc.perform(patch("/room/1/name")
                        .param("name", "Test"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /room/{roomId}/name - doit retourner 400 quand le paramètre name est absent")
    void should_return400_when_nameParamMissingForRoomNameUpdate() throws Exception {
        mockMvc.perform(patch("/room/1/name")
                        .param("campusId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /room/{roomId}/name - doit transmettre les bons paramètres au service")
    void should_passCorrectParams_to_service_for_roomNameUpdate() throws Exception {
        doNothing().when(roomService).updateRoomName(anyLong(), anyLong(), anyString());

        mockMvc.perform(patch("/room/5/name")
                        .param("campusId", "3")
                        .param("name", "Salle Finale"))
                .andExpect(status().isNoContent());

        // controller : roomService.updateRoomName(campusId=3, roomId=5, name="Salle Finale")
        verify(roomService).updateRoomName(3L, 5L, "Salle Finale");
    }
}
