package com.johnsproject.joo;

public class JppVirtualMachine {

	public static final char TYPE_FUNCTION = 127;
	public static final char TYPE_INT = 126;
	public static final char TYPE_FIXED = 125;
	public static final char TYPE_BOOL = 124;
	public static final char TYPE_CHAR = 123;
	
	public static final char TYPE_ARRAY_INT = 122;
	public static final char TYPE_ARRAY_FIXED = 121;
	public static final char TYPE_ARRAY_BOOL = 120;
	public static final char TYPE_ARRAY_CHAR = 119;
	
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
	private int jooCodeIndex = 0;
	private byte[] componentCounts = new byte[9];
	private int[] jooComponents = new int[64];
	private int[] jooArrays = new int[126];
	private int[] jooParameters = new int[6];
	private char[] jooCode = new char[1024];
	
	// getters used by unit tests
	public int getJooCodeSize() {
		return jooCodeSize;
	}

	public int getJooCodeIndex() {
		return jooCodeIndex;
	}

	public byte[] getComponentCounts() {
		return componentCounts;
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
		for (int i = 0; i < jooCodeSize; i++) {
			if(jooCode[i] == LINE_BREAK) {
				jooCodeIndex = i + 1;
			} else {
				continue;
			}
			boolean isTypeDeclaration = false;
			for (int j = TYPE_FUNCTION; j >= TYPES_START; j--) {
				if(jooCode[jooCodeIndex] == j) {
					int typeIndex = j - TYPES_START;
					byte typeCount = (byte) jooCode[jooCodeIndex + 1];
					for (int k = 8; k >= typeIndex; k--) {
						typeCount += componentCounts[k];
					}
					componentCounts[typeIndex] = typeCount;
					isTypeDeclaration = true;
					break;
				}
			}
			if(!isTypeDeclaration) {
				if(jooCode[jooCodeIndex] == KEYWORD_FUNCTION) {
					int componentIndex = (int)jooCode[jooCodeIndex + 1];
					jooComponents[componentIndex] = jooCodeIndex + 3;
				} else {
					int componentIndex = (int)jooCode[jooCodeIndex];
					char componentType = parseComponentType(componentIndex);
					if(jooCode[jooCodeIndex + 1] != LINE_BREAK) {
						if(componentType == TYPE_CHAR) {
							jooComponents[componentIndex] = jooCode[jooCodeIndex + 1];
						} else {
							jooComponents[componentIndex] = parseNumber(jooCodeIndex + 1);
						}
					}
				}
			}
		}
	}	
	
	char parseComponentType(int componentIndex) {
		if(componentIndex >= componentCounts[TYPE_FUNCTION - TYPES_START]) {
			return TYPE_FUNCTION;
		}
		else if(componentIndex >= componentCounts[TYPE_INT - TYPES_START]) {
			return TYPE_INT;
		}
		else if(componentIndex >= componentCounts[TYPE_FIXED - TYPES_START]) {
			return TYPE_FIXED;
		}
		else if(componentIndex >= componentCounts[TYPE_BOOL - TYPES_START]) {
			return TYPE_BOOL;
		}
		else if(componentIndex >= componentCounts[TYPE_CHAR - TYPES_START]) {
			return TYPE_CHAR;
		}
		else if(componentIndex >= componentCounts[TYPE_ARRAY_INT - TYPES_START]) {
			return TYPE_ARRAY_INT;
		}
		else if(componentIndex >= componentCounts[TYPE_ARRAY_FIXED - TYPES_START]) {
			return TYPE_ARRAY_FIXED;
		}
		else if(componentIndex >= componentCounts[TYPE_ARRAY_BOOL - TYPES_START]) {
			return TYPE_ARRAY_BOOL;
		} else {
			return TYPE_ARRAY_CHAR;
		}
	}
	
	int parseNumber(int jooCodeIndex) {
		if((jooCode[jooCodeIndex] >= NUMBER_0) && (jooCode[jooCodeIndex] <= NUMBER_9)) {
			int result = 0;
			int index = 1;
			int lineLength = jooCodeIndex;
			for (; jooCode[lineLength] != LINE_BREAK; lineLength++);
			for (int i = lineLength - 1; i >= jooCodeIndex; i--) {
				result += (jooCode[i] - NUMBER_0) * index;
				index *= 10;
			}
			return result;
		}
		return -1;
	}
}
