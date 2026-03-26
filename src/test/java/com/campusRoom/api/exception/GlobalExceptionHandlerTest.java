package com.campusRoom.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Tests unitaires pour GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("handleBusinessException renvoie la réponse correcte")
    void shouldHandleBusinessException() {
        when(request.getRequestURI()).thenReturn("/api/test");
        CampusRoomBusinessException ex =
                new CampusRoomBusinessException("Erreur métier", HttpStatus.CONFLICT);

        ResponseEntity<ApiErrorResponse> response = handler.handleBusinessException(ex, request);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Erreur métier", response.getBody().message());
        assertEquals("/api/test", response.getBody().path());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    @DisplayName("handleValidationException renvoie les erreurs de champ")
    void shouldHandleValidationException() {
        when(request.getRequestURI()).thenReturn("/api/validation");

        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("user", "name", "Nom obligatoire");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ApiErrorResponse> response = handler.handleValidationException(ex, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Erreur de validation des champs", response.getBody().message());
        assertEquals("/api/validation", response.getBody().path());
        assertNotNull(response.getBody().timestamp());

        // Vérifie le détail des erreurs de champ
        assertNotNull(response.getBody().validationErrors());
        assertEquals(1, response.getBody().validationErrors().size());
        assertEquals("Nom obligatoire", response.getBody().validationErrors().get("name"));
    }
}