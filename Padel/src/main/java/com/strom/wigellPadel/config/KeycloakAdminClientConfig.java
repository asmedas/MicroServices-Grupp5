package com.strom.wigellPadel.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakAdminClientConfig {

    @Value("${spring.keycloak.server-url}")
    private String serverUrl;

    @Value("${spring.keycloak.realm}")
    private String realm;

    @Value("${spring.keycloak.admin.client-id}")
    private String adminClientId;

    @Value("${spring.keycloak.admin.client-secret}")
    private String adminClientSecret;

    @Bean
    public Keycloak keycloakAdminClient() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(adminClientId)
                .clientSecret(adminClientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }
}
