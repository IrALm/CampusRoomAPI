package com.campusRoom.api.controller;

import com.campusRoom.api.dto.formDto.UserFormDto;
import com.campusRoom.api.dto.outPutDto.UserDto;
import com.campusRoom.api.entity.Role;
import com.campusRoom.api.exception.CampusRoomBusinessException;
import com.campusRoom.api.service.UserService;
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

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private EntityManagerFactory entityManagerFactory;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ==================== Builders ====================

    private UserFormDto buildUserFormDto() {
        return new UserFormDto("Alice", "Dupont", "alice@esgi.fr", "STUDENT");
    }

    private UserDto buildUserDto() {
        return new UserDto(1L, "Alice", "Dupont", "alice@esgi.fr", Role.STUDENT, new ArrayList<>());
    }

    private UserDto buildTeacherDto() {
        return new UserDto(2L, "Bernard", "Martin", "bernard@esgi.fr", Role.TEACHER, new ArrayList<>());
    }

    // ==================== POST /user ====================

    @Test
    @DisplayName("POST /user - doit retourner 204 quand l'utilisateur est créé avec succès")
    void should_return204_when_userIsCreatedSuccessfully() throws Exception {
        doNothing().when(userService).createUser(any());

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUserFormDto())))
                .andExpect(status().isNoContent());

        verify(userService).createUser(any(UserFormDto.class));
    }

    @Test
    @DisplayName("POST /user - doit retourner 204 quand un TEACHER est créé avec succès")
    void should_return204_when_teacherIsCreatedSuccessfully() throws Exception {
        doNothing().when(userService).createUser(any());
        UserFormDto teacherDto = new UserFormDto("Bernard", "Martin", "bernard@esgi.fr", "TEACHER");

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherDto)))
                .andExpect(status().isNoContent());

        verify(userService).createUser(any(UserFormDto.class));
    }

    @Test
    @DisplayName("POST /user - doit retourner 400 quand le body JSON est vide (aucun champ)")
    void should_return400_when_bodyIsEmpty() throws Exception {
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("POST /user - doit retourner 400 et l'erreur de validation quand firstName est blank")
    void should_return400WithValidationError_when_firstNameIsBlank() throws Exception {
        UserFormDto invalidDto = new UserFormDto("", "Dupont", "alice@esgi.fr", "STUDENT");

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.firstName").exists());
    }

    @Test
    @DisplayName("POST /user - doit retourner 400 et l'erreur de validation quand lastName est blank")
    void should_return400WithValidationError_when_lastNameIsBlank() throws Exception {
        UserFormDto invalidDto = new UserFormDto("Alice", "", "alice@esgi.fr", "STUDENT");

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.lastName").exists());
    }

    @Test
    @DisplayName("POST /user - doit retourner 400 et l'erreur de validation quand email est blank")
    void should_return400WithValidationError_when_emailIsBlank() throws Exception {
        UserFormDto invalidDto = new UserFormDto("Alice", "Dupont", "", "STUDENT");

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.email").exists());
    }

    @Test
    @DisplayName("POST /user - doit retourner 400 et l'erreur de validation quand role est null")
    void should_return400WithValidationError_when_roleIsNull() throws Exception {
        String jsonWithNullRole =
                "{\"firstName\":\"Alice\",\"lastName\":\"Dupont\",\"email\":\"alice@esgi.fr\",\"role\":null}";

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithNullRole))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.role").exists());
    }

    @Test
    @DisplayName("POST /user - doit retourner 409 quand l'email est déjà utilisé")
    void should_return409_when_emailAlreadyTaken() throws Exception {
        doThrow(new CampusRoomBusinessException("Email déjà utilisé", HttpStatus.CONFLICT))
                .when(userService).createUser(any());

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUserFormDto())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Email déjà utilisé"));

        verify(userService).createUser(any(UserFormDto.class));
    }

    @Test
    @DisplayName("POST /user - doit retourner 400 quand le rôle est invalide (ex : ADMIN)")
    void should_return400_when_roleIsInvalid() throws Exception {
        doThrow(new CampusRoomBusinessException("Rôle invalide", HttpStatus.BAD_REQUEST))
                .when(userService).createUser(any());
        UserFormDto invalidRoleDto = new UserFormDto("Alice", "Dupont", "alice@esgi.fr", "ADMIN");

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRoleDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Rôle invalide"));
    }

    @Test
    @DisplayName("POST /user - doit retourner 400 quand le body est absent")
    void should_return400_when_bodyIsAbsent() throws Exception {
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // ==================== GET /user ====================

    @Test
    @DisplayName("GET /user - doit retourner 200 avec le UserDto quand l'email existe (STUDENT)")
    void should_return200WithUserDto_when_emailExistsForStudent() throws Exception {
        UserDto userDto = buildUserDto();
        when(userService.getUserByEmail("alice@esgi.fr")).thenReturn(userDto);

        mockMvc.perform(get("/user")
                        .param("email", "alice@esgi.fr"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Dupont"))
                .andExpect(jsonPath("$.email").value("alice@esgi.fr"))
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.reservationDtoList").isArray());

        verify(userService).getUserByEmail("alice@esgi.fr");
    }

    @Test
    @DisplayName("GET /user - doit retourner 200 avec le UserDto quand l'email existe (TEACHER)")
    void should_return200WithUserDto_when_emailExistsForTeacher() throws Exception {
        UserDto teacherDto = buildTeacherDto();
        when(userService.getUserByEmail("bernard@esgi.fr")).thenReturn(teacherDto);

        mockMvc.perform(get("/user")
                        .param("email", "bernard@esgi.fr"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.firstName").value("Bernard"))
                .andExpect(jsonPath("$.role").value("TEACHER"));

        verify(userService).getUserByEmail("bernard@esgi.fr");
    }

    @Test
    @DisplayName("GET /user - doit retourner 404 quand l'email est introuvable")
    void should_return404_when_emailNotFound() throws Exception {
        when(userService.getUserByEmail(anyString()))
                .thenThrow(new CampusRoomBusinessException("Utilisateur introuvable", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/user")
                        .param("email", "inconnu@esgi.fr"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Utilisateur introuvable"))
                .andExpect(jsonPath("$.path").value("/user"));
    }

    @Test
    @DisplayName("GET /user - doit retourner 400 quand le paramètre email est absent")
    void should_return400_when_emailParamIsMissing() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isBadRequest());
    }

    // ==================== PATCH /user/{userId}/firstName ====================

    @Test
    @DisplayName("PATCH /user/{userId}/firstName - doit retourner 204 quand le prénom est mis à jour")
    void should_return204_when_firstNameIsUpdatedSuccessfully() throws Exception {
        doNothing().when(userService).updateFirstName(anyLong(), anyString());

        mockMvc.perform(patch("/user/1/firstName")
                        .param("firstName", "Bob"))
                .andExpect(status().isNoContent());

        verify(userService).updateFirstName(1L, "Bob");
    }

    @Test
    @DisplayName("PATCH /user/{userId}/firstName - doit transmettre le bon userId au service")
    void should_passCorrectUserId_to_service_for_firstNameUpdate() throws Exception {
        doNothing().when(userService).updateFirstName(anyLong(), anyString());

        mockMvc.perform(patch("/user/42/firstName")
                        .param("firstName", "Charlie"))
                .andExpect(status().isNoContent());

        verify(userService).updateFirstName(42L, "Charlie");
    }

    @Test
    @DisplayName("PATCH /user/{userId}/firstName - doit retourner 400 quand le paramètre firstName est absent")
    void should_return400_when_firstNameParamIsMissing() throws Exception {
        mockMvc.perform(patch("/user/1/firstName"))
                .andExpect(status().isBadRequest());
    }

    // ==================== PATCH /user/{userId}/lastName ====================

    @Test
    @DisplayName("PATCH /user/{userId}/lastName - doit retourner 204 quand le nom est mis à jour")
    void should_return204_when_lastNameIsUpdatedSuccessfully() throws Exception {
        doNothing().when(userService).updateLastName(anyLong(), anyString());

        mockMvc.perform(patch("/user/1/lastName")
                        .param("lastName", "Martin"))
                .andExpect(status().isNoContent());

        verify(userService).updateLastName(1L, "Martin");
    }

    @Test
    @DisplayName("PATCH /user/{userId}/lastName - doit transmettre le bon userId au service")
    void should_passCorrectUserId_to_service_for_lastNameUpdate() throws Exception {
        doNothing().when(userService).updateLastName(anyLong(), anyString());

        mockMvc.perform(patch("/user/7/lastName")
                        .param("lastName", "Durand"))
                .andExpect(status().isNoContent());

        verify(userService).updateLastName(7L, "Durand");
    }

    @Test
    @DisplayName("PATCH /user/{userId}/lastName - doit retourner 400 quand le paramètre lastName est absent")
    void should_return400_when_lastNameParamIsMissing() throws Exception {
        mockMvc.perform(patch("/user/1/lastName"))
                .andExpect(status().isBadRequest());
    }
}
