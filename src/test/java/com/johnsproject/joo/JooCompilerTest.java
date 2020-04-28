package com.johnsproject.joo;

import java.util.Map;

import org.junit.Test;

import static com.johnsproject.joo.JooCompiler.*;
import static org.junit.Assert.assertEquals;

public class JooCompilerTest {

	@Test
	public void compileTest() throws Exception {
		final JooCompiler jooCompiler = new JooCompiler();
		final String compiledJooCode = jooCompiler.compile("TestCode.joo");
		final String[] jooLines = compiledJooCode.split("" + JooVirtualMachine.LINE_BREAK);
		
		final char intTest0Name = 0 + JooVirtualMachine.COMPONENTS_START;
		final char intTest1Name = 1 + JooVirtualMachine.COMPONENTS_START;
		final char ifTestName = 2 + JooVirtualMachine.COMPONENTS_START;
		final char fixedTest0Name = 3 + JooVirtualMachine.COMPONENTS_START;
		final char fixedTest1Name = 4 + JooVirtualMachine.COMPONENTS_START;
		final char boolTest0Name = 5 + JooVirtualMachine.COMPONENTS_START;
		final char boolTest1Name = 6 + JooVirtualMachine.COMPONENTS_START;
		final char boolTest2Name = 7 + JooVirtualMachine.COMPONENTS_START;
		final char charTest0Name = 8 + JooVirtualMachine.COMPONENTS_START;
		final char charTest1Name = 9 + JooVirtualMachine.COMPONENTS_START;
		final char charTest2Name = 10 + JooVirtualMachine.COMPONENTS_START;
		
		final char intTestName = 11 + JooVirtualMachine.COMPONENTS_START;
		final char fixedTestName = 12 + JooVirtualMachine.COMPONENTS_START;
		final char boolTestName = 13 + JooVirtualMachine.COMPONENTS_START;
		final char charTestName = 14 + JooVirtualMachine.COMPONENTS_START;
		
		final char startName = 15 + JooVirtualMachine.COMPONENTS_START;
		final char functionTestName = 16 + JooVirtualMachine.COMPONENTS_START;
		final char libraryTestName = 17 + JooVirtualMachine.COMPONENTS_START;
		final char directoryLibraryTestName = 18 + JooVirtualMachine.COMPONENTS_START;
		
		final char paramTest0Name = 0 + JooVirtualMachine.PARAMETERS_START;
		final char paramTest1Name = 1 + JooVirtualMachine.PARAMETERS_START;

		int line = 0;
		assertEquals(jooLines[line++], "" + JooVirtualMachine.TYPE_INT + (char)3);
		assertEquals(jooLines[line++], "" + intTest0Name);
		assertEquals(jooLines[line++], "" + intTest1Name + toVirtualMachineNumber("10"));
		assertEquals(jooLines[line++], "" + ifTestName);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.TYPE_FIXED + (char)2);
		assertEquals(jooLines[line++], "" + fixedTest0Name);
		assertEquals(jooLines[line++], "" + fixedTest1Name + toVirtualMachineNumber("" + Math.round(100.5f * 255)));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.TYPE_BOOL + (char)3);
		assertEquals(jooLines[line++], "" + boolTest0Name);
		assertEquals(jooLines[line++], "" + boolTest1Name + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + boolTest2Name + toVirtualMachineNumber("0"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.TYPE_CHAR + (char)3);
		assertEquals(jooLines[line++], "" + charTest0Name);
		assertEquals(jooLines[line++], "" + charTest1Name + 'A');
		assertEquals(jooLines[line++], "" + charTest2Name + 'C');
		assertEquals(jooLines[line++], "" + JooVirtualMachine.TYPE_ARRAY_INT + (char)1);
		assertEquals(jooLines[line++], "" + intTestName + (char)10);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.TYPE_ARRAY_FIXED + (char)1);
		assertEquals(jooLines[line++], "" + fixedTestName + (char)5);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.TYPE_ARRAY_BOOL + (char)1);
		assertEquals(jooLines[line++], "" + boolTestName + (char)15);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.TYPE_ARRAY_CHAR + (char)1);
		assertEquals(jooLines[line++], "" + charTestName + (char)13);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.TYPE_FUNCTION + (char)4);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_FUNCTION + startName);
		assertEquals(jooLines[line++], "" + intTest0Name + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("100"));
		assertEquals(jooLines[line++], "" + intTest0Name + JooVirtualMachine.OPERATOR_SUBTRACT + intTest1Name);
		assertEquals(jooLines[line++], "" + intTest0Name + JooVirtualMachine.OPERATOR_MULTIPLY + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + intTest0Name + JooVirtualMachine.OPERATOR_DIVIDE + toVirtualMachineNumber("10"));
		assertEquals(jooLines[line++], "" + intTest1Name + JooVirtualMachine.OPERATOR_ASSIGN + toVirtualMachineNumber("6"));
		assertEquals(jooLines[line++], "" + fixedTest0Name + JooVirtualMachine.OPERATOR_ADD + fixedTest1Name);
		assertEquals(jooLines[line++], "" + fixedTest0Name + JooVirtualMachine.OPERATOR_SUBTRACT + toVirtualMachineNumber("" + Math.round(0.5f * 255)));
		assertEquals(jooLines[line++], "" + fixedTest0Name + JooVirtualMachine.OPERATOR_MULTIPLY + toVirtualMachineNumber("" + Math.round(2.5f * 255)));
		assertEquals(jooLines[line++], "" + fixedTest0Name + JooVirtualMachine.OPERATOR_DIVIDE + toVirtualMachineNumber("" + Math.round(5f * 255)));
		assertEquals(jooLines[line++], "" + fixedTest1Name + JooVirtualMachine.OPERATOR_ASSIGN + toVirtualMachineNumber("" + Math.round(50f * 255)));
		assertEquals(jooLines[line++], "" + boolTest0Name + JooVirtualMachine.OPERATOR_ASSIGN + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + boolTest1Name + JooVirtualMachine.OPERATOR_ASSIGN + toVirtualMachineNumber("0"));
		assertEquals(jooLines[line++], "" + boolTest2Name + JooVirtualMachine.OPERATOR_ASSIGN + boolTest1Name);
		assertEquals(jooLines[line++], "" + charTest0Name + JooVirtualMachine.OPERATOR_ASSIGN + JooVirtualMachine.TYPE_CHAR + 'A');
		assertEquals(jooLines[line++], "" + charTest1Name + JooVirtualMachine.OPERATOR_ASSIGN + JooVirtualMachine.TYPE_CHAR + 'B');
		assertEquals(jooLines[line++], "" + charTest2Name + JooVirtualMachine.OPERATOR_ASSIGN + charTest1Name);
		assertEquals(jooLines[line++], "" + intTestName + toIndex(0) + JooVirtualMachine.OPERATOR_ASSIGN + toVirtualMachineNumber("30"));
		assertEquals(jooLines[line++], "" + intTestName + toIndex(1) + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("15"));
		assertEquals(jooLines[line++], "" + intTestName + toIndex(0) + JooVirtualMachine.OPERATOR_SUBTRACT + intTest1Name);
		assertEquals(jooLines[line++], "" + intTestName + toIndex(1) + JooVirtualMachine.OPERATOR_DIVIDE + toVirtualMachineNumber("5"));
		assertEquals(jooLines[line++], "" + intTestName + toIndex(0) + JooVirtualMachine.OPERATOR_MULTIPLY + intTestName + toIndex(1));
		assertEquals(jooLines[line++], "" + intTestName + toIndex(7) + JooVirtualMachine.OPERATOR_ASSIGN + toVirtualMachineNumber("25"));
		assertEquals(jooLines[line++], "" + fixedTestName + toIndex(0) + JooVirtualMachine.OPERATOR_ASSIGN + toVirtualMachineNumber("" + Math.round(60.5f * 255)));
		assertEquals(jooLines[line++], "" + fixedTestName + toIndex(1) + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("" + Math.round(15f * 255)));
		assertEquals(jooLines[line++], "" + fixedTestName + toIndex(0) + JooVirtualMachine.OPERATOR_SUBTRACT + fixedTest1Name);
		assertEquals(jooLines[line++], "" + fixedTestName + toIndex(1) + JooVirtualMachine.OPERATOR_DIVIDE + toVirtualMachineNumber("" + Math.round(5f * 255)));
		assertEquals(jooLines[line++], "" + fixedTestName + toIndex(0) + JooVirtualMachine.OPERATOR_MULTIPLY + fixedTestName + toIndex(1));
		assertEquals(jooLines[line++], "" + fixedTestName + toIndex(2) + JooVirtualMachine.OPERATOR_ASSIGN_NEGATIVE + toVirtualMachineNumber("" + Math.round(10f * 255)));
		assertEquals(jooLines[line++], "" + fixedTestName + toIndex(3) + JooVirtualMachine.OPERATOR_ASSIGN_POSITIVE + fixedTestName + toIndex(2));
		assertEquals(jooLines[line++], "" + fixedTestName + toIndex(4) + JooVirtualMachine.OPERATOR_ASSIGN_INVERSE + fixedTestName + toIndex(3));
		assertEquals(jooLines[line++], "" + fixedTestName + toIndex(5) + JooVirtualMachine.OPERATOR_ASSIGN + toVirtualMachineNumber("" + Math.round(25.25f * 255)));
		assertEquals(jooLines[line++], "" + boolTestName + toIndex(9) + JooVirtualMachine.OPERATOR_ASSIGN + boolTest0Name);
		assertEquals(jooLines[line++], "" + boolTestName + toIndex(10) + JooVirtualMachine.OPERATOR_ASSIGN + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + boolTestName + toIndex(11) + JooVirtualMachine.OPERATOR_ASSIGN + boolTestName + toIndex(10));
		assertEquals(jooLines[line++], "" + boolTestName + toIndex(2) + JooVirtualMachine.OPERATOR_ASSIGN_NEGATIVE + toVirtualMachineNumber("1"));	
		assertEquals(jooLines[line++], "" + boolTestName + toIndex(3) + JooVirtualMachine.OPERATOR_ASSIGN_POSITIVE + boolTestName + toIndex(2));	
		assertEquals(jooLines[line++], "" + boolTestName + toIndex(4) + JooVirtualMachine.OPERATOR_ASSIGN_INVERSE + boolTestName + toIndex(3));	
		assertEquals(jooLines[line++], "" + boolTestName + toIndex(5) + JooVirtualMachine.OPERATOR_ASSIGN + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + charTestName + toIndex(9) + JooVirtualMachine.OPERATOR_ASSIGN + charTest0Name);
		assertEquals(jooLines[line++], "" + charTestName + toIndex(10) + JooVirtualMachine.OPERATOR_ASSIGN + JooVirtualMachine.TYPE_CHAR + 'C');
		assertEquals(jooLines[line++], "" + charTestName + toIndex(11) + JooVirtualMachine.OPERATOR_ASSIGN + charTestName + toIndex(10));
		assertEquals(jooLines[line++], "" + charTestName + toIndex(0) + JooVirtualMachine.OPERATOR_ASSIGN + JooVirtualMachine.TYPE_CHAR + 'd');
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF + intTest0Name + JooVirtualMachine.COMPARATOR_EQUALS + toVirtualMachineNumber("18"));
		assertEquals(jooLines[line++], "" + ifTestName + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF + intTest0Name + JooVirtualMachine.COMPARATOR_NOT_EQUALS + toVirtualMachineNumber("18"));
		assertEquals(jooLines[line++], "" + ifTestName + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + ifTestName + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF + intTest0Name + JooVirtualMachine.COMPARATOR_NOT_EQUALS + intTest1Name);
		assertEquals(jooLines[line++], "" + ifTestName + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + intTestName + toIndex(2) + JooVirtualMachine.OPERATOR_ASSIGN + toVirtualMachineNumber("100"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF + intTest0Name + JooVirtualMachine.COMPARATOR_SMALLER + intTestName + toIndex(2));
		assertEquals(jooLines[line++], "" + ifTestName + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF + intTestName + toIndex(2) + JooVirtualMachine.COMPARATOR_BIGGER + intTest0Name);
		assertEquals(jooLines[line++], "" + ifTestName + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF + intTest0Name + JooVirtualMachine.COMPARATOR_SMALLER_EQUALS + intTestName + toIndex(2));
		assertEquals(jooLines[line++], "" + ifTestName + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF + intTestName + toIndex(2) + JooVirtualMachine.COMPARATOR_BIGGER_EQUALS + intTest0Name);
		assertEquals(jooLines[line++], "" + ifTestName + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + intTestName + toIndex(3) + JooVirtualMachine.OPERATOR_ASSIGN + intTestName + toIndex(2));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF + intTestName + toIndex(2) + JooVirtualMachine.COMPARATOR_BIGGER_EQUALS + intTestName + toIndex(3));
		assertEquals(jooLines[line++], "" + ifTestName + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF + intTestName + toIndex(2) + JooVirtualMachine.COMPARATOR_SMALLER_EQUALS + intTestName + toIndex(3));
		assertEquals(jooLines[line++], "" + ifTestName + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + functionTestName + JooVirtualMachine.KEYWORD_PARAMETER + intTest0Name + JooVirtualMachine.KEYWORD_PARAMETER + intTestName);
		assertEquals(jooLines[line++], "" + libraryTestName + JooVirtualMachine.KEYWORD_PARAMETER + intTestName);
		assertEquals(jooLines[line++], "" + directoryLibraryTestName + JooVirtualMachine.KEYWORD_PARAMETER + intTestName);		
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_FUNCTION + functionTestName);
		assertEquals(jooLines[line++], "" + fixedTest1Name + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("" + Math.round(25f * 255)));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF + fixedTest1Name + JooVirtualMachine.COMPARATOR_SMALLER_EQUALS + toVirtualMachineNumber("" + Math.round(80f * 255)));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_FUNCTION_REPEAT);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + paramTest0Name + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("100"));
		assertEquals(jooLines[line++], "" + paramTest1Name + toIndex(5) + JooVirtualMachine.OPERATOR_ASSIGN + paramTest0Name);
		assertEquals(jooLines[line++], "" + paramTest1Name + toIndex(4) + JooVirtualMachine.OPERATOR_ASSIGN + paramTest1Name + toIndex(5));
		assertEquals(jooLines[line++], "" + paramTest0Name + JooVirtualMachine.OPERATOR_ADD + paramTest1Name + toIndex(4));
		assertEquals(jooLines[line++], "" + paramTest1Name + intTest1Name + JooVirtualMachine.OPERATOR_ASSIGN + intTest1Name);
		assertEquals(jooLines[line++], "" + paramTest0Name + JooVirtualMachine.OPERATOR_SUBTRACT + paramTest1Name + intTest1Name);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF + paramTest0Name + JooVirtualMachine.COMPARATOR_SMALLER + paramTest1Name + toIndex(5));
		assertEquals(jooLines[line++], "" + ifTestName + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("1"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + ifTestName + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("2"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_FUNCTION + libraryTestName);
		assertEquals(jooLines[line++], "" + paramTest0Name + toIndex(0) + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("10"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_FUNCTION + directoryLibraryTestName);
		assertEquals(jooLines[line++], "" + paramTest0Name + toIndex(0) + JooVirtualMachine.OPERATOR_ADD + toVirtualMachineNumber("20"));
	}
	
	@Test
	public void parseVariablesTest() throws Exception {
		final JooCompiler jooCompiler = new JooCompiler();
		jooCompiler.compile("TestCode.joo");
		final Map<String, Variable>[] variables = jooCompiler.getVariables();
		
		int type = 0;
		assert(variables[type].get("intTest0").getByteCodeName() == 1);
		assert(variables[type].get("intTest1").getByteCodeName() == 2);
		assert(variables[type].get("ifTest").getByteCodeName() == 3);
		assertEquals(variables[type].get("intTest0").getValue(), "");
		assertEquals(variables[type].get("intTest1").getValue(), "10");
		assertEquals(variables[type].get("ifTest").getValue(), "");
		type++;
		assert(variables[type].get("fixedTest0").getByteCodeName() == 4);
		assert(variables[type].get("fixedTest1").getByteCodeName() == 5);
		assertEquals(variables[type].get("fixedTest0").getValue(), "");
		assertEquals(variables[type].get("fixedTest1").getValue(), "" + Math.round(100.5f * 255));
		type++;
		assert(variables[type].get("boolTest0").getByteCodeName() == 6);
		assert(variables[type].get("boolTest1").getByteCodeName() == 7);
		assert(variables[type].get("boolTest2").getByteCodeName() == 8);
		assertEquals(variables[type].get("boolTest0").getValue(), "");
		assertEquals(variables[type].get("boolTest1").getValue(), "1");
		assertEquals(variables[type].get("boolTest2").getValue(), "0");
		type++;
		assert(variables[type].get("charTest0").getByteCodeName() == 9);
		assert(variables[type].get("charTest1").getByteCodeName() == 10);
		assert(variables[type].get("charTest2").getByteCodeName() == 11);
		assertEquals(variables[type].get("charTest0").getValue(), "");
		assertEquals(variables[type].get("charTest1").getValue(), "A");	
		assertEquals(variables[type].get("charTest2").getValue(), "C");	
		type++;
		assert(variables[type].get("intTest").getByteCodeName() == 12);
		assertEquals(variables[type].get("intTest").getValue(), "10");
		type++;
		assert(variables[type].get("fixedTest").getByteCodeName() == 13);
		assertEquals(variables[type].get("fixedTest").getValue(), "5");
		type++;
		assert(variables[type].get("boolTest").getByteCodeName() == 14);
		assertEquals(variables[type].get("boolTest").getValue(), "15");
		type++;
		assert(variables[type].get("charTest").getByteCodeName() == 15);
		assertEquals(variables[type].get("charTest").getValue(), "13");			
	}
	
	@Test
	public void parseFunctionsTest() throws Exception {
		final JooCompiler jooCompiler = new JooCompiler();
		jooCompiler.compile("TestCode.joo");
		final Map<String, Function> functions = jooCompiler.getFunctions();
		
		final Function start = functions.get("Start");
		final Function functionTest = functions.get("FunctionTest");
		
		assert(start.getByteCodeName() == 16);
		assertEquals(start.getParameters().size(), 0);
		
		assert(functionTest.getByteCodeName() == 17);
		assert(functionTest.getParameters().containsKey("_paramTest0"));
		assert(functionTest.getParameters().containsKey("_paramTest1"));
		assertEquals(functionTest.getParameters().get("_paramTest0"), "" + TYPE_INT);
		assertEquals(functionTest.getParameters().get("_paramTest1"), "" + TYPE_ARRAY_INT);
		
		int currentInstructionIndex = 0;
		Instruction currentInstruction = null;
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "intTest0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "100");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "intTest0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_SUBTRACT);
		assertEquals(currentInstruction.getVariable1Name(), "intTest1");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "intTest0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_MULTIPLY);
		assertEquals(currentInstruction.getValue(), "2");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "intTest0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_DIVIDE);
		assertEquals(currentInstruction.getValue(), "10");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "intTest1");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(), "6");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixedTest0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getVariable1Name(), "fixedTest1");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixedTest0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_SUBTRACT);
		assertEquals(currentInstruction.getValue(), "" + Math.round(0.5 * 255));
		assertEquals(currentInstruction.getValueType(), TYPE_FIXED);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixedTest0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_MULTIPLY);
		assertEquals(currentInstruction.getValue(), "" + Math.round(2.5 * 255));
		assertEquals(currentInstruction.getValueType(), TYPE_FIXED);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixedTest0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_DIVIDE);
		assertEquals(currentInstruction.getValue(), "" + Math.round(5 * 255));
		assertEquals(currentInstruction.getValueType(), TYPE_FIXED);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixedTest1");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(), "" + Math.round(50 * 255));
		assertEquals(currentInstruction.getValueType(), TYPE_FIXED);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "boolTest0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_BOOL);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "boolTest1");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(), "0");
		assertEquals(currentInstruction.getValueType(), TYPE_BOOL);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "boolTest2");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getVariable1Name(), "boolTest1");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "charTest0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(), "A");
		assertEquals(currentInstruction.getValueType(), TYPE_CHAR);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "charTest1");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(), "B");
		assertEquals(currentInstruction.getValueType(), TYPE_CHAR);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "charTest2");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getVariable1Name(), "charTest1");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "intTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(), "30");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "intTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "1");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "15");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "intTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_SUBTRACT);
		assertEquals(currentInstruction.getVariable1Name(), "intTest1");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "intTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "1");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_DIVIDE);
		assertEquals(currentInstruction.getValue(), "5");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "intTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_MULTIPLY);
		assertEquals(currentInstruction.getVariable1Name(), "intTest");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "1");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "intTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "7");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(), "25");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixedTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(),  "" + Math.round(60.5 * 255));
		assertEquals(currentInstruction.getValueType(), TYPE_FIXED);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixedTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "1");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(),  "" + Math.round(15 * 255));
		assertEquals(currentInstruction.getValueType(), TYPE_FIXED);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixedTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_SUBTRACT);
		assertEquals(currentInstruction.getVariable1Name(), "fixedTest1");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixedTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "1");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_DIVIDE);
		assertEquals(currentInstruction.getValue(),  "" + Math.round(5 * 255));
		assertEquals(currentInstruction.getValueType(), TYPE_FIXED);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixedTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_MULTIPLY);
		assertEquals(currentInstruction.getVariable1Name(), "fixedTest");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "1");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixedTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "2");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN_NEGATIVE);
		assertEquals(currentInstruction.getValue(),  "" + Math.round(10 * 255));
		assertEquals(currentInstruction.getValueType(), TYPE_FIXED);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixedTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "3");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN_POSITIVE);
		assertEquals(currentInstruction.getVariable1Name(), "fixedTest");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "2");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixedTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "4");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN_INVERSE);
		assertEquals(currentInstruction.getVariable1Name(), "fixedTest");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "3");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixedTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "5");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(),  "" + Math.round(25.25 * 255));
		assertEquals(currentInstruction.getValueType(), TYPE_FIXED);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "boolTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "9");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getVariable1Name(), "boolTest0");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "boolTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "10");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_BOOL);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "boolTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "11");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getVariable1Name(), "boolTest");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "10");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "boolTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "2");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN_NEGATIVE);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_BOOL);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "boolTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "3");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN_POSITIVE);
		assertEquals(currentInstruction.getVariable1Name(), "boolTest");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "2");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "boolTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "4");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN_INVERSE);
		assertEquals(currentInstruction.getVariable1Name(), "boolTest");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "3");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "boolTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "5");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_BOOL);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "charTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "9");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getVariable1Name(), "charTest0");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "charTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "10");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(), "C");
		assertEquals(currentInstruction.getValueType(), TYPE_CHAR);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "charTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "11");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getVariable1Name(), "charTest");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "10");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "charTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(), "d");
		assertEquals(currentInstruction.getValueType(), TYPE_CHAR);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isCondition(), true);
		assertEquals(currentInstruction.getVariable0Name(), "intTest0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.COMPARATOR_EQUALS);
		assertEquals(currentInstruction.getValue(), "18");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "ifTest");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isCondition(), true);
		assertEquals(currentInstruction.getVariable0Name(), "intTest0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.COMPARATOR_NOT_EQUALS);
		assertEquals(currentInstruction.getValue(), "18");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "ifTest");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_ELSE);

		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "ifTest");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "2");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_IF);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "ifTest");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_ELSE);

		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "ifTest");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "2");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_IF);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isCondition(), true);
		assertEquals(currentInstruction.getVariable0Name(), "intTest0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.COMPARATOR_NOT_EQUALS);
		assertEquals(currentInstruction.getVariable1Name(), "intTest1");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "ifTest");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_ELSE);

		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "ifTest");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "2");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_IF);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "intTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "2");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(), "100");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isCondition(), true);
		assertEquals(currentInstruction.getVariable0Name(), "intTest0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.COMPARATOR_SMALLER);
		assertEquals(currentInstruction.getVariable1Name(), "intTest");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "2");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "ifTest");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_ELSE);

		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "ifTest");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "2");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_IF);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isCondition(), true);
		assertEquals(currentInstruction.getVariable0Name(), "intTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "2");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.COMPARATOR_BIGGER);
		assertEquals(currentInstruction.getVariable1Name(), "intTest0");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "ifTest");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_ELSE);

		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "ifTest");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "2");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_IF);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isCondition(), true);
		assertEquals(currentInstruction.getVariable0Name(), "intTest0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.COMPARATOR_SMALLER_EQUALS);
		assertEquals(currentInstruction.getVariable1Name(), "intTest");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "2");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "ifTest");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_ELSE);

		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "ifTest");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "2");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_IF);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isCondition(), true);
		assertEquals(currentInstruction.getVariable0Name(), "intTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "2");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.COMPARATOR_BIGGER_EQUALS);
		assertEquals(currentInstruction.getVariable1Name(), "intTest0");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "ifTest");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_ELSE);

		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "ifTest");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "2");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_IF);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "intTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "3");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getVariable1Name(), "intTest");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "2");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isCondition(), true);
		assertEquals(currentInstruction.getVariable0Name(), "intTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "2");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.COMPARATOR_BIGGER_EQUALS);
		assertEquals(currentInstruction.getVariable1Name(), "intTest");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "3");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "ifTest");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_ELSE);

		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "ifTest");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "2");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_IF);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isCondition(), true);
		assertEquals(currentInstruction.getVariable0Name(), "intTest");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "2");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.COMPARATOR_SMALLER_EQUALS);
		assertEquals(currentInstruction.getVariable1Name(), "intTest");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "3");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "ifTest");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_ELSE);

		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "ifTest");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "2");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_IF);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isFunctionCall(), true);
		assertEquals(currentInstruction.getFunctionName(), "FunctionTest");
		assertEquals(currentInstruction.getParameters().get(0), "intTest0");
		assertEquals(currentInstruction.getParameters().get(1), "intTest");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isFunctionCall(), true);
		assertEquals(currentInstruction.getFunctionName(), "TestLibrary_FunctionTest");
		assertEquals(currentInstruction.getParameters().get(0), "intTest");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isFunctionCall(), true);
		assertEquals(currentInstruction.getFunctionName(), "TestDirectoryLibrary_FunctionTest");
		assertEquals(currentInstruction.getParameters().get(0), "intTest");
		
		currentInstructionIndex = 0;
		
		currentInstruction = functionTest.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixedTest1");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "" + Math.round(25 * 255));
		assertEquals(currentInstruction.getValueType(), TYPE_FIXED);
		
		currentInstruction = functionTest.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isCondition(), true);
		assertEquals(currentInstruction.getVariable0Name(), "fixedTest1");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.COMPARATOR_SMALLER_EQUALS);
		assertEquals(currentInstruction.getValue(),  "" + Math.round(80 * 255));
		assertEquals(currentInstruction.getValueType(), TYPE_FIXED);
		
		currentInstruction = functionTest.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_FUNCTION_REPEAT);
		
		currentInstruction = functionTest.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_IF);
		
		currentInstruction = functionTest.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "_paramTest0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "100");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = functionTest.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "_paramTest1");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "5");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getVariable1Name(), "_paramTest0");
		
		currentInstruction = functionTest.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "_paramTest1");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "4");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getVariable1Name(), "_paramTest1");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "5");
		
		currentInstruction = functionTest.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "_paramTest0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getVariable1Name(), "_paramTest1");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "4");
		
		currentInstruction = functionTest.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "_paramTest1");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "intTest1");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getVariable1Name(), "intTest1");
		
		currentInstruction = functionTest.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "_paramTest0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_SUBTRACT);
		assertEquals(currentInstruction.getVariable1Name(), "_paramTest1");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "intTest1");
		
		currentInstruction = functionTest.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isCondition(), true);
		assertEquals(currentInstruction.getVariable0Name(), "_paramTest0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.COMPARATOR_SMALLER);
		assertEquals(currentInstruction.getVariable1Name(), "_paramTest1");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "5");
		
		currentInstruction = functionTest.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "ifTest");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = functionTest.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_ELSE);

		currentInstruction = functionTest.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "ifTest");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "2");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = functionTest.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_IF);
	}
	
	private char toIndex(int i) {
		return (char) (i + JooVirtualMachine.ARRAY_INDEXES_START);
	}
	
	private String toVirtualMachineNumber(String value) {
		for (int i = 0; i < 9; i++) {
			value = value.replace((char)('0' + i), (char)(JooVirtualMachine.NUMBER_0 + i));
		}
		return value;
	}
}
