package com.vhs.rental.vhsrentalsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
 * Custom runtime exception used for violations of business rules
 * (for example, trying to rent an already rented VHS).
 * Typically results in an HTTP 400 Bad Request response.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessLogicException extends RuntimeException {
    private Object[] args;

    public BusinessLogicException(String messageKey) {
        super(messageKey);
    }

    public BusinessLogicException(String messageKey, Object[] args) {
        super(messageKey);
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }
}