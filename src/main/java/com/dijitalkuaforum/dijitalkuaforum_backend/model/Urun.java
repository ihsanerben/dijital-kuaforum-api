// src/main/java/.../model/Urun.java

package com.dijitalkuaforum.dijitalkuaforum_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "urunler")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Urun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ad; // Ürün Adı (Örn: Şampuan, Saç Kremi)

    @Column(nullable = false)
    private BigDecimal fiyat; // Satış Fiyatı

    @Column(nullable = false)
    private Integer stokAdedi; // Mevcut Stok Miktarı

    @Column(nullable = true)
    private String tedarikci; // Tedarikçi Firma
}