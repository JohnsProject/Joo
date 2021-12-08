package com.johnsproject.joo;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.johnsproject.joo.util.FileUtil;

public class JooCompiler {
	
	public static final String KEYWORD_IMPORT = "import";
	public static final String KEYWORD_INCLUDE = "include";
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
	// used in joo compiler settings only
	public static final String KEYWORD_TYPE_SEPARATOR = "|";

	public static final String KEYWORD_OPERATOR = "operator";
	public static final String KEYWORD_NATIVE = "native";
	
	public static final String TYPE_INT = "int";
	public static final String TYPE_FIXED = "fixed";
	public static final String TYPE_BOOL = "bool";
	public static final String TYPE_CHAR = "char";
	public static final String TYPE_ARRAY_INT = TYPE_INT + KEYWORD_ARRAY;
	public static final String TYPE_ARRAY_FIXED = TYPE_FIXED + KEYWORD_ARRAY;
	public static final String TYPE_ARRAY_BOOL = TYPE_BOOL + KEYWORD_ARRAY;
	public static final String TYPE_ARRAY_CHAR = TYPE_CHAR + KEYWORD_ARRAY;
	public static final String TYPE_PARAMETER = "parameter";
	public static final String TYPE_OPERATOR = "operator";
	
	public static final String ALPHANUMERIC_REGEX = "[A-Za-z0-9]+";
	public static final String CONSTANT_REGEX = "[A-Z0-9_]+";
	
	public static final String LINE_BREAK = "\n";	
	
	public static final char VM_TYPE_INT = JooVirtualMachine.TYPE_INT;
	public static final char VM_TYPE_FIXED = JooVirtualMachine.TYPE_FIXED;
	public static final char VM_TYPE_BOOL = JooVirtualMachine.TYPE_BOOL;
	public static final char VM_TYPE_CHAR = JooVirtualMachine.TYPE_CHAR;
	
	public static final char VM_TYPE_ARRAY_INT = JooVirtualMachine.TYPE_ARRAY_INT;
	public static final char VM_TYPE_ARRAY_FIXED = JooVirtualMachine.TYPE_ARRAY_FIXED;
	public static final char VM_TYPE_ARRAY_BOOL = JooVirtualMachine.TYPE_ARRAY_BOOL;
	public static final char VM_TYPE_ARRAY_CHAR = JooVirtualMachine.TYPE_ARRAY_CHAR;
	public static final char VM_TYPE_FUNCTION = JooVirtualMachine.TYPE_FUNCTION;
	
	public static final char VM_KEYWORD_IF = JooVirtualMachine.KEYWORD_IF;
	public static final char VM_KEYWORD_ELSE_IF = JooVirtualMachine.KEYWORD_ELSE_IF;
	public static final char VM_KEYWORD_ELSE = JooVirtualMachine.KEYWORD_ELSE;
	public static final char VM_KEYWORD_FUNCTION = JooVirtualMachine.KEYWORD_FUNCTION;
	public static final char VM_KEYWORD_FUNCTION_CALL = JooVirtualMachine.KEYWORD_FUNCTION_CALL;
	public static final char VM_KEYWORD_FUNCTION_REPEAT = JooVirtualMachine.KEYWORD_FUNCTION_REPEAT;
	public static final char VM_KEYWORD_ARGUMENT = JooVirtualMachine.KEYWORD_ARGUMENT;
	
	public static final char VM_LINE_BREAK = JooVirtualMachine.LINE_BREAK;
	
	public static final char VM_NUMBER_9 = JooVirtualMachine.NUMBER_9;
	public static final char VM_NUMBER_8 = JooVirtualMachine.NUMBER_8;
	public static final char VM_NUMBER_7 = JooVirtualMachine.NUMBER_7;
	public static final char VM_NUMBER_6 = JooVirtualMachine.NUMBER_6;
	public static final char VM_NUMBER_5 = JooVirtualMachine.NUMBER_5;
	public static final char VM_NUMBER_4 = JooVirtualMachine.NUMBER_4;
	public static final char VM_NUMBER_3 = JooVirtualMachine.NUMBER_3;
	public static final char VM_NUMBER_2 = JooVirtualMachine.NUMBER_2;
	public static final char VM_NUMBER_1 = JooVirtualMachine.NUMBER_1;
	public static final char VM_NUMBER_0 = JooVirtualMachine.NUMBER_0;
	
	public static final char FIXED_POINT = JooVirtualMachine.FIXED_POINT;
	public static final int FIXED_POINT_ONE = (1 << FIXED_POINT) - 1;
	
	public static final byte VM_COMPONENTS_START = JooVirtualMachine.COMPONENTS_START;
	public static final byte VM_COMPONENTS_END = JooVirtualMachine.COMPONENTS_END;
	public static final byte VM_PARAMETERS_START = JooVirtualMachine.PARAMETERS_START;
	public static final byte VM_PARAMETERS_END = JooVirtualMachine.PARAMETERS_END;
	public static final byte VM_OPERATORS_START = JooVirtualMachine.OPERATORS_START;
	public static final byte VM_OPERATORS_END = JooVirtualMachine.OPERATORS_END;
	public static final byte VM_NATIVE_FUNCTIONS_START = JooVirtualMachine.NATIVE_FUNCTIONS_START;
	public static final byte VM_NATIVE_FUNCTIONS_END = JooVirtualMachine.NATIVE_FUNCTIONS_END;
	public static final byte VM_TYPES_START = JooVirtualMachine.TYPES_START;
	public static final byte VM_TYPES_END = JooVirtualMachine.TYPES_END;
	
	private Code code;
	
	public JooCompiler() { }
	
	Code getCode() {
		return code;
	}

	public String compile(String path) {
		String byteCode = "";
		try {
			final String codePath = path;
			final String codeData = FileUtil.read(codePath);
			String[] codeLines = getLines(codeData);
			codeLines = addIncludedCode(codeLines);
			Code code = new Code();
			parseConstants(code, codeLines);
			parseOperators(code, codeLines);
			parseNatives(code, codeLines);
			parseVariables(code, codeLines);
			parseFunctions(code, codeLines);
			code = parseCode(codeData);
			analyseCode(code);
			byteCode = toByteCode(code);
		} catch (ParseException e) {
			// text editors usually begin at line 1
			System.err.println(e.getMessage() + ", Line: " + (e.getErrorOffset() + 1));
		}
		return byteCode;
	}
	
