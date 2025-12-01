package com.dijitalkuaforum.dijitalkuaforum_backend.exception;

// RuntimeException olarak tanımlıyoruz ki, try-catch blokları olmadan fırlatılabilinsin
public class DuplicateValueException extends RuntimeException {

    public DuplicateValueException(String message) {
        // Hata mesajı formatı: "Customer already exists with Email : 'test@test.com'"
        super(String.format(message));
    }
}