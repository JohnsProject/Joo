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
import java.util.Set;
import java.util.regex.Pattern;

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
	// used in joo compiler config only
	public static final String KEYWORD_TYPE_SEPARATOR = "|";

	public static final String TYPE_PARAMETER = "parameter";
	public static final String TYPE_FUNCTION = "function";
	public static final String TYPE_INT = "int";
	public static final String TYPE_FIXED = "fixed";
	public static final String TYPE_BOOL = "bool";
	public static final String TYPE_CHAR = "char";
	public static final String TYPE_ARRAY_INT = TYPE_INT + KEYWORD_ARRAY_START + KEYWORD_ARRAY_END;
	public static final String TYPE_ARRAY_FIXED = TYPE_FIXED + KEYWORD_ARRAY_START + KEYWORD_ARRAY_END;
	public static final String TYPE_ARRAY_BOOL = TYPE_BOOL + KEYWORD_ARRAY_START + KEYWORD_ARRAY_END;
	public static final String TYPE_ARRAY_CHAR = TYPE_CHAR + KEYWORD_ARRAY_START + KEYWORD_ARRAY_END;
	
	public static final String LINE_BREAK = "\n";
	
	public static final int FIXED_POINT = 255;
	
	public static final String PATH_COMPILER_CONFIG = "JooCompilerConfig.jcc";
	
	public static final String[] COMPILER_TYPES = new String[] {
			TYPE_INT,
			TYPE_FIXED,
			TYPE_BOOL,
			TYPE_CHAR,
			TYPE_ARRAY_INT,
			TYPE_ARRAY_FIXED,
			TYPE_ARRAY_BOOL,
			TYPE_ARRAY_CHAR,
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
	private List<Operator> operators;
	private List<NativeFunction> nativeFunctions;
	private Map<String, Variable>[] variables;
	private Map<String, Function> functions;
	
	public JooCompiler() {
		name = JooVirtualMachine.COMPONENTS_START;
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
		operators = new ArrayList<>();
		nativeFunctions = new ArrayList<>();
		parseConfig(directoryPath, operators, nativeFunctions);
		String code = FileUtil.read(path);
//		analyse(code, operators, nativeFunctions);
		code = includeIncludes(directoryPath, code);
		code = replaceDefines(code);
		final String[] codeLines = getLines(code);		
		variables = parseVariables(codeLines);
		functions = parseFunctions(codeLines, operators, variables);
		String compiledJooCode = "";
		compiledJooCode = writeVariablesAndFunctions(compiledJooCode, variables, functions);
		compiledJooCode = writeFunctionsAndInstructions(compiledJooCode, variables, functions, nativeFunctions);
		return compiledJooCode;
	}
	
	private String getDirectoryPath(String path) {
		String[] pathPieces = path.split(Pattern.quote(File.separator));
		String codeDirectoryPath = "";
		for (int i = 0; i < pathPieces.length - 1; i++) {
			codeDirectoryPath += pathPieces[i] + File.separator;
		}
		return codeDirectoryPath;
	}
	
	String includeIncludes(String directoryPath, String code) {
		final String[] codeLines = getLines(code);
		for (int i = 0; i < codeLines.length; i++) {
			if(codeLines[i].contains(KEYWORD_INCLUDE + " ")) {
				final String filePath = codeLines[i].replace(KEYWORD_INCLUDE + " ", "");
				directoryPath += filePath;
				if(FileUtil.fileExists(directoryPath)) {
					code += FileUtil.read(directoryPath);
				} else {
					code += FileUtil.read(filePath);
				}
				code = code.replace(codeLines[i], "");
			}
		}
		return code;
	}
	
	String replaceDefines(String code) {
		final String[] codeLines = getLines(code);
		for (int i = 0; i < codeLines.length; i++) {
			if(codeLines[i].contains(KEYWORD_DEFINE + " ")) {
				final String[] defineData = codeLines[i].replace(" ", "").replace(KEYWORD_DEFINE, "").split(KEYWORD_VARIABLE_ASSIGN);
				code = code.replace(codeLines[i], "");
				code = code.replace(defineData[0], defineData[1]);
			}
		}
		return code;
	}
	
	void parseConfig(String directoryPath, List<Operator> operators, List<NativeFunction> nativeFunctions) {
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
			String line = configLines[i].replace(" ", "");
			if(line.isEmpty() || line.contains(KEYWORD_COMMENT)) {
				continue;
			}
			else if(line.equals("@OPERATORS")) {
				currentType = 0;
			}
			else if(line.equals("@FUNCTIONS")) {
				currentType = 1;
			} else {
				switch (currentType) {
				case 0:				
					operators.add(parseOperator(line));		
					break;
				case 1:
					nativeFunctions.add(parseNativeFunction(line));
					break;
				}
			}
		}
	}
	
	private Operator parseOperator(String line) {
		if(line.contains(KEYWORD_TYPE_SEPARATOR)) {
			String[] operatorData = line.split(Pattern.quote(KEYWORD_TYPE_SEPARATOR));
			Operator operator = new Operator(operatorData[0]);
			for (int i = 1; i < operatorData.length; i++) {
				operator.addSupportedType(operatorData[i]);
			}
			return operator;
		} else {
			return new Operator(line);
		}
	}
	
	private NativeFunction parseNativeFunction(String line) {
		if(line.contains(KEYWORD_PARAMETER)) {
			String[] nativeFunctionData = line.split(KEYWORD_PARAMETER);
			NativeFunction nativeFunction = new NativeFunction(nativeFunctionData[0]);
			for (int i = 1; i < nativeFunctionData.length; i++) {
				parseParameter(nativeFunctionData[i], i - 1, nativeFunction);
			}
			return nativeFunction;				
		} else {
			return new NativeFunction(line);
		}
	}
	
	private void parseParameter(String parameter, int parameterIndex, NativeFunction nativeFunction) {
		if(parameter.contains(KEYWORD_TYPE_SEPARATOR)) {
			String[] parameterData = parameter.split(Pattern.quote(KEYWORD_TYPE_SEPARATOR));
			for (int i = 0; i < parameterData.length; i++) {
				nativeFunction.addParameterType(parameterIndex, parameterData[i]);
			}
		} else {
			nativeFunction.addParameterType(parameterIndex, parameter);
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
	
	void analyse(final String code, final List<Operator> operators, final List<NativeFunction> nativeFunctions) {
		List<String> warnings = new ArrayList<>();
		List<String> errors = new ArrayList<>();
		List<String> defineNames = new ArrayList<>();
		final String[] codeLines = getLines(code);
		for (int i = 0; i < codeLines.length; i++) {
			String line = codeLines[i];
			String errorData = "Line " + i + " : ";
			if(line.contains(KEYWORD_INCLUDE + " ")) {
				String filePath = line.replace(KEYWORD_INCLUDE + " ", "").replace(" ", "");
				if(!FileUtil.fileExists(filePath) && !FileUtil.isResource(filePath)) {
					errors.add(errorData + "Included file does not exist : " + filePath);
				}
			}
			else if(line.contains(KEYWORD_DEFINE + " ")) {
				line = line.replace(KEYWORD_DEFINE + " ", "");
				if(line.contains(KEYWORD_VARIABLE_ASSIGN)) {
					String defineName =  line.replace(" ", "").split(KEYWORD_VARIABLE_ASSIGN)[0];
					if(defineNames.contains(defineName)) {
						errors.add(errorData + "Duplicate constant name : " + defineName);
					} else {
						defineNames.add(defineName);
					}
				} else {
					errors.add(errorData + "Unassigned constant : " + line);					
				}
			}
		}
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
	Map<String, Function> parseFunctions(String[] codeLines, final List<Operator> operators, final Map<String, Variable>[] variables) {
		Map<String, Function> functions = new LinkedHashMap<>();
		Function currentFunction = null;
		for (int i = 0; i < codeLines.length; i++) {
			String codeLine = codeLines[i];
			if(codeLine.isEmpty() || codeLine.contains(KEYWORD_COMMENT)) {
				continue;
			}
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
				Instruction instruction = parseInstruction(codeLine, operators, variables, currentFunction.getParameters());
				if(instruction != null) {
					currentFunction.addInstruction(instruction);
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
						parameterType = "" + COMPILER_TYPES[j];
						parameterName = functionParameterData.replace(COMPILER_TYPES[j], "").replace(" ", "");
						parameters.put(parameterName, parameterType);
					}
				}
			}
		} else {
			functionName = codeLine.replace(" ", "").replaceFirst(KEYWORD_FUNCTION, "");
		}
		final Function result = new Function(functionName, name++, parameters);
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
			if(codeLine.isEmpty() || codeLine.contains(KEYWORD_COMMENT)) {
				continue;
			}
			// whitespace ensures the variable type isn't part of a bigger word
			// function declaration can also contain type declaration in parameter but should not be parsed here
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
				final Variable result = new Variable(variableName, variableType, name++, variableValue);
				variables.put(variableName, result);
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
			if(codeLines[i].isEmpty() || codeLines[i].contains(KEYWORD_COMMENT)) {
				continue;
			}
			// remove whitespaces, name and value shouldn't have whitespaces
			final String codeLine = codeLines[i].replace(" ", "");
			if(codeLine.contains(arrayType + KEYWORD_ARRAY_START) && !codeLine.contains(KEYWORD_FUNCTION)) {
				final String[] variableData = codeLine.replace(arrayType + KEYWORD_ARRAY_START, "").split(KEYWORD_ARRAY_END);
				// name++ because names used in joo virtual machine are unique characters	
				final Variable result = new Variable(variableData[1], arrayType, name++, variableData[0]);
				variables.put(variableData[1], result);			
				codeLines[i] = "";
			}
		}
		return variables;
	}
	
	/**
	 * This method tries to parse the instruction in the given code line. 
	 * If it's a known instruction it get's parsed into a instruction object that 
	 * is then returned, null is returned if it's a comment or unknown instruction.
	 * 
	 * 
	 * @param codeLine
	 * @param operators
	 * @param variables
	 * @param parameters
	 * @return Instruction object if instruction can be parsed, null if it's a comment or unknown instruction.
	 */
	private Instruction parseInstruction(String codeLine, final List<Operator> operators, final Map<String, Variable>[] variables, final Map<String, String> parameters) {		
		Instruction instruction = new Instruction();
		// whitespace ensures the keyword isn't part of a bigger word
		if(codeLine.contains(KEYWORD_FUNCTION_CALL + " ")) {
			parseFunctionCall(codeLine, instruction);
			instruction.isFunctionCall(true);
		}
		else if(codeLine.contains(KEYWORD_IF + " ")) {
			codeLine = codeLine.replaceFirst(KEYWORD_IF, "");
			parseBinaryOperation(codeLine, variables, parameters, operators, instruction);
			instruction.isCondition(true);
		}
		else if(codeLine.contains(KEYWORD_ELSE)) {
			instruction.setOperator(JooVirtualMachine.KEYWORD_ELSE);
		}
		else if(codeLine.contains(KEYWORD_IF_END)) {
			instruction.setOperator(JooVirtualMachine.KEYWORD_IF);
		}
		else if(codeLine.contains(KEYWORD_FUNCTION_REPEAT)) {
			instruction.setOperator(JooVirtualMachine.KEYWORD_FUNCTION_REPEAT);
		}
		else if(codeLine.contains(KEYWORD_FUNCTION_END)) {
			instruction.setOperator(JooVirtualMachine.KEYWORD_FUNCTION);
		} else { // if all if's failed it means the instruction is a variable operation
			parseBinaryOperation(codeLine, variables, parameters, operators, instruction);
		}
		if(!instruction.isFunctionCall() && (instruction.getOperator() == 0)) {
			return null;
		}		
		return instruction;
	}
	
	private void parseFunctionCall(String codeLine, Instruction instruction) {
		codeLine = codeLine.replace(" ", "").replaceFirst(KEYWORD_FUNCTION_CALL, "");
		if(codeLine.contains(KEYWORD_PARAMETER)) {
			String[] functionCallData = codeLine.split(KEYWORD_PARAMETER);
			instruction.setFunctionName(functionCallData[0]);
			// parsing in case of array with index as parameter will be done later
			for (int i = 1; i < functionCallData.length; i++) {
				instruction.addParameter(functionCallData[i]);
			}
		} else {
			instruction.setFunctionName(codeLine);
		}
	}
	
	/**
	 * This method parses a binary operation (operation like a + b) with the possible operators
	 * and fills the given instruction object with the parsed data.
	 * 
	 * @param codeLine
	 * @param variables
	 * @param parameters
	 * @param operators
	 * @param instruction
	 */
	private void parseBinaryOperation(String codeLine, final Map<String, Variable>[] variables, final Map<String, String> parameters,
																				List<Operator> operators, Instruction instruction) {
		String[] operationData = parseBinaryOperationVariablesAndOperator(codeLine, operators, instruction);
		if(operationData != null) {
			if(operationData[0].contains(KEYWORD_ARRAY_START)) {
				String[] variableData = operationData[0].replace(KEYWORD_ARRAY_END, "").split(Pattern.quote(KEYWORD_ARRAY_START));
				instruction.setVariable0Name(variableData[0]);
				instruction.hasVariable0(true);
				instruction.setVariable0ArrayIndex(variableData[1]);
				instruction.hasVariable0ArrayIndex(true);
			} else {
				instruction.setVariable0Name(operationData[0]);
				instruction.hasVariable0(true);
			}
			if(operationData[1].contains(KEYWORD_ARRAY_START)) {
				String[] variableData = operationData[1].replace(KEYWORD_ARRAY_END, "").split(Pattern.quote(KEYWORD_ARRAY_START));
				instruction.setVariable1Name(variableData[0]);
				instruction.hasVariable1(true);
				instruction.setVariable1ArrayIndex(variableData[1]);
				instruction.hasVariable1ArrayIndex(true);
			} else {
				// part behind the operator may contain a value instead of variable
				parseBinaryOperationValue(operationData, variables, parameters, instruction);
			}
		}
	}
	
	/**
	 * This method parses the operator used in the operation and and returns the variables before and 
	 * after the operator. It also sets the operator in the instruction object.
	 * 
	 * @param codeLine
	 * @param operators
	 * @param instruction
	 * @return variables before and after the operator.
	 */
	private String[] parseBinaryOperationVariablesAndOperator(String codeLine, List<Operator> operators, Instruction instruction) {
		String[] operationData = null;
		for (int i = 0; i < operators.size(); i++) {
			if(codeLine.contains(" " + operators.get(i).getName() + " ")) {
				instruction.setOperator((char) (i + JooVirtualMachine.OPERATORS_START));
				String possibleOperator = operators.get(i).getName();
				operationData = codeLine.replace(" ", "").split(Pattern.quote(possibleOperator));
				break;
			}
		}
		return operationData;
	}
	
	/**
	 * This method parses the variable or value after the operator and sets it in the instruction object.
	 * 
	 * @param operationData
	 * @param variables
	 * @param parameters
	 * @param instruction
	 */
	private void parseBinaryOperationValue(final String[] operationData, final Map<String, Variable>[] variables,
																			final Map<String, String> parameters, Instruction instruction) {
		if(operationData[1].contains(KEYWORD_CHAR)) {
			instruction.setValue("" + operationData[1].toCharArray()[1]);
			instruction.hasValue(true);
			instruction.setValueType(TYPE_CHAR);
		} else {
			try {
				String variableName = operationData[0];
				if(variableName.contains(KEYWORD_ARRAY_START)){
					variableName = variableName.split(Pattern.quote(KEYWORD_ARRAY_START))[0];
				}
				boolean isIntParameter = false;
				if(parameters.containsKey(variableName)) {
					isIntParameter = parameters.get(variableName).equals(TYPE_INT)
									|| parameters.get(variableName).equals(TYPE_ARRAY_INT);
				}
				// if variable before operator is int
				if(variables[0].containsKey(variableName) || variables[4].containsKey(variableName) || isIntParameter) {
					instruction.setValue("" + Integer.parseInt(operationData[1]));
					instruction.hasValue(true);
					instruction.setValueType(TYPE_INT);
				}
				else {
					instruction.setValue("" + Math.round(Float.parseFloat(operationData[1]) * FIXED_POINT));
					instruction.hasValue(true);
					instruction.setValueType(TYPE_FIXED);
				}
			}
			catch(NumberFormatException notNumber) {
				if(operationData[1].equals(KEYWORD_TRUE) || operationData[1].equals(KEYWORD_FALSE)) {
					instruction.setValue("" + (operationData[1].equals(KEYWORD_FALSE) ? 0 : 1));
					instruction.hasValue(true);
					instruction.setValueType(TYPE_BOOL);
				} else {
					instruction.setVariable1Name(operationData[1]);
					instruction.hasVariable1(true);
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
						value = toVirtualMachineNumber(value);
					} else if(i > 3) { // if value is array index
						value = "" + (char)Integer.parseInt(value);
					}
					code += "" + variable.getByteCodeName() + value + JooVirtualMachine.LINE_BREAK;
				}
			}
		}
		if(functions.size() > 0) {
			code += "" + JooVirtualMachine.TYPE_FUNCTION + (char)functions.size() + JooVirtualMachine.LINE_BREAK;
		}
		return code;
	}
	
	/**
	 * This method writes the function declaration and the function instructions into the joo code string 
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
	String writeFunctionsAndInstructions(String code, final Map<String, Variable>[] variables, final Map<String, Function> functions, final List<NativeFunction> nativeFunctions) {
		for (Function function : functions.values()) {
			// doesn't need the parameters in the function declaration, the parameters are already listed in function call
			code += "" + JooVirtualMachine.KEYWORD_FUNCTION + function.getByteCodeName() + JooVirtualMachine.LINE_BREAK;
			for (Instruction instruction : function.getInstructions()) {
				if(instruction.isCondition()) {
					code += "" + JooVirtualMachine.KEYWORD_IF;
					code = writeBinaryOperation(code, function.getParameters().keySet(), variables, instruction);
				}
				else if(instruction.isFunctionCall()) {
					if(functions.containsKey(instruction.getFunctionName())) {
						code += "" + functions.get(instruction.getFunctionName()).getByteCodeName();
					} else {
						for (int i = 0; i < nativeFunctions.size(); i++) {
							if(nativeFunctions.get(i).getName().equals(instruction.getFunctionName())) {
								code += "" + JooVirtualMachine.KEYWORD_FUNCTION_CALL + (char)(i + JooVirtualMachine.NATIVE_FUNCTIONS_START);
							}
						}
					}
				} else {
					code = writeBinaryOperation(code, function.getParameters().keySet(), variables, instruction);
				}
				code = writeInstructionParameters(code, variables, instruction);
				code += "" + JooVirtualMachine.LINE_BREAK;
			}
		}
		return code;
	}

	private String writeBinaryOperation(String code, final Set<String> parameters, final Map<String, Variable>[] variables, Instruction instruction) {
		if(instruction.hasVariable0()) {
			code += getVirtualMachineVariableName(instruction.getVariable0Name(), parameters, variables);
			if(instruction.hasVariable0ArrayIndex()) {
				try {
					code += "" + (char)(Integer.parseInt(instruction.getVariable0ArrayIndex()) + JooVirtualMachine.ARRAY_INDEXES_START);
				} catch (NumberFormatException e) {
					code += getVirtualMachineVariableName(instruction.getVariable0ArrayIndex(), parameters, variables);				
				}
			}
		}
		code += "" + instruction.getOperator();
		if(instruction.hasVariable1()) {
			code += getVirtualMachineVariableName(instruction.getVariable1Name(), parameters, variables);
			if(instruction.hasVariable1ArrayIndex()) {
				try {
					code += "" + (char)(Integer.parseInt(instruction.getVariable1ArrayIndex()) + JooVirtualMachine.ARRAY_INDEXES_START);
				} catch (NumberFormatException e) {
					code += getVirtualMachineVariableName(instruction.getVariable1ArrayIndex(), parameters, variables);
				}
			}
		}
		if(instruction.hasValue()) {
			if(instruction.getValueType().equals(TYPE_CHAR)) {
				// put type char character in front of it so the virtual machine knows
				// the character is not a variable name
				code += JooVirtualMachine.TYPE_CHAR +  instruction.getValue();
			} else {				
				code += toVirtualMachineNumber(instruction.getValue());
			}
		}
		return code;
	}
	
	private String getVirtualMachineVariableName(String variableName, final Set<String> parameters, final Map<String, Variable>[] variables) {
		for (int i = 0; i < variables.length; i++) {
			if(variables[i].containsKey(variableName)) {
				return "" + variables[i].get(variableName).getByteCodeName();
			}
		}
		int i = 0;
		for (String parameter : parameters) {
			if(parameter.equals(variableName)) {
				return "" + (char)(i + JooVirtualMachine.PARAMETERS_START);
			}
			i++;
		}
		return "";
	}
	
	/**
	 * This method writes the instruction parameters into the joo code string if the instruction
	 * is a function call.
	 * 
	 * @param code
	 * @param variables
	 * @param instruction
	 * @return
	 */
	private String writeInstructionParameters(String code, final Map<String, Variable>[] variables, Instruction instruction) {
		for (String parameter : instruction.getParameters()) {
			String parameterName = "";
			String parameterIndex = "";
			// parse if parameter is a array with index				
			if(parameter.contains(KEYWORD_ARRAY_START)) {
				String[] parameterData = parameter.replace(KEYWORD_ARRAY_END, "").split(Pattern.quote(KEYWORD_ARRAY_START));
				parameterName = parameterData[0];
				parameterIndex = "" + (char) (Integer.parseInt(parameterData[1]) + 1);
			} else {
				parameterName = parameter;
			}
			String newParameterName = "";
			for (int i = 0; i < variables.length; i++) {
				if(variables[i].containsKey(parameterName)) {
					newParameterName = "" + variables[i].get(parameterName).getByteCodeName();
					break;
				}
			}
			code += "" + JooVirtualMachine.KEYWORD_PARAMETER + newParameterName + parameterIndex;
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