	/**
	 * This method splits up the code to a array of code lines. 
	 * It removes unnecessary characters like '\t' and '\r' and splits the code at 
	 * the '\n' characters. It also converts comment only to empty lines and removes 
	 * the comments in the lines that also contain code.
	 * 
	 * The empty lines are kept so the compiler can tell the location of the errors.
	 *  
	 * @param code to get lines from.
	 * @return Array of code lines. 
	 */
	String[] getLines(String code) {
		code = code.replace("\t", "");
		code = code.replace("\r", "");
		String[] codeLines = code.split(LINE_BREAK);
		boolean multiLineComment = false;
		for (int i = 0; i < codeLines.length; i++) {
			String line = codeLines[i];
			if(line.equals(KEYWORD_COMMENT + KEYWORD_COMMENT)) {
				multiLineComment = !multiLineComment;
				codeLines[i] = "";
			}
			else if(multiLineComment) {
				codeLines[i] = "";
			}
			else if(line.contains(KEYWORD_COMMENT)) {
				final String[] lineData = line.split(KEYWORD_COMMENT);
				final String lineCode = lineData[0];
				if(lineCode.isEmpty()) {
					codeLines[i] = "";
				} else {
					codeLines[i] = lineCode;
				}
			}
		}
		return codeLines;
	}
	
	String[] addIncludedCode(String[] codeLines) throws ParseException {
		final List<String> fullCodeLines = new ArrayList<String>();
		fullCodeLines.addAll(Arrays.asList(codeLines));
		for (int i = 0; i < codeLines.length; i++) {
			final String line = codeLines[i];
			if(!line.isEmpty()) {
				final String[] lineData = line.split(" ");
				if(lineData[0].equals(KEYWORD_INCLUDE)) {
					validateInclude(lineData, i);
					final String[] includedCode = getIncludedCode(lineData);
					fullCodeLines.addAll(Arrays.asList(includedCode));
					fullCodeLines.set(i, "");
				}
			}
		}
		return fullCodeLines.toArray(new String[0]);
	}
	
	private void validateInclude(String[] lineData, int lineIndex) throws ParseException {
		if(lineData.length != 2)
			throw new ParseException("Invalid inclusion declaration", lineIndex);
	}
	
	private String[] getIncludedCode(String[] lineData) throws ParseException {
		final String path = lineData[1];
		final String codeData = FileUtil.read(path);
		final String[] codeLines = getLines(codeData);
		return addIncludedCode(codeLines);
	}
	
	void parseConstants(Code code, String[] codeLines) throws ParseException {
		for (int i = 0; i < codeLines.length; i++) {
			final String line = codeLines[i];
			if(!line.isEmpty()) {
				final String[] lineData = line.split(" ");
				if(lineData[0].equals(KEYWORD_CONSTANT)) {
					validateConstant(code, lineData, i);
					replaceConstant(code, lineData, codeLines, i);
					codeLines[i] = "";
				}
			}
		}
	}
	
	private void validateConstant(Code code, String[] lineData, int lineIndex) throws ParseException {
		if(lineData.length != 4) 
			throw new ParseException("Invalid constant declaration", lineIndex);
		
		final String name = lineData[1];
		final String operator = lineData[2];
		final char firstCharacter = name.toCharArray()[0];
		if(!operator.equals(KEYWORD_VARIABLE_ASSIGN))
			throw new ParseException("Invalid constant assignment operator, Operator: " + operator + ", Constant: " + name, lineIndex);
		if(code.hasComponent(name, KEYWORD_CONSTANT))
			throw new ParseException("Duplicate constant, Constant: " + name, lineIndex);
		if(!name.matches(CONSTANT_REGEX))
			throw new ParseException("Constant names should contain only uppercase alphanumeric characters, Constant: " + name, lineIndex);
		if(!Character.isAlphabetic(firstCharacter))
			throw new ParseException("Constant names should start with a alphabetic character, Constant: " + name, lineIndex);
	}
	
	private void replaceConstant(Code code, String[] lineData, String[] codeLines, int lineIndex) {
		final String name = lineData[1];
		final String value = lineData[3];
		
		for (int i = 0; i < codeLines.length; i++) {
			final String line = codeLines[i];
			if(!line.substring(0, KEYWORD_CONSTANT.length()).equals(KEYWORD_CONSTANT))
				codeLines[i] = line.replace(name, value);
		}
		
		final CodeComponent nameComponent = new CodeComponent(name, (byte) 0, KEYWORD_CONSTANT, (byte) 0, lineIndex);
		final CodeComponent valueComponent = new CodeComponent(value, (byte) 0, KEYWORD_CONSTANT, (byte) 0, lineIndex);
		nameComponent.addComponent(valueComponent);
		code.addComponent(nameComponent);
	}
	
	void parseOperators(Code code, String[] codeLines) throws ParseException {
		for (int i = 0; i < codeLines.length; i++) {
			final String line = codeLines[i];
			if(!line.isEmpty()) {
				final String[] lineData = line.split(" ");
				if(lineData[0].equals(KEYWORD_OPERATOR)) {
					validateOperator(code, lineData, i);
					parseOperator(code, lineData, i);
					codeLines[i] = "";
				}
			}
		}
	}
	
	private void validateOperator(Code code, String[] lineData, int lineIndex) throws ParseException {
		if(lineData.length != 3)
			throw new ParseException("Invalid operator declaration", lineIndex);

		final String name = lineData[1];
		if(code.hasComponent(name, KEYWORD_OPERATOR))
			throw new ParseException("Duplicate operator, Operator: " + name, lineIndex);		
		if(name.matches(ALPHANUMERIC_REGEX))
			throw new ParseException("Operators shouldn't contain alphanumeric characters, Operator: " + name, lineIndex);
	}
	
	private void parseOperator(Code code, String[] lineData, int lineIndex) throws ParseException {
		final String name = lineData[1];
		final byte byteCodeName = (byte)(code.getComponentWithTypeCount(KEYWORD_OPERATOR) + VM_COMPONENTS_START);
		final CodeComponent component = new CodeComponent(name, byteCodeName, KEYWORD_OPERATOR, (byte) 0, lineIndex);
		parseSupportedType(component, lineData[2], lineIndex);
		code.addComponent(component);
	}
	
	void parseNatives(Code code, String[] codeLines) throws ParseException {
		for (int i = 0; i < codeLines.length; i++) {
			final String line = codeLines[i];
			if(!line.isEmpty()) {
				final String[] lineData = line.split(" ");
				if(lineData[0].equals(KEYWORD_NATIVE)) {
					validateNative(code, lineData, i);
					parseNative(code, lineData, i);
					codeLines[i] = "";
				}
			}
		}
	}
	
	private void validateNative(Code code, String[] lineData, int lineIndex) throws ParseException {
		if(lineData.length < 2)
			throw new ParseException("Invalid native declaration", lineIndex);
		
		final String name = lineData[1];
		final char firstCharacter = name.toCharArray()[0];
		if(code.hasComponent(name, KEYWORD_NATIVE))
			throw new ParseException("Duplicate native, Native: " + name, lineIndex);		
		if(!Character.isAlphabetic(firstCharacter))
			throw new ParseException("Native names should start with a alphabetic character, Native: " + name, lineIndex);
		if(Character.isLowerCase(firstCharacter))
			throw new ParseException("Native names should start with a uppercase character, Native: " + name, lineIndex);
		if(!name.matches(ALPHANUMERIC_REGEX))
			throw new ParseException("Native names should contain only alphanumeric characters, Native: " + name, lineIndex);
	}
	
