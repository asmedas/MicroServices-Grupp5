package com.strom.wigellPadel.dto;

public record CustomerWithAccountCreateDto(
        String firstName,
        String lastName,
        String username,
        String password
) {
}
