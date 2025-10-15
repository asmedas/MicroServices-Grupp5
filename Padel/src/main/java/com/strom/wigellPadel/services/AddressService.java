package com.strom.wigellPadel.services;

import com.strom.wigellPadel.dto.AddressCreateDto;
import com.strom.wigellPadel.dto.AddressDto;
import com.strom.wigellPadel.dto.CustomerDto;
import com.strom.wigellPadel.entities.Address;
import com.strom.wigellPadel.entities.Customer;
import com.strom.wigellPadel.mapper.CustomerMapper;
import com.strom.wigellPadel.repositories.AddressRepo;
import com.strom.wigellPadel.repositories.CustomerRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class AddressService {

    private final AddressRepo addressRepo;
    private final CustomerRepo customerRepo;

    public AddressService(AddressRepo addressRepo, CustomerRepo customerRepo) {
        this.addressRepo = addressRepo;
        this.customerRepo = customerRepo;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CustomerDto createAddress(Long id, AddressCreateDto dto) {
        if (id == null || dto == null) {
            throw new IllegalArgumentException("ID eller body är null");
        }
        if (dto.street() == null || dto.street().isEmpty()
        || dto.postalCode() == null || dto.postalCode().isEmpty()
        || dto.city() == null || dto.city().isEmpty()){
            throw new IllegalArgumentException("Inget fält får vara null");
        }
        Customer customer = customerRepo.findById(id).get();

        Address address = addressRepo.findByStreetAndPostalCodeAndCity(
                        dto.street(), dto.postalCode(), dto.city())
                .orElseGet(() -> {
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

        return CustomerMapper.toDto(customer);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAddressFromCustomer(Long id, Long addressId) {
        if (id == null || addressId == null) {
            throw new IllegalArgumentException("KundID eller AdressID är null");
        }
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kund med id " + id + " hittades inte"));
        Address address = addressRepo.findById(addressId)
                .orElseThrow(()-> new EntityNotFoundException("Adress med id " + id + " hittades inte"));
        if (!customer.getAddress().contains(address)) {
            throw new EntityNotFoundException("Adress med id " + id + " finns inte på kunden");
        }
        customer.getAddress().remove(address);
        address.getCustomers().remove(customer);
        customerRepo.save(customer);
    }

}
