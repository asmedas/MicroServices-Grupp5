package com.miriam.travel.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CustomerCreateRequest {
    @NotBlank @Size(max=36)
    public String id;

    @NotBlank @Size(max=64)
    public String username;

    @NotBlank @Size(max=128)
    public String fullName;

    @NotBlank @Email @Size(max=128)
    public String email;
    public String role;
}
