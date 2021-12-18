package com.johnsproject.joo;

public class JooVirtualMachine {

	public static final char TYPE_INT = 127;
	public static final char TYPE_FIXED = 126;
	public static final char TYPE_BOOL = 125;
	public static final char TYPE_CHAR = 124;
	
	public static final char TYPE_ARRAY_INT = 123;
	public static final char TYPE_ARRAY_FIXED = 122;
	public static final char TYPE_ARRAY_BOOL = 121;
	public static final char TYPE_ARRAY_CHAR = 120;
	public static final char TYPE_FUNCTION = 119;
	
	public static final char KEYWORD_IF = 118;
	public static final char KEYWORD_ELSE_IF = 117;
	public static final char KEYWORD_ELSE = 116;
	public static final char KEYWORD_FUNCTION = 115;
	public static final char KEYWORD_FUNCTION_CALL = 114;
	public static final char KEYWORD_FUNCTION_REPEAT = 113;
	public static final char KEYWORD_PARAMETER = 112;
	
	public static final char LINE_BREAK = 101;
	
	public static final char NUMBER_9 = 100;
	public static final char NUMBER_8 = 99;
	public static final char NUMBER_7 = 98;
	public static final char NUMBER_6 = 97;
	public static final char NUMBER_5 = 96;
	public static final char NUMBER_4 = 95;
	public static final char NUMBER_3 = 94;
	public static final char NUMBER_2 = 93;
	public static final char NUMBER_1 = 92;
	public static final char NUMBER_0 = 91;
	
	public static final byte COMPONENTS_START = 1;
	public static final byte COMPONENTS_END = 65;
	public static final byte PARAMETERS_START = 66;
	public static final byte PARAMETERS_END = 72;
	public static final byte ARRAY_INDEXES_START = 73;
	public static final byte ARRAY_INDEXES_END = 127;
	public static final byte TYPES_START = 119;
	public static final byte TYPES_END = 127;
	public static final byte OPERATORS_START = 1;
	public static final byte OPERATORS_END = 127;
	public static final byte NATIVE_FUNCTIONS_START = 1;
	public static final byte NATIVE_FUNCTIONS_END = 127;
	
	public static final char FIXED_POINT = 8;	

	private int codeSize = 0;
	private byte[] componentIndexes = new byte[9];
	private int[] components = new int[COMPONENTS_END - COMPONENTS_START];
	/* The array memory can have up to int.maxValue length. 
	 * The default lenght is for compatibility with the arduino */
	private int[] arrays = new int[(ARRAY_INDEXES_END - ARRAY_INDEXES_START) * 2];
	private int[] parameters = new int[PARAMETERS_END - PARAMETERS_START];
	private boolean[] ifs = new boolean[6];
	/* The code can have up to int.maxValue length. 
	 * The default lenght is for compatibility with the arduino */
	private char[] code = new char[1024];
	
	// getters used by unit tests
	public int getCodeSize() {
		return codeSize;
	}

	public byte[] getComponentIndexes() {
		return componentIndexes;
	}

	public int[] getComponents() {
		return components;
	}

	public int[] getArrays() {
		return arrays;
	}

	public int[] getParameters() {
		return parameters;
	}

	public char[] getCode() {
		return code;
	}
	
	private String rawCode;
	
	public void setCode(String rawCode) {
		this.rawCode = rawCode;
		codeSize = 0;
		codeSize |= rawCode.charAt(0) << 8;
		codeSize |= rawCode.charAt(1);
		for (int i = 0; i < codeSize; i++) {
			code[i] = rawCode.charAt(i + 2);
		}
	}
	
	public void start() {
		initialize();
		interpretFunction((char)componentIndexes[TYPE_FUNCTION - TYPES_START]);
	}