	private void parseNative(Code code, String[] lineData, int lineIndex) throws ParseException {		
		final String name = lineData[1];
		final byte byteCodeName = (byte)(code.getComponentWithTypeCount(KEYWORD_NATIVE) + VM_COMPONENTS_START);
		final CodeComponent component = new CodeComponent(name, byteCodeName, KEYWORD_NATIVE, (byte) 0, lineIndex);
		for (int i = 2; i < lineData.length; i++) {
			final String paramName = "param" + (i - 2);
			final byte paramByteCodeName = (byte) ((i - 2) + VM_COMPONENTS_START);
			final CodeComponent paramComponent = new CodeComponent(paramName, paramByteCodeName, TYPE_PARAMETER, (byte) 0, lineIndex);
			parseSupportedType(paramComponent, lineData[i], lineIndex);
			component.addComponent(paramComponent);
		}
		code.addComponent(component);
	}	
	
	private void parseSupportedType(CodeComponent component, String supportedType, int lineIndex) throws ParseException {
		final String[] supportedTypes;
		if(supportedType.contains(KEYWORD_TYPE_SEPARATOR)) {
			supportedTypes = supportedType.split(Pattern.quote(KEYWORD_TYPE_SEPARATOR));
		} else {
			supportedTypes = new String[] {supportedType};
		}	
		for (int i = 0; i < supportedTypes.length; i++) {
			String type = supportedTypes[i];
			if(isVariable(type)) {
				CodeComponent typeComponent = new CodeComponent(type, toByteCodeType(type), type, toByteCodeType(type), lineIndex);
				component.addComponent(typeComponent);
			} else {
				throw new ParseException("Invalid supported type, Type: " + type, lineIndex);
			}
		}
	}
	
	void parseVariables(Code code, String[] codeLines) throws ParseException {
		for (int i = 0; i < codeLines.length; i++) {
			final String line = codeLines[i];
			if(!line.isEmpty()) {
				final String[] lineData = line.split(" ");
				if(isVariable(lineData[0])) {
					validateVariable(code, lineData, i);
					parseVariable(code, lineData, i);
					codeLines[i] = "";
				}
			}
		}
	}
	
	private void validateVariable(Code code, String[] lineData, int lineIndex) throws ParseException {
		if((lineData.length != 2) && (lineData.length != 4))
			throw new ParseException("Invalid variable declaration", lineIndex);
		
		final String type = toType(lineData[0]);
		final String name = lineData[1];
		final char firstCharacter = name.toCharArray()[0];
		if(code.hasComponent(name, type))
			throw new ParseException("Duplicate variable, Variable: " + name, lineIndex);
		if(!Character.isAlphabetic(firstCharacter))
			throw new ParseException("Variable names should start with a alphabetic character, Variable: " + name, lineIndex);
		if(Character.isUpperCase(firstCharacter))
			throw new ParseException("Variable names should start with a lowercase character, Variable: " + name, lineIndex);
		if(!name.matches(ALPHANUMERIC_REGEX))
			throw new ParseException("Variable names should contain only alphanumeric characters, Variable: " + name, lineIndex);
		if(lineData.length == 4) 
		{
			if(type.contains(KEYWORD_ARRAY))
				throw new ParseException("Invalid array declaration. Value can't be assigned to array", lineIndex);
			
			final String operator = lineData[2];
			if(!operator.equals(KEYWORD_VARIABLE_ASSIGN))
				throw new ParseException("Invalid assignment operator, Operator: " + operator, lineIndex);
		}
	}
	
	private void parseVariable(Code code, String[] lineData, int lineIndex) throws ParseException {
		final String name = lineData[1];
		final String type = toType(lineData[0]);
		final byte byteCodeName = (byte)(code.getComponentWithTypeCount(type) + VM_COMPONENTS_START);
		final byte byteCodeType = toByteCodeType(type);
		CodeComponent component = new CodeComponent(name, byteCodeName, type, byteCodeType, lineIndex);
		if(isArray(type))
			parseArraySize(component, lineData, lineIndex);
		else if(lineData.length == 4) // value is assigned
			parseVariableValue(component, lineData, lineIndex);
		
		code.addComponent(component);
	}
	
	private void parseArraySize(CodeComponent component, String[] lineData, int lineIndex) throws ParseException {
		final String[] typeData = lineData[0].split(KEYWORD_ARRAY);
		final String size = typeData[1];
		final byte byteCodeSize;
		try {
			byteCodeSize = (byte) Byte.parseByte(size);
		} catch (NumberFormatException e) {
			throw new ParseException("Invalid array size declaration, Size: " + size, lineIndex);
		}
		final CodeComponent arraySizeComponent = new CodeComponent(size, byteCodeSize, KEYWORD_ARRAY, (byte)0, lineIndex);
		component.addComponent(arraySizeComponent);
	}
	
	private void parseVariableValue(CodeComponent component, String[] lineData, int lineIndex) throws ParseException {
		final CodeComponent variableValueComponent = parseValueComponent(lineData[3], component.getType(), lineIndex);
		component.addComponent(variableValueComponent);
	}
	
	void parseFunctions(Code code, String[] codeLines) throws ParseException {
		for (int i = 0; i < codeLines.length; i++) {
			final String line = codeLines[i];
			if(!line.isEmpty()) {
				final String[] lineData = line.split(" ");
				if(lineData[0].equals(KEYWORD_FUNCTION)) {					
					validateFunctionClose(code, i);
					validateFunction(code, lineData, i);
					parseFunction(code, lineData, i);
					codeLines[i] = "";
				} else if (lineData[0].equals(KEYWORD_FUNCTION_END)) {			
					validateFunctionEnd(code, lineData, i);
					parseFunctionEnd(code, lineData, i);
					codeLines[i] = "";		
					validateFunctionClose(code, i);
				}
				if(i + 1 == codeLines.length)
					validateFunctionClose(code, i);
			}
		}
	}
	
