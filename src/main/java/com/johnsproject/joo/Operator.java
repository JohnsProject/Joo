package com.johnsproject.joo;

import java.util.ArrayList;
import java.util.List;

public class Operator {

	private String name;
	private List<String> supportedTypes;
	
	public Operator(String name) {
		this.name = name;
		supportedTypes = new ArrayList<>();
	}

	public String getName() {
		return name;
	}
	
	public void addSupportedType(String type) {
		supportedTypes.add(type);
	}

	public List<String> getSupportedTypes() {
		return supportedTypes;
	}
}
