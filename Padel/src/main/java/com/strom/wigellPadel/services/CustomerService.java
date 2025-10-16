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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    private final KeycloakUserServiceImpl keycloakUserService;
    private final CustomerRepo customerRepo;
    private final AddressRepo addressRepo;

    public CustomerService(KeycloakUserServiceImpl keycloakUserService, CustomerRepo customerRepo, AddressRepo addressRepo) {
        this.keycloakUserService = keycloakUserService;
        this.customerRepo = customerRepo;
        this.addressRepo = addressRepo;
        logger.debug("CustomerService initialized");
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<CustomerDto> getAllCustomers() {
        logger.info("Hämtar alla kunder");
        try {
            List<CustomerDto> customers = customerRepo.findAll().stream()
                    .map(CustomerMapper::toDto)
                    .toList();
            logger.debug("Lyckades hämta {} kunder", customers.size());
            return customers;
        } catch (Exception e) {
            logger.error("Error vid hämtning av kunder", e);
            throw e;
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CustomerDto createCustomer(CustomerCreateDto dto) {
        logger.info("Skapar nu kund med username: {}", dto.username());
        try {
            if (dto == null) {
                logger.error("Body är null");
                throw new IllegalArgumentException("Body är null");
            }
            if (dto.firstName() == null || dto.lastName() == null || dto.street() == null ||
                    dto.postalCode() == null || dto.city() == null || dto.email() == null ||
                    dto.username() == null || dto.password() == null) {
                logger.error("Ogiltig input: Inget fält får vara null");
                throw new IllegalArgumentException("Inget fält får vara null");
            }

            Address address = addressRepo.findByStreetAndPostalCodeAndCity(
                            dto.street(), dto.postalCode(), dto.city())
                    .orElseGet(() -> {
                        logger.debug("Skapar ny adress: {} {} {}", dto.street(), dto.postalCode(), dto.city());
                        Address newAddress = new Address(dto.street(), dto.postalCode(), dto.city());
                        return addressRepo.save(newAddress);
                    });

            String keycloakUserId = keycloakUserService.createUserAndAssignRole(
                    dto.username(), dto.email(), dto.password(), "USER");
            logger.debug("Skapat Keycloak user med id: {}", keycloakUserId);

            Customer newCustomer = new Customer(
                    dto.firstName(), dto.lastName(), new HashSet<>(), dto.email(), dto.username(), keycloakUserId);

            Set<Address> addresses = newCustomer.getAddress();
            addresses.add(address);
            if (address.getCustomers() == null) {
                address.setCustomers(new HashSet<>());
            }
            address.getCustomers().add(newCustomer);

            Customer savedCustomer = customerRepo.save(newCustomer);
            logger.info("Lyckades skapa ny kund med id: {}", savedCustomer.getId());
            return CustomerMapper.toDto(savedCustomer);
        } catch (Exception e) {
            logger.error("Error vid skapande av kund med username: {}", dto.username(), e);
            throw e;
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCustomer(Long id) {
        logger.info("Tar bort kund med id: {}", id);
        try {
            if (id == null) {
                logger.error("Id är null");
                throw new IllegalArgumentException("Id är null");
            }
            Customer customer = customerRepo.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Kund med id {} hittades inte", id);
                        return new EntityNotFoundException("Kund med id " + id + " hittades inte");
                    });
            customerRepo.delete(customer);
            logger.info("Lyckades ta bort kund med id: {}", id);
        } catch (Exception e) {
            logger.error("Error vid borttag av kund med id: {}", id, e);
            throw e;
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CustomerDto updateCustomer(Long id, CustomerUpdateDto dto) {
        logger.info("Uppdaterar kund med id: {}", id);
        try {
            if (id == null || dto == null) {
                logger.error("ID eller body är null");
                throw new IllegalArgumentException("ID eller body är null");
            }
            if (dto.firstName() == null || dto.lastName() == null || dto.street() == null ||
                    dto.postalCode() == null || dto.city() == null || dto.email() == null ||
                    dto.username() == null) {
                logger.error("Ogiltig input: Inget fält får vara null");
                throw new IllegalArgumentException("Inget fält får vara null");
            }

            Customer updatedCustomer = customerRepo.findById(id)
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

            UserUpdateProfileDto profileDto = new UserUpdateProfileDto(
                    dto.email(), null, dto.firstName(), dto.lastName());
            keycloakUserService.updateUserProfile(updatedCustomer.getKeycloakUserId(), profileDto);
            logger.debug("Uppdaterade Keycloak user profile för userId: {}", updatedCustomer.getKeycloakUserId());

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
            logger.info("Lyckades uppdatera kund med id: {}", id);
            return CustomerMapper.toDto(savedCustomer);
        } catch (Exception e) {
            logger.error("Error vid uppdatering av kund med id: {}", id, e);
            throw e;
        }
    }
}