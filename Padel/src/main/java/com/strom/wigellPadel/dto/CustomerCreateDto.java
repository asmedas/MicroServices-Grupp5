package com.strom.wigellPadel.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerCreateDto(
        @NotBlank @Size(max = 50) String firstName,
        @NotBlank @Size(max = 50) String lastName,
        @NotBlank @Size(max = 50) String street,
        @NotBlank @Size(max = 50) String postalCode,
        @NotBlank @Size(max = 50) String city,
        @NotBlank @Email @Size(max = 50) String email,
        @NotBlank @Size(max = 50) String username,
        @NotBlank @Size(max = 100) String password
) {
}