	void initialize() {	
		resetVM();
		int codeIndex = 0;
		boolean functionFound = false;
		for (int i = 0; i < codeSize; i++) {
			if(code[i] == LINE_BREAK) {
				codeIndex = i + 1;
			}
			else if(code[i] == KEYWORD_FUNCTION) {
				functionFound = true;
			}
			else if (i != 0) {
				continue;
			}
			boolean isTypeDeclaration = parseTypeDeclaration(codeIndex);
			if(!isTypeDeclaration) {
				boolean isFunctionDeclaration = parseFunctionDeclaration(codeIndex);
				if(!isFunctionDeclaration && !functionFound) {
					parseVariableDeclaration(codeIndex);
				}
			}
		}
	}	
	
	void resetVM() {
		for (int i = 0; i < componentIndexes.length; i++) {
			componentIndexes[i] = 0;
		}
		for (int i = 0; i < components.length; i++) {
			components[i] = 0;
		}
		for (int i = 0; i < arrays.length; i++) {
			arrays[i] = 0;
		}
		for (int i = 0; i < parameters.length; i++) {
			parameters[i] = 0;
		}
		for (int i = 0; i < ifs.length; i++) {
			ifs[i] = false;
		}
	}
	
	boolean parseTypeDeclaration(int codeIndex) {
		for (int j = TYPES_END; j >= TYPES_START; j--) {
			if(code[codeIndex] == j) {
				int typeIndex = j - TYPES_START;
				byte typeCount = (byte) (code[codeIndex + 1] - COMPONENTS_START);
				typeCount += componentIndexes[typeIndex];
				// assign typeCount to typeIndex - 1 to make type count directly
				// accessible through componentIndexes[TYPE_ - TYPES_START]
				if(typeIndex > 0) {
					componentIndexes[typeIndex - 1] = typeCount;
				}
				return true;
			}
		}
		return false;
	}
	
	boolean parseFunctionDeclaration(int codeIndex) {
		if(code[codeIndex] == KEYWORD_FUNCTION) {
			// - 1 because the compiler adds 1 to avoid names with character 0
			int componentIndex = (int)code[codeIndex + 1] - COMPONENTS_START;
			// jooCodeIndex + 3 because KEYWORD_FUNCTION + name + lineBreak
			components[componentIndex] = codeIndex + 3;
			return true;
		}
		return false;
	}	
	
	boolean parseVariableDeclaration(int codeIndex) {
		int componentIndex = (int)code[codeIndex] - COMPONENTS_START;
		char componentType = interpretComponentType(componentIndex);
		char componentValue = code[codeIndex + 1];
		// if value is declared
		if(componentValue != LINE_BREAK) {
			// if variable is char or array there is no number behind the variable name
			// as the array sizes are expressed as a char to make code smaller
			if(componentType == TYPE_CHAR) {
				components[componentIndex] = componentValue;
			}
			// if is array
			else if(componentType <= TYPE_ARRAY_INT) {
				// assign array size to componentIndex + 1 to make arrayStartIndex be directly
				// accessible through jooComponents[array variable index]
				int arrayStartIndex = componentValue + components[componentIndex];
				components[componentIndex + 1] = arrayStartIndex;
			} else {
				components[componentIndex] = parseNumber(codeIndex + 1);
			}
			return true;
		}
		return false;
	}
	
	int parseNumber(int codeIndex) {
		if((code[codeIndex] >= NUMBER_0) && (code[codeIndex] <= NUMBER_9)) {
			int result = 0;
			int index = 100000;
			for (int i = codeIndex; code[i] != LINE_BREAK; i++) {
				index /= 10;
				result += (code[i] - NUMBER_0) * index;
			}
			return result / index;
		}
		return -1;
	}
	
