package com.campusRoom.api.service.research.sort;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitaires pour RoomSortEnum")
class RoomSortEnumTest {

    @Test
    @DisplayName("doit retourner le fieldName correct pour chaque enum")
    void shouldReturnCorrectFieldName() {
        assertEquals("id", RoomSortEnum.ID.getFieldName());
        assertEquals("name", RoomSortEnum.NAME.getFieldName());
        assertEquals("capacity", RoomSortEnum.CAPACITY.getFieldName());
        assertEquals("location", RoomSortEnum.LOCATION.getFieldName());
        assertEquals("campus.name", RoomSortEnum.CAMPUS.getFieldName());
    }

    @Test
    @DisplayName("isValidField retourne true pour les champs valides")
    void isValidFieldShouldReturnTrueForValid() {
        assertTrue(RoomSortEnum.isValidField("id"));
        assertTrue(RoomSortEnum.isValidField("NAME")); // insensible à la casse
        assertTrue(RoomSortEnum.isValidField("capacity"));
        assertTrue(RoomSortEnum.isValidField("LOCATION"));
        assertTrue(RoomSortEnum.isValidField("campus.name"));
    }

    @Test
    @DisplayName("isValidField retourne false pour les champs invalides ou null")
    void isValidFieldShouldReturnFalseForInvalid() {
        assertFalse(RoomSortEnum.isValidField(null));
        assertFalse(RoomSortEnum.isValidField(""));
        assertFalse(RoomSortEnum.isValidField(" "));
        assertFalse(RoomSortEnum.isValidField("unknown"));
    }

    @Test
    @DisplayName("resolveField retourne le champ correct pour les valeurs valides")
    void resolveFieldShouldReturnCorrectFieldForValid() {
        assertEquals("id", RoomSortEnum.resolveField("id"));
        assertEquals("name", RoomSortEnum.resolveField("NAME"));
        assertEquals("capacity", RoomSortEnum.resolveField("CAPACITY"));
        assertEquals("location", RoomSortEnum.resolveField("LOCATION"));
        assertEquals("campus.name", RoomSortEnum.resolveField("CAMPUS.NAME"));
    }

    @Test
    @DisplayName("resolveField retourne ID par défaut pour les valeurs invalides ou null")
    void resolveFieldShouldReturnDefaultForInvalid() {
        assertEquals("id", RoomSortEnum.resolveField(null));
        assertEquals("id", RoomSortEnum.resolveField(""));
        assertEquals("id", RoomSortEnum.resolveField(" "));
        assertEquals("id", RoomSortEnum.resolveField("unknown"));
    }
}