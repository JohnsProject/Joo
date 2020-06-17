package com.johnsproject.joo;

/**
 * The Variable class contains the data of a joo variable (like name and value).
 * <br>
 * If the variable is a array the value parameter is the array size.
 * 
 * @author John Ferraz Salomon
 *
 */
public class Variable {
	
	private final String name;
	private final String type;
	private final char byteCodeName;
	private final String value;
	
	public Variable(final String name, final String type, final char byteCodeName, final String value) {
		this.name = name;
		this.type = type;
		this.byteCodeName = byteCodeName;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public char getByteCodeName() {
		return byteCodeName;
	}

	public String getValue() {
		return value;
	}
}	
