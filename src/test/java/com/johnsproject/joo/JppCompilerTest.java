package com.johnsproject.joo;

import java.util.Map;

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
		
		final char intTest0Name = 0 + JppVirtualMachine.COMPONENTS_START;
		final char intTest1Name = 1 + JppVirtualMachine.COMPONENTS_START;
		final char ifTestName = 2 + JppVirtualMachine.COMPONENTS_START;
		final char fixedTest0Name = 3 + JppVirtualMachine.COMPONENTS_START;
		final char fixedTest1Name = 4 + JppVirtualMachine.COMPONENTS_START;
		final char boolTest0Name = 5 + JppVirtualMachine.COMPONENTS_START;
		final char boolTest1Name = 6 + JppVirtualMachine.COMPONENTS_START;
		final char boolTest2Name = 7 + JppVirtualMachine.COMPONENTS_START;
		final char charTest0Name = 8 + JppVirtualMachine.COMPONENTS_START;
		final char charTest1Name = 9 + JppVirtualMachine.COMPONENTS_START;
		final char charTest2Name = 10 + JppVirtualMachine.COMPONENTS_START;
		
		final char intTestName = 11 + JppVirtualMachine.COMPONENTS_START;
		final char fixedTestName = 12 + JppVirtualMachine.COMPONENTS_START;
		final char boolTestName = 13 + JppVirtualMachine.COMPONENTS_START;
		final char charTestName = 14 + JppVirtualMachine.COMPONENTS_START;
		
		final char startName = 15 + JppVirtualMachine.COMPONENTS_START;
		final char functionTestName = 16 + JppVirtualMachine.COMPONENTS_START;
		
		final char paramTest0Name = 0 + JppVirtualMachine.PARAMETERS_START;
		final char paramTest1Name = 1 + JppVirtualMachine.PARAMETERS_START;

		int line = 0;
		assertEquals(jooLines[line++], "" + JppVirtualMachine.TYPE_INT + (char)3);
		assertEquals(jooLines[line++], "" + intTest0Name);
		assertEquals(jooLines[line++], "" + intTest1Name + toVirtualMachineNumber("10"));
		assertEquals(jooLines[line++], "" + ifTestName);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.TYPE_FIXED + (char)2);
		assertEquals(jooLines[line++], "" + fixedTest0Name);
		assertEquals(jooLines[line++], "" + fixedTest1Name + toVirtualMachineNumber("" + Math.round(100.5f * 255)));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.TYPE_BOOL + (char)3);
		assertEquals(jooLines[line++], "" + boolTest0Name);
		assertEquals(jooLines[line++], "" + boolTest1Name + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + boolTest2Name + toVirtualMachineNumber("0"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.TYPE_CHAR + (char)3);
		assertEquals(jooLines[line++], "" + charTest0Name);
		assertEquals(jooLines[line++], "" + charTest1Name + 'A');
		assertEquals(jooLines[line++], "" + charTest2Name + 'C');
		assertEquals(jooLines[line++], "" + JppVirtualMachine.TYPE_ARRAY_INT + (char)1);
		assertEquals(jooLines[line++], "" + intTestName + (char)10);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.TYPE_ARRAY_FIXED + (char)1);
		assertEquals(jooLines[line++], "" + fixedTestName + (char)5);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.TYPE_ARRAY_BOOL + (char)1);
		assertEquals(jooLines[line++], "" + boolTestName + (char)15);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.TYPE_ARRAY_CHAR + (char)1);
		assertEquals(jooLines[line++], "" + charTestName + (char)13);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.TYPE_FUNCTION + (char)2);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_FUNCTION + startName);
		assertEquals(jooLines[line++], "" + intTest0Name + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("100"));
		assertEquals(jooLines[line++], "" + intTest0Name + JppVirtualMachine.OPERATOR_SUBTRACT + intTest1Name);
		assertEquals(jooLines[line++], "" + intTest0Name + JppVirtualMachine.OPERATOR_MULTIPLY + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + intTest0Name + JppVirtualMachine.OPERATOR_DIVIDE + toVirtualMachineNumber("10"));
		assertEquals(jooLines[line++], "" + intTest1Name + JppVirtualMachine.OPERATOR_SET_EQUALS + toVirtualMachineNumber("6"));
		assertEquals(jooLines[line++], "" + fixedTest0Name + JppVirtualMachine.OPERATOR_ADD + fixedTest1Name);
		assertEquals(jooLines[line++], "" + fixedTest0Name + JppVirtualMachine.OPERATOR_SUBTRACT + toVirtualMachineNumber("" + Math.round(0.5f * 255)));
		assertEquals(jooLines[line++], "" + fixedTest0Name + JppVirtualMachine.OPERATOR_MULTIPLY + toVirtualMachineNumber("" + Math.round(2.5f * 255)));
		assertEquals(jooLines[line++], "" + fixedTest0Name + JppVirtualMachine.OPERATOR_DIVIDE + toVirtualMachineNumber("" + Math.round(5f * 255)));
		assertEquals(jooLines[line++], "" + fixedTest1Name + JppVirtualMachine.OPERATOR_SET_EQUALS + toVirtualMachineNumber("" + Math.round(50f * 255)));
		assertEquals(jooLines[line++], "" + boolTest0Name + JppVirtualMachine.OPERATOR_SET_EQUALS + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + boolTest1Name + JppVirtualMachine.OPERATOR_SET_EQUALS + toVirtualMachineNumber("0"));
		assertEquals(jooLines[line++], "" + boolTest2Name + JppVirtualMachine.OPERATOR_SET_EQUALS + boolTest1Name);
		assertEquals(jooLines[line++], "" + charTest0Name + JppVirtualMachine.OPERATOR_SET_EQUALS + JppVirtualMachine.TYPE_CHAR + 'A');
		assertEquals(jooLines[line++], "" + charTest1Name + JppVirtualMachine.OPERATOR_SET_EQUALS + JppVirtualMachine.TYPE_CHAR + 'B');
		assertEquals(jooLines[line++], "" + charTest2Name + JppVirtualMachine.OPERATOR_SET_EQUALS + charTest1Name);
		assertEquals(jooLines[line++], "" + intTestName + toIndex(0) + JppVirtualMachine.OPERATOR_SET_EQUALS + toVirtualMachineNumber("30"));
		assertEquals(jooLines[line++], "" + intTestName + toIndex(1) + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("15"));
		assertEquals(jooLines[line++], "" + intTestName + toIndex(0) + JppVirtualMachine.OPERATOR_SUBTRACT + intTest1Name);
		assertEquals(jooLines[line++], "" + intTestName + toIndex(1) + JppVirtualMachine.OPERATOR_DIVIDE + toVirtualMachineNumber("5"));
		assertEquals(jooLines[line++], "" + intTestName + toIndex(0) + JppVirtualMachine.OPERATOR_MULTIPLY + intTestName + toIndex(1));
		assertEquals(jooLines[line++], "" + fixedTestName + toIndex(0) + JppVirtualMachine.OPERATOR_SET_EQUALS + toVirtualMachineNumber("" + Math.round(60.5f * 255)));
		assertEquals(jooLines[line++], "" + fixedTestName + toIndex(1) + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("" + Math.round(15f * 255)));
		assertEquals(jooLines[line++], "" + fixedTestName + toIndex(0) + JppVirtualMachine.OPERATOR_SUBTRACT + fixedTest1Name);
		assertEquals(jooLines[line++], "" + fixedTestName + toIndex(1) + JppVirtualMachine.OPERATOR_DIVIDE + toVirtualMachineNumber("" + Math.round(5f * 255)));
		assertEquals(jooLines[line++], "" + fixedTestName + toIndex(0) + JppVirtualMachine.OPERATOR_MULTIPLY + fixedTestName + toIndex(1));
		assertEquals(jooLines[line++], "" + boolTestName + toIndex(9) + JppVirtualMachine.OPERATOR_SET_EQUALS + boolTest0Name);
		assertEquals(jooLines[line++], "" + boolTestName + toIndex(10) + JppVirtualMachine.OPERATOR_SET_EQUALS + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + boolTestName + toIndex(11) + JppVirtualMachine.OPERATOR_SET_EQUALS + boolTestName + toIndex(10));
		assertEquals(jooLines[line++], "" + charTestName + toIndex(9) + JppVirtualMachine.OPERATOR_SET_EQUALS + charTest0Name);
		assertEquals(jooLines[line++], "" + charTestName + toIndex(10) + JppVirtualMachine.OPERATOR_SET_EQUALS + JppVirtualMachine.TYPE_CHAR + 'C');
		assertEquals(jooLines[line++], "" + charTestName + toIndex(11) + JppVirtualMachine.OPERATOR_SET_EQUALS + charTestName + toIndex(10));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF + intTest0Name + JppVirtualMachine.COMPARATOR_EQUALS + toVirtualMachineNumber("18"));
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF + intTest0Name + JppVirtualMachine.COMPARATOR_NOT_EQUALS + toVirtualMachineNumber("18"));
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF + intTest0Name + JppVirtualMachine.COMPARATOR_NOT_EQUALS + intTest1Name);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + intTestName + toIndex(2) + JppVirtualMachine.OPERATOR_SET_EQUALS + toVirtualMachineNumber("100"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF + intTest0Name + JppVirtualMachine.COMPARATOR_SMALLER + intTestName + toIndex(2));
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF + intTestName + toIndex(2) + JppVirtualMachine.COMPARATOR_BIGGER + intTest0Name);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF + intTest0Name + JppVirtualMachine.COMPARATOR_SMALLER_EQUALS + intTestName + toIndex(2));
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF + intTestName + toIndex(2) + JppVirtualMachine.COMPARATOR_BIGGER_EQUALS + intTest0Name);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + intTestName + toIndex(3) + JppVirtualMachine.OPERATOR_SET_EQUALS + intTestName + toIndex(2));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF + intTestName + toIndex(2) + JppVirtualMachine.COMPARATOR_BIGGER_EQUALS + intTestName + toIndex(3));
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF + intTestName + toIndex(2) + JppVirtualMachine.COMPARATOR_SMALLER_EQUALS + intTestName + toIndex(3));
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_FUNCTION_CALL + functionTestName
				+ JppVirtualMachine.KEYWORD_PARAMETER + intTest0Name 
				+ JppVirtualMachine.KEYWORD_PARAMETER + intTestName);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_FUNCTION + functionTestName);
		assertEquals(jooLines[line++], "" + paramTest0Name + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("100"));
		assertEquals(jooLines[line++], "" + paramTest1Name + toIndex(5) + JppVirtualMachine.OPERATOR_SET_EQUALS + paramTest0Name);
		assertEquals(jooLines[line++], "" + paramTest1Name + toIndex(4) + JppVirtualMachine.OPERATOR_SET_EQUALS + paramTest1Name + toIndex(5));
		assertEquals(jooLines[line++], "" + paramTest0Name + JppVirtualMachine.OPERATOR_ADD + paramTest1Name + toIndex(4));
		assertEquals(jooLines[line++], "" + paramTest1Name + intTest1Name + JppVirtualMachine.OPERATOR_SET_EQUALS + intTest1Name);
		assertEquals(jooLines[line++], "" + paramTest0Name + JppVirtualMachine.OPERATOR_SUBTRACT + paramTest1Name + intTest1Name);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF + paramTest0Name + JppVirtualMachine.COMPARATOR_SMALLER + paramTest1Name + toIndex(5));
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF);
	}
	
	@Test
	public void parseVariablesTest() throws Exception {
		final String jooCode = FileUtil.readResource("TestCode.joo");
		final JppCompiler jppCompiler = new JppCompiler();
		final String[] jooLines = jppCompiler.getJooLines(jooCode);
		final Map<String, Variable>[] variables = jppCompiler.parseVariables(jooLines);
		
		int type = 0;
		assert(variables[type].get("intTest0").getName() == 1);
		assert(variables[type].get("intTest1").getName() == 2);
		assert(variables[type].get("ifTest").getName() == 3);
		assertEquals(variables[type].get("intTest0").getValue(), "");
		assertEquals(variables[type].get("intTest1").getValue(), "10");
		assertEquals(variables[type].get("ifTest").getValue(), "");
		type++;
		assert(variables[type].get("fixedTest0").getName() == 4);
		assert(variables[type].get("fixedTest1").getName() == 5);
		assertEquals(variables[type].get("fixedTest0").getValue(), "");
		assertEquals(variables[type].get("fixedTest1").getValue(), "" + Math.round(100.5f * 255));
		type++;
		assert(variables[type].get("boolTest0").getName() == 6);
		assert(variables[type].get("boolTest1").getName() == 7);
		assert(variables[type].get("boolTest2").getName() == 8);
		assertEquals(variables[type].get("boolTest0").getValue(), "");
		assertEquals(variables[type].get("boolTest1").getValue(), "1");
		assertEquals(variables[type].get("boolTest2").getValue(), "0");
		type++;
		assert(variables[type].get("charTest0").getName() == 9);
		assert(variables[type].get("charTest1").getName() == 10);
		assert(variables[type].get("charTest2").getName() == 11);
		assertEquals(variables[type].get("charTest0").getValue(), "");
		assertEquals(variables[type].get("charTest1").getValue(), "A");	
		assertEquals(variables[type].get("charTest2").getValue(), "C");	
		type++;
		assert(variables[type].get("intTest").getName() == 12);
		assertEquals(variables[type].get("intTest").getValue(), "" + (char)10);
		type++;
		assert(variables[type].get("fixedTest").getName() == 13);
		assertEquals(variables[type].get("fixedTest").getValue(), "" + (char)5);
		type++;
		assert(variables[type].get("boolTest").getName() == 14);
		assertEquals(variables[type].get("boolTest").getValue(), "" + (char)15);
		type++;
		assert(variables[type].get("charTest").getName() == 15);
		assertEquals(variables[type].get("charTest").getValue(), "" + (char)13);			
	}
	
	@Test
	public void parseFunctionsTest() throws Exception {
		final String jooCode = FileUtil.readResource("TestCode.joo");
		final JppCompiler jppCompiler = new JppCompiler();
		final String[] jooLines = jppCompiler.getJooLines(jooCode);
		final Map<String, Variable>[] variables = jppCompiler.parseVariables(jooLines);
		final Map<String, Function> functions = jppCompiler.parseFunctions(jooLines, variables);
		
		final Function start = functions.get("Start");
		final Function functionTest = functions.get("FunctionTest");
		
		assert(start.getName() == 16);
		assertEquals(start.getParameters().size(), 0);
		
		assert(functionTest.getName() == 17);
		assert(functionTest.getParameters().containsKey("_paramTest0"));
		assert(functionTest.getParameters().containsKey("_paramTest1"));
		assertEquals(functionTest.getParameters().get("_paramTest0"), "" + JppVirtualMachine.TYPE_INT);
		assertEquals(functionTest.getParameters().get("_paramTest1"), "" + JppVirtualMachine.TYPE_ARRAY_INT);
		
		int currentOperationIndex = 0;
		Operation currentOperation = null;
		
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
		assertEquals(currentOperation.getVariable0Name(), "intTest0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_MULTIPLY);
		assertEquals(currentOperation.getValue(), "2");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "intTest0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_DIVIDE);
		assertEquals(currentOperation.getValue(), "10");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "intTest1");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SET_EQUALS);
		assertEquals(currentOperation.getValue(), "6");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "fixedTest0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getVariable1Name(), "fixedTest1");
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "fixedTest0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SUBTRACT);
		assertEquals(currentOperation.getValue(), "" + Math.round(0.5 * 255));
		assertEquals(currentOperation.getValueType(), TYPE_FIXED);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "fixedTest0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_MULTIPLY);
		assertEquals(currentOperation.getValue(), "" + Math.round(2.5 * 255));
		assertEquals(currentOperation.getValueType(), TYPE_FIXED);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "fixedTest0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_DIVIDE);
		assertEquals(currentOperation.getValue(), "" + Math.round(5 * 255));
		assertEquals(currentOperation.getValueType(), TYPE_FIXED);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "fixedTest1");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SET_EQUALS);
		assertEquals(currentOperation.getValue(), "" + Math.round(50 * 255));
		assertEquals(currentOperation.getValueType(), TYPE_FIXED);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "boolTest0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SET_EQUALS);
		assertEquals(currentOperation.getValue(), "1");
		assertEquals(currentOperation.getValueType(), TYPE_BOOL);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "boolTest1");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SET_EQUALS);
		assertEquals(currentOperation.getValue(), "0");
		assertEquals(currentOperation.getValueType(), TYPE_BOOL);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "boolTest2");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SET_EQUALS);
		assertEquals(currentOperation.getVariable1Name(), "boolTest1");
		
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
		assertEquals(currentOperation.getVariable0Name(), "charTest2");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SET_EQUALS);
		assertEquals(currentOperation.getVariable1Name(), "charTest1");
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "intTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), "0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SET_EQUALS);
		assertEquals(currentOperation.getValue(), "30");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "intTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), "1");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(), "15");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "intTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), "0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SUBTRACT);
		assertEquals(currentOperation.getVariable1Name(), "intTest1");
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "intTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), "1");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_DIVIDE);
		assertEquals(currentOperation.getValue(), "5");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "intTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), "0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_MULTIPLY);
		assertEquals(currentOperation.getVariable1Name(), "intTest");
		assertEquals(currentOperation.getVariable1ArrayIndex(), "1");
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "fixedTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), "0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SET_EQUALS);
		assertEquals(currentOperation.getValue(),  "" + Math.round(60.5 * 255));
		assertEquals(currentOperation.getValueType(), TYPE_FIXED);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "fixedTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), "1");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(),  "" + Math.round(15 * 255));
		assertEquals(currentOperation.getValueType(), TYPE_FIXED);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "fixedTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), "0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SUBTRACT);
		assertEquals(currentOperation.getVariable1Name(), "fixedTest1");
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "fixedTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), "1");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_DIVIDE);
		assertEquals(currentOperation.getValue(),  "" + Math.round(5 * 255));
		assertEquals(currentOperation.getValueType(), TYPE_FIXED);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "fixedTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), "0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_MULTIPLY);
		assertEquals(currentOperation.getVariable1Name(), "fixedTest");
		assertEquals(currentOperation.getVariable1ArrayIndex(), "1");
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "boolTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), "9");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SET_EQUALS);
		assertEquals(currentOperation.getVariable1Name(), "boolTest0");
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "boolTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), "10");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SET_EQUALS);
		assertEquals(currentOperation.getValue(), "1");
		assertEquals(currentOperation.getValueType(), TYPE_BOOL);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "boolTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), "11");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SET_EQUALS);
		assertEquals(currentOperation.getVariable1Name(), "boolTest");
		assertEquals(currentOperation.getVariable1ArrayIndex(), "10");
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "charTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), "9");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SET_EQUALS);
		assertEquals(currentOperation.getVariable1Name(), "charTest0");
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "charTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), "10");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SET_EQUALS);
		assertEquals(currentOperation.getValue(), "C");
		assertEquals(currentOperation.getValueType(), TYPE_CHAR);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "charTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), "11");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SET_EQUALS);
		assertEquals(currentOperation.getVariable1Name(), "charTest");
		assertEquals(currentOperation.getVariable1ArrayIndex(), "10");
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.isCondition(), true);
		assertEquals(currentOperation.getVariable0Name(), "intTest0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.COMPARATOR_EQUALS);
		assertEquals(currentOperation.getValue(), "18");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "ifTest");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(), "1");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.isCondition(), true);
		assertEquals(currentOperation.getVariable0Name(), "intTest0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.COMPARATOR_NOT_EQUALS);
		assertEquals(currentOperation.getValue(), "18");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "ifTest");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(), "1");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_ELSE);

		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "ifTest");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(), "2");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_IF);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "ifTest");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(), "1");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_ELSE);

		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "ifTest");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(), "2");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_IF);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.isCondition(), true);
		assertEquals(currentOperation.getVariable0Name(), "intTest0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.COMPARATOR_NOT_EQUALS);
		assertEquals(currentOperation.getVariable1Name(), "intTest1");
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "ifTest");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(), "1");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_ELSE);

		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "ifTest");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(), "2");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_IF);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "intTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), "2");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SET_EQUALS);
		assertEquals(currentOperation.getValue(), "100");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.isCondition(), true);
		assertEquals(currentOperation.getVariable0Name(), "intTest0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.COMPARATOR_SMALLER);
		assertEquals(currentOperation.getVariable1Name(), "intTest");
		assertEquals(currentOperation.getVariable1ArrayIndex(), "2");
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "ifTest");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(), "1");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_ELSE);

		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "ifTest");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(), "2");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_IF);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.isCondition(), true);
		assertEquals(currentOperation.getVariable0Name(), "intTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), "2");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.COMPARATOR_BIGGER);
		assertEquals(currentOperation.getVariable1Name(), "intTest0");
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "ifTest");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(), "1");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_ELSE);

		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "ifTest");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(), "2");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_IF);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.isCondition(), true);
		assertEquals(currentOperation.getVariable0Name(), "intTest0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.COMPARATOR_SMALLER_EQUALS);
		assertEquals(currentOperation.getVariable1Name(), "intTest");
		assertEquals(currentOperation.getVariable1ArrayIndex(), "2");
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "ifTest");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(), "1");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_ELSE);

		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "ifTest");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(), "2");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_IF);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.isCondition(), true);
		assertEquals(currentOperation.getVariable0Name(), "intTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), "2");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.COMPARATOR_BIGGER_EQUALS);
		assertEquals(currentOperation.getVariable1Name(), "intTest0");
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "ifTest");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(), "1");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_ELSE);

		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "ifTest");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(), "2");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_IF);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "intTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), "3");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SET_EQUALS);
		assertEquals(currentOperation.getVariable1Name(), "intTest");
		assertEquals(currentOperation.getVariable1ArrayIndex(), "2");
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.isCondition(), true);
		assertEquals(currentOperation.getVariable0Name(), "intTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), "2");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.COMPARATOR_BIGGER_EQUALS);
		assertEquals(currentOperation.getVariable1Name(), "intTest");
		assertEquals(currentOperation.getVariable1ArrayIndex(), "3");
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "ifTest");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(), "1");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_ELSE);

		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "ifTest");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(), "2");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_IF);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.isCondition(), true);
		assertEquals(currentOperation.getVariable0Name(), "intTest");
		assertEquals(currentOperation.getVariable0ArrayIndex(), "2");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.COMPARATOR_SMALLER_EQUALS);
		assertEquals(currentOperation.getVariable1Name(), "intTest");
		assertEquals(currentOperation.getVariable1ArrayIndex(), "3");
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "ifTest");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(), "1");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_ELSE);

		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "ifTest");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(), "2");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_IF);
		
		currentOperation = start.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_FUNCTION_CALL);
		assertEquals(currentOperation.getVariable1Name(), "FunctionTest");
		assertEquals(currentOperation.getParameters()[0], "intTest0");
		assertEquals(currentOperation.getParameters()[1], "intTest");
		
		
		currentOperationIndex = 0;
		currentOperation = functionTest.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "_paramTest0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(), "100");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = functionTest.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "_paramTest1");
		assertEquals(currentOperation.getVariable0ArrayIndex(), "5");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SET_EQUALS);
		assertEquals(currentOperation.getVariable1Name(), "_paramTest0");
		
		currentOperation = functionTest.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "_paramTest1");
		assertEquals(currentOperation.getVariable0ArrayIndex(), "4");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SET_EQUALS);
		assertEquals(currentOperation.getVariable1Name(), "_paramTest1");
		assertEquals(currentOperation.getVariable1ArrayIndex(), "5");
		
		currentOperation = functionTest.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "_paramTest0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getVariable1Name(), "_paramTest1");
		assertEquals(currentOperation.getVariable1ArrayIndex(), "4");
		
		currentOperation = functionTest.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "_paramTest1");
		assertEquals(currentOperation.getVariable0ArrayIndex(), "intTest1");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SET_EQUALS);
		assertEquals(currentOperation.getVariable1Name(), "intTest1");
		
		currentOperation = functionTest.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "_paramTest0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_SUBTRACT);
		assertEquals(currentOperation.getVariable1Name(), "_paramTest1");
		assertEquals(currentOperation.getVariable1ArrayIndex(), "intTest1");
		
		currentOperation = functionTest.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.isCondition(), true);
		assertEquals(currentOperation.getVariable0Name(), "_paramTest0");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.COMPARATOR_SMALLER);
		assertEquals(currentOperation.getVariable1Name(), "_paramTest1");
		assertEquals(currentOperation.getVariable1ArrayIndex(), "5");
		
		currentOperation = functionTest.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "ifTest");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(), "1");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = functionTest.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_ELSE);

		currentOperation = functionTest.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getVariable0Name(), "ifTest");
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.OPERATOR_ADD);
		assertEquals(currentOperation.getValue(), "2");
		assertEquals(currentOperation.getValueType(), TYPE_INT);
		
		currentOperation = functionTest.getOperations().get(currentOperationIndex++);
		assertEquals(currentOperation.getOperator(), JppVirtualMachine.KEYWORD_IF);
	}
	
	@Test
	public void writeVariablesAndFunctionsTest() throws Exception {
		final String jooCode = FileUtil.readResource("TestCode.joo");
		final JppCompiler jppCompiler = new JppCompiler();
		String[] jooLines = jppCompiler.getJooLines(jooCode);
		final Map<String, Variable>[] variables = jppCompiler.parseVariables(jooLines);
		final Map<String, Function> functions = jppCompiler.parseFunctions(jooLines, variables);
		String compiledJooCode = "";
		compiledJooCode = jppCompiler.writeVariablesAndFunctions(compiledJooCode, variables, functions);
		jooLines = compiledJooCode.split("" + JppVirtualMachine.LINE_BREAK);
		
		final char intTest0Name = 0 + JppVirtualMachine.COMPONENTS_START;
		final char intTest1Name = 1 + JppVirtualMachine.COMPONENTS_START;
		final char ifTestName = 2 + JppVirtualMachine.COMPONENTS_START;
		final char fixedTest0Name = 3 + JppVirtualMachine.COMPONENTS_START;
		final char fixedTest1Name = 4 + JppVirtualMachine.COMPONENTS_START;
		final char boolTest0Name = 5 + JppVirtualMachine.COMPONENTS_START;
		final char boolTest1Name = 6 + JppVirtualMachine.COMPONENTS_START;
		final char boolTest2Name = 7 + JppVirtualMachine.COMPONENTS_START;
		final char charTest0Name = 8 + JppVirtualMachine.COMPONENTS_START;
		final char charTest1Name = 9 + JppVirtualMachine.COMPONENTS_START;
		final char charTest2Name = 10 + JppVirtualMachine.COMPONENTS_START;
		
		final char intTestName = 11 + JppVirtualMachine.COMPONENTS_START;
		final char fixedTestName = 12 + JppVirtualMachine.COMPONENTS_START;
		final char boolTestName = 13 + JppVirtualMachine.COMPONENTS_START;
		final char charTestName = 14 + JppVirtualMachine.COMPONENTS_START;

		int line = 0;
		assertEquals(jooLines[line++], "" + JppVirtualMachine.TYPE_INT + (char)3);
		assertEquals(jooLines[line++], "" + intTest0Name);
		assertEquals(jooLines[line++], "" + intTest1Name + toVirtualMachineNumber("10"));
		assertEquals(jooLines[line++], "" + ifTestName);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.TYPE_FIXED + (char)2);
		assertEquals(jooLines[line++], "" + fixedTest0Name);
		assertEquals(jooLines[line++], "" + fixedTest1Name + toVirtualMachineNumber("" + Math.round(100.5f * 255)));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.TYPE_BOOL + (char)3);
		assertEquals(jooLines[line++], "" + boolTest0Name);
		assertEquals(jooLines[line++], "" + boolTest1Name + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + boolTest2Name + toVirtualMachineNumber("0"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.TYPE_CHAR + (char)3);
		assertEquals(jooLines[line++], "" + charTest0Name);
		assertEquals(jooLines[line++], "" + charTest1Name + 'A');
		assertEquals(jooLines[line++], "" + charTest2Name + 'C');
		assertEquals(jooLines[line++], "" + JppVirtualMachine.TYPE_ARRAY_INT + (char)1);
		assertEquals(jooLines[line++], "" + intTestName + (char)10);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.TYPE_ARRAY_FIXED + (char)1);
		assertEquals(jooLines[line++], "" + fixedTestName + (char)5);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.TYPE_ARRAY_BOOL + (char)1);
		assertEquals(jooLines[line++], "" + boolTestName + (char)15);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.TYPE_ARRAY_CHAR + (char)1);
		assertEquals(jooLines[line++], "" + charTestName + (char)13);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.TYPE_FUNCTION + (char)2);
	}
	
	@Test
	public void writeFunctionsAndOperationsTest() throws Exception {
		final String jooCode = FileUtil.readResource("TestCode.joo");
		final JppCompiler jppCompiler = new JppCompiler();
		String[] jooLines = jppCompiler.getJooLines(jooCode);
		final Map<String, Variable>[] variables = jppCompiler.parseVariables(jooLines);
		final Map<String, Function> functions = jppCompiler.parseFunctions(jooLines, variables);
		String compiledJooCode = "";
		compiledJooCode = jppCompiler.writeFunctionsAndOperations(compiledJooCode, variables, functions);
		jooLines = compiledJooCode.split("" + JppVirtualMachine.LINE_BREAK);
		
		final char intTest0Name = 0 + JppVirtualMachine.COMPONENTS_START;
		final char intTest1Name = 1 + JppVirtualMachine.COMPONENTS_START;
		final char ifTestName = 2 + JppVirtualMachine.COMPONENTS_START;
		final char fixedTest0Name = 3 + JppVirtualMachine.COMPONENTS_START;
		final char fixedTest1Name = 4 + JppVirtualMachine.COMPONENTS_START;
		final char boolTest0Name = 5 + JppVirtualMachine.COMPONENTS_START;
		final char boolTest1Name = 6 + JppVirtualMachine.COMPONENTS_START;
		final char boolTest2Name = 7 + JppVirtualMachine.COMPONENTS_START;
		final char charTest0Name = 8 + JppVirtualMachine.COMPONENTS_START;
		final char charTest1Name = 9 + JppVirtualMachine.COMPONENTS_START;
		final char charTest2Name = 10 + JppVirtualMachine.COMPONENTS_START;
		
		final char intTestName = 11 + JppVirtualMachine.COMPONENTS_START;
		final char fixedTestName = 12 + JppVirtualMachine.COMPONENTS_START;
		final char boolTestName = 13 + JppVirtualMachine.COMPONENTS_START;
		final char charTestName = 14 + JppVirtualMachine.COMPONENTS_START;
		
		final char startName = 15 + JppVirtualMachine.COMPONENTS_START;
		final char functionTestName = 16 + JppVirtualMachine.COMPONENTS_START;
		
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
		
		int line = 0;
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_FUNCTION + startName);
		assertEquals(jooLines[line++], "" + intTest0Name + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("100"));
		assertEquals(jooLines[line++], "" + intTest0Name + JppVirtualMachine.OPERATOR_SUBTRACT + intTest1Name);
		assertEquals(jooLines[line++], "" + intTest0Name + JppVirtualMachine.OPERATOR_MULTIPLY + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + intTest0Name + JppVirtualMachine.OPERATOR_DIVIDE + toVirtualMachineNumber("10"));
		assertEquals(jooLines[line++], "" + intTest1Name + JppVirtualMachine.OPERATOR_SET_EQUALS + toVirtualMachineNumber("6"));
		assertEquals(jooLines[line++], "" + fixedTest0Name + JppVirtualMachine.OPERATOR_ADD + fixedTest1Name);
		assertEquals(jooLines[line++], "" + fixedTest0Name + JppVirtualMachine.OPERATOR_SUBTRACT + toVirtualMachineNumber("" + Math.round(0.5f * 255)));
		assertEquals(jooLines[line++], "" + fixedTest0Name + JppVirtualMachine.OPERATOR_MULTIPLY + toVirtualMachineNumber("" + Math.round(2.5f * 255)));
		assertEquals(jooLines[line++], "" + fixedTest0Name + JppVirtualMachine.OPERATOR_DIVIDE + toVirtualMachineNumber("" + Math.round(5f * 255)));
		assertEquals(jooLines[line++], "" + fixedTest1Name + JppVirtualMachine.OPERATOR_SET_EQUALS + toVirtualMachineNumber("" + Math.round(50f * 255)));
		assertEquals(jooLines[line++], "" + boolTest0Name + JppVirtualMachine.OPERATOR_SET_EQUALS + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + boolTest1Name + JppVirtualMachine.OPERATOR_SET_EQUALS + toVirtualMachineNumber("0"));
		assertEquals(jooLines[line++], "" + boolTest2Name + JppVirtualMachine.OPERATOR_SET_EQUALS + boolTest1Name);
		assertEquals(jooLines[line++], "" + charTest0Name + JppVirtualMachine.OPERATOR_SET_EQUALS + JppVirtualMachine.TYPE_CHAR + 'A');
		assertEquals(jooLines[line++], "" + charTest1Name + JppVirtualMachine.OPERATOR_SET_EQUALS + JppVirtualMachine.TYPE_CHAR + 'B');
		assertEquals(jooLines[line++], "" + charTest2Name + JppVirtualMachine.OPERATOR_SET_EQUALS + charTest1Name);
		assertEquals(jooLines[line++], "" + intTestName + toIndex(0) + JppVirtualMachine.OPERATOR_SET_EQUALS + toVirtualMachineNumber("30"));
		assertEquals(jooLines[line++], "" + intTestName + toIndex(1) + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("15"));
		assertEquals(jooLines[line++], "" + intTestName + toIndex(0) + JppVirtualMachine.OPERATOR_SUBTRACT + intTest1Name);
		assertEquals(jooLines[line++], "" + intTestName + toIndex(1) + JppVirtualMachine.OPERATOR_DIVIDE + toVirtualMachineNumber("5"));
		assertEquals(jooLines[line++], "" + intTestName + toIndex(0) + JppVirtualMachine.OPERATOR_MULTIPLY + intTestName + toIndex(1));
		assertEquals(jooLines[line++], "" + fixedTestName + toIndex(0) + JppVirtualMachine.OPERATOR_SET_EQUALS + toVirtualMachineNumber("" + Math.round(60.5f * 255)));
		assertEquals(jooLines[line++], "" + fixedTestName + toIndex(1) + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("" + Math.round(15f * 255)));
		assertEquals(jooLines[line++], "" + fixedTestName + toIndex(0) + JppVirtualMachine.OPERATOR_SUBTRACT + fixedTest1Name);
		assertEquals(jooLines[line++], "" + fixedTestName + toIndex(1) + JppVirtualMachine.OPERATOR_DIVIDE + toVirtualMachineNumber("" + Math.round(5f * 255)));
		assertEquals(jooLines[line++], "" + fixedTestName + toIndex(0) + JppVirtualMachine.OPERATOR_MULTIPLY + fixedTestName + toIndex(1));
		assertEquals(jooLines[line++], "" + boolTestName + toIndex(9) + JppVirtualMachine.OPERATOR_SET_EQUALS + boolTest0Name);
		assertEquals(jooLines[line++], "" + boolTestName + toIndex(10) + JppVirtualMachine.OPERATOR_SET_EQUALS + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + boolTestName + toIndex(11) + JppVirtualMachine.OPERATOR_SET_EQUALS + boolTestName + toIndex(10));
		assertEquals(jooLines[line++], "" + charTestName + toIndex(9) + JppVirtualMachine.OPERATOR_SET_EQUALS + charTest0Name);
		assertEquals(jooLines[line++], "" + charTestName + toIndex(10) + JppVirtualMachine.OPERATOR_SET_EQUALS + JppVirtualMachine.TYPE_CHAR + 'C');
		assertEquals(jooLines[line++], "" + charTestName + toIndex(11) + JppVirtualMachine.OPERATOR_SET_EQUALS + charTestName + toIndex(10));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF + intTest0Name + JppVirtualMachine.COMPARATOR_EQUALS + toVirtualMachineNumber("18"));
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF + intTest0Name + JppVirtualMachine.COMPARATOR_NOT_EQUALS + toVirtualMachineNumber("18"));
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF + intTest0Name + JppVirtualMachine.COMPARATOR_NOT_EQUALS + intTest1Name);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + intTestName + toIndex(2) + JppVirtualMachine.OPERATOR_SET_EQUALS + toVirtualMachineNumber("100"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF + intTest0Name + JppVirtualMachine.COMPARATOR_SMALLER + intTestName + toIndex(2));
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF + intTestName + toIndex(2) + JppVirtualMachine.COMPARATOR_BIGGER + intTest0Name);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF + intTest0Name + JppVirtualMachine.COMPARATOR_SMALLER_EQUALS + intTestName + toIndex(2));
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF + intTestName + toIndex(2) + JppVirtualMachine.COMPARATOR_BIGGER_EQUALS + intTest0Name);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + intTestName + toIndex(3) + JppVirtualMachine.OPERATOR_SET_EQUALS + intTestName + toIndex(2));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF + intTestName + toIndex(2) + JppVirtualMachine.COMPARATOR_BIGGER_EQUALS + intTestName + toIndex(3));
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF + intTestName + toIndex(2) + JppVirtualMachine.COMPARATOR_SMALLER_EQUALS + intTestName + toIndex(3));
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_FUNCTION_CALL + functionTestName
				+ JppVirtualMachine.KEYWORD_PARAMETER + intTest0Name 
				+ JppVirtualMachine.KEYWORD_PARAMETER + intTestName);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_FUNCTION + functionTestName);
		assertEquals(jooLines[line++], "" + paramTest0Name + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("100"));
		assertEquals(jooLines[line++], "" + paramTest1Name + toIndex(5) + JppVirtualMachine.OPERATOR_SET_EQUALS + paramTest0Name);
		assertEquals(jooLines[line++], "" + paramTest1Name + toIndex(4) + JppVirtualMachine.OPERATOR_SET_EQUALS + paramTest1Name + toIndex(5));
		assertEquals(jooLines[line++], "" + paramTest0Name + JppVirtualMachine.OPERATOR_ADD + paramTest1Name + toIndex(4));
		assertEquals(jooLines[line++], "" + paramTest1Name + intTest1Name + JppVirtualMachine.OPERATOR_SET_EQUALS + intTest1Name);
		assertEquals(jooLines[line++], "" + paramTest0Name + JppVirtualMachine.OPERATOR_SUBTRACT + paramTest1Name + intTest1Name);
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF + paramTest0Name + JppVirtualMachine.COMPARATOR_SMALLER + paramTest1Name + toIndex(5));
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JppVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JppVirtualMachine.KEYWORD_IF);
	}
	
	private char toIndex(int i) {
		return (char) (i + JppVirtualMachine.ARRAY_INDEXES_START);
	}
	
	private String toVirtualMachineNumber(String value) {
		for (int i = 0; i < 9; i++) {
			value = value.replace((char)('0' + i), (char)(JppVirtualMachine.NUMBER_0 + i));
		}
		return value;
	}
}
