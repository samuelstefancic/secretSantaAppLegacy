package com.santa.work.exception.invitationExceptions;

import org.springframework.http.HttpStatus;

public class InvalidInvitationException extends InvitationException{
    public InvalidInvitationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
