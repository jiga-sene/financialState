package org.tc.financial.state.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class Poste implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -883164324677972292L;
	
	@NonNull
	private String label;
	private String note;
	private String index;
	
	private int order;
	
	@NonNull
	private Map<String, Integer> values; 
	
	public Poste () {
		this.values = new HashMap<>();
	}
}
