package org.tc.financial.state.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tc.financial.state.dto.State;
import org.tc.financial.state.service.DataJsonService;
import org.tc.financial.state.service.TemplateService;

@RestController
@RequestMapping(path="/test")
public class TestRestController {

	@Autowired
	private TemplateService templateService;
	
	@Autowired
	private DataJsonService jsonService;

	@GetMapping(path="/file")
	public String test() throws FileNotFoundException {
		File file = templateService.retrieveDataFileByName("etatft");
		State state = jsonService.retriveDataToPoste(file);
		return state.getPosteGroups().get(0).getLabel();
	}
	
	@GetMapping(path="/map")
	public Map<String, Integer> map() {
		Map<String, Integer> map = new HashMap<>();
		
		map.put("id", 12);
		map.put("col", 154);
		map.put("pol", 65652);
		map.put("mds", 654);
		map.put("ret", 764);
		map.put("uhg", 875);
		
		return map;
	}
}
