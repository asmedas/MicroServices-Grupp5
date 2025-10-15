package com.andreas.wigellmcrental.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressDto(
        Long id,
        @NotBlank @Size(max = 100) String street,
        @NotBlank @Size(max = 50) String city,
        @NotBlank @Size(max = 50) String country,
        @NotBlank @Size(max = 10) String postalCode
) {}
