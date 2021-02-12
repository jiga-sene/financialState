package org.tc.financial.state.service;

import org.springframework.stereotype.Service;

@Service
public class UtilService {

	public static float calcul(float valueOne, float valueTwo, String operator) {

		float result = 0;
		switch (operator) {
		case "+":
			result = valueOne + valueTwo;
			break;
		case "-":
			result = valueOne - valueTwo;
			break;
		case "*":
			result = valueOne * valueTwo;
			break;
		case "/":
			result = valueOne / valueTwo;
			break;
		default:
			break;
		}
		return result;
	}

	public static Integer tryParseInt(String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