	private void validateFunction(Code code, String[] lineData, int lineIndex) throws ParseException {
		if(lineData.length < 2)
			throw new ParseException("Invalid function declaration", lineIndex);
		
		final String functionName = lineData[1];
		final char firstFunctionCharacter = functionName.toCharArray()[0];		
		if(code.hasComponent(functionName, KEYWORD_FUNCTION))
			throw new ParseException("Duplicate function, Function: " + functionName, lineIndex);
		else if(!Character.isAlphabetic(firstFunctionCharacter))
			throw new ParseException("Function names should start with a alphabetic character, Function: " + functionName, lineIndex);
		else if(Character.isLowerCase(firstFunctionCharacter))
			throw new ParseException("Function names should start with a uppercase character, Function: " + functionName, lineIndex);
		else if(!functionName.matches(ALPHANUMERIC_REGEX))
			throw new ParseException("Function names should contain only alphanumeric characters, Function: " + functionName, lineIndex);	
		
		final List<String> parameterNames = new ArrayList<>();
		for (int i = 2; i < lineData.length; i += 2) {
			final String paramType = lineData[i];
			if(!isVariable(paramType))
				throw new ParseException("Invalid parameter type declaration, Function: " + functionName, lineIndex);
			
			try {
				final String paramName = lineData[i + 1];
				final char[] paramNameCharacters = paramName.toCharArray();
				if(parameterNames.contains(paramName))
					throw new ParseException("Duplicate parameter, Param: " + paramName + ", Function: " + functionName, lineIndex);		
				else if(paramNameCharacters[0] != '_')
					throw new ParseException("Parameter names should start with a _, Param: " + paramName + ", Function: " + functionName, lineIndex);
				else if(!Character.isAlphabetic(paramNameCharacters[1]))
					throw new ParseException("Parameter names should start with a alphabetic character, Param: " + paramName + ", Function: " + functionName, lineIndex);
				else if(Character.isUpperCase(paramNameCharacters[1]))
					throw new ParseException("Parameter names should start with a lowercase character, Param: " + paramName + ", Function: " + functionName, lineIndex);
				else if(!paramName.substring(1).matches(ALPHANUMERIC_REGEX))
					throw new ParseException("Parameter names should contain only alphanumeric characters, Param: " + paramName + ", Function: " + functionName, lineIndex);
				
				parameterNames.add(paramName);
			} catch(ArrayIndexOutOfBoundsException e) {
				throw new ParseException("Invalid parameter declaration, Function: " + functionName, lineIndex);
			}
		}
	}
	
	private void parseFunction(Code code, String[] lineData, int lineIndex) throws ParseException {		
		final String name = lineData[1];
		final String type = KEYWORD_FUNCTION;
		final byte byteCodeName = (byte)(code.getComponentWithTypeCount(type) + VM_COMPONENTS_START);
		final byte byteCodeType = toByteCodeType(type);
		CodeComponent function = new CodeComponent(name, byteCodeName, type, byteCodeType, lineIndex);
		for (int i = 2; (i + 1) < lineData.length; i += 2) {
			final String paramName = lineData[i + 1];
			final byte paramByteCodeName = (byte) (((i - 2) / 2) + VM_PARAMETERS_START);
			final String paramType = lineData[i];
			final byte paramByteCodeType = toByteCodeType(paramType);
			CodeComponent parameter = new CodeComponent(paramName, paramByteCodeName, paramType, paramByteCodeType, lineIndex);
			function.addComponent(parameter);
		}
		code.addComponent(function);
	}
	
	private void validateFunctionEnd(Code code, String[] lineData, int lineIndex) throws ParseException {
		if(lineData.length > 1)
			throw new ParseException("Invalid function end", lineIndex);
	}
	
	private void parseFunctionEnd(Code code, String[] lineData, int lineIndex) throws ParseException {		
		CodeComponent functionEnd = new CodeComponent(lineData[0], (byte) 0, lineData[0], (byte) 0, lineIndex);
		code.addComponent(functionEnd);
	}
	
	/**
	 * Checks if the function is a endless function.
	 */
	private void validateFunctionClose(Code code, int lineIndex) throws ParseException {
		if(code.getComponentWithTypeCount(KEYWORD_FUNCTION) > code.getComponentWithTypeCount(KEYWORD_FUNCTION_END)) {
			final CodeComponent function = code.getComponentWithType(KEYWORD_FUNCTION, code.getComponentWithTypeCount(KEYWORD_FUNCTION) - 1);
			throw new ParseException("Endless function, Function: " + function.getName(), lineIndex);
		}
		if(code.getComponentWithTypeCount(KEYWORD_FUNCTION) < code.getComponentWithTypeCount(KEYWORD_FUNCTION_END)) {
			throw new ParseException("endFunction should have a corresponding function", lineIndex);
		}
	}
	
	void parseInstructions(Code code, String[] codeLines) throws ParseException {
		for (int i = 0; i < codeLines.length; i++) {
			final String line = codeLines[i];
			if(!line.isEmpty()) {
				validateInstruction(code, line, i);
				final String[] lineData = line.split(" ");
				boolean parsed = false;
				if(!parsed)
					parsed = parseInstruction(code, lineData, i);
//				if(!parsed)
//					parsed = parseCondition(code, lineData, i);
//				if(!parsed)
//					parsed = parseOperation(code, lineData, i);
				if(!parsed)
					throw new ParseException("Invalid instruction", i);
				else
					codeLines[i] = "";
			}
		}
	}
	
	private void validateInstruction(Code code, String codeLine, int lineIndex) throws ParseException {
		boolean isInsideFunction = false;
		for (int i = 0; i < code.getComponentWithTypeCount(KEYWORD_FUNCTION); i++) {
			final CodeComponent function = code.getComponentWithType(KEYWORD_FUNCTION, i);
			final CodeComponent functionEnd = code.getComponent(code.getComponents().indexOf(function) + 1);
			if(lineIndex > function.getLineIndex() && lineIndex < functionEnd.getLineIndex())
				isInsideFunction = true;
		}
		if(!isInsideFunction)
			throw new ParseException("Instructions should be inside functions, Instruction: " + codeLine, lineIndex);
	}
	
	private boolean parseInstruction(Code code, String[] lineData, int lineIndex) throws ParseException {
		boolean isParsed = true;
		final String keyword = lineData[0];
		if (keyword.equals(KEYWORD_FUNCTION_CALL)) {
			validateFunctionCall(code, lineData, lineIndex);
			parseFunctionCall(code, lineData, lineIndex);
		} else if (keyword.equals(KEYWORD_FUNCTION_REPEAT)) {
			validateFunctionRepeat(code, lineData, lineIndex);
			parseFunctionRepeat(code, lineData, lineIndex);
		} else {
			isParsed = false;
		}
		return isParsed;
	}
	
	private void validateFunctionCall(Code code, String[] lineData, int lineIndex) throws ParseException {
		if(lineData.length < 2)
			throw new ParseException("Invalid function call", lineIndex);
		
		final String name = lineData[1];
		if(!code.hasComponent(name, KEYWORD_FUNCTION)) {
			throw new ParseException("Called function doesn't exist, Function: " + name, lineIndex);	
		}
		
		final CodeComponent function = code.getComponent(name, KEYWORD_FUNCTION);
		for (int i = 2; i < lineData.length; i++) {
			final CodeComponent variable = getVariableWithName(lineData[i], code, lineIndex);
			final CodeComponent param = function.getComponent(i - 2);
			if(!param.hasType(variable.getType()))
				throw new ParseException("The argument type must match the parameter type, Argument: " + lineData[i] + ", Parameter: " + param.getName() + ", Function: " + function.getName(), lineIndex);
		}
	}
	
