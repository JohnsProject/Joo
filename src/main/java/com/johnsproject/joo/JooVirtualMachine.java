package com.johnsproject.joo;

public class JooVirtualMachine {
	
	public static final char KEYWORD_ELSE = 38;
	public static final char KEYWORD_IF = 39;
	public static final char KEYWORD_FUNCTION = 36;
	public static final char KEYWORD_FUNCTION_CALL = 37;
	public static final char KEYWORD_FUNCTION_REPEAT = 41;
	public static final char KEYWORD_PARAMETER = 58;

	public static final char NOT_EQUALS = 33;
	public static final char ADD = 43;
	public static final char SUBTRACT = 45;
	public static final char MULTIPLY = 42;
	public static final char DIVIDE = 47;
	public static final char SMALLER = 60;
	public static final char EQUALS = 61;
	public static final char BIGGER = 40;

	public static final char VARIABLES_START = 1; // start at 1 because character 0 is null so it doesn't work
	public static final char VARIABLES_END = 32;
	public static final char FUNCTIONS_START = 1;
	public static final char FUNCTIONS_END = 16;
	public static final char PARAMETERS_START = 62;
	public static final char PARAMETERS_END = 68;
	public static final char ARRAYS_START = 69;
	public static final char ARRAYS_END = 82;
	public static final char ARRAY_INDICES_START = 83;
	public static final char ARRAY_INDICES_END = 127;
	
	public static final char LINE_BREAK = 59;
	private static final char CHARACTER_NULL = 0;
	private static final short INDEX_NULL = -1;
	
	public static final char TYPE_FUNCTION = 127;
	public static final char TYPE_INT = 126;
	public static final char TYPE_FIXED = 125;
	public static final char TYPE_BOOL = 124;
	public static final char TYPE_CHAR = 123;
	
	public static final char TYPE_ARRAY_INT = 122;
	public static final char TYPE_ARRAY_FIXED = 121;
	public static final char TYPE_ARRAY_BOOL = 120;
	public static final char TYPE_ARRAY_CHAR = 119;
	
	// doesn't matter where it starts the call char is in front of it
	// the limit is the ASCII chart so 126 native functions
	public static final char FUNCTION_PRINT = 127;
	public static final char FUNCTION_PRINT_LINE = 126;
	public static final char FUNCTION_COPY = 125;
	public static final char FUNCTION_SET_DELAY = 124;
	public static final char FUNCTION_GET_MILLISECOND = 123;
	public static final char FUNCTION_STRING = 122;
	public static final char FUNCTION_INVERT = 121;
	public static final char FUNCTION_POSITIVE = 120;
	public static final char FUNCTION_NEGATIVE = 119;
	public static final char FUNCTION_RANDOM = 118;
	public static final char FUNCTION_MAX = 117;
	public static final char FUNCTION_MIN = 116;
	public static final char FUNCTION_SIN = 115;
	public static final char FUNCTION_COS = 114;
	public static final char FUNCTION_TAN = 113;
	public static final char FUNCTION_POW = 112;
	public static final char FUNCTION_SQRT = 111;
	public static final char FUNCTION_DRAW_COLOR = 110;
	public static final char FUNCTION_DRAW_WINDOW = 109;
	public static final char FUNCTION_DRAW_POINT = 108;
	public static final char FUNCTION_DRAW_LINE = 107;
	public static final char FUNCTION_DRAW_TRIANGLE = 106;
	public static final char FUNCTION_DRAW_RECT = 105;
	public static final char FUNCTION_DRAW_CIRCLE = 104;
	public static final char FUNCTION_FILL_TRIANGLE = 103;
	public static final char FUNCTION_FILL_RECT = 102;
	public static final char FUNCTION_FILL_CIRCLE = 101;
	public static final char FUNCTION_START_PIN = 100;
	public static final char FUNCTION_GET_PIN = 99;
	public static final char FUNCTION_SET_PIN = 98;
	
	public static final char FIXED_POINT = 8;
	
	private byte functionCount = 0;
	private short[] functionLines = new short[FUNCTIONS_END - FUNCTIONS_START];

	private byte intVariableCount = 0;
	private byte fixedVariableCount = 0;
	private byte boolVariableCount = 0;
	private byte charVariableCount = 0;
	private short[] variableValues = new short[(VARIABLES_END - VARIABLES_START) + (ARRAY_INDICES_END - ARRAY_INDICES_START)];
	
	private byte intArrayCount = 0;
	private byte fixedArrayCount = 0;
	private byte boolArrayCount = 0;
	private byte charArrayCount = 0;
	private byte[] arraySizes = new byte[ARRAYS_END - ARRAYS_START];
	
	private char[] parameterNames = new char[PARAMETERS_END - PARAMETERS_START];
	
	private short jooCodeSize = 0;
	private char[] jooCode = new char[1024];
	
	private short currentType = CHARACTER_NULL;
	private byte allIfs = 0;
	private byte failedIfs = 0;
	private byte[] failedIfIndex = new byte[8];
	
	// string buffer used by string native function
	private static final byte STRING_BUFFER_SIZE = 64;
	private char[] stringBuffer = new char[STRING_BUFFER_SIZE];
	
	// getters only used by unit tests	
	short[] getFunctionLines() {
		return functionLines;
	}
	
	short[] getVariableValues() {
		return variableValues;
	}
	
	char[] getParameterNames() {
		return parameterNames;
	}
	
	byte[] getArraySizes() {
		return arraySizes;
	}
	
	byte getAllIfs() {
		return allIfs;
	}

	byte getFailedIfs() {
		return failedIfs;
	}
	
	public char[] getCode() {
		return jooCode;
	}
	
	public short getCodeSize() {
		return jooCodeSize;
	}

	public void setJooCodeSize(short jooCodeSize) {
		this.jooCodeSize = jooCodeSize;
	}

	public void start() {
		interpretFunction(jooCode, functionLines[0]);
	}

	short getFunctionIndex(char name) {
		short realName = (short)(name - FUNCTIONS_START);
		if(realName < functionCount) {
			return realName;
		}
		return INDEX_NULL;
	}
	
	short getVariableIndex(char name) {
		short variableName = (short) name;
		if((variableName >= PARAMETERS_START) && (variableName < PARAMETERS_END)) {
			variableName = (short) parameterNames[variableName - PARAMETERS_START];
		}
		if((variableName >= ARRAYS_START) && (variableName < ARRAYS_END)) {
			return (short) (variableName - ARRAYS_START);
		}
		if((variableName >= VARIABLES_END) && (variableName < VARIABLES_END + (ARRAY_INDICES_END - ARRAY_INDICES_START))) {
			return variableName;
		}
		if((variableName >= VARIABLES_START) && (variableName < VARIABLES_END)) {
			return (short) (variableName - VARIABLES_START);
		}
		return INDEX_NULL;
	}
	
