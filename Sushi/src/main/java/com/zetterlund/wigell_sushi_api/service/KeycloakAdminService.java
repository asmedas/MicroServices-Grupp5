package com.zetterlund.wigell_sushi_api.service;

import com.zetterlund.wigell_sushi_api.exception.AlreadyExistsError;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class KeycloakAdminService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakAdminService.class);
    private final Keycloak keycloak;
    private final String realm;

    public KeycloakAdminService(Keycloak keycloak, @Value("${keycloak.admin.realm}") String realm) {
        this.keycloak = keycloak;
        this.realm = realm;
    }

    // Används för att skapa en ny användare i Keycloak
    public void createUser(String username, String email, String firstName, String lastName, String password) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);

        var users = realm().users();
        var existingId = users.searchByUsername(username, false).stream()
                .filter(u -> username.equalsIgnoreCase(u.getUsername()))
                .map(UserRepresentation::getId)
                .findFirst()
                .orElse(null);
        if (existingId != null) {
            logger.error("Username already exists");
            throw new AlreadyExistsError("Username already exists: " + username);
        }

        if (email != null && !email.isEmpty()) {
            var usersWithEmail = users.searchByEmail(email, false);
            if (!usersWithEmail.isEmpty()) {
                logger.error("Email already exists");
                throw new AlreadyExistsError("Email already exists: " + email);
            }
        }
        // Skapa användaren
        var response = realm().users().create(user);
        if (response.getStatus() != 201) {
            throw new RuntimeException("Failed to create user. Status: " + response.getStatus());
        }

        // Hämta skapad användare och sätter lösenord
        String userId = realm().users().search(username).getFirst().getId();
        setPassword(userId, password);
    }

    // Sätt lösenord för en användare
    public void setPassword(String userId, String password) {
        var userResource = realm().users().get(userId);
        var credential = new org.keycloak.representations.idm.CredentialRepresentation();

        credential.setType(org.keycloak.representations.idm.CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        userResource.resetPassword(credential);
    }

    // Tilldela roller till en användare i Keycloak
    public void setUserRoles(String userId, List<String> newRoles) {
        var realmRes = realm();
        var userRes = realmRes.users().get(userId);

        // Ta bort nuvarande roller
        var currentRoles = userRes.roles().realmLevel().listAll();
        if (!currentRoles.isEmpty()) {
            userRes.roles().realmLevel().remove(currentRoles);
        }

        // Lägg till nya roller
        var allRoles = realmRes.roles().list();
        List<RoleRepresentation> toAdd = new ArrayList<>(newRoles.size());
        for (String name : newRoles) {
            RoleRepresentation rep = allRoles.stream()
                    .filter(r -> r.getName().equalsIgnoreCase(name))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + name));
            toAdd.add(rep);
        }
        userRes.roles().realmLevel().add(toAdd);
    }

    // Hämta användarresurs för det aktuella realm
    protected RealmResource realm() {
        return keycloak.realm(realm);
    }
}
