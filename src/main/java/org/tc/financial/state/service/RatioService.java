package org.tc.financial.state.service;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tc.financial.state.dto.Ratio;

@Service
public class RatioService {

	@Autowired
	private TemplateService templateService;
	@Autowired
	private DataJsonService jsonService;

	public List<Ratio> getRatioTemplate() throws FileNotFoundException {

		return jsonService.getRationInTemplate(templateService.retrieveDataFileByName(Propriete.RATIO_FILE_NAME));
	}

	public List<String> getAllStateTypesUsedInRatio(final List<Ratio> ratios) {

		if (ratios == null || ratios.size() > 0)
			return null;

		Set<String> stateTypes = new LinkedHashSet<String>();
		for (Ratio ratio : ratios) {
			stateTypes.add(ratio.getStateFirst().getStateType());
			stateTypes.add(ratio.getStateSecond().getStateType());
			stateTypes.add(ratio.getStateThird().getStateType());
		}
		stateTypes.remove("");
		return List.copyOf(stateTypes);
	}

	public HashMap<String, Set<String>> getAllStatesInRatio(final List<Ratio> ratios, final List<String> stateTypes) {

		if (stateTypes == null || stateTypes.size() > 0)
			return null;

		HashMap<String, Set<String>> states = new HashMap<>();
		stateTypes.forEach(stateType -> {
			states.put(stateType, new LinkedHashSet<>());
		});
		ratios.forEach(ratio -> {
			Arrays.asList(ratio.getStateFirst(), ratio.getStateFirst(), ratio.getStateFirst()).forEach(state -> {
				if (!state.getStateType().trim().isEmpty() && !state.getLabel().trim().isEmpty())
					states.get(state.getStateType()).add(state.getLabel().trim());

			});
		});
		return states;
	}

	public float calculateValueOfRatio(final Ratio ratio, final HashMap<String, Integer> values) {

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
		return result;
	}
}
