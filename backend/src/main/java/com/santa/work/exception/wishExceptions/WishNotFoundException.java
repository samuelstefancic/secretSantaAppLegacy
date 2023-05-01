package com.santa.work.exception.wishExceptions;

import org.springframework.http.HttpStatus;

public class WishNotFoundException extends WishException{
    public WishNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
