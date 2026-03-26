package com.campusRoom.api.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests unitaires pour CampusRoomBusinessException")
class CampusRoomBusinessExceptionTest {

    @Test
    @DisplayName("Doit créer une exception avec message et statut")
    void shouldCreateExceptionWithMessageAndStatus() {
        // Arrange
        String message = "Salle introuvable";
        HttpStatus status = HttpStatus.NOT_FOUND;

        // Act
        CampusRoomBusinessException ex = new CampusRoomBusinessException(message, status);

        // Assert
        assertNotNull(ex, "L'exception ne doit pas être null");
        assertEquals(message, ex.getMessage(), "Le message doit être correct");
        assertEquals(status, ex.getStatus(), "Le status doit être correct");
        assertTrue(ex instanceof BusinessException, "Doit hériter de BusinessException");
        assertTrue(ex instanceof RuntimeException, "Doit hériter de RuntimeException");
    }

    @Test
    @DisplayName("getStatus doit renvoyer le code HTTP correct")
    void shouldReturnCorrectHttpStatus() {
        CampusRoomBusinessException ex = new CampusRoomBusinessException("Erreur", HttpStatus.BAD_REQUEST);
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus(), "Le statut HTTP doit correspondre");
    }

    @Test
    @DisplayName("getMessage doit renvoyer le message correct")
    void shouldReturnCorrectMessage() {
        CampusRoomBusinessException ex = new CampusRoomBusinessException("Erreur spécifique", HttpStatus.CONFLICT);
        assertEquals("Erreur spécifique", ex.getMessage(), "Le message doit correspondre");
    }
}