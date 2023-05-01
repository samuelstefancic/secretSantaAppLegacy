package com.santa.work.exception.secretSantaGroupExceptions;

import org.springframework.http.HttpStatus;

public class InvalidSecretSantaGroupException extends SecretSantaGroupException{
    public InvalidSecretSantaGroupException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
