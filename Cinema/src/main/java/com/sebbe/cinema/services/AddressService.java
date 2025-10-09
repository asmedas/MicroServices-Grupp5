package com.sebbe.cinema.services;

import com.sebbe.cinema.entities.Address;
import com.sebbe.cinema.repositories.AddressRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;
    private static final Logger log = LoggerFactory.getLogger(AddressService.class);

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Address findOrCreateAddress(String street, String city, String postalCode){
        log.info("Looking for address: street='{}', city='{}', postalCode='{}'",
                street, city, postalCode);

        Optional<Address> address = addressRepository.findByStreetAndCityAndPostalCode(street, city, postalCode);

        if (address.isPresent()) {
            log.info("Address found: {}", address.get());
            return address.get();
        } else {
            log.warn("No address found for: street='{}', city='{}', postalCode='{}'",
                    street, city, postalCode + " creating new address");
            return addressRepository.save(new Address(street, city, postalCode));
        }
    }

    public Address normalizeAddress(Address address){
        address.setStreet(address.getStreet().toLowerCase());
        address.setCity(address.getCity().toLowerCase());
        return address;
    }

}
