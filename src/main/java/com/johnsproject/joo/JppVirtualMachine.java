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

	private int codeSize = 0;
	private byte[] componentIndexes = new byte[9];
	private int[] components = new int[64];
	private int[] arrays = new int[126];
	private int[] parameters = new int[6];
	private boolean[] ifs = new boolean[6];
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
	
	public void start() {
		interpretFunction((char)componentIndexes[TYPE_FUNCTION - TYPES_START]);
	}

	public void initialize(final char[] newCode) {
		codeSize = newCode.length;
		for (int i = 0; i < codeSize; i++) {
			code[i] = newCode[i];
		}
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
	
	boolean parseTypeDeclaration(int codeIndex) {
		for (int j = TYPE_INT; j >= TYPES_START; j--) {
			if(code[codeIndex] == j) {
				int typeIndex = j - TYPES_START;
				byte typeCount = (byte) code[codeIndex + 1];
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
			int componentIndex = (int)code[codeIndex + 1] - 1;
			// jooCodeIndex + 3 because (KEYWORD_FUNCTION + name + lineBreak)
			components[componentIndex] = codeIndex + 3;
			return true;
		}
		return false;
	}	
	
	boolean parseVariableDeclaration(int codeIndex) {
		int componentIndex = (int)code[codeIndex] - 1;
		char componentType = parseComponentType(componentIndex);
		if(code[codeIndex + 1] != LINE_BREAK) {
			// if variable is char or array there is no number behind the variable name
			// as the array sizes are expressed as a char to make code smaller
			if(componentType == TYPE_CHAR) {
				components[componentIndex] = code[codeIndex + 1];
			}
			// if is array
			else if(componentType <= TYPE_ARRAY_INT) {
				// assign array size to componentIndex + 1 to make arrayStartIndex is directly
				// accessible through jooComponents[array variable index]
				int arrayStartIndex = code[codeIndex + 1] + components[componentIndex];
				components[componentIndex + 1] = arrayStartIndex;
			} else {
				components[componentIndex] = parseNumber(codeIndex + 1);
			}
			return true;
		}
		return false;
	}
	
	void interpretFunction(char functionIndex) {
		int startCodeIndex = components[functionIndex];
		int codeIndex = startCodeIndex;
		byte allIfs = -1;
		boolean canInterpretCode = true;
		for (int i = codeIndex; i < codeSize; i++) {
			if(code[i] == LINE_BREAK) {
				codeIndex = i + 1;
			} 
			else if (i != startCodeIndex) {
				continue;
			}
			switch (code[codeIndex]) {
			case KEYWORD_IF:
				if(code[codeIndex + 1] == LINE_BREAK) {
					if(allIfs > 0) {
						canInterpretCode = ifs[--allIfs];						
					} else {
						canInterpretCode = true;	
					}
				} else {
					allIfs++;
					canInterpretCode = interpretVariableOperation(codeIndex + 1);
					ifs[allIfs] = canInterpretCode;
				}				
				break;
			case KEYWORD_ELSE:
				if((allIfs > 0) && ifs[allIfs - 1]) {
					canInterpretCode = !ifs[allIfs];
				} else { // if there is only 1 if
					canInterpretCode = !ifs[allIfs];
				}
				break;
			case KEYWORD_FUNCTION_CALL:
				
				break;
			// this function ends where another function starts
			case KEYWORD_FUNCTION:				
				return;
			default:
				if(canInterpretCode) {
					interpretVariableOperation(codeIndex);
				}
				break;
			}
		}
	}
	
	boolean interpretVariableOperation(int codeIndex) {
		int variable0Index = code[codeIndex] - 1;
		int variable0ArrayIndex = code[codeIndex + 1] - 1;
		char variable0Type = parseComponentType(variable0Index);
		// if is array
		if(variable0Type <= TYPE_ARRAY_INT) {
			// if variable0 is an array there is a array index behind variable0Index
			codeIndex++;
		}
		char operator = code[codeIndex + 1];
		int variable1Value = parseOperationValue(codeIndex);
		// if is not array
		if(variable0Type > TYPE_ARRAY_INT) {
			return interpretVariableOperation(variable0Type, variable0Index, operator, variable1Value, components);
		} else {
			// get index in arrays array
			variable0Index = components[variable0Index] + variable0ArrayIndex;
			// convert array type to primitive type
			variable0Type = (char)(variable0Type + (TYPE_INT - TYPE_ARRAY_INT));
			return interpretVariableOperation(variable0Type, variable0Index, operator, variable1Value, arrays);
		}
	}
	
	int parseOperationValue(int codeIndex) {
		int variable1Index = code[codeIndex + 2] - 1;
		int variable1ArrayIndex = code[codeIndex + 3] - 1;
		char variable1Type = parseComponentType(variable1Index);
		int variable1Value = parseNumber(codeIndex + 2);
		// if is not number get variable value
		if(variable1Value == -1) {
			// if TYPE_CHAR after the operator then it's a char value
			if((variable1Index + 1) == TYPE_CHAR) {
				variable1Value = variable1ArrayIndex;
			} else {
				// if is not array
				if(variable1Type > TYPE_ARRAY_INT) {
					variable1Value = components[variable1Index];
				} else {
					variable1Value = arrays[components[variable1Index] + variable1ArrayIndex];
				}
			}
		}
		return variable1Value;
	}
	
	boolean interpretVariableOperation(char variable0Type, int variable0Index, char operator, int variable1Value, int[] values) {
		switch (operator) {
		case OPERATOR_ADD:
			values[variable0Index] += variable1Value;
			break;
		case OPERATOR_SUBTRACT:
			values[variable0Index] -= variable1Value;
			break;
		case OPERATOR_MULTIPLY:
			if(variable0Type == TYPE_FIXED) {
				long result = (long)values[variable0Index] * (long)variable1Value;
				values[variable0Index] = (int)(result >> FIXED_POINT);
			} else {
				values[variable0Index] *= variable1Value;
			}
			break;
		case OPERATOR_DIVIDE:
			if(variable0Type == TYPE_FIXED) {
				long result = (long)values[variable0Index] << FIXED_POINT;
				values[variable0Index] = (int)(result / variable1Value);
			} else {
				values[variable0Index] /= variable1Value;
			}
			break;
		case OPERATOR_SET_EQUALS:
			values[variable0Index] = variable1Value;
			break;
		case COMPARATOR_EQUALS:
			return values[variable0Index] == variable1Value;
		case COMPARATOR_NOT_EQUALS:
			return values[variable0Index] != variable1Value;
		case COMPARATOR_BIGGER:
			return values[variable0Index] > variable1Value;
		case COMPARATOR_SMALLER:
			return values[variable0Index] < variable1Value;
		case COMPARATOR_BIGGER_EQUALS:
			return values[variable0Index] >= variable1Value;
		case COMPARATOR_SMALLER_EQUALS:
			return values[variable0Index] <= variable1Value;
		}
		return false;
	}
	
	char parseComponentType(int componentIndex) {
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
		} else {
			return TYPE_FUNCTION;
		}
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
}
