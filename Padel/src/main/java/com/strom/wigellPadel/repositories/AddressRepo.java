package com.strom.wigellPadel.repositories;

import com.strom.wigellPadel.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepo extends JpaRepository<Address, Long> {
}
