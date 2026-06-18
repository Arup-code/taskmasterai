package com.dexcode.taskmasterai.exceptions.auth;

public class InvalidAuthCredentialException extends RuntimeException {
    public InvalidAuthCredentialException(String message) {
        super(message);
    }
}
