// src/main/java/.../controller/RandevuController.java (GÜNCELLENMİŞ)

package com.dijitalkuaforum.dijitalkuaforum_backend.controller;

import com.dijitalkuaforum.dijitalkuaforum_backend.dto.LoginRequestDTO;
import com.dijitalkuaforum.dijitalkuaforum_backend.model.*;
import com.dijitalkuaforum.dijitalkuaforum_backend.dto.RandevuTalepDTO;
import com.dijitalkuaforum.dijitalkuaforum_backend.service.AuthService;
import com.dijitalkuaforum.dijitalkuaforum_backend.service.CustomerService; // Müşteri bilgisini çekmek için
import com.dijitalkuaforum.dijitalkuaforum_backend.service.RandevuServis;
import com.dijitalkuaforum.dijitalkuaforum_backend.exception.UnauthorizedException; // Yetkilendirme için
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/randevular")
@RequiredArgsConstructor
public class RandevuController {

    private final RandevuServis randevuServis;
    private final AuthService authService;
    private final CustomerService customerService;

    // Admin Kontrol Mekanizması (Aynı Kalır)
    private Optional<Barber> checkAuthentication(String username, String password) {
        if (username == null || password == null) {
            return Optional.empty();
        }
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        return authService.login(loginRequest);
    }

    // --- YENİ: MÜŞTERİ DETAY İSTATİSTİĞİ (Aynı Kalır) ---
    @GetMapping("/admin/musteriGecmis/{customerId}")
    public ResponseEntity<List<Randevu>> getMusteriGecmisRandevulari(
            @RequestHeader("Username") String username,
            @RequestHeader("Password") String password,
            @PathVariable Long customerId) {

        if (checkAuthentication(username, password).isEmpty()) {
            throw new UnauthorizedException();
        }

        List<Randevu> randevular = randevuServis.musteriGecmisRandevulariniGetir(customerId);
        return ResponseEntity.ok(randevular);
    }


    // --- TAKVİM GÖRÜNÜMÜ VE KONTROLÜ (Aynı Kalır) ---
    @GetMapping("/takvim")
    public ResponseEntity<List<Randevu>> getRandevularByDate(
            @RequestParam("tarih") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tarih,
            @RequestParam(required = false) Long barberId
    ) {
        List<Randevu> randevular = randevuServis.getRandevularByDate(tarih, barberId);
        return ResponseEntity.ok(randevular);
    }


    // --- YENİ: İSTATİSTİK RAPORU ENDPOINT'İ (Aşama 4.3) ---
    // GET /api/randevular/admin/istatistik
    @GetMapping("/admin/istatistik")
    public ResponseEntity<Map<String, Object>> getIstatistikRaporu(
            @RequestHeader("Username") String username,
            @RequestHeader("Password") String password) {

        if (checkAuthentication(username, password).isEmpty()) {
            throw new UnauthorizedException();
        }

        Map<String, Object> rapor = randevuServis.getIstatistikRaporu();

        return ResponseEntity.ok(rapor);
    }

    // --- 3. ADMİN İŞLEMLERİ (Aynı Kalır) ---
    @PutMapping("/admin/guncelle/{id}")
    public ResponseEntity<Randevu> updateRandevuStatus(
            @RequestHeader("Username") String username,
            @RequestHeader("Password") String password,
            @PathVariable Long id,
            @RequestParam String yeniDurum)
    {
        if (checkAuthentication(username, password).isEmpty()) {
            throw new UnauthorizedException();
        }

        Randevu guncellenmisRandevu = randevuServis.randevuDurumuGuncelle(id, yeniDurum);
        return ResponseEntity.ok(guncellenmisRandevu);
    }

    @GetMapping("/admin/hepsi")
    public ResponseEntity<List<Randevu>> getAllRandevularAdmin(
            @RequestHeader("Username") String username,
            @RequestHeader("Password") String password) {

        if (checkAuthentication(username, password).isEmpty()) {
            throw new UnauthorizedException();
        }

        List<Randevu> randevular = randevuServis.tumRandevulariGetir();
        return ResponseEntity.ok(randevular);
    }



    // --- YENİ: MÜSAİT SLOTLARI GETİRME ENDPOINT'İ (Admin) ---
    // GET /api/randevular/admin/availableSlots?serviceId=...&date=...
    @GetMapping("/admin/availableSlots")
    public ResponseEntity<List<String>> getAvailableSlots(
            @RequestHeader("Username") String username,
            @RequestHeader("Password") String password,
            @RequestParam("serviceId") Long hizmetId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        if (checkAuthentication(username, password).isEmpty()) {
            throw new UnauthorizedException();
        }

        List<String> slots = randevuServis.getAvailableSlotsAdmin(hizmetId, date);
        return ResponseEntity.ok(slots);
    }


    @PostMapping("/admin/create")
    public ResponseEntity<Randevu> createAdminRandevu(
            @RequestHeader("Username") String username,
            @RequestHeader("Password") String password,
            @RequestBody RandevuTalepDTO talepDTO) {

        if (checkAuthentication(username, password).isEmpty()) {
            throw new UnauthorizedException();
        }

        Customer customer = customerService.idIleMusteriGetir(talepDTO.getCustomerId());

        Randevu yeniRandevu = randevuServis.randevuOlustur(
                customer,
                talepDTO.getStartTime(),
                talepDTO.getHizmetIdleri());

        return new ResponseEntity<>(yeniRandevu, HttpStatus.CREATED);
    }

    // --- 1. MÜŞTERİ İŞLEMLERİ ---

    // Yeni Randevu Oluşturma (Müşteri)
    // POST /api/randevular/olustur
    @PostMapping("/olustur")
    public ResponseEntity<Randevu> randevuOlustur(@RequestBody RandevuTalepDTO talepDTO) {

        // ÖNEMLİ NOT: Gerçek bir sistemde bu customerId, JWT token veya Session bilgisinden alınmalıdır.
        // Şimdilik Servis katmanında kullanılmak üzere DTO'dan alıyoruz.
        Customer customer = customerService.idIleMusteriGetir(talepDTO.getCustomerId());

        Randevu yeniRandevu = randevuServis.randevuOlustur(
                customer,
                talepDTO.getStartTime(),
                talepDTO.getHizmetIdleri()
        );

        return new ResponseEntity<>(yeniRandevu, HttpStatus.CREATED);
    }
}