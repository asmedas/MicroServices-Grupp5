package com.strom.wigellPadel.dto;

import com.strom.wigellPadel.entities.Address;

import java.util.Set;

public record CustomerDto(
        Long id,
        String username,
        String firstName,
        String lastName,
        Set<Address> address,
        String keycloakUserId

) {
}
