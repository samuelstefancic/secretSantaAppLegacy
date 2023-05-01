package com.santa.work.exception.matchExceptions;

import org.springframework.http.HttpStatus;

public class InvalidMatchException extends MatchException{
    public InvalidMatchException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
