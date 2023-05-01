package com.santa.work.exception.secretSantaGroupExceptions;

import org.springframework.http.HttpStatus;

public class SecretSantaGroupException extends RuntimeException{

    private final HttpStatus httpStatus;

    public SecretSantaGroupException(String message, HttpStatus httpStatus){
        super(message);
        this.httpStatus = httpStatus;
    }
    public HttpStatus getHttpStatus() {return httpStatus;}
}
