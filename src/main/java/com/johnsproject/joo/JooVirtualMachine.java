package com.johnsproject.joo;

/*
MIT License

Copyright (c) 2019 John´s Project

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
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
	
	public static final char KEYWORD_ELSE = 118;
	public static final char KEYWORD_IF = 117;
	public static final char KEYWORD_FUNCTION = 116;
	public static final char KEYWORD_FUNCTION_CALL = 115;
	public static final char KEYWORD_FUNCTION_REPEAT = 114;
	public static final char KEYWORD_PARAMETER = 113;
	
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
	
	public static final byte PARAMETERS_START = 66;
	public static final byte COMPONENTS_START = 1;
	public static final byte ARRAY_INDEXES_START = 72;
	public static final byte TYPES_START = 119;
	public static final byte OPERATORS_START = 1;
	public static final byte NATIVE_FUNCTIONS_START = 1;
	
	public static final char FIXED_POINT = 8;	

	// operators need to be in the same order as in JooCompilerConfig.jcc
	public static final char COMPARATOR_SMALLER_EQUALS = 0 + OPERATORS_START;
	public static final char COMPARATOR_BIGGER_EQUALS = 1 + OPERATORS_START;
	public static final char COMPARATOR_SMALLER = 2 + OPERATORS_START;
	public static final char COMPARATOR_BIGGER = 3 + OPERATORS_START;
	public static final char COMPARATOR_EQUALS = 4 + OPERATORS_START;
	public static final char COMPARATOR_NOT_EQUALS = 5 + OPERATORS_START;
	
	public static final char OPERATOR_ADD = 6 + OPERATORS_START;
	public static final char OPERATOR_SUBTRACT = 7 + OPERATORS_START;
	public static final char OPERATOR_MULTIPLY = 8 + OPERATORS_START;
	public static final char OPERATOR_DIVIDE = 9 + OPERATORS_START;
	public static final char OPERATOR_SET_EQUALS = 10 + OPERATORS_START;
	
	public static final char FUNCTION_PRINT = 0 + NATIVE_FUNCTIONS_START;

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
			int componentIndex = (int)code[codeIndex + 1] - COMPONENTS_START;
			// jooCodeIndex + 3 because (KEYWORD_FUNCTION + name + lineBreak)
			components[componentIndex] = codeIndex + 3;
			return true;
		}
		return false;
	}	
	
	boolean parseVariableDeclaration(int codeIndex) {
		int componentIndex = (int)code[codeIndex] - COMPONENTS_START;
		char componentType = interpretComponentType(componentIndex);
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
			case KEYWORD_ELSE:
				canInterpretCode = interpretElseOperation(allIfs, canInterpretCode);
				break;
			case KEYWORD_FUNCTION:				
				return;
			case KEYWORD_FUNCTION_CALL:
				if(canInterpretCode) {
					interpretFunctionCall(codeIndex);
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
					interpretVariableOperation(codeIndex);
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
	
	boolean interpretElseOperation(byte allIfs, boolean canInterpretCode) {
		if((allIfs > 0) && ifs[allIfs - 1]) {
			return !canInterpretCode;
		} else { // if there is only 1 if
			return !canInterpretCode;
		}
	}
	
	void interpretFunctionCall(int codeIndex) {
		codeIndex++;
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
		interpretFunction((char) (code[codeIndex] - COMPONENTS_START));
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
			return interpretVariableOperation(variable0Type, variable0Index, operator, variable1Value, components);
		} else {
			int variable0ArrayIndex = interpretArrayIndex(code[codeIndex]);
			// get index in arrays array
			variable0Index = components[variable0Index] + variable0ArrayIndex;
			// convert array type to primitive type
			variable0Type = (char)(variable0Type + (TYPE_INT - TYPE_ARRAY_INT));
			return interpretVariableOperation(variable0Type, variable0Index, operator, variable1Value, arrays);
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
		} else {
			return TYPE_FUNCTION;
		}
	}
}
