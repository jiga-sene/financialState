package org.tc.financial.state.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.tc.financial.state.dto.State;
import org.tc.financial.state.dto.StateType;
import org.tc.financial.state.service.Propriete;
import org.tc.financial.state.service.StateService;
import org.tc.financial.state.service.StateTypeService;

@Controller
public class IndexController {

	@Autowired
	private StateTypeService stateTypeService;
	@Autowired
	private StateService stateService;

	@GetMapping
	public String getIndex(Model model) {

		model.addAttribute("application.title", Propriete.title);
		model.addAttribute("statesType", stateTypeService.getStatesType());
		return "index";
	}

	@GetMapping("/financial/{stateID}/{customerID}")
	public String stateFinancier(@PathVariable("stateID") String stateID, @PathVariable("customerID") String customerID,
			Model model) {

		List<StateType> statesType = stateTypeService.getStatesType();
		StateType stateType = stateTypeService.getStateType(statesType, stateID);

		model.addAttribute("application.title", Propriete.title);
		model.addAttribute("statesType", statesType);
		model.addAttribute("stateType", stateTypeService.getStateType(statesType, stateID));
		model.addAttribute("years", Propriete.getThreeLastYears());
		/*model.addAttribute("state", stateService.PrepareDefaultStateDTO(stateService.getStateTemplate(stateID),
				Propriete.getThreeLastYears()));
		*/
		State state = stateService.prepareStateDTO(stateService.PrepareDefaultStateDTO(stateService.getStateTemplate(stateID),
				Propriete.getThreeLastYears()), customerID, stateType, Propriete.getThreeLastYears());
		model.addAttribute("state", state);
		model.addAttribute("customerID", customerID);
		return "index";
	}

	@PostMapping("/financial/{stateCode}/{stateID}")
	public ResponseEntity<String> saveFinancialState(@PathVariable("stateID") UUID stateID,
			@PathVariable("stateCode") String stateCode, @RequestBody Map<String, Object> values) throws IOException {

		if (values == null || values.size() <= 0)
			return new ResponseEntity<String>("Data is empty.", HttpStatus.NO_CONTENT);

		if (stateID == null)
			return new ResponseEntity<String>("State identifiant is invalid.", HttpStatus.NOT_FOUND);

		StateType stateType = stateTypeService.getStateTypeDTO(stateID);
		if (stateType == null)
			return new ResponseEntity<String>("State identifiant is invalid.", HttpStatus.NOT_FOUND);

		stateService.prepareFinancialStatesToSave(values, stateType);

		return new ResponseEntity<>(HttpStatus.CREATED);
	}
}
