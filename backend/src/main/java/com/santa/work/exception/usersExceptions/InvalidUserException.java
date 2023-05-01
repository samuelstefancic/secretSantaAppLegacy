package com.santa.work.exception.usersExceptions;

import org.springframework.http.HttpStatus;

public class InvalidUserException extends UsersException{
    public InvalidUserException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
