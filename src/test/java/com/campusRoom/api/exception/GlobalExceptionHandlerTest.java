package com.campusRoom.api.exception;

import com.campusRoom.api.entity.ReservationType;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    // ==================== handleBusinessException ====================

    @Test
    @DisplayName("handleBusinessException - doit retourner le statut HTTP de l'exception")
    void should_returnExceptionStatus_when_businessExceptionHandled() {
        CampusRoomBusinessException ex =
                new CampusRoomBusinessException("Campus introuvable", HttpStatus.NOT_FOUND);
        when(request.getRequestURI()).thenReturn("/campus");

        ResponseEntity<ApiErrorResponse> response = handler.handleBusinessException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("handleBusinessException - le corps doit contenir le code HTTP numérique")
    void should_containStatusCode_in_body_when_businessExceptionHandled() {
        CampusRoomBusinessException ex =
                new CampusRoomBusinessException("Conflit", HttpStatus.CONFLICT);
        when(request.getRequestURI()).thenReturn("/campus");

        ResponseEntity<ApiErrorResponse> response = handler.handleBusinessException(ex, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(409);
    }

    @Test
    @DisplayName("handleBusinessException - le corps doit contenir le message de l'exception")
    void should_containExceptionMessage_in_body_when_businessExceptionHandled() {
        CampusRoomBusinessException ex =
                new CampusRoomBusinessException("Campus introuvable", HttpStatus.NOT_FOUND);
        when(request.getRequestURI()).thenReturn("/campus");

        ResponseEntity<ApiErrorResponse> response = handler.handleBusinessException(ex, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Campus introuvable");
    }

    @Test
    @DisplayName("handleBusinessException - le corps doit contenir le path de la requête")
    void should_containRequestPath_in_body_when_businessExceptionHandled() {
        CampusRoomBusinessException ex =
                new CampusRoomBusinessException("msg", HttpStatus.NOT_FOUND);
        when(request.getRequestURI()).thenReturn("/campus");

        ResponseEntity<ApiErrorResponse> response = handler.handleBusinessException(ex, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().path()).isEqualTo("/campus");
    }

    @Test
    @DisplayName("handleBusinessException - le corps doit contenir le reason phrase du statut")
    void should_containReasonPhrase_in_body_when_businessExceptionHandled() {
        CampusRoomBusinessException ex =
                new CampusRoomBusinessException("msg", HttpStatus.NOT_FOUND);
        when(request.getRequestURI()).thenReturn("/room");

        ResponseEntity<ApiErrorResponse> response = handler.handleBusinessException(ex, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("Not Found");
    }

    @Test
    @DisplayName("handleBusinessException - le corps doit contenir un timestamp non null")
    void should_containNonNullTimestamp_when_businessExceptionHandled() {
        CampusRoomBusinessException ex =
                new CampusRoomBusinessException("msg", HttpStatus.CONFLICT);
        when(request.getRequestURI()).thenReturn("/user");

        ResponseEntity<ApiErrorResponse> response = handler.handleBusinessException(ex, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    @DisplayName("handleBusinessException - validationErrors doit être null pour une BusinessException")
    void should_haveNullValidationErrors_when_businessExceptionHandled() {
        CampusRoomBusinessException ex =
                new CampusRoomBusinessException("msg", HttpStatus.NOT_FOUND);
        when(request.getRequestURI()).thenReturn("/campus");

        ResponseEntity<ApiErrorResponse> response = handler.handleBusinessException(ex, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().validationErrors()).isNull();
    }

    // ==================== handleValidationException ====================

    @Test
    @DisplayName("handleValidationException - doit retourner 400 BAD_REQUEST")
    void should_return400_when_validationExceptionHandled() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());
        when(request.getRequestURI()).thenReturn("/campus");

        ResponseEntity<ApiErrorResponse> response = handler.handleValidationException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("handleValidationException - doit contenir les erreurs de validation dans la map")
    void should_containFieldErrors_when_validationExceptionHandled() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("campusFormDto", "name", "ne doit pas être vide");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(request.getRequestURI()).thenReturn("/campus");

        ResponseEntity<ApiErrorResponse> response = handler.handleValidationException(ex, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().validationErrors())
                .containsEntry("name", "ne doit pas être vide");
    }

    @Test
    @DisplayName("handleValidationException - doit regrouper plusieurs erreurs de champs différents")
    void should_containMultipleFieldErrors_when_multipleFieldsInvalid() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError nameError = new FieldError("campusFormDto", "name", "ne doit pas être vide");
        FieldError cityError = new FieldError("campusFormDto", "city", "ne doit pas être vide");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(nameError, cityError));
        when(request.getRequestURI()).thenReturn("/campus");

        ResponseEntity<ApiErrorResponse> response = handler.handleValidationException(ex, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().validationErrors())
                .containsKey("name")
                .containsKey("city");
    }

    @Test
    @DisplayName("handleValidationException - le message doit indiquer une erreur de validation")
    void should_containValidationMessage_in_body() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());
        when(request.getRequestURI()).thenReturn("/room");

        ResponseEntity<ApiErrorResponse> response = handler.handleValidationException(ex, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Erreur de validation des champs");
    }

    // ==================== handleNotReadable — corps générique ====================

    @Test
    @DisplayName("handleNotReadable - doit retourner 400 pour un corps de requête illisible")
    void should_return400_when_messageNotReadable() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        RuntimeException genericCause = new RuntimeException("cause générique");
        when(ex.getMostSpecificCause()).thenReturn(genericCause);
        when(request.getRequestURI()).thenReturn("/reservation");

        ResponseEntity<ApiErrorResponse> response = handler.handleNotReadable(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("handleNotReadable - le message doit être 'Requête invalide' pour une cause non-enum")
    void should_returnGenericMessage_when_causeIsNotInvalidFormat() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        RuntimeException genericCause = new RuntimeException("cause générique");
        when(ex.getMostSpecificCause()).thenReturn(genericCause);
        when(request.getRequestURI()).thenReturn("/reservation");

        ResponseEntity<ApiErrorResponse> response = handler.handleNotReadable(ex, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Requête invalide");
    }

    // ==================== handleNotReadable — valeur enum invalide ====================

    @Test
    @DisplayName("handleNotReadable - doit mentionner la valeur invalide quand la cause est un enum mal formé")
    void should_containInvalidValue_when_causeIsInvalidFormatOnEnum() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        InvalidFormatException invalidFormat = mock(InvalidFormatException.class);
        when(invalidFormat.getTargetType()).thenReturn((Class) ReservationType.class);
        when(invalidFormat.getValue()).thenReturn("INVALID_TYPE");
        when(ex.getMostSpecificCause()).thenReturn(invalidFormat);
        when(request.getRequestURI()).thenReturn("/reservation");

        ResponseEntity<ApiErrorResponse> response = handler.handleNotReadable(ex, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).contains("INVALID_TYPE");
    }

    @Test
    @DisplayName("handleNotReadable - doit lister les valeurs enum acceptées quand la cause est un enum mal formé")
    void should_containAcceptedEnumValues_when_causeIsInvalidFormatOnEnum() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        InvalidFormatException invalidFormat = mock(InvalidFormatException.class);
        when(invalidFormat.getTargetType()).thenReturn((Class) ReservationType.class);
        when(invalidFormat.getValue()).thenReturn("TOTO");
        when(ex.getMostSpecificCause()).thenReturn(invalidFormat);
        when(request.getRequestURI()).thenReturn("/reservation");

        ResponseEntity<ApiErrorResponse> response = handler.handleNotReadable(ex, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message())
                .contains("COURSE")
                .contains("MEETING")
                .contains("EXAM");
    }

    @Test
    @DisplayName("handleNotReadable - doit retourner 400 même pour un enum invalide")
    void should_return400_when_enumValueIsInvalid() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        InvalidFormatException invalidFormat = mock(InvalidFormatException.class);
        when(invalidFormat.getTargetType()).thenReturn((Class) ReservationType.class);
        when(invalidFormat.getValue()).thenReturn("WRONG");
        when(ex.getMostSpecificCause()).thenReturn(invalidFormat);
        when(request.getRequestURI()).thenReturn("/reservation");

        ResponseEntity<ApiErrorResponse> response = handler.handleNotReadable(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().status()).isEqualTo(400);
    }
}
