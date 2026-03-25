package com.campusRoom.api.mapper;

import com.campusRoom.api.dto.outPutDto.UserDto;
import com.campusRoom.api.entity.Role;
import com.campusRoom.api.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    // ==================== Builders ====================

    private User buildUser(Long id, Role role) {
        return User.builder()
                .id(id).firstName("Alice").lastName("Dupont")
                .email("alice@esgi.fr").role(role)
                .reservations(new ArrayList<>())
                .build();
    }

    // ==================== toDTO ====================

    @Test
    @DisplayName("toDTO - doit mapper id, firstName, lastName et email correctement")
    void should_mapScalarFields_when_toDTO() {
        User user = buildUser(1L, Role.STUDENT);

        UserDto result = mapper.toDTO(user);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.firstName()).isEqualTo("Alice");
        assertThat(result.lastName()).isEqualTo("Dupont");
        assertThat(result.email()).isEqualTo("alice@esgi.fr");
    }

    @Test
    @DisplayName("toDTO - doit mapper le rôle STUDENT correctement")
    void should_mapStudentRole_when_toDTO() {
        User user = buildUser(1L, Role.STUDENT);

        UserDto result = mapper.toDTO(user);

        assertThat(result.role()).isEqualTo(Role.STUDENT);
    }

    @Test
    @DisplayName("toDTO - doit mapper le rôle TEACHER correctement")
    void should_mapTeacherRole_when_toDTO() {
        User user = buildUser(2L, Role.TEACHER);

        UserDto result = mapper.toDTO(user);

        assertThat(result.role()).isEqualTo(Role.TEACHER);
    }

    @Test
    @DisplayName("toDTO - doit mapper reservations vers reservationDtoList (liste vide)")
    void should_returnEmptyReservationDtoList_when_userHasNoReservations() {
        User user = buildUser(1L, Role.STUDENT);

        UserDto result = mapper.toDTO(user);

        assertThat(result.reservationDtoList()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("toDTO - doit retourner null quand l'utilisateur est null")
    void should_returnNull_when_userIsNull() {
        UserDto result = mapper.toDTO(null);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("toDTO - doit mapper un utilisateur TEACHER avec les bons champs")
    void should_mapAllFields_when_teacherToDTO() {
        User teacher = User.builder()
                .id(99L).firstName("Bernard").lastName("Martin")
                .email("bernard@esgi.fr").role(Role.TEACHER)
                .reservations(new ArrayList<>())
                .build();

        UserDto result = mapper.toDTO(teacher);

        assertThat(result.id()).isEqualTo(99L);
        assertThat(result.firstName()).isEqualTo("Bernard");
        assertThat(result.lastName()).isEqualTo("Martin");
        assertThat(result.email()).isEqualTo("bernard@esgi.fr");
        assertThat(result.role()).isEqualTo(Role.TEACHER);
    }
}
