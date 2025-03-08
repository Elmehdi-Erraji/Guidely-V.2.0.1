package com.spring.guidely.web.error;

public class FaqNotFoundException extends RuntimeException {
    public FaqNotFoundException(String message) {
        super(message);
    }
}