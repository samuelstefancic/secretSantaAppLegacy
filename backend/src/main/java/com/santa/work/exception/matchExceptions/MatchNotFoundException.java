package com.santa.work.exception.matchExceptions;

import org.springframework.http.HttpStatus;

public class MatchNotFoundException extends MatchException{
    public MatchNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
