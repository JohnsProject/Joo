/*
MIT License

Copyright (c) 2020 John´s Project

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package com.johnsproject.joo;

/**
 * The Operation class contains the data of a joo operation (like addition or multiplication).
 * This class isn't private because unit tests need to access it.
 * <br>
 * If the variables of the operation are don't have a array index the index should be an empty string.
 * <br>
 * If the operation is a function call the variable0 name should be an empty string,
 * the operator the function call keyword and the variable1 name the function name.
 * <br>
 * If the function call doesn't pass parameters the parameter names are null, the
 * parameter names array itself should always be a string array with length 6.
 * 
 * @author John Ferraz Salomon
 *
 */
public class Operation {
	
	private boolean condition;
	
	private String variable0Name;
	private String variable0ArrayIndex;
	
	private char operator;
	
	private String variable1Name;
	private String variable1ArrayIndex;
	
	private String value;
	private String valueType;
	
	private String[] parameters;

	public Operation() {
		this.condition = false;
		this.variable0Name = "";
		this.variable0ArrayIndex = "";
		this.operator = 0;
		this.variable1Name = "";
		this.variable1ArrayIndex = "";
		this.value = "";
		this.valueType = "";
		this.parameters = new String[6];
	}

	public void isCondition(boolean condition) {
		this.condition = condition;
	}

	public void setVariable0Name(String variable0Name) {
		this.variable0Name = variable0Name;
	}

	public void setVariable0ArrayIndex(String variable0ArrayIndex) {
		this.variable0ArrayIndex = variable0ArrayIndex;
	}

	public void setOperator(char operator) {
		this.operator = operator;
	}

	public void setVariable1Name(String variable1Name) {
		this.variable1Name = variable1Name;
	}

	public void setVariable1ArrayIndex(String variable1ArrayIndex) {
		this.variable1ArrayIndex = variable1ArrayIndex;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public boolean isCondition() {
		return condition;
	}
	
	public String getVariable0Name() {
		return variable0Name;
	}

	public String getVariable0ArrayIndex() {
		return variable0ArrayIndex;
	}

	public char getOperator() {
		return operator;
	}

	public String getVariable1Name() {
		return variable1Name;
	}

	public String getVariable1ArrayIndex() {
		return variable1ArrayIndex;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getValueType() {
		return valueType;
	}

	public String[] getParameters() {
		return parameters;
	}		
}
