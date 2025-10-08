package com.strom.wigellPadel.dto;

import com.strom.wigellPadel.entities.Address;

public record CustomerUpdateDto(
        String firstName,
        String lastName,
        Address address
) {
}
