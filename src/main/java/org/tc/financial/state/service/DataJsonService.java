package org.tc.financial.state.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.tc.financial.state.dto.Ratio;
import org.tc.financial.state.dto.State;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

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
			// state = gson.fromJson(new FileReader(fileData), State.class);
			state = gson.fromJson(new InputStreamReader(new FileInputStream(fileData), Charset.forName("windows-1252")),
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
					new InputStreamReader(new FileInputStream(ratioInFile), Charset.forName("windows-1252")),
					ArrayList.class);
			return ratios;
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}
}
