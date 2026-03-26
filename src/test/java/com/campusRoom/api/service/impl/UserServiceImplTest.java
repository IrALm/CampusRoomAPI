package com.campusRoom.api.service.impl;

import com.campusRoom.api.dto.formDto.UserFormDto;
import com.campusRoom.api.dto.outPutDto.UserDto;
import com.campusRoom.api.entity.Role;
import com.campusRoom.api.entity.User;
import com.campusRoom.api.exception.CampusRoomBusinessException;
import com.campusRoom.api.mapper.UserMapper;
import com.campusRoom.api.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires pour UserServiceImpl")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Nested
    @DisplayName("Tests createUser()")
    class CreateUserTests {

        @Test
        @DisplayName("Créer un utilisateur avec succès")
        void shouldCreateUserSuccessfully() {
            UserFormDto dto = UserFormDto.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("john.doe@example.com")
                    .role("STUDENT")
                    .build();

            when(userRepository.existsByEmail(dto.email())).thenReturn(false);

            userService.createUser(dto);

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            User saved = captor.getValue();

            assertEquals("John", saved.getFirstName());
            assertEquals("Doe", saved.getLastName());
            assertEquals("john.doe@example.com", saved.getEmail());
            assertEquals(Role.STUDENT, saved.getRole());
        }

        @Test
        @DisplayName("Créer un utilisateur déjà existant lance une exception")
        void shouldThrowExceptionIfUserExists() {
            UserFormDto dto = UserFormDto.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("john.doe@example.com")
                    .role("ADMIN")
                    .build();

            when(userRepository.existsByEmail(dto.email())).thenReturn(true);

            CampusRoomBusinessException ex = assertThrows(
                    CampusRoomBusinessException.class,
                    () -> userService.createUser(dto)
            );

            assertEquals(HttpStatus.CONFLICT, ex.getStatus());
            assertTrue(ex.getMessage().contains("existe déjà"));
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Créer un utilisateur avec rôle invalide lance une exception")
        void shouldThrowExceptionIfRoleInvalid() {
            UserFormDto dto = UserFormDto.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("john.doe@example.com")
                    .role("INVALID_ROLE")
                    .build();

            when(userRepository.existsByEmail(dto.email())).thenReturn(false);

            CampusRoomBusinessException ex = assertThrows(
                    CampusRoomBusinessException.class,
                    () -> userService.createUser(dto)
            );

            assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
            assertTrue(ex.getMessage().contains("invalide"));
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Tests getUserByEmail()")
    class GetUserByEmailTests {

        @Test
        @DisplayName("Retourne l'utilisateur existant")
        void shouldReturnUserDto() {
            User user = User.builder().id(1L).firstName("John").lastName("Doe")
                    .email("john.doe@example.com").role(Role.STUDENT).build();
            UserDto dto = UserDto.builder().id(1L).firstName("John").lastName("Doe")
                    .email("john.doe@example.com").role(Role.STUDENT).build();

            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
            when(userMapper.toDTO(user)).thenReturn(dto);

            UserDto result = userService.getUserByEmail("john.doe@example.com");
            assertEquals(dto, result);
        }

        @Test
        @DisplayName("Utilisateur inexistant lance une exception")
        void shouldThrowExceptionIfUserNotFound() {
            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());

            CampusRoomBusinessException ex = assertThrows(
                    CampusRoomBusinessException.class,
                    () -> userService.getUserByEmail("john.doe@example.com")
            );

            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        }
    }

    @Nested
    @DisplayName("Tests getUserById()")
    class GetUserByIdTests {

        @Test
        @DisplayName("Retourne l'utilisateur existant")
        void shouldReturnUser() {
            User user = User.builder().id(1L).firstName("Jane").lastName("Doe")
                    .email("jane.doe@example.com").role(Role.STUDENT).build();
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            User result = userService.getUserById(1L);
            assertEquals(user, result);
        }

        @Test
        @DisplayName("Utilisateur inexistant lance une exception")
        void shouldThrowExceptionIfUserNotFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            CampusRoomBusinessException ex = assertThrows(
                    CampusRoomBusinessException.class,
                    () -> userService.getUserById(1L)
            );

            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        }
    }

    @Nested
    @DisplayName("Tests updateFirstName() et updateLastName()")
    class UpdateNameTests {

        @Test
        @DisplayName("Met à jour le prénom de l'utilisateur")
        void shouldUpdateFirstName() {
            userService.updateFirstName(1L, "NewName");
            verify(userRepository).updateFirstName(1L, "NewName");
        }

        @Test
        @DisplayName("Met à jour le nom de l'utilisateur")
        void shouldUpdateLastName() {
            userService.updateLastName(1L, "NewLastName");
            verify(userRepository).updateLastName(1L, "NewLastName");
        }
    }

    @Nested
    @DisplayName("Tests deleteById()")
    class DeleteByIdTests {

        @Test
        @DisplayName("Supprime l'utilisateur si il existe")
        void shouldDeleteUser() {
            when(userRepository.existsById(1L)).thenReturn(true);

            userService.deleteById(1L);

            verify(userRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Lance exception si l'utilisateur n'existe pas")
        void shouldThrowExceptionIfUserNotFound() {
            when(userRepository.existsById(2L)).thenReturn(false);

            CampusRoomBusinessException ex = assertThrows(
                    CampusRoomBusinessException.class,
                    () -> userService.deleteById(2L)
            );

            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
            verify(userRepository, never()).deleteById(anyLong());
        }
    }
}