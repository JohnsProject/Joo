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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The Function class contains the data of a joo function (like name and parameters).
 * This class isn't private because unit tests need to access it.
 * 
 * @author John Ferraz Salomon
 *
 */
public class Function {

	private final String name;
	private final char byteCodeName;
	private Map<String, String> parameters;
	private List<Operation> operations;
	
	public Function(final String name, final char byteCodeName, final Map<String, String> parameters) {
		this.name = name;
		this.byteCodeName = byteCodeName;
		this.parameters = parameters;
		this.operations = new ArrayList<>();
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

	public void addOperation(Operation operation) {
		operations.add(operation);
	}
	
	public List<Operation> getOperations() {
		return operations;
	}
}