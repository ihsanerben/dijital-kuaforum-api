package com.dijitalkuaforum.dijitalkuaforum_backend.dto;

import com.dijitalkuaforum.dijitalkuaforum_backend.model.Customer;
import lombok.Data;

@Data
public class SecuredCustomerRequestDTO {
    // Güvenlik bilgileri (login kontrolü için)
    private String username;
    private String password;

    // İşlem yapılacak müşteri bilgileri
    private Customer customer;
}