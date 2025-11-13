package com.dijitalkuaforum.dijitalkuaforum_backend.controller;

import com.dijitalkuaforum.dijitalkuaforum_backend.dto.CustomerLoginRequestDTO;
import com.dijitalkuaforum.dijitalkuaforum_backend.dto.CustomerRegisterRequestDTO;
import com.dijitalkuaforum.dijitalkuaforum_backend.model.Customer;
import com.dijitalkuaforum.dijitalkuaforum_backend.repository.CustomerRepository;
import com.dijitalkuaforum.dijitalkuaforum_backend.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/public/customerAuth")
@RequiredArgsConstructor
public class CustomerAuthController {

    private final CustomerRepository customerRepository;
    private final CustomerService customerService; // E-posta tekilliği kontrolü için

    // --- 1. MÜŞTERİ KAYIT / GÜNCELLEME İŞLEMİ (/register) ---
    @PostMapping("/register")
    public ResponseEntity<?> registerOrUpdateCustomer(@RequestBody CustomerRegisterRequestDTO request) {

        // 1. Telefon Numarası Kontrolü (Ana Anahtar)
        Optional<Customer> existingCustomerOpt = customerRepository.findByPhoneNumber(request.getPhoneNumber());

        if (existingCustomerOpt.isPresent()) {
            // --- KULLANICI 1 SENARYOSU: ADMIN TARAFINDAN EKLENMİŞ KAYDI GÜNCELLE ---
            Customer customer = existingCustomerOpt.get();

            // Sadece şifresi olmayan veya şifresini güncellemek isteyenler için
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return new ResponseEntity<>("Lütfen geçerli bir şifre girin.", HttpStatus.BAD_REQUEST);
            }

            // E-posta tekilliği kontrolü (Eğer e-posta zaten farklı bir kullanıcıda varsa hata ver)
            if (request.getEmail() != null && !request.getEmail().isEmpty() &&
                    customerService.validateEmailIsUnique(request.getEmail()) &&
                    !customer.getEmail().equals(request.getEmail()))
            {
                return new ResponseEntity<>("Bu e-posta başka bir hesapta kayıtlı.", HttpStatus.BAD_REQUEST);
            }

            // Bilgileri güncelle
            customer.setFullName(request.getFullName());
            customer.setEmail(request.getEmail());
            customer.setPassword(request.getPassword()); // Şifre atanıyor/güncelleniyor.

            Customer updatedCustomer = customerRepository.save(customer);
            updatedCustomer.setPassword(null); // Güvenlik
            return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);

        } else {
            // --- KULLANICI 2 SENARYOSU: TAMAMEN YENİ KAYIT ---
            if (request.getEmail() == null || request.getEmail().trim().isEmpty() || customerService.validateEmailIsUnique(request.getEmail())) {
                return new ResponseEntity<>("Geçerli ve benzersiz bir e-posta zorunludur.", HttpStatus.BAD_REQUEST);
            }

            Customer newCustomer = new Customer();
            newCustomer.setFullName(request.getFullName());
            newCustomer.setPhoneNumber(request.getPhoneNumber());
            newCustomer.setEmail(request.getEmail());
            newCustomer.setPassword(request.getPassword());

            Customer savedCustomer = customerRepository.save(newCustomer);
            savedCustomer.setPassword(null);
            return new ResponseEntity<>(savedCustomer, HttpStatus.CREATED);
        }
    }

    // --- 2. MÜŞTERİ GİRİŞ İŞLEMİ (/login) ---
    @PostMapping("/login")
    public ResponseEntity<?> loginCustomer(@RequestBody CustomerLoginRequestDTO loginRequest) {
        // Telefon ve Şifre ile direkt veritabanında ara
        Optional<Customer> customerOpt = customerRepository.findByPhoneNumberAndPassword(
                loginRequest.getPhoneNumber(),
                loginRequest.getPassword()
        );

        if (customerOpt.isEmpty()) {
            return new ResponseEntity<>("Telefon numarası veya şifre hatalı.", HttpStatus.UNAUTHORIZED);
        }

        Customer customer = customerOpt.get();
        customer.setPassword(null); // Şifreyi döndürme
        return ResponseEntity.ok(customer);
    }
}