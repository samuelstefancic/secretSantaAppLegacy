package com.santa.work.exception.invitationExceptions;

import org.springframework.http.HttpStatus;

public class InvitationException extends RuntimeException {
    private final HttpStatus httpStatus;

    public InvitationException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
    public HttpStatus getHttpStatus() {return httpStatus;}
}
