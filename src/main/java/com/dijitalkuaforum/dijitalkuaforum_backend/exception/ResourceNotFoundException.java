package com.dijitalkuaforum.dijitalkuaforum_backend.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        // Hata mesajı formatı: "User not found with id : 123"
        super(String.format("%s bulunamadı. (%s : '%s')", resourceName, fieldName, fieldValue));
    }
}