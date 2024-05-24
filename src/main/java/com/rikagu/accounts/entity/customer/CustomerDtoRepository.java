package com.rikagu.accounts.entity.customer;

import org.springframework.stereotype.Repository;

@Repository
public class CustomerDtoRepository {

    private final CustomerRepository customerRepository;

    public CustomerDtoRepository(final CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer save(CustomerSaveDto customer) {
        return customerRepository.save(new Customer(customer.firstName(), customer.lastName()));
    }
}
