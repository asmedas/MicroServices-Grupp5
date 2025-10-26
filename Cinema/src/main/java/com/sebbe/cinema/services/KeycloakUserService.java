package com.sebbe.cinema.services;

import com.sebbe.cinema.dtos.customerDtos.UpdateUserProfileDto;

public interface KeycloakUserService {
    String createUserForCustomer(String username, String email, String rawPassword);
    void updateUserProfile(String userId, UpdateUserProfileDto profile);
    void deleteUser(String userId);
}
