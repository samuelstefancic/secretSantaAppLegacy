package com.santa.work.exception.secretSantaGroupExceptions;

import org.springframework.http.HttpStatus;

public class SecretSantaGroupNotFoundException extends SecretSantaGroupException{
    public SecretSantaGroupNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
