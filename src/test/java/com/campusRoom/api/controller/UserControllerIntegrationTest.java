package com.campusRoom.api.controller;

import com.campusRoom.api.dto.formDto.UserFormDto;
import com.campusRoom.api.dto.outPutDto.UserDto;
import com.campusRoom.api.dto.researchDto.UserPageDto;
import com.campusRoom.api.dto.researchDto.UserSearchDto;
import com.campusRoom.api.service.UserService;
import com.campusRoom.api.service.research.UserResearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

@WebMvcTest(UserController.class)
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserResearchService userResearchService;

    private UserFormDto userForm;
    private UserDto userDto;
    private UserSearchDto searchDto;
    private UserPageDto pageDto;

    @BeforeEach
    void setUp() {
        userForm = UserFormDto.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role("STUDENT")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        searchDto = UserSearchDto.builder()
                .page(0)
                .size(10)
                .sortBy("email")
                .sortDirection(org.springframework.data.domain.Sort.Direction.ASC)
                .build();

        pageDto = UserPageDto.builder()
                .contenu(List.of(userDto))
                .pageActuelle(0)
                .totalPages(1)
                .totalElements(1)
                .dernierePage(true)
                .premierePage(true)
                .build();
    }

    @Test
    @DisplayName("POST /user crée un utilisateur et renvoie 204")
    void shouldCreateUser() throws Exception {
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userForm)))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).createUser(any(UserFormDto.class));
    }

    @Test
    @DisplayName("GET /user récupère un utilisateur par email")
    void shouldGetUserByEmail() throws Exception {
        when(userService.getUserByEmail("test@example.com")).thenReturn(userDto);

        mockMvc.perform(get("/user")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(userService, times(1)).getUserByEmail("test@example.com");
    }

    @Test
    @DisplayName("PATCH /user/{userId}/firstName met à jour le prénom")
    void shouldUpdateFirstName() throws Exception {
        mockMvc.perform(patch("/user/1/firstName")
                        .param("firstName", "Jane"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).updateFirstName(1L, "Jane");
    }

    @Test
    @DisplayName("PATCH /user/{userId}/lastName met à jour le nom")
    void shouldUpdateLastName() throws Exception {
        mockMvc.perform(patch("/user/1/lastName")
                        .param("lastName", "Smith"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).updateLastName(1L, "Smith");
    }

    @Test
    @DisplayName("POST /user/search renvoie une page d'utilisateurs")
    void shouldSearchUsers() throws Exception {
        when(userResearchService.search(any(UserSearchDto.class))).thenReturn(pageDto);

        mockMvc.perform(post("/user/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenu[0].id").value(1))
                .andExpect(jsonPath("$.contenu[0].email").value("test@example.com"))
                .andExpect(jsonPath("$.pageActuelle").value(0))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(userResearchService, times(1)).search(any(UserSearchDto.class));
    }

    @Test
    @DisplayName("DELETE /user/{userId} supprime un utilisateur")
    void shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/user/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteById(1L);
    }
}
