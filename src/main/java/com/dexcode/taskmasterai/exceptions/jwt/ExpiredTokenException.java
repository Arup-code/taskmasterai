package com.dexcode.taskmasterai.exceptions.jwt;

public class ExpiredTokenException extends RuntimeException {
    public ExpiredTokenException(String message) {
        super(message);
    }
}
