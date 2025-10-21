package com.andreas.wigellmcrental.repository;

import com.andreas.wigellmcrental.entity.Address;
import com.andreas.wigellmcrental.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByCustomer(Customer customer);
}

