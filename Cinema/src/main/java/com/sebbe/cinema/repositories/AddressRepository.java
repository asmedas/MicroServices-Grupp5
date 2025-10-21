package com.sebbe.cinema.repositories;

import com.sebbe.cinema.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Address a " +
            "WHERE LOWER(a.street) = LOWER(:street) " +
            "AND LOWER(a.city) = LOWER(:city) " +
            "AND LOWER(a.postalCode) = LOWER(:postalCode) ")
    boolean existsByStreetAndCityAndPostalCodeIgnoreCase(String street, String city, String postalCode);
    Address findByStreetAndCityAndPostalCodeIgnoreCase(String street, String city, String postalCode);
}
