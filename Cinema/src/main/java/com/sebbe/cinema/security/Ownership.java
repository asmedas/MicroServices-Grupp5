package com.sebbe.cinema.security;

import com.sebbe.cinema.repositories.CustomerRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class Ownership {

    private final CustomerRepository customerRepository;

    public Ownership(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public boolean isSelf(Authentication authentication, Long customerId) {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) return false;
        String sub = jwtAuth.getToken().getClaimAsString("sub");
        if (sub == null) return false;

        return customerRepository.findById(customerId)
                .map(s -> sub.equals(s.getKeycloakId()))
                .orElse(false);
    }

}
