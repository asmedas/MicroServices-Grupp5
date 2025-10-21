package com.strom.wigellPadel.services;

import com.strom.wigellPadel.dto.AddressCreateDto;
import com.strom.wigellPadel.dto.CustomerDto;
import com.strom.wigellPadel.entities.Address;
import com.strom.wigellPadel.entities.Customer;
import com.strom.wigellPadel.mapper.CustomerMapper;
import com.strom.wigellPadel.repositories.AddressRepo;
import com.strom.wigellPadel.repositories.CustomerRepo;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class AddressService {

    private static final Logger logger = LoggerFactory.getLogger(AddressService.class);
    private final AddressRepo addressRepo;
    private final CustomerRepo customerRepo;

    public AddressService(AddressRepo addressRepo, CustomerRepo customerRepo) {
        this.addressRepo = addressRepo;
        this.customerRepo = customerRepo;
        logger.debug("AddressService initialized");
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CustomerDto createAddress(Long id, AddressCreateDto dto) {
        logger.info("Skapar adress för kund med id: {}", id);
        try {
            if (id == null || dto == null) {
                logger.error("ID eller body är null");
                throw new IllegalArgumentException("ID eller body är null");
            }
            if (dto.street() == null || dto.street().isEmpty() || dto.postalCode() == null ||
                    dto.postalCode().isEmpty() || dto.city() == null || dto.city().isEmpty()) {
                logger.error("Ogiltig input: Inget fält får vara null");
                throw new IllegalArgumentException("Inget fält får vara null");
            }
            Customer customer = customerRepo.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Kund med id {} hittades inte", id);
                        return new EntityNotFoundException("Kund med id " + id + " hittades inte");
                    });

            Address address = addressRepo.findByStreetAndPostalCodeAndCity(
                            dto.street(), dto.postalCode(), dto.city())
                    .orElseGet(() -> {
                        logger.debug("Skapar ny adress: {} {} {}", dto.street(), dto.postalCode(), dto.city());
                        Address newAddress = new Address(dto.street(), dto.postalCode(), dto.city());
                        return addressRepo.save(newAddress);
                    });

            Set<Address> addresses = customer.getAddress() != null ? customer.getAddress() : new HashSet<>();
            addresses.add(address);
            customer.setAddress(addresses);
            if (address.getCustomers() == null) {
                address.setCustomers(new HashSet<>());
            }
            address.getCustomers().add(customer);
            customerRepo.save(customer);
            logger.info("Lyckades skapa adress för kund med id: {}", id);
            return CustomerMapper.toDto(customer);
        } catch (Exception e) {
            logger.error("Error vid skapande av adress för kund med id: {}", id, e);
            throw e;
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAddressFromCustomer(Long id, Long addressId) {
        logger.info("Tar bort adress med id: {} från kund med id: {}", addressId, id);
        try {
            if (id == null || addressId == null) {
                logger.error("KundID eller AdressID är null");
                throw new IllegalArgumentException("KundID eller AdressID är null");
            }
            Customer customer = customerRepo.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Kund med id {} hittades inte", id);
                        return new EntityNotFoundException("Kund med id " + id + " hittades inte");
                    });
            Address address = addressRepo.findById(addressId)
                    .orElseThrow(() -> {
                        logger.error("Adress med id {} hittades inte", addressId);
                        return new EntityNotFoundException("Adress med id " + addressId + " hittades inte");
                    });
            if (!customer.getAddress().contains(address)) {
                logger.error("Adress med id {} finns inte på kund med id {}", addressId, id);
                throw new EntityNotFoundException("Adress med id " + addressId + " finns inte på kunden");
            }
            customer.getAddress().remove(address);
            address.getCustomers().remove(customer);
            customerRepo.save(customer);
            logger.info("Lyckades ta bort adress med id: {} från kund med id: {}", addressId, id);
        } catch (Exception e) {
            logger.error("Error vid borttag av adress med id: {} från kund med id: {}", addressId, id, e);
            throw e;
        }
    }
}