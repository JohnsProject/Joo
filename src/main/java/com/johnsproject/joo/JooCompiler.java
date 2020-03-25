package com.johnsproject.joo;

import java.io.File;
import java.util.HashMap;

import com.johnsproject.joo.util.FileUtil;

public class JooCompiler {

	public static final String CODE_ENDING = ".joo";
	public static final String BYTECODE_ENDING = ".cjoo";
	public static final String NATIVE_FUNCTIONS_FILE_NAME = "NativeFunctions.jnf";
	
	public static final String KEYWORD_INCLUDE = "include";
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

	public static final String TYPE_PARAMETER = "parameter";
	public static final String TYPE_INT = "int";
	public static final String TYPE_FIXED = "fixed";
	public static final String TYPE_BOOL = "bool";
	public static final String TYPE_CHAR = "char";
	
	public static final String TYPE_ARRAY_INT = "int[";
	public static final String TYPE_ARRAY_FIXED = "fixed[";
	public static final String TYPE_ARRAY_BOOL = "bool[";
	public static final String TYPE_ARRAY_CHAR = "char[";
	
	public static final String COMPARATOR_SMALLER = "<";
	public static final String COMPARATOR_BIGGER = ">";
	public static final String COMPARATOR_SMALLER_EQUALS = "<=";
	public static final String COMPARATOR_BIGGER_EQUALS = ">=";
	public static final String COMPARATOR_EQUALS = "==";
	public static final String COMPARATOR_NOT_EQUALS = "!=";
	
	public static final String OPERATOR_ADD = "+=";
	public static final String OPERATOR_SUBTRACT = "-=";
	public static final String OPERATOR_MULTIPLY = "*=";
	public static final String OPERATOR_DIVIDE = "/=";
	public static final String OPERATOR_SET_EQUALS = "=";
	
	public static final String LINE_BREAK = "\n";

	public static final int NATIVE_FUNCTIONS_START = 127;
	
	private static final int ARRAY_INDEX_INT = 0;
	private static final int ARRAY_INDEX_FIXED = 1;
	private static final int ARRAY_INDEX_BOOL = 2;
	private static final int ARRAY_INDEX_CHAR = 3;
	
	static class Variable {
		String name = "";
		String value = "";
	}

	private HashMap<String, String> functionNames;
	private HashMap<String, Variable> parameterNames;
	private HashMap<String, Variable>[] variables;
	private HashMap<String, Variable>[] arrayVariables;

	private String[] nativeFunctions;
	private char variableName;
	private char arrayName;
	private char functionName;
	private String bytecode;
	private String[] types;
	private String[] arrayTypes;
	private char[] vmTypes;
	private char[] vmArrayTypes;
	