	short getVariableIndex(char name, short index) {
		short variableName = (short) name;
		if((variableName >= PARAMETERS_START) && (variableName < PARAMETERS_END)) {
			variableName = (short) parameterNames[variableName - PARAMETERS_START];
		}
		if((variableName >= ARRAYS_START) && (variableName < ARRAYS_END)) {
			return (short) (getArrayIndex((short) (variableName - ARRAYS_START), index) + VARIABLES_END);
		}
		if((variableName >= VARIABLES_START) && (variableName < VARIABLES_END)) {
			return (short) (variableName - VARIABLES_START);
		}
		return INDEX_NULL;
	}
	
	short getArrayIndex(short variableIndex, short index) {
		if(variableIndex >= ARRAYS_END - ARRAYS_START) {
			return INDEX_NULL;
		}
		short inArrayIndex = 0;
		for (short i = 0; i < variableIndex; i++) {
			inArrayIndex += arraySizes[i];
		}
		if((index >= VARIABLES_START) && (index < VARIABLES_END)) {
			char variableName = getVariableType((char)index);
			if (variableName == TYPE_INT) {
				index = (short) (variableValues[getVariableIndex((char)index)] + ARRAY_INDICES_START);
			}
		}
		inArrayIndex = (short) (inArrayIndex + index - ARRAY_INDICES_START);
		if((inArrayIndex > 0) && (inArrayIndex < 100)) {
			return inArrayIndex;
		}
		return INDEX_NULL;
	}
	
	char getVariableType(char name) {
		short variableName = (short) name;
		if((variableName >= PARAMETERS_START) && (variableName < PARAMETERS_END)) {
			variableName = (short) parameterNames[variableName - PARAMETERS_START];
		}
		if((variableName >= ARRAYS_START) && (variableName < ARRAYS_END)) {
			variableName -= ARRAYS_START;
			if(variableName < intArrayCount) {
				return TYPE_ARRAY_INT;
			}
			else if(variableName < fixedArrayCount) {
				return TYPE_ARRAY_FIXED;
			}
			else if(variableName < boolArrayCount) {
				return TYPE_ARRAY_BOOL;
			}
			else if(variableName < charArrayCount) {
				return TYPE_ARRAY_CHAR;
			}
		}
		if((variableName >= VARIABLES_START) && (variableName < VARIABLES_END)) {
			variableName -= VARIABLES_START;
			if(variableName < intVariableCount) {
				return TYPE_INT;
			}
			else if(variableName < fixedVariableCount) {
				return TYPE_FIXED;
			}
			else if(variableName < boolVariableCount) {
				return TYPE_BOOL;
			}
			else if(variableName < charVariableCount) {
				return TYPE_CHAR;
			}
		}
		if((variableName >= VARIABLES_END) && (variableName < VARIABLES_END + (ARRAY_INDICES_END - ARRAY_INDICES_START))) {
			short currentArray = 0;
			for (short i = 0; i < ARRAYS_END - ARRAYS_START; i++) {
				currentArray += arraySizes[i];
				if(currentArray > variableName - VARIABLES_END) {
					short arrayType = (short) getVariableType((char)((i + ARRAYS_START) - 1));
					switch (arrayType) {
					case TYPE_ARRAY_INT: return TYPE_INT;
					case TYPE_ARRAY_FIXED: return TYPE_FIXED;
					case TYPE_ARRAY_BOOL: return TYPE_BOOL;
					case TYPE_ARRAY_CHAR: return TYPE_CHAR;
					}
				}
			}
		}
		return CHARACTER_NULL;
	}
	
	public void initialize() {
		short lineIndex = 0;
		for (short i = lineIndex; i < jooCodeSize; i++) {
			if(i > 0) { // needed because first character is not LINE_BREAK meaning first line would be jumped
				if(jooCode[i] == LINE_BREAK) {
					lineIndex = (short) (i + 1);
				} else {
					if(i == jooCodeSize - 2) {
						break;
					}
					continue;
				}
			}
			searchFunctions(jooCode, lineIndex);
			searchInts(jooCode, lineIndex);
			searchFixeds(jooCode, lineIndex);
			searchBools(jooCode, lineIndex);
			searchChars(jooCode, lineIndex);
			searchIntArrays(jooCode, lineIndex);
			searchFixedArrays(jooCode, lineIndex);
			searchBoolArrays(jooCode, lineIndex);
			searchCharArrays(jooCode, lineIndex);
		}
	}
	
	void searchFunctions(char[] jooCode, short lineIndex) {
		if(jooCode[lineIndex] == TYPE_FUNCTION) {
			byte count = (byte)jooCode[lineIndex + 1];
			functionCount = count;
			currentType = TYPE_FUNCTION;
		}
		else if((currentType == TYPE_FUNCTION) && (jooCode[lineIndex] == KEYWORD_FUNCTION)) {
			if(jooCode[lineIndex + 1] != LINE_BREAK) {
				short functionIndex = getFunctionIndex(jooCode[lineIndex + 1]);
				if(functionIndex != INDEX_NULL) {
					short startLine = (short) (lineIndex + 2);
					short parameterCount = 3;
					for (short i = startLine; i < jooCodeSize; i++) {
						if(jooCode[i] == LINE_BREAK) {
							break;
						}
						parameterCount++;
					}
					functionLines[functionIndex] = (short) (lineIndex + parameterCount);
				}
			}
		}
	}
	
	void searchInts(char[] jooCode, short lineIndex) {
		if(jooCode[lineIndex] == TYPE_INT) {
			byte variableCount = (byte)jooCode[lineIndex + 1];
			intVariableCount = variableCount;
			currentType = TYPE_INT;
		}
		else if(currentType == TYPE_INT) {
			short variableValue = 0;
			if(jooCode[lineIndex + 1] != LINE_BREAK) {
				variableValue = parseInt(jooCode, (short) (lineIndex + 1));
			}
			short variableIndex = getVariableIndex(jooCode[lineIndex]);
			if(variableIndex != INDEX_NULL) {
				variableValues[variableIndex] = variableValue;
			}
		}
	}
	
	void searchFixeds(char[] jooCode, short lineIndex) {
		if(jooCode[lineIndex] == TYPE_FIXED) {
			byte variableCount = (byte)jooCode[lineIndex + 1];
			fixedVariableCount = (byte) (variableCount + intVariableCount);
			currentType = TYPE_FIXED;
		}
		else if(currentType == TYPE_FIXED) {
			short variableValue = 0;
			if(jooCode[lineIndex + 1] != LINE_BREAK) {
				variableValue = parseFixed(jooCode, (short) (lineIndex + 1));
			}
			short variableIndex = getVariableIndex(jooCode[lineIndex]);
			if(variableIndex != INDEX_NULL) {
				variableValues[variableIndex] = variableValue;
			}
		}
	}
	
