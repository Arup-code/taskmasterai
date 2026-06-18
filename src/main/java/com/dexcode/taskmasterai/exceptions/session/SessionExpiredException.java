package com.dexcode.taskmasterai.exceptions.session;

public class SessionExpiredException extends RuntimeException {
    public SessionExpiredException(String message) {
        super(message);
    }
}
