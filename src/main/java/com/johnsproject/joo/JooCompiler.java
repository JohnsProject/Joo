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
	public static final String KEYWORD_COMMENT = "#";
	public static final String KEYWORD_ARRAY = ":";
	public static final String KEYWORD_CHAR = "'";
	public static final String KEYWORD_VARIABLE_ASSIGN = "=";
	// used in joo compiler settings only
	public static final String KEYWORD_TYPE_SEPARATOR = "|";

	public static final String KEYWORD_PARAMETER = "parameter";
	public static final String KEYWORD_OPERATOR = "operator";
	public static final String KEYWORD_TYPE_REGISTRY = "typeRegistry";
	
	
	public static final String TYPE_INT = "int";
	public static final String TYPE_FIXED = "fixed";
	public static final String TYPE_BOOL = "bool";
	public static final String TYPE_CHAR = "char";
	public static final String TYPE_ARRAY_INT = TYPE_INT + KEYWORD_ARRAY;
	public static final String TYPE_ARRAY_FIXED = TYPE_FIXED + KEYWORD_ARRAY;
	public static final String TYPE_ARRAY_BOOL = TYPE_BOOL + KEYWORD_ARRAY;
	public static final String TYPE_ARRAY_CHAR = TYPE_CHAR + KEYWORD_ARRAY;
	
	public static final String REGEX_ALPHANUMERIC = "[A-Za-z0-9]+";
	
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
	
	private String settingType;
	private Settings settings;
	private Code code;
	
	
	public JooCompiler() { }
	
	void setSettings(Settings settings) {
		this.settings = settings;
	}
	
	Code getCode() {
		return code;
	}

	public String compile(String path) {
		final String directoryPath = getDirectoryPath(path);
		String byteCode = "";
		try {
			final String settingsPath = directoryPath + PATH_COMPILER_SETTINGS;
			final String settingsData = FileUtil.read(settingsPath);
			settings = parseSettings(settingsData);
			final String codePath = path;
			final String codeData = FileUtil.read(codePath);
			code = parseCode(codeData);
			analyseCode(code);
			byteCode = toByteCode(code);
		} catch (ParseException e) {
			// text editors usually begin at line 1
			System.err.println(e.getMessage() + ", Line: " + (e.getErrorOffset() + 1));
		}
		return byteCode;
		
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
//		String compiledJooCode = "";
//		compiledJooCode = writeVariablesAndFunctions(compiledJooCode);
//		compiledJooCode = writeFunctionsAndInstructions(compiledJooCode);
//		return compiledJooCode;
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
			final String line = settingsLines[i];
			if(line.isEmpty())
				continue;
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
		if(settingType.equals(KEYWORD_OPERATOR)) {
			parseOperatorSetting(setting, lineData, lineIndex);
		}
		else if(settingType.equals(KEYWORD_FUNCTION)) {
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
			final String paramType = KEYWORD_PARAMETER;
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
			if(isVariable(type)) {
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
		createTypeRegistry(code, codeLines);
		for (int i = 0; i < codeLines.length; i++) {
			final String line = codeLines[i];
			if(line.isEmpty())
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
				final int typeIndex = JooVirtualMachine.TYPE_INT - type;
				typeCountBuffer[typeIndex]++;
			}
		}
		for (int i = 0; i < typeCountBuffer.length; i++) {
			if(i > 0)
				typeCountBuffer[i] += typeCountBuffer[i - 1];
			final byte typeCount = (byte) typeCountBuffer[i];
			final byte byteCodeType = (byte) (JooVirtualMachine.TYPE_INT - i);
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
		if (isVariable(typeData)) {
			if((lineData.length != 2) && (lineData.length != 4))
				throw new ParseException("Invalid variable declaration", lineIndex);
			final String name = lineData[1];
			final String type = toType(typeData);
			final byte byteCodeName = getUniqueByteCodeName(code, name, type);
			final byte byteCodeType = toByteCodeType(type);
			CodeComponent variable = new CodeComponent(name, byteCodeName, type, byteCodeType, lineIndex);
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
					byteCodeSize = (byte) Byte.parseByte(size);
				} catch (NumberFormatException e) {
					throw new ParseException("Invalid array size declaration, Size: " + size, lineIndex);
				}
				final CodeComponent arraySizeComponent = new CodeComponent(size, byteCodeSize, KEYWORD_ARRAY, (byte)0, lineIndex);
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
		final String type = KEYWORD_FUNCTION;
		final byte byteCodeName = getUniqueByteCodeName(code, name, type);
		final byte byteCodeType = (byte)JooVirtualMachine.KEYWORD_FUNCTION;
		CodeComponent function = new CodeComponent(name, byteCodeName, type, byteCodeType, lineIndex);
		for (int i = 2; i < lineData.length; i += 2) {
			try {
				final String paramName = lineData[i + 1];
				final byte paramByteCodeName = (byte) (((i - 2) / 2) + JooVirtualMachine.COMPONENTS_START);
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
	}

	private void parseFunctionCall(Code code, String[] lineData, int lineIndex) throws ParseException {
		if(lineData.length < 2)
			throw new ParseException("Invalid function call", lineIndex);
		final String name = lineData[1];
		final String type = KEYWORD_FUNCTION_CALL;
		final byte byteCodeType = (byte)JooVirtualMachine.KEYWORD_FUNCTION_CALL;
		CodeComponent functionCall = new CodeComponent(name, (byte) 0, type, byteCodeType, lineIndex);
		for (int i = 2; i < lineData.length; i++) {
			final String argumentName = lineData[i];
			final byte argumentByteCodeName = (byte) ((i - 2) + JooVirtualMachine.COMPONENTS_START);
			final CodeComponent variable = getVariableWithName(argumentName, code, lineIndex);
			final String argumentType = variable.getType();
			final byte argumentByteCodeType = variable.getByteCodeType();
			CodeComponent argument = new CodeComponent(argumentName, argumentByteCodeName, argumentType, argumentByteCodeType, lineIndex);
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
			if(!settings.hasSettingWithName(name))
				throw new ParseException("Invalid operator, Operator: " + name, lineIndex);				
			final byte byteCodeName = settings.getSettingWithName(name).getByteCodeName();
			final String type = KEYWORD_OPERATOR;
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
			throw new ParseException("Invalid instruction", lineIndex);
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
				final byte byteCodeIndex = (byte) (Byte.parseByte(index) + JooVirtualMachine.COMPONENTS_START);
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
				if(instruction.hasType(KEYWORD_FUNCTION)) {
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
		byte byteCodeName = JooVirtualMachine.COMPONENTS_START;
		final byte byteCodeType = toByteCodeType(type);
		// toByteCodeType returns -1 if type doens't exist
		if(byteCodeType > 0) {
			CodeComponent typeRegistry = code.getComponentWithType(KEYWORD_TYPE_REGISTRY);
			CodeComponent registry = typeRegistry.getComponentWithType(type);
			final int typeIndex = JooVirtualMachine.TYPE_INT - byteCodeType;
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
		else if(type.equals(KEYWORD_FUNCTION)) {
			return JooVirtualMachine.TYPE_FUNCTION;
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
		if(type == JooVirtualMachine.TYPE_INT) {
			return TYPE_INT;
		}
		else if(type == JooVirtualMachine.TYPE_FIXED) {
			return TYPE_FIXED;
		}
		else if(type == JooVirtualMachine.TYPE_BOOL) {
			return TYPE_BOOL;
		}
		else if(type == JooVirtualMachine.TYPE_CHAR) {
			return TYPE_CHAR;
		}
		else if(type == JooVirtualMachine.TYPE_ARRAY_INT) {
			return TYPE_ARRAY_INT;
		}
		else if(type == JooVirtualMachine.TYPE_ARRAY_FIXED) {
			return TYPE_ARRAY_FIXED;
		}
		else if(type == JooVirtualMachine.TYPE_ARRAY_BOOL) {
			return TYPE_ARRAY_BOOL;
		}
		else if(type == JooVirtualMachine.TYPE_ARRAY_CHAR) {
			return TYPE_ARRAY_CHAR;
		}
		else if(type == JooVirtualMachine.TYPE_FUNCTION) {
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
	
	void analyseCode(Code code) throws ParseException {
		for (CodeComponent component : code.getComponents()) {
			if(isVariable(component.getType())) {
				analyseVariable(code, component);
			} 
			else if(component.hasType(KEYWORD_FUNCTION)) {
				analyseFunctionDeclaration(code, component);
			}
			else if(component.hasType(KEYWORD_FUNCTION_CALL)) {
				analyseFunctionCall(code, component);
			}
		}
	}
	
	private void analyseVariable(Code code, CodeComponent variable) throws ParseException {
		final String name = variable.getName();
		final int lineIndex = variable.getLineIndex();
		final char firstCharacter = name.toCharArray()[0];
		if(!Character.isAlphabetic(firstCharacter)) {
			throw new ParseException("Variable names should start with a alphabetic character, Name: " + name, lineIndex);
		}
		else if(Character.isUpperCase(firstCharacter)) {
			throw new ParseException("Variable names should start with a lowercase character, Name: " + name, lineIndex);
		}
		else if(!name.matches(REGEX_ALPHANUMERIC)) {
			throw new ParseException("Variable names should contain only alphanumeric characters, Name: " + name, lineIndex);
		}
		else if(code.getComponentWithNameCount(name) > 1) {
			throw new ParseException("Duplicate variable, Name: " + name, lineIndex);			
		}
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
		else if(!name.matches(REGEX_ALPHANUMERIC)) {
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
		else if(!name.substring(1).matches(REGEX_ALPHANUMERIC)) {
			throw new ParseException("Parameter names should contain only alphanumeric characters, Name: " + name, lineIndex);
		}
		else if(function.getComponentWithNameCount(name) > 1) {
			throw new ParseException("Duplicate parameter, Name: " + name, lineIndex);			
		}
	}
	
	private void analyseFunctionCall(Code code, CodeComponent function) throws ParseException {
		final String name = function.getName();
		final int lineIndex = function.getLineIndex();
		if(code.getComponentCount(name, KEYWORD_FUNCTION) == 0) {
			throw new ParseException("Invalid function, Name: " + name, lineIndex);			
		}
	}
	
	String toByteCode(Code code) {
		String byteCode = "";
		byteCode += writeComponentsIndex(code);
		for (CodeComponent component : code.getComponents()) {
//			if(component.hasType(KEY)) {
//				
//			}
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
		String componentIndex = "" + (char)toByteCodeType(type) + (char)code.getComponentWithTypeCount(type) + JooVirtualMachine.LINE_BREAK;
		for (CodeComponent component : code.getComponents()) {
			if(component.hasType(type)) {
				componentIndex += "" + (char)component.getByteCodeName();
				if(component.hasComponentWithType(type)) { // does this variable have a value assigned to?
					final CodeComponent value = component.getComponentWithType(type);
					componentIndex += toByteCodeNumber(value.getName());
				}
				else if(component.hasComponentWithType(KEYWORD_ARRAY)) { // is this variable a array?
					final CodeComponent arraySize = component.getComponentWithType(KEYWORD_ARRAY);
					componentIndex += "" + (char)arraySize.getByteCodeName();
				}
				componentIndex += "" + JooVirtualMachine.LINE_BREAK;
				// only write function names, the function start index will be added after the code is written
			}
		}
		return componentIndex;
	}
	
	/**
	 * Converts the value with default number characters to a number with byte code number characters.
	 * 
	 * @param value to convert.
	 * @return The byte code representation of the specified value.
	 */
	String toByteCodeNumber(String value) {
		for (int i = 0; i <= 9; i++) {
			final char codeNumber = (char)('0' + i);
			final char byteCodeNumber = (char)(JooVirtualMachine.NUMBER_0 + i);
			value = value.replace(codeNumber, byteCodeNumber);
		}
		return value;
	}
}