package com.johnsproject.joo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The JppCompiler is a try to create a better version of joo that has cleaner 
 * compiler code and uses the ASCII chart in a more efficient way.
 * 
 * @author John Ferraz Salomon
 *
 */
public class JppCompiler {

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
	public static final String KEYWORD_CHAR = "'";

	public static final String TYPE_PARAMETER = "parameter";
	public static final String TYPE_FUNCTION = "function";
	public static final String TYPE_INT = "int";
	public static final String TYPE_FIXED = "fixed";
	public static final String TYPE_BOOL = "bool";
	public static final String TYPE_CHAR = "char";
	
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
	
	public static final String[] COMPILER_COMPARATORS = new String[] {
			COMPARATOR_SMALLER,
			COMPARATOR_BIGGER,
			COMPARATOR_SMALLER_EQUALS,
			COMPARATOR_BIGGER_EQUALS,
			COMPARATOR_EQUALS,
			COMPARATOR_NOT_EQUALS,
	};
	
	public static final String[] COMPILER_OPERATORS = new String[] {
			OPERATOR_ADD,
			OPERATOR_SUBTRACT,
			OPERATOR_MULTIPLY,
			OPERATOR_DIVIDE,
			OPERATOR_SET_EQUALS,
	};
	
	public static final char[] VM_COMPARATORS = new char[] {
			JppVirtualMachine.COMPARATOR_SMALLER,
			JppVirtualMachine.COMPARATOR_BIGGER,
			JppVirtualMachine.COMPARATOR_SMALLER_EQUALS,
			JppVirtualMachine.COMPARATOR_BIGGER_EQUALS,
			JppVirtualMachine.COMPARATOR_EQUALS,
			JppVirtualMachine.COMPARATOR_NOT_EQUALS,
	};
	
	public static final char[] VM_OPERATORS = new char[] {
			JppVirtualMachine.OPERATOR_ADD,
			JppVirtualMachine.OPERATOR_SUBTRACT,
			JppVirtualMachine.OPERATOR_MULTIPLY,
			JppVirtualMachine.OPERATOR_DIVIDE,
			JppVirtualMachine.OPERATOR_SET_EQUALS,
	};
	
	public static final char[] VM_TYPES = new char[] {
			JppVirtualMachine.TYPE_INT,
			JppVirtualMachine.TYPE_FIXED,
			JppVirtualMachine.TYPE_BOOL,
			JppVirtualMachine.TYPE_CHAR,
			JppVirtualMachine.TYPE_ARRAY_INT,
			JppVirtualMachine.TYPE_ARRAY_FIXED,
			JppVirtualMachine.TYPE_ARRAY_BOOL,
			JppVirtualMachine.TYPE_ARRAY_CHAR,
	};
	
	/**
	 * The Variable class contains the data of a joo variable (like name and value).
	 * This class isn't private because unit tests need to access it.
	 * <br>
	 * If there is no value given in the variable declaration the default is 0.
	 * <br>
	 * If the variable is a array the value parameter is the array size.
	 * 
	 * @author John Ferraz Salomon
	 *
	 */
	static class Variable {
		
		private final char name;
		private final String value;
		
		public Variable(final char name, final String value) {
			this.name = name;
			this.value = value;
		}
		
		public char getName() {
			return name;
		}

		public String getValue() {
			return value;
		}
	}	
	
	/**
	 * The Function class contains the data of a joo function (like name and parameters).
	 * This class isn't private because unit tests need to access it.
	 * <br>
	 * If the function doesn't have parameters the parameter names are null, the
	 * parameter names array itself should always be a string array with length 6.
	 * 
	 * @author John Ferraz Salomon
	 *
	 */
	static class Function {
		
		private final char name;
		private final String[] parameters;
		private ArrayList<Operation> operations;
		
		public Function(final char name, final String[] parameters) {
			this.name = name;
			this.parameters = parameters;
			this.operations = new ArrayList<>();
		}
		
		public char getName() {
			return name;
		}

		public String[] getParameters() {
			return parameters;
		}

