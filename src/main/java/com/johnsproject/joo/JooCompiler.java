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

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.johnsproject.joo.util.FileUtil;

public class JooCompiler {

	public static final String KEYWORD_INCLUDE = "include";
	public static final String KEYWORD_IMPORT = "import";
	public static final String KEYWORD_DEFINE = "define";
	public static final String KEYWORD_IF = "if";
	public static final String KEYWORD_ELSE = "else";
	public static final String KEYWORD_IF_END = "endIf";
	public static final String KEYWORD_FUNCTION = "function";
	public static final String KEYWORD_FUNCTION_END = "endFunction";
	public static final String KEYWORD_FUNCTION_REPEAT = "repeatFunction";
	public static final String KEYWORD_FUNCTION_CALL = "call";
	public static final String KEYWORD_TRUE = "true";
	public static final String KEYWORD_FALSE = "false";
	public static final String KEYWORD_PARAMETER = ":";
	public static final String KEYWORD_COMMENT = "#";
	public static final String KEYWORD_ARRAY_START = "[";
	public static final String KEYWORD_ARRAY_END = "]";
	public static final String KEYWORD_CHAR = "'";
	public static final String KEYWORD_VARIABLE_ASSIGN = "=";

	public static final String TYPE_PARAMETER = "parameter";
	public static final String TYPE_FUNCTION = "function";
	public static final String TYPE_INT = "int";
	public static final String TYPE_FIXED = "fixed";
	public static final String TYPE_BOOL = "bool";
	public static final String TYPE_CHAR = "char";
	
	public static final String LINE_BREAK = "\n";
	
	public static final int FIXED_POINT = 255;
	
	public static final String PATH_COMPILER_CONFIG = "JooCompilerConfig.jcc";
	
	public static final String[] COMPILER_TYPES = new String[] {
			TYPE_INT,
			TYPE_FIXED,
			TYPE_BOOL,
			TYPE_CHAR,
			TYPE_INT + KEYWORD_ARRAY_START + KEYWORD_ARRAY_END,
			TYPE_FIXED + KEYWORD_ARRAY_START + KEYWORD_ARRAY_END,
			TYPE_BOOL + KEYWORD_ARRAY_START + KEYWORD_ARRAY_END,
			TYPE_CHAR + KEYWORD_ARRAY_START + KEYWORD_ARRAY_END,
	};
	
	public static final char[] VM_TYPES = new char[] {
			JooVirtualMachine.TYPE_INT,
			JooVirtualMachine.TYPE_FIXED,
			JooVirtualMachine.TYPE_BOOL,
			JooVirtualMachine.TYPE_CHAR,
			JooVirtualMachine.TYPE_ARRAY_INT,
			JooVirtualMachine.TYPE_ARRAY_FIXED,
			JooVirtualMachine.TYPE_ARRAY_BOOL,
			JooVirtualMachine.TYPE_ARRAY_CHAR,
	};
	
	private char name;
	private List<String> operators;
	private List<String> nativeFunctions;
	private Map<String, Variable>[] variables;
	private Map<String, Function> functions;
	
	public JooCompiler() {
		name = JooVirtualMachine.COMPONENTS_START;
	}
	
	public List<String> getOperators() {
		return operators;
	}

	public List<String> getNativeFunctions() {
		return nativeFunctions;
	}

	public Map<String, Variable>[] getVariables() {
		return variables;
	}

	public Map<String, Function> getFunctions() {
		return functions;
	}

	/**
	 * This method compiles the human readable joo code in the code string
	 * to joo virtual machine readable joo code.
	 * 
	 * @param path human readable joo code path.
	 * @return joo virtual machine readable joo code.
	 */
	public String compile(final String path) {
		name = JooVirtualMachine.COMPONENTS_START;
		String directoryPath = getDirectoryPath(path);
		String jooCode = FileUtil.read(path);
		jooCode = includeIncludes(directoryPath, jooCode);
		jooCode = replaceDefines(jooCode);
		operators = new ArrayList<>();
		nativeFunctions = new ArrayList<>();
		parseConfig(directoryPath, operators, nativeFunctions);
		final String[] codeLines = getLines(jooCode);		
		variables = parseVariables(codeLines);
		functions = parseFunctions(codeLines, operators, variables);
		String compiledJooCode = "";
		compiledJooCode = writeVariablesAndFunctions(compiledJooCode, variables, functions);
		compiledJooCode = writeFunctionsAndOperations(compiledJooCode, variables, functions, nativeFunctions);
		return compiledJooCode;
	}
	
