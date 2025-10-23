package com.zetterlund.wigell_sushi_api.service;

import com.zetterlund.wigell_sushi_api.dto.AddressDto;
import com.zetterlund.wigell_sushi_api.dto.CustomerCreationRequestDto;
import com.zetterlund.wigell_sushi_api.dto.CustomerDto;
import com.zetterlund.wigell_sushi_api.entity.Address;
import com.zetterlund.wigell_sushi_api.entity.Customer;
import com.zetterlund.wigell_sushi_api.exception.ResourceNotFoundException;
import com.zetterlund.wigell_sushi_api.exception.UnexpectedError;
import com.zetterlund.wigell_sushi_api.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    private final CustomerRepository customerRepository;
    private final KeycloakUserService keycloakUserService;

    public CustomerService(CustomerRepository customerRepository, KeycloakUserService keycloakUserService) {
        this.customerRepository = customerRepository;
        this.keycloakUserService = keycloakUserService;
    }

    public List<Customer> getAllCustomers() {
        logger.info("getAllCustomers service class");
        return customerRepository.findAll();
    }

    public CustomerDto addCustomerFromDto(CustomerCreationRequestDto dto) {
        if (dto == null) throw new IllegalArgumentException("Body är null");

        Customer customer = new Customer();
        customer.setUsername(dto.getUsername());
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhoneNumber(dto.getPhoneNumber());

        // Skapa Keycloak-user
        try {
            String keycloakUserId = keycloakUserService.createUserForCustomer(
                    customer.getUsername(),
                    customer.getEmail(),
                    dto.getRawPassword()
            );
            customer.setKeycloakUserId(keycloakUserId);
        } catch (DataAccessException ex) {
            logger.error("Failed to update Keycloak profile for customer {} (keycloakId={})",
                    customer.getId(), customer.getKeycloakUserId(), ex);
            throw new UnexpectedError("Keycloak update failed " + ex);
        }

        // Adresser
        if (dto.getAddresses() != null) {
            customer.setAddresses(dto.getAddresses().stream().map(addressDto -> {
                Address address = new Address();
                address.setStreet(addressDto.getStreet());
                address.setPostalCode(addressDto.getPostalCode());
                address.setCity(addressDto.getCity());
                address.setCustomer(customer);
                return address;
            }).toList());
        }

        // Spara kund
        Customer savedCustomer = customerRepository.save(customer);

        // Returnera CustomerDto istället för Customer
        return mapToDto(savedCustomer);
    }

    public CustomerDto updateCustomer(Integer customerId,
                                      CustomerCreationRequestDto dto) {
        logger.info("Updating customer with id {}", customerId);

        Customer existing = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer with id " + customerId + " not found."));

        existing.setUsername(dto.getUsername());
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setEmail(dto.getEmail());
        existing.setPhoneNumber(dto.getPhoneNumber());

        // Adresshantering
        if (dto.getAddresses() != null && !dto.getAddresses().isEmpty()) {
            // Rensa befintliga adresser
            existing.getAddresses().clear();

            for (AddressDto addressDto : dto.getAddresses()) {
                Address address = new Address();
                address.setStreet(addressDto.getStreet());
                address.setPostalCode(addressDto.getPostalCode());
                address.setCity(addressDto.getCity());
                address.setCustomer(existing);
                existing.getAddresses().add(address);
            }
        }

        Customer saved = customerRepository.save(existing);
        logger.info("Customer with id {} successfully updated.", customerId);

        return mapToDto(saved);
    }

    public void deleteCustomerById(Integer customerId) {
        logger.info("deleteCustomerById service class");
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer with id " + customerId + " not found.");
        }
        customerRepository.deleteById(customerId);
    }

    public CustomerDto mapToDto(Customer customer) {
        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setUsername(customer.getUsername());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        dto.setPhoneNumber(customer.getPhoneNumber());

        List<AddressDto> addressDtos = customer.getAddresses().stream().map(address -> {
            AddressDto adTo = new AddressDto();
            adTo.setId(address.getId());
            adTo.setStreet(address.getStreet());
            adTo.setPostalCode(address.getPostalCode());
            adTo.setCity(address.getCity());
            return adTo;
        }).toList();

        dto.setAddresses(addressDtos);
        return dto;
    }
}
