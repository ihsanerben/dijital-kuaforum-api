package com.dijitalkuaforum.dijitalkuaforum_backend.service;

import com.dijitalkuaforum.dijitalkuaforum_backend.exception.DuplicateValueException;
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

    public Customer updateCustomer(Long id, Customer customerDetails) {
        // 1. Müşterinin var olup olmadığını kontrol et
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: ", "id", id));

        // 2. Müşteri nesnesindeki alanları yeni detaylarla güncelle
        customer.setName(customerDetails.getName());
        customer.setLastName(customerDetails.getLastName());
        customer.setEmail(customerDetails.getEmail());
        customer.setPhoneNumber(customerDetails.getPhoneNumber());

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