	private String getDirectoryPath(String path) {
		String[] pathPieces = path.split("\\" + File.separator);
		String codeDirectoryPath = "";
		for (int i = 0; i < pathPieces.length - 1; i++) {
			codeDirectoryPath += pathPieces[i] + File.separator;
		}
		return codeDirectoryPath;
	}
	
	String includeIncludes(String directoryPath, String jooCode) {
		final String[] codeLines = getLines(jooCode);
		for (int i = 0; i < codeLines.length; i++) {
			if(codeLines[i].contains(KEYWORD_INCLUDE + " ")) {
				final String filePath = codeLines[i].replace(KEYWORD_INCLUDE + " ", "");
				directoryPath += filePath;
				if(FileUtil.fileExists(directoryPath)) {
					jooCode += FileUtil.read(directoryPath);
				} else {
					jooCode += FileUtil.read(filePath);
				}
				jooCode = jooCode.replace(codeLines[i], "");
			}
		}
		return jooCode;
	}
	
	String replaceDefines(String jooCode) {
		final String[] codeLines = getLines(jooCode);
		for (int i = 0; i < codeLines.length; i++) {
			if(codeLines[i].contains(KEYWORD_DEFINE + " ")) {
				final String[] defineData = codeLines[i].replace(" ", "").replace(KEYWORD_DEFINE, "").split(KEYWORD_VARIABLE_ASSIGN);
				jooCode = jooCode.replace(codeLines[i], "");
				jooCode = jooCode.replace(defineData[0], defineData[1]);
			}
		}
		return jooCode;
	}
	
	void parseConfig(String directoryPath, List<String> operators, List<String> nativeFunctions) {
		directoryPath += PATH_COMPILER_CONFIG;
		final String config;
		if(FileUtil.fileExists(directoryPath)) {
			config = FileUtil.read(directoryPath);
		} else {
			config = FileUtil.read(PATH_COMPILER_CONFIG);
		}
		final String[] configLines = getLines(config);
		int currentType = -1;
		for (int i = 0; i < configLines.length; i++) {
			if(configLines[i].contains(KEYWORD_COMMENT) || configLines[i].isEmpty()) {
				continue;
			}
			else if(configLines[i].equals("@OPERATORS")) {
				currentType = 0;
				continue;
			}
			else if(configLines[i].equals("@FUNCTIONS")) {
				currentType = 1;
				continue;
			}
			switch (currentType) {
			case 0:
				operators.add(configLines[i]);
				break;
			case 1:
				nativeFunctions.add(configLines[i]);
				break;
			}
		}
	}
	
	/**
	 * This method splits up the code string to a string array of code lines. 
	 * It's a new method be cause it's also used by unit tests.
	 * 
	 * @param code
	 * @return string array of code lines. 
	 */
	String[] getLines(final String code) {
		return code.replace("\r", "").split(LINE_BREAK);
	}
	
	/**
	 * This method parses functions of the given type in the joo code lines. 
	 * If a line with a declaration is found the functions is added to the map and the code 
	 * line is set to empty string to avoid conflict with other search methods.
	 * 
	 * @param codeLines
	 * @param operators
	 * @param variables
	 * @return map of functions that contains the function names as keys and the function objects as values.
	 */
	Map<String, Function> parseFunctions(String[] codeLines, final List<String> operators, final Map<String, Variable>[] variables) {
		Map<String, Function> functions = new LinkedHashMap<>();
		Function currentFunction = null;
		for (int i = 0; i < codeLines.length; i++) {
			String codeLine = codeLines[i];
			// whitespace ensures the function keyword isn't part of a bigger word
			if(codeLine.contains(KEYWORD_FUNCTION + " ")) {
				currentFunction = parseFunctionDeclaration(codeLine, functions);	
				codeLines[i] = "";
			}
			else if (codeLine.replace(" ", "").equals(KEYWORD_FUNCTION_END)) {
				currentFunction = null;	
				codeLines[i] = "";
			}
			if(currentFunction != null) {
				Operation operation = parseOperation(codeLine, operators, variables, currentFunction.getParameters());
				if(operation != null) {
					currentFunction.addOperation(operation);
				}
			}
		}
		return functions;
	}
		
