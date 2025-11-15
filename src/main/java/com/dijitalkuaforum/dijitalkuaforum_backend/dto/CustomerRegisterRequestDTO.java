package com.dijitalkuaforum.dijitalkuaforum_backend.dto;

import lombok.Data;

@Data
public class CustomerRegisterRequestDTO {
    private String fullName;
    private String phoneNumber;
    private String email;
    private String password;
}