	void searchBools(char[] jooCode, short lineIndex) {
		if(jooCode[lineIndex] == TYPE_BOOL) {
			byte variableCount = (byte)jooCode[lineIndex + 1];
			boolVariableCount = (byte) (variableCount + fixedVariableCount);
			currentType = TYPE_BOOL;
		}
		else if(currentType == TYPE_BOOL) {
			short variableValue = 0;
			if(jooCode[lineIndex + 1] != LINE_BREAK) {
				variableValue = parseBool(jooCode, (short) (lineIndex + 1));
			}
			short variableIndex = getVariableIndex(jooCode[lineIndex]);
			if(variableIndex != INDEX_NULL) {
				variableValues[variableIndex] = variableValue;
			}
		}
	}
	
	void searchChars(char[] jooCode, short lineIndex) {
		if(jooCode[lineIndex] == TYPE_CHAR) {
			byte variableCount = (byte)jooCode[lineIndex + 1];
			charVariableCount = (byte) (variableCount + boolVariableCount);
			currentType = TYPE_CHAR;
		}
		else if(currentType == TYPE_CHAR) {
			short variableValue = 0;
			if(jooCode[lineIndex + 1] != LINE_BREAK) {
				variableValue = (short) parseChar(jooCode, (short) (lineIndex + 1));
			}
			short variableIndex = getVariableIndex(jooCode[lineIndex]);
			if(variableIndex != INDEX_NULL) {
				variableValues[variableIndex] = variableValue;
			}
		}
	}
	
	void searchIntArrays(char[] jooCode, short lineIndex) {
		if(jooCode[lineIndex] == TYPE_ARRAY_INT) {
			byte variableCount = (byte)jooCode[lineIndex + 1];
			intArrayCount = variableCount;
			currentType = TYPE_ARRAY_INT;
		}
		else if(currentType == TYPE_ARRAY_INT) {
			byte variableValue = (byte)jooCode[lineIndex + 1];
			short variableIndex = getVariableIndex(jooCode[lineIndex]);
			if(variableIndex != INDEX_NULL) {
				arraySizes[variableIndex] = variableValue;
			}
		}
	}
	
	void searchFixedArrays(char[] jooCode, short lineIndex) {
		if(jooCode[lineIndex] == TYPE_ARRAY_FIXED) {
			byte variableCount = (byte)jooCode[lineIndex + 1];
			fixedArrayCount = (byte) (variableCount + intArrayCount);
			currentType = TYPE_ARRAY_FIXED;
		}
		else if(currentType == TYPE_ARRAY_FIXED) {
			byte variableValue = (byte)jooCode[lineIndex + 1];
			short variableIndex = getVariableIndex(jooCode[lineIndex]);
			if(variableIndex != INDEX_NULL) {
				arraySizes[variableIndex] = variableValue;
			}
		}
	}
	
	void searchBoolArrays(char[] jooCode, short lineIndex) {
		if(jooCode[lineIndex] == TYPE_ARRAY_BOOL) {
			byte variableCount = (byte)jooCode[lineIndex + 1];
			boolArrayCount = (byte) (variableCount + fixedArrayCount);
			currentType = TYPE_ARRAY_BOOL;
		}
		else if(currentType == TYPE_ARRAY_BOOL) {
			byte variableValue = (byte)jooCode[lineIndex + 1];
			short variableIndex = getVariableIndex(jooCode[lineIndex]);
			if(variableIndex != INDEX_NULL) {
				arraySizes[variableIndex] = variableValue;
			}
		}
	}
	
	void searchCharArrays(char[] jooCode, short lineIndex) {
		if(jooCode[lineIndex] == TYPE_ARRAY_CHAR) {
			byte variableCount = (byte)jooCode[lineIndex + 1];
			charArrayCount = (byte) (variableCount + boolArrayCount);
			currentType = TYPE_ARRAY_CHAR;
		}
		else if(currentType == TYPE_ARRAY_CHAR) {
			byte variableValue = (byte)jooCode[lineIndex + 1];
			short variableIndex = getVariableIndex(jooCode[lineIndex]);
			if(variableIndex != INDEX_NULL) {
				arraySizes[variableIndex] = variableValue;
			}
		}
	}
	
	void callFunction(char[] jooCode, short lineIndex) {
		char functionName = jooCode[lineIndex + 1];
		char param0 = parameterNames[0];
		char param1 = parameterNames[1];
		char param2 = parameterNames[2];
		char param3 = parameterNames[3];
		char param4 = parameterNames[4];
		char param5 = parameterNames[5];
		fillParameters(jooCode, lineIndex, param0, param1, param2, param3, param4, param5);
		switch (functionName) {
		case FUNCTION_PRINT:
			print(jooCode, lineIndex, false);
			break;
		case FUNCTION_PRINT_LINE:
			print(jooCode, lineIndex, true);
			break;
		case FUNCTION_COPY:
			copy(jooCode, lineIndex);
			break;
		case FUNCTION_SET_DELAY:
			setDelay(jooCode, lineIndex);
			break;
		case FUNCTION_GET_MILLISECOND:
			getSecond(jooCode, lineIndex);
			break;
		case FUNCTION_STRING:
			string(jooCode, lineIndex);
			break;
		case FUNCTION_INVERT:
			invert(jooCode, lineIndex);
			break;
		case FUNCTION_POSITIVE:
			positive(jooCode, lineIndex);
			break;
		case FUNCTION_NEGATIVE:
			negative(jooCode, lineIndex);
			break;
		default:
			short functionIndex = getFunctionIndex(functionName);
			if(functionIndex != INDEX_NULL) {
				interpretFunction(jooCode, functionLines[functionIndex]);
			}
			break;
		}
		parameterNames[0] = param0;
		parameterNames[1] = param1;
		parameterNames[2] = param2;
		parameterNames[3] = param3;
		parameterNames[4] = param4;
		parameterNames[5] = param5;
	}
	
	void fillParameters(char[] jooCode, short lineIndex, char param0, char param1, char param2, char param3, char param4, char param5) {
		short parameterCount = -1;
		for (short i = (short) (lineIndex + 1); i < jooCodeSize; i++) {
			if((jooCode[i] == KEYWORD_PARAMETER) || (i == lineIndex + 1)) {
				parameterCount++;
				i++;
			} else {
				if(jooCode[i] == LINE_BREAK) {
					break;
				}
				continue;
			}
			switch(jooCode[i]) {
			case PARAMETERS_START:
				parameterNames[parameterCount] = param0;
				break;
			case PARAMETERS_START + 1:
				parameterNames[parameterCount] = param1;
				break;
			case PARAMETERS_START + 2:
				parameterNames[parameterCount] = param2;
				break;
			case PARAMETERS_START + 3:
				parameterNames[parameterCount] = param3;
				break;
			case PARAMETERS_START + 4:
				parameterNames[parameterCount] = param4;
				break;
			case PARAMETERS_START + 5:
				parameterNames[parameterCount] = param5;
				break;
			default:
				if((jooCode[i] >= ARRAYS_START) && ((jooCode[i + 1] >= ARRAY_INDICES_START) || (jooCode[i + 1] < VARIABLES_END))) {
					short variableIndex = getVariableIndex(jooCode[i], (short) jooCode[i + 1]);
					if(variableIndex != INDEX_NULL) {
						parameterNames[parameterCount] = (char)variableIndex;
					}
				} else {
					parameterNames[parameterCount] = jooCode[i];
				}
				break;
			}
		}
	}
	
