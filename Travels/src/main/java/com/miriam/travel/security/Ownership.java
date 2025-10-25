package com.miriam.travel.security;

import com.miriam.travel.entity.Booking;
import com.miriam.travel.entity.Customer;
import com.miriam.travel.repository.BookingRepository;
import com.miriam.travel.repository.CustomerRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Ownership {

    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;

    public Ownership(CustomerRepository customerRepository, BookingRepository bookingRepository) {
        this.customerRepository = customerRepository;
        this.bookingRepository = bookingRepository;
    }

    public boolean isSelf(Authentication authentication, String customerId) {
        if (authentication == null || !authentication.isAuthenticated()) return false;

        String preferredUsername = extractPreferredUsername(authentication);
        if (preferredUsername == null) return false;

        Optional<Customer> c = customerRepository.findById(customerId);
        return c.isPresent() && preferredUsername.equalsIgnoreCase(c.get().getUsername());
    }

    public boolean isBookingOwner(Authentication authentication, Long bookingId) {
        if (authentication == null || !authentication.isAuthenticated()) return false;

        String preferredUsername = extractPreferredUsername(authentication);
        if (preferredUsername == null) return false;

        Optional<Booking> b = bookingRepository.findById(bookingId);
        return b.isPresent() &&
                b.get().getCustomer() != null &&
                preferredUsername.equalsIgnoreCase(b.get().getCustomer().getUsername());
    }


    private String extractPreferredUsername(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            Object v = jwt.getClaim("preferred_username");
            return v != null ? v.toString() : authentication.getName();
        }
        return authentication.getName();
    }
}
