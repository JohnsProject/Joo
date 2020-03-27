package com.johnsproject.joo;

import java.util.HashMap;

import org.junit.Test;

import com.johnsproject.joo.util.FileUtil;

import static com.johnsproject.joo.JppCompiler.*;
import static org.junit.Assert.assertEquals;

public class JppCompilerTest {

	@Test
	public void compileTest() throws Exception {
		final String jooCode = FileUtil.readResource("TestCode.joo");
		final JppCompiler jppCompiler = new JppCompiler();
		final String compiledJooCode = jppCompiler.compile(jooCode);
		final String[] jooLines = compiledJooCode.split("" + JppVirtualMachine.LINE_BREAK);
		
		final char intTest0Name = 1;
		final char intTest1Name = 2;
		final char fixedTest0Name = 3;
		final char fixedTest1Name = 4;
		final char boolTest0Name = 5;
		final char boolTest1Name = 6;
		final char charTest0Name = 7;
		final char charTest1Name = 8;
		
		final char intTestName = 9;
		final char fixedTestName = 10;
		final char boolTestName = 11;
		final char charTestName = 12;
		
		final char startName = 12 + JppVirtualMachine.COMPONENTS_START;
		final char functionTestName = 13 + JppVirtualMachine.COMPONENTS_START;
		
		final char paramTest0Name = 0 + JppVirtualMachine.PARAMETERS_START;
		final char paramTest1Name = 1 + JppVirtualMachine.PARAMETERS_START;
		
		assertEquals(jooLines[0], "" + JppVirtualMachine.TYPE_INT + (char)2);
		assertEquals(jooLines[1], "" + intTest0Name);
		assertEquals(jooLines[2], "" + intTest1Name + toVirtualMachineNumber("10"));
		assertEquals(jooLines[3], "" + JppVirtualMachine.TYPE_FIXED + (char)2);
		assertEquals(jooLines[4], "" + fixedTest0Name);
		assertEquals(jooLines[5], "" + fixedTest1Name + toVirtualMachineNumber("" + Math.round(10.15f * 255)));
		assertEquals(jooLines[6], "" + JppVirtualMachine.TYPE_BOOL + (char)2);
		assertEquals(jooLines[7], "" + boolTest1Name + toVirtualMachineNumber("1"));
		assertEquals(jooLines[8], "" + boolTest0Name);
		assertEquals(jooLines[9], "" + JppVirtualMachine.TYPE_CHAR + (char)2);
		assertEquals(jooLines[10], "" + charTest1Name + 'A');
		assertEquals(jooLines[11], "" + charTest0Name);
		assertEquals(jooLines[12], "" + JppVirtualMachine.TYPE_ARRAY_INT + (char)1);
		assertEquals(jooLines[13], "" + intTestName + (char)10);
		assertEquals(jooLines[14], "" + JppVirtualMachine.TYPE_ARRAY_FIXED + (char)1);
		assertEquals(jooLines[15], "" + fixedTestName + (char)5);
		assertEquals(jooLines[16], "" + JppVirtualMachine.TYPE_ARRAY_BOOL + (char)1);
		assertEquals(jooLines[17], "" + boolTestName + (char)15);
		assertEquals(jooLines[18], "" + JppVirtualMachine.TYPE_ARRAY_CHAR + (char)1);
		assertEquals(jooLines[19], "" + charTestName + (char)13);
		assertEquals(jooLines[20], "" + JppVirtualMachine.TYPE_FUNCTION + (char)2);
		assertEquals(jooLines[21], "" + JppVirtualMachine.KEYWORD_FUNCTION + startName);
		assertEquals(jooLines[22], "" + JppVirtualMachine.KEYWORD_FUNCTION_CALL + functionTestName
											+ JppVirtualMachine.KEYWORD_PARAMETER + intTest0Name 
											+ JppVirtualMachine.KEYWORD_PARAMETER + intTestName);
		assertEquals(jooLines[23], "" + intTest0Name + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("100"));
		assertEquals(jooLines[24], "" + intTest0Name + JppVirtualMachine.OPERATOR_SUBTRACT + intTest1Name);
		assertEquals(jooLines[25], "" + fixedTest0Name + JppVirtualMachine.OPERATOR_MULTIPLY + toVirtualMachineNumber("" + Math.round(25.54f * 255)));
		assertEquals(jooLines[26], "" + fixedTest0Name + JppVirtualMachine.OPERATOR_DIVIDE + fixedTest1Name);
		assertEquals(jooLines[27], "" + boolTest0Name + JppVirtualMachine.OPERATOR_SET_EQUALS + toVirtualMachineNumber("0"));
		assertEquals(jooLines[28], "" + boolTest1Name + JppVirtualMachine.OPERATOR_SET_EQUALS + toVirtualMachineNumber("1"));
		assertEquals(jooLines[29], "" + charTest0Name + JppVirtualMachine.OPERATOR_SET_EQUALS + JppVirtualMachine.TYPE_CHAR + 'A');
		assertEquals(jooLines[30], "" + charTest1Name + JppVirtualMachine.OPERATOR_SET_EQUALS + JppVirtualMachine.TYPE_CHAR + 'B');
		assertEquals(jooLines[31], "" + JppVirtualMachine.KEYWORD_IF + intTest0Name + JppVirtualMachine.COMPARATOR_EQUALS + toVirtualMachineNumber("100"));
		assertEquals(jooLines[32], "" + intTestName + (char)3 + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("100"));
		assertEquals(jooLines[33], "" + JppVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[34], "" + intTestName + (char)4 + JppVirtualMachine.OPERATOR_SUBTRACT + intTest0Name);
		assertEquals(jooLines[35], "" + fixedTestName + (char)8 + JppVirtualMachine.OPERATOR_MULTIPLY + intTestName + (char)10);
		assertEquals(jooLines[36], "" + JppVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[37], "" + JppVirtualMachine.KEYWORD_IF + intTest0Name + JppVirtualMachine.COMPARATOR_BIGGER + intTest1Name);
		assertEquals(jooLines[38], "" + JppVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[39], "" + JppVirtualMachine.KEYWORD_IF + intTestName + (char)3 + JppVirtualMachine.COMPARATOR_SMALLER + intTest1Name);
		assertEquals(jooLines[40], "" + JppVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[41], "" + JppVirtualMachine.KEYWORD_IF + intTestName + (char)3 + JppVirtualMachine.COMPARATOR_NOT_EQUALS + intTestName + (char)10);
		assertEquals(jooLines[42], "" + JppVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[43], "" + JppVirtualMachine.KEYWORD_FUNCTION + functionTestName);
		assertEquals(jooLines[44], "" + paramTest0Name + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("100"));
		assertEquals(jooLines[45], "" + paramTest1Name + (char)6 + JppVirtualMachine.OPERATOR_SET_EQUALS + paramTest0Name);
	}
	
