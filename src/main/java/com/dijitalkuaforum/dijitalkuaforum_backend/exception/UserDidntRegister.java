package com.dijitalkuaforum.dijitalkuaforum_backend.exception;

public class UserDidntRegister extends RuntimeException {
    public UserDidntRegister(String message) {
        super(String.format(message));
    }
}