	void interpretFunction(char[] jooCode, short functionLine) {
			short lineIndex = functionLine;
			for (short i = lineIndex; i < jooCodeSize; i++) {
				if(i > lineIndex) { // needed because first character is not LINE_BREAK meaning first line would be jumped
					if(jooCode[i] == LINE_BREAK) {
						lineIndex = (short) (i + 1);
						i++;
					} else {
						if(i == jooCodeSize - 2) {
							break;
						}
						continue;
					}
				}
				switch (jooCode[lineIndex]) {
				case KEYWORD_FUNCTION: // keyword function and endFunction are the same to free 1 ASCII character
					return;
				case KEYWORD_FUNCTION_REPEAT:
					if(failedIfs == 0) {
						interpretFunction(jooCode, functionLine);
					}
					return;
				case KEYWORD_ELSE:
					if((failedIfs > 0) && (failedIfIndex[failedIfs-1] == allIfs)) {
						failedIfs--;
					} else {
						failedIfs++;
					}
					break;
				case KEYWORD_IF:
					interpretIfBlock(jooCode, lineIndex);
					break;
				case KEYWORD_FUNCTION_CALL:
					if(failedIfs == 0) {
						try {// try block to ignore stack overflow error that is thrown if a function calls itself
							// If not ignored recursive functions wouldn't work
							callFunction(jooCode, lineIndex);
						} catch(StackOverflowError e) { }
					}
					break;
				default:
					if(failedIfs == 0) {
						interpretVariableOperation(jooCode, lineIndex);
					}
					break;
				}
			}
	}
	
	void interpretIfBlock(char[] jooCode, short lineIndex) {
		if (jooCode[lineIndex + 1] == LINE_BREAK) {
			if((failedIfs > 0) && (failedIfIndex[failedIfs-1] == allIfs)) {
				failedIfs--;
			}
			if(allIfs > 0) {
				allIfs--;
			}
		} else {
			boolean comparisonCorrect = interpretIfOperation(jooCode, lineIndex);
			allIfs++;
			if (!comparisonCorrect) {
				failedIfIndex[failedIfs] = allIfs;
				failedIfs++;
			}
		}
	}
	
	boolean interpretIfOperation(char[] jooCode, short lineIndex) {
		char variableType = getVariableType(jooCode[lineIndex + 1]);
		if(variableType != CHARACTER_NULL) {
			switch (variableType) {
			case TYPE_INT:
				return interpretIntComparison(jooCode, (short) (lineIndex + 1));
			case TYPE_FIXED:
				return interpretFixedComparison(jooCode, (short) (lineIndex + 1));
			case TYPE_BOOL:
				return interpretBoolComparison(jooCode, (short) (lineIndex + 1));
			case TYPE_CHAR:
				return interpretCharComparison(jooCode, (short) (lineIndex + 1));
			case TYPE_ARRAY_INT:
				return interpretIntArrayComparison(jooCode, (short) (lineIndex + 1));
			case TYPE_ARRAY_FIXED:
				return interpretFixedArrayComparison(jooCode, (short) (lineIndex + 1));
			case TYPE_ARRAY_BOOL:
				return interpretBoolArrayComparison(jooCode, (short) (lineIndex + 1));
			case TYPE_ARRAY_CHAR:
				return interpretCharArrayComparison(jooCode, (short) (lineIndex + 1));
			}				
		}
		return false;
	}
	
	void interpretVariableOperation(char[] jooCode, short lineIndex) {
		char variableType = getVariableType(jooCode[lineIndex]);
		if(variableType != CHARACTER_NULL) {
			switch (variableType) {
			case TYPE_INT:
				interpretIntOperation(jooCode, lineIndex);
				break;
			case TYPE_FIXED:
				interpretFixedOperation(jooCode, lineIndex);
				break;
			case TYPE_BOOL:
				interpretBoolOperation(jooCode, lineIndex);
				break;
			case TYPE_CHAR:
				interpretCharOperation(jooCode, lineIndex);
				break;
			case TYPE_ARRAY_INT:
				interpretIntArrayOperation(jooCode, lineIndex);
				break;
			case TYPE_ARRAY_FIXED:
				interpretFixedArrayOperation(jooCode, lineIndex);
				break;
			case TYPE_ARRAY_BOOL:
				interpretBoolArrayOperation(jooCode, lineIndex);
				break;
			case TYPE_ARRAY_CHAR:
				interpretCharArrayOperation(jooCode, lineIndex);
				break;
			}
		}
	}
	
	void interpretIntOperation(char[] jooCode, short lineIndex) {
		short variableIndex = getVariableIndex(jooCode[lineIndex]);
		if(variableIndex != INDEX_NULL) {
			switch (jooCode[lineIndex + 1]) {
			case ADD:
				variableValues[variableIndex] += parseInt(jooCode, (short) (lineIndex + 2));
				break;
			case SUBTRACT:
				variableValues[variableIndex] -= parseInt(jooCode, (short) (lineIndex + 2));
				break;
			case MULTIPLY:
				variableValues[variableIndex] *= parseInt(jooCode, (short) (lineIndex + 2));
				break;
			case DIVIDE:
				variableValues[variableIndex] /= parseInt(jooCode, (short) (lineIndex + 2));
				break;
			case EQUALS:
				variableValues[variableIndex] = parseInt(jooCode, (short) (lineIndex + 2));
				break;
			}
		}
	}
	
	boolean interpretIntComparison(char[] jooCode, short lineIndex) {
		short variableIndex = getVariableIndex(jooCode[lineIndex]);
		if(variableIndex != INDEX_NULL) {
			switch (jooCode[lineIndex + 1]) {
			case EQUALS:
				return variableValues[variableIndex] == parseInt(jooCode, (short) (lineIndex + 2));
			case NOT_EQUALS:
				return variableValues[variableIndex] != parseInt(jooCode, (short) (lineIndex + 2));
			case BIGGER:
				if(jooCode[lineIndex + 2] == EQUALS) {
					return variableValues[variableIndex] >= parseInt(jooCode, (short) (lineIndex + 3));
				}
				return variableValues[variableIndex] > parseInt(jooCode, (short) (lineIndex + 2));
			case SMALLER:
				if(jooCode[lineIndex + 2] == EQUALS) {
					return variableValues[variableIndex] <= parseInt(jooCode, (short) (lineIndex + 3));
				}
				return variableValues[variableIndex] < parseInt(jooCode, (short) (lineIndex + 2));
			}
		}
		return false;
	}
	
