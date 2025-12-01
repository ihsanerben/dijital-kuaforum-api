package com.dijitalkuaforum.dijitalkuaforum_backend.controller;

import com.dijitalkuaforum.dijitalkuaforum_backend.model.Barber;
import com.dijitalkuaforum.dijitalkuaforum_backend.model.Hizmet;
import com.dijitalkuaforum.dijitalkuaforum_backend.service.AuthService;
import com.dijitalkuaforum.dijitalkuaforum_backend.service.HizmetServis;
import com.dijitalkuaforum.dijitalkuaforum_backend.exception.UnauthorizedException;
import com.dijitalkuaforum.dijitalkuaforum_backend.dto.LoginRequestDTO; // AuthService'in ihtiyacı varsa
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/hizmetler")
@RequiredArgsConstructor
public class HizmetController {

    private final HizmetServis hizmetServis;
    private final AuthService authService; // Admin yetkilendirmesi için

    // Admin Kontrol Mekanizması (CustomerController'dan aldığınız benzer yapı)
    private Optional<Barber> checkAuthentication(String username, String password) {
        if (username == null || password == null) {
            return Optional.empty();
        }
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        return authService.login(loginRequest);
    }

    // --- 1. ADMIN CRUD İŞLEMLERİ ---

    // Hizmet Ekleme (CREATE - Sadece Admin)
    @PostMapping("/create")
    public ResponseEntity<Hizmet> createHizmet(
            @RequestHeader("Username") String username,
            @RequestHeader("Password") String password,
            @RequestBody Hizmet hizmet) {

        if (checkAuthentication(username, password).isEmpty()) {
            throw new UnauthorizedException();
        }

        Hizmet savedHizmet = hizmetServis.hizmetEkle(hizmet);
        return new ResponseEntity<>(savedHizmet, HttpStatus.CREATED);
    }

    // Hizmet Güncelleme (UPDATE - Sadece Admin)
    @PutMapping("/update/{id}")
    public ResponseEntity<Hizmet> updateHizmet(
            @RequestHeader("Username") String username,
            @RequestHeader("Password") String password,
            @PathVariable Long id,
            @RequestBody Hizmet hizmetDetaylari) {

        if (checkAuthentication(username, password).isEmpty()) {
            throw new UnauthorizedException();
        }

        Hizmet updatedHizmet = hizmetServis.hizmetGuncelle(id, hizmetDetaylari);
        return ResponseEntity.ok(updatedHizmet);
    }

    // Hizmet Silme (DELETE - Sadece Admin)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteHizmet(
            @RequestHeader("Username") String username,
            @RequestHeader("Password") String password,
            @PathVariable Long id) {

        if (checkAuthentication(username, password).isEmpty()) {
            throw new UnauthorizedException();
        }

        hizmetServis.hizmetSil(id);
        return ResponseEntity.ok("Hizmet başarıyla silindi.");
    }

    // --- 2. PUBLIC/MÜŞTERİ ERİŞİMİ ---

    // Tüm hizmetleri listeleme (READ ALL - Herkes)
    @GetMapping("/public/getAll")
    public ResponseEntity<List<Hizmet>> getAllHizmetler() {
        List<Hizmet> hizmetler = hizmetServis.tumHizmetleriGetir();
        return ResponseEntity.ok(hizmetler);
    }

}