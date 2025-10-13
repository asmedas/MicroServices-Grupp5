package com.sebbe.cinema.dtos.customerDtos;

import com.sebbe.cinema.dtos.addressDto.CreateAddressDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

public record CreateCustomerWithAccountDto(
        @NotBlank @Size(min = 1, max = 100) String firstName,
        @NotBlank @Size(min = 1, max = 100) String lastName,
        @NotBlank @Positive @Max(120) int age,
        @NotBlank @Size(min = 10, max = 36)String username,
        @NotBlank @Size(min = 10, max = 36) String password,
        @NotBlank @Length(max = 100) @Email String email,
        @NotBlank @Valid CreateAddressDto address
) {
}