	private void parseFunctionCall(Code code, String[] lineData, int lineIndex) throws ParseException {
		final String name = lineData[1];
		final String type = KEYWORD_FUNCTION_CALL;
		final byte byteCodeType = (byte)VM_KEYWORD_FUNCTION_CALL;
		CodeComponent functionCall = new CodeComponent(name, (byte) 0, type, byteCodeType, lineIndex);
		for (int i = 2; i < lineData.length; i++) {
			final String argumentName = lineData[i];
			final CodeComponent variable = getVariableWithName(argumentName, code, lineIndex);
			final byte argumentByteCodeName = variable.getByteCodeName();
			final String argumentType = variable.getType();
			final byte argumentByteCodeType = variable.getByteCodeType();
			CodeComponent argument = new CodeComponent(argumentName, argumentByteCodeName, argumentType, argumentByteCodeType, lineIndex);
			functionCall.addComponent(argument);
		}
		
		code.addComponent(functionCall);
	}
	
	private void validateFunctionRepeat(Code code, String[] lineData, int lineIndex) throws ParseException {
		if(lineData.length > 1)
			throw new ParseException("Invalid function repeat", lineIndex);
	}
	
	private void parseFunctionRepeat(Code code, String[] lineData, int lineIndex) throws ParseException {
		CodeComponent functionEnd = new CodeComponent(lineData[0], (byte) 0, lineData[0], (byte) 0, lineIndex);
		code.addComponent(functionEnd);
	}
	
	Code parseCode(String codeData) throws ParseException {
		final String[] codeLines = getLines(codeData);
		final Code code = new Code();
		createTypeRegistry(code, codeLines);
		for (int i = 0; i < codeLines.length; i++) {
			final String line = codeLines[i];
			if(line.isEmpty() || line.contains(KEYWORD_OPERATOR) || line.contains(KEYWORD_NATIVE))
				continue;
			parseCodeComponent(code, line, i);
		}
		return code;
	}
	
	void createTypeRegistry(Code code, String[] codeLines) {
		final CodeComponent typeRegistry = new CodeComponent(KEYWORD_TYPE_REGISTRY, (byte) 0, KEYWORD_TYPE_REGISTRY, (byte) 0, 0);
		int[] typeCountBuffer = new int[9];
		for (int i = 0; i < codeLines.length; i++) {
			final String line = codeLines[i];
			if(line.isEmpty())
				continue;
			final String[] lineData = line.split(" ");
			final byte type = toByteCodeType(lineData[0]);
			// toByteCodeType returns -1 if type doens't exist
			if(type > 0) {
				final int typeIndex = VM_TYPE_INT - type;
				typeCountBuffer[typeIndex]++;
			}
		}
		for (int i = 0; i < typeCountBuffer.length; i++) {
			if(i > 0)
				typeCountBuffer[i] += typeCountBuffer[i - 1];
			final byte typeCount = (byte) typeCountBuffer[i];
			final byte byteCodeType = (byte) (VM_TYPE_INT - i);
			final String type = toType(byteCodeType); 
			final CodeComponent typeComponent = new CodeComponent("" + typeCount, typeCount, type, byteCodeType, 0);
			typeRegistry.addComponent(typeComponent);
		}
		code.addComponent(typeRegistry);
	} 
	
	private void parseCodeComponent(Code code, String line, int lineIndex) throws ParseException {
		final String[] lineData = line.split(" ");
		boolean parsed = false;
		if(!parsed)
			parsed = parseFunctionComponent(code, lineData, lineIndex);
		if(!parsed)
			parsed = parseConditionComponent(code, lineData, lineIndex);
		if(!parsed)
			parsed = parseOperationComponent(code, lineData, lineIndex);
		if(!parsed)
			throw new ParseException("Invalid instruction", lineIndex);
	}
	
	boolean parseFunctionComponent(Code code, String[] lineData, int lineIndex) throws ParseException {
		final String keyword = lineData[0];
		if (keyword.equals(KEYWORD_FUNCTION)) {
			parseFunctionDeclaration(code, lineData, lineIndex);
		}
		else if (keyword.equals(KEYWORD_FUNCTION_CALL)) {
			parseFunctionCall(code, lineData, lineIndex);
		}
		else if (keyword.equals(KEYWORD_FUNCTION_REPEAT)) {
			parseKeywordComponent(code, lineData, lineIndex, KEYWORD_FUNCTION_REPEAT, (byte)VM_KEYWORD_FUNCTION_REPEAT);
		}
		else if (keyword.equals(KEYWORD_FUNCTION_END)) {
			parseKeywordComponent(code, lineData, lineIndex, KEYWORD_FUNCTION_END, (byte)VM_KEYWORD_FUNCTION);
		} else {
			return false;
		}
		return true;
	}

	private void parseFunctionDeclaration(Code code, String[] lineData, int lineIndex) throws ParseException {
		if(lineData.length < 2)
			throw new ParseException("Invalid function declaration", lineIndex);
		final String name = lineData[1];
		final String type = KEYWORD_FUNCTION;
		final byte byteCodeName = getUniqueByteCodeName(code, name, type);
		final byte byteCodeType = (byte)VM_KEYWORD_FUNCTION;
		CodeComponent function = new CodeComponent(name, byteCodeName, type, byteCodeType, lineIndex);
		for (int i = 2; i < lineData.length; i += 2) {
			try {
				final String paramName = lineData[i + 1];
				final byte paramByteCodeName = (byte) (((i - 2) / 2) + VM_PARAMETERS_START);
				final String paramType = lineData[i];
				final byte paramByteCodeType = toByteCodeType(paramType);
				if(!isVariable(paramType))
					throw new ParseException("Invalid parameter type declaration, Type: " + paramType, lineIndex);
				CodeComponent parameter = new CodeComponent(paramName, paramByteCodeName, paramType, paramByteCodeType, lineIndex);
				function.addComponent(parameter);
			} catch(ArrayIndexOutOfBoundsException e) {
				throw new ParseException("Invalid parameter declaration", lineIndex);
			}
		}
		
		code.addComponent(function);
		analyseFunctionDeclaration(code, function);
	}
	
	private void analyseFunctionDeclaration(Code code, CodeComponent function) throws ParseException {
		final String name = function.getName();
		final int lineIndex = function.getLineIndex();
		final char firstCharacter = name.toCharArray()[0];
		if(!Character.isAlphabetic(firstCharacter)) {
			throw new ParseException("Function names should start with a alphabetic character, Name: " + name, lineIndex);
		}
		else if(Character.isLowerCase(firstCharacter)) {
			throw new ParseException("Function names should start with a uppercase character, Name: " + name, lineIndex);
		}
		else if(!name.matches(ALPHANUMERIC_REGEX)) {
			throw new ParseException("Function names should contain only alphanumeric characters, Name: " + name, lineIndex);
		}
		else if(code.getComponentCount(name, KEYWORD_FUNCTION) > 1) {
			throw new ParseException("Duplicate function, Name: " + name, lineIndex);			
		}
		for(CodeComponent parameter : function.getComponents()) {
			analyseFunctionParameter(function, parameter);
		}
	}
	