	private Function parseFunctionDeclaration(String codeLine, Map<String, Function> functions) {
		String functionName = "";
		Map<String, String> parameters = new LinkedHashMap<>();
		if(codeLine.contains(KEYWORD_PARAMETER)) {
			final String[] functionData = codeLine.replace(KEYWORD_FUNCTION, "").split(KEYWORD_PARAMETER);
			// remove whitespaces, names shouldn't have whitespaces
			functionName = functionData[0].replace(" ", "");
			for (int i = 1; i < functionData.length; i++) {
				String functionParameterData = functionData[i];
				String parameterType = "";
				String parameterName = "";
				for (int j = 0; j < COMPILER_TYPES.length; j++) {
					// whitespace ensures the variable type isn't part of a bigger word
					if(functionParameterData.contains(COMPILER_TYPES[j] + " ")) {
						parameterType = "" + VM_TYPES[j];
						parameterName = functionParameterData.replace(COMPILER_TYPES[j], "").replace(" ", "");
						parameters.put(parameterName, parameterType);
					}
				}
			}
		} else {
			functionName = codeLine.replace(" ", "").replaceFirst(KEYWORD_FUNCTION, "");
		}
		Function result = new Function(name++, parameters);
		functions.put(functionName, result);
		return result;	
	}
	
	/**
	 * This method parses variables and arrays of all types in the joo code lines. 
	 * If a line with a declaration is found the variable is added to the map and the code 
	 * line is set to empty string to avoid conflict with other search methods.
	 * 
	 * @param codeLines
	 * @param variableType
	 * @return map of variables that contains the variable names as keys and the variable objects as values.
	 */
	@SuppressWarnings("unchecked")
	Map<String, Variable>[] parseVariables(String[] codeLines){
		return new Map[] {
				parseVariables(codeLines, TYPE_INT),
				parseVariables(codeLines, TYPE_FIXED),
				parseVariables(codeLines, TYPE_BOOL),
				parseVariables(codeLines, TYPE_CHAR),
				parseArrays(codeLines, TYPE_INT),
				parseArrays(codeLines, TYPE_FIXED),
				parseArrays(codeLines, TYPE_BOOL),
				parseArrays(codeLines, TYPE_CHAR),
		};
	}
	
	/**
	 * This method parses variables of the given type in the joo code lines. 
	 * If a line with a declaration is found the variable is added to the map and the code 
	 * line is set to empty string to avoid conflict with other search methods.
	 * 
	 * @param codeLines
	 * @param variableType
	 * @return map of variables that contains the variable names as keys and the variable objects as values.
	 */
	private Map<String, Variable> parseVariables(String[] codeLines, final String variableType) {
		Map<String, Variable> variables = new LinkedHashMap<>();
		for (int i = 0; i < codeLines.length; i++) {
			String codeLine = codeLines[i];
			// whitespace ensures the variable type isn't part of a bigger word
			// function declaration also contain type declaration but should not be parsed here
			if(codeLine.contains(variableType + " ") && !codeLine.contains(KEYWORD_FUNCTION)) {
				// remove whitespaces, name and value shouldn't have whitespaces
				codeLine = codeLine.replace(" ", "");
				String variableName = "";
				String variableValue = "";
				if(codeLine.contains(KEYWORD_VARIABLE_ASSIGN)) {
					final String[] variableData = codeLine.replaceFirst(variableType, "").split(KEYWORD_VARIABLE_ASSIGN);
					variableName = variableData[0];
					if(variableType.equals(TYPE_INT))
						variableValue = variableData[1];
					else if(variableType.equals(TYPE_FIXED))
						variableValue = "" + Math.round(Float.parseFloat(variableData[1]) * FIXED_POINT);
					else if(variableType.equals(TYPE_BOOL))
						variableValue = "" + (variableData[1].equals(KEYWORD_FALSE) ? 0 : 1);
					else if(variableType.equals(TYPE_CHAR))
						variableValue = "" + variableData[1].toCharArray()[1];
				} else {
					variableName = codeLine.replaceFirst(variableType, "");
				}	
				// name++ because names used in joo virtual machine are unique characters	
				variables.put(variableName, new Variable(name++, variableValue));
				codeLines[i] = "";
			}
		}
		return variables;
	}
	
