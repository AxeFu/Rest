package com.example.server.exceptions;

public class BodyIsNotJsonException extends Exception {
    public BodyIsNotJsonException(String message) {
        super(message);
    }
}
