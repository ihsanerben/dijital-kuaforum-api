// src/main/java/com/dijitalkuaforum/dijitalkuaforum_backend/exception/AppointmentConflictException.java

package com.dijitalkuaforum.dijitalkuaforum_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // HTTP 409
public class AppointmentConflictException extends RuntimeException {
    public AppointmentConflictException(String message) {
        super(message);
    }
}