	/**
	 * This method parses arrays of the given type in the joo code lines. 
	 * If a line with a declaration is found the array is added to the map and the code 
	 * line is set to empty string to avoid conflict with other search methods.
	 * 
	 * @param codeLines
	 * @param arrayType
	 * @return map of arrays that contains the arrays names as keys and the variable objects as values. 
	 * The value field of the Variable objects contains the size of the array.
	 */
	private Map<String, Variable> parseArrays(String[] codeLines, final String arrayType) {
		Map<String, Variable> variables = new LinkedHashMap<>();
		for (int i = 0; i < codeLines.length; i++) {
			// remove whitespaces, name and value shouldn't have whitespaces
			final String codeLine = codeLines[i].replace(" ", "");
			if(codeLine.contains(arrayType + KEYWORD_ARRAY_START) && !codeLine.contains(KEYWORD_FUNCTION)) {
				final String[] variableData = codeLine.replace(arrayType + KEYWORD_ARRAY_START, "").split(KEYWORD_ARRAY_END);
				// name++ because names used in joo virtual machine are unique characters	
				variables.put(variableData[1], new Variable(name++, "" + (char)Integer.parseInt(variableData[0])));			
				codeLines[i] = "";
			}
		}
		return variables;
	}
	
	/**
	 * This method tries to parse the operation in the given code line. 
	 * If it is a known operation the operation get's parsed into a Operation object that 
	 * is then returned, null is returned if it's a comment or unknown operation.
	 * 
	 * 
	 * @param codeLine
	 * @param operators
	 * @param variables
	 * @param parameters
	 * @return Operation object if operation can be parsed, null if it's a comment or unknown operation.
	 */
	private Operation parseOperation(String codeLine, final List<String> operators, final Map<String, Variable>[] variables, final Map<String, String> parameters) {		
		if(codeLine.contains(KEYWORD_COMMENT)) {
			return null;
		}
		Operation operation = new Operation();
		// whitespace ensures the keyword isn't part of a bigger word
		if(codeLine.contains(KEYWORD_FUNCTION_CALL + " ")) {
			parseFunctionCall(codeLine, operation);
		}
		else if(codeLine.contains(KEYWORD_IF + " ")) {
			codeLine = codeLine.replaceFirst(KEYWORD_IF, "");
			parseBinaryOperation(codeLine, variables, parameters, operators, operation);
			operation.isCondition(true);
		}
		else if(codeLine.contains(KEYWORD_ELSE)) {
			operation.setOperator(JooVirtualMachine.KEYWORD_ELSE);
		}
		else if(codeLine.contains(KEYWORD_IF_END)) {
			operation.setOperator(JooVirtualMachine.KEYWORD_IF);
		}
		else if(codeLine.contains(KEYWORD_FUNCTION_REPEAT)) {
			operation.setOperator(JooVirtualMachine.KEYWORD_FUNCTION_REPEAT);
		}
		else if(codeLine.contains(KEYWORD_FUNCTION_END)) {
			operation.setOperator(JooVirtualMachine.KEYWORD_FUNCTION);
		} else { // if all if's failed it means the operation is a variable operation
			parseBinaryOperation(codeLine, variables, parameters, operators, operation);
		}
		// if operator is null the operation could not be parsed
		if(operation.getOperator() == 0) {
			return null;
		}		
		return operation;
	}
	
