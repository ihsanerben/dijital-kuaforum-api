// src/main/java/com/dijitalkuaforum/dijitalkuaforum_backend/service/RandevuServis.java

package com.dijitalkuaforum.dijitalkuaforum_backend.service;

import com.dijitalkuaforum.dijitalkuaforum_backend.model.*;
import com.dijitalkuaforum.dijitalkuaforum_backend.repository.*;
import com.dijitalkuaforum.dijitalkuaforum_backend.exception.AppointmentConflictException;
import com.dijitalkuaforum.dijitalkuaforum_backend.exception.ResourceNotFoundException;
import com.dijitalkuaforum.dijitalkuaforum_backend.dto.ServiceDistributionDTO; // Ensure this import exists
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

    private final RandevuRepository randevuRepository;
    private final RandevuHizmetRepository randevuHizmetRepository;
    private final HizmetRepository hizmetRepository;
    private final BarberRepository barberRepository;
    private final EmailService emailService; // 📧 INJECT EMAIL SERVICE

    // Constants
    public static final String STATUS_ONAYLANDI = "ONAYLANDI";
    public static final String STATUS_REDDEDILDI = "REDDEDILDI";
    public static final String STATUS_BEKLEMEDE = "BEKLEMEDE";
    private static final int SLOT_INTERVAL_MINUTES = 10;
    private static final int IS_BASLANGIC_SAATI = 9;
    private static final int IS_BITIS_SAATI = 18;

    // Helper: Get Single Barber
    private Barber getSingleBarber() {
        return barberRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Barber", "kuaför", 0L));
    }

    // --- 1. CUSTOMER APPOINTMENT CREATION (Müşteri) ---
    @Transactional
    public Randevu randevuOlustur(
            Customer customer,
            LocalDateTime startTime,
            List<Long> hizmetIdleri)
    {
        Barber kuaför = getSingleBarber();

        int toplamSureDakika = 0;
        BigDecimal toplamFiyat = BigDecimal.ZERO;

        List<Hizmet> secilenHizmetler = hizmetIdleri.stream()
                .map(id -> hizmetRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hizmet", "id", id)))
                .collect(Collectors.toList());

        for (Hizmet hizmet : secilenHizmetler) {
            toplamSureDakika += hizmet.getSureDakika();
            toplamFiyat = toplamFiyat.add(hizmet.getFiyat());
        }

        LocalDateTime endTime = startTime.plusMinutes(toplamSureDakika);

        // Check Working Hours
        if (startTime.getHour() < IS_BASLANGIC_SAATI || endTime.getHour() >= IS_BITIS_SAATI || startTime.toLocalDate().isBefore(LocalDateTime.now().toLocalDate())) {
            throw new IllegalArgumentException("Randevu çalışma saatleri veya geçmiş bir tarih dışındadır.");
        }

        // Check Conflicts
        if (isTimeSlotAvailable(kuaför, startTime, endTime)) {
            throw new AppointmentConflictException("Seçilen saat aralığı doludur veya çakışmaktadır.");
        }

        Randevu yeniRandevu = new Randevu();
        yeniRandevu.setCustomer(customer);
        yeniRandevu.setBarber(kuaför);
        yeniRandevu.setStartTime(startTime);
        yeniRandevu.setEndTime(endTime);
        yeniRandevu.setTotalPrice(toplamFiyat);
        yeniRandevu.setStatus(STATUS_BEKLEMEDE); // Default: PENDING

        Randevu kaydedilenRandevu = randevuRepository.save(yeniRandevu);

        for (Hizmet hizmet : secilenHizmetler) {
            RandevuHizmet randevuHizmet = new RandevuHizmet();
            randevuHizmet.setRandevu(kaydedilenRandevu);
            randevuHizmet.setHizmet(hizmet);
            randevuHizmetRepository.save(randevuHizmet);
        }

        // 📧 SEND EMAIL: PENDING
        emailService.sendAppointmentCreated(customer, kaydedilenRandevu);
        emailService.thereIsAAppointment(customer, kaydedilenRandevu);

        return kaydedilenRandevu;
    }

    // --- 2. ADMIN APPOINTMENT CREATION (Directly Approved) ---
    @Transactional
    public Randevu randevuOlusturAdmin(
            Customer customer,
            LocalDateTime startTime,
            List<Long> hizmetIdleri)
    {
        Barber kuaför = getSingleBarber();

        int toplamSureDakika = 0;
        BigDecimal toplamFiyat = BigDecimal.ZERO;

        List<Hizmet> secilenHizmetler = hizmetIdleri.stream()
                .map(id -> hizmetRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hizmet", "id", id)))
                .collect(Collectors.toList());

        for (Hizmet hizmet : secilenHizmetler) {
            toplamSureDakika += hizmet.getSureDakika();
            toplamFiyat = toplamFiyat.add(hizmet.getFiyat());
        }

        LocalDateTime endTime = startTime.plusMinutes(toplamSureDakika);

        if (isTimeSlotAvailable(kuaför, startTime, endTime)) {
            throw new AppointmentConflictException("Seçilen saat aralığı doludur veya çakışmaktadır.");
        }

        Randevu yeniRandevu = new Randevu();
        yeniRandevu.setCustomer(customer);
        yeniRandevu.setBarber(kuaför);
        yeniRandevu.setStartTime(startTime);
        yeniRandevu.setEndTime(endTime);
        yeniRandevu.setTotalPrice(toplamFiyat);
        yeniRandevu.setStatus(STATUS_ONAYLANDI); // Direct Approval

        Randevu kaydedilenRandevu = randevuRepository.save(yeniRandevu);

        for (Hizmet hizmet : secilenHizmetler) {
            RandevuHizmet randevuHizmet = new RandevuHizmet();
            randevuHizmet.setRandevu(kaydedilenRandevu);
            randevuHizmet.setHizmet(hizmet);
            randevuHizmetRepository.save(randevuHizmet);
        }

        // 📧 SEND EMAIL: CONFIRMED
        emailService.sendAppointmentStatusUpdate(customer, kaydedilenRandevu);

        return kaydedilenRandevu;
    }

    // --- 3. STATUS UPDATE (Approve/Reject) ---
    @Transactional
    public Randevu randevuDurumuGuncelle(Long randevuId, String yeniDurum) {
        Randevu randevu = randevuRepository.findById(randevuId)
                .orElseThrow(() -> new ResourceNotFoundException("Randevu", "id", randevuId));

        randevu.setStatus(yeniDurum);
        Randevu updatedRandevu = randevuRepository.save(randevu);

        // 📧 SEND EMAIL: STATUS UPDATE
        emailService.sendAppointmentStatusUpdate(randevu.getCustomer(), updatedRandevu);

        return updatedRandevu;
    }

    // --- UTILITY METHODS ---

    public List<String> getAvailableSlotsAdmin(Long hizmetId, LocalDate date) {
        Hizmet hizmet = hizmetRepository.findById(hizmetId)
                .orElseThrow(() -> new ResourceNotFoundException("Hizmet", "id", hizmetId));
        Barber kuaför = getSingleBarber();

        int requiredDuration = hizmet.getSureDakika();
        List<String> availableSlots = new ArrayList<>();

        List<Randevu> appointments = getRandevularByDate(date, kuaför.getId());

        LocalTime currentTime = LocalTime.of(IS_BASLANGIC_SAATI, 0);
        LocalTime endTimeLimit = LocalTime.of(IS_BITIS_SAATI, 0);

        while (currentTime.isBefore(endTimeLimit)) {
            LocalDateTime slotStartDateTime = date.atTime(currentTime);
            LocalDateTime slotEndDateTime = slotStartDateTime.plusMinutes(requiredDuration);

            if (slotEndDateTime.toLocalTime().isAfter(endTimeLimit)) break;

            boolean isConflicting = false;
            for (Randevu app : appointments) {
                if (app.getStatus().equals(STATUS_ONAYLANDI) || app.getStatus().equals(STATUS_BEKLEMEDE)) {
                    if (slotStartDateTime.isBefore(app.getEndTime()) && slotEndDateTime.isAfter(app.getStartTime())) {
                        isConflicting = true;
                        break;
                    }
                }
            }
            if (!isConflicting) {
                availableSlots.add(currentTime.toString().substring(0, 5));
            }
            currentTime = currentTime.plusMinutes(SLOT_INTERVAL_MINUTES);
        }
        return availableSlots;
    }

    public List<Randevu> getRandevularByDate(LocalDate date, Long barberId) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atStartOfDay().plusDays(1).minusNanos(1);

        if (barberId != null) {
            return randevuRepository.findByStartTimeBetweenAndBarberId(startOfDay, endOfDay, barberId);
        } else {
            return randevuRepository.findByStartTimeBetween(startOfDay, endOfDay);
        }
    }

    public List<Randevu> tumRandevulariGetir() {
        return randevuRepository.findAll();
    }

    public List<Randevu> musteriGecmisRandevulariniGetir(Long customerId) {
        return randevuRepository.findByCustomerIdAndStatusOrderByStartTimeDesc(customerId, STATUS_ONAYLANDI);
    }

    private boolean isTimeSlotAvailable(Barber barber, LocalDateTime startTime, LocalDateTime endTime) {
        List<Randevu> conflictingAppointments = randevuRepository.findConflictingAppointments(
                barber.getId(), startTime, endTime, STATUS_REDDEDILDI
        );
        return !conflictingAppointments.isEmpty();
    }

    // Statistics
    public Map<String, Object> getIstatistikRaporu() {
        Map<String, Object> rapor = new HashMap<>();
        rapor.put("tamamlanmisRandevuSayisi", randevuRepository.countByStatus(STATUS_ONAYLANDI));
        rapor.put("beklemedeRandevuSayisi", randevuRepository.countByStatus(STATUS_BEKLEMEDE));
        BigDecimal toplamGelir = randevuRepository.sumTotalPriceByStatus(STATUS_ONAYLANDI);
        rapor.put("toplamGelir", toplamGelir != null ? toplamGelir : BigDecimal.ZERO);
        return rapor;
    }
}