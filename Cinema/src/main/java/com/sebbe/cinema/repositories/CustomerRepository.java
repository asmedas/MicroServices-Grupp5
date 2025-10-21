package com.sebbe.cinema.repositories;

import com.sebbe.cinema.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByEmail(String email);

    Optional<Customer> findByKeycloakId(String keycloakId);

}
