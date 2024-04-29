package com.mli.discord.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

public class InvalidJwtAuthenticationException extends AuthenticationException {

    private HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

    public InvalidJwtAuthenticationException(String msg) {
        super(msg);
    }

    public InvalidJwtAuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}