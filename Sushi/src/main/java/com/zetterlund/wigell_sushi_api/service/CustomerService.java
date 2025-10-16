package com.zetterlund.wigell_sushi_api.service;

import com.zetterlund.wigell_sushi_api.entity.Customer;
import com.zetterlund.wigell_sushi_api.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final KeycloakUserService keycloakUserService;

    public CustomerService(CustomerRepository customerRepository, KeycloakUserService keycloakUserService) {
        this.customerRepository = customerRepository;
        this.keycloakUserService = keycloakUserService;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer addCustomer(Customer customer, String rawPassword) {
        // Skapa en Keycloak-användare
        String keycloakUserId = keycloakUserService.createUserForCustomer(
                customer.getUsername(),
                customer.getUsername(), // Använd namn som e-post (eller byt)
                rawPassword
        );

        // Spara användar-ID från Keycloak till kundobjektet (valfritt)
        customer.setKeycloakUserId(keycloakUserId);

        return customerRepository.save(customer);
    }
}
