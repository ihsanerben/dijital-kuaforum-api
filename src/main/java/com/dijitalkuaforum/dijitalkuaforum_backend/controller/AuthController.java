package com.dijitalkuaforum.dijitalkuaforum_backend.controller;

import com.dijitalkuaforum.dijitalkuaforum_backend.dto.LoginRequestDTO;
import com.dijitalkuaforum.dijitalkuaforum_backend.dto.ResponseRequestDTO;
import com.dijitalkuaforum.dijitalkuaforum_backend.model.Barber;
import com.dijitalkuaforum.dijitalkuaforum_backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth") // Tüm metotlar bu path altında çalışacak
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        // Validation (boş kontrolü)
        if (loginRequest.getUsername() == null || loginRequest.getPassword() == null ||
                loginRequest.getUsername().trim().isEmpty() || loginRequest.getPassword().trim().isEmpty()) {
            return new ResponseEntity<>("Kullanıcı adı ve şifre boş bırakılamaz.", HttpStatus.BAD_REQUEST);
        }

        Optional<Barber> optionalBarber = authService.login(loginRequest);

        if (optionalBarber.isPresent()) {
            // Login başarılı
            // Güvenlik için şifre alanı null'a çekilebilir veya şifresiz bir DTO döndürülebilir.
            Barber loggedInBarber = optionalBarber.get();

            ResponseRequestDTO responseRequestDTO = new ResponseRequestDTO();

            responseRequestDTO.setId(loggedInBarber.getId());
            responseRequestDTO.setUsername(loggedInBarber.getUsername());


            return new ResponseEntity<>("Kullanici giris yapti: " + responseRequestDTO, HttpStatus.OK); // 200 OK
        } else {
            // Login başarısız
            return new ResponseEntity<>("Kullanıcı adı veya şifre hatalı.", HttpStatus.UNAUTHORIZED); // 401 Unauthorized
        }
    }
}