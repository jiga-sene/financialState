package org.tc.financial.state.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Data
public class State implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1739666164805792648L;

	private String month;
	private String year;
	
	private int resources;
	
	private List<String> resourceValues;
	@NonNull
	private List<PosteGroup> posteGroups;
}
