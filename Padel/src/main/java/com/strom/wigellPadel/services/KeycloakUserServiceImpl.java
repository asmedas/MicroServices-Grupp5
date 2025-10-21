package com.strom.wigellPadel.services;

import com.strom.wigellPadel.dto.UserUpdateProfileDto;
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
import java.util.Objects;

@Service
public class KeycloakUserServiceImpl implements KeycloakUserService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserServiceImpl.class);
    private final Keycloak keycloak;
    private final String realm;

    public KeycloakUserServiceImpl(Keycloak keycloak, @Value("${spring.keycloak.realm}") String realm) {
        this.keycloak = keycloak;
        this.realm = realm;
        logger.debug("KeycloakUserServiceImpl initialized med realm: {}", realm);
    }

    private RealmResource realm() {
        return keycloak.realm(realm);
    }

    private UserResource user(String userId) {
        return realm().users().get(userId);
    }

    @Override
    public String createUserAndAssignRole(String username, String email, String rawPassword, String roleName) {
        logger.info("Skapar Keycloak user med username: {}", username);
        try {
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
                    logger.error("Username {} finns redan i Keycloak med en annan e-post", username);
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
                            .orElseThrow(() -> {
                                logger.error("Username {} används redan i Keycloak", username);
                                return new IllegalArgumentException("Användarnamnet används redan i Keycloak");
                            });
                } else if (status >= 300) {
                    logger.error("Kunde inte skapa Keycloak-användare, HTTP status: {}", status);
                    throw new IllegalStateException("Kunde inte skapa Keycloak-användare. HTTP " + status);
                } else {
                    existingId = extractIdFromLocation(resp);
                    logger.debug("Skapade Keycloak user med id: {}", existingId);
                }
                resp.close();
            }

            var cred = new CredentialRepresentation();
            cred.setType(CredentialRepresentation.PASSWORD);
            cred.setValue(rawPassword);
            cred.setTemporary(false);
            user(existingId).resetPassword(cred);
            logger.debug("Skapar password för Keycloak user med id: {}", existingId);

            var roleRep = realm().roles().list().stream()
                    .filter(r -> r.getName().equalsIgnoreCase(roleName))
                    .findFirst()
                    .orElseThrow(() -> {
                        logger.error("Realm-rollen {} saknas eller kan inte läsas i realm {}", roleName, realm);
                        return new IllegalArgumentException("Realm-rollen '" + roleName + "' saknas eller kan inte läsas i realm '" + realm + "'.");
                    });

            user(existingId).roles().realmLevel().add(java.util.List.of(roleRep));
            logger.info("Lyckades skapa och tilldela roll {} till Keycloak user med id: {}", roleName, existingId);
            return existingId;
        } catch (Exception e) {
            logger.error("Error vid skapande av Keycloak user med username: {}", username, e);
            throw e;
        }
    }

    private static String extractIdFromLocation(Response response) {
        logger.debug("Extracting user id från Keycloak-svar location");
        try {
            URI location = response.getLocation();
            if (location == null) {
                logger.error("Keycloak-svar saknar Location header");
                throw new IllegalStateException("Keycloak-svar saknar Location-header - kan ej läsa ut userId");
            }
            String path = location.getPath();
            int idx = path.lastIndexOf('/');
            String userId = idx >= 0 ? path.substring(idx + 1) : path;
            logger.debug("Extracted user id: {}", userId);
            return userId;
        } catch (Exception e) {
            logger.error("Error extracting user id från Keycloak-svar", e);
            throw e;
        }
    }

    @Override
    public void updateUserProfile(String userId, UserUpdateProfileDto profile) {
        logger.info("Uppdaterar Keycloak user profile med id: {}", userId);
        try {
            Objects.requireNonNull(userId, "userId får inte vara null");
            Objects.requireNonNull(profile, "profile får inte vara null");

            UserResource ur = user(userId);
            UserRepresentation current = ur.toRepresentation();

            if (profile.email() != null) {
                current.setEmail(profile.email());
                logger.debug("Uppdaterade email för Keycloak user med id: {}", userId);
            }
            if (profile.emailVerified() != null) {
                current.setEmailVerified(profile.emailVerified());
                logger.debug("Uppdaterade emailVerified för Keycloak user med id: {}", userId);
            }
            if (profile.firstName() != null) {
                current.setFirstName(profile.firstName());
                logger.debug("Uppdaterade firstName för Keycloak user med id: {}", userId);
            }
            if (profile.lastName() != null) {
                current.setLastName(profile.lastName());
                logger.debug("Uppdaterade lastName för Keycloak user med id: {}", userId);
            }

            ur.update(current);
            logger.info("Lyckades uppdatera Keycloak user profile med id: {}", userId);
        } catch (Exception e) {
            logger.error("Error vid uppdatering av Keycloak user profile med id: {}", userId, e);
            throw e;
        }
    }

    @Override
    public void deleteUser(String userId) {
        logger.info("Tar bort Keycloak user med id: {}", userId);
        try {
            user(userId).remove();
            logger.info("Lyckades ta bort Keycloak user med id: {}", userId);
        } catch (Exception e) {
            logger.error("Error vid borttag av Keycloak user med id: {}", userId, e);
            throw e;
        }
    }
}