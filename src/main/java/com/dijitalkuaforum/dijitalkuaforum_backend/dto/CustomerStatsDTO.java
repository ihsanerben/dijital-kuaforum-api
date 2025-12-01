package com.dijitalkuaforum.dijitalkuaforum_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerStatsDTO {
    private Long customerId;
    private String fullName;
    private Long totalAppointments;      // Toplam Randevu Sayısı
    private BigDecimal totalSpent;       // Toplam Harcama
    private LocalDateTime lastVisitDate; // Son Ziyaret
    private String favoriteService;      // En çok aldığı hizmet
}