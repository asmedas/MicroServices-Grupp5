package com.strom.wigellPadel.services;

import com.strom.wigellPadel.dto.UserUpdateProfileDto;

public interface KeycloakUserService {

    String createUserAndAssignRole(String username, String email, String rawPassword, String roleName);

    void updateUserProfile(String userId, UserUpdateProfileDto profile);

    void deleteUser(String userId);

}
