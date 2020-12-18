package org.tc.financial.state.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.tc.financial.state.domain.Customer;

public interface CustomerRepository extends CrudRepository<Customer, UUID>{

	Customer findByCode(String code);

}
