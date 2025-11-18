// src/main/java/com/dijitalkuaforum/dijitalkuaforum_backend/service/RandevuServis.java

package com.dijitalkuaforum.dijitalkuaforum_backend.service;

import com.dijitalkuaforum.dijitalkuaforum_backend.exception.ResourceNotFoundException;
import com.dijitalkuaforum.dijitalkuaforum_backend.model.*;
import com.dijitalkuaforum.dijitalkuaforum_backend.repository.*;
import com.dijitalkuaforum.dijitalkuaforum_backend.exception.AppointmentConflictException; // İleride oluşturacağız
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RandevuServis {

    private static final String STATUS_BEKLEMEDE = "BEKLEMEDEKANKSS";
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
    private static final int SLOT_INTERVAL_MINUTES = 10; // 10 dakikalık bloklar

    public static final String STATUS_ONAYLANDI = "ONAYLANDI";

    public List<Randevu> musteriGecmisRandevulariniGetir(Long customerId) {
        // Sadece onaylanmış ve tamamlanmış randevuları gösteriyoruz
        return randevuRepository.findByCustomerIdAndStatusOrderByStartTimeDesc(customerId, STATUS_ONAYLANDI);
    }

    // --- YENİ METOT: ADMİN HIZLI RANDEVU OLUŞTURMA (ONAYLANDI) ---

    // YENİ METOT: Admin için uygun saatleri hesaplar
    public List<String> getAvailableSlotsAdmin(Long hizmetId, LocalDate date) {
        Hizmet hizmet = hizmetRepository.findById(hizmetId)
                .orElseThrow(() -> new ResourceNotFoundException("Hizmet", "id", hizmetId));

        int requiredDuration = hizmet.getSureDakika();
        List<String> availableSlots = new ArrayList<>();

        // O günkü tüm aktif randevuları çek
        List<Randevu> appointments = getRandevularByDate(date, null); // Kuaför ID'si şimdilik null

        LocalTime currentTime = LocalTime.of(IS_BASLANGIC_SAATI, 0);
        LocalTime endTimeLimit = LocalTime.of(IS_BITIS_SAATI, 0);

        while (currentTime.isBefore(endTimeLimit)) {

            LocalDateTime slotStartDateTime = date.atTime(currentTime);
            LocalDateTime slotEndDateTime = slotStartDateTime.plusMinutes(requiredDuration);

            // Randevu bitiş saati, çalışma bitişini geçiyorsa, bu slotu kontrol etme
            if (slotEndDateTime.toLocalTime().isAfter(endTimeLimit)) {
                break;
            }

            boolean isConflicting = false;

            // Çakışma kontrolü
            for (Randevu app : appointments) {
                // Sadece onaylanmış ve bekleyen randevularla çakışmayı kontrol et
                if (app.getStatus().equals(STATUS_ONAYLANDI) || app.getStatus().equals(STATUS_BEKLEMEDE)) {

                    // Çakışma Mantığı: Yeni slot başlangıcı, mevcut randevu bitişinden önce VE yeni slot bitişi, mevcut randevu başlangıcından sonra
                    if (slotStartDateTime.isBefore(app.getEndTime()) && slotEndDateTime.isAfter(app.getStartTime())) {
                        isConflicting = true;
                        break;
                    }
                }
            }

            if (!isConflicting) {
                availableSlots.add(currentTime.toString().substring(0, 5)); // HH:MM formatında ekle
            }

            // Sonraki 10 dakikalık bloğa geç
            currentTime = currentTime.plusMinutes(SLOT_INTERVAL_MINUTES);
        }

        return availableSlots;
    }


    // --- YENİ METOT: İSTATİSTİK RAPORLARI ---
    public Map<String, Object> getIstatistikRaporu() {
        Map<String, Object> rapor = new HashMap<>();

        // 1. Tamamlanmış (ONAYLANDI) randevuları hesapla
        long tamamlanmisSayisi = randevuRepository.countByStatus(STATUS_ONAYLANDI);
        rapor.put("tamamlanmisRandevuSayisi", tamamlanmisSayisi);

        // 2. Beklemedeki randevuları say
        long beklemedeSayisi = randevuRepository.countByStatus(STATUS_BEKLEMEDE);
        rapor.put("beklemedeRandevuSayisi", beklemedeSayisi);

        // 3. Toplam Gelir (Sadece ONAYLANDI olanlar)
        BigDecimal toplamGelir = randevuRepository.sumTotalPriceByStatus(STATUS_ONAYLANDI);
        rapor.put("toplamGelir", toplamGelir != null ? toplamGelir : BigDecimal.ZERO);

        // NOT: Eğer bu metot içindeki repository metotları tanımlı değilse, uygulamanız başlamayabilir.
        // countByStatus ve sumTotalPriceByStatus metotlarının RandevuRepository'de tanımlı olduğundan emin olun.

        return rapor;
    }

    // YENİ METOT: Belirli bir tarih için randevuları çekme
    public List<Randevu> getRandevularByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        // RandevuRepository'de randevuları başlangıç zamanına göre çekme metodu tanımlanmalı
        return randevuRepository.findByStartTimeBetween(startOfDay, endOfDay);

        // NOT: Gerçek projede, sadece "ONAYLANDI" veya "BEKLEYEN" randevular çekilmelidir.
    }

    @Transactional
    public Randevu randevuDurumuGuncelle(Long randevuId, String yeniDurum) {
        // 1. Randevuyu bul
        Randevu randevu = randevuRepository.findById(randevuId)
                .orElseThrow(() -> new ResourceNotFoundException("Randevu", "id", randevuId));

        // 2. Durum geçerliliğini kontrol et (Basit kontrol)
        if (!yeniDurum.equals("ONAYLANDI") && !yeniDurum.equals("REDDEDİLDİ")) {
            throw new IllegalArgumentException("Geçersiz randevu durumu. ONAYLANDI veya REDDEDİLDİ olmalıdır.");
        }

        // 3. Durumu güncelle
        randevu.setStatus(yeniDurum);

        // 4. Kaydet ve döndür
        return randevuRepository.save(randevu);
        // NOT: Onaylandığında e-posta/SMS gönderimi Aşama 4'te yapılabilir.
    }

    public List<Randevu> getRandevularByDate(LocalDate date, Long barberId) {
        // Başlangıç ve bitiş saatlerini hesapla
        LocalDateTime startOfDay = date.atStartOfDay();
        // Gün sonu için 23:59:59.999999999'u temsil eden bir sonraki günün başlangıcı
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        if (barberId != null) {
            // Kuaför ID'si belirtilmişse
            // NOT: findByStartTimeBetweenAndBarberId metodu doğru çalışmayabilir,
            // bu yüzden sadece temel zaman aralığı sorgusunu kullanalım ve filtrelemeyi serviste yapalım.

            // Eğer Repository metodu doğru çalışıyorsa:
            return randevuRepository.findByStartTimeBetweenAndBarberId(startOfDay, endOfDay, barberId);
        } else {
            // Kuaför ID'si belirtilmemişse (Genel Takvim)
            return randevuRepository.findByStartTimeBetween(startOfDay, endOfDay);
        }
    }

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