package com.teddy.jwt_authflow.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TokenVerificationException extends ResponseStatusException {

    private static final long serialVersionUID = 7439642984069939024L;
    private static final String DEFAULT_MESSAGE = "Authentication failure: Token missing, invalid, revoked or expired";

    public TokenVerificationException() {
        super(HttpStatus.UNAUTHORIZED, DEFAULT_MESSAGE);
    }
}
