package com.strom.wigellPadel.security;

import com.strom.wigellPadel.entities.Customer;
import com.strom.wigellPadel.repositories.CustomerRepo;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component("ownership")
public class Ownership {
    private final CustomerRepo customerRepo;

    public Ownership(CustomerRepo customerRepo) {
        this.customerRepo = customerRepo;
    }

    public boolean isSelf(Authentication authentication, Long customerId) {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) return false;
        String sub = jwtAuth.getToken().getClaimAsString("sub");
        if (sub == null) return false;

        return customerRepo.findById(customerId)
                .map(s -> sub.equals(s.getKeycloakUserId()))
                .orElse(false);
    }


}
