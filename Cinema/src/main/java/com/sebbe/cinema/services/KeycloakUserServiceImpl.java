package com.sebbe.cinema.services;

import com.sebbe.cinema.dtos.customerDtos.UpdateUserProfileDto;
import com.sebbe.cinema.exceptions.AlreadyExistsError;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class KeycloakUserServiceImpl implements KeycloakUserService {

    private final Keycloak keycloak;
    private final String realm;
    private static final String[] USERS_TO_DELETE = {"gunnar", "ingrid", "gabbi"};
    private static final Logger log = LoggerFactory.getLogger(KeycloakUserServiceImpl.class);

    public KeycloakUserServiceImpl(Keycloak keycloak,
                                   @Value("${keycloak.realm}") String realm){
        this.keycloak = keycloak;
        this.realm = realm;
    }

    /**
     * då jag kör create-drop så är såklart keycloaks databas ej påverkad när min applikation stängs ner.
     * därför körs denna vid startup så min User data återställs till endast två användare vid startup;
     * user och admin
     */
    @Override
    public void initializeUsersOnStartup() {
        log.debug("Resetting keycloak user-database to only user and admin");
        var usersResource = realm().users();
        var allUsers = usersResource.list();

        log.debug("Found {} total users in Keycloak", allUsers.size());

        var usersToDelete = allUsers.stream()
                .filter(user -> {
                    String username = user.getUsername();
                    return username != null &&
                            Stream.of(USERS_TO_DELETE)
                                    .anyMatch(protectedUser -> protectedUser.equalsIgnoreCase(username));
                })
                .toList();

        log.debug("Found {} users to delete (excluding protected: {})",
                usersToDelete.size(), String.join(", ", USERS_TO_DELETE));

        for (UserRepresentation user : usersToDelete) {
            String username = user.getUsername();
            String userId = user.getId();

            try {
                log.debug("Deleting user: {} (ID: {})", username, userId);
                usersResource.delete(userId);
                log.info("Successfully deleted user: {}", username);
            } catch (Exception e) {
                log.warn("Failed to delete user {} (ID: {}): {}", username, userId, e.getMessage());
            }
        }

        var remainingUsers = usersResource.list();
        var remainingUsernames = remainingUsers.stream()
                .map(UserRepresentation::getUsername)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        log.info("=== Keycloak initialization complete ===");
        log.info("Remaining users: {}", String.join(", ", remainingUsernames));
    }


    @Override
    public String createUserForCustomer(String username, String email, String rawPassword) {
        log.debug("Trying to create user in keycloak");
        var users = realm().users();

        var existingId = users.searchByUsername(username, false).stream()
                .filter(u -> username.equalsIgnoreCase(u.getUsername()))
                .map(UserRepresentation::getId)
                .findFirst()
                .orElse(null);
        if (existingId != null) {
            log.error("Username already exists");
            throw new AlreadyExistsError("Username already exists: " + username);
        }

        if (email != null && !email.isEmpty()) {
            var usersWithEmail = users.searchByEmail(email, false);
            if (!usersWithEmail.isEmpty()) {
                log.error("Email already exists");
                throw new AlreadyExistsError("Email already exists: " + email);
            }
        }

        log.debug("Creating User rep");
        UserRepresentation rep = new UserRepresentation();
        rep.setUsername(username);
        rep.setEmail(email);
        rep.setEnabled(true);

        log.debug("Creating User");
        var resp = users.create(rep);
        int status = resp.getStatus();
        if (status == 409) {
            existingId = users.searchByUsername(username, true).stream()
                    .filter(u -> username.equalsIgnoreCase(u.getUsername()))
                    .map(UserRepresentation::getId)
                    .findFirst()
                    .orElseThrow(() -> new AlreadyExistsError("Username already in use: " + username));
        } else if (status >= 300) {
            log.error("Failed to create user. HTTP status: " + status);
            throw new IllegalStateException("Failed to create user. HTTP status: " + status);
        } else {
            log.debug("Extracting keycloak userId");
            existingId = extractIdFromLocation(resp);
        }
        resp.close();


        log.debug("Creating CredentialRepresentation for User");
        var cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(rawPassword);
        cred.setTemporary(false);
        user(existingId).resetPassword(cred);

        log.debug("Setting User roles");
        var roleRep = realm().roles().list().stream()
                .filter(r -> r.getName().equalsIgnoreCase("USER"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + "USER"));
        user(existingId).roles().realmLevel().add(List.of(roleRep));
        log.debug("Returning keycloakID");
        return existingId;
    }



    @Override
    public void updateUserProfile(String userId, UpdateUserProfileDto profile) {
        Objects.requireNonNull(userId, "userId får inte vara null");
        Objects.requireNonNull(profile, "profile får inte vara null");

        UserResource user = user(userId);
        UserRepresentation current = user.toRepresentation();

        if(profile.email() != null){
            current.setEmail(profile.email());
        }
        if(profile.emailVerified() != null){
            current.setEmailVerified(profile.emailVerified());
        }
        if(profile.firstName() != null){
            current.setFirstName(profile.firstName());
        }
        if(profile.lastName() != null){
            current.setLastName(profile.lastName());
        }

        user.update(current);
    }

    @Override
    public void deleteUser(String userId) {
        user(userId).remove();
    }

    private UserResource user(String userId){
        return realm().users().get(userId);
    }

    private static String extractIdFromLocation(Response resp){
        URI location = resp.getLocation();
        if(location == null){
            throw new IllegalStateException("Failed to create user. No location header in response.");
        }
        String path = location.getPath();
        int idx = path.lastIndexOf('/');
        return idx >= 0 ? path.substring(idx+1) : path;
    }

    public RealmResource realm(){
        return keycloak.realm(realm);
    }
}