	private void parseFunctionCall(String codeLine, Operation operation) {
		codeLine = codeLine.replace(" ", "").replaceFirst(KEYWORD_FUNCTION_CALL, "");
		if(codeLine.contains(KEYWORD_PARAMETER)) {
			String[] functionCallData = codeLine.split(KEYWORD_PARAMETER);
			operation.setVariable1Name(functionCallData[0]);
			// parsing in case of array with index as parameter will be done later
			for (int i = 1; i < functionCallData.length; i++) {
				operation.getParameters()[i-1] = functionCallData[i];
			}
		} else {
			operation.setVariable1Name(codeLine);
		}
		operation.setOperator(JooVirtualMachine.KEYWORD_FUNCTION_CALL);
	}
	
	/**
	 * This method parses a binary operation (operation like a + b) with the possible operators
	 * and fills the given operation object with the parsed data.
	 * 
	 * @param codeLine
	 * @param variables
	 * @param parameters
	 * @param operators
	 * @param operation
	 */
	private void parseBinaryOperation(String codeLine, final Map<String, Variable>[] variables, final Map<String, String> parameters,
																				List<String> operators, Operation operation) {
		String[] operationData = parseBinaryOperationVariablesAndOperator(codeLine, operators, operation);
		if(operationData != null) {
			if(operationData[0].contains(KEYWORD_ARRAY_START)) {
				String[] variableData = operationData[0].replace(KEYWORD_ARRAY_END, "").split("\\" + KEYWORD_ARRAY_START);
				operation.setVariable0Name(variableData[0]);
				operation.setVariable0ArrayIndex(variableData[1]);
			} else {
				operation.setVariable0Name(operationData[0]);
			}
			if(operationData[1].contains(KEYWORD_ARRAY_START)) {
				String[] variableData = operationData[1].replace(KEYWORD_ARRAY_END, "").split("\\" + KEYWORD_ARRAY_START);
				operation.setVariable1Name(variableData[0]);
				operation.setVariable1ArrayIndex(variableData[1]);
			} else {
				// part behind the operator may contain a value instead of variable
				parseBinaryOperationValue(operationData, variables, parameters, operation);
			}
		}
	}
	
	/**
	 * This method parses the operator used in the operation and and returns the variables before and 
	 * after the operator. It also sets the operator in the Operation object.
	 * 
	 * @param codeLine
	 * @param operators
	 * @param operation
	 * @return variables before and after the operator.
	 */
	private String[] parseBinaryOperationVariablesAndOperator(String codeLine, List<String> operators, Operation operation) {
		String[] operationData = null;
		for (int i = 0; i < operators.size(); i++) {
			if(codeLine.contains(" " + operators.get(i) + " ")) {
				operation.setOperator((char) (i + JooVirtualMachine.OPERATORS_START));
				String possibleOperator = operators.get(i);
				// some characters like '+' need a backslash in front of it
				possibleOperator = possibleOperator.replace("+", "\\+");
				possibleOperator = possibleOperator.replace("*", "\\*");
				possibleOperator = possibleOperator.replace("?", "\\?");
				operationData = codeLine.replace(" ", "").split(possibleOperator);
				break;
			}
		}
		return operationData;
	}
	
