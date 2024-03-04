package org.tc.financial.state.controller;

import java.io.FileNotFoundException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.tc.financial.state.dto.State;
import org.tc.financial.state.dto.StateType;
import org.tc.financial.state.service.Propriete;
import org.tc.financial.state.service.RatioService;
import org.tc.financial.state.service.StateService;
import org.tc.financial.state.service.StateTypeService;

@Controller
public class IndexController {

	private static final String CUSTOMERID_NAME = "customerID";

	@Autowired
	private StateTypeService stateTypeService;
	@Autowired
	private StateService stateService;
	@Autowired
	private RatioService ratioService;

	@GetMapping
	public String getIndex(@RequestParam(required = false, name = "customer") String customerID, Model model) {

		model.addAttribute("application.title", Propriete.title);
		model.addAttribute("statesType", stateTypeService.getStatesType());
		model.addAttribute("customerID", customerID);
		return "index";
	}

	@GetMapping("/state/{customerID}")
	public ResponseEntity<Object> stateFinancialAddedInLastThreeYears(@PathVariable("customerID") String customerID) {

		if (customerID == null || customerID.trim().isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Customer not specified");

		List<String> financialStates = stateService.getAllFinancialStateAddedByCustomer(customerID);
		if (financialStates == null || financialStates.size() < 1)
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No financial state added");
		return ResponseEntity.status(HttpStatus.OK).body(financialStates);
	}

	@GetMapping("/state/{stateID}/{customerID}")
	public String stateFinancier(@PathVariable("stateID") String stateID, @PathVariable("customerID") String customerID,
			Model model) {

		List<StateType> statesType = stateTypeService.getStatesType();
		StateType stateType = stateTypeService.getStateType(statesType, stateID);

		model.addAttribute("application.title", Propriete.title);
		model.addAttribute("statesType", statesType);
		model.addAttribute("stateType", stateTypeService.getStateType(statesType, stateID));
		model.addAttribute("years", Propriete.getThreeLastYears());

		State state = stateService.prepareStateDTO(stateService
				.PrepareDefaultStateDTO(stateService.getStateTemplate(stateID), Propriete.getThreeLastYears()),
				customerID, stateType, Propriete.getThreeLastYears());
		model.addAttribute("state", state);
		model.addAttribute("customerID", customerID);
		return "index";
	}

	@PostMapping("/state/{stateCode}/{stateID}")
	public ModelAndView saveFinancialState(@PathVariable("stateID") UUID stateID,
			@PathVariable("stateCode") String stateCode, @RequestBody Map<String, Object> values,
			ModelAndView modelView) throws IOException {

		if (values == null || values.size() <= 0) {
			modelView.setStatus(HttpStatus.NO_CONTENT);
			modelView.addObject("error_message", "Data is empty.");
			return modelView;
		}

		if (stateID == null) {
			modelView.setStatus(HttpStatus.NOT_FOUND);
			modelView.addObject("error_message", "State identifiant is invalid.");
			return modelView;
		}

		StateType stateType = stateTypeService.getStateTypeDTO(stateID);
		if (stateType == null) {
			modelView.setStatus(HttpStatus.NOT_FOUND);
			modelView.addObject("error_message", "State identifiant is invalid.");
			return modelView;
		}

		String customerID = values.getOrDefault(CUSTOMERID_NAME, "").toString();
		stateService.prepareFinancialStatesToSave(values, stateType);
		List<String> years = Propriete.getThreeLastYears();
		State state = stateService.prepareStateDTO(
				stateService.PrepareDefaultStateDTO(stateService.getStateTemplate(stateCode), years), customerID,
				stateType, Propriete.getThreeLastYears());

		modelView.addObject("state", state);
		modelView.addObject("years", years);
		modelView.setViewName("fragment/financial :: financial");
		modelView.setStatus(HttpStatus.CREATED);
		return modelView;
	}

	@GetMapping("/state/ratios/{customerID}")
	public String getRatios(@PathVariable("customerID") String customerID, Model model) throws FileNotFoundException {

		List<StateType> statesType = stateTypeService.getStatesType();
		List<String> years = Propriete.getThreeLastYears();

		model.addAttribute("application.title", Propriete.title);
		model.addAttribute("statesType", statesType);
		model.addAttribute("customerID", customerID);
		model.addAttribute("ratios", ratioService.getAllRatios(customerID, years));
		model.addAttribute("years", years);

		return "ratios";
	}
}
