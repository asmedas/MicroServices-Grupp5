package com.strom.wigellPadel.services;

import com.strom.wigellPadel.dto.UserUpdateProfileDto;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Objects;

@Service
public class KeycloakUserServiceImpl implements KeycloakUserService {

    private final Keycloak keycloak;
    private final String realm;

    public KeycloakUserServiceImpl(Keycloak keycloak, @Value("${spring.keycloak.realm}") String realm) {
        this.keycloak = keycloak;
        this.realm = realm;
    }

    private RealmResource realm() {
        return keycloak.realm(realm);
    }

    private UserResource user(String userId) {
        return realm().users().get(userId);
    }

    @Override
    public String createUserAndAssignRole(String username, String email, String rawPassword, String roleName) {

        var users = realm().users();
        var existingId = users.searchByUsername(username, true).stream()
                .filter(u -> username.equalsIgnoreCase(u.getUsername()))
                .map(UserRepresentation::getId)
                .findFirst()
                .orElse(null);

        if (existingId != null) {
            var rep = users.get(existingId).toRepresentation();
            if (email != null && rep.getEmail() != null &&
                    !email.equalsIgnoreCase(rep.getEmail())) {
                throw new IllegalArgumentException("Användarnamnet finns redan i Keycloak med en annan e-post");
            }
        } else {
            UserRepresentation rep = new UserRepresentation();
            rep.setUsername(username);
            rep.setEmail(email);
            rep.setEnabled(true);

            var resp = users.create(rep);
            int status = resp.getStatus();
            if (status == 409) {
                existingId = users.searchByUsername(username, true).stream()
                        .filter(u -> username.equalsIgnoreCase(u.getUsername()))
                        .map(UserRepresentation::getId)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Användarnamnet används redan i Keycloak"));
            } else if (status >= 300) {
                throw new IllegalStateException("Kunde inte skapa Keycloak-användare. HTTP " + status);
            } else {
                existingId = extractIdFromLocation(resp);
            }
            resp.close();
        }

        var cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(rawPassword);
        cred.setTemporary(false);
        user(existingId).resetPassword(cred);

        var roleRep = realm().roles().list().stream()
                .filter(r -> r.getName().equalsIgnoreCase(roleName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Realm-rollen '" + roleName + "' saknas eller kan inte läsas i realm '" + realm + "'."));

        user(existingId).roles().realmLevel().add(java.util.List.of(roleRep));

        return existingId;
    }

    private static String extractIdFromLocation(Response response) {
        URI location = response.getLocation();
        if (location == null) {
            throw new IllegalStateException("Keycloak-svar saknar Location-header - kan ej läsa ut userId");
        }
        String path = location.getPath();
        int idx = path.lastIndexOf('/');
        return idx >= 0 ? path.substring(idx + 1) : path;
    }

    @Override
    public void updateUserProfile(String userId, UserUpdateProfileDto profile) {
        Objects.requireNonNull(userId, "userId får inte vara null");
        Objects.requireNonNull(profile, "email får inte vara null");

        UserResource ur = user(userId);
        UserRepresentation current = ur.toRepresentation();

        if (profile.email() != null) {
            current.setEmail(profile.email());
        }
        if (profile.emailVerified() != null) {
            current.setEmailVerified(profile.emailVerified());
        }
        if (profile.firstName() != null) {
            current.setFirstName(profile.firstName());
        }
        if (profile.lastName() != null) {
            current.setLastName(profile.lastName());
        }

        ur.update(current);
    }

    @Override
    public void deleteUser(String userId) {
        user(userId).remove();
    }

}
