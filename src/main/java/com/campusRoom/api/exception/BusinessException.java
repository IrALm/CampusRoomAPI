package com.campusRoom.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BusinessException extends RuntimeException {

    /**
     * Le code HTTP associé à cette exception.
     *  Retourne le code HTTP associé à cette exception.
     */
    private final HttpStatus status;

    /**
     * Construit une exception métier avec un message et un statut HTTP.
     *
     * @param message le message décrivant l'erreur métier
     * @param status  le code HTTP à renvoyer
     */
    protected BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
