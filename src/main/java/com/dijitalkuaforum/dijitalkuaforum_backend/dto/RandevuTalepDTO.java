// src/main/java/com/dijitalkuaforum/dijitalkuaforum_backend/dto/RandevuTalepDTO.java

package com.dijitalkuaforum.dijitalkuaforum_backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RandevuTalepDTO {

    // Randevuyu alan müşterinin ID'si (Örn: Giriş yapmış müşterinin ID'si)
    private Long customerId;

    // Randevu başlangıç zamanı (yyyy-MM-ddTHH:mm:ss formatında gelmeli)
    private LocalDateTime startTime;

    // Seçilen hizmetlerin ID'leri
    private List<Long> hizmetIdleri;
}