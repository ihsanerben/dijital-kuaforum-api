// src/main/java/com/dijitalkuaforum/dijitalkuaforum_backend/repository/RandevuRepository.java (GÜNCELLENDİ)

package com.dijitalkuaforum.dijitalkuaforum_backend.repository;

import com.dijitalkuaforum.dijitalkuaforum_backend.model.Randevu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RandevuRepository extends JpaRepository<Randevu, Long> {

    // YENİ SORGULAR: Çakışan Randevuları Bulma
    @Query("SELECT r FROM Randevu r WHERE r.barber.id = :barberId " +
            "AND r.status <> :excludedStatus " + // REDDEDİLEN randevuları sayma
            "AND (" +
            // Mevcut randevu yeni randevunun başlangıcında bitiyor veya yeni randevu mevcut randevunun bitişinde başlıyor
            "  (r.startTime < :endTime AND r.endTime > :startTime)" +
            ")")
    List<Randevu> findConflictingAppointments(
            @Param("barberId") Long barberId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("excludedStatus") String excludedStatus
    );

    List<Randevu> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    // 2. Kuaför ID'sine göre tarih aralığı sorgusu
// Bu metot isminin Spring Data JPA tarafından doğru yorumlandığından emin olunmalıdır.
    List<Randevu> findByStartTimeBetweenAndBarberId(LocalDateTime start, LocalDateTime end, Long barberId);
}