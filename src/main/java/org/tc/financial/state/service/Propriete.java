package org.tc.financial.state.service;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Propriete {

	public static String title;
	
	public static String APPLICATION_TITLE = "application.title";
	
	public static String BILAN_ACTIF_TITLE = "BILAN ACTIF";
	public static String BILAN_PASSIF_TITLE = "BILAN PASSIF";
	public static String COMPTE_RESULTAT_TITLE = "COMPTE DE RESULTAT";
	public static String FLUX_TRESORERIE_TITLE = " FLUX DE TRESORERIE";
	
	public static String RATIO_FILE_NAME = "ratio";

	@Value("${application.title}")
	public void setTitle(String value) {
		title = value;
	}
	
	public static List<String> getThreeLastYears() {

		List<String> years = new ArrayList<String>();

		years.add(Integer.toString(Year.now().getValue() - 3));
		years.add(Integer.toString(Year.now().getValue() - 2));
		years.add(Integer.toString(Year.now().getValue() - 1));

		return years;
	}
	
}
