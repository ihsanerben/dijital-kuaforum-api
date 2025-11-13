// src/main/java/com/dijitalkuaforum/dijitalkuaforum_backend/controller/RandevuController.java

package com.dijitalkuaforum.dijitalkuaforum_backend.controller;

import com.dijitalkuaforum.dijitalkuaforum_backend.model.*;
import com.dijitalkuaforum.dijitalkuaforum_backend.dto.RandevuTalepDTO;
import com.dijitalkuaforum.dijitalkuaforum_backend.service.CustomerService; // Müşteri bilgisini çekmek için
import com.dijitalkuaforum.dijitalkuaforum_backend.service.RandevuServis;
import com.dijitalkuaforum.dijitalkuaforum_backend.exception.UnauthorizedException; // Yetkilendirme için
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/randevular")
@RequiredArgsConstructor
public class RandevuController {

    private final RandevuServis randevuServis;
    private final CustomerService customerService; // Customer nesnesini bulmak için

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

    // --- 2. TAKVİM GÖRÜNÜMÜ VE KONTROLÜ (Aşama 3'e hazırlık) ---

    // Müsait Saatleri Görüntüleme (Müşteri Takvimi)
    // Bu endpoint, bir gün veya hafta için müsait 5 dakikalık dilimleri döndürür.
    // Şimdilik sadece randevuları getirip Frontend'in işlemesini sağlıyoruz.
    // GET /api/randevular/takvim?tarih=YYYY-MM-DD
    @GetMapping("/takvim")
    public ResponseEntity<List<Randevu>> randevulariGetir(
            @RequestParam(required = false) String tarih,
            @RequestParam(required = false) Long barberId // İleride kuaföre özel takvim için
    ) {
        // İleride bu metod, takvim görünümünün ana verisini sağlayacaktır.
        // RandevuServis'ten o gün/hafta için onaylanmış/beklemedeki randevuları çekip döndürmeliyiz.
        // Şimdilik tüm randevuları getiriyoruz:
        List<Randevu> randevular = randevuServis.tumRandevulariGetir();

        // Gerçek implementasyonda: 
        // randevuServis.getRandevularByDateAndBarber(tarih, barberId); gibi bir metot kullanılacak.

        return ResponseEntity.ok(randevular);
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