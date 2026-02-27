package com.campusRoom.api.exception;

import org.springframework.http.HttpStatus;

public class CampusRoomBusinessException extends BusinessException {

    public CampusRoomBusinessException(String message , HttpStatus status) {

        super( message , status);
    }
}
