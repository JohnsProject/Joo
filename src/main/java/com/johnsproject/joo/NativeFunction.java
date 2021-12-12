package com.johnsproject.joo;

import java.util.ArrayList;
import java.util.List;

public class NativeFunction {
	
	private String name;
	private final char byteCodeName;
	private List<Parameter> parameters;
	
	public NativeFunction(String name, char byteCodeName) {
		this.name = name;
		this.byteCodeName = byteCodeName;
		parameters = new ArrayList<>();
	}

	public String getName() {
		return name;
	}
	
	public char getByteCodeName() {
		return byteCodeName;
	}
	
	public int getParameterCount() {
		return parameters.size();
	}
	
	public void addParameterType(int parameterIndex, String type) {
		if(parameterIndex >= parameters.size()) {
			parameters.add(new Parameter());
		}
		parameters.get(parameterIndex).addSupportedType(type);
	}

	public List<String> getParameterTypes(int parameterIndex) {
		if(parameterIndex >= parameters.size()) {
			parameters.add(new Parameter());
		}
		return parameters.get(parameterIndex).getSupportedTypes();
	}
	
	private static class Parameter {
		
		private List<String> supportedTypes;
		
		public Parameter() {
			supportedTypes = new ArrayList<>();			
		}

		public void addSupportedType(String type) {
			supportedTypes.add(type);
		}
		
		public List<String> getSupportedTypes() {
			return supportedTypes;
		}
	}
}