	void interpretFixedOperation(char[] jooCode, short lineIndex) {
		short variableIndex = getVariableIndex(jooCode[lineIndex]);
		if(variableIndex != INDEX_NULL) {
			switch (jooCode[lineIndex + 1]) {
			case ADD:
				variableValues[variableIndex] += parseFixed(jooCode, (short) (lineIndex + 2));
				break;
			case SUBTRACT:
				variableValues[variableIndex] -= parseFixed(jooCode, (short) (lineIndex + 2));
				break;
			case MULTIPLY:
				int multiplyResult = (int)variableValues[variableIndex] * (int)parseFixed(jooCode, (short) (lineIndex + 2));
				variableValues[variableIndex] = (short) (multiplyResult >> FIXED_POINT);
				break;
			case DIVIDE:
				int divideResult = (int)variableValues[variableIndex] << FIXED_POINT;
				variableValues[variableIndex] = (short)(divideResult / parseFixed(jooCode, (short) (lineIndex + 2)));
				break;
			case EQUALS:
				variableValues[variableIndex] = parseFixed(jooCode, (short) (lineIndex + 2));
				break;
			}
		}
	}
	
	boolean interpretFixedComparison(char[] jooCode, short lineIndex) {
		short variableIndex = getVariableIndex(jooCode[lineIndex]);
		if(variableIndex != INDEX_NULL) {
			switch (jooCode[lineIndex + 1]) {
			case EQUALS:
				return variableValues[variableIndex] == parseFixed(jooCode, (short) (lineIndex + 2));
			case NOT_EQUALS:
				return variableValues[variableIndex] != parseFixed(jooCode, (short) (lineIndex + 2));
			case BIGGER:
				if(jooCode[lineIndex + 2] == EQUALS) {
					return variableValues[variableIndex] >= parseFixed(jooCode, (short) (lineIndex + 3));
				}
				return variableValues[variableIndex] > parseFixed(jooCode, (short) (lineIndex + 2));
			case SMALLER:
				if(jooCode[lineIndex + 2] == EQUALS) {
					return variableValues[variableIndex] <= parseFixed(jooCode, (short) (lineIndex + 3));
				}
				return variableValues[variableIndex] < parseFixed(jooCode, (short) (lineIndex + 2));
			}
		}
		return false;
	}
	
	void interpretBoolOperation(char[] jooCode, short lineIndex) {
		short variableIndex = getVariableIndex(jooCode[lineIndex]);
		if(variableIndex != INDEX_NULL) {
			switch (jooCode[lineIndex + 1]) {
			case EQUALS:
				variableValues[variableIndex] = parseBool(jooCode, (short) (lineIndex + 2));
				break;
			case NOT_EQUALS:
				variableValues[variableIndex] = (short) (parseBool(jooCode, (short) (lineIndex + 2)) == 0 ? 1 : 0);
				break;
			}
		}
	}
	
	boolean interpretBoolComparison(char[] jooCode, short lineIndex) {
		short variableIndex = getVariableIndex(jooCode[lineIndex]);
		if(variableIndex != INDEX_NULL) {
			switch (jooCode[lineIndex + 1]) {
			case EQUALS:
				return variableValues[variableIndex] == parseBool(jooCode, (short) (lineIndex + 2));
			case NOT_EQUALS:
				return variableValues[variableIndex] != parseBool(jooCode, (short) (lineIndex + 2));
			}
		}
		return false;
	}
	
	void interpretCharOperation(char[] jooCode, short lineIndex) {
		short variableIndex = getVariableIndex(jooCode[lineIndex]);
		if(variableIndex != INDEX_NULL) {
			switch (jooCode[lineIndex + 1]) {
			case EQUALS:
				variableValues[variableIndex] = parseChar(jooCode, (short) (lineIndex + 2));
				break;
			}
		}
	}
	
	boolean interpretCharComparison(char[] jooCode, short lineIndex) {
		short variableIndex = getVariableIndex(jooCode[lineIndex]);
		if(variableIndex != INDEX_NULL) {
			switch (jooCode[lineIndex + 1]) {
			case EQUALS:
				return variableValues[variableIndex] == parseChar(jooCode, (short) (lineIndex + 2));
			case NOT_EQUALS:
				return variableValues[variableIndex] != parseChar(jooCode, (short) (lineIndex + 2));
			}
		}
		return false;
	}
	
	void interpretIntArrayOperation(char[] jooCode, short lineIndex) {
		short variableIndex = getVariableIndex(jooCode[lineIndex], (short) jooCode[lineIndex + 1]);
		if(variableIndex != INDEX_NULL) {
			switch (jooCode[lineIndex + 2]) {
			case ADD:
				variableValues[variableIndex] += parseInt(jooCode, (short) (lineIndex + 3));
				break;
			case SUBTRACT:
				variableValues[variableIndex] -= parseInt(jooCode, (short) (lineIndex + 3));
				break;
			case MULTIPLY:
				variableValues[variableIndex] *= parseInt(jooCode, (short) (lineIndex + 3));
				break;
			case DIVIDE:
				variableValues[variableIndex] /= parseInt(jooCode, (short) (lineIndex + 3));
				break;
			case EQUALS:
				variableValues[variableIndex] = parseInt(jooCode, (short) (lineIndex + 3));
				break;
			}
		}
	}
	
	boolean interpretIntArrayComparison(char[] jooCode, short lineIndex) {
		short variableIndex = getVariableIndex(jooCode[lineIndex], (short) jooCode[lineIndex + 1]);
		if(variableIndex != INDEX_NULL) {
			switch (jooCode[lineIndex + 2]) {
			case EQUALS:
				return variableValues[variableIndex] == parseInt(jooCode, (short) (lineIndex + 3));
			case NOT_EQUALS:
				return variableValues[variableIndex] != parseInt(jooCode, (short) (lineIndex + 3));
			case BIGGER:
				if(jooCode[lineIndex + 3] == EQUALS) {
					return variableValues[variableIndex] >= parseInt(jooCode, (short) (lineIndex + 4));
				}
				return variableValues[variableIndex] > parseInt(jooCode, (short) (lineIndex + 3));
			case SMALLER:
				if(jooCode[lineIndex + 3] == EQUALS) {
					return variableValues[variableIndex] <= parseInt(jooCode, (short) (lineIndex + 4));
				}
				return variableValues[variableIndex] < parseInt(jooCode, (short) (lineIndex + 3));
			}
		}
		return false;
	}
	
