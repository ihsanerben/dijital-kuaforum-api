package com.dijitalkuaforum.dijitalkuaforum_backend.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "barbers")
public class Barber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Kuaför sahibinin benzersiz ID'si

    @Column(nullable = false, unique = true)
    private String username; // Giriş için kullanılacak kullanıcı adı (Kuaför adı olabilir)

    @Column(nullable = false)
    private String password; // Hashlenmiş şifre

}