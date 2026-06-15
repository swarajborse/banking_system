package com.swaraj.banking_system.exception;

public class DuplicateResourceException
        extends RuntimeException {

    public DuplicateResourceException(
            String message
    ) {
        super(message);
    }
}