// src/main/java/.../controller/UrunController.java

package com.dijitalkuaforum.dijitalkuaforum_backend.controller;

import com.dijitalkuaforum.dijitalkuaforum_backend.model.Barber;
import com.dijitalkuaforum.dijitalkuaforum_backend.model.Urun;
import com.dijitalkuaforum.dijitalkuaforum_backend.service.AuthService;
import com.dijitalkuaforum.dijitalkuaforum_backend.service.UrunServis;
import com.dijitalkuaforum.dijitalkuaforum_backend.exception.UnauthorizedException;
import com.dijitalkuaforum.dijitalkuaforum_backend.exception.ResourceNotFoundException;
import com.dijitalkuaforum.dijitalkuaforum_backend.dto.LoginRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/urunler")
@RequiredArgsConstructor
public class UrunController {

    private final UrunServis urunServis;
    private final AuthService authService; // Admin yetkilendirmesi için

    // Admin Kontrol Mekanizması (Diğer Controller'larınızdan alınmıştır)
    private Optional<Barber> checkAuthentication(String username, String password) {
        if (username == null || password == null) {
            return Optional.empty();
        }
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        return authService.login(loginRequest);
    }

    // --- 1. ÜRÜN EKLEME (CREATE - Admin) ---
    @PostMapping("/create")
    public ResponseEntity<Urun> createUrun(
            @RequestHeader("Username") String username,
            @RequestHeader("Password") String password,
            @RequestBody Urun urun) {

        if (checkAuthentication(username, password).isEmpty()) {
            throw new UnauthorizedException();
        }

        Urun savedUrun = urunServis.urunEkle(urun);
        return new ResponseEntity<>(savedUrun, HttpStatus.CREATED);
    }

    // --- 2. TÜM ÜRÜNLERİ GETİRME (READ ALL - Admin) ---
    @GetMapping("/getAll")
    public ResponseEntity<List<Urun>> getAllUrunler(
            @RequestHeader("Username") String username,
            @RequestHeader("Password") String password) {

        if (checkAuthentication(username, password).isEmpty()) {
            throw new UnauthorizedException();
        }

        List<Urun> urunler = urunServis.tumUrunleriGetir();
        return ResponseEntity.ok(urunler);
    }

    // --- 3. ÜRÜN GÜNCELLEME (UPDATE - Admin) ---
    @PutMapping("/update/{id}")
    public ResponseEntity<Urun> updateUrun(
            @RequestHeader("Username") String username,
            @RequestHeader("Password") String password,
            @PathVariable Long id,
            @RequestBody Urun urunDetaylari) {

        if (checkAuthentication(username, password).isEmpty()) {
            throw new UnauthorizedException();
        }

        Urun updatedUrun = urunServis.urunGuncelle(id, urunDetaylari);
        return ResponseEntity.ok(updatedUrun);
    }

    // --- 4. ÜRÜN SİLME (DELETE - Admin) ---
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUrun(
            @RequestHeader("Username") String username,
            @RequestHeader("Password") String password,
            @PathVariable Long id) {

        if (checkAuthentication(username, password).isEmpty()) {
            throw new UnauthorizedException();
        }

        urunServis.urunSil(id);
        return ResponseEntity.ok("Ürün başarıyla silindi.");
    }
}