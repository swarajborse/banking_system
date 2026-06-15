package com.swaraj.banking_system.exception;

public class InvalidLoginException
        extends RuntimeException {

    public InvalidLoginException(
            String message
    ) {
        super(message);
    }
}