	void interpretFixedArrayOperation(char[] jooCode, short lineIndex) {
		short variableIndex = getVariableIndex(jooCode[lineIndex], (short) jooCode[lineIndex + 1]);
		if(variableIndex != INDEX_NULL) {
			switch (jooCode[lineIndex + 2]) {
			case ADD:
				variableValues[variableIndex] += parseFixed(jooCode, (short) (lineIndex + 3));
				break;
			case SUBTRACT:
				variableValues[variableIndex] -= parseFixed(jooCode, (short) (lineIndex + 3));
				break;
			case MULTIPLY:
				int multiplyResult = (int)variableValues[variableIndex] * (int)parseFixed(jooCode, (short) (lineIndex + 3));
				variableValues[variableIndex] = (short) (multiplyResult >> FIXED_POINT);
				break;
			case DIVIDE:
				int divideResult = (int)variableValues[variableIndex] << FIXED_POINT;
				variableValues[variableIndex] = (short)(divideResult / parseFixed(jooCode, (short) (lineIndex + 3)));
				break;
			case EQUALS:
				variableValues[variableIndex] = parseFixed(jooCode, (short) (lineIndex + 3));
				break;
			}
		}
	}
	
	boolean interpretFixedArrayComparison(char[] jooCode, short lineIndex) {
		short variableIndex = getVariableIndex(jooCode[lineIndex], (short) jooCode[lineIndex + 1]);
		if(variableIndex != INDEX_NULL) {
			switch (jooCode[lineIndex + 2]) {
			case EQUALS:
				return variableValues[variableIndex] == parseFixed(jooCode, (short) (lineIndex + 3));
			case NOT_EQUALS:
				return variableValues[variableIndex] != parseFixed(jooCode, (short) (lineIndex + 3));
			case BIGGER:
				if(jooCode[lineIndex + 3] == EQUALS) {
					return variableValues[variableIndex] >= parseFixed(jooCode, (short) (lineIndex + 4));
				}
				return variableValues[variableIndex] > parseFixed(jooCode, (short) (lineIndex + 3));
			case SMALLER:
				if(jooCode[lineIndex + 3] == EQUALS) {
					return variableValues[variableIndex] <= parseFixed(jooCode, (short) (lineIndex + 4));
				}
				return variableValues[variableIndex] < parseFixed(jooCode, (short) (lineIndex + 3));
			}
		}
		return false;
	}
	
	void interpretBoolArrayOperation(char[] jooCode, short lineIndex) {
		short variableIndex = getVariableIndex(jooCode[lineIndex], (short) jooCode[lineIndex + 1]);
		if(variableIndex != INDEX_NULL) {
			switch (jooCode[lineIndex + 2]) {
			case EQUALS:
				variableValues[variableIndex] = parseBool(jooCode, (short) (lineIndex + 3));
				break;
			case NOT_EQUALS:
				variableValues[variableIndex] = (short) (parseBool(jooCode, (short) (lineIndex + 3)) == 0 ? 1 : 0);
				break;
			}
		}
	}
	
	boolean interpretBoolArrayComparison(char[] jooCode, short lineIndex) {
		short variableIndex = getVariableIndex(jooCode[lineIndex], (short) jooCode[lineIndex + 1]);
		if(variableIndex != INDEX_NULL) {
			switch (jooCode[lineIndex + 2]) {
			case EQUALS:
				return variableValues[variableIndex] == parseBool(jooCode, (short) (lineIndex + 3));
			case NOT_EQUALS:
				return variableValues[variableIndex] != parseBool(jooCode, (short) (lineIndex + 3));
			}
		}
		return false;
	}
	
	void interpretCharArrayOperation(char[] jooCode, short lineIndex) {
		short variableIndex = getVariableIndex(jooCode[lineIndex], (short) jooCode[lineIndex + 1]);
		if(variableIndex != INDEX_NULL) {
			switch (jooCode[lineIndex + 2]) {
			case EQUALS:
				variableValues[variableIndex] = parseChar(jooCode, (short) (lineIndex + 3));
				break;
			}
		}
	}
	
	boolean interpretCharArrayComparison(char[] jooCode, short lineIndex) {
		short variableIndex = getVariableIndex(jooCode[lineIndex], (short) jooCode[lineIndex + 1]);
		if(variableIndex != INDEX_NULL) {
			switch (jooCode[lineIndex + 2]) {
			case EQUALS:
				return variableValues[variableIndex] == parseChar(jooCode, (short) (lineIndex + 3));
			case NOT_EQUALS:
				return variableValues[variableIndex] != parseChar(jooCode, (short) (lineIndex + 3));
			}
		}
		return false;
	}
	
	short parseInt(char[] jooCode, short valueIndex) {
		// no need to test if it's a short as the test has been done before calling the method
		short variableIndex = getVariableIndex(jooCode[valueIndex], (short) jooCode[valueIndex + 1]);
		if(variableIndex != INDEX_NULL) {
			return variableValues[variableIndex];
		}	
		short result = 0;
		short index = 1;
		short lineLength = valueIndex;
		for (; jooCode[lineLength] != LINE_BREAK; lineLength++);
		for (short i = (short) (lineLength - 1); i >= valueIndex; i--) {
			result += (short)(jooCode[i] - '0') * index;
			index *= 10;
		}
		return result;
	}
	
	short parseFixed(char[] jooCode, short valueIndex) {
		short variableIndex = getVariableIndex(jooCode[valueIndex], (short) jooCode[valueIndex + 1]);
		if(variableIndex != INDEX_NULL) {
			return variableValues[variableIndex];
		}	
		short integer = 0;
		short fractional = 0;
		short intIndex = 0;
		short fractionalIndex = 1;
		short lineLength = valueIndex;
		for (; jooCode[lineLength] != LINE_BREAK; lineLength++);
		for (short i = (short) (lineLength - 1); i >= valueIndex; i--) {
			if(jooCode[i] == '.') {
				intIndex = 1;
				continue;
			}
			if(intIndex > 0) {
				integer += (short)(jooCode[i] - '0') * intIndex;
				intIndex *= 10;
			} else {
				fractional += (short)(jooCode[i] - '0') * fractionalIndex;
				fractionalIndex *= 10;
			}
		}
		if(intIndex > 0) {
			return (short) (((integer) << FIXED_POINT) + (((int)fractional << FIXED_POINT) / fractionalIndex));
		}
		return (short) (fractional << FIXED_POINT);
	}
	
	short parseBool(char[] jooCode, short valueIndex) {
		short variableIndex = getVariableIndex(jooCode[valueIndex], (short) jooCode[valueIndex + 1]);
		if(variableIndex != INDEX_NULL) {
			return variableValues[variableIndex];
		}	
		return (short)(jooCode[valueIndex] - '0');
	}
	
	short parseChar(char[] jooCode, short valueIndex) {
		short variableIndex = getVariableIndex(jooCode[valueIndex], (short) jooCode[valueIndex + 1]);
		if(variableIndex != INDEX_NULL) {
			return variableValues[variableIndex];
		}	
		return (short) jooCode[valueIndex];
	}
	
	
	// Joo native funtions implementation starts here //
	
