package com.campusRoom.api.service.impl;

import com.campusRoom.api.dto.formDto.UserFormDto;
import com.campusRoom.api.dto.outPutDto.UserDto;
import com.campusRoom.api.entity.Role;
import com.campusRoom.api.entity.User;
import com.campusRoom.api.exception.CampusRoomBusinessException;
import com.campusRoom.api.mapper.UserMapper;
import com.campusRoom.api.repository.UserRepository;
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
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    // ==================== verifyIfUserExist ====================

    @Test
    @DisplayName("verifyIfUserExist - doit retourner true quand l'email est déjà utilisé")
    void should_returnTrue_when_emailAlreadyExists() {
        when(userRepository.existsByEmail("alice@esgi.fr")).thenReturn(true);

        boolean result = userService.verifyIfUserExist("alice@esgi.fr");

        assertThat(result).isTrue();
        verify(userRepository).existsByEmail("alice@esgi.fr");
    }

    @Test
    @DisplayName("verifyIfUserExist - doit retourner false quand l'email n'est pas encore utilisé")
    void should_returnFalse_when_emailNotRegistered() {
        when(userRepository.existsByEmail("nouveau@esgi.fr")).thenReturn(false);

        boolean result = userService.verifyIfUserExist("nouveau@esgi.fr");

        assertThat(result).isFalse();
        verify(userRepository).existsByEmail("nouveau@esgi.fr");
    }

    // ==================== getUserByEmail ====================

    @Test
    @DisplayName("getUserByEmail - doit retourner le UserDto quand l'email existe")
    void should_returnUserDto_when_emailExists() {
        User user = User.builder().id(1L).firstName("Alice").lastName("Dupont")
                .email("alice@esgi.fr").role(Role.STUDENT).reservations(new ArrayList<>()).build();
        UserDto expectedDto = new UserDto(1L, "Alice", "Dupont", "alice@esgi.fr",
                Role.STUDENT, new ArrayList<>());
        when(userRepository.findByEmail("alice@esgi.fr")).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(expectedDto);

        UserDto result = userService.getUserByEmail("alice@esgi.fr");

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo("alice@esgi.fr");
        assertThat(result.role()).isEqualTo(Role.STUDENT);
        verify(userRepository).findByEmail("alice@esgi.fr");
        verify(userMapper).toDTO(user);
    }

    @Test
    @DisplayName("getUserByEmail - doit lever CampusRoomBusinessException quand l'email est introuvable")
    void should_throwNotFoundException_when_emailNotFound() {
        when(userRepository.findByEmail("inconnu@esgi.fr")).thenReturn(Optional.empty());

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> userService.getUserByEmail("inconnu@esgi.fr"));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ex.getMessage()).contains("inconnu@esgi.fr");
        verify(userRepository).findByEmail("inconnu@esgi.fr");
        verifyNoInteractions(userMapper);
    }

    // ==================== getUserById ====================

    @Test
    @DisplayName("getUserById - doit retourner l'utilisateur quand l'id existe")
    void should_returnUser_when_userIdExists() {
        User user = User.builder().id(1L).firstName("Bob").lastName("Martin")
                .email("bob@esgi.fr").role(Role.TEACHER).reservations(new ArrayList<>()).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getRole()).isEqualTo(Role.TEACHER);
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("getUserById - doit lever CampusRoomBusinessException quand l'id est introuvable")
    void should_throwNotFoundException_when_userIdNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> userService.getUserById(99L));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(userRepository).findById(99L);
    }

    // ==================== verifyIfRoleIsValid ====================

    @Test
    @DisplayName("verifyIfRoleIsValid - doit retourner true pour le rôle STUDENT en majuscules")
    void should_returnTrue_when_roleIsStudentUpperCase() {
        assertThat(userService.verifyIfRoleIsValid("STUDENT")).isTrue();
    }

    @Test
    @DisplayName("verifyIfRoleIsValid - doit retourner true pour le rôle TEACHER en majuscules")
    void should_returnTrue_when_roleIsTeacherUpperCase() {
        assertThat(userService.verifyIfRoleIsValid("TEACHER")).isTrue();
    }

    @Test
    @DisplayName("verifyIfRoleIsValid - doit retourner true pour un rôle valide en minuscules (insensible à la casse)")
    void should_returnTrue_when_roleIsLowerCase() {
        assertThat(userService.verifyIfRoleIsValid("student")).isTrue();
    }

    @Test
    @DisplayName("verifyIfRoleIsValid - doit retourner false pour un rôle invalide")
    void should_returnFalse_when_roleIsInvalid() {
        assertThat(userService.verifyIfRoleIsValid("ADMIN")).isFalse();
    }

    @Test
    @DisplayName("verifyIfRoleIsValid - doit retourner false pour un rôle null")
    void should_returnFalse_when_roleIsNull() {
        assertThat(userService.verifyIfRoleIsValid(null)).isFalse();
    }

    @Test
    @DisplayName("verifyIfRoleIsValid - doit retourner false pour une chaîne vide")
    void should_returnFalse_when_roleIsEmpty() {
        assertThat(userService.verifyIfRoleIsValid("")).isFalse();
    }

    @Test
    @DisplayName("verifyIfRoleIsValid - doit retourner false pour une chaîne avec espaces")
    void should_returnFalse_when_roleIsBlankWithSpaces() {
        assertThat(userService.verifyIfRoleIsValid("   ")).isFalse();
    }

    // ==================== createUser ====================

    @Test
    @DisplayName("createUser - doit créer l'utilisateur quand l'email est disponible et le rôle est valide")
    void should_createUser_when_emailAvailableAndRoleValid() {
        UserFormDto dto = new UserFormDto("Alice", "Dupont", "alice@esgi.fr", "STUDENT");
        when(userRepository.existsByEmail("alice@esgi.fr")).thenReturn(false);

        userService.createUser(dto);

        verify(userRepository).existsByEmail("alice@esgi.fr");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("createUser - doit créer l'utilisateur avec le rôle TEACHER")
    void should_createUser_when_roleIsTeacher() {
        UserFormDto dto = new UserFormDto("Prof", "Martin", "prof@esgi.fr", "TEACHER");
        when(userRepository.existsByEmail("prof@esgi.fr")).thenReturn(false);

        userService.createUser(dto);

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("createUser - doit lever CampusRoomBusinessException quand l'email est déjà pris")
    void should_throwConflictException_when_emailAlreadyTaken() {
        UserFormDto dto = new UserFormDto("Alice", "Dupont", "alice@esgi.fr", "STUDENT");
        when(userRepository.existsByEmail("alice@esgi.fr")).thenReturn(true);

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> userService.createUser(dto));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getMessage()).contains("alice@esgi.fr");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("createUser - doit lever CampusRoomBusinessException quand le rôle est invalide")
    void should_throwBadRequestException_when_roleIsInvalid() {
        UserFormDto dto = new UserFormDto("Alice", "Dupont", "alice@esgi.fr", "ADMIN");
        when(userRepository.existsByEmail("alice@esgi.fr")).thenReturn(false);

        CampusRoomBusinessException ex = assertThrows(CampusRoomBusinessException.class,
                () -> userService.createUser(dto));

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ex.getMessage()).contains("Alice");
        verify(userRepository, never()).save(any());
    }

    // ==================== updateFirstName ====================

    @Test
    @DisplayName("updateFirstName - doit déléguer la mise à jour du prénom au repository")
    void should_delegateFirstNameUpdate_toRepository() {
        userService.updateFirstName(1L, "Bob");

        verify(userRepository).updateFirstName(1L, "Bob");
    }

    @Test
    @DisplayName("updateFirstName - doit déléguer même si le prénom est vide (pas de validation dans le service)")
    void should_delegateFirstNameUpdate_evenWithEmptyName() {
        userService.updateFirstName(1L, "");

        verify(userRepository).updateFirstName(1L, "");
    }

    // ==================== updateLastName ====================

    @Test
    @DisplayName("updateLastName - doit déléguer la mise à jour du nom au repository")
    void should_delegateLastNameUpdate_toRepository() {
        userService.updateLastName(1L, "Martin");

        verify(userRepository).updateLastName(1L, "Martin");
    }

    @Test
    @DisplayName("updateLastName - doit déléguer même si le nom est vide (pas de validation dans le service)")
    void should_delegateLastNameUpdate_evenWithEmptyName() {
        userService.updateLastName(1L, "");

        verify(userRepository).updateLastName(1L, "");
    }
}
