package com.miriam.travel.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CustomerUpdateRequest {
    @NotBlank @Size(max=64)
    public String username;

    @NotBlank @Size(max=128)
    public String fullName;

    @NotBlank @Email @Size(max=128)
    public String email;
}