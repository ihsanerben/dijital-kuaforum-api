package com.dijitalkuaforum.dijitalkuaforum_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime; // Başlangıç ve bitiş saatleri için
import java.math.BigDecimal; // Toplam fiyat için

@Entity
@Table(name = "randevular")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Randevu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Müşteri ilişkisi (Randevuyu kimin aldığı)
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // Kuaför ilişkisi (Hangi kuaförün randevusu, tek kuaför varsa Barber ID kullanılmayabilir, ama sistemde tutalım)
    @ManyToOne
    @JoinColumn(name = "barber_id", nullable = true)
    private Barber barber; // Opsiyonel olabilir

    @Column(nullable = false)
    private LocalDateTime startTime; // Randevu başlangıç zamanı (5 dakikalık dilim)

    @Column(nullable = false)
    private LocalDateTime endTime;   // Randevu bitiş zamanı (Süreye göre otomatik hesaplanır)

    @Column(nullable = false)
    private String status; // Örn: BEKLEMEDE, ONAYLANDI, REDDEDİLDİ

    @Column(nullable = true)
    private BigDecimal totalPrice; // Toplam hizmet fiyatı (hesaplanıp kaydedilir)
}