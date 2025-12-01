package com.dijitalkuaforum.dijitalkuaforum_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralStatsDTO {
    private BigDecimal totalRevenue;        // Toplam Ciro
    private Long completedAppointments;     // Tamamlanan Randevular
    private Long pendingAppointments;       // Bekleyen Randevular
    private Long totalCustomers;            // Toplam Müşteri Sayısı
    private List<ServiceDistributionDTO> serviceDistribution; // Hizmet Dağılımı (Pasta Grafik için)
}