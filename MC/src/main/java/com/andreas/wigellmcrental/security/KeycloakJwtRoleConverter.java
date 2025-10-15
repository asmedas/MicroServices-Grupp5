package com.andreas.wigellmcrental.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


// Den här klassen omvandlar rollerna från Keycloak (i JWT-tokenen)
// till ett format som Spring Security förstår ("ROLE_USER", "ROLE_ADMIN", osv).

@Component
public class KeycloakJwtRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");

        if (realmAccess == null || realmAccess.isEmpty()) {
            return Collections.emptySet();
        }

        Collection<String> roles = (Collection<String>) realmAccess.get("roles");

        if (roles == null) {
            return Collections.emptySet();
        }

        return roles.stream()
                // lägg till "ROLE_" framför varje rollnamn (t.ex. USER → ROLE_USER)
                .map(role -> "ROLE_" + role.toUpperCase())
                // konvertera varje sträng till en "authority" som Spring Security använder
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
