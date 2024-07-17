package com.example.server.exceptions;

public class AuthorizationException extends Exception {
    public AuthorizationException() {
        super("Authorization error, invalid username or password!");
    }

    public AuthorizationException(String text) {
        super(text);
    }
}