		public void addOperation(Operation operation) {
			operations.add(operation);
		}
		
		public ArrayList<Operation> getOperations() {
			return operations;
		}
	}
	
	/**
	 * The Operation class contains the data of a joo operation (like addition or multiplication).
	 * This class isn't private because unit tests need to access it.
	 * <br>
	 * If the variables of the operation are don't have a array index the index should be -1.
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
	static class Operation {
		
		private boolean condition;
		
		private String variable0Name;
		private int variable0ArrayIndex;
		
		private char operator;
		
		private String variable1Name;
		private int variable1ArrayIndex;
		
		private String value;
		private String valueType;
		
		private String[] parameters;

		public Operation() {
			this.condition = false;
			this.variable0Name = "";
			this.variable0ArrayIndex = -1;
			this.operator = 0;
			this.variable1Name = "";
			this.variable1ArrayIndex = -1;
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

		public void setVariable0ArrayIndex(int variable0ArrayIndex) {
			this.variable0ArrayIndex = variable0ArrayIndex;
		}

		public void setOperator(char operator) {
			this.operator = operator;
		}

		public void setVariable1Name(String variable1Name) {
			this.variable1Name = variable1Name;
		}

		public void setVariable1ArrayIndex(int variable1ArrayIndex) {
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

		public int getVariable0ArrayIndex() {
			return variable0ArrayIndex;
		}

		public char getOperator() {
			return operator;
		}

		public String getVariable1Name() {
			return variable1Name;
		}

		public int getVariable1ArrayIndex() {
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
	
	private char name;
	
	public JppCompiler() {
		// starts at 1 because 0 character is null character
		name = JppVirtualMachine.COMPONENTS_START;
	}
	
	/**
	 * This method compiles the human readable joo code in the jooCode string
	 * to joo virtual machine readable joo code.
	 * 
	 * @param jooCode human readable joo code.
	 * @return joo virtual machine readable joo code.
	 */
	String compile(final String jooCode) {
		// starts at 1 because 0 character is null character
		name = JppVirtualMachine.COMPONENTS_START;
		final String[] jooLines = getJooLines(jooCode);		
		final HashMap<String, Variable>[] variables = parseVariables(jooLines);
		final HashMap<String, Function> functions = parseFunctions(jooLines);
		String compiledJooCode = "";
		compiledJooCode = writeVariablesAndFunctions(compiledJooCode, variables, functions);
		compiledJooCode = writeFunctionsAndOperations(compiledJooCode, variables, functions);
		return compiledJooCode;
	}
	
	/**
	 * This method splits up the jooCode string to a string array of joo code lines. 
	 * It's a new method be cause it's also used by unit tests.
	 * 
	 * @param jooCode
	 * @return string array of joo code lines. 
	 */
	String[] getJooLines(final String jooCode) {
		return jooCode.replace("\r", "").split(LINE_BREAK);
	}
	
	/**
	 * This method parses functions of the given type in the joo code lines. 
	 * If a line with a declaration is found the functions is added to the map and the code 
	 * line is set to empty string to avoid conflict with other search methods.
	 * 
	 * @param jooLines
	 * @return map of functions that contains the function names as keys and the function objects as values.
	 */
	HashMap<String, Function> parseFunctions(String[] jooLines) {
		HashMap<String, Function> variables = new HashMap<>();
		Function currentFunction = null;
		for (int i = 0; i < jooLines.length; i++) {
			String jooLine = jooLines[i];
			// whitespace ensures the function keyword isn't part of a bigger word
			if(jooLine.contains(KEYWORD_FUNCTION + " ")) {
				currentFunction = parseFunctionDeclaration(jooLine, variables);	
				jooLines[i] = "";
			}
			else if (jooLine.equals(KEYWORD_FUNCTION_END)) {
				currentFunction = null;	
				jooLines[i] = "";
			}
			if(currentFunction != null) {
				Operation operation = parseOperation(jooLine);
				if(operation != null) {
					currentFunction.addOperation(operation);
				}
			}
		}
		return variables;
	}
		
