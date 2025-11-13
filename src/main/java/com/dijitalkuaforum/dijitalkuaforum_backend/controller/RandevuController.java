// src/main/java/com/dijitalkuaforum/dijitalkuaforum_backend/controller/RandevuController.java

package com.dijitalkuaforum.dijitalkuaforum_backend.controller;

import com.dijitalkuaforum.dijitalkuaforum_backend.model.*;
import com.dijitalkuaforum.dijitalkuaforum_backend.dto.RandevuTalepDTO;
import com.dijitalkuaforum.dijitalkuaforum_backend.service.CustomerService; // Müşteri bilgisini çekmek için
import com.dijitalkuaforum.dijitalkuaforum_backend.service.RandevuServis;
import com.dijitalkuaforum.dijitalkuaforum_backend.exception.UnauthorizedException; // Yetkilendirme için
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/randevular")
@RequiredArgsConstructor
public class RandevuController {

    private final RandevuServis randevuServis;
    private final CustomerService customerService; // Customer nesnesini bulmak için


    // --- TAKVİM GÖRÜNÜMÜ VE KONTROLÜ (TEK VE TEMİZ METOT) ---
    // Bu metot /api/randevular/takvim rotasını yönetir.
    // GET /api/randevular/takvim?tarih=YYYY-MM-DD&barberId=X
    @GetMapping("/takvim")
    public ResponseEntity<List<Randevu>> getRandevularByDate(
            @RequestParam("tarih") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tarih, // Tarih zorunlu
            @RequestParam(required = false) Long barberId // Kuaför ID'si isteğe bağlı
    ) {
        // RandevuServis'ten o gün/hafta için onaylanmış/beklemedeki randevuları çek
        // RandevuServis'te bu metodu oluşturmamız gerekecek.
        List<Randevu> randevular = randevuServis.getRandevularByDate(tarih, barberId);

        return ResponseEntity.ok(randevular);
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


    // --- 3. ADMİN İŞLEMLERİ (Admin yetkilendirmesi gereklidir) ---

    // Randevunun Durumunu Güncelleme (ONAYLANDI/REDDEDİLDİ)
    // PUT /api/randevular/admin/guncelle/1
    @PutMapping("/admin/guncelle/{id}")
    public ResponseEntity<Randevu> updateRandevuStatus(
            @RequestHeader("Username") String username,
            @RequestHeader("Password") String password,
            @PathVariable Long id,
            @RequestParam String yeniDurum)
    {
        // Yetkilendirme (AuthService'e ihtiyaç duyar, şimdilik atlanmıştır)
        // if (authService.checkAdmin(username, password).isEmpty()) {
        //     throw new UnauthorizedException();
        // }

        // RandevuServis'te bu metotun implementasyonu gereklidir.
        // Randevu guncellenmisRandevu = randevuServis.randevuDurumuGuncelle(id, yeniDurum);
        // return ResponseEntity.ok(guncellenmisRandevu);

        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    // Tüm Randevuları Getirme (Admin Paneli İçin Detaylı Liste)
    // GET /api/randevular/admin/hepsi
    @GetMapping("/admin/hepsi")
    public ResponseEntity<List<Randevu>> getAllRandevularAdmin(
            @RequestHeader("Username") String username,
            @RequestHeader("Password") String password) {

        // Yetkilendirme (AuthService'e ihtiyaç duyar, şimdilik atlanmıştır)
        // if (authService.checkAdmin(username, password).isEmpty()) {
        //     throw new UnauthorizedException();
        // }

        List<Randevu> randevular = randevuServis.tumRandevulariGetir();
        return ResponseEntity.ok(randevular);
    }
}