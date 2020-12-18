package org.tc.financial.state;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.tc.financial.state.repository.StateTypeRepository;
import org.tc.financial.state.service.DataJsonService;
import org.tc.financial.state.service.TemplateService;

@SpringBootTest
@Sql({"/data.sql"})
class FinancialStateApplicationTests {

	@Autowired
	private StateTypeRepository stateTypeRepository;
	
	@Autowired
	private TemplateService templateService;
	
	@Autowired
	private DataJsonService dataJsonService;
	
	@Test
	void contextLoads() throws FileNotFoundException {
		assertEquals(4, stateTypeRepository.count());
		
		assertEquals(true, templateService.retrieveDataFileByName("ETATFT").exists());
		
		assertEquals("Trésorerie au 1er janvier", dataJsonService.retriveDataToPoste(templateService.retrieveDataFileByName("ETATFT")).getPosteGroups().get(0).getLabel());
	}

}
