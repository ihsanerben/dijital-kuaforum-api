// src/main/java/.../repository/RandevuRepository.java - FİNAL KOD

package com.dijitalkuaforum.dijitalkuaforum_backend.repository;

import com.dijitalkuaforum.dijitalkuaforum_backend.dto.ServiceDistributionDTO;
import com.dijitalkuaforum.dijitalkuaforum_backend.model.Randevu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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


    // 1. Müşterinin ONAYLANDI statüsündeki randevularını say
    long countByCustomerIdAndStatus(Long customerId, String status);

    // 2. Müşterinin toplam harcamasını hesapla
    @Query("SELECT SUM(r.totalPrice) FROM Randevu r WHERE r.customer.id = :customerId AND r.status = :status")
    BigDecimal sumTotalPriceByCustomerAndStatus(Long customerId, String status);

    // 3. Müşterinin son randevusunu bul
    Optional<Randevu> findTopByCustomerIdAndStatusOrderByStartTimeDesc(Long customerId, String status);

    // 4. Müşterinin en çok aldığı hizmeti bul (Kompleks sorgu)
    @Query(value = "SELECT h.ad FROM randevu_hizmetleri rh " +
            "JOIN randevular r ON rh.randevu_id = r.id " +
            "JOIN hizmetler h ON rh.hizmet_id = h.id " +
            "WHERE r.customer_id = :customerId AND r.status = 'ONAYLANDI' " +
            "GROUP BY h.ad " +
            "ORDER BY COUNT(*) DESC LIMIT 1", nativeQuery = true)
    String findFavoriteServiceByCustomer(Long customerId);

    // 5. Tarih Aralığına Göre Ciro
    @Query("SELECT SUM(r.totalPrice) FROM Randevu r WHERE r.status = 'ONAYLANDI' AND r.startTime BETWEEN :start AND :end")
    BigDecimal calculateTotalRevenue(LocalDateTime start, LocalDateTime end);

    // 6. Tarih Aralığına Göre Randevu Sayısı
    @Query("SELECT COUNT(r) FROM Randevu r WHERE r.status = :status AND r.startTime BETWEEN :start AND :end")
    long countByStatusAndDateRange(String status, LocalDateTime start, LocalDateTime end);

    @Query("SELECT new com.dijitalkuaforum.dijitalkuaforum_backend.dto.ServiceDistributionDTO(rh.hizmet.ad, COUNT(rh.hizmet.ad)) " +
            "FROM RandevuHizmet rh JOIN rh.randevu r " +
            "WHERE r.status = :status AND r.startTime BETWEEN :start AND :end " +
            "GROUP BY rh.hizmet.ad")
    List<ServiceDistributionDTO> getServiceDistribution(String status, LocalDateTime start, LocalDateTime end);
}