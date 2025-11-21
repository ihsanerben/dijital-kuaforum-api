package com.dijitalkuaforum.dijitalkuaforum_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceDistributionDTO {
    private String name;  // Service Name (e.g., "Haircut")
    private Long count;   // How many times this service was booked
}