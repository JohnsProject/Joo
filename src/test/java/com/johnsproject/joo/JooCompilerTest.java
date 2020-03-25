package com.johnsproject.joo;

import static org.junit.Assert.assertEquals;
import static com.johnsproject.joo.JooCompiler.*;

import java.util.HashMap;

import org.junit.Test;

public class JooCompilerTest {

	@Test
	public void compileTest() throws Exception {
		JooCompiler jooCompiler = new JooCompiler();
		String jooCode = new String (
				TYPE_INT + "[10]test1" + LINE_BREAK +
				TYPE_INT + "test2" + OPERATOR_SET_EQUALS + "100" + LINE_BREAK +
				TYPE_FIXED + "[15]fTest1" + LINE_BREAK +
				TYPE_FIXED + "fTest2" + OPERATOR_SET_EQUALS + "1.20" + LINE_BREAK +
				TYPE_BOOL + "[20]bTest1" + LINE_BREAK +
				TYPE_BOOL + "bTest2" + OPERATOR_SET_EQUALS + "true" + LINE_BREAK +
				TYPE_CHAR + "[25]cTest1" + LINE_BREAK +
				TYPE_CHAR + "cTest2" + OPERATOR_SET_EQUALS + "A" + LINE_BREAK +
				KEYWORD_FUNCTION + "Start" + LINE_BREAK +
				KEYWORD_FUNCTION_CALL + "Test" + KEYWORD_PARAMETER + "test1[test3]" + LINE_BREAK +
				KEYWORD_FUNCTION_END + LINE_BREAK +
				TYPE_INT + "test3" + OPERATOR_SET_EQUALS + "10" + LINE_BREAK +
				KEYWORD_FUNCTION + "Test" + KEYWORD_PARAMETER + "param1" + LINE_BREAK +
				"test3" + OPERATOR_SET_EQUALS + "20" + LINE_BREAK +
				"param1" + OPERATOR_ADD + "10" + LINE_BREAK +
				"test2" + OPERATOR_MULTIPLY + "15" + LINE_BREAK +
				KEYWORD_IF + "param1" + COMPARATOR_SMALLER + "test3" + LINE_BREAK +
				KEYWORD_IF_END + LINE_BREAK +
				KEYWORD_FUNCTION_CALL + "Print" + KEYWORD_PARAMETER + "param1" + LINE_BREAK +
				KEYWORD_FUNCTION_CALL + "Print" + KEYWORD_PARAMETER + "param1" + KEYWORD_PARAMETER + "test2" + LINE_BREAK +
				KEYWORD_FUNCTION_END + LINE_BREAK
		);
		String jooNF = jooCompiler.loadNativeFunctions("");
		jooCompiler.setNativeFunctions(jooCompiler.parseNativeFunctions(jooNF));
		char test1Name = JooVirtualMachine.ARRAYS_START + 0;
		char test2Name = JooVirtualMachine.VARIABLES_START + 0;
		char fTest1Name = JooVirtualMachine.ARRAYS_START + 1;
		char fTest2Name = JooVirtualMachine.VARIABLES_START + 1;
		char bTest1Name = JooVirtualMachine.ARRAYS_START + 2;
		char bTest2Name = JooVirtualMachine.VARIABLES_START + 2;
		char cTest1Name = JooVirtualMachine.ARRAYS_START + 3;
		char cTest2Name = JooVirtualMachine.VARIABLES_START + 3;
		char test3Name = JooVirtualMachine.VARIABLES_START + 4;
		char parameter1Name = JooVirtualMachine.PARAMETERS_START + 0;
		char startFuncionName = JooVirtualMachine.FUNCTIONS_START + 0;
		char testFunctionName = JooVirtualMachine.FUNCTIONS_START + 1;
		String[] compiledJooCode = jooCompiler.compile(jooCode).split("" + (char)JooVirtualMachine.LINE_BREAK);
		assertEquals(compiledJooCode[0], "" + JooVirtualMachine.TYPE_INT + (char)2);
		assertEquals(compiledJooCode[1], "" + test2Name + "100"); // this way because HashMap sorts it somehow
		assertEquals(compiledJooCode[2], "" + test3Name + "10");
		assertEquals(compiledJooCode[3], "" + JooVirtualMachine.TYPE_FIXED + (char)1);
		assertEquals(compiledJooCode[4], "" + fTest2Name + "1.20");
		assertEquals(compiledJooCode[5], "" + JooVirtualMachine.TYPE_BOOL + (char)1);
		assertEquals(compiledJooCode[6], "" + bTest2Name + "1");
		assertEquals(compiledJooCode[7], "" + JooVirtualMachine.TYPE_CHAR + (char)1);
		assertEquals(compiledJooCode[8], "" + cTest2Name + "A");
		assertEquals(compiledJooCode[9], "" + JooVirtualMachine.TYPE_ARRAY_INT + (char)1);
		assertEquals(compiledJooCode[10], "" + test1Name + ((char)10));
		assertEquals(compiledJooCode[11], "" + JooVirtualMachine.TYPE_ARRAY_FIXED + (char)1);
		assertEquals(compiledJooCode[12], "" + fTest1Name + ((char)15));
		assertEquals(compiledJooCode[13], "" + JooVirtualMachine.TYPE_ARRAY_BOOL + (char)1);
		assertEquals(compiledJooCode[14], "" + bTest1Name + ((char)20));
		assertEquals(compiledJooCode[15], "" + JooVirtualMachine.TYPE_ARRAY_CHAR + (char)1);
		assertEquals(compiledJooCode[16], "" + cTest1Name + ((char)25));
		assertEquals(compiledJooCode[17], "" + JooVirtualMachine.TYPE_FUNCTION + (char)2);
		assertEquals(compiledJooCode[18], "" + JooVirtualMachine.KEYWORD_FUNCTION + startFuncionName);
		assertEquals(compiledJooCode[19], "" + JooVirtualMachine.KEYWORD_FUNCTION_CALL + testFunctionName + test1Name + test3Name);
		assertEquals(compiledJooCode[20], "" + JooVirtualMachine.KEYWORD_FUNCTION + testFunctionName + parameter1Name);
		assertEquals(compiledJooCode[21], "" + test3Name + JooVirtualMachine.EQUALS + "20");
		assertEquals(compiledJooCode[22], "" + parameter1Name + JooVirtualMachine.ADD + "10");
		assertEquals(compiledJooCode[23], "" + test2Name + JooVirtualMachine.MULTIPLY + "15");
		assertEquals(compiledJooCode[24], "" + JooVirtualMachine.KEYWORD_IF + parameter1Name + JooVirtualMachine.SMALLER + test3Name);
		assertEquals(compiledJooCode[25], "" + JooVirtualMachine.KEYWORD_IF);
		assertEquals(compiledJooCode[26], "" + JooVirtualMachine.KEYWORD_FUNCTION_CALL + JooVirtualMachine.FUNCTION_PRINT + parameter1Name);
		assertEquals(compiledJooCode[27], "" + JooVirtualMachine.KEYWORD_FUNCTION_CALL + JooVirtualMachine.FUNCTION_PRINT
											+ parameter1Name + JooVirtualMachine.KEYWORD_PARAMETER + test2Name);
		assertEquals(compiledJooCode[28], "" + JooVirtualMachine.KEYWORD_FUNCTION);
	}
	
