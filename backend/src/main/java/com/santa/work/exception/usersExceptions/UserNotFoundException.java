package com.santa.work.exception.usersExceptions;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends UsersException{

    public UserNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
