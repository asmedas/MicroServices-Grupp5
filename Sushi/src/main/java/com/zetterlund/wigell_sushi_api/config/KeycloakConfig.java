package com.zetterlund.wigell_sushi_api.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Bean
    public Keycloak keycloak(
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String serverUrl,
            @Value("${keycloak.admin.realm}") String realm,
            @Value("${keycloak.admin.username}") String username,
            @Value("${keycloak.admin.password}") String password,
            @Value("${spring.security.oauth2.client.registration.keycloak.client-id}") String clientId) {

        return KeycloakBuilder.builder()
                .serverUrl(serverUrl.replace("/realms/" + realm, "")) // Extrahera basal URL
                .realm(realm)
                .username(username)
                .password(password)
                .clientId(clientId)
                .build();
    }
}
