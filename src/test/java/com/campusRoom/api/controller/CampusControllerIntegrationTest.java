package com.campusRoom.api.controller;

import com.campusRoom.api.dto.formDto.CampusFormDto;
import com.campusRoom.api.dto.outPutDto.CampusDto;
import com.campusRoom.api.dto.researchDto.CampusPageDto;
import com.campusRoom.api.dto.researchDto.CampusSearchDto;
import com.campusRoom.api.service.CampusService;
import com.campusRoom.api.service.research.CampusResearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CampusController.class)
@DisplayName("Tests d'intégration - CampusController")
class CampusControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CampusService campusService;

    @MockBean
    private CampusResearchService campusResearchService;

    @Autowired
    private ObjectMapper objectMapper;

    // ================================
    // CREATE
    // ================================
    @Test
    @DisplayName("POST /campus -> 204 OK")
    void shouldCreateCampus() throws Exception {

        CampusFormDto dto = CampusFormDto.builder()
                .name("Campus Nantes")
                .city("Nantes")
                .build();

        mockMvc.perform(post("/campus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        verify(campusService).createCampus(any(CampusFormDto.class));
    }

    @Test
    @DisplayName("POST /campus -> 400 si invalide")
    void shouldFailCreateCampusIfInvalid() throws Exception {

        CampusFormDto dto = CampusFormDto.builder()
                .name(null) // invalide
                .city(null)
                .build();

        mockMvc.perform(post("/campus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // ================================
    // GET BY NAME
    // ================================
    @Test
    @DisplayName("GET /campus?name=... -> 200")
    void shouldGetCampusByName() throws Exception {

        CampusDto dto = CampusDto.builder()
                .id(1L)
                .name("Campus Central")
                .city("Nantes")
                .roomDtoList(List.of())
                .build();

        when(campusService.getCampusByName("Campus Central")).thenReturn(dto);

        mockMvc.perform(get("/campus")
                        .param("name", "Campus Central"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Campus Central"))
                .andExpect(jsonPath("$.city").value("Nantes"));

        verify(campusService).getCampusByName("Campus Central");
    }

    // ================================
    // UPDATE
    // ================================
    @Test
    @DisplayName("PATCH /campus/{id}/update -> 204")
    void shouldUpdateCampus() throws Exception {

        mockMvc.perform(patch("/campus/1/update")
                        .param("name", "Nouveau Campus")
                        .param("city", "Paris"))
                .andExpect(status().isNoContent());

        verify(campusService).updateNameAndCity(1L, "Nouveau Campus", "Paris");
    }

    // ================================
    // SEARCH
    // ================================
    @Test
    @DisplayName("POST /campus/search -> 200 avec contenu")
    void shouldSearchCampus() throws Exception {

        CampusSearchDto searchDto = CampusSearchDto.builder()
                .page(0)
                .size(10)
                .build();

        CampusDto campus = CampusDto.builder()
                .id(1L)
                .name("Campus Nantes")
                .city("Nantes")
                .roomDtoList(List.of())
                .build();

        CampusPageDto pageDto = new CampusPageDto(
                List.of(campus),
                0,
                1,
                1,
                true,
                true
        );

        when(campusResearchService.search(any())).thenReturn(pageDto);

        mockMvc.perform(post("/campus/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenu.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(campusResearchService).search(any());
    }

    // ================================
    // DELETE
    // ================================
    @Test
    @DisplayName("DELETE /campus/{id} -> 204")
    void shouldDeleteCampus() throws Exception {

        mockMvc.perform(delete("/campus/1"))
                .andExpect(status().isNoContent());

        verify(campusService).deleteById(1L);
    }
}