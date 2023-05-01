package com.santa.work.exception.wishExceptions;

import org.springframework.http.HttpStatus;

public class InvalidWishException extends WishException{
    public InvalidWishException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
