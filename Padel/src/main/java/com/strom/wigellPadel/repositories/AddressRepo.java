package com.strom.wigellPadel.repositories;

import com.strom.wigellPadel.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AddressRepo extends JpaRepository<Address, Long> {

    @Query("SELECT a FROM Address a WHERE a.street = :street AND a.postalCode = :postalCode AND a.city = :city")
    Optional<Address> findByStreetAndPostalCodeAndCity(@Param("street") String street, @Param("postalCode") String postalCode, @Param("city") String city);

}