	void interpretFunction(char functionIndex) {
		boolean if0 = ifs[0];
		boolean if1 = ifs[1];
		boolean if2 = ifs[2];
		boolean if3 = ifs[3];
		boolean if4 = ifs[4];
		boolean if5 = ifs[5];
		int startCodeIndex = components[functionIndex];
		int codeIndex = startCodeIndex;
		byte allIfs = -1;
		boolean canInterpretCode = true;
		for (int i = codeIndex; i < codeSize; i++) {
			if((code[i] == LINE_BREAK) && (i + 1 < codeSize)) {
				codeIndex = i + 1;
			} 
			else if (i != startCodeIndex) {
				continue;
			}
			else if(code[codeIndex] == KEYWORD_FUNCTION) {
				break;
			}
			switch (code[codeIndex]) {
			case KEYWORD_IF:
				allIfs = interpretIfOperation(codeIndex, allIfs);
				canInterpretCode = ifs[allIfs];
				break;
			case KEYWORD_ELSE_IF:
				canInterpretCode = interpretElseIfOperation(codeIndex, allIfs);
				break;
			case KEYWORD_ELSE:
				canInterpretCode = interpretElseOperation(allIfs, canInterpretCode);
				break;
			case KEYWORD_FUNCTION:				
				return;
			case KEYWORD_FUNCTION_CALL:
				if(canInterpretCode) {
					interpretFunctionCall(codeIndex, true);
				}
				break;
			case KEYWORD_FUNCTION_REPEAT:
				if(canInterpretCode) {
					interpretFunction(functionIndex);
					return;
				}
				break;
			default:
				if(canInterpretCode) {
					if(interpretComponentType(code[codeIndex] - COMPONENTS_START) == TYPE_FUNCTION) {
						interpretFunctionCall(codeIndex, false);
					} else {
						interpretVariableOperation(codeIndex);
					}
				}
				break;
			}
		}
		ifs[0] = if0;
		ifs[1] = if1;
		ifs[2] = if2;
		ifs[3] = if3;
		ifs[4] = if4;
		ifs[5] = if5;
	}
	
	byte interpretIfOperation(int codeIndex, byte allIfs) {
		if(code[codeIndex + 1] == LINE_BREAK) {
			if(allIfs > 0) {
				allIfs--;						
			} else {
				ifs[allIfs] = true;	
			}
		} else {
			allIfs++;
			ifs[allIfs] = interpretVariableOperation(codeIndex + 1);
		}
		return allIfs;
	}
	
	boolean interpretElseIfOperation(int codeIndex, byte allIfs) {
		if((allIfs > 0) && ifs[allIfs - 1]) {
			return interpretVariableOperation(codeIndex + 1);
		} else { // if there is only 1 if
			return interpretVariableOperation(codeIndex + 1);
		}
	}
	
	boolean interpretElseOperation(byte allIfs, boolean canInterpretCode) {
		if((allIfs > 0) && ifs[allIfs - 1]) {
			return !canInterpretCode;
		} else { // if there is only 1 if
			return !canInterpretCode;
		}
	}
	
	void interpretFunctionCall(int codeIndex, boolean nativeFunction) {
		if(nativeFunction) {
			codeIndex++;
		}
		int parameter0 = parameters[0];
		int parameter1 = parameters[1];
		int parameter2 = parameters[2];
		int parameter3 = parameters[3];
		int parameter4 = parameters[4];
		int parameter5 = parameters[5];
		byte currentParameter = 0;
		for (int i = codeIndex; code[i] != LINE_BREAK; i++) {
			if(code[i] == KEYWORD_PARAMETER) {
				int parameterIndex = code[i + 1] - COMPONENTS_START;
				char arrayIndex = code[i + 2];
				if((arrayIndex != KEYWORD_PARAMETER) && (arrayIndex != LINE_BREAK)) {
					// don't subtract ARRAY_INDICES_START because its used to know if it's an array index
					parameterIndex = components[parameterIndex] + arrayIndex;
				}
				parameters[currentParameter++] = parameterIndex;
			} else {
				continue;
			}
		}
		if(nativeFunction) {
			callNativeFunction(code[codeIndex]);
		} else {
			interpretFunction((char) (code[codeIndex] - COMPONENTS_START));
		}
		parameters[0] = parameter0;
		parameters[1] = parameter1;
		parameters[2] = parameter2;
		parameters[3] = parameter3;
		parameters[4] = parameter4;
		parameters[5] = parameter5;
	}
	
