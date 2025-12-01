package com.dijitalkuaforum.dijitalkuaforum_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    // Telefon numarası zorunlu kalır, ancak artık UNIQUE DEĞİL (Admin/Kullanıcı kaydı için)
    @Column(nullable = false, unique = true)
    private String phoneNumber;

    // E-posta alanı artık zorunlu DEĞİL (Admin hızlı kaydı için), ancak unique kalması mantıklı
    // Not: Register aşamasında null kontrolü yapacağız.
    @Column(nullable = true, unique = true)
    private String email;

    // YENİ ALAN: Şifre (Müşteri Girişi için zorunlu)
    // Admin eklerken null olabilir, Müşteri kaydolurken doldurulur.
    @Column(nullable = true)
    private String password;

    
}