	void print(char[] jooCode, short lineIndex, boolean printLine) {
		if(jooCode[lineIndex + 2] == FUNCTION_STRING) {
			for (int i = 0; i < STRING_BUFFER_SIZE; i++) {
				System.out.print(stringBuffer[i]);
			}
		} else {
			short variableIndex = getVariableIndex(parameterNames[0]);
			if(variableIndex != INDEX_NULL) {
				char variableType = getVariableType(parameterNames[0]);
				switch (variableType) {
				case TYPE_INT:
					System.out.print(variableValues[variableIndex]);
					break;
				case TYPE_FIXED:
					int fixedPointValue = (1 << FIXED_POINT) - 1;
					float floatValue = variableValues[variableIndex] >> FIXED_POINT;
					floatValue += (float)(variableValues[variableIndex] & fixedPointValue) / fixedPointValue;
					System.out.print(floatValue);
					break;
				case TYPE_BOOL:
					System.out.print(variableValues[variableIndex] != 0);
					break;
				case TYPE_CHAR:
					System.out.print((char)variableValues[variableIndex]);
					break;
				case TYPE_ARRAY_INT:
					int intArrayIndex = VARIABLES_START + VARIABLES_END;
					for (int i = 0; i < variableIndex; i++) {
						intArrayIndex += arraySizes[variableIndex];
					}
					System.out.print("[");
					for (int i = intArrayIndex; i < arraySizes[variableIndex] + intArrayIndex; i++) {
						System.out.print(variableValues[i] + ", ");
					}
					System.out.print("]");
					break;
				case TYPE_ARRAY_FIXED:
					int fixedArrayIndex = VARIABLES_START + VARIABLES_END;
					for (int i = 0; i < variableIndex; i++) {
						fixedArrayIndex += arraySizes[variableIndex];
					}
					System.out.print("[");
					for (int i = fixedArrayIndex; i < arraySizes[variableIndex] + fixedArrayIndex; i++) {
						int fixedPointArrayValue = (1 << FIXED_POINT) - 1;
						float arrayFloatValue = variableValues[i] >> FIXED_POINT;
						arrayFloatValue += (float)(variableValues[i] & fixedPointArrayValue) / fixedPointArrayValue;
						System.out.print(arrayFloatValue + ", ");
					}
					System.out.print("]");
					break;
				case TYPE_ARRAY_BOOL:
					int boolArrayIndex = variableIndex + VARIABLES_START + VARIABLES_END;
					for (int i = 0; i < variableIndex; i++) {
						boolArrayIndex += arraySizes[variableIndex];
					}
					System.out.print("[");
					for (int i = boolArrayIndex; i < arraySizes[variableIndex] + boolArrayIndex; i++) {
						System.out.print((variableValues[i] != 0) + ", ");
					}
					System.out.print("]");
					break;
				case TYPE_ARRAY_CHAR:
					int charArrayIndex = variableIndex + VARIABLES_START + VARIABLES_END;
					for (int i = 0; i < variableIndex; i++) {
						charArrayIndex += arraySizes[variableIndex];
					}
					System.out.print("[");
					for (int i = charArrayIndex; i < arraySizes[variableIndex] + charArrayIndex; i++) {
						System.out.print((char)variableValues[i] + ", ");
					}
					System.out.print("]");
					break;
				}
			}
		}
		if(printLine) {
			System.out.println();
		}
	}
	
	void copy(char[] jooCode, short lineIndex) {
		short variable0Index = getVariableIndex(parameterNames[0]);
		short variable1Index = getVariableIndex(parameterNames[1]);
		if(variable0Index != INDEX_NULL) {
			char variable0Type = getVariableType(parameterNames[0]);
			char variable1Type = getVariableType(parameterNames[1]);
			switch (variable0Type) {
			case TYPE_INT:
				// if's used to convert other data types to the target data type
				// that means the copy function can also be used to convert data types	
				if(variable1Type == TYPE_INT)
					variableValues[variable0Index] = variableValues[variable1Index];
				else if(variable1Type == TYPE_FIXED)
					variableValues[variable0Index] = (short)(variableValues[variable1Index] >> FIXED_POINT);
				else if(variable1Type == TYPE_BOOL)
					variableValues[variable0Index] = variableValues[variable1Index];
				else if(variable1Type == TYPE_CHAR)
					variableValues[variable0Index] = variableValues[variable1Index];
				break;
			case TYPE_FIXED:
				if(variable1Type == TYPE_INT)
					variableValues[variable0Index] = (short) (variableValues[variable1Index] << FIXED_POINT);
				else if(variable1Type == TYPE_FIXED)
					variableValues[variable0Index] = variableValues[variable1Index];
				else if(variable1Type == TYPE_BOOL)
					variableValues[variable0Index] = (short) (variableValues[variable1Index] << FIXED_POINT);
				else if(variable1Type == TYPE_CHAR)
					variableValues[variable0Index] = (short) (variableValues[variable1Index] << FIXED_POINT);
				break;
			case TYPE_BOOL:
				if(variable1Type == TYPE_INT)
					variableValues[variable0Index] = (short) (variableValues[variable1Index] != 0 ? 1 : 0);
				else if(variable1Type == TYPE_FIXED)
					variableValues[variable0Index] = (short) (variableValues[variable1Index] != 0 ? 1 : 0);
				else if(variable1Type == TYPE_BOOL)
					variableValues[variable0Index] = variableValues[variable1Index];
				else if(variable1Type == TYPE_CHAR)
					variableValues[variable0Index] = (short) (variableValues[variable1Index] != 0 ? 1 : 0);
				break;
			case TYPE_CHAR:
				if(variable1Type == TYPE_INT)
					variableValues[variable0Index] = variableValues[variable1Index];
				else if(variable1Type == TYPE_FIXED)
					variableValues[variable0Index] = (short)(variableValues[variable1Index] >> FIXED_POINT);
				else if(variable1Type == TYPE_BOOL)
					variableValues[variable0Index] = variableValues[variable1Index];
				else if(variable1Type == TYPE_CHAR)
					variableValues[variable0Index] = variableValues[variable1Index];
				break;
			case TYPE_ARRAY_INT:
				int intArraySize = arraySizes[variable0Index] < arraySizes[variable1Index] ? arraySizes[variable0Index] : arraySizes[variable1Index];
				int intArray0Index = 0;
				for (int i = 0; i < variable0Index; i++) {
					intArray0Index += arraySizes[i];
				}
				int intArray1Index = VARIABLES_START + VARIABLES_END;
				for (int i = 0; i < variable1Index; i++) {
					intArray1Index += arraySizes[i];
				}
				intArray1Index -= intArray0Index;
				intArray0Index += VARIABLES_START + VARIABLES_END;
				// for loop is outside to increase code readability a few if's won't slow down so much
				for (int i = intArray0Index; i < intArraySize + intArray0Index; i++) {	
					if(variable1Type == TYPE_ARRAY_INT)
						variableValues[i] = variableValues[i + intArray1Index];
					else if(variable1Type == TYPE_ARRAY_FIXED)
						variableValues[i] = (short)(variableValues[i + intArray1Index] >> FIXED_POINT);
					else if(variable1Type == TYPE_ARRAY_BOOL)
						variableValues[i] = variableValues[i + intArray1Index];
					else if(variable1Type == TYPE_ARRAY_CHAR)
						variableValues[i] = variableValues[i + intArray1Index];
				}
				break;
			case TYPE_ARRAY_FIXED:
				int fixedArraySize = arraySizes[variable0Index] < arraySizes[variable1Index] ? arraySizes[variable0Index] : arraySizes[variable1Index];
				int fixedArray0Index = variable0Index + VARIABLES_START + VARIABLES_END;
				int fixedArray1Index = variable1Index - variable0Index;
				for (int i = fixedArray0Index; i < fixedArraySize + fixedArray0Index; i++) {
					if(variable1Type == TYPE_ARRAY_INT)
						variableValues[i] = (short) (variableValues[i + fixedArray1Index] << FIXED_POINT);
					else if(variable1Type == TYPE_ARRAY_FIXED)
						variableValues[i] = variableValues[i + fixedArray1Index];
					else if(variable1Type == TYPE_ARRAY_BOOL)
						variableValues[i] = (short) (variableValues[i + fixedArray1Index] << FIXED_POINT);
					else if(variable1Type == TYPE_ARRAY_CHAR)
						variableValues[i] = (short) (variableValues[i + fixedArray1Index] << FIXED_POINT);
				}
				break;
			case TYPE_ARRAY_BOOL:
				int boolArraySize = arraySizes[variable0Index] < arraySizes[variable1Index] ? arraySizes[variable0Index] : arraySizes[variable1Index];
				int boolArray0Index = variable0Index + VARIABLES_START + VARIABLES_END;
				int boolArray1Index = variable1Index - variable0Index;
				for (int i = boolArray0Index; i < boolArraySize + boolArray0Index; i++) {
					if(variable1Type == TYPE_ARRAY_INT)
						variableValues[i] = (short) (variableValues[i + boolArray1Index] != 0 ? 1 : 0);
					else if(variable1Type == TYPE_ARRAY_FIXED)
						variableValues[i] = (short) (variableValues[i + boolArray1Index] != 0 ? 1 : 0);
					else if(variable1Type == TYPE_ARRAY_BOOL)
						variableValues[i] = variableValues[i + boolArray1Index];
					else if(variable1Type == TYPE_ARRAY_CHAR)
						variableValues[i] = (short) (variableValues[i + boolArray1Index] != 0 ? 1 : 0);
				}
				break;
			case TYPE_ARRAY_CHAR:
				int charArraySize = arraySizes[variable0Index] < arraySizes[variable1Index] ? arraySizes[variable0Index] : arraySizes[variable1Index];
				int charArray0Index = variable0Index + VARIABLES_START + VARIABLES_END;
				int charArray1Index = variable1Index - variable0Index;
				for (int i = charArray0Index; i < charArraySize + charArray0Index; i++) {
					if(variable1Type == TYPE_ARRAY_INT)
						variableValues[i] = variableValues[i + charArray1Index];
					else if(variable1Type == TYPE_ARRAY_FIXED)
						variableValues[i] = (short)(variableValues[i + charArray1Index] >> FIXED_POINT);
					else if(variable1Type == TYPE_ARRAY_BOOL)
						variableValues[i] = variableValues[i + charArray1Index];
					else if(variable1Type == TYPE_ARRAY_CHAR)
						variableValues[i] = variableValues[i + charArray1Index];
				}
				break;
			}
		}
	}
	
