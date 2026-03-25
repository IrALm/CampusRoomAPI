package com.campusRoom.api.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RoleTest {

    // ==================== isValid — valeurs valides ====================

    @Test
    @DisplayName("isValid - doit retourner true pour 'STUDENT' en majuscules")
    void should_returnTrue_when_studentUpperCase() {
        assertThat(Role.isValid("STUDENT")).isTrue();
    }

    @Test
    @DisplayName("isValid - doit retourner true pour 'TEACHER' en majuscules")
    void should_returnTrue_when_teacherUpperCase() {
        assertThat(Role.isValid("TEACHER")).isTrue();
    }

    @Test
    @DisplayName("isValid - doit retourner true pour 'student' en minuscules (insensible à la casse)")
    void should_returnTrue_when_studentLowerCase() {
        assertThat(Role.isValid("student")).isTrue();
    }

    @Test
    @DisplayName("isValid - doit retourner true pour 'teacher' en minuscules (insensible à la casse)")
    void should_returnTrue_when_teacherLowerCase() {
        assertThat(Role.isValid("teacher")).isTrue();
    }

    @Test
    @DisplayName("isValid - doit retourner true pour 'Student' en casse mixte")
    void should_returnTrue_when_studentMixedCase() {
        assertThat(Role.isValid("Student")).isTrue();
    }

    // ==================== isValid — valeurs invalides ====================

    @Test
    @DisplayName("isValid - doit retourner false pour null")
    void should_returnFalse_when_null() {
        assertThat(Role.isValid(null)).isFalse();
    }

    @Test
    @DisplayName("isValid - doit retourner false pour une chaîne vide")
    void should_returnFalse_when_emptyString() {
        assertThat(Role.isValid("")).isFalse();
    }

    @Test
    @DisplayName("isValid - doit retourner false pour une chaîne avec espaces uniquement")
    void should_returnFalse_when_blankString() {
        assertThat(Role.isValid("   ")).isFalse();
    }

    @Test
    @DisplayName("isValid - doit retourner false pour 'ADMIN' (rôle inexistant)")
    void should_returnFalse_when_admin() {
        assertThat(Role.isValid("ADMIN")).isFalse();
    }

    @Test
    @DisplayName("isValid - doit retourner false pour une valeur quelconque inconnue")
    void should_returnFalse_when_unknownRole() {
        assertThat(Role.isValid("SUPERUSER")).isFalse();
    }
}
