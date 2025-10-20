package com.sebbe.cinema.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {})
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").authenticated()
                        .requestMatchers(HttpMethod.GET,"/api/v1/movies").hasRole("USER")
                        .requestMatchers(HttpMethod.POST,"/api/v1/bookings").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/tickets").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,"/api/v1/tickets**").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,"/api/v1/screenings**").hasRole("USER")
                        .requestMatchers(HttpMethod.PATCH,"/api/v1/bookings/**").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,"/api/v1/bookings**").hasRole("USER")

                        .requestMatchers("/api/v1/customers", "api/v1/customers/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/movies", "/api/v1/movies/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/rooms", "/api/v1/rooms/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/screenings", "/api/v1/screenings/**").hasRole("ADMIN")
                )
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter())))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint()))
                .build();
    }

    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthConverter(){
        return jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();

            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            if(realmAccess != null && realmAccess.get("roles") instanceof Collection<?> rawRoles){
                for (Object r : rawRoles){
                    String role = String.valueOf(r).toUpperCase();
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                }
            }
            return new JwtAuthenticationToken(jwt, authorities);
        };

    }

}
