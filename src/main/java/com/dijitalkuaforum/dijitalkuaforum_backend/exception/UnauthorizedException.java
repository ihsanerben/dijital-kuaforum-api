package com.dijitalkuaforum.dijitalkuaforum_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// @ResponseStatus anotasyonu, bu hata fırlatıldığında Spring'in otomatik olarak 401 döndürmesini sağlar
// Ancak biz zaten @ExceptionHandler ile merkezi yönetimi kurduğumuz için opsiyoneldir, yine de ekleyebiliriz.
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {

    // Kullanıcıya yönelik net bir mesaj içerir
    private static final String DEFAULT_MESSAGE = "Yetkisiz Erişim: Geçerli kimlik bilgileri gereklidir.";

    public UnauthorizedException() {
        super(DEFAULT_MESSAGE);
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}