	@Test
	public void parseNativeFunctionsTest() throws Exception {
		JooCompiler jooCompiler = new JooCompiler();
		String jooNF = jooCompiler.loadNativeFunctions("");
		String[] nativeFunctions = jooCompiler.parseNativeFunctions(jooNF);
		assertEquals(nativeFunctions[0], "Print");
		assertEquals(nativeFunctions[1], "PrintLine");
		assertEquals(nativeFunctions[2], "Copy");
		assertEquals(nativeFunctions[3], "SetDelay");
		assertEquals(nativeFunctions[4], "GetSecond");
		assertEquals(nativeFunctions[5], "String");
		assertEquals(nativeFunctions[6], "Invert");
		assertEquals(nativeFunctions[7], "Positive");
		assertEquals(nativeFunctions[8], "Negative");
	}
	
	@Test
	public void replaceVariablesTest() throws Exception {
		HashMap<String, Variable> intVariables = new HashMap<String, Variable>();
		HashMap<String, Variable> fixedVariables = new HashMap<String, Variable>();
		HashMap<String, Variable> boolVariables = new HashMap<String, Variable>();
		HashMap<String, Variable> charVariables = new HashMap<String, Variable>();
		JooCompiler jooCompiler = new JooCompiler();
		String[] jooCode = new String[] {
				TYPE_INT + "test1",
				TYPE_INT + "test2" + OPERATOR_SET_EQUALS + "100",
				TYPE_FIXED + "fixedTest" + OPERATOR_SET_EQUALS + "1.15",
				TYPE_BOOL + "boolTest" + OPERATOR_SET_EQUALS + "true",
				TYPE_CHAR + "charTest" + OPERATOR_SET_EQUALS + "A",
				"test3" + OPERATOR_SET_EQUALS + "20",
				"test1" + OPERATOR_ADD + "10",
				"test2" + OPERATOR_MULTIPLY + "15",
				TYPE_INT + "test3" + OPERATOR_SET_EQUALS + "10",
				KEYWORD_FUNCTION_CALL + "Print" + KEYWORD_PARAMETER + "test1",
				KEYWORD_FUNCTION_CALL + "Print" + KEYWORD_PARAMETER + "test1" + KEYWORD_PARAMETER + "test2",
				KEYWORD_IF + "test1" + COMPARATOR_SMALLER_EQUALS + "test3",
		};
		char test1Name = JooVirtualMachine.VARIABLES_START + 0;
		char test2Name = JooVirtualMachine.VARIABLES_START + 1;
		char fixedTestName = JooVirtualMachine.VARIABLES_START + 2;
		char boolTestName = JooVirtualMachine.VARIABLES_START + 3;
		char charTestName = JooVirtualMachine.VARIABLES_START + 4;
		char test3Name = JooVirtualMachine.VARIABLES_START + 5;
		for (int i = 0; i < jooCode.length; i++) {
			jooCompiler.searchVariables(jooCode, i, TYPE_INT, intVariables);
			jooCompiler.searchVariables(jooCode, i, TYPE_FIXED, fixedVariables);
			jooCompiler.searchVariables(jooCode, i, TYPE_BOOL, boolVariables);
			jooCompiler.searchVariables(jooCode, i, TYPE_CHAR, charVariables);
		}
		for (int i = 0; i < jooCode.length; i++) {
			jooCompiler.replaceVariables(jooCode, i, TYPE_INT, intVariables);
			jooCompiler.replaceVariables(jooCode, i, TYPE_FIXED, fixedVariables);
			jooCompiler.replaceVariables(jooCode, i, TYPE_BOOL, boolVariables);
			jooCompiler.replaceVariables(jooCode, i, TYPE_CHAR, charVariables);
		}
		assertEquals(intVariables.get("test1").name, "" + test1Name);
		assertEquals(intVariables.get("test1").value, "");
		assertEquals(intVariables.get("test2").name, "" + test2Name);
		assertEquals(intVariables.get("test2").value, "100");
		assertEquals(fixedVariables.get("fixedTest").name, "" + fixedTestName);
		assertEquals(fixedVariables.get("fixedTest").value, "1.15");
		assertEquals(boolVariables.get("boolTest").name, "" + boolTestName);
		assertEquals(boolVariables.get("boolTest").value, "true");
		assertEquals(charVariables.get("charTest").name, "" + charTestName);
		assertEquals(charVariables.get("charTest").value, "A");
		assertEquals(jooCode[5], "" + test3Name + OPERATOR_SET_EQUALS + "20");
		assertEquals(jooCode[6], "" + test1Name + OPERATOR_ADD + "10");
		assertEquals(jooCode[7], "" + test2Name + OPERATOR_MULTIPLY + "15");
		assertEquals(intVariables.get("test3").name, "" + test3Name);
		assertEquals(intVariables.get("test3").value, "10");
		assertEquals(jooCode[9], "" + KEYWORD_FUNCTION_CALL + "Print" + KEYWORD_PARAMETER + test1Name);
		assertEquals(jooCode[10], "" + KEYWORD_FUNCTION_CALL + "Print" + KEYWORD_PARAMETER
				+ test1Name + KEYWORD_PARAMETER + test2Name);
		assertEquals(jooCode[11], "" + KEYWORD_IF + test1Name + COMPARATOR_SMALLER_EQUALS + test3Name);
	}
	
