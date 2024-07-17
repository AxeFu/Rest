package com.example.server.exceptions;

public class AddTableQueryException extends Exception {
    public AddTableQueryException() {
    }
    public AddTableQueryException(String text) {
        super(text);
    }
}
