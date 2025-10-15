package com.andreas.wigellmcrental.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerDto(
        Long id,
        @NotBlank @Size(max = 50) String username,
        @NotBlank @Size(max = 100) String name,
        @Email String email,
        String phone
) {}
