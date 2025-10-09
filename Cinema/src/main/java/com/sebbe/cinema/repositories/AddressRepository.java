package com.sebbe.cinema.repositories;

import com.sebbe.cinema.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByStreetAndCityAndPostalCode(String street, String city, String postalCode);
}
