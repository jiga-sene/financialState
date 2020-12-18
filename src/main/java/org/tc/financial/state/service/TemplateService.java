package org.tc.financial.state.service;

import java.io.File;
import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import lombok.NonNull;

@Service
public class TemplateService {

	@Value("${config.data.path}")
	private String pathBase;

	public File retrieveDataFileByName(@NonNull String stateCode) throws FileNotFoundException {
		String filename = stateCode.toLowerCase().concat(".json");
		return ResourceUtils.getFile("classpath:" + pathBase.concat(filename));
	}
}
