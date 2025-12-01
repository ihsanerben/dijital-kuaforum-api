// src/main/java/.../repository/RandevuRepository.java - FİNAL KOD

package com.dijitalkuaforum.dijitalkuaforum_backend.repository;

import com.dijitalkuaforum.dijitalkuaforum_backend.model.Randevu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RandevuRepository extends JpaRepository<Randevu, Long> {

    // --- İSTATİSTİK SORGULARI (Aşama 4.3) ---

    // 1. Statüye göre randevuları sayar
    long countByStatus(String status);

    // 2. Belirli bir statüdeki randevuların toplam fiyatını hesaplar
    @Query("SELECT SUM(r.totalPrice) FROM Randevu r WHERE r.status = :status")
    BigDecimal sumTotalPriceByStatus(@Param("status") String status);

    // --- TAKVİM VE ÇAKIŞMA SORGULARI ---

    // 3. Çakışan Randevuları Bulma (REDDEDİLEN hariç)
    @Query("SELECT r FROM Randevu r WHERE r.barber.id = :barberId " +
            "AND r.status IN ('ONAYLANDI', 'BEKLEMEDE') " +
            "AND (" +
            "  (r.startTime < :endTime AND r.endTime > :startTime)" + // Çakışma kontrolü
            ")")
    List<Randevu> findConflictingAppointments(
            @Param("barberId") Long barberId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("excludedStatus") String excludedStatus
    );

    // 4. Tarih Aralığındaki Tüm Randevuları Getir
    List<Randevu> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    // 5. Kuaför ID'sine göre Tarih Aralığındaki Randevuları Getir (Takvim için)
    List<Randevu> findByStartTimeBetweenAndBarberId(LocalDateTime start, LocalDateTime end, Long barberId);

    // 6. Müşteri Detayları: Müşteri ID'sine ve Statüye göre randevuları getir
    List<Randevu> findByCustomerIdAndStatusOrderByStartTimeDesc(Long customerId, String status);

}