package com.santa.work.exception.matchExceptions;

import org.springframework.http.HttpStatus;

public class MatchException extends RuntimeException{
    private final HttpStatus httpStatus;

    public MatchException(String message, HttpStatus httpStatus){
        super(message);
        this.httpStatus = httpStatus;
    }
    public HttpStatus getHttpStatus() {return httpStatus;}
}
