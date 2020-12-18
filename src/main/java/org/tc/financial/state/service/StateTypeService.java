package org.tc.financial.state.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tc.financial.state.dto.StateType;
import org.tc.financial.state.repository.StateTypeRepository;

@Service
public class StateTypeService {

	@Autowired
	private StateTypeRepository stateTypeRepository;

	public List<StateType> getStatesType() {

		List<StateType> stateTypes = new ArrayList<StateType>();
		stateTypeRepository.findByActiveTrueOrderByLabelAsc().forEach(stType -> {
			stateTypes.add(new StateType(stType.getUuid(), stType.getCode(), stType.getDescription(), stType.getIcone(),
					stType.getLabel(), stType.getTemplateName(), stType.isActive()));
		});
		return stateTypes;
	}

	public StateType getStateType(List<StateType> statesType, String stateID) {

		if (stateID == null)
			return null;

		if (statesType == null || statesType.size() < 1)
			statesType = getStatesType();

		for (StateType stateType : statesType) {
			if (stateType.getCode().equalsIgnoreCase(stateID))
				return stateType;
		}
		return null;
	}

	public StateType getStateTypeDTO(UUID stateID) {

		if (stateID == null)
			return null;
		StateType stateType = new StateType();
		stateTypeRepository.findById(stateID).ifPresent(stType -> {
			stateType.setUuid(stType.getUuid());
			stateType.setLabel(stType.getLabel());
			stateType.setCode(stType.getCode());
			stateType.setIcone(stType.getIcone());
			stateType.setDescription(stType.getDescription());
			stateType.setTemplateName(stType.getTemplateName());
		});
		return stateType;
	}

	public org.tc.financial.state.domain.StateType getStateType(UUID stateID) {

		if (stateID == null)
			return null;
		return stateTypeRepository.findById(stateID).orElse(null);
	}
}