	public JooCompiler() {
		bytecode = "";
		variableName = JooVirtualMachine.VARIABLES_START;
		arrayName = JooVirtualMachine.ARRAYS_START;
		functionName = JooVirtualMachine.FUNCTIONS_START;
		nativeFunctions = new String[0];
		functionNames = new HashMap<String, String>();
		parameterNames = new HashMap<String, Variable>();
		variables = new HashMap[4];
		variables[ARRAY_INDEX_INT] = new HashMap<String, Variable>();
		variables[ARRAY_INDEX_FIXED] = new HashMap<String, Variable>();
		variables[ARRAY_INDEX_BOOL] = new HashMap<String, Variable>();
		variables[ARRAY_INDEX_CHAR] = new HashMap<String, Variable>();
		arrayVariables = new HashMap[4];
		arrayVariables[ARRAY_INDEX_INT] = new HashMap<String, Variable>();
		arrayVariables[ARRAY_INDEX_FIXED] = new HashMap<String, Variable>();
		arrayVariables[ARRAY_INDEX_BOOL] = new HashMap<String, Variable>();
		arrayVariables[ARRAY_INDEX_CHAR] = new HashMap<String, Variable>();
		types = new String[4];
		types[ARRAY_INDEX_INT] = TYPE_INT;
		types[ARRAY_INDEX_FIXED] = TYPE_FIXED;
		types[ARRAY_INDEX_BOOL] = TYPE_BOOL;
		types[ARRAY_INDEX_CHAR] = TYPE_CHAR;
		arrayTypes = new String[4];
		arrayTypes[ARRAY_INDEX_INT] = TYPE_ARRAY_INT;
		arrayTypes[ARRAY_INDEX_FIXED] = TYPE_ARRAY_FIXED;
		arrayTypes[ARRAY_INDEX_BOOL] = TYPE_ARRAY_BOOL;
		arrayTypes[ARRAY_INDEX_CHAR] = TYPE_ARRAY_CHAR;
		vmTypes = new char[4];
		vmTypes[ARRAY_INDEX_INT] = JooVirtualMachine.TYPE_INT;
		vmTypes[ARRAY_INDEX_FIXED] = JooVirtualMachine.TYPE_FIXED;
		vmTypes[ARRAY_INDEX_BOOL] = JooVirtualMachine.TYPE_BOOL;
		vmTypes[ARRAY_INDEX_CHAR] = JooVirtualMachine.TYPE_CHAR;
		vmArrayTypes = new char[4];
		vmArrayTypes[ARRAY_INDEX_INT] = JooVirtualMachine.TYPE_ARRAY_INT;
		vmArrayTypes[ARRAY_INDEX_FIXED] = JooVirtualMachine.TYPE_ARRAY_FIXED;
		vmArrayTypes[ARRAY_INDEX_BOOL] = JooVirtualMachine.TYPE_ARRAY_BOOL;
		vmArrayTypes[ARRAY_INDEX_CHAR] = JooVirtualMachine.TYPE_ARRAY_CHAR;
	}
	
	// setter used by unit tests only
	void setNativeFunctions(String[] nativeFunctions) {
		this.nativeFunctions = nativeFunctions;
	}
	
	public int getVariableCount() {
		int size = 0;
		for (int i = 0; i < variables.length; i++) {
			size += variables[i].size();
		}
		return size;
	}
	
	public int getArrayCount() {
		int size = 0;
		for (int i = 0; i < arrayVariables.length; i++) {
			size += arrayVariables[i].size();
		}
		return size;
	}
	
	public int getArraysSize() {
		int size = 0;
		for (int i = 0; i < arrayVariables.length; i++) {
			for (Variable array : arrayVariables[i].values()) {
				size += Integer.parseInt(array.value);
			}
		}
		return size;
	}
	
	public int getFunctionCount() {
		return functionNames.size();
	}
	
	public String getBytecode() {
		return bytecode;
	}	
	
	public String compileProject(String jooCodePath) {
		String jooCodeDirectoryPath = getJooCodeDirectoryPath(jooCodePath);
		String jooNativeFunctions = loadNativeFunctions(jooCodeDirectoryPath);
		nativeFunctions = parseNativeFunctions(jooNativeFunctions);
		String rawJooCode = FileUtil.read(jooCodePath);
		rawJooCode = includeIncludedCode(rawJooCode, jooCodeDirectoryPath);
		bytecode = compile(rawJooCode);
		jooCodePath = jooCodePath.replace(CODE_ENDING, "") + BYTECODE_ENDING;
		FileUtil.write(jooCodePath, bytecode);
		return bytecode;
	}
	
	String getJooCodeDirectoryPath(String jooCodePath) {
		String[] jooCodePathParts = jooCodePath.split("\\" + File.separator);
		String jooCodeDirectoryPath = "";
		for (int i = 0; i < jooCodePathParts.length - 1; i++) {
			jooCodeDirectoryPath += jooCodePathParts[i] + File.separator;
		}
		return jooCodeDirectoryPath;
	}
	
