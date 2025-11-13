// src/main/java/.../service/CustomerService.java (GÜNCELLENMİŞ)

package com.dijitalkuaforum.dijitalkuaforum_backend.service;

import com.dijitalkuaforum.dijitalkuaforum_backend.exception.ResourceNotFoundException;
import com.dijitalkuaforum.dijitalkuaforum_backend.model.Customer;
import com.dijitalkuaforum.dijitalkuaforum_backend.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    // YENİ METOT: Telefon numarasına göre müşteri bulma (Auth Controller için)
    public Optional<Customer> findByPhoneNumber(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber);
    }

    public Customer updateCustomer(Long id, Customer customerDetails) {
        // 1. Müşterinin var olup olmadığını kontrol et
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: ", "id", id));

        // 2. Müşteri nesnesindeki alanları yeni detaylarla güncelle
        customer.setFullName(customerDetails.getFullName());
        customer.setEmail(customerDetails.getEmail());
        customer.setPhoneNumber(customerDetails.getPhoneNumber());

        // GÜVENLİK KONTROLÜ: Admin, müşteriyi güncellerken şifre alanını boş gönderebilir.
        // Bu durumda mevcut şifresini KORUMALIYIZ.
        // Eğer customerDetails objesi bir şifre içeriyorsa (null/boş değilse), o zaman güncelleme yapılır.
        if (customerDetails.getPassword() != null && !customerDetails.getPassword().isEmpty()) {
            customer.setPassword(customerDetails.getPassword());
        }

        // 3. Güncellenmiş nesneyi veritabanına kaydet (SAVE metodu hem insert hem update yapar)
        final Customer updatedCustomer = customerRepository.save(customer);
        return updatedCustomer;
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public void deleteById(Long id) {
        customerRepository.deleteById(id);
    }

    public boolean validateEmailIsUnique(String email) {
        return customerRepository.existsByEmail(email);
    }
}