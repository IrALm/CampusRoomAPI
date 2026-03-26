package com.campusRoom.api.service.research.sort;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitaires pour ReservationSortEnum")
class ReservationSortEnumTest {

    @Test
    @DisplayName("doit retourner le fieldName correct pour chaque enum")
    void shouldReturnCorrectFieldName() {
        assertEquals("id", ReservationSortEnum.ID.getFieldName());
        assertEquals("type", ReservationSortEnum.TYPE.getFieldName());
        assertEquals("startTime", ReservationSortEnum.START_TIME.getFieldName());
        assertEquals("endTime", ReservationSortEnum.END_TIME.getFieldName());
        assertEquals("room.name", ReservationSortEnum.ROOM.getFieldName());
        assertEquals("user.lastName", ReservationSortEnum.USER.getFieldName());
        assertEquals("description", ReservationSortEnum.DESCRIPTION.getFieldName());
        assertEquals("maxDurationHours", ReservationSortEnum.MAX_DURATION_HOURS.getFieldName());
    }

    @Test
    @DisplayName("isValidField retourne true pour les champs valides")
    void isValidFieldShouldReturnTrueForValid() {
        assertTrue(ReservationSortEnum.isValidField("id"));
        assertTrue(ReservationSortEnum.isValidField("TYPE")); // insensible à la casse
        assertTrue(ReservationSortEnum.isValidField("startTime"));
        assertTrue(ReservationSortEnum.isValidField("endTime"));
        assertTrue(ReservationSortEnum.isValidField("room.name"));
        assertTrue(ReservationSortEnum.isValidField("USER.LASTNAME"));
        assertTrue(ReservationSortEnum.isValidField("description"));
        assertTrue(ReservationSortEnum.isValidField("maxDurationHours"));
    }

    @Test
    @DisplayName("isValidField retourne false pour les champs invalides ou null")
    void isValidFieldShouldReturnFalseForInvalid() {
        assertFalse(ReservationSortEnum.isValidField(null));
        assertFalse(ReservationSortEnum.isValidField(""));
        assertFalse(ReservationSortEnum.isValidField(" "));
        assertFalse(ReservationSortEnum.isValidField("unknown"));
    }

    @Test
    @DisplayName("resolveField retourne le champ correct pour les valeurs valides")
    void resolveFieldShouldReturnCorrectFieldForValid() {
        assertEquals("id", ReservationSortEnum.resolveField("id"));
        assertEquals("type", ReservationSortEnum.resolveField("TYPE"));
        assertEquals("startTime", ReservationSortEnum.resolveField("startTime"));
        assertEquals("endTime", ReservationSortEnum.resolveField("ENDTIME"));
        assertEquals("room.name", ReservationSortEnum.resolveField("ROOM.NAME"));
        assertEquals("user.lastName", ReservationSortEnum.resolveField("user.lastname"));
        assertEquals("description", ReservationSortEnum.resolveField("DESCRIPTION"));
        assertEquals("maxDurationHours", ReservationSortEnum.resolveField("MAXDURATIONHOURS"));
    }

    @Test
    @DisplayName("resolveField retourne ID par défaut pour les valeurs invalides ou null")
    void resolveFieldShouldReturnDefaultForInvalid() {
        assertEquals("id", ReservationSortEnum.resolveField(null));
        assertEquals("id", ReservationSortEnum.resolveField(""));
        assertEquals("id", ReservationSortEnum.resolveField(" "));
        assertEquals("id", ReservationSortEnum.resolveField("unknown"));
    }
}