	@Test
	public void replaceArraysTest() throws Exception {
		HashMap<String, Variable> intArrayVariables = new HashMap<String, Variable>();
		HashMap<String, Variable> fixedArrayVariables = new HashMap<String, Variable>();
		HashMap<String, Variable> boolArrayVariables = new HashMap<String, Variable>();
		HashMap<String, Variable> charArrayVariables = new HashMap<String, Variable>();
		JooCompiler jooCompiler = new JooCompiler();
		String[] jooCode = new String[] {
				TYPE_INT + "[32]test1",
				TYPE_INT + "[10]test2",
				TYPE_FIXED + "[15]fixedTest",
				TYPE_BOOL + "[14]boolTest",
				TYPE_CHAR + "[18]charTest",
				"test3" + OPERATOR_ADD + "test1[15]",
				"test3" + OPERATOR_MULTIPLY + "test2[5]",
				TYPE_INT + "test3" + OPERATOR_SET_EQUALS + "10",
				KEYWORD_FUNCTION_CALL + "Print" + KEYWORD_PARAMETER + "test1[4]",
				KEYWORD_FUNCTION_CALL + "Print" + KEYWORD_PARAMETER+ "test1[3]" + KEYWORD_PARAMETER + "test2[5]",
				KEYWORD_IF + "test1[2]" + COMPARATOR_SMALLER_EQUALS + "test3",
		};
		char test1Name = JooVirtualMachine.ARRAYS_START + 0;
		char test2Name = JooVirtualMachine.ARRAYS_START + 1;
		char fixedTestName = JooVirtualMachine.ARRAYS_START + 2;
		char boolTestName = JooVirtualMachine.ARRAYS_START + 3;
		char charTestName = JooVirtualMachine.ARRAYS_START + 4;
		for (int i = 0; i < jooCode.length; i++) {
			jooCompiler.searchVariables(jooCode, i, TYPE_ARRAY_INT, intArrayVariables);
			jooCompiler.searchVariables(jooCode, i, TYPE_ARRAY_FIXED, fixedArrayVariables);
			jooCompiler.searchVariables(jooCode, i, TYPE_ARRAY_BOOL, boolArrayVariables);
			jooCompiler.searchVariables(jooCode, i, TYPE_ARRAY_CHAR, charArrayVariables);
		}
		for (int i = 0; i < jooCode.length; i++) {
			jooCompiler.replaceVariables(jooCode, i, TYPE_ARRAY_INT, intArrayVariables);
			jooCompiler.replaceVariables(jooCode, i, TYPE_ARRAY_FIXED, fixedArrayVariables);
			jooCompiler.replaceVariables(jooCode, i, TYPE_ARRAY_BOOL, boolArrayVariables);
			jooCompiler.replaceVariables(jooCode, i, TYPE_ARRAY_CHAR, charArrayVariables);
		}
		jooCompiler.replaceArrayKeywordsWithNumberIndex(jooCode);
		assertEquals(intArrayVariables.get("test1").name, "" + test1Name);
		assertEquals(intArrayVariables.get("test1").value, "32");
		assertEquals(intArrayVariables.get("test2").name, "" + test2Name);
		assertEquals(intArrayVariables.get("test2").value, "10");
		assertEquals(fixedArrayVariables.get("fixedTest").name, "" + fixedTestName);
		assertEquals(fixedArrayVariables.get("fixedTest").value, "15");
		assertEquals(boolArrayVariables.get("boolTest").name, "" + boolTestName);
		assertEquals(boolArrayVariables.get("boolTest").value, "14");
		assertEquals(charArrayVariables.get("charTest").name, "" + charTestName);
		assertEquals(charArrayVariables.get("charTest").value, "18");
		assertEquals(jooCode[5], "" + "test3" + OPERATOR_ADD + test1Name + ((char)(15 + JooVirtualMachine.ARRAY_INDICES_START)));
		assertEquals(jooCode[6], "" + "test3" + OPERATOR_MULTIPLY + test2Name + ((char)(5 + JooVirtualMachine.ARRAY_INDICES_START)));
		assertEquals(jooCode[8], "" + KEYWORD_FUNCTION_CALL + "Print" + KEYWORD_PARAMETER
				+ test1Name + ((char)(4 + JooVirtualMachine.ARRAY_INDICES_START)));
		assertEquals(jooCode[9], "" + KEYWORD_FUNCTION_CALL + "Print" + KEYWORD_PARAMETER + test1Name
				+ ((char)(3 + JooVirtualMachine.ARRAY_INDICES_START)) + KEYWORD_PARAMETER + test2Name + ((char)(5 + JooVirtualMachine.ARRAY_INDICES_START)));
		assertEquals(jooCode[10], "" + KEYWORD_IF + test1Name + ((char)(2 + JooVirtualMachine.ARRAY_INDICES_START)) + COMPARATOR_SMALLER_EQUALS + "test3");
	}
	
