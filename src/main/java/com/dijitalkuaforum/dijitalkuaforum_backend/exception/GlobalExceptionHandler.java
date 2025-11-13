package com.dijitalkuaforum.dijitalkuaforum_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Genel Hata: 500 Internal Server Error döndürür (Tüm beklenmeyen hatalar için)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Beklenmedik bir sunucu hatası oluştu. Lütfen sistem yöneticisi ile iletişime geçin."), HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    // Özel Hata: 404 Not Found döndürür
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    // Yeni Hata: 409 Conflict döndürür
    @ExceptionHandler(DuplicateValueException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateValueException(DuplicateValueException ex) {
        // İstemciye 409 Conflict HTTP durum kodu döndürür
        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        // İstemciye 401 Unauthorized HTTP durum kodu döndürür
        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()),
                HttpStatus.UNAUTHORIZED
        );
    }

    // 409 Conflict (Çakışma) hatalarını yakalar
    @ExceptionHandler(AppointmentConflictException.class)
    public ResponseEntity<String> handleAppointmentConflictException(AppointmentConflictException ex) {
        // Özellikle 409 dönmesi için (Eğer @ResponseStatus doğru çalışmazsa)
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    // GlobalExceptionHandler.java'dan gelen kod
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}