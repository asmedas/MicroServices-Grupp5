package com.miriam.travel.repository;

import com.miriam.travel.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {
    boolean existsByUsername(String username);
}
