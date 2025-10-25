package com.miriam.travel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableMethodSecurity
public class KeycloakSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()

                //(USER/ADMIN)
                .requestMatchers(HttpMethod.GET, "/api/v1/destinations/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/bookings/**").hasRole("USER")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/bookings/**").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/api/v1/bookings/**").hasAnyRole("USER")

                //(ADMIN)
                .requestMatchers("/api/v1/customers/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/destinations/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/destinations/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/destinations/**").hasRole("ADMIN")

                .anyRequest().authenticated()
        );

        http.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
        );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter conv = new JwtAuthenticationConverter();
        conv.setJwtGrantedAuthoritiesConverter(this::extractRealmRoles);
        return conv;
    }

    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Map<String,Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null) return List.of();
        Object roles = realmAccess.get("roles");
        if (!(roles instanceof List<?> list)) return List.of();
        return list.stream()
                .map(Object::toString)
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.toUpperCase()))
                .collect(Collectors.toSet());
    }
}