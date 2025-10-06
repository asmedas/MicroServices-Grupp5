package com.strom.wigellPadel.repositories;

import com.strom.wigellPadel.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepo extends JpaRepository<Customer, Integer> {
}
