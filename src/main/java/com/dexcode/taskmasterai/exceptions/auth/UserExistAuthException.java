package com.dexcode.taskmasterai.exceptions.auth;

public class UserExistAuthException extends RuntimeException {
    public UserExistAuthException(String message) {
        super(message);
    }
}
