package com.zetterlund.wigell_sushi_api.service;

import com.zetterlund.wigell_sushi_api.entity.Address;
import com.zetterlund.wigell_sushi_api.entity.Customer;
import com.zetterlund.wigell_sushi_api.exception.ResourceNotFoundException;
import com.zetterlund.wigell_sushi_api.repository.AddressRepository;
import com.zetterlund.wigell_sushi_api.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class AddressService {
    private static final Logger logger = LoggerFactory.getLogger(AddressService.class);

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;

    public AddressService(AddressRepository addressRepository, CustomerRepository customerRepository) {
        this.addressRepository = addressRepository;
        this.customerRepository = customerRepository;
    }

    public Address addAddress(Long customerId, Address address) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            throw new ResourceNotFoundException("Customer not found with id " + customerId);
        }

        Customer customer = customerOpt.get();
        address.setCustomer(customer);
        Address savedAddress = addressRepository.save(address);

        logger.info("Address with id {} added for customer with id {}", savedAddress.getId(), customerId);

        return savedAddress;
    }

    public void deleteAddress(Long customerId, Long addressId) {
        Optional<Address> addressOpt = addressRepository.findById(addressId);
        if (addressOpt.isEmpty() || !addressOpt.get().getCustomer().getId().equals(customerId)) {
            throw new ResourceNotFoundException("Address not found or does not belong to the customer");
        }

        addressRepository.deleteById(addressId);
        logger.info("Address with id {} deleted for customer with id {}", addressId, customerId);
    }
}
