package com.campusRoom.api.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class CampusRoomBusinessExceptionTest {

    // ==================== Héritage ====================

    @Test
    @DisplayName("CampusRoomBusinessException doit être une sous-classe de BusinessException")
    void should_beInstanceOf_BusinessException() {
        CampusRoomBusinessException ex =
                new CampusRoomBusinessException("msg", HttpStatus.BAD_REQUEST);

        assertThat(ex).isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("CampusRoomBusinessException doit être une RuntimeException")
    void should_beInstanceOf_RuntimeException() {
        CampusRoomBusinessException ex =
                new CampusRoomBusinessException("msg", HttpStatus.BAD_REQUEST);

        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    // ==================== getMessage ====================

    @Test
    @DisplayName("getMessage - doit retourner le message passé au constructeur")
    void should_returnMessage_when_constructed() {
        CampusRoomBusinessException ex =
                new CampusRoomBusinessException("Campus introuvable", HttpStatus.NOT_FOUND);

        assertThat(ex.getMessage()).isEqualTo("Campus introuvable");
    }

    @Test
    @DisplayName("getMessage - doit préserver un message avec caractères spéciaux")
    void should_preserveMessage_with_specialCharacters() {
        String message = "L'email alice@esgi.fr est déjà utilisé.";
        CampusRoomBusinessException ex =
                new CampusRoomBusinessException(message, HttpStatus.CONFLICT);

        assertThat(ex.getMessage()).isEqualTo(message);
    }

    // ==================== getStatus ====================

    @Test
    @DisplayName("getStatus - doit retourner NOT_FOUND quand construit avec NOT_FOUND")
    void should_returnNotFound_when_constructedWithNotFound() {
        CampusRoomBusinessException ex =
                new CampusRoomBusinessException("introuvable", HttpStatus.NOT_FOUND);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("getStatus - doit retourner CONFLICT quand construit avec CONFLICT")
    void should_returnConflict_when_constructedWithConflict() {
        CampusRoomBusinessException ex =
                new CampusRoomBusinessException("déjà existant", HttpStatus.CONFLICT);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("getStatus - doit retourner BAD_REQUEST quand construit avec BAD_REQUEST")
    void should_returnBadRequest_when_constructedWithBadRequest() {
        CampusRoomBusinessException ex =
                new CampusRoomBusinessException("invalide", HttpStatus.BAD_REQUEST);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("getStatus - doit retourner INTERNAL_SERVER_ERROR quand construit avec 500")
    void should_returnInternalServerError_when_constructedWith500() {
        CampusRoomBusinessException ex =
                new CampusRoomBusinessException("erreur interne", HttpStatus.INTERNAL_SERVER_ERROR);

        assertThat(ex.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ==================== cohérence message + status ====================

    @Test
    @DisplayName("message et status doivent être indépendants l'un de l'autre")
    void should_storeMessageAndStatusIndependently() {
        String message = "Salle introuvable";
        HttpStatus status = HttpStatus.NOT_FOUND;

        CampusRoomBusinessException ex = new CampusRoomBusinessException(message, status);

        assertThat(ex.getMessage()).isEqualTo(message);
        assertThat(ex.getStatus()).isEqualTo(status);
        assertThat(ex.getStatus().value()).isEqualTo(404);
    }
}
