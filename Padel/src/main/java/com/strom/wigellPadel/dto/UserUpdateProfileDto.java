package com.strom.wigellPadel.dto;

public record UserUpdateProfileDto(
        String email,
        Boolean emailVerified,
        String firstName,
        String lastName
) {
}
