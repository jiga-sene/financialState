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
			statesToSave.putIfAbsent(year, new org.tc.financial.state.domain.State(year, "", "admin", "", customer,
					stateTypeService.getStateType(stateType.getUuid())));
		});

		Gson gson = new Gson();

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
						Integer valueTMP = UtilService.tryParseInt(String.valueOf(values.getOrDefault(key, "")));
						final int value = valueTMP == null ? 0 : valueTMP;
						poste.getValues().put(resource, value);
					});
				}
			}

			stateDTOTMP = subTotalCalculation(stateDTOTMP);
			stateToSave.getValue().setData(gson.toJson(stateDTOTMP));
			// break;
		}
		saveFinancialStates(statesToSave);
		return statesToSave;
	}

	private State subTotalCalculation(State stateDTO) {

		stateDTO.getPosteGroups().forEach(posteGroup -> {
			stateDTO.getResourceValues().forEach(resource -> {
				posteGroup.getValues().put(resource, 0);
			});
		});

		List<Integer> containsFormuleIndex = new ArrayList<>();

		for (int statePG = 0; statePG < stateDTO.getPosteGroups().size(); statePG++) {
			final int iStatePG = statePG;
			if (stateDTO.getPosteGroups().get(statePG).getFormule().trim().isEmpty())
				stateDTO.getPosteGroups().get(statePG).getPostes().forEach(poste -> {
					stateDTO.getResourceValues().forEach(resource -> {
						final int valueToAdd = (poste.getSigne() != null && poste.getSigne().trim().equals("-"))
								? -poste.getValues().get(resource)
								: poste.getValues().get(resource);
						stateDTO.getPosteGroups().get(iStatePG).getValues().put(resource, Integer
								.sum(stateDTO.getPosteGroups().get(iStatePG).getValues().get(resource), valueToAdd));
					});
				});
			else
				containsFormuleIndex.add(statePG);
		}
		return formulaCalculation(stateDTO, containsFormuleIndex);
	}

	private State formulaCalculation(State stateDTO, List<Integer> containsFormuleIndex) {

		// Reset all PosteGroups values
		containsFormuleIndex.forEach(statePG -> {
			stateDTO.getResourceValues().forEach(resource -> {
				stateDTO.getPosteGroups().get(statePG).getValues().put(resource, 0);
			});
		});

		// Formula Calculation
		containsFormuleIndex.forEach(statePG -> {
			String formule = stateDTO.getPosteGroups().get(statePG).getFormule();
			stateDTO.getResourceValues().forEach(resource -> {
				stateDTO.getPosteGroups().forEach(posteGroup -> {
					if (!posteGroup.getIndex().isEmpty() && formule.contains(posteGroup.getIndex())) {
						stateDTO.getPosteGroups().get(statePG).getValues().put(resource,
								Integer.sum(stateDTO.getPosteGroups().get(statePG).getValues().get(resource),
										posteGroup.getValues().get(resource)));
						posteGroup.getPostes().forEach(poste -> {
							if (!poste.getIndex().isEmpty() && formule.contains(poste.getIndex()))
								stateDTO.getPosteGroups().get(statePG).getValues().put(resource,
										Integer.sum(stateDTO.getPosteGroups().get(statePG).getValues().get(resource),
												poste.getValues().get(resource)));
						});
					}
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

			if (states == null || states.size() < 1) {
				stateRepository.save(stateToSave);
			} else {
				states.get(0).setData(stateToSave.getData());
				stateRepository.save(states.get(0));
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
			if (stateDTO != null) {
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
					state.getResourceValues().forEach(resource -> {
						state.getPosteGroups().get(iStatePG).getValues().put(resource + "_" + st.getYear(),
								posteGroup.getValues().get(resource));
					});
				}
			}
		});

		return state;
	}

	public List<String> getAllFinancialStateAddedByCustomer(String customerCode) {

		if (customerCode == null || customerCode.trim().isEmpty())
			return null;

		List<org.tc.financial.state.domain.State> states = stateRepository
				.findAllByCustomerCodeAndYearInOrderByYearDesc(customerCode, Propriete.getThreeLastYears());

		if (states == null || states.size() < 1)
			return null;

		final List<String> financialStates = new ArrayList<String>();
		states.forEach(state -> {
			if (!financialStates.contains(state.getStateType().getCode()))
				financialStates.add(state.getStateType().getCode());
		});
		return financialStates;
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
