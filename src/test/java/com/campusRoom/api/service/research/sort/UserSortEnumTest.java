package com.campusRoom.api.service.research.sort;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitaires pour UserSortEnum")
class UserSortEnumTest {

    @Test
    @DisplayName("doit retourner le fieldName correct pour chaque enum")
    void shouldReturnCorrectFieldName() {
        assertEquals("id", UserSortEnum.ID.getFieldName());
        assertEquals("firstName", UserSortEnum.FIRST_NAME.getFieldName());
        assertEquals("lastName", UserSortEnum.LAST_NAME.getFieldName());
        assertEquals("email", UserSortEnum.EMAIL.getFieldName());
        assertEquals("role", UserSortEnum.ROLE.getFieldName());
    }

    @Test
    @DisplayName("isValidField retourne true pour les champs valides")
    void isValidFieldShouldReturnTrueForValid() {
        assertTrue(UserSortEnum.isValidField("id"));
        assertTrue(UserSortEnum.isValidField("FIRSTNAME")); // insensible à la casse
        assertTrue(UserSortEnum.isValidField("lastName"));
        assertTrue(UserSortEnum.isValidField("EMAIL"));
        assertTrue(UserSortEnum.isValidField("role"));
    }

    @Test
    @DisplayName("isValidField retourne false pour les champs invalides ou null")
    void isValidFieldShouldReturnFalseForInvalid() {
        assertFalse(UserSortEnum.isValidField(null));
        assertFalse(UserSortEnum.isValidField(""));
        assertFalse(UserSortEnum.isValidField(" "));
        assertFalse(UserSortEnum.isValidField("unknown"));
    }

    @Test
    @DisplayName("resolveField retourne le champ correct pour les valeurs valides")
    void resolveFieldShouldReturnCorrectFieldForValid() {
        assertEquals("id", UserSortEnum.resolveField("id"));
        assertEquals("firstName", UserSortEnum.resolveField("FIRSTNAME"));
        assertEquals("lastName", UserSortEnum.resolveField("LASTNAME"));
        assertEquals("email", UserSortEnum.resolveField("EMAIL"));
        assertEquals("role", UserSortEnum.resolveField("ROLE"));
    }

    @Test
    @DisplayName("resolveField retourne ID par défaut pour les valeurs invalides ou null")
    void resolveFieldShouldReturnDefaultForInvalid() {
        assertEquals("id", UserSortEnum.resolveField(null));
        assertEquals("id", UserSortEnum.resolveField(""));
        assertEquals("id", UserSortEnum.resolveField(" "));
        assertEquals("id", UserSortEnum.resolveField("unknown"));
    }
}