	private void analyseFunctionParameter(CodeComponent function, CodeComponent parameter) throws ParseException {
		final String name = parameter.getName();
		final int lineIndex = parameter.getLineIndex();
		final char[] nameCharacters = name.toCharArray();
		if(nameCharacters[0] != '_') {
			throw new ParseException("Parameter names should start with a _, Name: " + name, lineIndex);
		}
		else if(!Character.isAlphabetic(nameCharacters[1])) {
			throw new ParseException("Parameter names should start with a alphabetic character, Name: " + name, lineIndex);
		}
		else if(Character.isUpperCase(nameCharacters[1])) {
			throw new ParseException("Parameter names should start with a lowercase character, Name: " + name, lineIndex);
		}
		else if(!name.substring(1).matches(ALPHANUMERIC_REGEX)) {
			throw new ParseException("Parameter names should contain only alphanumeric characters, Name: " + name, lineIndex);
		}
		else if(function.getComponentWithNameCount(name) > 1) {
			throw new ParseException("Duplicate parameter, Name: " + name, lineIndex);			
		}
	}

	boolean parseConditionComponent(Code code, String[] lineData, int lineIndex) throws ParseException {
		final String keyword = lineData[0];
		if(keyword.equals(KEYWORD_IF) || keyword.equals(KEYWORD_ELSE_IF)) {
			parseCondition(code, lineData, lineIndex);
		}
		else if (keyword.equals(KEYWORD_ELSE)) {
			parseKeywordComponent(code, lineData, lineIndex, KEYWORD_ELSE, (byte)VM_KEYWORD_ELSE);
		}
		else if (keyword.equals(KEYWORD_IF_END)) {
			parseKeywordComponent(code, lineData, lineIndex, KEYWORD_IF_END, (byte)VM_KEYWORD_IF);
		} else {
			return false;
		}
		return true;
	}

	private void parseCondition(Code code, String[] lineData, int lineIndex) throws ParseException {
		if(lineData.length != 4)
			throw new ParseException("Invalid condition declaration", lineIndex);
		final String name = lineData[2];
		if(!code.hasComponentWithName(name))
			throw new ParseException("Invalid comparison operator, Operator: " + name, lineIndex);				
		final byte byteCodeName = code.getComponentWithName(name).getByteCodeName();
		final String type = lineData[0];
		final byte byteCodeType = (byte) (type.equals(KEYWORD_IF) ? VM_KEYWORD_IF : VM_KEYWORD_ELSE_IF);
		CodeComponent condition = new CodeComponent(name, byteCodeName, type, byteCodeType, lineIndex);
		final String variable0Data = lineData[1];
		final CodeComponent variable0 = parseVariableComponent(variable0Data, code, lineIndex);
		condition.addComponent(variable0);
		final String variable1Data = lineData[3];
		try {
			final CodeComponent variable1 = parseVariableComponent(variable1Data, code, lineIndex);
			condition.addComponent(variable1);
		} catch (ParseException e) { // if a exception is thrown it's not a variable so it has to be a value
			final CodeComponent value = parseValueComponent(variable1Data, variable0.getType(), lineIndex);
			condition.addComponent(value);
		}
		code.addComponent(condition);
	}
	
	boolean parseOperationComponent(Code code, String[] lineData, int lineIndex) throws ParseException {
		if (lineData.length == 3) {
			final String name = lineData[1];
			if(!code.hasComponentWithName(name))
				throw new ParseException("Invalid operator, Operator: " + name, lineIndex);				
			final byte byteCodeName = code.getComponentWithName(name).getByteCodeName();
			final String type = TYPE_OPERATOR;
			CodeComponent operation = new CodeComponent(name, byteCodeName, type, (byte) 0, lineIndex);
			final String variable0Data = lineData[0];
			final CodeComponent variable0 = parseVariableComponent(variable0Data, code, lineIndex);
			operation.addComponent(variable0);
			final String variable1Data = lineData[2];
			try {
				final CodeComponent variable1 = parseVariableComponent(variable1Data, code, lineIndex);
				operation.addComponent(variable1);
			} catch (ParseException e) { // if a exception is thrown it's not a variable so it has to be a value
				final CodeComponent value = parseValueComponent(variable1Data, variable0.getType(), lineIndex);
				operation.addComponent(value);
			}
			code.addComponent(operation);
		} else {
			return false;
		}
		return true;
	}
	
	/**
	 * Creates a new {@link CodeComponent} for the keyword and adds it to the {@link Code}.
	 * The code line should only contain one keyword like "functionEnd".
	 * 
	 * @param code to add the keyword component to.
	 * @param lineData the code line with the keyword.
	 * @param lineIndex the index of the code line.
	 * @param keyword to add to the code.
	 * @param byteCodeKeyword the byte code representation of the keyword.
	 * @throws ParseException If the length of the code line is > 1.
	 */
	private void parseKeywordComponent(Code code, String[] lineData, int lineIndex, String keyword, byte byteCodeKeyword) throws ParseException {
		if(lineData.length > 1)
			throw new ParseException("Invalid instruction, Instruction: " + keyword, lineIndex);
		final CodeComponent keywordComponent = new CodeComponent(keyword, byteCodeKeyword, keyword, byteCodeKeyword, lineIndex);
		code.addComponent(keywordComponent);
	}
	
	/**
	 * Parses the specified variable access and returns a {@link CodeComponent} with the access data. 
	 * 
	 * @param name name of the variable, can contain array index.
	 * @param code that contains the code parsed until this line.
	 * @param lineIndex the code line that contains the specified variable access.
	 * @return A CodeComponent with the access data.
	 * @throws ParseException If the variable with the specified name doesn't exist.
	 * If it contains the array keyword but the array index can't be parsed.
	 * If a variable that is not of type array has an index.
	 */
	private CodeComponent parseVariableComponent(String name, Code code, int lineIndex) throws ParseException {
		CodeComponent arrayIndex = null;
		if(name.contains(KEYWORD_ARRAY)) {
			final String[] arrayData = name.split(KEYWORD_ARRAY);
			final String index = arrayData[1];
			name = arrayData[0];
			try {
				final byte byteCodeIndex = (byte) (Byte.parseByte(index) + VM_COMPONENTS_START);
				arrayIndex = new CodeComponent(index, byteCodeIndex, KEYWORD_ARRAY, (byte)0, lineIndex);
			} catch (NumberFormatException e1) {
				try {
					arrayIndex = getVariableWithName(index, code, lineIndex);
				} catch (ParseException e2) {
					throw new ParseException("Invalid array index, Index: " + index, lineIndex);					
				}
			}
		}
		final CodeComponent declaredVariable = getVariableWithName(name, code, lineIndex);
		final byte byteCodeName = declaredVariable.getByteCodeName();
		final String type = declaredVariable.getType();
		final byte byteCodeType = declaredVariable.getByteCodeType();
		final CodeComponent variable = new CodeComponent(name, byteCodeName, type, byteCodeType, lineIndex);
		if(arrayIndex != null) {
			if(!isArray(declaredVariable.getType()))
				throw new ParseException("A variable that is not of type array has an index, Name: " + name, lineIndex);
			variable.addComponent(arrayIndex);
		}
		return variable;
	}
	
