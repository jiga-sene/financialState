package org.tc.financial.state.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.tc.financial.state.dto.Ratio;
import org.tc.financial.state.dto.State;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

@Service
public class DataJsonService {

	Gson gson = new Gson();

	public State retriveDataToPoste(String data) {

		State state = gson.fromJson(data, State.class);
		return state;
	}

	public State retriveDataToPoste(File fileData) {
		State state;
		try {
			// state = gson.fromJson(new FileReader(fileData), State.class); // Charset.forName("Windows-1252")
			state = gson.fromJson(new InputStreamReader(new FileInputStream(fileData), StandardCharsets.UTF_8),
					State.class);
			return state;
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {

			e.printStackTrace();
		}
		return null;
	}

	public String retrievePosteToData(State state) {
		String data = gson.toJson(state);
		return data;
	}

	@SuppressWarnings("unchecked")
	public List<Ratio> getRationInTemplate(File ratioInFile) {

		List<Ratio> ratios;
		try {
			ratios = (ArrayList<Ratio>) gson.fromJson(
					new InputStreamReader(new FileInputStream(ratioInFile), Charset.forName("utf-8")),
					new TypeToken<List<Ratio>>() {
					}.getType());
			return ratios;
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}
}
