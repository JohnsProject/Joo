package com.johnsproject.joo;

import static com.johnsproject.joo.JooVirtualMachine.ADD;
import static com.johnsproject.joo.JooVirtualMachine.ARRAYS_START;
import static com.johnsproject.joo.JooVirtualMachine.ARRAY_INDICES_START;
import static com.johnsproject.joo.JooVirtualMachine.BIGGER;
import static com.johnsproject.joo.JooVirtualMachine.DIVIDE;
import static com.johnsproject.joo.JooVirtualMachine.EQUALS;
import static com.johnsproject.joo.JooVirtualMachine.FUNCTIONS_START;
import static com.johnsproject.joo.JooVirtualMachine.KEYWORD_FUNCTION;
import static com.johnsproject.joo.JooVirtualMachine.KEYWORD_FUNCTION_CALL;
import static com.johnsproject.joo.JooVirtualMachine.KEYWORD_IF;
import static com.johnsproject.joo.JooVirtualMachine.KEYWORD_PARAMETER;
import static com.johnsproject.joo.JooVirtualMachine.LINE_BREAK;
import static com.johnsproject.joo.JooVirtualMachine.MULTIPLY;
import static com.johnsproject.joo.JooVirtualMachine.NOT_EQUALS;
import static com.johnsproject.joo.JooVirtualMachine.PARAMETERS_START;
import static com.johnsproject.joo.JooVirtualMachine.SMALLER;
import static com.johnsproject.joo.JooVirtualMachine.SUBTRACT;
import static com.johnsproject.joo.JooVirtualMachine.TYPE_ARRAY_BOOL;
import static com.johnsproject.joo.JooVirtualMachine.TYPE_ARRAY_CHAR;
import static com.johnsproject.joo.JooVirtualMachine.TYPE_ARRAY_FIXED;
import static com.johnsproject.joo.JooVirtualMachine.TYPE_ARRAY_INT;
import static com.johnsproject.joo.JooVirtualMachine.TYPE_BOOL;
import static com.johnsproject.joo.JooVirtualMachine.TYPE_CHAR;
import static com.johnsproject.joo.JooVirtualMachine.TYPE_FIXED;
import static com.johnsproject.joo.JooVirtualMachine.TYPE_FUNCTION;
import static com.johnsproject.joo.JooVirtualMachine.TYPE_INT;
import static com.johnsproject.joo.JooVirtualMachine.VARIABLES_START;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class JooVirtualMachineTest {

	@Test
	public void initializeTest() throws Exception {
		JooVirtualMachine jooInterpreter = new JooVirtualMachine();
		char variable0Name = VARIABLES_START + (short) 0;
		char variable1Name = VARIABLES_START + 1;
		char variable2Name = VARIABLES_START + 2;
		char variable3Name = VARIABLES_START + 3;
		char variable4Name = VARIABLES_START + 4;
		char variable5Name = VARIABLES_START + 5;
		char param0Name = PARAMETERS_START + 1;
		char array0Name = ARRAYS_START + (short) 0;
		char array1Name = ARRAYS_START + 1;
		char array2Name = ARRAYS_START + 2;
		char array3Name = ARRAYS_START + 3;
		char array4Name = ARRAYS_START + 4;
		char startFunctionName = FUNCTIONS_START + (short) 0;
		char functionName = FUNCTIONS_START + 1;
		String rawJooCode = new String(
				"" + TYPE_INT + (char)3 + LINE_BREAK +
				"" + variable0Name + "100" + LINE_BREAK +
				"" + variable1Name + "10" + LINE_BREAK +
				"" + variable2Name + "" + LINE_BREAK +
				"" + TYPE_FIXED + (char)1 + LINE_BREAK +
				"" + variable3Name + "15.125" + LINE_BREAK +
				"" + TYPE_BOOL + (char)1 + LINE_BREAK +
				"" + variable4Name + "1" + LINE_BREAK +
				"" + TYPE_CHAR + (char)1 + LINE_BREAK +
				"" + variable5Name + "A" + LINE_BREAK +
				"" + TYPE_ARRAY_INT + (char)2 + LINE_BREAK +
				"" + array0Name + ((char)10) + LINE_BREAK +
				"" + array1Name + ((char)15) + LINE_BREAK +
				"" + TYPE_ARRAY_FIXED + (char)1 + LINE_BREAK +
				"" + array2Name + ((char)18) + LINE_BREAK +
				"" + TYPE_ARRAY_BOOL + (char)1 + LINE_BREAK +
				"" + array3Name + ((char)20) + LINE_BREAK +
				"" + TYPE_ARRAY_CHAR + (char)1 + LINE_BREAK +
				"" + array4Name + ((char)25) + LINE_BREAK +
				"" + TYPE_FUNCTION + (char)2 + LINE_BREAK +
				"" + KEYWORD_FUNCTION + startFunctionName + LINE_BREAK +
				"" + KEYWORD_FUNCTION + functionName + param0Name + LINE_BREAK +
				"" + KEYWORD_FUNCTION + LINE_BREAK
		);
		char[] jooCode = rawJooCode.toCharArray();
		char[] vmCode = jooInterpreter.getJooCode();
		jooInterpreter.setJooCodeSize((short) jooCode.length);
		for (int i = (short) 0; i < jooCode.length; i++) {
			vmCode[i] = jooCode[i];
		}	
		jooInterpreter.initialize();
		short[] functionLines = jooInterpreter.getFunctionLines();
		short[] variableValues = jooInterpreter.getVariableValues();
		byte[] arraySizes = jooInterpreter.getArraySizes();
		assertEquals(functionLines[0], 70);
		assertEquals(functionLines[1], 74);
		assertEquals(variableValues[0], 100);
		assertEquals(variableValues[1], 10);
		assertEquals(variableValues[2], (short) 0);
		assertEquals(variableValues[3], Math.round(15.125f * (1 << 8)));
		assertEquals(variableValues[4], 1);
		assertEquals(variableValues[5], 'A');
		assertEquals(arraySizes[0], 10);
		assertEquals(arraySizes[1], 15);
		assertEquals(arraySizes[2], 18);
		assertEquals(arraySizes[3], 20);
		assertEquals(arraySizes[4], 25);
	}
	
	@Test
	public void searchFunctionsTest() throws Exception {
		JooVirtualMachine jooInterpreter = new JooVirtualMachine();
		char startFunctionName = FUNCTIONS_START + (short) 0;
		char functionName = FUNCTIONS_START + 1;
		char parameter0Name = PARAMETERS_START + (short) 0;
		String rawJooCode = new String(
				"" + TYPE_FUNCTION + (char)2 + LINE_BREAK +
				"" + KEYWORD_FUNCTION + startFunctionName + LINE_BREAK +
				"" + KEYWORD_FUNCTION + functionName + parameter0Name + LINE_BREAK +
				"" + KEYWORD_FUNCTION + LINE_BREAK
		);
		char[] jooCode = rawJooCode.toCharArray();
		jooInterpreter.setJooCodeSize((short) 20);
		jooInterpreter.searchFunctions(jooCode, (short) (short) 0);
		jooInterpreter.searchFunctions(jooCode, (short) 3);
		jooInterpreter.searchFunctions(jooCode, (short) 6);
		jooInterpreter.searchFunctions(jooCode, (short) 8);
		jooInterpreter.searchFunctions(jooCode, (short) 11);
		short[] functionLines = jooInterpreter.getFunctionLines();
		assertEquals(functionLines[0], 6);
		assertEquals(functionLines[1], 10);
	}
	
	@Test
	public void searchIntsTest() throws Exception {
		JooVirtualMachine jooInterpreter = new JooVirtualMachine();
		char variable0Name = VARIABLES_START + (short) 0;
		char variable1Name = VARIABLES_START + 1;
		char variable2Name = VARIABLES_START + 2;
		String[] rawJooCode = {
				"" + TYPE_INT + (char)3 + LINE_BREAK,
				"" + variable0Name + "100" + LINE_BREAK,
				"" + variable1Name + "10" + LINE_BREAK,
				"" + variable2Name + "" + LINE_BREAK,
		};
		jooInterpreter.searchInts(rawJooCode[0].toCharArray(), (short) (short) 0);
		jooInterpreter.searchInts(rawJooCode[1].toCharArray(), (short) (short) 0);
		jooInterpreter.searchInts(rawJooCode[2].toCharArray(), (short) (short) 0);
		jooInterpreter.searchInts(rawJooCode[3].toCharArray(), (short) (short) 0);
		short[] variableValues = jooInterpreter.getVariableValues();
		assertEquals(variableValues[0], 100);
		assertEquals(variableValues[1], 10);
		assertEquals(variableValues[2], (short) 0);
	}
	
	@Test
	public void searchFixedsTest() throws Exception {
		JooVirtualMachine jooInterpreter = new JooVirtualMachine();
		char variable0Name = VARIABLES_START + (short) 0;
		char variable1Name = VARIABLES_START + 1;
		char variable2Name = VARIABLES_START + 2;
		String[] rawJooCode = {
				"" + TYPE_FIXED + (char)3 + LINE_BREAK,
				"" + variable0Name + "42.15" + LINE_BREAK,
				"" + variable1Name + "10" + LINE_BREAK,
				"" + variable2Name + "" + LINE_BREAK,
		};
		jooInterpreter.searchFixeds(rawJooCode[0].toCharArray(), (short) (short) 0);
		jooInterpreter.searchFixeds(rawJooCode[1].toCharArray(), (short) (short) 0);
		jooInterpreter.searchFixeds(rawJooCode[2].toCharArray(), (short) (short) 0);
		jooInterpreter.searchFixeds(rawJooCode[3].toCharArray(), (short) (short) 0);
		short[] variableValues = jooInterpreter.getVariableValues();
		// test float +1 and -1 because of precision errors
		assertEquals(variableValues[0], Math.round(42.15f * (1 << 8)));
		assertEquals(variableValues[1], Math.round(10f * (1 << 8)));
		assertEquals(variableValues[2], Math.round(0f * (1 << 8)));
	}
	
	@Test
	public void searchBoolsTest() throws Exception {
		JooVirtualMachine jooInterpreter = new JooVirtualMachine();
		char variable0Name = VARIABLES_START + (short) 0;
		char variable1Name = VARIABLES_START + 1;
		char variable2Name = VARIABLES_START + 2;
		String[] rawJooCode = {
				"" + TYPE_BOOL + (char)3 + LINE_BREAK,
				"" + variable0Name + "1" + LINE_BREAK,
				"" + variable1Name + "0" + LINE_BREAK,
				"" + variable2Name + "1" + LINE_BREAK,
		};
		jooInterpreter.searchBools(rawJooCode[0].toCharArray(), (short) (short) 0);
		jooInterpreter.searchBools(rawJooCode[1].toCharArray(), (short) (short) 0);
		jooInterpreter.searchBools(rawJooCode[2].toCharArray(), (short) (short) 0);
		jooInterpreter.searchBools(rawJooCode[3].toCharArray(), (short) (short) 0);
		short[] variableValues = jooInterpreter.getVariableValues();
		// test float +1 and -1 because of precision errors
		assertEquals(variableValues[0], 1);
		assertEquals(variableValues[1], 0);
		assertEquals(variableValues[2], 1);
	}
	
	@Test
	public void searchCharsTest() throws Exception {
		JooVirtualMachine jooInterpreter = new JooVirtualMachine();
		char variable0Name = VARIABLES_START + (short) 0;
		char variable1Name = VARIABLES_START + 1;
		char variable2Name = VARIABLES_START + 2;
		String[] rawJooCode = {
				"" + TYPE_CHAR + (char)3 + LINE_BREAK,
				"" + variable0Name + "A" + LINE_BREAK,
				"" + variable1Name + "b" + LINE_BREAK,
				"" + variable2Name + "C" + LINE_BREAK,
		};
		jooInterpreter.searchChars(rawJooCode[0].toCharArray(), (short) (short) 0);
		jooInterpreter.searchChars(rawJooCode[1].toCharArray(), (short) (short) 0);
		jooInterpreter.searchChars(rawJooCode[2].toCharArray(), (short) (short) 0);
		jooInterpreter.searchChars(rawJooCode[3].toCharArray(), (short) (short) 0);
		short[] variableValues = jooInterpreter.getVariableValues();
		// test float +1 and -1 because of precision errors
		assertEquals(variableValues[0], 'A');
		assertEquals(variableValues[1], 'b');
		assertEquals(variableValues[2], 'C');
	}
	
	@Test
	public void searchIntArraysTest() throws Exception {
		JooVirtualMachine jooInterpreter = new JooVirtualMachine();
		char variable0Name = ARRAYS_START + (short) 0;
		char variable1Name = ARRAYS_START + 1;
		char variable2Name = ARRAYS_START + 2;
		String[] rawJooCode = {
				"" + TYPE_ARRAY_INT + (char)3 + LINE_BREAK,
				"" + variable0Name + ((char)32) + LINE_BREAK,
				"" + variable1Name + ((char)16) + LINE_BREAK,
				"" + variable2Name + ((char)48) + LINE_BREAK,
		};
		jooInterpreter.searchIntArrays(rawJooCode[0].toCharArray(), (short)(short) 0);
		jooInterpreter.searchIntArrays(rawJooCode[1].toCharArray(), (short)(short) 0);
		jooInterpreter.searchIntArrays(rawJooCode[2].toCharArray(), (short)(short) 0);
		jooInterpreter.searchIntArrays(rawJooCode[3].toCharArray(), (short)(short) 0);
		byte[] arraySizes = jooInterpreter.getArraySizes();
		assertEquals(arraySizes[0], 32);
		assertEquals(arraySizes[1], 16);
		assertEquals(arraySizes[2], 48);
	}
	
	@Test
	public void searchFixedArraysTest() throws Exception {
		JooVirtualMachine jooInterpreter = new JooVirtualMachine();
		char variable0Name = ARRAYS_START + (short) 0;
		char variable1Name = ARRAYS_START + 1;
		char variable2Name = ARRAYS_START + 2;
		String[] rawJooCode = {
				"" + TYPE_ARRAY_FIXED + (char)3 + LINE_BREAK,
				"" + variable0Name + ((char)32) + LINE_BREAK,
				"" + variable1Name + ((char)16) + LINE_BREAK,
				"" + variable2Name + ((char)48) + LINE_BREAK,
		};
		jooInterpreter.searchFixedArrays(rawJooCode[0].toCharArray(), (short)(short) 0);
		jooInterpreter.searchFixedArrays(rawJooCode[1].toCharArray(), (short)(short) 0);
		jooInterpreter.searchFixedArrays(rawJooCode[2].toCharArray(), (short)(short) 0);
		jooInterpreter.searchFixedArrays(rawJooCode[3].toCharArray(), (short)(short) 0);
		byte[] arraySizes = jooInterpreter.getArraySizes();
		assertEquals(arraySizes[0], 32);
		assertEquals(arraySizes[1], 16);
		assertEquals(arraySizes[2], 48);
	}
	
	@Test
	public void searchBoolArraysTest() throws Exception {
		JooVirtualMachine jooInterpreter = new JooVirtualMachine();
		char variable0Name = ARRAYS_START + (short) 0;
		char variable1Name = ARRAYS_START + 1;
		char variable2Name = ARRAYS_START + 2;
		String[] rawJooCode = {
				"" + TYPE_ARRAY_BOOL + (char)3 + LINE_BREAK,
				"" + variable0Name + ((char)32) + LINE_BREAK,
				"" + variable1Name + ((char)16) + LINE_BREAK,
				"" + variable2Name + ((char)48) + LINE_BREAK,
		};
		jooInterpreter.searchBoolArrays(rawJooCode[0].toCharArray(), (short)(short) 0);
		jooInterpreter.searchBoolArrays(rawJooCode[1].toCharArray(), (short)(short) 0);
		jooInterpreter.searchBoolArrays(rawJooCode[2].toCharArray(), (short)(short) 0);
		jooInterpreter.searchBoolArrays(rawJooCode[3].toCharArray(), (short)(short) 0);
		byte[] arraySizes = jooInterpreter.getArraySizes();
		assertEquals(arraySizes[0], 32);
		assertEquals(arraySizes[1], 16);
		assertEquals(arraySizes[2], 48);
	}
	
	@Test
	public void searchCharArraysTest() throws Exception {
		JooVirtualMachine jooInterpreter = new JooVirtualMachine();
		char variable0Name = ARRAYS_START + (short) 0;
		char variable1Name = ARRAYS_START + 1;
		char variable2Name = ARRAYS_START + 2;
		String[] rawJooCode = {
				"" + TYPE_ARRAY_CHAR + (char)3 + LINE_BREAK,
				"" + variable0Name + ((char)32) + LINE_BREAK,
				"" + variable1Name + ((char)16) + LINE_BREAK,
				"" + variable2Name + ((char)48) + LINE_BREAK,
		};
		jooInterpreter.searchCharArrays(rawJooCode[0].toCharArray(), (short)(short) 0);
		jooInterpreter.searchCharArrays(rawJooCode[1].toCharArray(), (short)(short) 0);
		jooInterpreter.searchCharArrays(rawJooCode[2].toCharArray(), (short)(short) 0);
		jooInterpreter.searchCharArrays(rawJooCode[3].toCharArray(), (short)(short) 0);
		byte[] arraySizes = jooInterpreter.getArraySizes();
		assertEquals(arraySizes[0], 32);
		assertEquals(arraySizes[1], 16);
		assertEquals(arraySizes[2], 48);
	}
	
	@Test
	public void interpretFunctionTest() throws Exception {
		JooVirtualMachine jooInterpreter = new JooVirtualMachine();
		char variable0Name = VARIABLES_START + 0;
		char variable1Name = VARIABLES_START + 1;
		char variable2Name = VARIABLES_START + 2;
		char parameter0Name = PARAMETERS_START + 0;
		char startFunctionName = FUNCTIONS_START + 0;
		char functionName = FUNCTIONS_START + 1;
		String rawJooCode = new String(
				"" + TYPE_INT + (char)3 + LINE_BREAK +
				"" + variable0Name + "100" + LINE_BREAK +				
				"" + variable1Name + "10" + LINE_BREAK +
				"" + variable2Name + "" + LINE_BREAK +
				"" + TYPE_FUNCTION + (char)2 + LINE_BREAK +
				"" + KEYWORD_FUNCTION + startFunctionName + LINE_BREAK +
				"" + variable2Name + ADD + variable1Name + LINE_BREAK +
				"" + KEYWORD_FUNCTION_CALL + functionName + variable1Name + LINE_BREAK +
				"" + KEYWORD_FUNCTION + functionName + parameter0Name + LINE_BREAK +
				"" + parameter0Name + DIVIDE + "2" + LINE_BREAK +
				"" + KEYWORD_IF + parameter0Name + SMALLER + variable0Name + LINE_BREAK +
				"" + parameter0Name + MULTIPLY + "30" + LINE_BREAK +
				"" + KEYWORD_IF + parameter0Name + BIGGER + variable0Name + LINE_BREAK +
				"" + parameter0Name + DIVIDE + "2" + LINE_BREAK +
				"" + KEYWORD_IF + LINE_BREAK +
				"" + KEYWORD_IF + parameter0Name + BIGGER + variable0Name + LINE_BREAK +
				"" + parameter0Name + DIVIDE + "2" + LINE_BREAK +
				"" + KEYWORD_IF + LINE_BREAK +
				"" + parameter0Name + ADD + "30" + LINE_BREAK +
				"" + KEYWORD_IF + LINE_BREAK +
				"" + KEYWORD_FUNCTION + LINE_BREAK
		);
		char[] jooCode = rawJooCode.toCharArray();
		char[] vmCode = jooInterpreter.getJooCode();
		jooInterpreter.setJooCodeSize((short) jooCode.length);
		for (int i = (short) 0; i < jooCode.length; i++) {
			vmCode[i] = jooCode[i];
		}	
		jooInterpreter.initialize();
		jooInterpreter.start();
		short[] functionLines = jooInterpreter.getFunctionLines();
		short[] variableValues = jooInterpreter.getVariableValues();
		assertEquals(functionLines[0], 20);
		assertEquals(functionLines[1], 32);
		assertEquals(variableValues[0], 100);
		assertEquals(variableValues[1], 105);
		assertEquals(variableValues[2], 10);
	}
	
	@Test
	public void callFunctionTest() throws Exception {
		JooVirtualMachine jooInterpreter = new JooVirtualMachine();
		char variable0Name = VARIABLES_START + (short) 0;
		char variable1Name = VARIABLES_START + 1;
		char array0Name = ARRAYS_START + (short) 0;
		char parameter0Name = PARAMETERS_START + (short) 0;
		char parameter1Name = PARAMETERS_START + 1;
		char parameter2Name = PARAMETERS_START + 2;
		char parameter3Name = PARAMETERS_START + 3;
		char parameter4Name = PARAMETERS_START + 4;
		char startFunctionName = FUNCTIONS_START + (short) 0;
		char functionName = FUNCTIONS_START + 1;
		String rawJooCode = new String(
				"" + TYPE_INT + (char)2 + LINE_BREAK +
				"" + variable0Name + "100" + LINE_BREAK +				
				"" + variable1Name + "2" + LINE_BREAK +
				"" + TYPE_ARRAY_INT + (char)1 + LINE_BREAK +
				"" + array0Name + ((char)10) + LINE_BREAK +
				"" + TYPE_FUNCTION + (char)2 + LINE_BREAK +
				"" + KEYWORD_FUNCTION + startFunctionName + LINE_BREAK +
				"" + array0Name + ((char)(3 + ARRAY_INDICES_START)) + EQUALS + "50" + LINE_BREAK + 
				"" + array0Name + ((char)(2 + ARRAY_INDICES_START)) + EQUALS + "25" + LINE_BREAK + 
				"" + KEYWORD_FUNCTION_CALL + functionName + variable0Name
														+ KEYWORD_PARAMETER + variable1Name
														+ KEYWORD_PARAMETER + array0Name
														+ KEYWORD_PARAMETER + array0Name + ((char)(2 + ARRAY_INDICES_START))
														+ KEYWORD_PARAMETER + array0Name + variable1Name + LINE_BREAK +
				"" + KEYWORD_FUNCTION + functionName + parameter0Name + KEYWORD_PARAMETER + parameter1Name  
														+ KEYWORD_PARAMETER + parameter2Name + KEYWORD_PARAMETER + parameter3Name
														+ KEYWORD_PARAMETER + parameter4Name + LINE_BREAK +
				"" + parameter0Name + ADD + "10" + LINE_BREAK +
				"" + parameter1Name + EQUALS + "20" + LINE_BREAK +
				"" + parameter2Name + ((char)(3 + ARRAY_INDICES_START)) + MULTIPLY + "3" + LINE_BREAK +
				"" + parameter3Name + ADD + "30" + LINE_BREAK +
				"" + parameter4Name + ADD + parameter2Name + ((char)(3 + ARRAY_INDICES_START)) + LINE_BREAK +
				"" + KEYWORD_FUNCTION + LINE_BREAK
		);
		char[] jooCode = rawJooCode.toCharArray();
		char[] vmCode = jooInterpreter.getJooCode();
		jooInterpreter.setJooCodeSize((short) jooCode.length);
		for (int i = (short) 0; i < jooCode.length; i++) {
			vmCode[i] = jooCode[i];
		}	
		jooInterpreter.initialize();
		jooInterpreter.start();
		short[] variableValues = jooInterpreter.getVariableValues();
		byte[] arrayValues = jooInterpreter.getArraySizes();
		assertEquals(variableValues[0], 110);
		assertEquals(variableValues[1], 20);
		assertEquals(arrayValues[0], 10);
		assertEquals(variableValues[2 + 32], 205);
		assertEquals(variableValues[3 + 32], 150);
	}
	
	@Test
	public void interpretIfBlockTest() throws Exception {
		JooVirtualMachine jooInterpreter = new JooVirtualMachine();
		char variable0Name = VARIABLES_START + (short) 0;
		char variable1Name = VARIABLES_START + 1;
		char variable2Name = VARIABLES_START + 2;
		String[] rawJooCode = {
				"" + TYPE_INT + (char)3 + LINE_BREAK,
				"" + variable0Name + "100" + LINE_BREAK,
				"" + variable1Name + "10" + LINE_BREAK,
				"" + variable2Name + "" + LINE_BREAK,
				"" + KEYWORD_IF + variable1Name + SMALLER + variable0Name + LINE_BREAK,
				"" + KEYWORD_IF + LINE_BREAK,
				"" + KEYWORD_IF + variable0Name + SMALLER + "30" + LINE_BREAK,
				"" + KEYWORD_IF + LINE_BREAK,
		};
		jooInterpreter.searchInts(rawJooCode[0].toCharArray(), (short) 0);
		jooInterpreter.searchInts(rawJooCode[1].toCharArray(), (short) 0);
		jooInterpreter.searchInts(rawJooCode[2].toCharArray(), (short) 0);
		jooInterpreter.searchInts(rawJooCode[3].toCharArray(), (short) 0);
		jooInterpreter.interpretIfBlock(rawJooCode[4].toCharArray(), (short) 0);
		assertEquals(jooInterpreter.getAllIfs(), 1);
		assertEquals(jooInterpreter.getFailedIfs(), (short) 0);
		jooInterpreter.interpretIfBlock(rawJooCode[5].toCharArray(), (short) 0);
		jooInterpreter.interpretIfBlock(rawJooCode[6].toCharArray(), (short) 0);
		assertEquals(jooInterpreter.getAllIfs(), 1);
		assertEquals(jooInterpreter.getFailedIfs(), 1);
		jooInterpreter.interpretIfBlock(rawJooCode[7].toCharArray(), (short) 0);
		assertEquals(jooInterpreter.getAllIfs(), (short) 0);
		assertEquals(jooInterpreter.getFailedIfs(), (short) 0);
	}
	
	@Test
	public void interpretIfOperationTest() throws Exception {
		JooVirtualMachine jooInterpreter = new JooVirtualMachine();
		char variable0Name = VARIABLES_START + (short) 0;
		char variable1Name = VARIABLES_START + 1;
		String[] rawJooCode = {
				"" + TYPE_INT + (char)2 + LINE_BREAK,
				"" + variable0Name + "100" + LINE_BREAK,
				"" + variable1Name + "10" + LINE_BREAK,
				"" + KEYWORD_IF + variable1Name + SMALLER + variable0Name + LINE_BREAK,
				"" + KEYWORD_IF + LINE_BREAK,
				"" + KEYWORD_IF + variable1Name + SMALLER + EQUALS + variable0Name + LINE_BREAK,
				"" + KEYWORD_IF + LINE_BREAK,
				"" + KEYWORD_IF + variable0Name + BIGGER + EQUALS + "100" + LINE_BREAK,
				"" + KEYWORD_IF + LINE_BREAK,
				"" + KEYWORD_IF + variable0Name + BIGGER + "80" + LINE_BREAK,
				"" + KEYWORD_IF + LINE_BREAK,
				"" + KEYWORD_IF + variable0Name + EQUALS + "100" + LINE_BREAK,
				"" + KEYWORD_IF + LINE_BREAK,
				"" + KEYWORD_IF + variable0Name + NOT_EQUALS + "100" + LINE_BREAK,
				"" + KEYWORD_IF + LINE_BREAK,
		};
		jooInterpreter.searchInts(rawJooCode[0].toCharArray(), (short) 0);
		jooInterpreter.searchInts(rawJooCode[1].toCharArray(), (short) 0);
		jooInterpreter.searchInts(rawJooCode[2].toCharArray(), (short) 0);
		assertEquals(jooInterpreter.interpretIfOperation(rawJooCode[3].toCharArray(), (short) 0), true);
		assertEquals(jooInterpreter.interpretIfOperation(rawJooCode[4].toCharArray(), (short) 0), false);
		assertEquals(jooInterpreter.interpretIfOperation(rawJooCode[5].toCharArray(), (short) 0), true);
		assertEquals(jooInterpreter.interpretIfOperation(rawJooCode[6].toCharArray(), (short) 0), false);
		assertEquals(jooInterpreter.interpretIfOperation(rawJooCode[7].toCharArray(), (short) 0), true);
		assertEquals(jooInterpreter.interpretIfOperation(rawJooCode[8].toCharArray(), (short) 0), false);
		assertEquals(jooInterpreter.interpretIfOperation(rawJooCode[9].toCharArray(), (short) 0), true);
		assertEquals(jooInterpreter.interpretIfOperation(rawJooCode[10].toCharArray(), (short) 0), false);
		assertEquals(jooInterpreter.interpretIfOperation(rawJooCode[11].toCharArray(), (short) 0), true);
		assertEquals(jooInterpreter.interpretIfOperation(rawJooCode[12].toCharArray(), (short) 0), false);
		assertEquals(jooInterpreter.interpretIfOperation(rawJooCode[13].toCharArray(), (short) 0), false);
		assertEquals(jooInterpreter.interpretIfOperation(rawJooCode[14].toCharArray(), (short) 0), false);
	}
	
	@Test
	public void interpretVariableOperationTest() throws Exception {
		JooVirtualMachine jooInterpreter = new JooVirtualMachine();
		char variable0Name = VARIABLES_START + (short) 0;
		char variable1Name = VARIABLES_START + 1;
		char variable2Name = VARIABLES_START + 2;
		String[] rawJooCode = {
				"" + TYPE_INT + (char)3 + LINE_BREAK,
				"" + variable0Name + "100" + LINE_BREAK,
				"" + variable1Name + "10" + LINE_BREAK,
				"" + variable2Name + "" + LINE_BREAK,
				"" + variable2Name + ADD + variable1Name + LINE_BREAK,
				"" + variable1Name + EQUALS + "12" + LINE_BREAK,
				"" + variable1Name + SUBTRACT + "2" + LINE_BREAK,
				"" + variable1Name + DIVIDE + "2" + LINE_BREAK,
				"" + variable1Name + MULTIPLY + "2" + LINE_BREAK,
		};
		jooInterpreter.searchInts(rawJooCode[0].toCharArray(), (short) 0);
		jooInterpreter.searchInts(rawJooCode[1].toCharArray(), (short) 0);
		jooInterpreter.searchInts(rawJooCode[2].toCharArray(), (short) 0);
		jooInterpreter.searchInts(rawJooCode[3].toCharArray(), (short) 0);
		jooInterpreter.interpretIntOperation(rawJooCode[4].toCharArray(), (short) 0);
		jooInterpreter.interpretIntOperation(rawJooCode[5].toCharArray(), (short) 0);
		jooInterpreter.interpretIntOperation(rawJooCode[6].toCharArray(), (short) 0);
		jooInterpreter.interpretIntOperation(rawJooCode[7].toCharArray(), (short) 0);
		jooInterpreter.interpretIntOperation(rawJooCode[8].toCharArray(), (short) 0);
		short[] variableValues = jooInterpreter.getVariableValues();
		assertEquals(variableValues[0], 100);
		assertEquals(variableValues[1], 10);
		assertEquals(variableValues[2], 10);
	}
	
	@Test
	public void interpretIntOperationTest() throws Exception {
		JooVirtualMachine jooInterpreter = new JooVirtualMachine();
		char variable0Name = VARIABLES_START + (short) 0;
		char variable1Name = VARIABLES_START + 1;
		char variable2Name = VARIABLES_START + 2;
		String[] rawJooCode = {
				"" + TYPE_INT + (char)3 + LINE_BREAK,
				"" + variable0Name + "100" + LINE_BREAK,
				"" + variable1Name + "10" + LINE_BREAK,
				"" + variable2Name + "" + LINE_BREAK,
				"" + variable2Name + ADD + variable1Name + LINE_BREAK,
				"" + variable1Name + EQUALS + "12" + LINE_BREAK,
				"" + variable1Name + SUBTRACT + "2" + LINE_BREAK,
				"" + variable1Name + DIVIDE + "2" + LINE_BREAK,
				"" + variable1Name + MULTIPLY + "2" + LINE_BREAK,
		};
		jooInterpreter.searchInts(rawJooCode[0].toCharArray(), (short) 0);
		jooInterpreter.searchInts(rawJooCode[1].toCharArray(), (short) 0);
		jooInterpreter.searchInts(rawJooCode[2].toCharArray(), (short) 0);
		jooInterpreter.searchInts(rawJooCode[3].toCharArray(), (short) 0);
		jooInterpreter.interpretIntOperation(rawJooCode[4].toCharArray(), (short) 0);
		jooInterpreter.interpretIntOperation(rawJooCode[5].toCharArray(), (short) 0);
		jooInterpreter.interpretIntOperation(rawJooCode[6].toCharArray(), (short) 0);
		jooInterpreter.interpretIntOperation(rawJooCode[7].toCharArray(), (short) 0);
		jooInterpreter.interpretIntOperation(rawJooCode[8].toCharArray(), (short) 0);
		short[] variableValues = jooInterpreter.getVariableValues();
		assertEquals(variableValues[0], 100);
		assertEquals(variableValues[1], 10);
		assertEquals(variableValues[2], 10);
	}
	
	@Test
	public void interpretIntComparisonTest() throws Exception {
		JooVirtualMachine jooInterpreter = new JooVirtualMachine();
		char variable0Name = VARIABLES_START + (short) 0;
		char variable1Name = VARIABLES_START + 1;
		char variable2Name = VARIABLES_START + 2;
		String[] rawJooCode = {
				"" + TYPE_INT + (char)3 + LINE_BREAK,
				"" + variable0Name + "100" + LINE_BREAK,
				"" + variable1Name + "10" + LINE_BREAK,
				"" + variable2Name + "" + LINE_BREAK,
				"" + variable2Name + EQUALS + "0" + LINE_BREAK,
				"" + variable2Name + NOT_EQUALS + "1" + LINE_BREAK,
				"" + variable2Name + BIGGER + "1" + LINE_BREAK,
				"" + variable2Name + SMALLER + "1" + LINE_BREAK,
				"" + variable0Name + BIGGER + EQUALS + variable1Name + LINE_BREAK,
				"" + variable0Name + SMALLER + EQUALS + variable1Name + LINE_BREAK,
		};
		jooInterpreter.searchInts(rawJooCode[0].toCharArray(), (short) 0);
		jooInterpreter.searchInts(rawJooCode[1].toCharArray(), (short) 0);
		jooInterpreter.searchInts(rawJooCode[2].toCharArray(), (short) 0);
		jooInterpreter.searchInts(rawJooCode[3].toCharArray(), (short) 0);
		assertEquals(jooInterpreter.interpretIntComparison(rawJooCode[4].toCharArray(), (short) 0), true);
		assertEquals(jooInterpreter.interpretIntComparison(rawJooCode[5].toCharArray(), (short) 0), true);
		assertEquals(jooInterpreter.interpretIntComparison(rawJooCode[6].toCharArray(), (short) 0), false);
		assertEquals(jooInterpreter.interpretIntComparison(rawJooCode[7].toCharArray(), (short) 0), true);
		assertEquals(jooInterpreter.interpretIntComparison(rawJooCode[8].toCharArray(), (short) 0), true);
		assertEquals(jooInterpreter.interpretIntComparison(rawJooCode[9].toCharArray(), (short) 0), false);
	}
	
	@Test
	public void interpretFixedOperationTest() throws Exception {
		JooVirtualMachine jooInterpreter = new JooVirtualMachine();
		char variable0Name = VARIABLES_START + (short) 0;
		char variable1Name = VARIABLES_START + 1;
		char variable2Name = VARIABLES_START + 2;
		String[] rawJooCode = {
				"" + TYPE_FIXED + (char)3 + LINE_BREAK,
				"" + variable0Name + "100" + LINE_BREAK,
				"" + variable1Name + "10" + LINE_BREAK,
				"" + variable2Name + "" + LINE_BREAK,
				"" + variable2Name + ADD + variable1Name + LINE_BREAK,
				"" + variable1Name + EQUALS + "12.5" + LINE_BREAK,
				"" + variable1Name + SUBTRACT + "0.5" + LINE_BREAK,
				"" + variable1Name + DIVIDE + "2" + LINE_BREAK,
				"" + variable1Name + MULTIPLY + "2.5" + LINE_BREAK,
		};
		jooInterpreter.searchFixeds(rawJooCode[0].toCharArray(), (short) 0);
		jooInterpreter.searchFixeds(rawJooCode[1].toCharArray(), (short) 0);
		jooInterpreter.searchFixeds(rawJooCode[2].toCharArray(), (short) 0);
		jooInterpreter.searchFixeds(rawJooCode[3].toCharArray(), (short) 0);
		jooInterpreter.interpretFixedOperation(rawJooCode[4].toCharArray(), (short) 0);
		jooInterpreter.interpretFixedOperation(rawJooCode[5].toCharArray(), (short) 0);
		jooInterpreter.interpretFixedOperation(rawJooCode[6].toCharArray(), (short) 0);
		jooInterpreter.interpretFixedOperation(rawJooCode[7].toCharArray(), (short) 0);
		jooInterpreter.interpretFixedOperation(rawJooCode[8].toCharArray(), (short) 0);
		short[] variableValues = jooInterpreter.getVariableValues();
		assertEquals(variableValues[0], Math.round(100f * (1 << 8)));
		assertEquals(variableValues[1], Math.round(15f * (1 << 8)));
		assertEquals(variableValues[2], Math.round(10f * (1 << 8)));
	}
	
	@Test
	public void interpretFixedComparisonTest() throws Exception {
		JooVirtualMachine jooInterpreter = new JooVirtualMachine();
		char variable0Name = VARIABLES_START + (short) 0;
		char variable1Name = VARIABLES_START + 1;
		char variable2Name = VARIABLES_START + 2;
		String[] rawJooCode = {
				"" + TYPE_FIXED + (char)3 + LINE_BREAK,
				"" + variable0Name + "10.5" + LINE_BREAK,
				"" + variable1Name + "10" + LINE_BREAK,
				"" + variable2Name + "" + LINE_BREAK,
				"" + variable2Name + EQUALS + "0" + LINE_BREAK,
				"" + variable2Name + NOT_EQUALS + "1" + LINE_BREAK,
				"" + variable2Name + BIGGER + "1" + LINE_BREAK,
				"" + variable2Name + SMALLER + "1" + LINE_BREAK,
				"" + variable0Name + BIGGER + EQUALS + variable1Name + LINE_BREAK,
				"" + variable0Name + SMALLER + EQUALS + variable1Name + LINE_BREAK,
		};
		jooInterpreter.searchFixeds(rawJooCode[0].toCharArray(), (short) 0);
		jooInterpreter.searchFixeds(rawJooCode[1].toCharArray(), (short) 0);
		jooInterpreter.searchFixeds(rawJooCode[2].toCharArray(), (short) 0);
		jooInterpreter.searchFixeds(rawJooCode[3].toCharArray(), (short) 0);
		assertEquals(jooInterpreter.interpretFixedComparison(rawJooCode[4].toCharArray(), (short) 0), true);
		assertEquals(jooInterpreter.interpretFixedComparison(rawJooCode[5].toCharArray(), (short) 0), true);
		assertEquals(jooInterpreter.interpretFixedComparison(rawJooCode[6].toCharArray(), (short) 0), false);
		assertEquals(jooInterpreter.interpretFixedComparison(rawJooCode[7].toCharArray(), (short) 0), true);
		assertEquals(jooInterpreter.interpretFixedComparison(rawJooCode[8].toCharArray(), (short) 0), true);
		assertEquals(jooInterpreter.interpretFixedComparison(rawJooCode[9].toCharArray(), (short) 0), false);
	}
	
	@Test
	public void interpretBoolOperationTest() throws Exception {
		JooVirtualMachine jooInterpreter = new JooVirtualMachine();
		char variable0Name = VARIABLES_START + (short) 0;
		char variable1Name = VARIABLES_START + 1;
		char variable2Name = VARIABLES_START + 2;
		String[] rawJooCode = {
				"" + TYPE_BOOL + (char)3 + LINE_BREAK,
				"" + variable0Name + "1" + LINE_BREAK,
				"" + variable1Name + "0" + LINE_BREAK,
				"" + variable2Name + "1" + LINE_BREAK,
				"" + variable2Name + EQUALS + "0" + LINE_BREAK,
				"" + variable1Name + NOT_EQUALS + "0" + LINE_BREAK,
				"" + variable1Name + EQUALS + variable0Name + LINE_BREAK,
		};
		jooInterpreter.searchBools(rawJooCode[0].toCharArray(), (short) 0);
		jooInterpreter.searchBools(rawJooCode[1].toCharArray(), (short) 0);
		jooInterpreter.searchBools(rawJooCode[2].toCharArray(), (short) 0);
		jooInterpreter.searchBools(rawJooCode[3].toCharArray(), (short) 0);
		jooInterpreter.interpretBoolOperation(rawJooCode[4].toCharArray(), (short) 0);
		jooInterpreter.interpretBoolOperation(rawJooCode[5].toCharArray(), (short) 0);
		jooInterpreter.interpretBoolOperation(rawJooCode[6].toCharArray(), (short) 0);
		short[] variableValues = jooInterpreter.getVariableValues();
		assertEquals(variableValues[0], 1);
		assertEquals(variableValues[1], 1);
		assertEquals(variableValues[2], 0);
	}
	
	@Test
	public void interpretBoolComparisonTest() throws Exception {
		JooVirtualMachine jooInterpreter = new JooVirtualMachine();
		char variable0Name = VARIABLES_START + (short) 0;
		char variable1Name = VARIABLES_START + 1;
		char variable2Name = VARIABLES_START + 2;
		String[] rawJooCode = {
				"" + TYPE_BOOL + (char)3 + LINE_BREAK,
				"" + variable0Name + "1" + LINE_BREAK,
				"" + variable1Name + "0" + LINE_BREAK,
				"" + variable2Name + "1" + LINE_BREAK,
				"" + variable1Name + EQUALS + "0" + LINE_BREAK,
				"" + variable2Name + NOT_EQUALS + "0" + LINE_BREAK,
				"" + variable2Name + NOT_EQUALS + variable1Name + LINE_BREAK,
		};
		jooInterpreter.searchBools(rawJooCode[0].toCharArray(), (short) 0);
		jooInterpreter.searchBools(rawJooCode[1].toCharArray(), (short) 0);
		jooInterpreter.searchBools(rawJooCode[2].toCharArray(), (short) 0);
		jooInterpreter.searchBools(rawJooCode[3].toCharArray(), (short) 0);
		assertEquals(jooInterpreter.interpretBoolComparison(rawJooCode[4].toCharArray(), (short) 0), true);
		assertEquals(jooInterpreter.interpretBoolComparison(rawJooCode[5].toCharArray(), (short) 0), true);
		assertEquals(jooInterpreter.interpretBoolComparison(rawJooCode[6].toCharArray(), (short) 0), true);
	}
	
	@Test
	public void interpretCharOperationTest() throws Exception {
		JooVirtualMachine jooInterpreter = new JooVirtualMachine();
		char variable0Name = VARIABLES_START + (short) 0;
		char variable1Name = VARIABLES_START + 1;
		char variable2Name = VARIABLES_START + 2;
		String[] rawJooCode = {
				"" + TYPE_CHAR + (char)3 + LINE_BREAK,
				"" + variable0Name + "A" + LINE_BREAK,
				"" + variable1Name + "b" + LINE_BREAK,
				"" + variable2Name + "C" + LINE_BREAK,
				"" + variable2Name + EQUALS + "c" + LINE_BREAK,
		};
		jooInterpreter.searchChars(rawJooCode[0].toCharArray(), (short) 0);
		jooInterpreter.searchChars(rawJooCode[1].toCharArray(), (short) 0);
		jooInterpreter.searchChars(rawJooCode[2].toCharArray(), (short) 0);
		jooInterpreter.searchChars(rawJooCode[3].toCharArray(), (short) 0);
		jooInterpreter.interpretCharOperation(rawJooCode[4].toCharArray(), (short) 0);
		short[] variableValues = jooInterpreter.getVariableValues();
		assertEquals(variableValues[0], 'A');
		assertEquals(variableValues[1], 'b');
		assertEquals(variableValues[2], 'c');
	}
	
	@Test
	public void interpretCharComparisonTest() throws Exception {
		JooVirtualMachine jooInterpreter = new JooVirtualMachine();
		char variable0Name = VARIABLES_START + (short) 0;
		char variable1Name = VARIABLES_START + 1;
		char variable2Name = VARIABLES_START + 2;
		String[] rawJooCode = {
				"" + TYPE_CHAR + (char)3 + LINE_BREAK,
				"" + variable0Name + "A" + LINE_BREAK,
				"" + variable1Name + "b" + LINE_BREAK,
				"" + variable2Name + "C" + LINE_BREAK,
				"" + variable1Name + EQUALS + "b" + LINE_BREAK,
				"" + variable2Name + NOT_EQUALS + "A" + LINE_BREAK,
				"" + variable2Name + NOT_EQUALS + variable0Name + LINE_BREAK,
		};
		jooInterpreter.searchChars(rawJooCode[0].toCharArray(), (short) 0);
		jooInterpreter.searchChars(rawJooCode[1].toCharArray(), (short) 0);
		jooInterpreter.searchChars(rawJooCode[2].toCharArray(), (short) 0);
		jooInterpreter.searchChars(rawJooCode[3].toCharArray(), (short) 0);
		assertEquals(jooInterpreter.interpretCharComparison(rawJooCode[4].toCharArray(), (short) 0), true);
		assertEquals(jooInterpreter.interpretCharComparison(rawJooCode[5].toCharArray(), (short) 0), true);
		assertEquals(jooInterpreter.interpretCharComparison(rawJooCode[6].toCharArray(), (short) 0), true);
	}
	
	@Test
	public void interpretIntArrayOperationTest() throws Exception {
		JooVirtualMachine jooInterpreter = new JooVirtualMachine();
		char array0Name = ARRAYS_START + (short) 0;
		char array1Name = ARRAYS_START + 1;
		char variable0Name = VARIABLES_START + (short) 0;
		String[] rawJooCode = {
				"" + TYPE_INT + (char)1 + LINE_BREAK,
				"" + variable0Name + "3" + LINE_BREAK,
				"" + TYPE_ARRAY_INT + (char)2 + LINE_BREAK,
				"" + array0Name + ((char)10) + LINE_BREAK,
				"" + array1Name + ((char)15) + LINE_BREAK,
				"" + array0Name + variable0Name + ADD + "10" + LINE_BREAK,
				"" + array1Name + ((char)(2 + ARRAY_INDICES_START)) + EQUALS + "30" + LINE_BREAK,
				"" + array0Name + variable0Name + ADD + array1Name + ((char)(2 + ARRAY_INDICES_START)) + LINE_BREAK,
				"" + array1Name + ((char)(2 + ARRAY_INDICES_START)) + DIVIDE + "2" + LINE_BREAK,
				"" + array1Name + ((char)(2 + ARRAY_INDICES_START)) + MULTIPLY + "2" + LINE_BREAK,
				"" + array1Name + ((char)(2 + ARRAY_INDICES_START)) + SUBTRACT + "10" + LINE_BREAK,
		};
		jooInterpreter.searchInts(rawJooCode[0].toCharArray(), (short) 0);
		jooInterpreter.searchInts(rawJooCode[1].toCharArray(), (short) 0);
		jooInterpreter.searchIntArrays(rawJooCode[2].toCharArray(), (short) 0);
		jooInterpreter.searchIntArrays(rawJooCode[3].toCharArray(), (short) 0);
		jooInterpreter.searchIntArrays(rawJooCode[4].toCharArray(), (short) 0);
		jooInterpreter.interpretIntArrayOperation(rawJooCode[5].toCharArray(), (short) 0);
		jooInterpreter.interpretIntArrayOperation(rawJooCode[6].toCharArray(), (short) 0);
		jooInterpreter.interpretIntArrayOperation(rawJooCode[7].toCharArray(), (short) 0);
		jooInterpreter.interpretIntArrayOperation(rawJooCode[8].toCharArray(), (short) 0);
		jooInterpreter.interpretIntArrayOperation(rawJooCode[9].toCharArray(), (short) 0);
		jooInterpreter.interpretIntArrayOperation(rawJooCode[10].toCharArray(), (short) 0);
		byte[] arraySizes = jooInterpreter.getArraySizes();
		short[] arrayValues = jooInterpreter.getVariableValues();
		assertEquals(arraySizes[0], 10);
		assertEquals(arraySizes[1], 15);
		assertEquals(arrayValues[3 + 32], 40);
		assertEquals(arrayValues[12 + 32], 20);
	}
	
	@Test
	public void interpretIntArrayComparisonTest() throws Exception {
		JooVirtualMachine jooInterpreter = new JooVirtualMachine();
		char array0Name = ARRAYS_START + (short) 0;
		char array1Name = ARRAYS_START + 1;
		char variable0Name = VARIABLES_START + (short) 0;
		String[] rawJooCode = {
				"" + TYPE_INT + (char)1 + LINE_BREAK,
				"" + variable0Name + "3" + LINE_BREAK,
				"" + TYPE_ARRAY_INT + (char)2 + LINE_BREAK,
				"" + array0Name + ((char)10) + LINE_BREAK,
				"" + array1Name + ((char)15) + LINE_BREAK,
				"" + array0Name + variable0Name + ADD + "10" + LINE_BREAK,
				"" + array1Name + ((char)(2 + ARRAY_INDICES_START)) + EQUALS + "30" + LINE_BREAK,
				"" + array0Name + variable0Name + SMALLER + array1Name + ((char)(2 + ARRAY_INDICES_START)) + LINE_BREAK,
				"" + array1Name + ((char)(2 + ARRAY_INDICES_START)) + SMALLER + EQUALS + "2" + LINE_BREAK,
				"" + array1Name + ((char)(2 + ARRAY_INDICES_START)) + BIGGER + EQUALS + "2" + LINE_BREAK,
				"" + array1Name + ((char)(2 + ARRAY_INDICES_START)) + BIGGER + "2" + LINE_BREAK,
				"" + array1Name + ((char)(2 + ARRAY_INDICES_START)) + EQUALS + "2" + LINE_BREAK,
				"" + array1Name + ((char)(2 + ARRAY_INDICES_START)) + NOT_EQUALS + "2" + LINE_BREAK,
		};
		jooInterpreter.searchInts(rawJooCode[0].toCharArray(), (short) 0);
		jooInterpreter.searchInts(rawJooCode[1].toCharArray(), (short) 0);
		jooInterpreter.searchIntArrays(rawJooCode[2].toCharArray(), (short) 0);
		jooInterpreter.searchIntArrays(rawJooCode[3].toCharArray(), (short) 0);
		jooInterpreter.searchIntArrays(rawJooCode[4].toCharArray(), (short) 0);
		jooInterpreter.interpretIntArrayOperation(rawJooCode[5].toCharArray(), (short) 0);
		jooInterpreter.interpretIntArrayOperation(rawJooCode[6].toCharArray(), (short) 0);
		assertEquals(jooInterpreter.interpretIntArrayComparison(rawJooCode[7].toCharArray(), (short) 0), true);
		assertEquals(jooInterpreter.interpretIntArrayComparison(rawJooCode[8].toCharArray(), (short) 0), false);
		assertEquals(jooInterpreter.interpretIntArrayComparison(rawJooCode[9].toCharArray(), (short) 0), true);
		assertEquals(jooInterpreter.interpretIntArrayComparison(rawJooCode[10].toCharArray(), (short) 0), true);
		assertEquals(jooInterpreter.interpretIntArrayComparison(rawJooCode[11].toCharArray(), (short) 0), false);
		assertEquals(jooInterpreter.interpretIntArrayComparison(rawJooCode[12].toCharArray(), (short) 0), true);
		byte[] arraySizes = jooInterpreter.getArraySizes();
		short[] arrayValues = jooInterpreter.getVariableValues();
		assertEquals(arraySizes[0], 10);
		assertEquals(arraySizes[1], 15);
		assertEquals(arrayValues[3 + 32], 10);
		assertEquals(arrayValues[12 + 32], 30);
	}
}
