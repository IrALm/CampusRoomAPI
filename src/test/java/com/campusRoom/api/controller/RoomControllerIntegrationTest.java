package com.campusRoom.api.controller;

import com.campusRoom.api.dto.formDto.RoomFormDto;
import com.campusRoom.api.dto.outPutDto.RoomDto;
import com.campusRoom.api.dto.researchDto.RoomPageDto;
import com.campusRoom.api.dto.researchDto.RoomSearchDto;
import com.campusRoom.api.service.RoomService;
import com.campusRoom.api.service.research.RoomSearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomController.class)
class RoomControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RoomService roomService;

    @MockBean
    private RoomSearchService roomSearchService;

    private RoomFormDto roomForm;
    private RoomDto roomDto;
    private RoomSearchDto searchDto;
    private RoomPageDto pageDto;

    @BeforeEach
    void setUp() {
        roomForm = RoomFormDto.builder()
                .name("Salle 1")
                .location("Bâtiment A")
                .capacity(20)
                .campusId(1L)
                .build();

        roomDto = RoomDto.builder()
                .id(1L)
                .name("Salle 1")
                .location("Bâtiment A")
                .capacity(20)
                .build();

        searchDto = RoomSearchDto.builder()
                .campusName("Main Campus")
                .page(0)
                .size(10)
                .sortBy("name")
                .sortDirection(Sort.Direction.ASC)
                .build();

        pageDto = RoomPageDto.builder()
                .contenu(List.of(roomDto))
                .pageActuelle(0)
                .totalPages(1)
                .totalElements(1)
                .dernierePage(true)
                .premierePage(true)
                .build();
    }

    // ========================
    // CREATE
    // ========================

    @Test
    @DisplayName("POST /room crée une salle (204)")
    void shouldCreateRoom() throws Exception {
        mockMvc.perform(post("/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomForm)))
                .andExpect(status().isNoContent());

        verify(roomService).createRoom(any(RoomFormDto.class));
    }

    @Test
    @DisplayName("POST /room retourne 400 si données invalides")
    void shouldReturn400WhenInvalidRoom() throws Exception {
        roomForm = RoomFormDto.builder().build();

        mockMvc.perform(post("/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomForm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Erreur de validation des champs"));

        verify(roomService, never()).createRoom(any());
    }

    // ========================
    // GET
    // ========================

    @Test
    @DisplayName("GET /room récupère une salle par nom")
    void shouldGetRoomByName() throws Exception {
        when(roomService.getByRoomName("Salle 1")).thenReturn(roomDto);

        mockMvc.perform(get("/room")
                        .param("name", "Salle 1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Salle 1"))
                .andExpect(jsonPath("$.capacity").value(20));

        verify(roomService).getByRoomName("Salle 1");
    }

    // ========================
    // PATCH
    // ========================

    @Test
    @DisplayName("PATCH /room/{id}/capacity met à jour capacité")
    void shouldUpdateCapacity() throws Exception {
        mockMvc.perform(patch("/room/1/capacity")
                        .param("capacity", "50"))
                .andExpect(status().isNoContent());

        verify(roomService).updateRoomCapacity(1L, 50);
    }

    @Test
    @DisplayName("PATCH /room/{id}/name met à jour nom")
    void shouldUpdateName() throws Exception {
        mockMvc.perform(patch("/room/1/name")
                        .param("campusId", "1")
                        .param("name", "Salle X"))
                .andExpect(status().isNoContent());

        verify(roomService).updateRoomName(1L, 1L, "Salle X");
    }

    // ========================
    // SEARCH
    // ========================

    @Test
    @DisplayName("POST /room/search retourne résultats")
    void shouldSearchRooms() throws Exception {
        when(roomSearchService.search(any(RoomSearchDto.class))).thenReturn(pageDto);

        mockMvc.perform(post("/room/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenu[0].name").value("Salle 1"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(roomSearchService).search(any(RoomSearchDto.class));
    }

    @Test
    @DisplayName("POST /room/search retourne 400 si campusName absent")
    void shouldReturn400WhenCampusNameMissing() throws Exception {
        searchDto = RoomSearchDto.builder().build();

        mockMvc.perform(post("/room/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isBadRequest());

        verify(roomSearchService, never()).search(any());
    }

    // ========================
    // DELETE
    // ========================

    @Test
    @DisplayName("DELETE /room/{id} supprime une salle")
    void shouldDeleteRoom() throws Exception {
        mockMvc.perform(delete("/room/1"))
                .andExpect(status().isNoContent());

        verify(roomService).deleteById(1L);
    }
}
