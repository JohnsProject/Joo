package com.johnsproject.joo;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
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
	// add make types extencible like operators
	// joo is a game development programming language
	
	/*
	 Change joo project directory structure to
	 
	 -> ProjectName/
	 --> JooCompilerSettings.jcs
	 --> Source/
	 ---> Main.joo
	 ---> Other joo files
	 --> Build/
	 ---> ProjectName.cjoo
	 
	 Only the project folder path is passed and the compiler will search for
	 the Main.joo and the JooCompilerSettings.jcs file and compile it into the Build folder.
	*/

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
	public static final String KEYWORD_DEFINE = "define";
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
	public static final String KEYWORD_PARAMETER = ":";
	public static final String KEYWORD_COMMENT = "#";
	public static final String KEYWORD_ARRAY = ":";
	public static final String KEYWORD_CHAR = "'";
	public static final String KEYWORD_VARIABLE_ASSIGN = "=";
	// used in joo compiler settings only
	public static final String KEYWORD_TYPE_SEPARATOR = "|";

	public static final String TYPE_INSTRUCTION = "INSTRUCTION";	
	public static final String TYPE_PARAMETER = "PARAMETER";
	public static final String TYPE_OPERATOR = "OPERATOR";
	public static final String TYPE_FUNCTION = "FUNCTION";
	public static final String TYPE_ARRAY_SIZE = "ARRAY_SIZE";
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
	
	public static final String PATH_COMPILER_SETTINGS = "JooCompilerSettings.jcs";
	public static final String SETTINGS_TYPE_DECLARATION = "@";
	
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
	
	private List<Operator> operators;
	private List<NativeFunction> nativeFunctions;
	private Map<String, Variable>[] variables;
	private Map<String, Function> functions;
	private byte uniqueByteCodeName;
	private String settingType;
	private Settings settings;
	private Code code;
	
	
	public JooCompiler() { }
	
	void setSettings(Settings settings) {
		this.settings = settings;
	}

	public String compile(final String path) {
		final String directoryPath = getDirectoryPath(path);

		try {
			
			final String settingsPath = directoryPath + PATH_COMPILER_SETTINGS;
			final String settingsData = FileUtil.read(settingsPath);
			settings = parseSettings(settingsData);
			
			final String codePath = path;
			final String codeData = FileUtil.read(codePath);
			code = parseCode(codeData);
			
		} catch (ParseException e) {
			System.err.println(e.getMessage() + ", Line: " + e.getErrorOffset());
		}
		
//		parseConfig(directoryPath);
		
//		operators = new ArrayList<>();
//		nativeFunctions = new ArrayList<>();
//		variables = new Map[VM_TYPES.length];
//		functions = new LinkedHashMap<String, Function>();
//		// remove all tabs they are not needed at all
//		String code = FileUtil.read(path).replaceAll("\t", "");
//		code = includeIncludes(directoryPath, code);
//		code = replaceDefines(code);
//		final String[] codeLines = getLines(code);		
//		parseVariables(codeLines);
//		parseFunctions(codeLines);
		String compiledJooCode = "";
//		compiledJooCode = writeVariablesAndFunctions(compiledJooCode);
//		compiledJooCode = writeFunctionsAndInstructions(compiledJooCode);
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
	
	Settings parseSettings(String settingsData) throws ParseException {
		final String[] settingsLines = getLines(settingsData);
		final Settings settings = new Settings();
		for (int i = 0; i < settingsLines.length; i++) {
			String line = settingsLines[i];
			if(line.contains(SETTINGS_TYPE_DECLARATION)) {
				settingType = line.replace(SETTINGS_TYPE_DECLARATION, "").replace(" ", "");
			} else {
				Setting setting = parseSetting(line, i);
				if (settings.hasSettingWithName(setting.getName())) {
					throw new ParseException("Duplicate compiler setting, Name: " + setting.getName(), i);	
				} else {
					settings.addSetting(setting);
				}
			}
		}
		return settings;
	}
	
	private Setting parseSetting(String line, int lineIndex) throws ParseException {
		final String[] lineData = line.split(" ");
		if(lineData.length < 2)
			throw new ParseException("Invalid setting declaration", lineIndex);
		final String name = lineData[1];
		final byte byteCodeName;
		final String type = settingType;
		try {
			byteCodeName = Byte.parseByte(lineData[0]);
		} catch(NumberFormatException e) {
			throw new ParseException("Invalid byte code name, Name: " + lineData[0], lineIndex);
		}
		Setting setting = new Setting(name, byteCodeName, type);
		if(settingType.equals(TYPE_OPERATOR)) {
			parseOperatorSetting(setting, lineData, lineIndex);
		}
		else if(settingType.equals(TYPE_FUNCTION)) {
			parseFunctionSetting(setting, lineData, lineIndex);		
		}
		return setting;
	}
	
	private void parseOperatorSetting(Setting setting, String[] operatorData, int lineIndex) throws ParseException {
		if(operatorData.length != 3) {
			throw new ParseException("Invalid operator declaration", lineIndex);
		}
		parseTypeSetting(setting, operatorData[2], lineIndex);
	}
	
	private void parseFunctionSetting(Setting setting, String[] functionData, int lineIndex) throws ParseException {
		if(functionData.length == 2)
			return;
		for (int i = 2; i < functionData.length; i++) {
			// -2 so it starts at 0
			final String paramName = "param" + (i - 2);
			// -1 because byte code names start at 1
			final byte paramByteCodeName = (byte) (i - 1);
			final String paramType = TYPE_PARAMETER;
			Setting paramSetting = new Setting(paramName, paramByteCodeName, paramType);
			parseTypeSetting(paramSetting, functionData[i], lineIndex);
			setting.addSetting(paramSetting);
		}	
	}
	
	private void parseTypeSetting(Setting setting, String supportedType, int lineIndex) throws ParseException {
		final String[] supportedTypes;
		if(supportedType.contains(KEYWORD_TYPE_SEPARATOR)) {
			supportedTypes = supportedType.split(Pattern.quote(KEYWORD_TYPE_SEPARATOR));
		} else {
			supportedTypes = new String[] {supportedType};
		}	
		for (int i = 0; i < supportedTypes.length; i++) {
			String type = supportedTypes[i];
			if(typeExists(type)) {
				Setting typeSetting = new Setting(type, toByteCodeType(type), type);
				setting.addSetting(typeSetting);
			} else {
				throw new ParseException("Invalid supported type, Type: " + type, lineIndex);
			}
		}
	}
	
	Code parseCode(String codeData) throws ParseException {
		final String[] codeLines = getLines(codeData);
		final Code code = new Code();
		for (int i = 0; i < codeLines.length; i++) {
			parseCodeComponent(code, codeLines[i], i);
		}
		return code;
	}
	
	private void parseCodeComponent(Code code, String line, int lineIndex) throws ParseException {
		final String[] lineData = line.split(" ");
		boolean parsed = false;
		if(!parsed)
			parsed = parseVariableComponent(code, lineData, lineIndex);
		if(!parsed)
			parsed = parseFunctionComponent(code, lineData, lineIndex);
		if(!parsed)
			parsed = parseConditionComponent(code, lineData, lineIndex);
		if(!parsed)
			parsed = parseOperationComponent(code, lineData, lineIndex);
		if(!parsed)
			throw new ParseException("Invalid instruction", lineIndex);
	}
	
	boolean parseVariableComponent(Code code, String[] lineData, int lineIndex) throws ParseException {
		final String typeData = lineData[0];
		if (typeExists(typeData)) {
			if((lineData.length != 2) && (lineData.length != 4))
				throw new ParseException("Invalid variable declaration", lineIndex);
			final String name = lineData[1];
			final byte byteCodeName = getUniqueByteCodeName();
			final String type = toType(typeData);
			final byte byteCodeType = toByteCodeType(type);
			CodeComponent variable = new CodeComponent(name, byteCodeName, type, byteCodeType);
			boolean parsed = false;
			if(!parsed)
				parsed = parseVariableDeclarationWithoutValue(variable, lineData, lineIndex);
			if(!parsed)
				parsed = parseVariableDeclarationWithValue(variable, lineData, lineIndex); 
			code.addComponent(variable);
		} else {
			return false;
		}
		return true;
	}
	
	private boolean parseVariableDeclarationWithoutValue(CodeComponent variable, String[] lineData, int lineIndex) throws ParseException {
		if(lineData.length == 2) {
			if(variable.getType().contains(KEYWORD_ARRAY)) {
				final String[] typeData = lineData[0].split(KEYWORD_ARRAY);
				final String size = typeData[1];
				final byte byteCodeSize;
				try {
					byteCodeSize = (byte) (Byte.parseByte(size) + JooVirtualMachine.COMPONENTS_START);
				} catch (NumberFormatException e) {
					throw new ParseException("Invalid array size declaration, Size: " + size, lineIndex);
				}
				final CodeComponent arraySizeComponent = new CodeComponent(size, byteCodeSize, TYPE_ARRAY_SIZE, (byte)0);
				variable.addComponent(arraySizeComponent);
			}
		} else {
			return false;
		}
		return true;
	}
	
	private boolean parseVariableDeclarationWithValue(CodeComponent variable, String[] lineData, int lineIndex) throws ParseException {
		if(lineData.length == 4) {
			if(variable.getType().contains(KEYWORD_ARRAY))
				throw new ParseException("Invalid array declaration. Value can't be assigned to array", lineIndex);
			if(lineData[2].equals(KEYWORD_VARIABLE_ASSIGN)) {
				final CodeComponent variableValueComponent = parseValueComponent(lineData[3], variable.getType(), lineIndex);
				variable.addComponent(variableValueComponent);
			} else {
				throw new ParseException("Invalid assignment operator, Operator: " + lineData[2], lineIndex);					
			}
		} else {
			return false;
		}
		return true;
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
			parseKeywordComponent(code, lineData, lineIndex, KEYWORD_FUNCTION_REPEAT, (byte)JooVirtualMachine.KEYWORD_FUNCTION_REPEAT);
		}
		else if (keyword.equals(KEYWORD_FUNCTION_END)) {
			parseKeywordComponent(code, lineData, lineIndex, KEYWORD_FUNCTION_END, (byte)JooVirtualMachine.KEYWORD_FUNCTION);
		} else {
			return false;
		}
		return true;
	}

	private void parseFunctionDeclaration(Code code, String[] lineData, int lineIndex) throws ParseException {
		if(lineData.length < 2)
			throw new ParseException("Invalid function declaration", lineIndex);
		final String name = lineData[1];
		final byte byteCodeName = getUniqueByteCodeName();
		final String type = TYPE_FUNCTION;
		final byte byteCodeType = (byte)JooVirtualMachine.KEYWORD_FUNCTION;
		CodeComponent function = new CodeComponent(name, byteCodeName, type, byteCodeType);
		for (int i = 2; i < lineData.length; i += 2) {
			try {
				final String paramName = lineData[i + 1];
				final byte paramByteCodeName = (byte) (((i - 2) / 2) + JooVirtualMachine.COMPONENTS_START);
				final String paramType = lineData[i];
				final byte paramByteCodeType = toByteCodeType(paramType);
				if(!typeExists(paramType))
					throw new ParseException("Invalid parameter type declaration, Type: " + paramType, lineIndex);
				CodeComponent parameter = new CodeComponent(paramName, paramByteCodeName, paramType, paramByteCodeType);
				function.addComponent(parameter);
			} catch(ArrayIndexOutOfBoundsException e) {
				throw new ParseException("Invalid parameter declaration", lineIndex);
			}
		}
		code.addComponent(function);
	}

	private void parseFunctionCall(Code code, String[] lineData, int lineIndex) throws ParseException {
		if(lineData.length < 2)
			throw new ParseException("Invalid function call", lineIndex);
		final String name = lineData[1];
		final String type = KEYWORD_FUNCTION_CALL;
		final byte byteCodeType = (byte)JooVirtualMachine.KEYWORD_FUNCTION_CALL;
		CodeComponent functionCall = new CodeComponent(name, (byte) 0, type, byteCodeType);
		for (int i = 2; i < lineData.length; i++) {
			final String argumentName = lineData[i];
			final byte argumentByteCodeName = (byte) ((i - 2) + JooVirtualMachine.COMPONENTS_START);
			final CodeComponent variable = getVariableWithName(argumentName, code, lineIndex);
			final String argumentType = variable.getType();
			final byte argumentByteCodeType = variable.getByteCodeType();
			CodeComponent argument = new CodeComponent(argumentName, argumentByteCodeName, argumentType, argumentByteCodeType);
			functionCall.addComponent(argument);
		}
		code.addComponent(functionCall);
	}

	boolean parseConditionComponent(Code code, String[] lineData, int lineIndex) throws ParseException {
		final String keyword = lineData[0];
		if(keyword.equals(KEYWORD_IF) || keyword.equals(KEYWORD_ELSE_IF)) {
			parseCondition(code, lineData, lineIndex);
		}
		else if (keyword.equals(KEYWORD_ELSE)) {
			parseKeywordComponent(code, lineData, lineIndex, KEYWORD_ELSE, (byte)JooVirtualMachine.KEYWORD_ELSE);
		}
		else if (keyword.equals(KEYWORD_IF_END)) {
			parseKeywordComponent(code, lineData, lineIndex, KEYWORD_IF_END, (byte)JooVirtualMachine.KEYWORD_IF);
		} else {
			return false;
		}
		return true;
	}

	private void parseCondition(Code code, String[] lineData, int lineIndex) throws ParseException {
		if(lineData.length != 4)
			throw new ParseException("Invalid condition declaration", lineIndex);
		final String name = lineData[2];
		if(!settings.hasSettingWithName(name))
			throw new ParseException("Invalid comparison operator, Operator: " + name, lineIndex);				
		final byte byteCodeName = settings.getSettingWithName(name).getByteCodeName();
		final String type = lineData[0];
		final byte byteCodeType = (byte) (type.equals(KEYWORD_IF) ? JooVirtualMachine.KEYWORD_IF : JooVirtualMachine.KEYWORD_ELSE_IF);
		CodeComponent condition = new CodeComponent(name, byteCodeName, type, byteCodeType);
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
			if(!settings.hasSettingWithName(name))
				throw new ParseException("Invalid operator, Operator: " + name, lineIndex);				
			final byte byteCodeName = settings.getSettingWithName(name).getByteCodeName();
			final String type = TYPE_OPERATOR;
			CodeComponent operation = new CodeComponent(name, byteCodeName, type, (byte) 0);
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
			throw new ParseException("Invalid instruction", lineIndex);
		final CodeComponent keywordComponent = new CodeComponent(keyword, byteCodeKeyword, keyword, byteCodeKeyword);
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
				final byte byteCodeIndex = (byte) (Byte.parseByte(index) + JooVirtualMachine.COMPONENTS_START);
				arrayIndex = new CodeComponent(index, byteCodeIndex, KEYWORD_ARRAY, (byte)0);
			} catch (NumberFormatException e1) {
				try {
					arrayIndex = getVariableWithName(index, code, lineIndex);
				} catch (ParseException e2) {
					throw new ParseException("Invalid array index, Index: " + index, lineIndex);					
				}
			}
		}
		final CodeComponent declaredVariable = getVariableWithName(name, code, lineIndex);
		final CodeComponent variable = declaredVariable.clone();
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
				if(instruction.hasType(TYPE_FUNCTION)) {
					if(instruction.hasComponentWithName(name)) {
						variable = instruction.getComponentWithName(name);
					}
				}
			}
		}
		if(variable == null)
			throw new ParseException("Invalid variable, Name: " + name, lineIndex);		
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
				if(type.equals(TYPE_FIXED))
					number *= FIXED_POINT;
				value = "" + Math.round(number);
			} catch (NumberFormatException e) {
				throw new ParseException("The declared value type doesn't match the variable type", lineIndex);					
			}
		}
		final byte byteCodeValueType = toByteCodeType(valueType);
		return new CodeComponent(value, (byte)0, valueType, byteCodeValueType);
	}
	
	/**
	 * Returns a unique byte code name.
	 * 
	 * @return Unique byte code name.
	 */
	private byte getUniqueByteCodeName() {
		return (byte) ((uniqueByteCodeName++) + JooVirtualMachine.COMPONENTS_START);
	}
	
	/**
	 * Does the specified type exist?
	 * 
	 * @param type to check.
	 * @return If the specified type exists or not.
	 */
	private boolean typeExists(String type) {
		type = toType(type);
		return type.equals(TYPE_INT) || type.equals(TYPE_FIXED)
				|| type.equals(TYPE_BOOL) || type.equals(TYPE_CHAR)
				|| type.equals(TYPE_ARRAY_INT) || type.equals(TYPE_ARRAY_FIXED)
				|| type.equals(TYPE_ARRAY_BOOL) || type.equals(TYPE_ARRAY_CHAR);
	}
	
	/**
	 * Is the specified type a array type?
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
	 * @return The byte code representation of type.
	 */
	private byte toByteCodeType(String type) {
		type = toType(type);
		if(type.equals(TYPE_INT)) {
			return JooVirtualMachine.TYPE_INT;
		}
		else if(type.equals(TYPE_FIXED)) {
			return JooVirtualMachine.TYPE_FIXED;
		}
		else if(type.equals(TYPE_BOOL)) {
			return JooVirtualMachine.TYPE_BOOL;
		}
		else if(type.equals(TYPE_CHAR)) {
			return JooVirtualMachine.TYPE_CHAR;
		}
		else if(type.equals(TYPE_ARRAY_INT)) {
			return JooVirtualMachine.TYPE_ARRAY_INT;
		}
		else if(type.equals(TYPE_ARRAY_FIXED)) {
			return JooVirtualMachine.TYPE_ARRAY_FIXED;
		}
		else if(type.equals(TYPE_ARRAY_BOOL)) {
			return JooVirtualMachine.TYPE_ARRAY_BOOL;
		}
		else if(type.equals(TYPE_ARRAY_CHAR)) {
			return JooVirtualMachine.TYPE_ARRAY_CHAR;
		}
		return -1;
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
	
	/**
	 * This method splits up the code to a array of code lines. 
	 * It removes unnecessary characters like '\t' and '\r' and splits the code at 
	 * the '\n' characters. It also filters comment and empty lines and removes 
	 * the comments in the lines that also contain code.
	 *  
	 * @param code to get lines from.
	 * @return Array of code lines. 
	 */
	String[] getLines(String code) {
		code = code.replace("\t", "");
		code = code.replace("\r", "");
		String[] rawCodeLines = code.split(LINE_BREAK);
		List<String> codeLines = new ArrayList<String>();
		for (String line : rawCodeLines) {
			if(!line.isEmpty()) {
				if(line.contains(KEYWORD_COMMENT)) {
					String[] lineData = line.split(KEYWORD_COMMENT);
					if(lineData[0].isEmpty()) { // does this line only contain a comment?
						continue;
					} else { // remove the comment, only code is needed
						line = lineData[0];
					}
				}
				codeLines.add(line);
			}
		}
		return codeLines.toArray(new String[0]);
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
				directoryPath += filePath;
				if(FileUtil.fileExists(directoryPath)) {
					code += FileUtil.read(directoryPath);
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
			if(codeLine[0].equals(KEYWORD_DEFINE)) {
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
	
	void parseConfig(String directoryPath) {
		directoryPath += PATH_COMPILER_SETTINGS;
		final String config;
		if(FileUtil.fileExists(directoryPath)) {
			config = FileUtil.read(directoryPath);
		} else {
			config = FileUtil.read(PATH_COMPILER_SETTINGS);
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
			if(codeLine[0].contains(arrayType + KEYWORD_ARRAY_START)) {
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
		String arraySize = codeLine[0].replace(arrayType + KEYWORD_ARRAY_START, "");
		if(arraySize.contains(KEYWORD_ARRAY_END)) {
			arraySize = arraySize.replace(KEYWORD_ARRAY_END, "");
		} else {
			System.err.println("Error, Line : " + line + ", Message : Unclosed array size declaration, Name : " + variableName);
		}
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
				parseFunctionCallParameters(lineIndex, codeLine, instruction, calledFunctionParams);
			}
		} else {
			System.err.println("Error, Line : " + lineIndex + ", Message : Unknown function call, Name : " + functionName);
		}
	}
	
	private void parseFunctionCallParameters(final int lineIndex, final String[] codeLine, Instruction instruction, final Map<String, String> calledFunctionParams) {
		for (int i = 2; i < codeLine.length; i++) {
			// parsing in case of array with index as parameter will be done later
			final String variableName = codeLine[i];
			instruction.addParameter(variableName);
		}
		int paramIndex = 0;
		final List<String> instructionParams = instruction.getParameters();
		for (String paramType : calledFunctionParams.values()) {
			final String instructionParamName = instructionParams.get(paramIndex++);
			final int typeIndex = getTypeIndex(paramType);
			if(!variables[typeIndex].containsKey(instructionParamName)) {
				System.err.println("Error, Line : " + lineIndex + ", Message : Function call parameter not equal to function parameter type, ParameterName : " + instructionParamName);					
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
		if(rawVariableData.contains(KEYWORD_ARRAY_START)) {
			String[] variableData = rawVariableData.replace(KEYWORD_ARRAY_END, "").split(Pattern.quote(KEYWORD_ARRAY_START));
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
			if(variableName.contains(KEYWORD_ARRAY_START)){
				variableName = variableName.split(Pattern.quote(KEYWORD_ARRAY_START))[0];
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
		if(rawVariableData.contains(KEYWORD_ARRAY_START)) {
			String[] variableData = rawVariableData.replace(KEYWORD_ARRAY_END, "").split(Pattern.quote(KEYWORD_ARRAY_START));
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
	String[] splitCodeLine(final String line) {
		final String[] rawCodeLine = line.split(" ");
		int arraySize = 0;
		for (int i = 0; i < rawCodeLine.length; i++) {
			if(!rawCodeLine[i].isEmpty()) {
				arraySize++;
			}
		}
		String[] codeLine = new String[arraySize];
		for (int i = 0, j = 0; i < rawCodeLine.length; i++) {
			if(!rawCodeLine[i].isEmpty()) {
				codeLine[j] = rawCodeLine[i];
				j++;
			}
		}
		return codeLine;
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
		return codeLine.isEmpty() || codeLine.contains(KEYWORD_COMMENT);
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
					} else {
						for (int i = 0; i < nativeFunctions.size(); i++) {
							if(nativeFunctions.get(i).getName().equals(instruction.getFunctionName())) {
								code += "" + JooVirtualMachine.KEYWORD_FUNCTION_CALL + (char)(i + JooVirtualMachine.NATIVE_FUNCTIONS_START);
							}
						}
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
		for (int i = 0; i <= 9; i++) {
			value = value.replace((char)('0' + i), (char)(JooVirtualMachine.NUMBER_0 + i));
		}
		return value;
	}
}