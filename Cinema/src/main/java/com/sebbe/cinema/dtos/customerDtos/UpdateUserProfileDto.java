package com.sebbe.cinema.dtos.customerDtos;

public record UpdateUserProfileDto(
        String email,
        Boolean emailVerified,
        String firstName,
        String lastName
) {
}
