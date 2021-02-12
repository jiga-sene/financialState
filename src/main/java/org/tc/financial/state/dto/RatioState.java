package org.tc.financial.state.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RatioState implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 7936385260575146521L;

	private String stateType;
	private String label;

	private Integer value;
}
