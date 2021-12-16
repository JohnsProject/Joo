package com.johnsproject.joo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.johnsproject.joo.util.FileUtil;

public class JooCompiler {
	
	// TODO
	// import keyword
	// compiler syntax analyser
	// Standart native library
	// specification
	// change to a license that needs to buy a license in case of commercial use
	// add make types extensible like operators

	public static final String KEYWORD_INCLUDE = "include";
	/*
	 Import keyword is used to import external files. The file get's duplicated and renamed
	 to a single character. The file has to be in the same folder or nested folder of the folder
	 the joo file to be compiled is in. The file name can passed as parameter to the Native functions.
	 Usage is like: 
	 
	 	# import <file name> = <file path>
	  	import OtherJooApp = App2.cjoo
	  	call <Native function name>: OtherJooApp
	 */
	public static final String KEYWORD_IMPORT = "import";
	public static final String KEYWORD_CONSTANT = "constant";
	public static final String KEYWORD_IF = "if";
	public static final String KEYWORD_ELSE_IF = "elseIf";
	public static final String KEYWORD_ELSE = "else";
	public static final String KEYWORD_IF_END = "endIf";
	public static final String KEYWORD_FUNCTION = "function";
	public static final String KEYWORD_FUNCTION_END = "endFunction";
	public static final String KEYWORD_FUNCTION_REPEAT = "repeatFunction";
	public static final String KEYWORD_FUNCTION_CALL = "call";
	public static final String KEYWORD_TRUE = "true";
	public static final String KEYWORD_FALSE = "false";
	public static final String KEYWORD_COMMENT = "#";
	public static final String KEYWORD_ARRAY = ":";
	public static final String KEYWORD_CHAR = "'";
	public static final String KEYWORD_VARIABLE_ASSIGN = "=";
	public static final String KEYWORD_TYPE_SEPARATOR = "|";

	public static final String KEYWORD_OPERATOR = "operator";
	public static final String KEYWORD_NATIVE_FUNCTION = "native";
	
	public static final String TYPE_PARAMETER = "parameter";
	public static final String TYPE_FUNCTION = "function";
	public static final String TYPE_INT = "int";
	public static final String TYPE_FIXED = "fixed";
	public static final String TYPE_BOOL = "bool";
	public static final String TYPE_CHAR = "char";
	public static final String TYPE_ARRAY_INT = TYPE_INT + KEYWORD_ARRAY;
	public static final String TYPE_ARRAY_FIXED = TYPE_FIXED + KEYWORD_ARRAY;
	public static final String TYPE_ARRAY_BOOL = TYPE_BOOL + KEYWORD_ARRAY;
	public static final String TYPE_ARRAY_CHAR = TYPE_CHAR + KEYWORD_ARRAY;
	
	public static final String LINE_BREAK = "\n";
	
	public static final int FIXED_POINT = 255;

	public static final String CODE_ENDING = ".joo";
	public static final String BYTECODE_ENDING = ".cjoo";
	public static final String STANDART_LIBRARY = "StandartLibrary.joo";
	
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
	private boolean isInMultiLineComment;
	private List<Operator> operators;
	private Map<String, NativeFunction> nativeFunctions;
	private Map<String, Variable>[] variables;
	private Map<String, Function> functions;
	
	public JooCompiler() {
		name = JooVirtualMachine.COMPONENTS_START;
	}
	
	public int getComponentMemoryUsage() {
		int componentMemory = 0;
		for (int i = 0; i < variables.length; i++) {
			componentMemory += variables[i].size();
		}
		componentMemory += functions.size();
		return componentMemory;
	}
	