	boolean interpretVariableOperation(int codeIndex) {
		int variable0Index = interpretVariableIndex(code[codeIndex]);		
		char variable0Type = interpretComponentType(variable0Index);
		// if is array
		if(variable0Type <= TYPE_ARRAY_INT) {
			// if variable0 is an array there is a array index behind variable0Index
			codeIndex++;
		}
		char operator = code[codeIndex + 1];
		int variable1Value = interpretOperationValue(codeIndex);
		// if is not array
		if(variable0Type > TYPE_ARRAY_INT) {
			return interpretVariableOperation(components, variable0Index, variable0Type, operator, variable1Value);
		} else {
			int variable0ArrayIndex = interpretArrayIndex(code[codeIndex]);
			// get index in arrays array
			variable0Index = components[variable0Index] + variable0ArrayIndex;
			// convert array type to primitive type
			variable0Type = (char)(variable0Type + (TYPE_INT - TYPE_ARRAY_INT));
			return interpretVariableOperation(arrays, variable0Index, variable0Type, operator, variable1Value);
		}
	}
	
	int interpretOperationValue(int codeIndex) {
		int variable1Value = parseNumber(codeIndex + 2);
		// if is not number get variable value
		if(variable1Value == -1) {
			int variable1Index = interpretVariableIndex(code[codeIndex + 2]);
			// if TYPE_CHAR after the operator then it's a char value
			if((variable1Index + 1) == TYPE_CHAR) {
				variable1Value = code[codeIndex + 3];
			} else {
				char variable1Type = interpretComponentType(variable1Index);
				// if is not array
				if(variable1Type > TYPE_ARRAY_INT) {
					variable1Value = components[variable1Index];
				} else {
					int variable1ArrayIndex = interpretArrayIndex(code[codeIndex + 3]);
					variable1Value = arrays[components[variable1Index] + variable1ArrayIndex];
				}
			}
		}
		return variable1Value;
	}
	
	int interpretVariableIndex(int variableIndex) {
		variableIndex -= COMPONENTS_START;
		if((variableIndex >= PARAMETERS_START - COMPONENTS_START) && (variableIndex < ARRAY_INDEXES_START)) {
			variableIndex = parameters[(variableIndex - PARAMETERS_START) + COMPONENTS_START];
		}
		return variableIndex;
	}
	
	int interpretArrayIndex(int variableArrayIndex) {
		// if is number index
		if(variableArrayIndex >= ARRAY_INDEXES_START) {
			variableArrayIndex -= ARRAY_INDEXES_START;
		}
		// if is parameter index
		else if(variableArrayIndex >= PARAMETERS_START) {
			variableArrayIndex = parameters[variableArrayIndex - PARAMETERS_START];
		} else { // if is variable index
			variableArrayIndex = components[variableArrayIndex - COMPONENTS_START];
		}
		return variableArrayIndex;
	}
	
	char interpretComponentType(int componentIndex) {
		if(componentIndex < componentIndexes[TYPE_FIXED - TYPES_START]) {
			return TYPE_INT;
		}
		else if(componentIndex < componentIndexes[TYPE_BOOL - TYPES_START]) {
			return TYPE_FIXED;
		}
		else if(componentIndex < componentIndexes[TYPE_CHAR - TYPES_START]) {
			return TYPE_BOOL;
		}
		else if(componentIndex < componentIndexes[TYPE_ARRAY_INT - TYPES_START]) {
			return TYPE_CHAR;
		}
		else if(componentIndex < componentIndexes[TYPE_ARRAY_FIXED - TYPES_START]) {
			return TYPE_ARRAY_INT;
		}
		else if(componentIndex < componentIndexes[TYPE_ARRAY_BOOL - TYPES_START]) {
			return TYPE_ARRAY_FIXED;
		} 
		else if(componentIndex < componentIndexes[TYPE_ARRAY_CHAR - TYPES_START]) {
			return TYPE_ARRAY_BOOL;
		} 
		else if(componentIndex < componentIndexes[TYPE_FUNCTION - TYPES_START]) {
			return TYPE_ARRAY_CHAR;
		}
		else if (componentIndex < COMPONENTS_END){
			return TYPE_FUNCTION;
		}
		return 0;
	}
	
