package com.rikagu.accounts;

import java.util.List;

import com.rikagu.accounts.entity.customer.Customer;
import com.rikagu.accounts.entity.customer.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;

@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class AccountsApplicationTests {

    @Autowired
    CustomerRepository customerRepository;

    @Test
    @Commit
    void createCustomer() {
        // Create a new customer
        Customer customer = new Customer("John", "Doe");
        // Save the customer
        customerRepository.save(customer);

        // Find the customer by last name
        Customer foundCustomer = customerRepository.findByLastName("Doe").get(0);
        // Check if the customer is found
        assert (foundCustomer.getFirstName().equals("John"));
    }

	@Test
    @Commit
	void printAllCustomers() {
		// Create a new customer
        Customer customer = new Customer("John", "Doe");
        // Save the customer
        customerRepository.save(customer);

		// Print all customers
		List<Customer> customers = (List<Customer>) customerRepository.findAll();
		for (Customer customerA : customers) {
			System.out.println(customerA);
		}
	}
}
