package com.santa.work.exception.usersExceptions;

import org.springframework.http.HttpStatus;

public class UsersException extends RuntimeException{
    private final HttpStatus httpStatus;

    public UsersException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
    public HttpStatus getHttpStatus() {return httpStatus;}

}
