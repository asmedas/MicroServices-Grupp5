package com.andreas.wigellmcrental.service;

import com.andreas.wigellmcrental.dto.AddressDto;
import com.andreas.wigellmcrental.entity.Address;
import com.andreas.wigellmcrental.entity.Customer;
import com.andreas.wigellmcrental.repository.AddressRepository;
import com.andreas.wigellmcrental.repository.CustomerRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class AddressService {
    private static final Logger logger = LoggerFactory.getLogger(AddressService.class);

    private final AddressRepository addressRepo;
    private final CustomerRepository customerRepo;

    public AddressService(AddressRepository addressRepo, CustomerRepository customerRepo) {
        this.addressRepo = addressRepo;
        this.customerRepo = customerRepo;
    }

    public List<Address> findAll() {
        return addressRepo.findAll();
    }

    public Address addAddress(Long customerId, AddressDto dto, Authentication auth) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Address address = new Address(dto.city(), dto.street(), dto.postalCode(), dto.country());
        address.setCustomer(customer);
        logger.info("Address added for: customer: {}, city: {}, street: {}", customer.getUsername(), address.getCity(), address.getStreet());
        return addressRepo.save(address);
    }

    public Address updateAddress(Long id, AddressDto dto, Authentication auth) {
        Address address = addressRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        address.setCity(dto.city());
        address.setStreet(dto.street());
        address.setPostalCode(dto.postalCode());
        address.setCountry(dto.country());
        return addressRepo.save(address);
    }

    public void deleteAddress(Long id, Authentication auth) {
        Address address = addressRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        Customer customer = address.getCustomer(); //hämta kopplad kund
        addressRepo.delete(address);
        // Loggning med vem som tog bort adressen
        logger.info("Address deleted: id={}, customer={}, performedBy={}",
                id,
                customer != null ? customer.getUsername() : "unknown",
                auth != null ? auth.getName() : "system");
    }

}
