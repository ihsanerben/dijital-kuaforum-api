package com.dijitalkuaforum.dijitalkuaforum_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal; // Fiyat için

@Entity
@Table(name = "hizmetler")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hizmet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Hizmetin adı (Örn: Saç Kesimi)
    @Column(nullable = false, unique = true)
    private String ad;

    // Hizmetin tahmini süresi (Dakika cinsinden)
    @Column(nullable = false)
    private Integer sureDakika;

    // Hizmetin ücreti
    @Column(nullable = false)
    private BigDecimal fiyat;
}