	String loadNativeFunctions(String jooCodeDirectoryPath) {
		jooCodeDirectoryPath += NATIVE_FUNCTIONS_FILE_NAME;
		String jooNativeFunctions = FileUtil.read(jooCodeDirectoryPath);
		if(jooNativeFunctions.isEmpty()) {
			return FileUtil.readResource(NATIVE_FUNCTIONS_FILE_NAME);
		} else {
			return jooNativeFunctions;
		}
	}
	
	String[] parseNativeFunctions(String jooNativeFunctions) {
		String[] nativeFunctionsData = jooNativeFunctions.replace("\r", "").split("\n");
		String[] nativeFunctions = new String[nativeFunctionsData.length];
		for (int i = 0; i < nativeFunctionsData.length; i++) {
			nativeFunctions[i] = nativeFunctionsData[i];
		}
		return nativeFunctions;
	}
	
	String includeIncludedCode(String rawJooCode, String jooCodeDirectoryPath) {
		String fullJooCode = rawJooCode;
		if(rawJooCode.contains(KEYWORD_INCLUDE)) {
			final String[] jooCode = rawJooCode.split(LINE_BREAK);
			for (int i = 0; i < jooCode.length; i++) {
				if(jooCode[i].contains(KEYWORD_INCLUDE)) {
					String filePath = jooCodeDirectoryPath + jooCode[i].replace(" ", "").replace("\r", "").replace(KEYWORD_INCLUDE, "") + CODE_ENDING;
					fullJooCode += "\n" + FileUtil.read(filePath);
					fullJooCode = fullJooCode.replace(jooCode[i], "");
				}
			}
		}
		return fullJooCode;
	}
	
	String compile(String rawJooCode) {
		final String[] jooCode = rawJooCode.replace("\r", "").split(LINE_BREAK);
		resetVariables();
		searchKeywordsAndNames(jooCode);
		replaceKeywordsAndNames(jooCode);
		return formatCompiledCode(jooCode);
	}
	
	void resetVariables() {
		bytecode = "";
		variableName = JooVirtualMachine.VARIABLES_START;
		arrayName = JooVirtualMachine.ARRAYS_START;
		functionName = JooVirtualMachine.FUNCTIONS_START;
		functionNames.clear();
		parameterNames.clear();
		for (int i = 0; i < variables.length; i++) {
			variables[i].clear();
		}
		for (int i = 0; i < arrayVariables.length; i++) {
			arrayVariables[i].clear();
		}
	}
	
	void searchKeywordsAndNames(String[] jooCode) {
		for (int i = 0; i < jooCode.length; i++) {
			if(jooCode[i].contains(KEYWORD_COMMENT)) {
				continue;
			}
			// whitespace of strings should not be replaced
			if(jooCode[i].contains("call String")) {
				jooCode[i] = jooCode[i].replace("call String", "callString");
			} else {
				jooCode[i] = jooCode[i].replace(" ", "");
			}
			for (int j = 0; j < variables.length; j++) {
				searchVariables(jooCode, i, types[j], variables[j]);
			}
			for (int j = 0; j < arrayVariables.length; j++) {
				searchVariables(jooCode, i, arrayTypes[j], arrayVariables[j]);
			}
			searchParameters(jooCode, i, parameterNames);
			searchFunctions(jooCode, i, functionNames);	
		}
	}
	
	void replaceKeywordsAndNames(String[] jooCode) {
		for (int i = 0; i < jooCode.length; i++) {
			if(jooCode[i].contains(KEYWORD_COMMENT)) {
				continue;
			}
			for (int j = 0; j < variables.length; j++) {
				replaceVariables(jooCode, i, types[j], variables[j]);
			}
			for (int j = 0; j < arrayVariables.length; j++) {
				replaceVariables(jooCode, i, arrayTypes[j], arrayVariables[j]);
			}
			replaceVariables(jooCode, i, TYPE_PARAMETER, parameterNames);
			replaceFunctions(jooCode, i, functionNames);
			replaceIfs(jooCode, i);
			replaceOperators(jooCode, i);
		}
		replaceArrayKeywordsWithVariableIndex(jooCode, parameterNames);
		replaceArrayKeywordsWithVariableIndex(jooCode, variables[ARRAY_INDEX_INT]);
		replaceArrayKeywordsWithNumberIndex(jooCode);
	}
	
