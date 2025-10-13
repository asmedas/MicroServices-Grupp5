package com.sebbe.cinema.services;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class KeycloakAdminService {

    private final Keycloak keycloak;
    private final String realm;

    public KeycloakAdminService(Keycloak keycloak, @Value("${keycloak.admin.realm}")String realm) {
        this.keycloak = keycloak;
        this.realm = realm;
    }


    public void setUserRoles(String userId, List<String> newRoles){
        var realmRes = realm();
        var userRes = realmRes.users().get(userId);

        var currentDirect = userRes.roles().realmLevel().listAll();
        if(!currentDirect.isEmpty()){
            userRes.roles().realmLevel().remove(currentDirect);
        }

        var allRoles = realmRes.roles().list();
        List<RoleRepresentation> toAdd = new ArrayList<>(newRoles.size());
        for(String name : newRoles){
            RoleRepresentation rep = allRoles.stream()
                    .filter(r -> r.getName().equalsIgnoreCase(name))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + name));
            toAdd.add(rep);
        }
        userRes.roles().realmLevel().add(toAdd);
    }

    private RealmResource realm(){
        return keycloak.realm(realm);
    }

}

