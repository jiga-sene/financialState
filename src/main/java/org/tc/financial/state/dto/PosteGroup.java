package org.tc.financial.state.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class PosteGroup implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5646501781689548688L;
	
	@NonNull
	private String label;
	private String note;
	private String index;
	
	private int order;
	
	@NonNull
	private List<Poste> postes;
	
	private HashMap<String, Integer> values;
	
	public PosteGroup() {
		this.values = new HashMap<>();
	}
}