	String formatCompiledCode(String[] jooCode) {
		String compiledJooCode = "";
		// add declared variables
		for (int i = 0; i < variables.length; i++) {
			if(variables[i].size() == 0) {
				continue;
			}
			compiledJooCode += "" + vmTypes[i] + (char)variables[i].size() + JooVirtualMachine.LINE_BREAK;
			for (Variable variable : variables[i].values()) {
				variable.value = variable.value.replace(KEYWORD_TRUE, "1");
				variable.value = variable.value.replace(KEYWORD_FALSE, "0");
				compiledJooCode += "" + variable.name + variable.value + JooVirtualMachine.LINE_BREAK;
			}
		}
		// add declared arrays
		for (int i = 0; i < arrayVariables.length; i++) {
			if(arrayVariables[i].size() == 0) {
				continue;
			}
			compiledJooCode += "" + vmArrayTypes[i] + (char)arrayVariables[i].size() + JooVirtualMachine.LINE_BREAK;
			for (Variable variable : arrayVariables[i].values()) {
				compiledJooCode += "" + variable.name + (char)Integer.parseInt(variable.value) + JooVirtualMachine.LINE_BREAK;
			}
		}
		// add declared functions count
		compiledJooCode += "" + JooVirtualMachine.TYPE_FUNCTION + (char)functionNames.size() + JooVirtualMachine.LINE_BREAK;
		for (int i = 0; i < jooCode.length; i++) {
			if(jooCode[i].isEmpty() || jooCode[i].contains(KEYWORD_COMMENT)) {
				continue;
			}
			if(jooCode[i].equals("" + JooVirtualMachine.KEYWORD_FUNCTION)) {
				continue;
			}
			jooCode[i] = jooCode[i].replace(KEYWORD_TRUE, "1");
			jooCode[i] = jooCode[i].replace(KEYWORD_FALSE, "0");
			compiledJooCode += jooCode[i] + JooVirtualMachine.LINE_BREAK;
		}
		compiledJooCode += "" + JooVirtualMachine.KEYWORD_FUNCTION + JooVirtualMachine.LINE_BREAK;
		return compiledJooCode;
	}
	
	void replaceArrayKeywordsWithNumberIndex(String[] jooCode) {
		for (int i = 0; i < jooCode.length; i++) {
			if(jooCode[i].contains(KEYWORD_ARRAY_START)) {
				String[] arrays = jooCode[i].split("\\" + KEYWORD_ARRAY_START);
				String oldCode = arrays[0];
				String newCode = arrays[0];
				for (int j = 1; j < arrays.length; j++) {
					String[] arrayIndex = arrays[j].split(KEYWORD_ARRAY_END);
					try {
						oldCode += KEYWORD_ARRAY_START + arrays[j];
						newCode += (char)(Integer.parseInt(arrayIndex[0]) + JooVirtualMachine.ARRAY_INDICES_START);
						if(arrayIndex.length > 1) {
							newCode += arrayIndex[1].replace(KEYWORD_ARRAY_END, "");
						}
					} catch (NumberFormatException e) {}
				}
				jooCode[i] = jooCode[i].replace(oldCode, newCode);
			}
		}
	}
	
	void replaceArrayKeywordsWithVariableIndex(String[] jooCode, HashMap<String, Variable> variableMap) {
		for (int i = 0; i < jooCode.length; i++) {
			if(jooCode[i].contains(KEYWORD_ARRAY_START)) {
				String[] arrays = jooCode[i].split("\\" + KEYWORD_ARRAY_START);
				String oldCode = arrays[0];
				String newCode = arrays[0];
				for (int j = 1; j < arrays.length; j++) {
					String[] arrayIndex = arrays[j].split(KEYWORD_ARRAY_END);
					if(variableMap.containsKey(arrayIndex[0])) {
						oldCode += KEYWORD_ARRAY_START + arrays[j];
						newCode += variableMap.get(arrayIndex[0]).name;
						if(arrayIndex.length > 1) {
							newCode += arrayIndex[1].replace(KEYWORD_ARRAY_END, "");
						}
					}
				}
				jooCode[i] = jooCode[i].replace(oldCode, newCode);
			}
		}
	}
	
