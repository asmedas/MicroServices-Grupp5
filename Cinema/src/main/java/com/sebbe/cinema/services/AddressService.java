package com.sebbe.cinema.services;

import com.sebbe.cinema.dtos.addressDto.CreateAddressDto;
import com.sebbe.cinema.entities.Address;
import com.sebbe.cinema.exceptions.NoMatchException;
import com.sebbe.cinema.repositories.AddressRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;
    private static final Logger log = LoggerFactory.getLogger(AddressService.class);

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Address findOrCreateAddress(CreateAddressDto createAddressDto){
        log.info("Looking for address: street='{}', city='{}', postalCode='{}'",
                createAddressDto.street(), createAddressDto.city(), createAddressDto.postalCode());

        if (addressRepository.existsByStreetAndCityAndPostalCodeIgnoreCase(
                createAddressDto.street(), createAddressDto.city(), createAddressDto.postalCode())) {

            log.info("Address already exists for: street='{}', city='{}', postalCode='{}'",
                    createAddressDto.street(), createAddressDto.city(), createAddressDto.postalCode());

            return addressRepository.findByStreetAndCityAndPostalCodeIgnoreCase(
                    createAddressDto.street(), createAddressDto.city(), createAddressDto.postalCode());

        } else {
            log.warn("No address found for: street='{}', city='{}', postalCode='{}'. Creating new address.",
                    createAddressDto.street(), createAddressDto.city(), createAddressDto.postalCode());

            return addressRepository.save(new Address(
                    createAddressDto.street(),
                    createAddressDto.city(),
                    createAddressDto.postalCode()));
        }
    }

    public Address findById(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> {
                    log.error("Address not found");
                    return new NoMatchException("Address not found");
                });
    }
}
