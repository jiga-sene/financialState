package org.tc.financial.state.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tc.financial.state.domain.Customer;
import org.tc.financial.state.dto.Poste;
import org.tc.financial.state.dto.PosteGroup;
import org.tc.financial.state.dto.State;
import org.tc.financial.state.dto.StateType;
import org.tc.financial.state.repository.StateRepository;

import com.google.gson.Gson;

@Service
public class StateService {

	private static final String CUSTOMERID_NAME = "customerID";

	@Autowired
	private TemplateService templateService;
	@Autowired
	private DataJsonService jsonService;
	@Autowired
	private StateTypeService stateTypeService;
	@Autowired
	private CustomerService customerService;

	@Autowired
	private StateRepository stateRepository;

	public State getStateTemplate(String stateID) {
		State state = null;

		try {
			File file = templateService.retrieveDataFileByName(stateID);
			state = jsonService.retriveDataToPoste(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return state;
	}

	public State PrepareDefaultStateDTO(final State state, List<String> years) {

		if (state == null)
			return null;

		IntStream.range(0, state.getPosteGroups().size()).forEach(indexPG -> {
			state.getPosteGroups().forEach(posteGroup -> {
				IntStream.range(0, posteGroup.getPostes().size()).forEach(indexPO -> {
					posteGroup.getPostes().forEach(poste -> {
						years.forEach(year -> {
							state.getResourceValues().forEach(resource -> {
								poste.setNote("");
								poste.getValues().put(indexPG + " _ " + indexPO + " _ " + resource + " _ " + year,
										null);
							});
						});
					});
				});
			});
		});

		return state;
	}

	public Map<String, org.tc.financial.state.domain.State> prepareFinancialStatesToSave(Map<String, Object> values,
			StateType stateType) {

		String customerID = String.valueOf(values.getOrDefault(CUSTOMERID_NAME, ""));
		values.remove(CUSTOMERID_NAME);

		State stateDTO = this.getStateTemplate(stateType.getCode());

		List<String> years = this.getYearsInValues(values);
		Customer customer = customerService.getCustomerAndSaveIfNotExist(customerID);

		Map<String, org.tc.financial.state.domain.State> statesToSave = new HashMap<>(3);
		years.forEach(year -> {
			statesToSave.putIfAbsent(year, new org.tc.financial.state.domain.State(year, "", customerID, "", customer,
					stateTypeService.getStateType(stateType.getUuid())));
		});

		Gson gson = new Gson();
		List<Integer> containsFormuleIndex = new ArrayList<>();

		for (Map.Entry<String, org.tc.financial.state.domain.State> stateToSave : statesToSave.entrySet()) {
			State stateDTOTMP = stateDTO;
			for (int statePG = 0; statePG < stateDTOTMP.getPosteGroups().size(); statePG++) {
				PosteGroup posteGroup = stateDTOTMP.getPosteGroups().get(statePG);
				final int iStatePG = statePG;
				for (int statePO = 0; statePO < posteGroup.getPostes().size(); statePO++) {
					Poste poste = posteGroup.getPostes().get(statePO);
					final int iStatePO = statePO;
					poste.setNote(String.valueOf(values.getOrDefault(iStatePG + "_" + iStatePO + "_note", "")));
					stateDTOTMP.getResourceValues().forEach(resource -> {
						String key = iStatePG + "_" + iStatePO + "_" + resource + "_" + stateToSave.getKey();
						final int value = Propriete.tryParseInt(String.valueOf(values.getOrDefault(key, "")));
						poste.getValues().put(resource, value);

						// Calculation of the subtotal or total
						if (posteGroup.getFormule().trim().isEmpty()) {
							final int valueToAdd = poste.getSigne().trim().equals("-") ? -value : value;
							posteGroup.getValues().put(resource,
									Integer.sum(posteGroup.getValues().get(resource), valueToAdd));
						} else
							containsFormuleIndex.add(iStatePG);
					});
				}
			}
			stateDTOTMP = formulaCalculation(stateDTOTMP, containsFormuleIndex);
			stateToSave.getValue().setData(gson.toJson(stateDTOTMP));
		}
		saveFinancialStates(statesToSave);
		return statesToSave;
	}

	private State formulaCalculation(State stateDTO, List<Integer> containsFormuleIndex) {

		// Formula Calculation
		containsFormuleIndex.forEach(statePG -> {
			String formule = stateDTO.getPosteGroups().get(statePG).getFormule();
			stateDTO.getResourceValues().forEach(resource -> {
				stateDTO.getPosteGroups().forEach(posteGroup -> {
					if (formule.contains(posteGroup.getIndex()))
						stateDTO.getPosteGroups().get(statePG).getValues().put(resource,
								Integer.sum(stateDTO.getPosteGroups().get(statePG).getValues().get(resource),
										posteGroup.getValues().get(resource)));
					posteGroup.getPostes().forEach(poste -> {
						if (formule.contains(poste.getIndex()))
							stateDTO.getPosteGroups().get(statePG).getValues().put(resource,
									Integer.sum(stateDTO.getPosteGroups().get(statePG).getValues().get(resource),
											poste.getValues().get(resource)));
					});
				});
			});
		});
		return stateDTO;
	}

	private Map<String, org.tc.financial.state.domain.State> saveFinancialStates(
			Map<String, org.tc.financial.state.domain.State> statesToSave) {

		if (statesToSave == null || statesToSave.size() < 1)
			return null;

		statesToSave.forEach((year, stateToSave) -> {
			List<org.tc.financial.state.domain.State> states = stateRepository
					.findAllByYearAndMonthAndCustomerCodeAndStateTypeCode(stateToSave.getYear(), stateToSave.getMonth(),
							stateToSave.getCustomer().getCode(), stateToSave.getStateType().getCode());

			if (states == null || states.size() < 1)
				stateRepository.save(stateToSave);
			else {
				stateRepository.save(stateToSave);
				stateToSave.setUuid(states.get(0).getUuid());
			}
		});
		return statesToSave;
	}

	public State prepareStateDTO(State state, String customerCode, StateType stateType, List<String> years) {

		if (customerCode == null || stateType == null)
			return null;

		List<org.tc.financial.state.domain.State> states = getStatesOfCustomer(customerCode, stateType.getCode(),
				years);

		Gson gson = new Gson();
		states.forEach(st -> {
			State stateDTO = gson.fromJson(st.getData(), State.class);
			for (int statePG = 0; statePG < stateDTO.getPosteGroups().size(); statePG++) {
				PosteGroup posteGroup = stateDTO.getPosteGroups().get(statePG);
				final int iStatePG = statePG;
				for (int statePO = 0; statePO < posteGroup.getPostes().size(); statePO++) {
					Poste poste = posteGroup.getPostes().get(statePO);
					final int iStatePO = statePO;
					state.getPosteGroups().get(statePG).getPostes().get(statePO)
							.setNote(stateDTO.getPosteGroups().get(statePG).getPostes().get(statePO).getNote());
					poste.getValues().forEach((resource, value) -> {
						state.getPosteGroups().get(iStatePG).getPostes().get(iStatePO).getValues()
								.put(iStatePG + "_" + iStatePO + "_" + resource + "_" + st.getYear(), value);
					});
				}
			}
		});

		return state;
	}

	private List<org.tc.financial.state.domain.State> getStatesOfCustomer(String customerCode, String stateTypeCode,
			List<String> years) {

		if (customerCode == null || stateTypeCode == null)
			return null;

		return years == null
				? stateRepository.findAllByCustomerCodeAndStateTypeCodeOrderByYearDesc(customerCode, stateTypeCode)
				: stateRepository.findAllByCustomerCodeAndStateTypeCodeAndYearInOrderByYearDesc(customerCode,
						stateTypeCode, years);
	}

	private List<String> getYearsInValues(Map<String, Object> values) {

		List<String> years = new ArrayList<>();
		for (Map.Entry<String, Object> entry : values.entrySet()) {
			String year = entry.getKey().split("_").length == 4 ? entry.getKey().split("_")[3] : null;
			if (!years.contains(year) && years.size() < 3 && year != null) {
				years.add(year);
				if (years.size() == 3)
					return years;
			}
		}
		return null;
	}
}
