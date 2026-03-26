package com.campusRoom.api.service.research.sort;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitaires pour CampusSortEnum")
class CampusSortEnumTest {

    @Test
    @DisplayName("doit retourner le fieldName correct pour chaque enum")
    void shouldReturnCorrectFieldName() {
        assertEquals("id", CampusSortEnum.ID.getFieldName());
        assertEquals("name", CampusSortEnum.NAME.getFieldName());
        assertEquals("city", CampusSortEnum.CITY.getFieldName());
    }

    @Test
    @DisplayName("isValidField retourne true pour les champs valides")
    void isValidFieldShouldReturnTrueForValid() {
        assertTrue(CampusSortEnum.isValidField("id"));
        assertTrue(CampusSortEnum.isValidField("NAME")); // insensible à la casse
        assertTrue(CampusSortEnum.isValidField("City"));
    }

    @Test
    @DisplayName("isValidField retourne false pour les champs invalides ou null")
    void isValidFieldShouldReturnFalseForInvalid() {
        assertFalse(CampusSortEnum.isValidField(null));
        assertFalse(CampusSortEnum.isValidField(""));
        assertFalse(CampusSortEnum.isValidField(" "));
        assertFalse(CampusSortEnum.isValidField("unknown"));
    }

    @Test
    @DisplayName("resolveField retourne le champ correct pour les valeurs valides")
    void resolveFieldShouldReturnCorrectFieldForValid() {
        assertEquals("id", CampusSortEnum.resolveField("id"));
        assertEquals("name", CampusSortEnum.resolveField("NAME"));
        assertEquals("city", CampusSortEnum.resolveField("City"));
    }

    @Test
    @DisplayName("resolveField retourne ID par défaut pour les valeurs invalides ou null")
    void resolveFieldShouldReturnDefaultForInvalid() {
        assertEquals("id", CampusSortEnum.resolveField(null));
        assertEquals("id", CampusSortEnum.resolveField(""));
        assertEquals("id", CampusSortEnum.resolveField(" "));
        assertEquals("id", CampusSortEnum.resolveField("unknown"));
    }
}