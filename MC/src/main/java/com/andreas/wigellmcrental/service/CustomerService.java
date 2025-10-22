package com.andreas.wigellmcrental.service;

import com.andreas.wigellmcrental.entity.Address;
import com.andreas.wigellmcrental.entity.Customer;
import com.andreas.wigellmcrental.repository.AddressRepository;
import com.andreas.wigellmcrental.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepo;
    private final AddressRepository addressRepo;

    public CustomerService(CustomerRepository customerRepo, AddressRepository addressRepo) {
        this.customerRepo = customerRepo;
        this.addressRepo = addressRepo;
    }

    // --- KUNDER ---

    public List<Customer> findAll() {
        logger.info("Finding all customers");
        return customerRepo.findAll();
    }

    public Customer get(Long id) {
        logger.info("Fetching Customer with id: {}", id);
        List<Customer> customers = customerRepo.findAll();
        logger.info("Fetching Customer with id: {}", id);
        return customerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public Customer create(Customer in) {
        Customer saved = customerRepo.save(in);
        logger.info("Customer created: {}", saved.getUsername());
        return saved;
    }

    public Customer update(Long id, Customer in) {
        Customer existing = customerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        existing.setName(in.getName());
        existing.setEmail(in.getEmail());
        existing.setPhone(in.getPhone());
        Customer saved = customerRepo.save(existing);
        logger.info("Customer updated: {}", saved.getUsername());
        return saved;
    }

    public void delete(Long id) {
        Customer c = customerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customerRepo.delete(c);
        logger.warn("Customer deleted: {}", c.getUsername());
    }

}
