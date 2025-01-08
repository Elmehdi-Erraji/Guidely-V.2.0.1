package com.spring.guidely.web.error;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