	/**
	 * This method parses the variable or value after the operator and sets it in the Operation object.
	 * 
	 * @param operationData
	 * @param variables
	 * @param parameters
	 * @param operation
	 */
	private void parseBinaryOperationValue(final String[] operationData, final Map<String, Variable>[] variables,
																			final Map<String, String> parameters, Operation operation) {
		if(operationData[1].contains(KEYWORD_CHAR)) {
			operation.setValue("" + operationData[1].toCharArray()[1]);
			operation.setValueType(TYPE_CHAR);
		} else {
			try {
				String variableName = operationData[0];
				if(variableName.contains(KEYWORD_ARRAY_START)){
					variableName = variableName.split("\\" + KEYWORD_ARRAY_START)[0];
				}
				boolean isIntParameter = false;
				if(parameters.containsKey(variableName)) {
					isIntParameter = parameters.get(variableName).equals("" + JooVirtualMachine.TYPE_INT)
									|| parameters.get(variableName).equals("" + JooVirtualMachine.TYPE_ARRAY_INT);
				}
				// if variable before operator is int
				if(variables[0].containsKey(variableName) || variables[4].containsKey(variableName) || isIntParameter) {
					operation.setValue("" + Integer.parseInt(operationData[1]));
					operation.setValueType(TYPE_INT);
				}
				else {
					operation.setValue("" + Math.round(Float.parseFloat(operationData[1]) * FIXED_POINT));
					operation.setValueType(TYPE_FIXED);
				}
			}
			catch(NumberFormatException notNumber) {
				if(operationData[1].equals(KEYWORD_TRUE) || operationData[1].equals(KEYWORD_FALSE)) {
					operation.setValue("" + (operationData[1].equals(KEYWORD_FALSE) ? 0 : 1));
					operation.setValueType(TYPE_BOOL);
				} else {
					operation.setVariable1Name(operationData[1]);
				}
			}
		}
	}
	
	/**
	 * This method writes the declaration of variables and arrays of all types and the function count
	 * in the joo code string in a way the joo virtual machine can read it.
	 * <br>
	 * The joo virtual machine can't understand code written by this method only, use the {@link #compile(String) compile} method 
	 * to generate joo virtual machine readable code.
	 * 
	 * @param code
	 * @param variables
	 * @param functions
	 * @return joo code string that contains variables and array declarations. 
	 */
	String writeVariablesAndFunctions(String code, final Map<String, Variable>[] variables, final Map<String, Function> functions) {
		for (int i = 0; i < variables.length; i++) {
			if(variables[i].size() > 0) {
				code += "" + VM_TYPES[i] + (char)variables[i].size() + JooVirtualMachine.LINE_BREAK;
				for (Variable variable : variables[i].values()) {
					String value = variable.getValue();
					if(i < 3) { // if value is number
						value = toVirtualMachineNumber(variable.getValue());
					}
					code += "" + variable.getName() + value + JooVirtualMachine.LINE_BREAK;
				}
			}
		}
		if(functions.size() > 0) {
			code += "" + JooVirtualMachine.TYPE_FUNCTION + (char)functions.size() + JooVirtualMachine.LINE_BREAK;
		}
		return code;
	}
	
	/**
	 * This method writes the function declaration and the function operations into the joo code string 
	 * in a way the joo virtual machine can read it.
	 * <br>
	 * The joo virtual machine can't understand code written by this method only, use the {@link #compile(String) compile} method 
	 * to generate joo virtual machine readable code.
	 * 
	 * @param code
	 * @param variables
	 * @param functions
	 * @param nativeFunctions
	 * @return
	 */
	String writeFunctionsAndOperations(String code, final Map<String, Variable>[] variables, final Map<String, Function> functions, List<String> nativeFunctions) {
		for (Function function : functions.values()) {
			// doesn't need the parameters in the function declaration, the parameters are already listed in function call
			code += "" + JooVirtualMachine.KEYWORD_FUNCTION + function.getName() + JooVirtualMachine.LINE_BREAK;
			for (Operation operation : function.getOperations()) {
				if(operation.isCondition()) {
					code += "" + JooVirtualMachine.KEYWORD_IF;
				}
				code = writeBinaryOperation(code, function.getParameters().keySet().toArray(new String[0]), variables, operation);
				if(operation.getOperator() == JooVirtualMachine.KEYWORD_FUNCTION_CALL) {
					if(functions.containsKey(operation.getVariable1Name())) {
						code += "" + functions.get(operation.getVariable1Name()).getName();
					} else {
						for (int i = 0; i < nativeFunctions.size(); i++) {
							if(nativeFunctions.get(i).equals(operation.getVariable1Name())) {
								code += "" + operation.getOperator() + (char)(i + JooVirtualMachine.NATIVE_FUNCTIONS_START);
							}
						}
					}
				}
				code = writeOperationParameters(code, variables, operation);
				code += "" + JooVirtualMachine.LINE_BREAK;
			}
		}
		return code;
	}