	void searchVariables(String[] jooCode, int i, String variableType, HashMap<String, Variable> variableMap) {
		if(jooCode[i].length() < variableType.length()) {
			return;
		}
		String type = jooCode[i].substring(0, variableType.length());
		if((jooCode[i].length() > variableType.length() + 1) && (type + KEYWORD_ARRAY_START).equals(jooCode[i].substring(0, variableType.length() + 1))) {
			return;
		}
		if(type.equals(variableType)) {
			if(jooCode[i].contains(KEYWORD_ARRAY_START)) {
				Variable variable = new Variable();
				String jooLine = jooCode[i].replace(variableType, "").replace(KEYWORD_ARRAY_START, "");
				String[] lineData = jooLine.split(KEYWORD_ARRAY_END);
				variable.name = "" + (arrayName++);
				variable.value = lineData[0];
				variableMap.put(lineData[1], variable);
			} else {
				Variable variable = new Variable();
				variable.name = "" + (variableName++);
				variable.value = "";
				String jooLine = jooCode[i].replaceFirst(variableType, "");
				String oldVariableName = jooLine;
				if(jooLine.contains(OPERATOR_SET_EQUALS)) {
					String[] lineData = jooLine.split(OPERATOR_SET_EQUALS);
					oldVariableName = lineData[0];
					variable.value = lineData[1];
				}
				variableMap.put(oldVariableName, variable);
			}
		}
	}
	
	void searchParameters(String[] jooCode, int i, HashMap<String, Variable> parameterMap) {
		if(jooCode[i].length() < KEYWORD_FUNCTION.length()) {
			return;
		}
		String keyword = jooCode[i].substring(0, KEYWORD_FUNCTION.length());
		if(keyword.equals(KEYWORD_FUNCTION)) {
			if(jooCode[i].contains(KEYWORD_PARAMETER)) {
				String[] parameters = jooCode[i].split(KEYWORD_PARAMETER);
				for (int j = 1; j < parameters.length; j++) {
					Variable variable = new Variable();
					variable.name = "" + (char)((j - 1) + JooVirtualMachine.PARAMETERS_START);
					variable.value = "";
					parameterMap.put(parameters[j], variable);
				}
			}
		}
	}
	
	void replaceVariables(String[] jooCode, int i, String variableType, HashMap<String, Variable> variableMap) {
		String keywordIf = "";
		String keywordFuction = "";
		String keywordFunctionCall = "";
		String type = "";
		if(jooCode[i].length() > KEYWORD_IF.length()) {
			keywordIf = jooCode[i].substring(0, KEYWORD_IF.length());
		}
		if(jooCode[i].length() > KEYWORD_FUNCTION.length()) {
			keywordFuction = jooCode[i].substring(0, KEYWORD_FUNCTION.length());
		}
		if(jooCode[i].length() > KEYWORD_FUNCTION_CALL.length()) {
			keywordFunctionCall = jooCode[i].substring(0, KEYWORD_FUNCTION_CALL.length());
		}
		if(jooCode[i].length() > variableType.length()) {
			type = jooCode[i].substring(0, variableType.length());
		}
		if(type.equals(variableType)) {
			jooCode[i] = "";
		}
		else if(keywordFuction.equals(KEYWORD_FUNCTION) && variableType.equals(TYPE_PARAMETER)) {
			replaceParameterName(jooCode, i, variableType, variableMap);			
		}
		else if(keywordFunctionCall.equals(KEYWORD_FUNCTION_CALL)) {
			replaceParameterName(jooCode, i, variableType, variableMap);
		}
		else if(keywordIf.equals(KEYWORD_IF)) {
			boolean replaced = false;
			replaced = replaceVariableName(jooCode, i, replaced, COMPARATOR_EQUALS, variableMap);
			replaced = replaceVariableName(jooCode, i, replaced, COMPARATOR_NOT_EQUALS, variableMap);
			replaced = replaceVariableName(jooCode, i, replaced, COMPARATOR_BIGGER_EQUALS, variableMap);
			replaced = replaceVariableName(jooCode, i, replaced, COMPARATOR_SMALLER_EQUALS, variableMap);
			replaced = replaceVariableName(jooCode, i, replaced, COMPARATOR_BIGGER, variableMap);
			replaced = replaceVariableName(jooCode, i, replaced, COMPARATOR_SMALLER, variableMap);
		} else {
			boolean replaced = false;
			replaced = replaceVariableName(jooCode, i, replaced, OPERATOR_SET_EQUALS, variableMap);
		}
	}
	