	/**
	 * Returns the variable or function parameter with the specified name.
	 * 
	 * @param name to search for.
	 * @param code that contains the code parsed until this line.
	 * @param lineIndex the code line that contains the specified name.
	 * @return The variable or function parameter with the specified name.
	 * @throws ParseException If the variable doesn't exist.
	 */
	private CodeComponent getVariableWithName(String name, Code code, int lineIndex) throws ParseException {
		CodeComponent variable = null;
		if(code.hasComponentWithName(name)) {
			variable = code.getComponentWithName(name);
		} else {
			for (CodeComponent instruction : code.getComponents()) {
				if(instruction.hasType(KEYWORD_FUNCTION)) {
					if(instruction.hasComponentWithName(name)) {
						variable = instruction.getComponentWithName(name);
					}
				}
			}
		}
		if(variable == null)
			throw new ParseException("Variable doesn't exist, Variable: " + name, lineIndex);		
		return variable;
	}
	
	/**
	 * Parses a value of any type and returns a {@link CodeComponent} with the value's data.
	 * 
	 * @param rawValue the value to parse.
	 * @param type the type of the variable this value is compared or assigned to.
	 * @param lineIndex the code line that contains this value.
	 * @return A CodeComponent that contains the data of the specified value.
	 * @throws ParseException If the value type doesn't match the specified variable type.
	 * If the value is a char value with wrong declaration.
	 */
	private CodeComponent parseValueComponent(String rawValue, String type, int lineIndex) throws ParseException {
		String value = "";
		String valueType = "";
		if(rawValue.contains(KEYWORD_CHAR)) {
			valueType = TYPE_CHAR;
			char[] valueData = rawValue.toCharArray();
			if(valueData.length != 3)
				throw new ParseException("Invalid char value declaration", lineIndex);
			value = "" + (int)valueData[1];
		}
		else if(rawValue.equals(KEYWORD_TRUE) || rawValue.equals(KEYWORD_FALSE)) {
			valueType = TYPE_BOOL;
			value = rawValue.equals(KEYWORD_TRUE) ? "1" : "0";
		} else {
			try {
				valueType = type;
				double number = Double.parseDouble(rawValue);
				if(type.equals(TYPE_FIXED) || type.equals(TYPE_ARRAY_FIXED))
					number *= FIXED_POINT_ONE;
				value = "" + Math.round(number);
			} catch (NumberFormatException e) {
				throw new ParseException("The declared value type doesn't match the variable type", lineIndex);					
			}
		}
		final byte byteCodeValueType = toByteCodeType(valueType);
		return new CodeComponent(value, (byte)0, valueType, byteCodeValueType, lineIndex);
	}
	
	/**
	 * Returns a unique variable or function byte code name.
	 * 
	 * @param code of the component that will use the byte code name.
	 * @param name of the component that will use the byte code name.
	 * @param type of the component that will use the byte code name.
	 * @return Unique byte code name.
	 */
	private byte getUniqueByteCodeName(Code code, String name, String type) {
		byte byteCodeName = VM_COMPONENTS_START;
		final byte byteCodeType = toByteCodeType(type);
		// toByteCodeType returns -1 if type doens't exist
		if(byteCodeType > 0) {
			CodeComponent typeRegistry = code.getComponentWithType(KEYWORD_TYPE_REGISTRY);
			CodeComponent registry = typeRegistry.getComponentWithType(type);
			final int typeIndex = VM_TYPE_INT - byteCodeType;
			int previousTypeCount = 0; 
			if(typeIndex > 0) {
				previousTypeCount = typeRegistry.getComponent(typeIndex - 1).getByteCodeName(); 
			}
			byteCodeName += (byte) (registry.getComponentWithTypeCount(type) + previousTypeCount);
			final CodeComponent assignedName = new CodeComponent(name, byteCodeName, type, byteCodeType, 0);	
			registry.addComponent(assignedName);
		}
		return byteCodeName;
	}
	
	/**
	 * Is the specified type a variable or array type?
	 * 
	 * @param type to check.
	 * @return If the specified type is a variable type or not.
	 */
	private boolean isVariable(String type) {
		type = toType(type);
		return type.equals(TYPE_INT) || type.equals(TYPE_FIXED)
				|| type.equals(TYPE_BOOL) || type.equals(TYPE_CHAR)
				|| type.equals(TYPE_ARRAY_INT) || type.equals(TYPE_ARRAY_FIXED)
				|| type.equals(TYPE_ARRAY_BOOL) || type.equals(TYPE_ARRAY_CHAR);
	}
	
	/**
	 * Is the specified type a array type?
	 * A array type is also a variable type.
	 * 
	 * @param type to check.
	 * @return If the specified type is a array type or not.
	 */
	private boolean isArray(String type) {
		type = toType(type);
		return type.equals(TYPE_ARRAY_INT) || type.equals(TYPE_ARRAY_FIXED)
				|| type.equals(TYPE_ARRAY_BOOL) || type.equals(TYPE_ARRAY_CHAR);
	}
	
	/**
	 * Converts the specified type to it's byte code representation.
	 * 
	 * @param type to convert.
	 * @return The byte code representation of type. -1 if the type is unknown.
	 */
	byte toByteCodeType(String type) {
		type = toType(type);
		if(type.equals(TYPE_INT)) {
			return (byte) VM_TYPE_INT;
		}
		else if(type.equals(TYPE_FIXED)) {
			return (byte) VM_TYPE_FIXED;
		}
		else if(type.equals(TYPE_BOOL)) {
			return (byte) VM_TYPE_BOOL;
		}
		else if(type.equals(TYPE_CHAR)) {
			return (byte) VM_TYPE_CHAR;
		}
		else if(type.equals(TYPE_ARRAY_INT)) {
			return (byte) VM_TYPE_ARRAY_INT;
		}
		else if(type.equals(TYPE_ARRAY_FIXED)) {
			return (byte) VM_TYPE_ARRAY_FIXED;
		}
		else if(type.equals(TYPE_ARRAY_BOOL)) {
			return (byte) VM_TYPE_ARRAY_BOOL;
		}
		else if(type.equals(TYPE_ARRAY_CHAR)) {
			return (byte) VM_TYPE_ARRAY_CHAR;
		}
		else if(type.equals(KEYWORD_FUNCTION)) {
			return (byte) VM_TYPE_FUNCTION;
		}
		return -1;
	}
	
