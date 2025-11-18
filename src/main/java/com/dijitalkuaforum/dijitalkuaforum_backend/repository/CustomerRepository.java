package com.dijitalkuaforum.dijitalkuaforum_backend.repository;

import com.dijitalkuaforum.dijitalkuaforum_backend.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);

    Optional<Customer> findByPhoneNumber(String phoneNumber);

    // YENİ METOT: Giriş yapacak müşteriyi telefon ve şifre ile bulma (Basit Auth için)
    Optional<Customer> findByPhoneNumberAndPassword(String phoneNumber, String password);

    List<Customer> findByFullNameContainingIgnoreCase(String query);
}