	public static final char STANDART_OPERATOR_START = 0 + OPERATORS_START;
	
	public static final char STANDART_COMPARATOR_SMALLER_EQUALS = 0 + STANDART_OPERATOR_START;
	public static final char STANDART_COMPARATOR_BIGGER_EQUALS = 1 + STANDART_OPERATOR_START;
	public static final char STANDART_COMPARATOR_SMALLER = 2 + STANDART_OPERATOR_START;
	public static final char STANDART_COMPARATOR_BIGGER = 3 + STANDART_OPERATOR_START;
	public static final char STANDART_COMPARATOR_EQUALS = 4 + STANDART_OPERATOR_START;
	public static final char STANDART_COMPARATOR_NOT_EQUALS = 5 + STANDART_OPERATOR_START;

	public static final char STANDART_OPERATOR_ASSIGN = 6 + STANDART_OPERATOR_START;
	public static final char STANDART_OPERATOR_ASSIGN_POSITIVE = 7 + STANDART_OPERATOR_START;
	public static final char STANDART_OPERATOR_ASSIGN_NEGATIVE = 8 + STANDART_OPERATOR_START;
	public static final char STANDART_OPERATOR_ASSIGN_INVERSE = 9 + STANDART_OPERATOR_START;
	public static final char STANDART_OPERATOR_ADD = 10 + STANDART_OPERATOR_START;
	public static final char STANDART_OPERATOR_SUBTRACT = 11 + STANDART_OPERATOR_START;
	public static final char STANDART_OPERATOR_MULTIPLY = 12 + STANDART_OPERATOR_START;
	public static final char STANDART_OPERATOR_DIVIDE = 13 + STANDART_OPERATOR_START;
	public static final char STANDART_OPERATOR_REMAINDER = 14 + STANDART_OPERATOR_START;
	public static final char STANDART_OPERATOR_BITWISE_AND = 15 + STANDART_OPERATOR_START;
	public static final char STANDART_OPERATOR_BITWISE_XOR = 16 + STANDART_OPERATOR_START;
	public static final char STANDART_OPERATOR_BITWISE_OR = 17 + STANDART_OPERATOR_START;
	public static final char STANDART_OPERATOR_BITWISE_NOT = 18 + STANDART_OPERATOR_START;
	public static final char STANDART_OPERATOR_BITSHIFT_LEFT = 19 + STANDART_OPERATOR_START;
	public static final char STANDART_OPERATOR_BITSHIFT_RIGHT = 20 + STANDART_OPERATOR_START;
	