	/**
	 * Converts the specified byte code type to it's code representation.
	 * 
	 * @param type to convert.
	 * @return The code representation of type. Empty string if the type is unknown.
	 */
	String toType(byte type) {
		if(type == VM_TYPE_INT) {
			return TYPE_INT;
		}
		else if(type == VM_TYPE_FIXED) {
			return TYPE_FIXED;
		}
		else if(type == VM_TYPE_BOOL) {
			return TYPE_BOOL;
		}
		else if(type == VM_TYPE_CHAR) {
			return TYPE_CHAR;
		}
		else if(type == VM_TYPE_ARRAY_INT) {
			return TYPE_ARRAY_INT;
		}
		else if(type == VM_TYPE_ARRAY_FIXED) {
			return TYPE_ARRAY_FIXED;
		}
		else if(type == VM_TYPE_ARRAY_BOOL) {
			return TYPE_ARRAY_BOOL;
		}
		else if(type == VM_TYPE_ARRAY_CHAR) {
			return TYPE_ARRAY_CHAR;
		}
		else if(type == VM_TYPE_FUNCTION) {
			return KEYWORD_FUNCTION;
		}
		return "";
	}
	
	/**
	 * Returns the type of the declaration.
	 * If type contains a array keyword the array size is removed and type is returned.
	 * Else type is returned without changes.
	 * 
	 * @param type declaration.
	 * @return The type of the declaration.
	 */
	private String toType(String type) {
		// Remove the number after the array keyword if there is one
		if(type.contains(KEYWORD_ARRAY)) {
			return type.split(KEYWORD_ARRAY)[0] + KEYWORD_ARRAY;
		} else {
			return type;
		}
	}
	
	void analyseCode(Code code) throws ParseException {
		for (CodeComponent component : code.getComponents()) {
			if(component.hasType(KEYWORD_FUNCTION_CALL)) {
				analyseFunctionCall(code, component);
			}
		}
	}
	
	String toByteCode(Code code) {
		String byteCode = "";
		byteCode += writeComponentsIndex(code);
		for (CodeComponent component : code.getComponents()) {
			String byteCodeLine = "";
			byteCodeLine += writeFunction(code, component);
			byteCodeLine += writeOperation(component);
			byteCodeLine += writeCondition(component);
			if(!byteCodeLine.isEmpty())
				byteCode += byteCodeLine + VM_LINE_BREAK;
		}
		return byteCode;
	}
	
	private String writeComponentsIndex(Code code) {
		String componentsIndex = "";
		componentsIndex += writeComponentIndex(code, TYPE_INT);
		componentsIndex += writeComponentIndex(code, TYPE_FIXED);
		componentsIndex += writeComponentIndex(code, TYPE_BOOL);
		componentsIndex += writeComponentIndex(code, TYPE_CHAR);
		componentsIndex += writeComponentIndex(code, TYPE_ARRAY_INT);
		componentsIndex += writeComponentIndex(code, TYPE_ARRAY_FIXED);
		componentsIndex += writeComponentIndex(code, TYPE_ARRAY_BOOL);
		componentsIndex += writeComponentIndex(code, TYPE_ARRAY_CHAR);
		componentsIndex += writeComponentIndex(code, KEYWORD_FUNCTION);
		return componentsIndex;
	}
	
	private String writeComponentIndex(Code code, String type) {
		String componentIndex = "" + (char)toByteCodeType(type) + (char)code.getComponentWithTypeCount(type) + VM_LINE_BREAK;
		if(type.equals(KEYWORD_FUNCTION))
			return componentIndex; // the function index only needs the function count
		for (CodeComponent component : code.getComponents()) {
			if(component.hasType(type)) {
				componentIndex += "" + (char)component.getByteCodeName();
				if(component.hasComponentWithType(type)) { // does this variable have a value assigned to?
					final CodeComponent value = component.getComponentWithType(type);
					componentIndex += toByteCodeNumber(value.getName());
				}
				else if(component.hasComponentWithType(KEYWORD_ARRAY)) { // is this variable a array?
					final CodeComponent arraySize = component.getComponentWithType(KEYWORD_ARRAY);
					componentIndex += toByteCodeNumber(arraySize.getName());
				}
				componentIndex += "" + VM_LINE_BREAK;
			}
		}
		return componentIndex;
	}
	
	private String writeFunction(Code code, CodeComponent component) {
		String function = "";
		if(component.hasType(KEYWORD_FUNCTION) || component.hasType(KEYWORD_FUNCTION_REPEAT)) {
			// the endFunction is not needed as after a function there will always be another function or end of file
			function += (char)component.getByteCodeType();
		}
		else if(component.hasType(KEYWORD_FUNCTION_CALL)) {
			final CodeComponent calledFunction = code.getComponent(component.getName(), KEYWORD_FUNCTION);
			for (CodeComponent argument : component.getComponents())
				function += VM_KEYWORD_ARGUMENT + writeVariable(argument) + VM_LINE_BREAK;
			function += (char)calledFunction.getByteCodeName();
		}
		return function;
	}
	
	private String writeOperation(CodeComponent component) {
		String operation = "";
		if(component.hasType(TYPE_OPERATOR)) {
			operation += writeVariable(component.getComponent(0));
			operation += "" + (char)component.getByteCodeName();
			operation += writeVariable(component.getComponent(1));
		}
		return operation;
	}
	
	private String writeCondition(CodeComponent component) {
		String condition = "";
		if(component.hasType(KEYWORD_IF) || component.hasType(KEYWORD_ELSE_IF)) {
			condition += (char)component.getByteCodeType();
			condition += writeVariable(component.getComponent(0));
			condition += "" + (char)component.getByteCodeName();
			condition += writeVariable(component.getComponent(1));
		}
		else if (component.hasType(KEYWORD_ELSE) || component.hasType(KEYWORD_IF_END)) {
			condition += (char)component.getByteCodeType();
		}
		return condition;
	}
	
	/**
	 * Converts specified variable or value {@link CodeComponent} to it's byte code representation.
	 * 
	 * @param component to convert.
	 * @return The byte code representation of the specified CodeComponent.
	 */
	private String writeVariable(CodeComponent component) {
		String variable = "";
		final byte byteCodeName = component.getByteCodeName();
		if(byteCodeName >= VM_COMPONENTS_START) {
			variable += "" + (char)byteCodeName;
			if(component.getComponents().size() > 0) {
				final CodeComponent arrayIndex = component.getComponent(0);
				// if it has KEYWORD_ARRAY type then it's a number index, else it's a variable index
				if(arrayIndex.hasType(KEYWORD_ARRAY))
					variable += toByteCodeNumber(arrayIndex.getName());
				else
					variable += (char)arrayIndex.getByteCodeName();
			}
		} else { // if byte code name == 0, it's a value
			final String value = component.getName();
			variable += "" + toByteCodeNumber(value);
		}
		return variable;
	}
	
	/**
	 * Converts the value with default number characters to a number with byte code number characters.
	 * 
	 * @param value to convert.
	 * @return The byte code representation of the specified value.
	 */
	String toByteCodeNumber(String value) {
		value = "" + Math.round(Double.parseDouble(value));
		for (int i = 0; i <= 9; i++) {
			final char codeNumber = (char)('0' + i);
			final char byteCodeNumber = (char)(VM_NUMBER_0 + i);
			value = value.replace(codeNumber, byteCodeNumber);
		}
		return value;
	}
}