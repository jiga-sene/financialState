package org.tc.financial.state.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Ratio implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -2335775717599343687L;

	private String name;
	private String type;

	private RatioState stateFirst;
	private RatioState stateSecond;
	private RatioState stateThird;

	private String firstOperator;
	private String secondOperator;

	private Map<String, String> values;

	public Map<String, String> getValues() {
		if (values == null)
			values = new HashMap<>();
		return values;
	}
}
