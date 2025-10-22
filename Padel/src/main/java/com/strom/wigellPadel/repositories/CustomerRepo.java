package com.strom.wigellPadel.repositories;

import com.strom.wigellPadel.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepo extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUsername(String username);
}