	private Function parseFunctionDeclaration(String jooLine, HashMap<String, Function> variables) {
		// remove whitespaces, names shouldn't have whitespaces
		jooLine = jooLine.replace(" ", "");
		String functionName = "";
		String[] parameterNames = new String[6];
		if(jooLine.contains(KEYWORD_PARAMETER)) {
			final String[] functionData = jooLine.replace(KEYWORD_FUNCTION, "").split(KEYWORD_PARAMETER);
			functionName = functionData[0];
			for (int j = 1; j < functionData.length; j++) {
				parameterNames[j - 1] = functionData[j];
			}
		} else {
			functionName = jooLine.replaceFirst(KEYWORD_FUNCTION, "");
		}
		Function result = new Function(name++, parameterNames);
		variables.put(functionName, result);
		return result;	
	}
	
	/**
	 * This method parses variables and arrays of all types in the joo code lines. 
	 * If a line with a declaration is found the variable is added to the map and the code 
	 * line is set to empty string to avoid conflict with other search methods.
	 * 
	 * @param jooLines
	 * @param variableType
	 * @return map of variables that contains the variable names as keys and the variable objects as values.
	 */
	@SuppressWarnings("unchecked")
	HashMap<String, Variable>[] parseVariables(String[] jooLines){
		return new HashMap[] {
				parseVariables(jooLines, TYPE_INT),
				parseVariables(jooLines, TYPE_FIXED),
				parseVariables(jooLines, TYPE_BOOL),
				parseVariables(jooLines, TYPE_CHAR),
				parseArrays(jooLines, TYPE_INT),
				parseArrays(jooLines, TYPE_FIXED),
				parseArrays(jooLines, TYPE_BOOL),
				parseArrays(jooLines, TYPE_CHAR),
		};
	}
	
	/**
	 * This method parses variables of the given type in the joo code lines. 
	 * If a line with a declaration is found the variable is added to the map and the code 
	 * line is set to empty string to avoid conflict with other search methods.
	 * 
	 * @param jooLines
	 * @param variableType
	 * @return map of variables that contains the variable names as keys and the variable objects as values.
	 */
	private HashMap<String, Variable> parseVariables(String[] jooLines, final String variableType) {
		HashMap<String, Variable> variables = new HashMap<>();
		for (int i = 0; i < jooLines.length; i++) {
			String jooLine = jooLines[i];
			// whitespace ensures the variable type isn't part of a bigger word
			if(jooLine.contains(variableType + " ")) {
				// remove whitespaces, name and value shouldn't have whitespaces
				jooLine = jooLine.replace(" ", "");
				String variableName = "";
				String variableValue = "";
				if(jooLine.contains(OPERATOR_SET_EQUALS)) {
					final String[] variableData = jooLine.replaceFirst(variableType, "").split(OPERATOR_SET_EQUALS);
					variableName = variableData[0];
					if(variableType.equals(TYPE_INT))
						variableValue = variableData[1];
					else if(variableType.equals(TYPE_FIXED))
						variableValue = "" + Math.round(Float.parseFloat(variableData[1]) * 255);
					else if(variableType.equals(TYPE_BOOL))
						variableValue = "" + (variableData[1].equals(KEYWORD_FALSE) ? 0 : 1);
					else if(variableType.equals(TYPE_CHAR))
						variableValue = "" + variableData[1].toCharArray()[1];
				} else {
					variableName = jooLine.replaceFirst(variableType, "");
				}	
				// name++ because names used in joo virtual machine are unique characters	
				variables.put(variableName, new Variable(name++, variableValue));
				jooLines[i] = "";
			}
		}
		return variables;
	}
	
