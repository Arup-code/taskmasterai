package com.dexcode.taskmasterai.exceptions.jwt;

public class RevokedTokenException extends RuntimeException {
    public RevokedTokenException(String message) {
        super(message);
    }
}
