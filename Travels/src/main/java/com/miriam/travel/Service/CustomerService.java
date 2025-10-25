package com.miriam.travel.service;

import com.miriam.travel.dto.customer.CustomerCreateRequest;
import com.miriam.travel.dto.customer.CustomerSummaryDTO;
import com.miriam.travel.dto.customer.CustomerUpdateRequest;
import com.miriam.travel.entity.Address;
import com.miriam.travel.entity.Customer;
import com.miriam.travel.repository.AddressRepository;
import com.miriam.travel.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    public CustomerService(CustomerRepository customerRepository, AddressRepository addressRepository) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
    }

    @Transactional
    public List<CustomerSummaryDTO> listAll() {
        String user = getCurrentUser();
        log.debug("{} listed all customers", user);
        return customerRepository.findAll().stream()
                .map(c -> new CustomerSummaryDTO(
                        c.getId(),
                        c.getUsername(),
                        c.getFullName(),
                        c.getEmail(),
                        c.getAddresses() != null ? c.getAddresses().size() : 0,
                        c.getBookings() != null ? c.getBookings().size() : 0
                )).toList();
    }

    @Transactional
    public Customer create(CustomerCreateRequest req) {
        if (customerRepository.existsById(req.id)) {
            throw new IllegalArgumentException("Customer id already exists: " + req.id);
        }
        if (customerRepository.existsByUsername(req.username)) {
            throw new IllegalArgumentException("Username already exists: " + req.username);
        }
        Customer c = new Customer(req.id, req.username, req.fullName, req.email, req.role);
        Customer saved = customerRepository.save(c);
        String user = getCurrentUser();
        log.info("{} created customer id={} username={}", user, saved.getId(), saved.getUsername());
        return saved;
    }

    @Transactional
    public Customer update(String id, CustomerUpdateRequest req) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + id));
        c.setUsername(req.username);
        c.setFullName(req.fullName);
        c.setEmail(req.email);
        Customer saved = customerRepository.save(c);
        String user = getCurrentUser();
        log.info("{} updated customer id={} username={}", user, id, saved.getUsername());
        return saved;
    }

    @Transactional
    public void delete(String id) {
        if (!customerRepository.existsById(id)) {
            throw new EntityNotFoundException("Customer not found");
        }
        customerRepository.deleteById(id);
        String user = getCurrentUser();
        log.info("{} deleted customer id={}", user, id);
    }

    @Transactional
    public Address addAddress(String customerId, Address address) {
        Customer c = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + customerId));
        address.setCustomer(c);
        Address saved = addressRepository.save(address);
        String user = getCurrentUser();
        log.info("{} created address id={} for customer id={}", user, saved.getId(), customerId);
        return saved;
    }

    @Transactional
    public void deleteAddress(String customerId, Long addressId) {
        Address a = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found: " + addressId));
        if (a.getCustomer() == null || !customerId.equals(a.getCustomer().getId())) {
            throw new IllegalArgumentException("Address does not belong to customer");
        }
        addressRepository.delete(a);
        String user = getCurrentUser();
        log.info("{} deleted address id={} for customer id={}", user, addressId, customerId);
    }


    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "anonymous";
    }
}
