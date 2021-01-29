package org.tc.financial.state.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.tc.financial.state.domain.State;

public interface StateRepository extends CrudRepository<State, UUID> {

	List<State> findAllByYearAndMonthAndCustomerCodeAndStateTypeCode(String year, String month, String customerCode,
			String stateTypeCode);

	List<State> findAllByCustomerCodeAndStateTypeCodeOrderByYearDesc(String customerCode, String stateTypeCode);

	List<State> findAllByCustomerCodeAndStateTypeCodeAndYearInOrderByYearDesc(String customerCode, String stateTypeCode,
			List<String> years);

	List<State> findAllByCustomerCodeAndYearInOrderByYearDesc(String customerCode, List<String> years);
}