	@Test
	public void parseFunctionsTest() throws Exception {
		final String jooCode = FileUtil.readResource("TestCode.joo");
		final JppCompiler jppCompiler = new JppCompiler();
		final String[] jooLines = jppCompiler.getJooLines(jooCode);
		final HashMap<String, Function> functions = jppCompiler.parseFunctions(jooLines);
		
		final Function start = functions.get("Start");
		final Function functionTest = functions.get("FunctionTest");
		
		assert(start.getName() == 1);
		assert(functionTest.getName() == 2);
		
		assertEquals(start.getParameters()[0], null);
		assertEquals(functionTest.getParameters()[0], "_paramTest0");
		assertEquals(functionTest.getParameters()[1], "_paramTest1");
		
		int currentOperationIndex = 0;
		Operation currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_FUNCTION_CALL);
		assertEquals(currentOperation.getVariable1Name(), "FunctionTest");
		assertEquals(currentOperation.getVariable1ArrayIndex(), -1);
		assertEquals(currentOperation.getParameters()[0], "intTest0");
		assertEquals(currentOperation.getParameters()[1], "intTest");
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "intTest0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(), "100");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "intTest0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SUBTRACT);
		assertEquals(currentOperation.getVariable1Name(), "intTest1");
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "fixedTest0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_MULTIPLY);
		assertEquals(currentOperation.getValue(), "" + Math.round(25.54 * 255));
		assertEquals(currentOperation.getValueType(), TYPE_FIXED);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "fixedTest0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_DIVIDE);
		assertEquals(currentOperation.getVariable1Name(), "fixedTest1");
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "boolTest0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SET_EQUALS);
		assertEquals(currentOperation.getValue(), "0");
		assertEquals(currentOperation.getValueType(), TYPE_BOOL);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "boolTest1");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SET_EQUALS);
		assertEquals(currentOperation.getValue(), "1");
		assertEquals(currentOperation.getValueType(), TYPE_BOOL);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "charTest0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SET_EQUALS);
		assertEquals(currentOperation.getValue(), "A");
		assertEquals(currentOperation.getValueType(), TYPE_CHAR);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "charTest1");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SET_EQUALS);
		assertEquals(currentOperation.getValue(), "B");
		assertEquals(currentOperation.getValueType(), TYPE_CHAR);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.isCondition(), true);
		assertEquals(currentOperation.getVariable0Name(), "intTest0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.COMPARATOR_EQUALS);
		assertEquals(currentOperation.getValue(), "100");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "intTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), 2);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(), "100");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_ELSE);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "intTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), 3);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SUBTRACT);
		assertEquals(currentOperation.getVariable1Name(), "intTest0");
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "fixedTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), 7);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_MULTIPLY);
		assertEquals(currentOperation.getVariable1Name(), "intTest");
		assertEquals(currentOperation.getVariable1ArrayIndex(), 9);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_IF);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.isCondition(), true);
		assertEquals(currentOperation.getVariable0Name(), "intTest0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.COMPARATOR_BIGGER);
		assertEquals(currentOperation.getVariable1Name(), "intTest1");
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_IF);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.isCondition(), true);
		assertEquals(currentOperation.getVariable0Name(), "intTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), 2);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.COMPARATOR_SMALLER);
		assertEquals(currentOperation.getVariable1Name(), "intTest1");
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_IF);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.isCondition(), true);
		assertEquals(currentOperation.getVariable0Name(), "intTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), 2);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.COMPARATOR_NOT_EQUALS);
		assertEquals(currentOperation.getVariable1Name(), "intTest");
		assertEquals(currentOperation.getVariable1ArrayIndex(), 9);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_IF);
		
		currentOperationIndex = 0;
		currentOperation = functionTest.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "_paramTest0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(), "100");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = functionTest.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "_paramTest1");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SET_EQUALS);
		assertEquals(currentOperation.getVariable1Name(), "_paramTest0");
	}
	
	@Test
	public void parseVariablesTest() throws Exception {
		final String jooCode = FileUtil.readResource("TestCode.joo");
		final JppCompiler jppCompiler = new JppCompiler();
		final String[] jooLines = jppCompiler.getJooLines(jooCode);
		final HashMap<String, Variable>[] variables = jppCompiler.parseVariables(jooLines);
		
		assert(variables[0].get("intTest0").getName() == 1);
		assert(variables[0].get("intTest1").getName() == 2);
		assert(variables[1].get("fixedTest0").getName() == 3);
		assert(variables[1].get("fixedTest1").getName() == 4);
		assert(variables[2].get("boolTest0").getName() == 5);
		assert(variables[2].get("boolTest1").getName() == 6);
		assert(variables[3].get("charTest0").getName() == 7);
		assert(variables[3].get("charTest1").getName() == 8);
		assert(variables[4].get("intTest").getName() == 9);
		assert(variables[5].get("fixedTest").getName() == 10);
		assert(variables[6].get("boolTest").getName() == 11);
		assert(variables[7].get("charTest").getName() == 12);
		
		assertEquals(variables[0].get("intTest0").getValue(), "");
		assertEquals(variables[0].get("intTest1").getValue(), "10");
		assertEquals(variables[1].get("fixedTest0").getValue(), "");
		assertEquals(variables[1].get("fixedTest1").getValue(), "" + Math.round(10.15f * 255));
		assertEquals(variables[2].get("boolTest0").getValue(), "");
		assertEquals(variables[2].get("boolTest1").getValue(), "1");
		assertEquals(variables[3].get("charTest0").getValue(), "");
		assertEquals(variables[3].get("charTest1").getValue(), "A");		
		assertEquals(variables[4].get("intTest").getValue(), "" + (char)10);
		assertEquals(variables[5].get("fixedTest").getValue(), "" + (char)5);
		assertEquals(variables[6].get("boolTest").getValue(), "" + (char)15);
		assertEquals(variables[7].get("charTest").getValue(), "" + (char)13);
	}
	
	@Test
	public void writeVariablesAndFunctionsTest() throws Exception {
		final String jooCode = FileUtil.readResource("TestCode.joo");
		final JppCompiler jppCompiler = new JppCompiler();
		String[] jooLines = jppCompiler.getJooLines(jooCode);
		final HashMap<String, Variable>[] variables = jppCompiler.parseVariables(jooLines);
		final HashMap<String, Function> functions = jppCompiler.parseFunctions(jooLines);
		String compiledJooCode = "";
		compiledJooCode = jppCompiler.writeVariablesAndFunctions(compiledJooCode, variables, functions);
		jooLines = compiledJooCode.split("" + JppVirtualMachine.LINE_BREAK);
		
		final char intTest0Name = 1;
		final char intTest1Name = 2;
		final char fixedTest0Name = 3;
		final char fixedTest1Name = 4;
		final char boolTest0Name = 5;
		final char boolTest1Name = 6;
		final char charTest0Name = 7;
		final char charTest1Name = 8;
		
		final char intTestName = 9;
		final char fixedTestName = 10;
		final char boolTestName = 11;
		final char charTestName = 12;

		assertEquals(jooLines[0], "" + JppVirtualMachine.TYPE_INT + (char)2);
		assertEquals(jooLines[1], "" + intTest0Name);
		assertEquals(jooLines[2], "" + intTest1Name + toVirtualMachineNumber("10"));
		assertEquals(jooLines[3], "" + JppVirtualMachine.TYPE_FIXED + (char)2);
		assertEquals(jooLines[4], "" + fixedTest0Name);
		assertEquals(jooLines[5], "" + fixedTest1Name + toVirtualMachineNumber("" + Math.round(10.15f * 255)));
		assertEquals(jooLines[6], "" + JppVirtualMachine.TYPE_BOOL + (char)2);
		assertEquals(jooLines[7], "" + boolTest1Name + toVirtualMachineNumber("1"));
		assertEquals(jooLines[8], "" + boolTest0Name);
		assertEquals(jooLines[9], "" + JppVirtualMachine.TYPE_CHAR + (char)2);
		assertEquals(jooLines[10], "" + charTest1Name + 'A');
		assertEquals(jooLines[11], "" + charTest0Name);
		assertEquals(jooLines[12], "" + JppVirtualMachine.TYPE_ARRAY_INT + (char)1);
		assertEquals(jooLines[13], "" + intTestName + (char)10);
		assertEquals(jooLines[14], "" + JppVirtualMachine.TYPE_ARRAY_FIXED + (char)1);
		assertEquals(jooLines[15], "" + fixedTestName + (char)5);
		assertEquals(jooLines[16], "" + JppVirtualMachine.TYPE_ARRAY_BOOL + (char)1);
		assertEquals(jooLines[17], "" + boolTestName + (char)15);
		assertEquals(jooLines[18], "" + JppVirtualMachine.TYPE_ARRAY_CHAR + (char)1);
		assertEquals(jooLines[19], "" + charTestName + (char)13);
		assertEquals(jooLines[20], "" + JppVirtualMachine.TYPE_FUNCTION + (char)2);
	}
	
	@Test
	public void writeFunctionsAndOperationsTest() throws Exception {
		final String jooCode = FileUtil.readResource("TestCode.joo");
		final JppCompiler jppCompiler = new JppCompiler();
		String[] jooLines = jppCompiler.getJooLines(jooCode);
		final HashMap<String, Variable>[] variables = jppCompiler.parseVariables(jooLines);
		final HashMap<String, Function> functions = jppCompiler.parseFunctions(jooLines);
		String compiledJooCode = "";
		compiledJooCode = jppCompiler.writeFunctionsAndOperations(compiledJooCode, variables, functions);
		jooLines = compiledJooCode.split("" + JppVirtualMachine.LINE_BREAK);
		
		final char intTest0Name = 0 + JppVirtualMachine.COMPONENTS_START;
		final char intTest1Name = 1 + JppVirtualMachine.COMPONENTS_START;
		final char fixedTest0Name = 2 + JppVirtualMachine.COMPONENTS_START;
		final char fixedTest1Name = 3 + JppVirtualMachine.COMPONENTS_START;
		final char boolTest0Name = 4 + JppVirtualMachine.COMPONENTS_START;
		final char boolTest1Name = 5 + JppVirtualMachine.COMPONENTS_START;
		final char charTest0Name = 6 + JppVirtualMachine.COMPONENTS_START;
		final char charTest1Name = 7 + JppVirtualMachine.COMPONENTS_START;
		
		final char intTestName = 8 + JppVirtualMachine.COMPONENTS_START;
		final char fixedTestName = 9 + JppVirtualMachine.COMPONENTS_START;
		final char boolTestName = 10 + JppVirtualMachine.COMPONENTS_START;
		final char charTestName = 11 + JppVirtualMachine.COMPONENTS_START;
		
		final char startName = 12 + JppVirtualMachine.COMPONENTS_START;
		final char functionTestName = 13 + JppVirtualMachine.COMPONENTS_START;
		
		final char paramTest0Name = 0 + JppVirtualMachine.PARAMETERS_START;
		final char paramTest1Name = 1 + JppVirtualMachine.PARAMETERS_START;
		
		// shows each character of the compiled code as numbers
		// makes debugging easier
//		String kindOfReadableCompiledJooCode = "";
//		for (int i = 0; i < jooLines.length; i++) {
//			char[] jooLineChars = jooLines[i].toCharArray();
//			for (int j = 0; j < jooLineChars.length; j++) {
//				kindOfReadableCompiledJooCode += (int)jooLineChars[j] + "-";
//			}
//			kindOfReadableCompiledJooCode += "\n";
//		}
//		System.out.print(kindOfReadableCompiledJooCode);
		
		assertEquals(jooLines[0], "" + JppVirtualMachine.KEYWORD_FUNCTION + startName);
		assertEquals(jooLines[1], "" + JppVirtualMachine.KEYWORD_FUNCTION_CALL + functionTestName
											+ JppVirtualMachine.KEYWORD_PARAMETER + intTest0Name 
											+ JppVirtualMachine.KEYWORD_PARAMETER + intTestName);
		assertEquals(jooLines[2], "" + intTest0Name + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("100"));
		assertEquals(jooLines[3], "" + intTest0Name + JppVirtualMachine.OPERATOR_SUBTRACT + intTest1Name);
		assertEquals(jooLines[4], "" + fixedTest0Name + JppVirtualMachine.OPERATOR_MULTIPLY + toVirtualMachineNumber("" + Math.round(25.54f * 255)));
		assertEquals(jooLines[5], "" + fixedTest0Name + JppVirtualMachine.OPERATOR_DIVIDE + fixedTest1Name);
		assertEquals(jooLines[6], "" + boolTest0Name + JppVirtualMachine.OPERATOR_SET_EQUALS + toVirtualMachineNumber("0"));
		assertEquals(jooLines[7], "" + boolTest1Name + JppVirtualMachine.OPERATOR_SET_EQUALS + toVirtualMachineNumber("1"));
		assertEquals(jooLines[8], "" + charTest0Name + JppVirtualMachine.OPERATOR_SET_EQUALS + JppVirtualMachine.TYPE_CHAR + 'A');
		assertEquals(jooLines[9], "" + charTest1Name + JppVirtualMachine.OPERATOR_SET_EQUALS + JppVirtualMachine.TYPE_CHAR + 'B');
		assertEquals(jooLines[10], "" + JppVirtualMachine.KEYWORD_IF + intTest0Name + JppVirtualMachine.COMPARATOR_EQUALS + toVirtualMachineNumber("100"));
		assertEquals(jooLines[11], "" + intTestName + (char)3 + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("100"));
		assertEquals(jooLines[12], "" + JppVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[13], "" + intTestName + (char)4 + JppVirtualMachine.OPERATOR_SUBTRACT + intTest0Name);
		assertEquals(jooLines[14], "" + fixedTestName + (char)8 + JppVirtualMachine.OPERATOR_MULTIPLY + intTestName + (char)10);
		assertEquals(jooLines[15], "" + JppVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[16], "" + JppVirtualMachine.KEYWORD_IF + intTest0Name + JppVirtualMachine.COMPARATOR_BIGGER + intTest1Name);
		assertEquals(jooLines[17], "" + JppVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[18], "" + JppVirtualMachine.KEYWORD_IF + intTestName + (char)3 + JppVirtualMachine.COMPARATOR_SMALLER + intTest1Name);
		assertEquals(jooLines[19], "" + JppVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[20], "" + JppVirtualMachine.KEYWORD_IF + intTestName + (char)3 + JppVirtualMachine.COMPARATOR_NOT_EQUALS + intTestName + (char)10);
		assertEquals(jooLines[21], "" + JppVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[22], "" + JppVirtualMachine.KEYWORD_FUNCTION + functionTestName);
		assertEquals(jooLines[23], "" + paramTest0Name + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("100"));
		assertEquals(jooLines[24], "" + paramTest1Name + (char)6 + JppVirtualMachine.OPERATOR_SET_EQUALS + paramTest0Name);
	}
	
	private String toVirtualMachineNumber(String value) {
		for (int i = 0; i < 9; i++) {
			value = value.replace((char)('0' + i), (char)(JppVirtualMachine.NUMBER_0 + i));
		}
		return value;
	}
}
