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

@Service
public class KeycloakUserServiceImpl implements KeycloakUserService {

    private final Keycloak keycloak;
    private final String realm;
    private static final Logger log = LoggerFactory.getLogger(KeycloakUserServiceImpl.class);

    public KeycloakUserServiceImpl(Keycloak keycloak,
                                   @Value("${keycloak.realm}") String realm){
        this.keycloak = keycloak;
        this.realm = realm;
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
        if(existingId != null){
            var rep = users.get(existingId).toRepresentation();
            if(email != null && !email.isEmpty() && !email.equalsIgnoreCase(rep.getEmail())){
                log.error("Email already exists");
                throw new AlreadyExistsError("Email already exists: " + email);
            }
        } else {
            log.debug("Creating User rep");
            UserRepresentation rep = new UserRepresentation();
            rep.setUsername(username);
            rep.setEmail(email);
            rep.setEnabled(true);

            log.debug("Creating User");
            var resp = users.create(rep);
            int status = resp.getStatus();
            if(status == 409){
                existingId = users.searchByUsername(username, true).stream()
                        .filter(u -> username.equalsIgnoreCase(u.getUsername()))
                        .map(UserRepresentation::getId)
                        .findFirst()
                        .orElseThrow(() -> new AlreadyExistsError("Username already in use: " + username));
            } else if(status >= 300){
                throw new IllegalStateException("Failed to create user. HTTP status: " + status);
            } else {
                log.debug("Extracting keycloak userId");
                existingId = extractIdFromLocation(resp);
            }
            resp.close();
        }

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
