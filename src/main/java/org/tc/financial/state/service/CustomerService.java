package org.tc.financial.state.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tc.financial.state.domain.Customer;
import org.tc.financial.state.repository.CustomerRepository;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	public Customer getCustomer(String code) {
		if (code == null || code.isEmpty())
			return null;
		return customerRepository.findByCode(code);
	}

	public Customer getCustomerAndSaveIfNotExist(String code) {

		if (code == null || code.isEmpty())
			return null;

		Customer customer = getCustomer(code);
		if (customer != null)
			return customer;
		
		customer = new Customer(code, "");
		customer = customerRepository.save(customer);
		return customer;
	}
}