	private String writeBinaryOperation(String code, final String[] functionParameters, final Map<String, Variable>[] variables, Operation operation) {
		code += getVirtualMachineVariableName(operation.getVariable0Name(), functionParameters, variables);
		if(!operation.getVariable0ArrayIndex().isEmpty()) {
			// +1 because 0 is null character
			try {
				code += "" + (char)(Integer.parseInt(operation.getVariable0ArrayIndex()) + JooVirtualMachine.ARRAY_INDEXES_START);
			} catch (NumberFormatException e) {
				code += getVirtualMachineVariableName(operation.getVariable0ArrayIndex(), functionParameters, variables);				
			}
		}
		if(operation.getOperator() != JooVirtualMachine.KEYWORD_FUNCTION_CALL) {
			code += "" + operation.getOperator();
		}
		code += getVirtualMachineVariableName(operation.getVariable1Name(), functionParameters, variables);
		if(!operation.getVariable1ArrayIndex().isEmpty()) {
			try {
				code += "" + (char)(Integer.parseInt(operation.getVariable1ArrayIndex()) + JooVirtualMachine.ARRAY_INDEXES_START);
			} catch (NumberFormatException e) {
				code += getVirtualMachineVariableName(operation.getVariable1ArrayIndex(), functionParameters, variables);
			}
		}
		if(!operation.getValueType().isEmpty()) {
			if(operation.getValueType().equals(TYPE_CHAR)) {
				// put type char character in front of it so the virtual machine knows
				// the character is not a variable name
				code += JooVirtualMachine.TYPE_CHAR +  operation.getValue();
			} else {				
				code += toVirtualMachineNumber(operation.getValue());
			}
		}
		return code;
	}
	
	private String getVirtualMachineVariableName(String variableName, final String[] functionParameters, final Map<String, Variable>[] variables) {
		for (int i = 0; i < variables.length; i++) {
			if(variables[i].containsKey(variableName)) {
				return "" + variables[i].get(variableName).getName();
			}
		}
		for (int i = 0; i < functionParameters.length; i++) {
			if(functionParameters[i] != null) {
				if(functionParameters[i].equals(variableName)) {
					return "" + (char)(i + JooVirtualMachine.PARAMETERS_START);
				}
			}
		}
		return "";
	}
	
	/**
	 * This method writes the operation parameters into the joo code string if the operation
	 * is a function call.
	 * 
	 * @param code
	 * @param variables
	 * @param operation
	 * @return
	 */
	private String writeOperationParameters(String code, final Map<String, Variable>[] variables, Operation operation) {
		for (String parameter : operation.getParameters()) {
			if(parameter != null) {
				String parameterName = "";
				String parameterIndex = "";
				// parse if parameter is a array with index				
				if(parameter.contains(KEYWORD_ARRAY_START)) {
					String[] parameterData = parameter.replace(KEYWORD_ARRAY_END, "").split("\\" + KEYWORD_ARRAY_START);
					parameterName = parameterData[0];
					parameterIndex = "" + (char) (Integer.parseInt(parameterData[1]) + 1);
				} else {
					parameterName = parameter;
				}
				String newParameterName = "";
				for (int i = 0; i < variables.length; i++) {
					if(variables[i].containsKey(parameterName)) {
						newParameterName = "" + variables[i].get(parameterName).getName();
						break;
					}
				}
				code += "" + JooVirtualMachine.KEYWORD_PARAMETER + newParameterName + parameterIndex;
			}
		}
		return code;
	}
	
	private String toVirtualMachineNumber(String value) {
		// replace default number characters with other virtual machine ones
		for (int i = 0; i < 9; i++) {
			value = value.replace((char)('0' + i), (char)(JooVirtualMachine.NUMBER_0 + i));
		}
		return value;
	}
}