	public int getArrayMemoryUsage() {
		int arrayMemory = 0;
		for (int i = 4; i < variables.length; i++) {
			for (Variable variable : variables[i].values()) {
				arrayMemory += Integer.parseInt(variable.getValue());
			}
		}
		return arrayMemory;
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
	@SuppressWarnings("unchecked")
	public String compile(final String path) {
		name = JooVirtualMachine.COMPONENTS_START;
		isInMultiLineComment = false;
		operators = new ArrayList<>();
		nativeFunctions = new LinkedHashMap<String, NativeFunction>();
		variables = new Map[VM_TYPES.length];
		functions = new LinkedHashMap<String, Function>();
		String directoryPath = getDirectoryPath(path);
		// remove all tabs they are not needed at all
		String code = FileUtil.read(path).replaceAll("\t", "");
		code = includeIncludes(directoryPath, code);
		code = replaceDefines(code);
		code = parseConfig(code);
		final String[] codeLines = getLines(code);		
		parseVariables(codeLines);
		parseFunctions(codeLines);
		String compiledJooCode = "";
		compiledJooCode = writeVariablesAndFunctions(compiledJooCode);
		compiledJooCode = writeFunctionsAndInstructions(compiledJooCode);
		return getCodeSize(compiledJooCode) + compiledJooCode;
	}
	
	private String getCodeSize(String compiledJooCode) {
		String size = "";
		size += (char)((compiledJooCode.length() >> 8) & 255);
		size += (char)(compiledJooCode.length() & 255);
		return size;
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
			if(isNotCodeLine(codeLines[i])) {
				continue;
			}
			final String[] codeLine = splitCodeLine(codeLines[i]);
			if(codeLine[0].equals(KEYWORD_INCLUDE)) {
				final String filePath = codeLine[1];
				if(FileUtil.fileExists(directoryPath + filePath)) {
					code += FileUtil.read(directoryPath + filePath);
				} 
				else if (FileUtil.fileExists(filePath) || FileUtil.resourceExists(filePath)) {
					code += FileUtil.read(filePath);
				}
				else {
					System.err.println("Error, Line : " + i + ", Message : Included file does not exist, Path : " + filePath);
				}
				code = code.replace(codeLines[i], "");
			}
		}
		return code;
	}
	
	String replaceDefines(String code) {
		final String[] codeLines = getLines(code);
		List<String> defineNames = new ArrayList<>();
		for (int i = 0; i < codeLines.length; i++) {
			if(isNotCodeLine(codeLines[i])) {
				continue;
			}
			final String[] codeLine = splitCodeLine(codeLines[i]);
			if(codeLine[0].equals(KEYWORD_CONSTANT)) {
				if(defineNames.contains(codeLine[1])) {
					System.err.println("Error, Line : " + i + ", Message : Duplicate constant, Name : " + codeLine[1]);	
				}
				else if(codeLine[2].equals(KEYWORD_VARIABLE_ASSIGN)) {
					defineNames.add(codeLine[1]);
					code = code.replace(codeLines[i], "");
					code = code.replace(codeLine[1], codeLine[3]);
				} else {
					System.err.println("Error, Line : " + i + ", Message : Unassigned constant, Name : " + codeLine[1]);	
				}
			}
		}
		return code;
	}
	
	String parseConfig(String code) {
		final String[] codeLines = getLines(code);
		for (int i = 0; i < codeLines.length; i++) {
			if(isNotCodeLine(codeLines[i])) {
				continue;
			}
			final String[] codeLine = splitCodeLine(codeLines[i]);
			if(codeLine[0].equals(KEYWORD_OPERATOR)) {
				operators.add(parseOperator(codeLine));
				code = code.replace(codeLines[i], "");
			}
			if(codeLine[0].equals(KEYWORD_NATIVE_FUNCTION)) {
				nativeFunctions.put(codeLine[1], parseNativeFunction(codeLine));
				code = code.replace(codeLines[i], "");
			}
		}
		return code;
	}
	
	private Operator parseOperator(String[] codeLine) {
		String[] operatorTypes = codeLine[2].split(Pattern.quote(KEYWORD_TYPE_SEPARATOR));
		Operator operator = new Operator(codeLine[1]);
		for (int i = 1; i < operatorTypes.length; i++) {
			operator.addSupportedType(operatorTypes[i]);
		}
		return operator;
	}
	
