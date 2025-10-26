package com.miriam.travel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableMethodSecurity
public class KeycloakSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()

                        // ADMIN
                        .requestMatchers(HttpMethod.GET,    "/api/v1/customers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,   "/api/v1/customers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/v1/customers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/customers/**").hasRole("ADMIN")

                        // ADMIN
                        .requestMatchers(HttpMethod.POST,   "/api/v1/destinations/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/v1/destinations/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/destinations/**").hasRole("ADMIN")

                        // USER
                        .requestMatchers(HttpMethod.POST,   "/api/v1/bookings/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PATCH,  "/api/v1/bookings/**").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,    "/api/v1/bookings/**").hasRole("USER")

                        // ADMIN/USER
                        .requestMatchers(HttpMethod.GET, "/api/v1/destinations/**")
                        .hasAnyRole("USER","ADMIN")

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter()))
                );


        return http.build();
    }


    @Bean
    public org.springframework.core.convert.converter.Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthConverter() {
        return jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();


            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess != null && realmAccess.get("roles") instanceof Collection<?> rawRoles) {
                for (Object r : rawRoles) {
                    String role = String.valueOf(r).toUpperCase();
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                }
            }


            return new JwtAuthenticationToken(jwt, authorities);
        };
    }

}