	void replaceParameterName(String[] jooCode, int i, String variableType, HashMap<String, Variable> variableMap) {
		if(jooCode[i].contains(KEYWORD_PARAMETER)) {
			String[] parameters = jooCode[i].split(KEYWORD_PARAMETER);
			String oldString = "";
			String newString = "";
			for (int j = 1; j < parameters.length; j++) {
				String parameterData = parameters[j];
				String parameterName = parameterData;
				String arrayIndex = "";
				if(parameterData.contains(KEYWORD_ARRAY_START)) {
					String[] slitData = parameterData.split("\\" + KEYWORD_ARRAY_START);
					parameterName = slitData[0];
					arrayIndex = KEYWORD_ARRAY_START + slitData[1];
				}
				if(variableMap.containsKey(parameterName)) {
					oldString += KEYWORD_PARAMETER + parameterData;
					newString += KEYWORD_PARAMETER + variableMap.get(parameterName).name + arrayIndex;
				} else if (jooCode[i].contains(KEYWORD_FUNCTION_CALL) && parameterName.equals("String")){
					// replace string function name so vm knows the string buffer should be used
					oldString += KEYWORD_PARAMETER + parameterData;
					newString += KEYWORD_PARAMETER + JooVirtualMachine.FUNCTION_STRING;
				}
			}
			jooCode[i] = jooCode[i].replace(oldString, newString);
		}
	}
	
	boolean replaceVariableName(String[] jooCode, int i, boolean replaced, String operator, HashMap<String, Variable> variableMap) {
		if(!replaced) {
			if(jooCode[i].contains(operator)) {
				String[] lineData = jooCode[i].split(operator);
				lineData[0] = lineData[0].replace(KEYWORD_IF, "");
				lineData[0] = lineData[0].replace(OPERATOR_ADD.replace(OPERATOR_SET_EQUALS, ""), "");
				lineData[0] = lineData[0].replace(OPERATOR_SUBTRACT.replace(OPERATOR_SET_EQUALS, ""), "");
				lineData[0] = lineData[0].replace(OPERATOR_MULTIPLY.replace(OPERATOR_SET_EQUALS, ""), "");
				lineData[0] = lineData[0].replace(OPERATOR_DIVIDE.replace(OPERATOR_SET_EQUALS, ""), "");
				if(lineData[0].contains(KEYWORD_ARRAY_START)) {
					lineData[0] = lineData[0].split("\\" + KEYWORD_ARRAY_START)[0];
				}
				if(lineData[1].contains(KEYWORD_ARRAY_START)) {
					lineData[1] = lineData[1].split("\\" + KEYWORD_ARRAY_START)[0];
				}
				if(variableMap.containsKey(lineData[0])) {
					jooCode[i] = jooCode[i].replace(lineData[0], variableMap.get(lineData[0]).name);
				}
				if(variableMap.containsKey(lineData[1])) {
					jooCode[i] = jooCode[i].replace(lineData[1], variableMap.get(lineData[1]).name);
				}
				return true;
			}
			return false;
		}
		return true;
	}
	