	@Test
	public void replaceParametersTest() throws Exception {
		HashMap<String, Variable> parametersNames = new HashMap<String, Variable>();
		JooCompiler jooCompiler = new JooCompiler();
		String[] jooCode = new String[] {
				KEYWORD_FUNCTION + "Start",
				KEYWORD_FUNCTION_CALL + "Test" + KEYWORD_PARAMETER + "test1" + KEYWORD_PARAMETER + "test2",
				KEYWORD_FUNCTION_END,
				KEYWORD_FUNCTION + "Test" + KEYWORD_PARAMETER + "param1" + KEYWORD_PARAMETER + "param2",
				KEYWORD_FUNCTION_CALL + "Test" + KEYWORD_PARAMETER + "param1" + KEYWORD_PARAMETER + "param2",
				"param1" + OPERATOR_SET_EQUALS + "param2",
				KEYWORD_IF + "param1" + COMPARATOR_EQUALS + "test2",
				KEYWORD_IF_END,
				KEYWORD_FUNCTION_END,
		};
		char test1Name = JooVirtualMachine.PARAMETERS_START + 0;
		char test2Name = JooVirtualMachine.PARAMETERS_START + 1;
		for (int i = 0; i < jooCode.length; i++) {
			jooCompiler.searchParameters(jooCode, i, parametersNames);
		}
		for (int i = 0; i < jooCode.length; i++) {
			jooCompiler.replaceVariables(jooCode, i, TYPE_PARAMETER, parametersNames);
		}
		assertEquals(jooCode[0], "" + KEYWORD_FUNCTION + "Start");
		assertEquals(jooCode[1], "" + KEYWORD_FUNCTION_CALL + "Test" + KEYWORD_PARAMETER +  "test1"  + KEYWORD_PARAMETER +  "test2");
		assertEquals(jooCode[2], "" + KEYWORD_FUNCTION_END);
		assertEquals(jooCode[3], "" + KEYWORD_FUNCTION + "Test" + KEYWORD_PARAMETER + test1Name + KEYWORD_PARAMETER + test2Name);
		assertEquals(jooCode[4], "" + KEYWORD_FUNCTION_CALL + "Test" + KEYWORD_PARAMETER +  test1Name  + KEYWORD_PARAMETER +  test2Name);
		assertEquals(jooCode[5], "" + test1Name + OPERATOR_SET_EQUALS + test2Name);
		assertEquals(jooCode[6], "" + KEYWORD_IF + test1Name + COMPARATOR_EQUALS + "test2");
		assertEquals(jooCode[7], "" + KEYWORD_IF_END);
		assertEquals(jooCode[8], "" + KEYWORD_FUNCTION_END);
	}
	