	boolean interpretVariableOperation(int[] values, int variable0Index, char variable0Type, char operator, int variable1Value) {
		if (operator == STANDART_COMPARATOR_SMALLER_EQUALS) {
			return values[variable0Index] <= variable1Value;
		} else if (operator == STANDART_COMPARATOR_BIGGER_EQUALS) {
			return values[variable0Index] >= variable1Value;
		} else if (operator == STANDART_COMPARATOR_SMALLER) {
			return values[variable0Index] < variable1Value;
		} else if (operator == STANDART_COMPARATOR_BIGGER) {
			return values[variable0Index] > variable1Value;
		} else if (operator == STANDART_COMPARATOR_EQUALS) {
			return values[variable0Index] == variable1Value;
		} else if (operator == STANDART_COMPARATOR_NOT_EQUALS) {
			return values[variable0Index] != variable1Value;
		} else if (operator == STANDART_OPERATOR_ASSIGN) {
			values[variable0Index] = variable1Value;
		} else if (operator == STANDART_OPERATOR_ASSIGN_POSITIVE) {
			if (variable0Type == TYPE_BOOL) {
				values[variable0Index] = 1;
			} else {
				if (variable1Value < 0) {
					values[variable0Index] = -variable1Value;
				} else {
					values[variable0Index] = variable1Value;
				}
			}
		} else if (operator == STANDART_OPERATOR_ASSIGN_NEGATIVE) {
			if (variable0Type == TYPE_BOOL) {
				values[variable0Index] = 0;
			} else {
				if (variable1Value < 0) {
					values[variable0Index] = variable1Value;
				} else {
					values[variable0Index] = -variable1Value;
				}
			}
		} else if (operator == STANDART_OPERATOR_ASSIGN_INVERSE) {
			if (variable0Type == TYPE_BOOL) {
				if (variable1Value == 0) {
					values[variable0Index] = 1;
				} else {
					values[variable0Index] = 0;
				}
			} else {
				values[variable0Index] = -variable1Value;
			}
		} else if (operator == STANDART_OPERATOR_ADD) {
			values[variable0Index] += variable1Value;
		} else if (operator == STANDART_OPERATOR_SUBTRACT) {
			values[variable0Index] -= variable1Value;
		} else if (operator == STANDART_OPERATOR_MULTIPLY) {
			if (variable0Type == TYPE_FIXED) {
				long result = (long) values[variable0Index] * (long) variable1Value;
				values[variable0Index] = (int) (result >> FIXED_POINT);
			} else {
				values[variable0Index] *= variable1Value;
			}
		} else if (operator == STANDART_OPERATOR_DIVIDE) {
			if (variable0Type == TYPE_FIXED) {
				long result = (long) values[variable0Index] << FIXED_POINT;
				values[variable0Index] = (int) (result / variable1Value);
			} else {
				values[variable0Index] /= variable1Value;
			}
		} else if (operator == STANDART_OPERATOR_REMAINDER) {
			values[variable0Index] %= variable1Value;
		} else if (operator == STANDART_OPERATOR_BITWISE_AND) {
			values[variable0Index] &= variable1Value;
		} else if (operator == STANDART_OPERATOR_BITWISE_XOR) {
			values[variable0Index] ^= variable1Value;
		} else if (operator == STANDART_OPERATOR_BITWISE_OR) {
			values[variable0Index] |= variable1Value;
		} else if (operator == STANDART_OPERATOR_BITWISE_NOT) {
			values[variable0Index] = ~variable1Value;
		} else if (operator == STANDART_OPERATOR_BITSHIFT_LEFT) {
			values[variable0Index] <<= variable1Value;
		} else if (operator == STANDART_OPERATOR_BITSHIFT_RIGHT) {
			values[variable0Index] >>= variable1Value;
		}
		return false;
	}

	public static final char STANDART_NATIVE_START = 0 + NATIVE_FUNCTIONS_START;

	public static final char STANDART_NATIVE_EXECUTE = 0 + STANDART_NATIVE_START;
	public static final char STANDART_NATIVE_PRINT = 1 + STANDART_NATIVE_START;
	
	void callNativeFunction(char functionIndex) {
		switch (functionIndex) {
		case STANDART_NATIVE_EXECUTE:
			int programIndex = components[parameters[0]];
			int programStart = 0;
			for (int i = 0; i < programIndex; i++) {
				codeSize = 0;
				codeSize |= rawCode.charAt(programStart) << 8;
				codeSize |= rawCode.charAt(programStart + 1);
				programStart += codeSize + 2;
			}
			
			codeSize = 0;
			codeSize |= rawCode.charAt(programStart) << 8;
			codeSize |= rawCode.charAt(programStart + 1);
			for (int i = 0; i < codeSize; i++) {
				code[i] = rawCode.charAt(i + programStart + 2);
			}
			start();
			break;
		case STANDART_NATIVE_PRINT:
			System.out.println(components[parameters[0]]);
			break;
		}
	}
}
