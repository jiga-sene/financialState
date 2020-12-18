package org.tc.financial.state.dto;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StateType implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1868857378321615549L;

	private UUID uuid;
	
	private String code;
	
	private String description;
	
	private String icone;
	
	private String label;
	
	private String templateName;
	
	private boolean active;
}