	@Test
	public void replaceFunctionsTest() throws Exception {
		HashMap<String, String> functionNames = new HashMap<String, String>();
		JooCompiler jooCompiler = new JooCompiler();
		String[] jooCode = new String[] {
				KEYWORD_FUNCTION + "Start",
				KEYWORD_FUNCTION_CALL + "Test",
				KEYWORD_FUNCTION_END,
				KEYWORD_FUNCTION + "Test" + KEYWORD_PARAMETER + "test1" + KEYWORD_PARAMETER + "test2",
				KEYWORD_FUNCTION_CALL + "Print" + KEYWORD_PARAMETER + "test1",
				KEYWORD_FUNCTION_CALL + "Print" + KEYWORD_PARAMETER + "test1" + KEYWORD_PARAMETER + "test2",
				KEYWORD_FUNCTION_END,
		};
		String jooNF = jooCompiler.loadNativeFunctions("");
		jooCompiler.setNativeFunctions(jooCompiler.parseNativeFunctions(jooNF));
		char startFuncionName = JooVirtualMachine.FUNCTIONS_START + 0;
		char testFunctionName = JooVirtualMachine.FUNCTIONS_START + 1;
		for (int i = 0; i < jooCode.length; i++) {
			jooCompiler.searchFunctions(jooCode, i, functionNames);
		}
		for (int i = 0; i < jooCode.length; i++) {
			jooCompiler.replaceFunctions(jooCode, i, functionNames);
		}
		assertEquals(jooCode[0], "" + JooVirtualMachine.KEYWORD_FUNCTION + startFuncionName);
		assertEquals(jooCode[1], "" + JooVirtualMachine.KEYWORD_FUNCTION_CALL + testFunctionName);
		assertEquals(jooCode[2], "" + JooVirtualMachine.KEYWORD_FUNCTION);
		assertEquals(jooCode[3], "" + JooVirtualMachine.KEYWORD_FUNCTION + testFunctionName 
				+ "test1" + JooVirtualMachine.KEYWORD_PARAMETER + "test2");
		assertEquals(jooCode[4], "" + JooVirtualMachine.KEYWORD_FUNCTION_CALL + JooVirtualMachine.FUNCTION_PRINT + "test1");
		assertEquals(jooCode[5], "" + JooVirtualMachine.KEYWORD_FUNCTION_CALL + JooVirtualMachine.FUNCTION_PRINT
				+ "test1" + JooVirtualMachine.KEYWORD_PARAMETER + "test2");
		assertEquals(jooCode[6], "" + JooVirtualMachine.KEYWORD_FUNCTION);
	}
	