	void searchFunctions(String[] jooCode, int i, HashMap<String, String> functionMap) {
		if(jooCode[i].equals(KEYWORD_FUNCTION_END) || jooCode[i].equals(KEYWORD_FUNCTION_REPEAT)) {
			return;
		}
		if(jooCode[i].length() < KEYWORD_FUNCTION.length()) {
			return;
		}
		String keyword = jooCode[i].substring(0, KEYWORD_FUNCTION.length());
		if(keyword.equals(KEYWORD_FUNCTION)) {
			String oldFunctionName = jooCode[i].replace(KEYWORD_FUNCTION, "");
			if(oldFunctionName.contains(KEYWORD_PARAMETER)) {
				oldFunctionName = oldFunctionName.split(KEYWORD_PARAMETER)[0];
			}
			String newFunctionName = "" + (functionName++);
			functionMap.put(oldFunctionName, newFunctionName);
		}
	}
	
	void replaceFunctions(String[] jooCode, int i, HashMap<String, String> functionMap) {
		String keywordFuction = "";
		String keywordFunctionCall = "";
		if(jooCode[i].length() > KEYWORD_FUNCTION.length()) {
			keywordFuction = jooCode[i].substring(0, KEYWORD_FUNCTION.length());
		}
		if(jooCode[i].length() > KEYWORD_FUNCTION_CALL.length()) {
			keywordFunctionCall = jooCode[i].substring(0, KEYWORD_FUNCTION_CALL.length());
		}
		if(jooCode[i].equals(KEYWORD_FUNCTION_END)) {
			jooCode[i] = jooCode[i].replace(KEYWORD_FUNCTION_END,  "" + JooVirtualMachine.KEYWORD_FUNCTION);
		}
		else if(jooCode[i].equals(KEYWORD_FUNCTION_REPEAT)) {
			jooCode[i] = jooCode[i].replace(KEYWORD_FUNCTION_REPEAT,  "" + JooVirtualMachine.KEYWORD_FUNCTION_REPEAT);
		}
		else if(keywordFuction.equals(KEYWORD_FUNCTION)) {
			String functionName = jooCode[i];
			if(functionName.contains(KEYWORD_PARAMETER)) {
				functionName = functionName.split(KEYWORD_PARAMETER)[0];
				jooCode[i] = jooCode[i].replace(KEYWORD_PARAMETER, "" + JooVirtualMachine.KEYWORD_PARAMETER);
			}
			functionName = functionName.replace(KEYWORD_FUNCTION, "");
			jooCode[i] = jooCode[i].replace(KEYWORD_FUNCTION,  "" + JooVirtualMachine.KEYWORD_FUNCTION);
			jooCode[i] = jooCode[i].replaceFirst(KEYWORD_PARAMETER, "");
			if(!replaceNativeFunctions(jooCode, i, functionName)) {
				jooCode[i] = jooCode[i].replace(functionName, functionMap.get(functionName));
			}
		}
		else if(keywordFunctionCall.equals(KEYWORD_FUNCTION_CALL)) {
			String functionName = jooCode[i];
			if(functionName.contains(KEYWORD_PARAMETER)) {
				functionName = functionName.split(KEYWORD_PARAMETER)[0];
				jooCode[i] = jooCode[i].replace(KEYWORD_PARAMETER, "" + JooVirtualMachine.KEYWORD_PARAMETER);
			}
			functionName = functionName.replace(KEYWORD_FUNCTION_CALL, "");
			jooCode[i] = jooCode[i].replace(KEYWORD_FUNCTION_CALL,  "" + JooVirtualMachine.KEYWORD_FUNCTION_CALL);
			jooCode[i] = jooCode[i].replaceFirst(KEYWORD_PARAMETER, "");
			if(!replaceNativeFunctions(jooCode, i, functionName)) {
				jooCode[i] = jooCode[i].replace(functionName, functionMap.get(functionName));
			}
		}
	}
	