	/**
	 * This method parses arrays of the given type in the joo code lines. 
	 * If a line with a declaration is found the array is added to the map and the code 
	 * line is set to empty string to avoid conflict with other search methods.
	 * 
	 * @param jooLines
	 * @param arrayType
	 * @return map of arrays that contains the arrays names as keys and the variable objects as values. 
	 * The value field of the Variable objects contains the size of the array.
	 */
	private HashMap<String, Variable> parseArrays(String[] jooLines, final String arrayType) {
		HashMap<String, Variable> variables = new HashMap<>();
		for (int i = 0; i < jooLines.length; i++) {
			// remove whitespaces, name and value shouldn't have whitespaces
			final String jooLine = jooLines[i].replace(" ", "");
			if(jooLine.contains(arrayType + KEYWORD_ARRAY_START)) {
				final String[] variableData = jooLine.replace(arrayType + KEYWORD_ARRAY_START, "").split(KEYWORD_ARRAY_END);
				// name++ because names used in joo virtual machine are unique characters	
				variables.put(variableData[1], new Variable(name++, "" + (char)Integer.parseInt(variableData[0])));			
				jooLines[i] = "";
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
	 * @param jooLine
	 * @return Operation object if operation can be parsed, null if it's a comment or unknown operation.
	 */
	private Operation parseOperation(String jooLine) {		
		if(jooLine.contains(KEYWORD_COMMENT)) {
			return null;
		}
		Operation operation = new Operation();
		// whitespace ensures the keyword isn't part of a bigger word
		if(jooLine.contains(KEYWORD_FUNCTION_CALL + " ")) {
			parseFunctionCall(jooLine, operation);
		}
		else if(jooLine.contains(KEYWORD_IF + " ")) {
			jooLine = jooLine.replace(" ", "").replaceFirst(KEYWORD_IF, "");
			parseBinaryOperation(jooLine, COMPILER_COMPARATORS, VM_COMPARATORS, operation);
			operation.isCondition(true);
		}
		else if(jooLine.contains(KEYWORD_ELSE)) {
			operation.setOperator(JppVirtualMachine.KEYWORD_ELSE);
		}
		else if(jooLine.contains(KEYWORD_IF_END)) {
			operation.setOperator(JppVirtualMachine.KEYWORD_IF);
		}
		else if(jooLine.contains(KEYWORD_FUNCTION_REPEAT)) {
			operation.setOperator(JppVirtualMachine.KEYWORD_FUNCTION_REPEAT);
		}
		else if(jooLine.contains(KEYWORD_FUNCTION_END)) {
			operation.setOperator(JppVirtualMachine.KEYWORD_FUNCTION);
		} else { // if all if's failed it means the operation is a variable operation
			jooLine = jooLine.replace(" ", "");
			parseBinaryOperation(jooLine, COMPILER_OPERATORS, VM_OPERATORS, operation);
		}
		// if operator is null the operation could not be parsed
		if(operation.getOperator() == 0) {
			return null;
		}		
		return operation;
	}
	
	private void parseFunctionCall(String jooLine, Operation operation) {
		jooLine = jooLine.replace(" ", "").replaceFirst(KEYWORD_FUNCTION_CALL, "");
		if(jooLine.contains(KEYWORD_PARAMETER)) {
			String[] functionCallData = jooLine.split(KEYWORD_PARAMETER);
			operation.setVariable1Name(functionCallData[0]);
			// parsing in case of array with index as parameter will be done later
			for (int i = 1; i < functionCallData.length; i++) {
				operation.getParameters()[i-1] = functionCallData[i];
			}
		} else {
			operation.setVariable1Name(jooLine);
		}
		operation.setOperator(JppVirtualMachine.KEYWORD_FUNCTION_CALL);
	}
	
	/**
	 * This method parses a binary operation (operation like a + b) with the possible operators
	 * and fills the given operation object with the parsed data.
	 * 
	 * @param jooLine
	 * @param compilerOperators
	 * @param vmOperators
	 * @param operation
	 */
	private void parseBinaryOperation(String jooLine, String[] compilerOperators, char[] vmOperators, Operation operation) {
		String[] operationData = parseBinaryOperationVariablesAndOperator(jooLine, compilerOperators, vmOperators, operation);
		if(operationData != null) {
			if(operationData[0].contains(KEYWORD_ARRAY_START)) {
				String[] variableData = operationData[0].replace(KEYWORD_ARRAY_END, "").split("\\" + KEYWORD_ARRAY_START);
				operation.setVariable0Name(variableData[0]);
				operation.setVariable0ArrayIndex(Integer.parseInt(variableData[1]));
			} else {
				operation.setVariable0Name(operationData[0]);
			}
			if(operationData[1].contains(KEYWORD_ARRAY_START)) {
				String[] variableData = operationData[1].replace(KEYWORD_ARRAY_END, "").split("\\" + KEYWORD_ARRAY_START);
				operation.setVariable1Name(variableData[0]);
				operation.setVariable1ArrayIndex(Integer.parseInt(variableData[1]));
			} else {
				// part behind the operator may contain a value instead of variable
				parseBinaryOperationValue(operationData, operation);
			}
		}
	}
	
	/**
	 * This method parses the operator used in the operation and and returns the variables before and 
	 * after the operator. It also sets the operator in the Operation object.
	 * 
	 * @param jooLine
	 * @param compilerOperators
	 * @param vmOperators
	 * @param operation
	 * @return variables before and after the operator.
	 */
	private String[] parseBinaryOperationVariablesAndOperator(String jooLine, String[] compilerOperators, char[] vmOperators, Operation operation) {
		String[] operationData = null;
		for (int i = 0; i < compilerOperators.length; i++) {
			if(jooLine.contains(compilerOperators[i])) {
				operation.setOperator(vmOperators[i]);
				// some characters like '+' need a backslash in front of it
				String possibleOperator = compilerOperators[i];
				if(possibleOperator.equals(OPERATOR_ADD)) {
					possibleOperator = "\\" + OPERATOR_ADD;
				}
				if(possibleOperator.equals(OPERATOR_MULTIPLY)) {
					possibleOperator = "\\" + OPERATOR_MULTIPLY;
				}
				operationData = jooLine.split(possibleOperator);
				break;
			}
		}
		return operationData;
	}
	
	/**
	 * This method parses the variable or value after the operator and sets it in the Operation object.
	 * 
	 * @param operationData
	 * @param operation
	 */
	private void parseBinaryOperationValue(final String[] operationData, Operation operation) {
		if(operationData[1].contains(KEYWORD_CHAR)) {
			operation.setValue("" + operationData[1].toCharArray()[1]);
			operation.setValueType(TYPE_CHAR);
		} else {
			try {
				operation.setValue("" + Integer.parseInt(operationData[1]));
				operation.setValueType(TYPE_INT);
			}
			catch(NumberFormatException notInt) {
				try {
					operation.setValue("" + Math.round(Float.parseFloat(operationData[1]) * 255));
					operation.setValueType(TYPE_FIXED);
				}
				catch(NumberFormatException notFloat) {
					if(operationData[1].equals(KEYWORD_TRUE) || operationData[1].equals(KEYWORD_FALSE)) {
						operation.setValue("" + (operationData[1].equals(KEYWORD_FALSE) ? 0 : 1));
						operation.setValueType(TYPE_BOOL);
					} else {
						operation.setVariable1Name(operationData[1]);
					}
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
	 * @param jooCode
	 * @param variables
	 * @param functions
	 * @return joo code string that contains variables and array declarations. 
	 */
	String writeVariablesAndFunctions(String jooCode, final HashMap<String, Variable>[] variables, final HashMap<String, Function> functions) {
		for (int i = 0; i < variables.length; i++) {
			if(variables[i].size() > 0) {
				jooCode += "" + VM_TYPES[i] + (char)variables[i].size() + JppVirtualMachine.LINE_BREAK;
				for (Variable variable : variables[i].values()) {
					String value = variable.getValue();
					if(i < 3) { // if value is number
						value = toVirtualMachineNumber(variable.getValue());
					}
					jooCode += "" + variable.getName() + value + JppVirtualMachine.LINE_BREAK;
				}
			}
		}
		if(functions.size() > 0) {
			jooCode += "" + JppVirtualMachine.TYPE_FUNCTION + (char)functions.size() + JppVirtualMachine.LINE_BREAK;
		}
		return jooCode;
	}
	
	/**
	 * This method writes the function declaration and the function operations into the joo code string 
	 * in a way the joo virtual machine can read it.
	 * <br>
	 * The joo virtual machine can't understand code written by this method only, use the {@link #compile(String) compile} method 
	 * to generate joo virtual machine readable code.
	 * 
	 * @param jooCode
	 * @param variables
	 * @param functions
	 * @return
	 */
	String writeFunctionsAndOperations(String jooCode, final HashMap<String, Variable>[] variables, final HashMap<String, Function> functions) {
		for (Function function : functions.values()) {
			// doesn't need the parameters in the function declaration, the parameters are already listed in function call
			jooCode += "" + JppVirtualMachine.KEYWORD_FUNCTION + function.getName() + JppVirtualMachine.LINE_BREAK;
			for (Operation operation : function.getOperations()) {
				if(operation.isCondition()) {
					jooCode += "" + JppVirtualMachine.KEYWORD_IF;
				}
				jooCode = writeBinaryOperation(jooCode, function.getParameters(), variables, operation);
				if(operation.getOperator() == JppVirtualMachine.KEYWORD_FUNCTION_CALL) {
					if(functions.containsKey(operation.getVariable1Name())) {
						jooCode += "" + functions.get(operation.getVariable1Name()).getName();
					}
				}
				jooCode = writeOperationParameters(jooCode, variables, operation);
				jooCode += "" + JppVirtualMachine.LINE_BREAK;
			}
		}
		return jooCode;
	}

	private String writeBinaryOperation(String jooCode, final String[] functionParameters, final HashMap<String, Variable>[] variables, Operation operation) {
		jooCode += getVirtualMachineVariableName(operation.getVariable0Name(), functionParameters, variables);
		if(operation.getVariable0ArrayIndex() >= 0) {
			// +1 because 0 is null character
			jooCode += "" + (char)(operation.getVariable0ArrayIndex() + JppVirtualMachine.ARRAY_INDICES_START);
		}
		jooCode += "" + operation.getOperator();
		jooCode += getVirtualMachineVariableName(operation.getVariable1Name(), functionParameters, variables);
		if(operation.getVariable1ArrayIndex() >= 0) {
			jooCode += "" + (char)(operation.getVariable1ArrayIndex() + JppVirtualMachine.ARRAY_INDICES_START);
		}
		if(!operation.getValueType().isEmpty()) {
			if(operation.getValueType().equals(TYPE_CHAR)) {
				// put type char character in front of it so the virtual machine knows
				// the character is not a variable name
				jooCode += JppVirtualMachine.TYPE_CHAR +  operation.getValue();
			} else {				
				jooCode += toVirtualMachineNumber(operation.getValue());
			}
		}
		return jooCode;
	}
	
	private String getVirtualMachineVariableName(String variableName, final String[] functionParameters, final HashMap<String, Variable>[] variables) {
		for (int i = 0; i < variables.length; i++) {
			if(variables[i].containsKey(variableName)) {
				return "" + variables[i].get(variableName).getName();
			}
		}
		for (int i = 0; i < functionParameters.length; i++) {
			if(functionParameters[i] != null) {
				if(functionParameters[i].equals(variableName)) {
					return "" + (char)(i + JppVirtualMachine.PARAMETERS_START);
				}
			}
		}
		return "";
	}
	
	/**
	 * This method writes the operation parameters into the joo code string if the operation
	 * is a function call.
	 * 
	 * @param jooCode
	 * @param variables
	 * @param operation
	 * @return
	 */
	private String writeOperationParameters(String jooCode, final HashMap<String, Variable>[] variables, Operation operation) {
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
				jooCode += "" + JppVirtualMachine.KEYWORD_PARAMETER + newParameterName + parameterIndex;
			}
		}
		return jooCode;
	}
	
	private String toVirtualMachineNumber(String value) {
		// replace default number characters with other virtual machine ones
		for (int i = 0; i < 9; i++) {
			value = value.replace((char)('0' + i), (char)(JppVirtualMachine.NUMBER_0 + i));
		}
		return value;
	}
}