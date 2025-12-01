package com.dijitalkuaforum.dijitalkuaforum_backend.service;

import com.dijitalkuaforum.dijitalkuaforum_backend.dto.LoginRequestDTO;
import com.dijitalkuaforum.dijitalkuaforum_backend.model.Barber;
import com.dijitalkuaforum.dijitalkuaforum_backend.repository.BarberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor // Lombok: Final alanlar için otomatik constructor oluşturur (BarberRepository'yi inject etmek için)
public class AuthService {

    private final BarberRepository barberRepository;

    public Optional<Barber> login(LoginRequestDTO loginRequest) {
        // 1. Kullanıcı adına göre veritabanında arama yap
        Optional<Barber> optionalBarber = barberRepository.findByUsername(loginRequest.getUsername());

        if (optionalBarber.isPresent()) {
            Barber barber = optionalBarber.get();

            // 2. Şifreyi kontrol et (Düz metin karşılaştırması yapıyoruz)
            // GÜVENLİK NOTU: Gerçek uygulamalarda şifreler her zaman şifreli (hashed) saklanmalıdır!
            if (barber.getPassword().equals(loginRequest.getPassword())) {
                return Optional.of(barber); // Login başarılı
            }
        }

        // Kullanıcı bulunamadı veya şifre yanlış
        return Optional.empty();
    }
}