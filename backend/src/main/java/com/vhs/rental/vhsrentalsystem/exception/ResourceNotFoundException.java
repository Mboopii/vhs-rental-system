package com.vhs.rental.vhsrentalsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
 * Custom runtime exception used when a requested resource (User, Vhs)
 * cannot be found in the database.
 * Typically results in an HTTP 404 Not Found response.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    private Object[] args;

    public ResourceNotFoundException(String messageKey) {
        super(messageKey);
    }

    public ResourceNotFoundException(String messageKey, Object[] args) {
        super(messageKey);
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }
}
