package com.johnsproject.joo;

public class JppVirtualMachine {

	public static final char TYPE_INT = 127;
	public static final char TYPE_FIXED = 126;
	public static final char TYPE_BOOL = 125;
	public static final char TYPE_CHAR = 124;
	
	public static final char TYPE_ARRAY_INT = 123;
	public static final char TYPE_ARRAY_FIXED = 122;
	public static final char TYPE_ARRAY_BOOL = 121;
	public static final char TYPE_ARRAY_CHAR = 120;
	public static final char TYPE_FUNCTION = 119;
	
	public static final char KEYWORD_ELSE = 118;
	public static final char KEYWORD_IF = 117;
	public static final char KEYWORD_FUNCTION = 116;
	public static final char KEYWORD_FUNCTION_CALL = 115;
	public static final char KEYWORD_FUNCTION_REPEAT = 114;
	public static final char KEYWORD_PARAMETER = 113;

	public static final char COMPARATOR_SMALLER = 112;
	public static final char COMPARATOR_BIGGER = 111;
	public static final char COMPARATOR_SMALLER_EQUALS = 110;
	public static final char COMPARATOR_BIGGER_EQUALS = 109;
	public static final char COMPARATOR_EQUALS = 108;
	public static final char COMPARATOR_NOT_EQUALS = 107;
	
	public static final char OPERATOR_ADD = 106;
	public static final char OPERATOR_SUBTRACT = 105;
	public static final char OPERATOR_MULTIPLY = 104;
	public static final char OPERATOR_DIVIDE = 103;
	public static final char OPERATOR_SET_EQUALS = 102;
	
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
	
	public static final byte PARAMETERS_START = 85;
	public static final byte COMPONENTS_START = 1;
	public static final byte ARRAY_INDICES_START = 1;
	public static final byte TYPES_START = 119;
	
	public static final char FIXED_POINT = 8;	

	private int jooCodeSize = 0;
	private byte[] componentIndexes = new byte[9];
	private int[] jooComponents = new int[64];
	private int[] jooArrays = new int[126];
	private int[] jooParameters = new int[6];
	private char[] jooCode = new char[1024];
	
	// getters used by unit tests
	public int getJooCodeSize() {
		return jooCodeSize;
	}

	public byte[] getComponentIndexes() {
		return componentIndexes;
	}

	public int[] getJooComponents() {
		return jooComponents;
	}

	public int[] getJooArrays() {
		return jooArrays;
	}

	public int[] getJooParameters() {
		return jooParameters;
	}

	public char[] getJooCode() {
		return jooCode;
	}

	public void initialize(final char[] newJooCode) {
		jooCodeSize = newJooCode.length;
		for (int i = 0; i < jooCodeSize; i++) {
			jooCode[i] = newJooCode[i];
		}
		int jooCodeIndex = 0;
		boolean functionFound = false;
		for (int i = 0; i < jooCodeSize; i++) {
			if(jooCode[i] == LINE_BREAK) {
				jooCodeIndex = i + 1;
			}
			else if(jooCode[i] == KEYWORD_FUNCTION) {
				functionFound = true;
			}
			else if (i != 0) {
				continue;
			}
			boolean isTypeDeclaration = parseTypeDeclaration(jooCodeIndex);
			if(!isTypeDeclaration) {
				boolean isFunctionDeclaration = parseFunctionDeclaration(jooCodeIndex);
				if(!isFunctionDeclaration && !functionFound) {
					parseVariableDeclaration(jooCodeIndex);
				}
			}
		}
	}	
	
	boolean parseTypeDeclaration(int jooCodeIndex) {
		for (int j = TYPE_INT; j >= TYPES_START; j--) {
			if(jooCode[jooCodeIndex] == j) {
				int typeIndex = j - TYPES_START;
				byte typeCount = (byte) jooCode[jooCodeIndex + 1];
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
	
	boolean parseFunctionDeclaration(int jooCodeIndex) {
		if(jooCode[jooCodeIndex] == KEYWORD_FUNCTION) {
			// - 1 because the compiler adds 1 to avoid names with character 0
			int componentIndex = (int)jooCode[jooCodeIndex + 1] - 1;
			// jooCodeIndex + 3 because (KEYWORD_FUNCTION + name + lineBreak)
			jooComponents[componentIndex] = jooCodeIndex + 3;
			return true;
		}
		return false;
	}	
	
	boolean parseVariableDeclaration(int jooCodeIndex) {
		int componentIndex = (int)jooCode[jooCodeIndex] - 1;
		char componentType = parseComponentType(componentIndex);
		if(jooCode[jooCodeIndex + 1] != LINE_BREAK) {
			// if variable is char or array there is no number behind the variable name
			// as the array sizes are expressed as a char to make code smaller
			if((componentType == TYPE_CHAR) || (componentType <= TYPE_ARRAY_INT)) {
				jooComponents[componentIndex] = jooCode[jooCodeIndex + 1];
			} else {
				jooComponents[componentIndex] = parseNumber(jooCodeIndex + 1);
			}
			return true;
		}
		return false;
	}
	
	void interpretFunction(char functionIndex) {
		int jooCodeIndex = jooComponents[functionIndex];
		for (int i = jooCodeIndex; i < jooCodeSize; i++) {
			if(jooCode[i] == LINE_BREAK) {
				jooCodeIndex = i + 1;
			}
			else if(jooCode[i] == KEYWORD_FUNCTION) {
				break;
			} else {
				continue;
			}
			
		}
	}
	
	char parseComponentType(int componentIndex) {
		if(componentIndex <= componentIndexes[TYPE_FIXED - TYPES_START]) {
			return TYPE_INT;
		}
		else if(componentIndex <= componentIndexes[TYPE_BOOL - TYPES_START]) {
			return TYPE_FIXED;
		}
		else if(componentIndex <= componentIndexes[TYPE_CHAR - TYPES_START]) {
			return TYPE_BOOL;
		}
		else if(componentIndex <= componentIndexes[TYPE_ARRAY_INT - TYPES_START]) {
			return TYPE_CHAR;
		}
		else if(componentIndex <= componentIndexes[TYPE_ARRAY_FIXED - TYPES_START]) {
			return TYPE_ARRAY_INT;
		}
		else if(componentIndex <= componentIndexes[TYPE_ARRAY_BOOL - TYPES_START]) {
			return TYPE_ARRAY_FIXED;
		} 
		else if(componentIndex <= componentIndexes[TYPE_ARRAY_CHAR - TYPES_START]) {
			return TYPE_ARRAY_BOOL;
		} 
		else if(componentIndex <= componentIndexes[TYPE_FUNCTION - TYPES_START]) {
			return TYPE_ARRAY_CHAR;
		} else {
			return TYPE_FUNCTION;
		}
	}
	
	int parseNumber(int jooCodeIndex) {
		if((jooCode[jooCodeIndex] >= NUMBER_0) && (jooCode[jooCodeIndex] <= NUMBER_9)) {
			int result = 0;
			int index = 10000;
			for (int i = jooCodeIndex; jooCode[i] != LINE_BREAK; i++) {
				index /= 10;
				result += (jooCode[i] - NUMBER_0) * index;
			}
			return result / index;
		}
		return -1;
	}
}
