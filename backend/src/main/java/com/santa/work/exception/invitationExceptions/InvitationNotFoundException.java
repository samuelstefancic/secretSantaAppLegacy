package com.santa.work.exception.invitationExceptions;

import org.springframework.http.HttpStatus;

public class InvitationNotFoundException extends InvitationException{
    public InvitationNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
