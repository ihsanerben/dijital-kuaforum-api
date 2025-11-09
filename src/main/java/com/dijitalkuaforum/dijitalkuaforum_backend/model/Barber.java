package com.dijitalkuaforum.dijitalkuaforum_backend.model;

import jakarta.persistence.*;
import lombok.*;

// Lombok notasyonları ile getter, setter, yapıcı metotlar otomatik üretilir.
@Entity
@Data // Getter, Setter, toString, equals, hashCode sağlar
@NoArgsConstructor // Parametresiz yapıcı metot
@AllArgsConstructor // Tüm alanları içeren yapıcı metot
@Table(name = "barbers") // Veritabanındaki tablo adı
public class Barber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Kuaför sahibinin benzersiz ID'si

    @Column(nullable = false, unique = true)
    private String username; // Giriş için kullanılacak kullanıcı adı (Kuaför adı olabilir)

    @Column(nullable = false)
    private String password; // Hashlenmiş şifre

}