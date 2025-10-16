package com.zetterlund.wigell_sushi_api.service;

import com.zetterlund.wigell_sushi_api.dto.UpdateUserProfileDto;

public interface KeycloakUserService {
    String createUserForCustomer(String username, String email, String rawPassword);
    void updateUserProfile(String userId, UpdateUserProfileDto profile);
    void deleteUser(String userId);
}
