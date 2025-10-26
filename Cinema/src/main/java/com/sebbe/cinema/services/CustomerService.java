package com.sebbe.cinema.services;

import com.sebbe.cinema.dtos.addressDtos.CreateAddressDto;
import com.sebbe.cinema.dtos.customerDtos.CreateCustomerWithAccountDto;
import com.sebbe.cinema.dtos.customerDtos.CustomerDto;
import com.sebbe.cinema.dtos.customerDtos.CustomerUpdateDto;
import com.sebbe.cinema.dtos.customerDtos.UpdateUserProfileDto;
import com.sebbe.cinema.entities.Address;
import com.sebbe.cinema.entities.Booking;
import com.sebbe.cinema.entities.Customer;
import com.sebbe.cinema.entities.Ticket;
import com.sebbe.cinema.exceptions.AlreadyExistsError;
import com.sebbe.cinema.exceptions.NoMatchException;
import com.sebbe.cinema.exceptions.UnexpectedError;
import com.sebbe.cinema.mappers.CustomerMapper;
import com.sebbe.cinema.repositories.BookingRepository;
import com.sebbe.cinema.repositories.CustomerRepository;

import com.sebbe.cinema.repositories.TicketRepository;
import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final KeycloakUserService keycloakUserService;
    private final AddressService addressService;
    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    public CustomerService(CustomerRepository customerRepository, KeycloakUserService keycloakUserService
    , AddressService addressService, TicketRepository ticketRepository, BookingRepository bookingRepository){
        this.customerRepository = customerRepository;
        this.keycloakUserService = keycloakUserService;
        this.addressService = addressService;
    }

    @Transactional(readOnly = true)
    public List<CustomerDto> listCustomers(){
        return customerRepository.findAll().stream()
                .map(CustomerMapper::toDto)
                .toList();
    }


    /*
    känner att denna coupling är nödvändig vid skapande av Customer, då de måste ha en address
    och ett keycloak ID för att kunna hantera köp och bokningar mm.
     */
    public Customer createCustomerWithKeycloakUserAndAddress(CreateCustomerWithAccountDto dto) {
        String keycloakId = null;
        log.debug("Trying to create a Customer with Keycloak User and an Address");
        validateCreationDto(dto);
        try{
            keycloakId = keycloakUserService.createUserForCustomer(dto.username(), dto.email(), dto.password());

            Address address = addressService.findOrCreateAddress(dto.address());

            Customer customer = CustomerMapper.buildCustomerFromCreateDto(dto, keycloakId, address);

            return customerRepository.save(customer);
        } catch (DataAccessException | PersistenceException ex) {
            log.error("Database error during customer creation (Keycloak user left intact):", ex);
            throw ex;
        } catch (RuntimeException ex) {
            log.error("Unexpected runtime exception during customer creation (Keycloak user left intact):", ex);
            throw ex;
        }

    }

    public CustomerDto updateCustomer(Long id, CustomerUpdateDto dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NoMatchException("Customer not found"));

        ProfileChanges changes = detectProfileChanges(customer, dto);
        validateEmailUniqueness(customer, dto, changes.emailChanged());

        if (customer.getKeycloakId() != null && changes.hasAnyChange()) {
            syncKeycloakProfile(customer, dto, changes);
        }

        updateCustomerFields(customer, dto);
        return saveCustomerWithRollback(customer, id, changes);
    }

    public void deleteCustomer(long id){
        log.debug("Deleting customer with id {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NoMatchException("No customer found with id " + id));
        try {
            for (Ticket t : new ArrayList<>(customer.getTickets())) {
                t.removeTicketFromConnections();
            }
            for (Booking b : new ArrayList<>(customer.getBookings())) {
                b.removeBookingFromConnections();
            }
            customerRepository.delete(customer);

        } catch (DataAccessException e) {
            log.error("Database error deleting customer", e);
            throw new UnexpectedError("Database error deleting customer " + e);
        }
    }

    public CustomerDto addAddress(Long customerId, CreateAddressDto address){
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.error("Customer not found");
                    return new NoMatchException("Customer not found");
                });
        try{
            Address newAddress = addressService.findOrCreateAddress(address);
            customer.addAddress(newAddress);
            customerRepository.save(customer);
            log.debug("Address added to customer with id {}", customerId);
        } catch (DataAccessException e){
            log.error("Database error adding address to customer", e);
            throw new UnexpectedError("Database error adding address to customer " + e);
        }
        return CustomerMapper.toDto(customer);

    }

    public void removeAddress(Long customerId, Long addressId){
        log.debug("Removing address with id {} from customer with id {}", addressId, customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.error("Customer not found");
                    return new NoMatchException("Customer not found");
                });
        if(customer.getAddresses().size() == 1){
            log.error("Cannot remove last address");
            throw new IllegalStateException("Cannot remove last address");
        }
        try{
            Address address = addressService.findById(addressId);
            customer.removeAddress(address);
            customerRepository.save(customer);
            log.debug("Address with id {} removed from customer with id {}", addressId, customerId);
        } catch (DataAccessException e){
            log.error("Database error removing address from customer", e);
            throw new UnexpectedError("Database error removing address from customer " + e);
        }

    }


    /**
     * Interna hjälpfunktioner
     *
     * Privata metoder som används för att göra ovanstående kod mer lättläst
     *
     *   1. Validering:
     *      - validateCreationDto(...) -> säkerställer unik e-post vid skapande av kund
     *      - validateEmailUniqueness(...) -> säkerställer unik e-post vid uppdatering
     *
     *   2. Förändringsdetektering och mappning:
     *      - detectProfileChanges(...) -> avgör vilka profilfält som har ändrats
     *      - buildUpdateProfileDto(...) -> bygger DTO:n som skickas till Keycloak
     *      - updateCustomerFields(...) -> applicerar ändringarna på Customer-entiteten
     *
     *   3. Extern synkronisering och återställning:
     *      - syncKeycloakProfile(...) -> uppdaterar Keycloak när profildata ändras
     *      - saveCustomerWithRollback(...) -> sparar kunden och rullar tillbaka Keycloak
     *        om databasens skrivning misslyckas
     *      - rollbackKeycloakUpdate(...) -> kompensationsåtgärd vid misslyckad DB-uppdatering
     */
    private void validateCreationDto(CreateCustomerWithAccountDto dto) {
        if (customerRepository.existsByEmail(dto.email())) {
            log.error("Email already in use");
            throw new AlreadyExistsError("Email already in use");
        }
    }

    private ProfileChanges detectProfileChanges(Customer customer, CustomerUpdateDto dto) {
        log.debug("Detecting profile changes for customer with ID: {}", customer.getId());
        boolean emailChanged = dto.email() != null &&
                !dto.email().equalsIgnoreCase(customer.getEmail());
        boolean firstNameChanged = dto.firstName() != null &&
                !dto.firstName().equalsIgnoreCase(customer.getFirstName());
        boolean lastNameChanged = dto.lastName() != null &&
                !dto.lastName().equalsIgnoreCase(customer.getLastName());

        return new ProfileChanges(emailChanged, firstNameChanged, lastNameChanged);
    }

    private void validateEmailUniqueness(Customer customer, CustomerUpdateDto dto, boolean emailChanged) {
        log.debug("Validating email uniqueness for customer with ID: {}", customer.getId());
        if (emailChanged && customerRepository.existsByEmailAndIdNot(customer.getEmail(), customer.getId())) {
            throw new AlreadyExistsError("Email används redan");
        }
    }

    private void syncKeycloakProfile(Customer customer, CustomerUpdateDto dto, ProfileChanges changes) {
        log.debug("Syncing Keycloak profile for customer with ID: {}", customer.getId());
        try {
            UpdateUserProfileDto profileDto = buildUpdateProfileDto(dto, changes);
            keycloakUserService.updateUserProfile(customer.getKeycloakId(), profileDto);
        } catch (RuntimeException ex) {
            log.error("Failed to update Keycloak profile for customer {} (keycloakId={})",
                    customer.getId(), customer.getKeycloakId(), ex);
            throw new UnexpectedError("Keycloak update failed " + ex);
        }
    }

    private UpdateUserProfileDto buildUpdateProfileDto(CustomerUpdateDto dto, ProfileChanges changes) {
        return new UpdateUserProfileDto(
                changes.emailChanged() ? dto.email() : null,
                null,
                changes.firstNameChanged() ? dto.firstName() : null,
                changes.lastNameChanged() ? dto.lastName() : null
        );
    }

    private void updateCustomerFields(Customer customer, CustomerUpdateDto dto) {
        log.debug("Updating customer fields for customer with ID: {}", customer.getId());
        if (dto.firstName() != null) {
            customer.setFirstName(dto.firstName());
        }
        if (dto.lastName() != null) {
            customer.setLastName(dto.lastName());
        }
        if (dto.email() != null) {
            customer.setEmail(dto.email());
        }
        if(dto.age() != null){
            customer.setAge(dto.age());
        }
    }

    private CustomerDto saveCustomerWithRollback(Customer customer, Long id, ProfileChanges changes) {
        String originalEmail = customer.getEmail();
        String originalFirstName = customer.getFirstName();
        String originalLastName = customer.getLastName();

        try {
            log.debug("Updating customer with ID: {}", customer.getId());
            Customer saved = customerRepository.save(customer);
            return CustomerMapper.toDto(saved);
        } catch (DataAccessException ex) {
            log.error("Database error while updating customer {}", id, ex);

            if (customer.getKeycloakId() != null && changes.hasAnyChange()) {
                rollbackKeycloakUpdate(customer, id, originalEmail, originalFirstName,
                        originalLastName, changes);
            }

            throw new PersistenceException("Failed to update customer", ex);
        }
    }

    private void rollbackKeycloakUpdate(Customer customer, Long id, String originalEmail,
                                        String originalFirstName, String originalLastName,
                                        ProfileChanges changes) {
        try {
            log.warn("Rolling back Keycloak update for customer {} (keycloakId={})",
                    id, customer.getKeycloakId());

            UpdateUserProfileDto rollbackDto = new UpdateUserProfileDto(
                    changes.emailChanged() ? originalEmail : null,
                    null,
                    changes.firstNameChanged() ? originalFirstName : null,
                    changes.lastNameChanged() ? originalLastName : null
            );

            keycloakUserService.updateUserProfile(customer.getKeycloakId(), rollbackDto);
        } catch (Exception rollbackEx) {
            log.error("Rollback of Keycloak update failed for customer {}", id, rollbackEx);
        }
    }

    private record ProfileChanges(boolean emailChanged, boolean firstNameChanged,
                                  boolean lastNameChanged) {
        boolean hasAnyChange() {
            return emailChanged || firstNameChanged || lastNameChanged;
        }
    }

}