	@Test
	public void replaceIfsTest() throws Exception {
		JooCompiler jooCompiler = new JooCompiler();
		String[] jooCode = new String[] {
				KEYWORD_IF + COMPARATOR_EQUALS,
				KEYWORD_IF + COMPARATOR_NOT_EQUALS,
				KEYWORD_IF + COMPARATOR_BIGGER,
				KEYWORD_IF + COMPARATOR_SMALLER,
				KEYWORD_IF + COMPARATOR_BIGGER_EQUALS,
				KEYWORD_IF + COMPARATOR_SMALLER_EQUALS,
				KEYWORD_IF_END
		};
		for (int i = 0; i < jooCode.length; i++) {
			jooCompiler.replaceIfs(jooCode, i);
		}
		assertEquals(jooCode[0], "" + JooVirtualMachine.KEYWORD_IF + JooVirtualMachine.EQUALS);
		assertEquals(jooCode[1], "" + JooVirtualMachine.KEYWORD_IF + JooVirtualMachine.NOT_EQUALS);
		assertEquals(jooCode[2], "" + JooVirtualMachine.KEYWORD_IF + JooVirtualMachine.BIGGER);
		assertEquals(jooCode[3], "" + JooVirtualMachine.KEYWORD_IF + JooVirtualMachine.SMALLER);
		assertEquals(jooCode[4], "" + JooVirtualMachine.KEYWORD_IF + JooVirtualMachine.BIGGER + JooVirtualMachine.EQUALS);
		assertEquals(jooCode[5], "" + JooVirtualMachine.KEYWORD_IF + JooVirtualMachine.SMALLER + JooVirtualMachine.EQUALS);
		assertEquals(jooCode[6], "" + JooVirtualMachine.KEYWORD_IF);
	}
	
	@Test
	public void replaceOperatorsTest() throws Exception {
		JooCompiler jooCompiler = new JooCompiler();
		String[] jooCode = new String[] {
			OPERATOR_ADD,
			OPERATOR_SUBTRACT,
			OPERATOR_MULTIPLY,
			OPERATOR_DIVIDE,
		};
		for (int i = 0; i < jooCode.length; i++) {
			jooCompiler.replaceOperators(jooCode, i);
		}
		assertEquals(jooCode[0], "" + JooVirtualMachine.ADD);
		assertEquals(jooCode[1], "" + JooVirtualMachine.SUBTRACT);
		assertEquals(jooCode[2], "" + JooVirtualMachine.MULTIPLY);
		assertEquals(jooCode[3], "" + JooVirtualMachine.DIVIDE);
	}
	
}
