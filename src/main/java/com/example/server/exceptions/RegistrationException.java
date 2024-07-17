package com.example.server.exceptions;

public class RegistrationException extends Exception {
    public RegistrationException() {
        super("Registration error, contact technical support!");
    }

    public RegistrationException(String text) {
        super(text);
    }
}
