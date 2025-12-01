
package com.dijitalkuaforum.dijitalkuaforum_backend.dto;

import lombok.Data;

@Data
public class CustomerLoginRequestDTO {
    private String phoneNumber;
    private String password;
}