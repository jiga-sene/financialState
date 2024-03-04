package org.tc.financial.state.service;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tc.financial.state.domain.State;
import org.tc.financial.state.dto.Ratio;
import org.tc.financial.state.repository.StateRepository;

import com.google.gson.Gson;

@Service
public class RatioService {

	@Autowired
	private TemplateService templateService;
	@Autowired
	private DataJsonService jsonService;

	@Autowired
	private StateRepository stateRepository;

	public List<Ratio> getRatioTemplate() throws FileNotFoundException {

		return jsonService.getRationInTemplate(templateService.retrieveDataFileByName(Propriete.RATIO_FILE_NAME));
	}

	public List<String> getAllStateTypesUsedInRatio(final List<Ratio> ratios) {

		if (ratios == null || ratios.size() <= 0)
			return null;

		Set<String> stateTypes = new LinkedHashSet<String>();
		ratios.forEach(ratio -> {
			stateTypes.add(ratio.getStateFirst().getStateType());
			stateTypes.add(ratio.getStateSecond().getStateType());
			stateTypes.add(ratio.getStateThird().getStateType());
		});
		stateTypes.remove("");
		return List.copyOf(stateTypes);
	}

	private Map<String, Set<String>> getAllStatesInRatio(final List<Ratio> ratios, final List<String> stateTypes) {

		if (stateTypes == null || stateTypes.size() <= 0)
			return null;

		Map<String, Set<String>> states = new HashMap<>();
		stateTypes.forEach(stateType -> {
			states.put(stateType, new LinkedHashSet<>());
		});
		ratios.forEach(ratio -> {
			Arrays.asList(ratio.getStateFirst(), ratio.getStateSecond(), ratio.getStateThird()).forEach(state -> {
				if (!state.getStateType().trim().isEmpty() && !state.getLabel().trim().isEmpty())
					states.get(state.getStateType()).add(state.getLabel().trim());
			});
		});
		return states;
	}

	private String calculateValueOfRatio(final Ratio ratio, final Map<String, Integer> values) {

		float result = 0f;
		result = ratio.getStateSecond().getStateType().isEmpty()
				? values.getOrDefault(ratio.getStateFirst().getLabel(), 0)
				: UtilService.calcul(values.getOrDefault(ratio.getStateFirst().getLabel(), 0),
						values.getOrDefault(ratio.getStateSecond().getLabel(), 0), ratio.getFirstOperator());

		if (!ratio.getSecondOperator().isEmpty())
			result = ratio.getStateThird().getStateType().isEmpty()
					? UtilService.calcul(result, ratio.getStateThird().getValue(), ratio.getSecondOperator())
					: UtilService.calcul(result, values.getOrDefault(ratio.getStateThird().getLabel(), 0),
							ratio.getSecondOperator());
		return Propriete.getFloatDigitTwo(result);
	}

	private List<State> getAllStatesConcernedByRatio(String customerCode, List<String> statesType, List<String> years) {

		if (statesType == null || statesType.size() <= 0 || years == null || years.size() <= 0)
			return null;
		return stateRepository.findAllByCustomerCodeAndStateTypeCodeInAndYearInOrderByYearDescStateTypeCodeAsc(
				customerCode, statesType, years);
	}

	private Map<String, Map<String, org.tc.financial.state.dto.State>> retrieveStatesToMap(List<State> states) {

		if (states == null || states.size() <= 0)
			return null;

		Map<String, Map<String, org.tc.financial.state.dto.State>> statesMap = new HashMap<>();
		states.forEach(state -> statesMap.putIfAbsent(state.getYear(), new HashMap<>()));

		Gson gson = new Gson();
		states.forEach(state -> {
			statesMap.get(state.getYear()).putIfAbsent(state.getStateType().getCode(),
					gson.fromJson(state.getData(), org.tc.financial.state.dto.State.class));
		});
		return statesMap;
	}

	private Map<String, Map<String, Integer>> getAllPosteGroupAndPosteRequiredForRatio(
			Map<String, Map<String, org.tc.financial.state.dto.State>> statesMap) {

		Map<String, Map<String, Integer>> valuesByYear = new HashMap<String, Map<String, Integer>>();
		statesMap.forEach((year, stateMap) -> {
			valuesByYear.put(year, new HashMap<String, Integer>());
		});

		List<Ratio> ratios = null;
		try {
			ratios = getRatioTemplate();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (ratios == null)
			return null;

		Map<String, Set<String>> allStatesInRatio = getAllStatesInRatio(ratios, getAllStateTypesUsedInRatio(ratios));

		allStatesInRatio.forEach((stateType, states) -> {
			for (Map.Entry<String, Map<String, org.tc.financial.state.dto.State>> stateMap : statesMap.entrySet()) {
				if (stateMap.getValue().getOrDefault(stateType, null) != null)
					stateMap.getValue().get(stateType).getPosteGroups().forEach(posteGroup -> {
						if (states.contains(posteGroup.getLabel())) {
							valuesByYear.get(stateMap.getKey()).put(posteGroup.getLabel(),
									posteGroup.getValues().getOrDefault("Net", 0));
							posteGroup.getPostes().forEach(poste -> {
								if (states.contains(poste.getLabel())) {
									valuesByYear.get(stateMap.getKey()).put(poste.getLabel(),
											poste.getValues().getOrDefault("Net", 0));
								}
							});
						}
					});
			}
		});

		return valuesByYear;
	}

	public List<Ratio> getAllRatios(String customerCode, List<String> years) throws FileNotFoundException {

		List<Ratio> ratios = getRatioTemplate();

		Map<String, Map<String, Integer>> statesMap = getAllPosteGroupAndPosteRequiredForRatio(retrieveStatesToMap(
				getAllStatesConcernedByRatio(customerCode, getAllStateTypesUsedInRatio(ratios), years)));

		ratios.forEach(ratio -> {
			years.forEach(year -> {
				ratio.getValues().put(year, calculateValueOfRatio(ratio, statesMap.get(year)));
			});
		});
		return ratios;
	}
}