	boolean replaceNativeFunctions(String[] jooCode, int i, String functionName) {
		for (int j = 0; j < nativeFunctions.length; j++) {
			if(functionName.equals(nativeFunctions[j])) {
				jooCode[i] = jooCode[i].replace(functionName, "" + (char) (NATIVE_FUNCTIONS_START - j));
				return true;
			}
		}
		return false;
	}	
	
	void replaceOperators(String[] jooCode, int i) {
		if(jooCode[i].contains(OPERATOR_ADD)) {
			jooCode[i] = jooCode[i].replace(OPERATOR_ADD, "" + JooVirtualMachine.ADD);
		}
		else if(jooCode[i].contains(OPERATOR_SUBTRACT)) {
			jooCode[i] = jooCode[i].replace(OPERATOR_SUBTRACT, "" + JooVirtualMachine.SUBTRACT);
		}
		else if(jooCode[i].contains(OPERATOR_MULTIPLY)) {
			jooCode[i] = jooCode[i].replace(OPERATOR_MULTIPLY, "" + JooVirtualMachine.MULTIPLY);
		}
		else if(jooCode[i].contains(OPERATOR_DIVIDE)) {
			jooCode[i] = jooCode[i].replace(OPERATOR_DIVIDE, "" + JooVirtualMachine.DIVIDE);
		}
		else if(jooCode[i].contains(OPERATOR_SET_EQUALS)) {
			jooCode[i] = jooCode[i].replace(OPERATOR_SET_EQUALS, "" + JooVirtualMachine.EQUALS);
		}
	}

	void replaceIfs(String[] jooCode, int i) {
		String keywordIf = "";
		if(jooCode[i].length() > KEYWORD_IF.length()) {
			keywordIf = jooCode[i].substring(0, KEYWORD_IF.length());
		}
		if(jooCode[i].equals(KEYWORD_ELSE)) {
			jooCode[i] = jooCode[i].replace(KEYWORD_ELSE, "" + JooVirtualMachine.KEYWORD_ELSE);
		}
		if(jooCode[i].equals(KEYWORD_IF_END)) {
			jooCode[i] = jooCode[i].replace(KEYWORD_IF_END, "" + JooVirtualMachine.KEYWORD_IF);
		}
		else if (keywordIf.equals(KEYWORD_IF)){
			if(jooCode[i].contains(COMPARATOR_EQUALS)) {
				jooCode[i] = jooCode[i].replace(COMPARATOR_EQUALS, "" + JooVirtualMachine.EQUALS);
			}
			else if(jooCode[i].contains(COMPARATOR_NOT_EQUALS)) {
				jooCode[i] = jooCode[i].replace(COMPARATOR_NOT_EQUALS, "" + JooVirtualMachine.NOT_EQUALS);
			}
			else if(jooCode[i].contains(COMPARATOR_BIGGER_EQUALS)) {
				jooCode[i] = jooCode[i].replace(COMPARATOR_BIGGER_EQUALS, "" + JooVirtualMachine.BIGGER + JooVirtualMachine.EQUALS);
			}
			else if(jooCode[i].contains(COMPARATOR_SMALLER_EQUALS)) {
				jooCode[i] = jooCode[i].replace(COMPARATOR_SMALLER_EQUALS, "" + JooVirtualMachine.SMALLER + JooVirtualMachine.EQUALS);
			}
			else if(jooCode[i].contains(COMPARATOR_SMALLER)) {
				jooCode[i] = jooCode[i].replace(COMPARATOR_SMALLER, "" + JooVirtualMachine.SMALLER);
			}
			else if(jooCode[i].contains(COMPARATOR_BIGGER)) {
				jooCode[i] = jooCode[i].replace(COMPARATOR_BIGGER, "" + JooVirtualMachine.BIGGER);
			}
			jooCode[i] = jooCode[i].replace(KEYWORD_IF, "" + JooVirtualMachine.KEYWORD_IF);
		}
	}
}