	void setDelay(char[] jooCode, short lineIndex) {
		char variableType = getVariableType(parameterNames[0]);
		if(variableType == TYPE_FIXED) {
			short variableIndex = getVariableIndex(parameterNames[0]);
			short variableValue = variableValues[variableIndex];
			short integer = (short) (variableValue >> FIXED_POINT);
			short fractional = (short) (variableValue & ((1 << FIXED_POINT) - 1));
			try {
				Thread.sleep((integer * 1000) + (((int)fractional * 1000) >> FIXED_POINT));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private final long startMillis = System.currentTimeMillis();
	
	void getSecond(char[] jooCode, short lineIndex) {
		char variableType = getVariableType(parameterNames[0]);
		if(variableType == TYPE_FIXED) {
			short variableIndex = getVariableIndex(parameterNames[0]);
			short currentMillis = (short) (System.currentTimeMillis() - startMillis);
			short integer = (short) (currentMillis / 1000);
			short fractional = (short) (currentMillis % 1000);
			variableValues[variableIndex] = (short) (((integer) << FIXED_POINT) + (((int)fractional << FIXED_POINT) / 1000));
		}
	}
	
	void string(char[] jooCode, short lineIndex) {
		for (int i = lineIndex + 2; i < jooCodeSize; i++) {
			if((jooCode[i] == LINE_BREAK) || ((i - (lineIndex + 2)) >= STRING_BUFFER_SIZE)) {
				break;
			}
			stringBuffer[i - (lineIndex + 2)] = jooCode[i];
		}
	}
	
	// invert function used to invert the sign of shorteger data type
	// the interpreter doesn't support shorteger signing as it would add the overhead
	// of 1 character containing the sign in front of each shorteger value or variable declaration
	void invert(char[] jooCode, short lineIndex) {
		char variableType = getVariableType(parameterNames[0]);
		if((variableType == TYPE_INT) || (variableType == TYPE_FIXED)) {
			// getVariableIndex inside if because there is no need to get it if variable isn't number or bool
			short variableIndex = getVariableIndex(parameterNames[0]);
			variableValues[variableIndex] = (short) -variableValues[variableIndex];
		}
		else if(variableType == TYPE_BOOL) {
			short variableIndex = getVariableIndex(parameterNames[0]);
			variableValues[variableIndex] = (short) (variableValues[variableIndex] == 0 ? 1 : 0);
		}
	}
	
	void positive(char[] jooCode, short lineIndex) {
		char variableType = getVariableType(parameterNames[0]);
		if((variableType == TYPE_INT) || (variableType == TYPE_FIXED)) {
			short variableIndex = getVariableIndex(parameterNames[0]);
			if(variableValues[variableIndex] < 0) {
				variableValues[variableIndex] = (short) -variableValues[variableIndex];
			}
		}
		else if(variableType == TYPE_BOOL) {
			short variableIndex = getVariableIndex(parameterNames[0]);
			variableValues[variableIndex] = (short) 1;
		}
	}
	
	void negative(char[] jooCode, short lineIndex) {
		char variableType = getVariableType(parameterNames[0]);
		if((variableType == TYPE_INT) || (variableType == TYPE_FIXED)) {
			short variableIndex = getVariableIndex(parameterNames[0]);
			if(variableValues[variableIndex] > 0) {
				variableValues[variableIndex] = (short) -variableValues[variableIndex];
			}
		}
		else if(variableType == TYPE_BOOL) {
			short variableIndex = getVariableIndex(parameterNames[0]);
			variableValues[variableIndex] = (short) 0;
		}
	}
}