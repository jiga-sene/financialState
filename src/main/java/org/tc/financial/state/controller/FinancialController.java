package org.tc.financial.state.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.tc.financial.state.service.Propriete;

@Controller
@RequestMapping(path = "/financial")
public class FinancialController {

	private static String TITLE = "title";

	@GetMapping(path = "/index")
	public String index(Model model) {
		model.addAttribute(Propriete.APPLICATION_TITLE, Propriete.title);
		return "index";
	}

	@GetMapping(path = "/bilan-actif")
	public String bilanActif(Model model) {
		model.addAttribute(Propriete.APPLICATION_TITLE, Propriete.title);
		model.addAttribute(TITLE, Propriete.BILAN_ACTIF_TITLE);
		return "actif";
	}

	@GetMapping(path = "/bilan-passif")
	public String bilanPassif(Model model) {
		model.addAttribute(Propriete.APPLICATION_TITLE, Propriete.title);
		model.addAttribute(TITLE, Propriete.BILAN_PASSIF_TITLE);
		return "passif";
	}

	@GetMapping(path = "/compte-resultat")
	public String compteResultat(Model model) {
		model.addAttribute(Propriete.APPLICATION_TITLE, Propriete.title);
		model.addAttribute(TITLE, Propriete.COMPTE_RESULTAT_TITLE);
		return "compteResultat";
	}

	@GetMapping(path = "/flux-tresorerie")
	public String fluxTresorerie(Model model) {
		model.addAttribute(Propriete.APPLICATION_TITLE, Propriete.title);
		model.addAttribute(TITLE, Propriete.FLUX_TRESORERIE_TITLE);
		return "fluxTresorerie";
	}
}
