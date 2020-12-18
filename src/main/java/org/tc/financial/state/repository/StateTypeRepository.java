package org.tc.financial.state.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.tc.financial.state.domain.StateType;

public interface StateTypeRepository extends CrudRepository<StateType, UUID>{

	List<StateType> findByActiveTrueOrderByLabelAsc();
}
