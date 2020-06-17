package com.johnsproject.joo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The Function class contains the data of a joo function (like name and parameters).
 * 
 * @author John Ferraz Salomon
 *
 */
public class Function {

	private final String name;
	private final char byteCodeName;
	private Map<String, String> parameters;
	private List<Instruction> instructions;
	
	public Function(final String name, final char byteCodeName, final Map<String, String> parameters) {
		this.name = name;
		this.byteCodeName = byteCodeName;
		this.parameters = parameters;
		this.instructions = new ArrayList<>();
	}
	
	public String getName() {
		return name;
	}
	
	public char getByteCodeName() {
		return byteCodeName;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void addInstruction(Instruction instruction) {
		instructions.add(instruction);
	}
	
	public List<Instruction> getInstructions() {
		return instructions;
	}
}