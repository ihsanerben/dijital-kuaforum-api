package com.dijitalkuaforum.dijitalkuaforum_backend.exception;

// RuntimeException olarak tanımlıyoruz ki, try-catch blokları olmadan fırlatılabilinsin
public class DuplicateValueException extends RuntimeException {

    public DuplicateValueException(Object fieldValue, String fieldName ) {
        // Hata mesajı formatı: "Customer already exists with Email : 'test@test.com'"
        super(String.format("%s %s değeri sistemde zaten mevcut.", fieldValue,  fieldName));
    }
}