	private NativeFunction parseNativeFunction(String[] codeLine) {
		NativeFunction nativeFunction = new NativeFunction(codeLine[1], (char) (nativeFunctions.size() + JooVirtualMachine.NATIVE_FUNCTIONS_START));
		for (int i = 2; i < codeLine.length; i++) {
			parseParameter(codeLine[i], i - 2, nativeFunction);
		}
		return nativeFunction;
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
	
	/**
	 * This method parses variables and arrays of all types in the joo code lines. 
	 * If a line with a declaration is found the variable is added to the map and the code 
	 * line is set to empty string to avoid conflict with other parse methods.
	 * 
	 * @param codeLines
	 * @param variableType
	 * @return map of variables that contains the variable names as keys and the variable objects as values.
	 */
	void parseVariables(String[] codeLines){
		variables[0] = parseVariables(codeLines, TYPE_INT);
		variables[1] = parseVariables(codeLines, TYPE_FIXED);
		variables[2] = parseVariables(codeLines, TYPE_BOOL);
		variables[3] = parseVariables(codeLines, TYPE_CHAR);
		variables[4] = parseArrays(codeLines, TYPE_INT);
		variables[5] = parseArrays(codeLines, TYPE_FIXED);
		variables[6] = parseArrays(codeLines, TYPE_BOOL);
		variables[7] = parseArrays(codeLines, TYPE_CHAR);
		checkForDuplicateVariableNames();
	}
	
	private void checkForDuplicateVariableNames() {
		for (Map<String, Variable> map : variables) {
			for (String name : map.keySet()) {
				int namesCount = 0;
				for (Map<String, Variable> otherMap : variables) {
					for (String otherName : otherMap.keySet()) {
						if(name.equals(otherName)) {
							namesCount++;
						}
					}
				}
				if(namesCount > 1) {
					System.err.println("Error, Message : Duplicate variable, Name : " + name);
					return;
				}
			}
		}
	}
	
	/**
	 * This method parses variables of the given type in the joo code lines. 
	 * If a line with a declaration is found the variable is added to the map and the code 
	 * line is set to empty string to avoid conflict with other parse methods.
	 * 
	 * @param codeLines
	 * @param variableType
	 * @return map of variables that contains the variable names as keys and the variable objects as values.
	 */
	private Map<String, Variable> parseVariables(String[] codeLines, final String variableType) {
		Map<String, Variable> variables = new LinkedHashMap<>();
		for (int i = 0; i < codeLines.length; i++) {
			if(isNotCodeLine(codeLines[i])) {
				continue;
			}
			final String[] codeLine = splitCodeLine(codeLines[i]);
			if(codeLine[0].equals(variableType)) {
				String variableName = codeLine[1];
				if(variables.containsKey(variableName)) {
					System.err.println("Error, Message : Duplicate variable, Name : " + variableName);					
				}
				final String variableValue = getVariableValue(codeLine, variableType);
				// name++ because names used in joo virtual machine are unique characters	
				final Variable result = new Variable(variableName, variableType, name++, variableValue);
				variables.put(variableName, result);
				codeLines[i] = "";
			}
		}
		return variables;
	}
	
	private String getVariableValue(final String[] codeLine, final String variableType) {
		String variableValue = "";
		if(codeLine.length > 2 && codeLine[2].equals(KEYWORD_VARIABLE_ASSIGN)) {
			if(variableType.equals(TYPE_INT)) {
				variableValue = codeLine[3];
			}
			else if(variableType.equals(TYPE_FIXED)) {
				variableValue = "" + Math.round(Float.parseFloat(codeLine[3]) * FIXED_POINT);
			}
			else if(variableType.equals(TYPE_BOOL)) {
				variableValue = "" + (codeLine[3].equals(KEYWORD_FALSE) ? 0 : 1);
			}
			else if(variableType.equals(TYPE_CHAR)) {
				variableValue = "" + codeLine[3].toCharArray()[1];
			}
		}
		return variableValue;
	}
	
	/**
	 * This method parses arrays of the given type in the joo code lines. 
	 * If a line with a declaration is found the array is added to the map and the code 
	 * line is set to empty string to avoid conflict with other parse methods.
	 * 
	 * @param codeLines
	 * @param arrayType
	 * @return map of arrays that contains the arrays names as keys and the variable objects as values. 
	 * The value field of the Variable objects contains the size of the array.
	 */
	private Map<String, Variable> parseArrays(String[] codeLines, final String arrayType) {
		Map<String, Variable> variables = new LinkedHashMap<>();
		for (int i = 0; i < codeLines.length; i++) {
			if(isNotCodeLine(codeLines[i])) {
				continue;
			}
			final String[] codeLine = splitCodeLine(codeLines[i]);
			if(codeLine[0].contains(arrayType + KEYWORD_ARRAY)) {
				String variableName = codeLine[1];
				if(variables.containsKey(variableName)) {
					System.err.println("Error, Message : Duplicate variable, Name : " + variableName);					
				}
				final String arraySize = getArraySize(i, codeLine, arrayType, variableName);
				// name++ because names used in joo virtual machine are unique characters	
				final Variable result = new Variable(variableName, arrayType, name++, arraySize);
				variables.put(variableName, result);			
				codeLines[i] = "";
			}
		}
		return variables;
	}
	
	private String getArraySize(final int line, final String[] codeLine, final String arrayType, final String variableName) {
		String arraySize = codeLine[0].replace(arrayType + KEYWORD_ARRAY, "");
		try {
			int size = Integer.parseInt(arraySize);
			if(size < 0) {
				System.err.println("Error, Line : " + line + ", Message : Array size can't be negative, Name : " + variableName);						
			}
		} catch(NumberFormatException e) {
			System.err.println("Error, Line : " + line + ", Message : Array size is not number, Name : " + variableName);
		}
		return arraySize;
	}
	
	/**
	 * This method parses functions of the given type in the joo code lines. 
	 * If a line with a declaration is found the function is added to the map and the code 
	 * line is set to empty string to avoid conflict with other parse methods.
	 * 
	 * @param codeLines
	 * @return map of functions that contains the function names as keys and the function objects as values.
	 */
	void parseFunctions(String[] codeLines) {
		// this first pass registers all functions, all functions must be
		// known to know if a function call doesn't call a unknown function
		parseFunctionDeclarations(codeLines);
		parseFunctionsInstructions(codeLines);
	}
	
	private void parseFunctionDeclarations(String[] codeLines) {
		for (int i = 0; i < codeLines.length; i++) {
			if(isNotCodeLine(codeLines[i])) {
				continue;
			}
			String[] codeLine = splitCodeLine(codeLines[i]);
			if(codeLine[0].equals(KEYWORD_FUNCTION)) {
				parseFunctionDeclaration(codeLine);	
			}
		}
	}
	
	private void parseFunctionDeclaration(String[] codeLine) {
		String functionName = codeLine[1];
		if(functions.containsKey(functionName)) {
			System.err.println("Error, Message : Duplicate function, Name : " + functionName);
		}
		Map<String, String> parameters = new LinkedHashMap<>();
		// are there parameters in the function declaration?
		if(functionName.length() > 2) {
			parseFunctionDeclarationParameters(codeLine, parameters, functionName);
		}
		final Function result = new Function(functionName, name++, parameters);
		functions.put(functionName, result);
	}
	
	private void parseFunctionDeclarationParameters(final String[] codeLine, Map<String, String> parameters, final String functionName) {
		for (int i = 2; i < codeLine.length; i += 2) {
			String parameterType = codeLine[i];
			String parameterName = codeLine[i + 1];
			for (Map<String, Variable> map : variables) {
				if(map.containsKey(parameterName)) {
					System.err.println("Error, Message : Parameter same as variable, FunctionName : " + functionName + ", ParameterName : " + parameterName);
				}
			}
			if(parameters.containsKey(parameterName)) {
				System.err.println("Error, Message : Duplicate parameter, FunctionName : " + functionName + ", ParameterName : " + parameterName);
			}
			final int typeIndex = getTypeIndex(parameterType);
			// if type does not exist -1 is returned
			if(typeIndex < 0) {
				System.err.println("Error, Message : Unknown type declaration, FunctionName : " + functionName + ", Type : " + parameterType);		
			} else {		
				parameters.put(parameterName, parameterType);				
			}
		}
	}
	
	private void parseFunctionsInstructions(String[] codeLines) {
		Function currentFunction = null;
		for (int i = 0; i < codeLines.length; i++) {
			if(isNotCodeLine(codeLines[i])) {
				continue;
			}
			String[] codeLine = splitCodeLine(codeLines[i]);
			if(codeLine[0].equals(KEYWORD_FUNCTION)) {
				currentFunction = functions.get(codeLine[1]);	
				codeLines[i] = "";
			}
			else if (codeLine[0].equals(KEYWORD_FUNCTION_END)) {
				currentFunction = null;	
				codeLines[i] = "";
			} else { 
				if(currentFunction == null) {
					System.err.println("Error, Line : " + i + ", Message : Instruction outside of function, Instruction : " + codeLines[i]);				
				} else {
					Instruction instruction = parseInstruction(i, codeLine, currentFunction.getParameters());
					if(instruction == null) {
						System.err.println("Error, Line : " + i + ", Message : Unknown instruction, Instruction : " + codeLines[i]);
					} else {
						currentFunction.addInstruction(instruction);
					}
				}
			}
		}
	}
	
	/**
	 * This method tries to parse the instruction in the given code line. 
	 * If it's a known instruction it get's parsed into a instruction object that 
	 * is then returned, null is returned if it's a comment or unknown instruction.
	 * 
	 * @param lineIndex
	 * @param codeLine
	 * @param parameters
	 * @return Instruction object if instruction can be parsed, null if it's a comment or unknown instruction.
	 */
	private Instruction parseInstruction(final int lineIndex, final String[] codeLine, final Map<String, String> parameters) {		
		Instruction instruction = new Instruction();
		if(codeLine[0].equals(KEYWORD_FUNCTION_CALL)) {
			parseFunctionCall(lineIndex, codeLine, instruction);
			instruction.isFunctionCall(true);
		}
		else if(codeLine[0].equals(KEYWORD_IF) || codeLine[0].equals(KEYWORD_ELSE_IF)) {
			instruction.isCondition(true);
			instruction.setConditionType(codeLine[0]);
			parseBinaryOperation(lineIndex, codeLine, parameters, instruction);
		}
		else if(codeLine[0].equals(KEYWORD_ELSE)) {
			instruction.setOperator(JooVirtualMachine.KEYWORD_ELSE);
		}
		else if(codeLine[0].equals(KEYWORD_IF_END)) {
			instruction.setOperator(JooVirtualMachine.KEYWORD_IF);
		}
		else if(codeLine[0].equals(KEYWORD_FUNCTION_REPEAT)) {
			instruction.setOperator(JooVirtualMachine.KEYWORD_FUNCTION_REPEAT);
		}
		else if(codeLine[0].equals(KEYWORD_FUNCTION_END)) {
			instruction.setOperator(JooVirtualMachine.KEYWORD_FUNCTION);
		} else { // if all if's failed it means the instruction is a variable operation
			parseBinaryOperation(lineIndex, codeLine, parameters, instruction);
		}
		if(!instruction.isFunctionCall() && (instruction.getOperator() == 0)) {
			return null;
		}		
		return instruction;
	}
	
	private void parseFunctionCall(final int lineIndex, String[] codeLine, Instruction instruction) {
		final String functionName = codeLine[1];
		if(functions.containsKey(functionName)) {
			final Function calledFunction = functions.get(functionName);
			final Map<String, String> calledFunctionParams = calledFunction.getParameters();
			instruction.setFunctionName(functionName);
			if(calledFunctionParams.size() != codeLine.length - 2) {
				System.err.println("Error, Line : " + lineIndex + ", Message : Function call parameters not equal to function parameters, Name : " + functionName);				
			}
			// Has the function call parameters?
			if(codeLine.length > 2) {
				parseFunctionCallParameters(lineIndex, codeLine, instruction, calledFunctionParams.values());
			}
		} else if (nativeFunctions.containsKey(functionName)) {
			final NativeFunction calledFunction = nativeFunctions.get(functionName);
			instruction.setFunctionName(functionName);
			if(calledFunction.getParameterCount() != codeLine.length - 2) {
				System.err.println("Error, Line : " + lineIndex + ", Message : Function call parameters not equal to function parameters, Name : " + functionName);				
			}
			// Has the function call parameters?
			if(codeLine.length > 2) {
				parseFunctionCallParameters(lineIndex, codeLine, instruction, null);
			}
		} else {
			System.err.println("Error, Line : " + lineIndex + ", Message : Unknown function call, Name : " + functionName);
		}
	}
	
	private void parseFunctionCallParameters(final int lineIndex, final String[] codeLine, Instruction instruction, final Collection<String> calledFunctionParamTypes) {
		for (int i = 2; i < codeLine.length; i++) {
			// parsing in case of array with index as parameter will be done later
			final String variableName = codeLine[i];
			instruction.addParameter(variableName);
		}
		if(calledFunctionParamTypes != null) { // TODO type checking for native functions
			int paramIndex = 0;
			final List<String> instructionParams = instruction.getParameters();
			for (String paramType : calledFunctionParamTypes) {
				final String instructionParamName = instructionParams.get(paramIndex++);
				final int typeIndex = getTypeIndex(paramType);
				if(!variables[typeIndex].containsKey(instructionParamName)) {
					System.err.println("Error, Line : " + lineIndex + ", Message : Function call parameter not equal to function parameter type, ParameterName : " + instructionParamName);					
				}
			}
		}
	}
	
	/**
	 * This method parses a binary operation (operation like a + b) with the possible operators
	 * and fills the given instruction object with the parsed data.
	 * 
	 * @param codeLine
	 * @param parameters
	 * @param operators
	 * @param instruction
	 */
	private void parseBinaryOperation(final int lineIndex, final String[] codeLine, final Map<String, String> parameters, Instruction instruction) {
		final int startIndex = instruction.isCondition() ? 1 : 0;
		parseOperator(lineIndex, codeLine, codeLine[startIndex + 1], instruction);
		parseVariable0(lineIndex, codeLine[startIndex + 0], instruction);
		// try to parse the value behind the operator
		parseValue(codeLine[startIndex + 0], codeLine[startIndex + 2], parameters, instruction);
		// if there is no value it has to be a variable
		if(!instruction.hasValue()) {
			parseVariable1(lineIndex, codeLine[startIndex + 2], instruction);
		}
	}
	
	private void parseOperator(final int lineIndex, final String[] codeLine, final String operator, Instruction instruction) {
		for (int i = 0; i < operators.size(); i++) {
			if(operator.equals(operators.get(i).getName())) {
				instruction.setOperator((char) (i + JooVirtualMachine.OPERATORS_START));
				return;
			}
		}
		System.err.println("Error, Line : " + lineIndex + ", Message : Unknown operator, Operator : " + operator);				
	}
	
	private void parseVariable0(final int lineIndex, final String rawVariableData, Instruction instruction) {
		if(rawVariableData.contains(KEYWORD_ARRAY)) {
			String[] variableData = rawVariableData.split(Pattern.quote(KEYWORD_ARRAY));
			instruction.setVariable0Name(variableData[0]);
			instruction.setVariable0ArrayIndex(variableData[1]);
			instruction.hasVariable0ArrayIndex(true);
		} else {
			instruction.setVariable0Name(rawVariableData);
		}
		instruction.hasVariable0(true);
		if(!isNameUsed(instruction.getVariable0Name())) {
			System.err.println("Error, Line : " + lineIndex + ", Message : Unknown variable, Name : " + instruction.getVariable0Name());					
		}
	}
	
	/**
	 * This method parses the variable or value after the operator and sets it in the instruction object.
	 * 
	 * @param variable0Data
	 * @param variable1Data
	 * @param parameters
	 * @param instruction
	 */
	private void parseValue(final String variable0Data, final String variable1Data, final Map<String, String> parameters, Instruction instruction) {
		parseCharValue(variable1Data, instruction);
		if(!instruction.hasValue()) {
			parseIntValue(variable0Data, variable1Data, parameters, instruction);
		}
		if(!instruction.hasValue()) {
			parseFixedValue(variable1Data, instruction);
		}
		if(!instruction.hasValue()) {
			parseBoolValue(variable1Data, instruction);
		}
	}
	
	private void parseCharValue(final String variable1Data, Instruction instruction) {
		if(variable1Data.contains(KEYWORD_CHAR)) {
			instruction.setValue("" + variable1Data.toCharArray()[1]);
			instruction.hasValue(true);
			instruction.setValueType(TYPE_CHAR);
		}
	}
	
	private void parseIntValue(final String variable0Data, final String variable1Data, final Map<String, String> parameters, Instruction instruction) {
		try {
			String variableName = variable0Data;
			if(variableName.contains(KEYWORD_ARRAY)){
				variableName = variableName.split(Pattern.quote(KEYWORD_ARRAY))[0];
			}
			boolean isIntParameter = false;
			if(parameters.containsKey(variableName)) {
				isIntParameter = parameters.get(variableName).equals(TYPE_INT) || parameters.get(variableName).equals(TYPE_ARRAY_INT);
			}
			// if variable before operator is of type int, otherwise a number without fractional part of type fixed would be parsed into int
			if(variables[0].containsKey(variableName) || variables[4].containsKey(variableName) || isIntParameter) {
				instruction.setValue("" + Integer.parseInt(variable1Data));
				instruction.hasValue(true);
				instruction.setValueType(TYPE_INT);
			} 
		} catch(NumberFormatException notNumber) {
			return;
		}
	}
	
	private void parseFixedValue(final String variable1Data, Instruction instruction) {
		try {
			instruction.setValue("" + Math.round(Float.parseFloat(variable1Data) * FIXED_POINT));
			instruction.hasValue(true);
			instruction.setValueType(TYPE_FIXED);
		} catch(NumberFormatException notNumber) {
			return;
		}
	}
	
	private void parseBoolValue(final String variable1Data, Instruction instruction) {
		if(variable1Data.equals(KEYWORD_TRUE) || variable1Data.equals(KEYWORD_FALSE)) {
			instruction.setValue("" + (variable1Data.equals(KEYWORD_FALSE) ? 0 : 1));
			instruction.hasValue(true);
			instruction.setValueType(TYPE_BOOL);
		}
	}
	
	private void parseVariable1(final int lineIndex, final String rawVariableData, Instruction instruction) {
		if(rawVariableData.contains(KEYWORD_ARRAY)) {
			String[] variableData = rawVariableData.split(Pattern.quote(KEYWORD_ARRAY));
			instruction.setVariable1Name(variableData[0]);
			instruction.setVariable1ArrayIndex(variableData[1]);
			instruction.hasVariable1ArrayIndex(true);
		} else {
			instruction.setVariable1Name(rawVariableData);
		}
		instruction.hasVariable1(true);
		if(!isNameUsed(instruction.getVariable1Name())) {
			System.err.println("Error, Line : " + lineIndex + ", Message : Unknown variable, Name : " + instruction.getVariable1Name());					
		}
	}
	
	/**
	 * Splits the code line by the empty spaces and returns a string array without empty strings.
	 * 
	 * @param line
	 * @return A string array with the components of the code line.
	 */
	String[] splitCodeLine(String line) {
		if(line.contains(KEYWORD_COMMENT))
			line = line.substring(0, line.indexOf(KEYWORD_COMMENT));
			
		final String[] rawCodeLine = line.split(" ");
		final List<String> codeLine = new ArrayList<>();
		for (int i = 0; i < rawCodeLine.length; i++) {
			if(!rawCodeLine[i].isEmpty()) {
				codeLine.add(rawCodeLine[i]);
			}
		}
		return codeLine.toArray(rawCodeLine);
	}
	
	private boolean isNameUsed(final String name) {
		if(variables != null) {
			for (Map<String, Variable> map : variables) {
				if(map.containsKey(name)) {
					return true;
				}
			}
		}
		if(functions != null) {
			if(functions.containsKey(name)) {
				return true;
			}
			for (Function function : functions.values()) {
				if(function.getParameters().containsKey(name)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private int getTypeIndex(final String type) {
		if(type.equals(TYPE_INT)) {
			return 0;
		} else if(type.equals(TYPE_FIXED)) {
			return 1;
		} else if(type.equals(TYPE_BOOL)) {
			return 2;
		} else if(type.equals(TYPE_CHAR)) {
			return 3;
		} else if(type.equals(TYPE_ARRAY_INT)) {
			return 4;
		} else if(type.equals(TYPE_ARRAY_FIXED)) {
			return 5;
		} else if(type.equals(TYPE_ARRAY_BOOL)) {
			return 6;
		} else if(type.equals(TYPE_ARRAY_CHAR)) {
			return 7;
		} else {
			return -1;
		}
	}
	
	private boolean isNotCodeLine(final String codeLine) {		
		if(codeLine.contains(KEYWORD_COMMENT + KEYWORD_COMMENT))
			isInMultiLineComment = !isInMultiLineComment;
		
		return codeLine.isEmpty() || (codeLine.indexOf(KEYWORD_COMMENT) == 0) || isInMultiLineComment;
	}
	
	/**
	 * This method writes the declaration of variables and arrays of all types and the function count
	 * in the joo code string in a way the joo virtual machine can read it.
	 * <br>
	 * The joo virtual machine can't understand code written by this method only, use the {@link #compile(String) compile} method 
	 * to generate joo virtual machine readable code.
	 * 
	 * @param code
	 * @return joo code string that contains variables and array declarations. 
	 */
	String writeVariablesAndFunctions(String code) {
		for (int i = 0; i < variables.length; i++) {
			code += "" + VM_TYPES[i] + (char)(variables[i].size() + JooVirtualMachine.COMPONENTS_START) + JooVirtualMachine.LINE_BREAK;
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
	 * @return
	 */
	String writeFunctionsAndInstructions(String code) {
		for (Function function : functions.values()) {
			// doesn't need the parameters in the function declaration, the parameters are already listed in function call
			code += "" + JooVirtualMachine.KEYWORD_FUNCTION + function.getByteCodeName() + JooVirtualMachine.LINE_BREAK;
			for (Instruction instruction : function.getInstructions()) {
				if(instruction.isCondition()) {
					if(instruction.getConditionType().equals(KEYWORD_IF)) {
						code += "" + JooVirtualMachine.KEYWORD_IF;
					}
					else if (instruction.getConditionType().equals(KEYWORD_ELSE_IF)) {
						code += "" + JooVirtualMachine.KEYWORD_ELSE_IF;
					}
					code = writeBinaryOperation(code, function.getParameters().keySet(), instruction);
				}
				else if(instruction.isFunctionCall()) {
					if(functions.containsKey(instruction.getFunctionName())) {
						code += "" + functions.get(instruction.getFunctionName()).getByteCodeName();
					} else if(nativeFunctions.containsKey(instruction.getFunctionName())) {
						code += "" + JooVirtualMachine.KEYWORD_FUNCTION_CALL + nativeFunctions.get(instruction.getFunctionName()).getByteCodeName();
					}
				} else {
					code = writeBinaryOperation(code, function.getParameters().keySet(), instruction);
				}
				code = writeInstructionParameters(code, instruction);
				code += "" + JooVirtualMachine.LINE_BREAK;
			}
		}
		return code;
	}

	private String writeBinaryOperation(String code, final Set<String> parameters, Instruction instruction) {
		if(instruction.hasVariable0()) {
			code += getVirtualMachineVariableName(instruction.getVariable0Name(), parameters);
			if(instruction.hasVariable0ArrayIndex()) {
				try {
					code += "" + (char)(Integer.parseInt(instruction.getVariable0ArrayIndex()) + JooVirtualMachine.ARRAY_INDEXES_START);
				} catch (NumberFormatException e) {
					code += getVirtualMachineVariableName(instruction.getVariable0ArrayIndex(), parameters);				
				}
			}
		}
		code += "" + instruction.getOperator();
		if(instruction.hasVariable1()) {
			code += getVirtualMachineVariableName(instruction.getVariable1Name(), parameters);
			if(instruction.hasVariable1ArrayIndex()) {
				try {
					code += "" + (char)(Integer.parseInt(instruction.getVariable1ArrayIndex()) + JooVirtualMachine.ARRAY_INDEXES_START);
				} catch (NumberFormatException e) {
					code += getVirtualMachineVariableName(instruction.getVariable1ArrayIndex(), parameters);
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
	
	private String getVirtualMachineVariableName(String variableName, final Set<String> parameters) {
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
	 * @param instruction
	 * @return
	 */
	private String writeInstructionParameters(String code, Instruction instruction) {
		for (String parameter : instruction.getParameters()) {
			String parameterName = "";
			String parameterIndex = "";
			// parse if parameter is a array with index				
			if(parameter.contains(KEYWORD_ARRAY)) {
				String[] parameterData = parameter.split(Pattern.quote(KEYWORD_ARRAY));
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