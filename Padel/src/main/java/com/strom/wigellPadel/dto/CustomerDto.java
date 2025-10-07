package com.strom.wigellPadel.dto;

import com.strom.wigellPadel.entities.Address;

public record CustomerDto(
        Long id,
        String username,
        String firstName,
        String lastName,
        Address address,
        String keycloakUserId

) {
}
