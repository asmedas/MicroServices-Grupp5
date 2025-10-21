package com.zetterlund.wigell_sushi_api.service;

import com.zetterlund.wigell_sushi_api.dto.UpdateUserProfileDto;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KeycloakUserServiceImpl implements KeycloakUserService {

    private final KeycloakAdminService keycloakAdminService;

    public KeycloakUserServiceImpl(KeycloakAdminService keycloakAdminService) {
        this.keycloakAdminService = keycloakAdminService;
    }

    @Override
    public String createUserForCustomer(String username, String email, String rawPassword) {
        // Använd befintlig createUser från KeycloakAdminService
        keycloakAdminService.createUser(username, email, null, null, rawPassword);

        // Hämta och returnera användar-ID
        return keycloakAdminService.realm()
                .users()
                .search(username)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User creation failed in Keycloak"))
                .getId();
    }

    @Override
    public void updateUserProfile(String userId, UpdateUserProfileDto profile) {
        var user = keycloakAdminService.realm().users().get(userId);

        // Hämta nuvarande användarprofil
        UserRepresentation userRepresentation = user.toRepresentation();

        // Uppdatera relevant profilinformation
        Optional.ofNullable(profile.getEmail()).ifPresent(userRepresentation::setEmail);
        Optional.ofNullable(profile.getFirstName()).ifPresent(userRepresentation::setFirstName);
        Optional.ofNullable(profile.getLastName()).ifPresent(userRepresentation::setLastName);

        // Utför uppdateringen i Keycloak
        user.update(userRepresentation);
    }

    @Override
    public void deleteUser(String userId) {
        keycloakAdminService.realm()
                .users()
                .delete(userId);
    }
}
