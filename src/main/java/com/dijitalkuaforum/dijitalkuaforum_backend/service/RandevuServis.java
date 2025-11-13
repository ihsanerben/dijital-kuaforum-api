// src/main/java/com/dijitalkuaforum/dijitalkuaforum_backend/service/RandevuServis.java

package com.dijitalkuaforum.dijitalkuaforum_backend.service;

import com.dijitalkuaforum.dijitalkuaforum_backend.model.*;
import com.dijitalkuaforum.dijitalkuaforum_backend.repository.*;
import com.dijitalkuaforum.dijitalkuaforum_backend.exception.AppointmentConflictException; // İleride oluşturacağız
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RandevuServis {

    private final RandevuRepository randevuRepository;
    private final RandevuHizmetRepository randevuHizmetRepository;
    private final HizmetRepository hizmetRepository; // Süre/Fiyat çekmek için
    private final BarberRepository barberRepository; // Kuaför bulmak için (Opsiyonel)

    // Randevu Çakışma Exception'ı (Eğer henüz yoksa, ileride oluşturacağız)
    // Bu, HTTP 409 Conflict döndürmek için kullanılacaktır.
    // public class AppointmentConflictException extends RuntimeException { ... }

    // Kuaförün çalışma saatlerini varsayıyoruz (Daha basit bir sistem için)
    private static final int IS_BASLANGIC_SAATI = 9; // 09:00
    private static final int IS_BITIS_SAATI = 18;   // 18:00

    // --- RANDUVU OLUŞTURMA İŞLEMİ (Müşteri/Admin) ---
    @Transactional
    public Randevu randevuOlustur(
            Customer customer,
            LocalDateTime startTime,
            List<Long> hizmetIdleri)
    {
        // 1. Kuaför Seçimi (Şimdilik ilk kuaförü seçelim, tek kuaför varsayımıyla)
        Barber kuaför = barberRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Sistemde kayıtlı kuaför bulunamadı."));

        // 2. Toplam Süre ve Fiyat Hesaplama
        int toplamSureDakika = 0;
        BigDecimal toplamFiyat = BigDecimal.ZERO;

        List<Hizmet> secilenHizmetler = hizmetIdleri.stream()
                .map(id -> hizmetRepository.findById(id).orElseThrow(() -> new RuntimeException("Hizmet bulunamadı.")))
                .collect(Collectors.toList());

        for (Hizmet hizmet : secilenHizmetler) {
            toplamSureDakika += hizmet.getSureDakika();
            toplamFiyat = toplamFiyat.add(hizmet.getFiyat());
        }

        LocalDateTime endTime = startTime.plusMinutes(toplamSureDakika);

        // 3. Çalışma Saati Kontrolü
        if (startTime.getHour() < IS_BASLANGIC_SAATI || endTime.getHour() >= IS_BITIS_SAATI || startTime.toLocalDate().isBefore(LocalDateTime.now().toLocalDate())) {
            throw new IllegalArgumentException("Randevu çalışma saatleri veya geçmiş bir tarih dışındadır.");
        }

        // 4. Çakışma Kontrolü
        if (isTimeSlotAvailable(kuaför, startTime, endTime)) {
            // Eğer çakışma varsa
            throw new AppointmentConflictException("Seçilen saat aralığı doludur veya çakışmaktadır.");
        }

        // 5. Randevu Modelini Oluştur
        Randevu yeniRandevu = new Randevu();
        yeniRandevu.setCustomer(customer);
        yeniRandevu.setBarber(kuaför);
        yeniRandevu.setStartTime(startTime);
        yeniRandevu.setEndTime(endTime);
        yeniRandevu.setTotalPrice(toplamFiyat);
        yeniRandevu.setStatus("BEKLEMEDE"); // Varsayılan durum

        Randevu kaydedilenRandevu = randevuRepository.save(yeniRandevu);

        // 6. Randevu-Hizmet İlişkilerini Kaydet (Ara Tablo)
        for (Hizmet hizmet : secilenHizmetler) {
            RandevuHizmet randevuHizmet = new RandevuHizmet();
            randevuHizmet.setRandevu(kaydedilenRandevu);
            randevuHizmet.setHizmet(hizmet);
            randevuHizmetRepository.save(randevuHizmet);
        }

        return kaydedilenRandevu;
    }

    // --- TAKVİM KONTROL MANTIĞI ---

    // Zaman Aralığı Müsait mi? (Basit Çakışma Kontrolü)
    private boolean isTimeSlotAvailable(Barber barber, LocalDateTime startTime, LocalDateTime endTime) {
        // Kuaförün (veya tüm kuaförlerin, ihtiyaca göre) belirli saat aralığında çakışan randevularını getir
        List<Randevu> conflictingAppointments = randevuRepository.findConflictingAppointments(
                barber.getId(), startTime, endTime, "REDDEDİLDİ"
        );

        // Eğer REDDEDİLDİ harici herhangi bir randevu bulunursa (BEKLEMEDE/ONAYLANDI), çakışma var demektir.
        return !conflictingAppointments.isEmpty();
    }

    // --- DİĞER TEMEL CRUD METOTLARI ---

    public List<Randevu> tumRandevulariGetir() {
        // Admin paneli için kullanılacak (detaylı sorgu için kullanılabilir)
        return randevuRepository.findAll();
    }

    // ... (Diğer metotlar: randevuGuncelle, randevuSil)
}