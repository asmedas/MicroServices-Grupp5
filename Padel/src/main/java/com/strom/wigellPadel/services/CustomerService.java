package com.strom.wigellPadel.services;

import com.strom.wigellPadel.dto.CustomerCreateDto;
import com.strom.wigellPadel.dto.CustomerDto;
import com.strom.wigellPadel.dto.CustomerUpdateDto;
import com.strom.wigellPadel.dto.UserUpdateProfileDto;
import com.strom.wigellPadel.entities.Address;
import com.strom.wigellPadel.entities.Customer;
import com.strom.wigellPadel.mapper.CustomerMapper;
import com.strom.wigellPadel.repositories.AddressRepo;
import com.strom.wigellPadel.repositories.CustomerRepo;
import jakarta.persistence.EntityNotFoundException;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO LÄGG TILL FUNKTION FÖR LOGGNING SOM SPARAS TILL FIL PÅ SAMTLIGA METHODS

@Service
public class CustomerService {

    private final KeycloakUserServiceImpl keycloakUserService;
    private final CustomerRepo customerRepo;
    private final AddressRepo addressRepo;

    public CustomerService(KeycloakUserServiceImpl keycloakUserService, CustomerRepo customerRepo, AddressRepo addressRepo) {
        this.keycloakUserService = keycloakUserService;
        this.customerRepo = customerRepo;
        this.addressRepo = addressRepo;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<CustomerDto> getAllCustomers() {
        return customerRepo.findAll().stream()
                .map(CustomerMapper::toDto)
                .toList();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CustomerDto createCustomer(CustomerCreateDto dto) {
        if (dto == null) {
            return null;
        }
        if (dto.firstName() == null ||
                dto.lastName() == null ||
                dto.street() == null ||
                dto.postalCode() == null ||
                dto.city() == null ||
                dto.email() == null ||
                dto.username() == null ||
                dto.password() == null) {
            throw new IllegalArgumentException("Inget fält får vara null");
        }

        Address address = addressRepo.findByStreetAndPostalCodeAndCity(
                        dto.street(), dto.postalCode(), dto.city())
                .orElseGet(() -> {
                    Address newAddress = new Address(dto.street(), dto.postalCode(), dto.city());
                    return addressRepo.save(newAddress);
                });

        String keycloakUserId = keycloakUserService.createUserAndAssignRole(
                dto.username(), dto.email(), dto.password(), "USER");

        Customer newCustomer = new Customer(
                dto.firstName(),
                dto.lastName(),
                new HashSet<>(),
                dto.email(),
                dto.username(),
                keycloakUserId
        );

        Set<Address> addresses = newCustomer.getAddress();
        addresses.add(address);
        if (address.getCustomers() == null) {
            address.setCustomers(new HashSet<>());
        }
        address.getCustomers().add(newCustomer);

        Customer savedCustomer = customerRepo.save(newCustomer);

        return CustomerMapper.toDto(savedCustomer);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCustomer(Long id) {
        if (!customerRepo.existsById(id)) {
            throw new EntityNotFoundException("Kund med id " + id + " hittades inte");
        }
        customerRepo.deleteById(id);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CustomerDto updateCustomer(Long id, CustomerUpdateDto dto) {
        if (id == null || dto == null) {
            throw new IllegalArgumentException("ID eller body är null");
        }
        Customer updatedCustomer = customerRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kund med id " + id + " hittades"));
        if (    dto.firstName() == null ||
                dto.lastName() == null ||
                dto.street() == null ||
                dto.postalCode() == null ||
                dto.city() == null ||
                dto.email() == null ||
                dto.username() == null) {
            throw new IllegalArgumentException("Inget fält får vara null");
        }

        Address address = addressRepo.findByStreetAndPostalCodeAndCity(
                        dto.street(), dto.postalCode(), dto.city())
                .orElseGet(() -> {
                    Address newAddress = new Address(dto.street(), dto.postalCode(), dto.city());
                    return addressRepo.save(newAddress);
                });

        UserUpdateProfileDto profileDto = new UserUpdateProfileDto(
                dto.email(), null, dto.firstName(), dto.lastName());
        keycloakUserService.updateUserProfile(updatedCustomer.getKeycloakUserId(), profileDto);

        updatedCustomer.setFirstName(dto.firstName());
        updatedCustomer.setLastName(dto.lastName());
        updatedCustomer.setEmail(dto.email());
        updatedCustomer.setUsername(dto.username());

        Set<Address> addresses = updatedCustomer.getAddress() != null ? updatedCustomer.getAddress() : new HashSet<>();
        addresses.add(address);
        updatedCustomer.setAddress(addresses);
        if (address.getCustomers() == null) {
            address.setCustomers(new HashSet<>());
        }
        address.getCustomers().add(updatedCustomer);

        Customer savedCustomer = customerRepo.save(updatedCustomer);

        return CustomerMapper.toDto(savedCustomer);
    }





}
