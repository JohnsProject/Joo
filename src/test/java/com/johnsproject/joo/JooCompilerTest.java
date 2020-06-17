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

		final char int0 = 0 + JooVirtualMachine.COMPONENTS_START;
		final char int1 = 1 + JooVirtualMachine.COMPONENTS_START;
		final char correctIfs = 2 + JooVirtualMachine.COMPONENTS_START;
		final char fixed0 = 3 + JooVirtualMachine.COMPONENTS_START;
		final char fixed1 = 4 + JooVirtualMachine.COMPONENTS_START;
		final char bool0 = 5 + JooVirtualMachine.COMPONENTS_START;
		final char bool1 = 6 + JooVirtualMachine.COMPONENTS_START;
		final char bool2 = 7 + JooVirtualMachine.COMPONENTS_START;
		final char char0 = 8 + JooVirtualMachine.COMPONENTS_START;
		final char char1 = 9 + JooVirtualMachine.COMPONENTS_START;
		final char char2 = 10 + JooVirtualMachine.COMPONENTS_START;
		
		final char intArray = 11 + JooVirtualMachine.COMPONENTS_START;
		final char fixedArray = 12 + JooVirtualMachine.COMPONENTS_START;
		final char boolArray = 13 + JooVirtualMachine.COMPONENTS_START;
		final char charArray = 14 + JooVirtualMachine.COMPONENTS_START;
		
		final char start = 15 + JooVirtualMachine.COMPONENTS_START;
		final char function = 16 + JooVirtualMachine.COMPONENTS_START;
		final char libraryFunction = 17 + JooVirtualMachine.COMPONENTS_START;
		final char directoryLibraryFunction = 18 + JooVirtualMachine.COMPONENTS_START;
		
		final char param0 = 0 + JooVirtualMachine.PARAMETERS_START;
		final char param1 = 1 + JooVirtualMachine.PARAMETERS_START;

		int line = 0;
		assertEquals(jooLines[line++], "" + JooVirtualMachine.TYPE_INT + (char)3);
		assertEquals(jooLines[line++], "" + int0);
		assertEquals(jooLines[line++], "" + int1 + toBytecodeNumber("10"));
		assertEquals(jooLines[line++], "" + correctIfs);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.TYPE_FIXED + (char)2);
		assertEquals(jooLines[line++], "" + fixed0);
		assertEquals(jooLines[line++], "" + fixed1 + toBytecodeNumber("" + Math.round(100.5f * 255)));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.TYPE_BOOL + (char)3);
		assertEquals(jooLines[line++], "" + bool0);
		assertEquals(jooLines[line++], "" + bool1 + toBytecodeNumber("1"));
		assertEquals(jooLines[line++], "" + bool2 + toBytecodeNumber("0"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.TYPE_CHAR + (char)3);
		assertEquals(jooLines[line++], "" + char0);
		assertEquals(jooLines[line++], "" + char1 + 'A');
		assertEquals(jooLines[line++], "" + char2 + 'C');
		assertEquals(jooLines[line++], "" + JooVirtualMachine.TYPE_ARRAY_INT + (char)1);
		assertEquals(jooLines[line++], "" + intArray + (char)10);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.TYPE_ARRAY_FIXED + (char)1);
		assertEquals(jooLines[line++], "" + fixedArray + (char)5);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.TYPE_ARRAY_BOOL + (char)1);
		assertEquals(jooLines[line++], "" + boolArray + (char)15);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.TYPE_ARRAY_CHAR + (char)1);
		assertEquals(jooLines[line++], "" + charArray + (char)13);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.TYPE_FUNCTION + (char)4);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_FUNCTION + start);
		assertEquals(jooLines[line++], "" + int0 + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("100"));
		assertEquals(jooLines[line++], "" + int0 + JooVirtualMachine.OPERATOR_SUBTRACT + int1);
		assertEquals(jooLines[line++], "" + int0 + JooVirtualMachine.OPERATOR_MULTIPLY + toBytecodeNumber("2"));
		assertEquals(jooLines[line++], "" + int0 + JooVirtualMachine.OPERATOR_DIVIDE + toBytecodeNumber("10"));
		assertEquals(jooLines[line++], "" + int1 + JooVirtualMachine.OPERATOR_ASSIGN + toBytecodeNumber("6"));
		assertEquals(jooLines[line++], "" + fixed0 + JooVirtualMachine.OPERATOR_ADD + fixed1);
		assertEquals(jooLines[line++], "" + fixed0 + JooVirtualMachine.OPERATOR_SUBTRACT + toBytecodeNumber("" + Math.round(0.5f * 255)));
		assertEquals(jooLines[line++], "" + fixed0 + JooVirtualMachine.OPERATOR_MULTIPLY + toBytecodeNumber("" + Math.round(2.5f * 255)));
		assertEquals(jooLines[line++], "" + fixed0 + JooVirtualMachine.OPERATOR_DIVIDE + toBytecodeNumber("" + Math.round(5f * 255)));
		assertEquals(jooLines[line++], "" + fixed1 + JooVirtualMachine.OPERATOR_ASSIGN + toBytecodeNumber("" + Math.round(50f * 255)));
		assertEquals(jooLines[line++], "" + bool0 + JooVirtualMachine.OPERATOR_ASSIGN + toBytecodeNumber("1"));
		assertEquals(jooLines[line++], "" + bool1 + JooVirtualMachine.OPERATOR_ASSIGN + toBytecodeNumber("0"));
		assertEquals(jooLines[line++], "" + bool2 + JooVirtualMachine.OPERATOR_ASSIGN + bool1);
		assertEquals(jooLines[line++], "" + char0 + JooVirtualMachine.OPERATOR_ASSIGN + JooVirtualMachine.TYPE_CHAR + 'A');
		assertEquals(jooLines[line++], "" + char1 + JooVirtualMachine.OPERATOR_ASSIGN + JooVirtualMachine.TYPE_CHAR + 'B');
		assertEquals(jooLines[line++], "" + char2 + JooVirtualMachine.OPERATOR_ASSIGN + char1);
		assertEquals(jooLines[line++], "" + intArray + toIndex(0) + JooVirtualMachine.OPERATOR_ASSIGN + toBytecodeNumber("30"));
		assertEquals(jooLines[line++], "" + intArray + toIndex(1) + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("15"));
		assertEquals(jooLines[line++], "" + intArray + toIndex(0) + JooVirtualMachine.OPERATOR_SUBTRACT + int1);
		assertEquals(jooLines[line++], "" + intArray + toIndex(1) + JooVirtualMachine.OPERATOR_DIVIDE + toBytecodeNumber("5"));
		assertEquals(jooLines[line++], "" + intArray + toIndex(0) + JooVirtualMachine.OPERATOR_MULTIPLY + intArray + toIndex(1));
		assertEquals(jooLines[line++], "" + intArray + toIndex(7) + JooVirtualMachine.OPERATOR_ASSIGN + toBytecodeNumber("25"));
		assertEquals(jooLines[line++], "" + fixedArray + toIndex(0) + JooVirtualMachine.OPERATOR_ASSIGN + toBytecodeNumber("" + Math.round(60.5f * 255)));
		assertEquals(jooLines[line++], "" + fixedArray + toIndex(1) + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("" + Math.round(15f * 255)));
		assertEquals(jooLines[line++], "" + fixedArray + toIndex(0) + JooVirtualMachine.OPERATOR_SUBTRACT + fixed1);
		assertEquals(jooLines[line++], "" + fixedArray + toIndex(1) + JooVirtualMachine.OPERATOR_DIVIDE + toBytecodeNumber("" + Math.round(5f * 255)));
		assertEquals(jooLines[line++], "" + fixedArray + toIndex(0) + JooVirtualMachine.OPERATOR_MULTIPLY + fixedArray + toIndex(1));
		assertEquals(jooLines[line++], "" + fixedArray + toIndex(2) + JooVirtualMachine.OPERATOR_ASSIGN_NEGATIVE + toBytecodeNumber("" + Math.round(10f * 255)));
		assertEquals(jooLines[line++], "" + fixedArray + toIndex(3) + JooVirtualMachine.OPERATOR_ASSIGN_POSITIVE + fixedArray + toIndex(2));
		assertEquals(jooLines[line++], "" + fixedArray + toIndex(4) + JooVirtualMachine.OPERATOR_ASSIGN_INVERSE + fixedArray + toIndex(3));
		assertEquals(jooLines[line++], "" + fixedArray + toIndex(5) + JooVirtualMachine.OPERATOR_ASSIGN + toBytecodeNumber("" + Math.round(25.25f * 255)));
		assertEquals(jooLines[line++], "" + boolArray + toIndex(9) + JooVirtualMachine.OPERATOR_ASSIGN + bool0);
		assertEquals(jooLines[line++], "" + boolArray + toIndex(10) + JooVirtualMachine.OPERATOR_ASSIGN + toBytecodeNumber("1"));
		assertEquals(jooLines[line++], "" + boolArray + toIndex(11) + JooVirtualMachine.OPERATOR_ASSIGN + boolArray + toIndex(10));
		assertEquals(jooLines[line++], "" + boolArray + toIndex(2) + JooVirtualMachine.OPERATOR_ASSIGN_NEGATIVE + toBytecodeNumber("1"));	
		assertEquals(jooLines[line++], "" + boolArray + toIndex(3) + JooVirtualMachine.OPERATOR_ASSIGN_POSITIVE + boolArray + toIndex(2));	
		assertEquals(jooLines[line++], "" + boolArray + toIndex(4) + JooVirtualMachine.OPERATOR_ASSIGN_INVERSE + boolArray + toIndex(3));	
		assertEquals(jooLines[line++], "" + boolArray + toIndex(5) + JooVirtualMachine.OPERATOR_ASSIGN + toBytecodeNumber("1"));
		assertEquals(jooLines[line++], "" + charArray + toIndex(9) + JooVirtualMachine.OPERATOR_ASSIGN + char0);
		assertEquals(jooLines[line++], "" + charArray + toIndex(10) + JooVirtualMachine.OPERATOR_ASSIGN + JooVirtualMachine.TYPE_CHAR + 'C');
		assertEquals(jooLines[line++], "" + charArray + toIndex(11) + JooVirtualMachine.OPERATOR_ASSIGN + charArray + toIndex(10));
		assertEquals(jooLines[line++], "" + charArray + toIndex(0) + JooVirtualMachine.OPERATOR_ASSIGN + JooVirtualMachine.TYPE_CHAR + 'd');
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF + int0 + JooVirtualMachine.COMPARATOR_EQUALS + toBytecodeNumber("18"));
		assertEquals(jooLines[line++], "" + correctIfs + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("1"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF + int1 + JooVirtualMachine.COMPARATOR_EQUALS + toBytecodeNumber("6"));
		assertEquals(jooLines[line++], "" + correctIfs + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("1"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + correctIfs + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("2"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + correctIfs + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("1"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + correctIfs + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("2"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF + bool0 + JooVirtualMachine.COMPARATOR_EQUALS + toBytecodeNumber("0"));
		assertEquals(jooLines[line++], "" + correctIfs + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("1"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_ELSE_IF + bool0 + JooVirtualMachine.COMPARATOR_EQUALS + toBytecodeNumber("1"));
		assertEquals(jooLines[line++], "" + correctIfs + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("2"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF + int0 + JooVirtualMachine.COMPARATOR_NOT_EQUALS + int1);
		assertEquals(jooLines[line++], "" + correctIfs + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("1"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + correctIfs + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("2"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + intArray + toIndex(2) + JooVirtualMachine.OPERATOR_ASSIGN + toBytecodeNumber("100"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF + int0 + JooVirtualMachine.COMPARATOR_SMALLER + intArray + toIndex(2));
		assertEquals(jooLines[line++], "" + correctIfs + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("1"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + correctIfs + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("2"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF + intArray + toIndex(2) + JooVirtualMachine.COMPARATOR_BIGGER + int0);
		assertEquals(jooLines[line++], "" + correctIfs + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("1"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + correctIfs + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("2"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF + int0 + JooVirtualMachine.COMPARATOR_SMALLER_EQUALS + intArray + toIndex(2));
		assertEquals(jooLines[line++], "" + correctIfs + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("1"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + correctIfs + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("2"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF + intArray + toIndex(2) + JooVirtualMachine.COMPARATOR_BIGGER_EQUALS + int0);
		assertEquals(jooLines[line++], "" + correctIfs + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("1"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + correctIfs + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("2"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + intArray + toIndex(3) + JooVirtualMachine.OPERATOR_ASSIGN + intArray + toIndex(2));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF + intArray + toIndex(2) + JooVirtualMachine.COMPARATOR_BIGGER_EQUALS + intArray + toIndex(3));
		assertEquals(jooLines[line++], "" + correctIfs + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("1"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + correctIfs + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("2"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF + intArray + toIndex(2) + JooVirtualMachine.COMPARATOR_SMALLER_EQUALS + intArray + toIndex(3));
		assertEquals(jooLines[line++], "" + correctIfs + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("1"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + correctIfs + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("2"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + function + JooVirtualMachine.KEYWORD_PARAMETER + int0 + JooVirtualMachine.KEYWORD_PARAMETER + intArray);
		assertEquals(jooLines[line++], "" + libraryFunction + JooVirtualMachine.KEYWORD_PARAMETER + intArray);
		assertEquals(jooLines[line++], "" + directoryLibraryFunction + JooVirtualMachine.KEYWORD_PARAMETER + intArray);		
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_FUNCTION + function);
		assertEquals(jooLines[line++], "" + fixed1 + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("" + Math.round(25f * 255)));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF + fixed1 + JooVirtualMachine.COMPARATOR_SMALLER_EQUALS + toBytecodeNumber("" + Math.round(80f * 255)));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_FUNCTION_REPEAT);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + param0 + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("100"));
		assertEquals(jooLines[line++], "" + param1 + toIndex(5) + JooVirtualMachine.OPERATOR_ASSIGN + param0);
		assertEquals(jooLines[line++], "" + param1 + toIndex(4) + JooVirtualMachine.OPERATOR_ASSIGN + param1 + toIndex(5));
		assertEquals(jooLines[line++], "" + param0 + JooVirtualMachine.OPERATOR_ADD + param1 + toIndex(4));
		assertEquals(jooLines[line++], "" + param1 + int1 + JooVirtualMachine.OPERATOR_ASSIGN + int1);
		assertEquals(jooLines[line++], "" + param0 + JooVirtualMachine.OPERATOR_SUBTRACT + param1 + int1);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF + param0 + JooVirtualMachine.COMPARATOR_SMALLER + param1 + toIndex(5));
		assertEquals(jooLines[line++], "" + correctIfs + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("1"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_ELSE);
		assertEquals(jooLines[line++], "" + correctIfs + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("2"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_IF);
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_FUNCTION + libraryFunction);
		assertEquals(jooLines[line++], "" + param0 + toIndex(0) + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("10"));
		assertEquals(jooLines[line++], "" + JooVirtualMachine.KEYWORD_FUNCTION + directoryLibraryFunction);
		assertEquals(jooLines[line++], "" + param0 + toIndex(0) + JooVirtualMachine.OPERATOR_ADD + toBytecodeNumber("20"));
	}
	
	@Test
	public void parseVariablesTest() throws Exception {
		final JooCompiler jooCompiler = new JooCompiler();
		jooCompiler.compile("TestCode.joo");
		final Map<String, Variable>[] variables = jooCompiler.getVariables();
		
		int type = 0;
		assert(variables[type].get("int0").getByteCodeName() == 1);
		assert(variables[type].get("int1").getByteCodeName() == 2);
		assert(variables[type].get("correctIfs").getByteCodeName() == 3);
		assertEquals(variables[type].get("int0").getValue(), "");
		assertEquals(variables[type].get("int1").getValue(), "10");
		assertEquals(variables[type].get("correctIfs").getValue(), "");
		type++;
		assert(variables[type].get("fixed0").getByteCodeName() == 4);
		assert(variables[type].get("fixed1").getByteCodeName() == 5);
		assertEquals(variables[type].get("fixed0").getValue(), "");
		assertEquals(variables[type].get("fixed1").getValue(), "" + Math.round(100.5f * 255));
		type++;
		assert(variables[type].get("bool0").getByteCodeName() == 6);
		assert(variables[type].get("bool1").getByteCodeName() == 7);
		assert(variables[type].get("bool2").getByteCodeName() == 8);
		assertEquals(variables[type].get("bool0").getValue(), "");
		assertEquals(variables[type].get("bool1").getValue(), "1");
		assertEquals(variables[type].get("bool2").getValue(), "0");
		type++;
		assert(variables[type].get("char0").getByteCodeName() == 9);
		assert(variables[type].get("char1").getByteCodeName() == 10);
		assert(variables[type].get("char2").getByteCodeName() == 11);
		assertEquals(variables[type].get("char0").getValue(), "");
		assertEquals(variables[type].get("char1").getValue(), "A");	
		assertEquals(variables[type].get("char2").getValue(), "C");	
		type++;
		assert(variables[type].get("intArray").getByteCodeName() == 12);
		assertEquals(variables[type].get("intArray").getValue(), "10");
		type++;
		assert(variables[type].get("fixedArray").getByteCodeName() == 13);
		assertEquals(variables[type].get("fixedArray").getValue(), "5");
		type++;
		assert(variables[type].get("boolArray").getByteCodeName() == 14);
		assertEquals(variables[type].get("boolArray").getValue(), "15");
		type++;
		assert(variables[type].get("charArray").getByteCodeName() == 15);
		assertEquals(variables[type].get("charArray").getValue(), "13");			
	}
	
	@Test
	public void parseFunctionsTest() throws Exception {
		final JooCompiler jooCompiler = new JooCompiler();
		jooCompiler.compile("TestCode.joo");
		final Map<String, Function> functions = jooCompiler.getFunctions();
		
		final Function start = functions.get("Start");
		final Function function = functions.get("Function");
		
		assert(start.getByteCodeName() == 16);
		assertEquals(start.getParameters().size(), 0);
		
		assert(function.getByteCodeName() == 17);
		assert(function.getParameters().containsKey("_param0"));
		assert(function.getParameters().containsKey("_param1"));
		assertEquals(function.getParameters().get("_param0"), "" + TYPE_INT);
		assertEquals(function.getParameters().get("_param1"), "" + TYPE_ARRAY_INT);
		
		int currentInstructionIndex = 0;
		Instruction currentInstruction = null;
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "int0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "100");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "int0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_SUBTRACT);
		assertEquals(currentInstruction.getVariable1Name(), "int1");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "int0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_MULTIPLY);
		assertEquals(currentInstruction.getValue(), "2");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "int0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_DIVIDE);
		assertEquals(currentInstruction.getValue(), "10");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "int1");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(), "6");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixed0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getVariable1Name(), "fixed1");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixed0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_SUBTRACT);
		assertEquals(currentInstruction.getValue(), "" + Math.round(0.5 * 255));
		assertEquals(currentInstruction.getValueType(), TYPE_FIXED);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixed0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_MULTIPLY);
		assertEquals(currentInstruction.getValue(), "" + Math.round(2.5 * 255));
		assertEquals(currentInstruction.getValueType(), TYPE_FIXED);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixed0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_DIVIDE);
		assertEquals(currentInstruction.getValue(), "" + Math.round(5 * 255));
		assertEquals(currentInstruction.getValueType(), TYPE_FIXED);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixed1");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(), "" + Math.round(50 * 255));
		assertEquals(currentInstruction.getValueType(), TYPE_FIXED);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "bool0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_BOOL);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "bool1");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(), "0");
		assertEquals(currentInstruction.getValueType(), TYPE_BOOL);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "bool2");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getVariable1Name(), "bool1");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "char0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(), "A");
		assertEquals(currentInstruction.getValueType(), TYPE_CHAR);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "char1");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(), "B");
		assertEquals(currentInstruction.getValueType(), TYPE_CHAR);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "char2");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getVariable1Name(), "char1");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "intArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(), "30");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "intArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "1");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "15");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "intArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_SUBTRACT);
		assertEquals(currentInstruction.getVariable1Name(), "int1");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "intArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "1");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_DIVIDE);
		assertEquals(currentInstruction.getValue(), "5");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "intArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_MULTIPLY);
		assertEquals(currentInstruction.getVariable1Name(), "intArray");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "1");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "intArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "7");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(), "25");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixedArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(),  "" + Math.round(60.5 * 255));
		assertEquals(currentInstruction.getValueType(), TYPE_FIXED);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixedArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "1");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(),  "" + Math.round(15 * 255));
		assertEquals(currentInstruction.getValueType(), TYPE_FIXED);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixedArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_SUBTRACT);
		assertEquals(currentInstruction.getVariable1Name(), "fixed1");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixedArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "1");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_DIVIDE);
		assertEquals(currentInstruction.getValue(),  "" + Math.round(5 * 255));
		assertEquals(currentInstruction.getValueType(), TYPE_FIXED);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixedArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_MULTIPLY);
		assertEquals(currentInstruction.getVariable1Name(), "fixedArray");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "1");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixedArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "2");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN_NEGATIVE);
		assertEquals(currentInstruction.getValue(),  "" + Math.round(10 * 255));
		assertEquals(currentInstruction.getValueType(), TYPE_FIXED);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixedArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "3");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN_POSITIVE);
		assertEquals(currentInstruction.getVariable1Name(), "fixedArray");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "2");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixedArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "4");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN_INVERSE);
		assertEquals(currentInstruction.getVariable1Name(), "fixedArray");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "3");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixedArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "5");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(),  "" + Math.round(25.25 * 255));
		assertEquals(currentInstruction.getValueType(), TYPE_FIXED);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "boolArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "9");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getVariable1Name(), "bool0");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "boolArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "10");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_BOOL);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "boolArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "11");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getVariable1Name(), "boolArray");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "10");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "boolArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "2");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN_NEGATIVE);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_BOOL);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "boolArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "3");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN_POSITIVE);
		assertEquals(currentInstruction.getVariable1Name(), "boolArray");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "2");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "boolArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "4");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN_INVERSE);
		assertEquals(currentInstruction.getVariable1Name(), "boolArray");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "3");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "boolArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "5");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_BOOL);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "charArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "9");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getVariable1Name(), "char0");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "charArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "10");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(), "C");
		assertEquals(currentInstruction.getValueType(), TYPE_CHAR);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "charArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "11");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getVariable1Name(), "charArray");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "10");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "charArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(), "d");
		assertEquals(currentInstruction.getValueType(), TYPE_CHAR);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isCondition(), true);
		assertEquals(currentInstruction.getVariable0Name(), "int0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.COMPARATOR_EQUALS);
		assertEquals(currentInstruction.getValue(), "18");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "correctIfs");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isCondition(), true);
		assertEquals(currentInstruction.getVariable0Name(), "int1");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.COMPARATOR_EQUALS);
		assertEquals(currentInstruction.getValue(), "6");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "correctIfs");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_ELSE);

		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "correctIfs");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "2");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_IF);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "correctIfs");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_ELSE);

		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "correctIfs");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "2");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_IF);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isCondition(), true);
		assertEquals(currentInstruction.getConditionType(), KEYWORD_IF);
		assertEquals(currentInstruction.getVariable0Name(), "bool0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.COMPARATOR_EQUALS);
		assertEquals(currentInstruction.getValue(), "0");
		assertEquals(currentInstruction.getValueType(), TYPE_BOOL);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "correctIfs");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isCondition(), true);
		assertEquals(currentInstruction.getConditionType(), KEYWORD_ELSE_IF);
		assertEquals(currentInstruction.getVariable0Name(), "bool0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.COMPARATOR_EQUALS);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_BOOL);

		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "correctIfs");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "2");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_IF);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isCondition(), true);
		assertEquals(currentInstruction.getVariable0Name(), "int0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.COMPARATOR_NOT_EQUALS);
		assertEquals(currentInstruction.getVariable1Name(), "int1");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "correctIfs");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_ELSE);

		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "correctIfs");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "2");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_IF);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "intArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "2");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getValue(), "100");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isCondition(), true);
		assertEquals(currentInstruction.getVariable0Name(), "int0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.COMPARATOR_SMALLER);
		assertEquals(currentInstruction.getVariable1Name(), "intArray");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "2");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "correctIfs");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_ELSE);

		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "correctIfs");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "2");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_IF);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isCondition(), true);
		assertEquals(currentInstruction.getVariable0Name(), "intArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "2");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.COMPARATOR_BIGGER);
		assertEquals(currentInstruction.getVariable1Name(), "int0");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "correctIfs");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_ELSE);

		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "correctIfs");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "2");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_IF);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isCondition(), true);
		assertEquals(currentInstruction.getVariable0Name(), "int0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.COMPARATOR_SMALLER_EQUALS);
		assertEquals(currentInstruction.getVariable1Name(), "intArray");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "2");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "correctIfs");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_ELSE);

		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "correctIfs");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "2");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_IF);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isCondition(), true);
		assertEquals(currentInstruction.getVariable0Name(), "intArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "2");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.COMPARATOR_BIGGER_EQUALS);
		assertEquals(currentInstruction.getVariable1Name(), "int0");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "correctIfs");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_ELSE);

		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "correctIfs");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "2");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_IF);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "intArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "3");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getVariable1Name(), "intArray");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "2");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isCondition(), true);
		assertEquals(currentInstruction.getVariable0Name(), "intArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "2");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.COMPARATOR_BIGGER_EQUALS);
		assertEquals(currentInstruction.getVariable1Name(), "intArray");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "3");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "correctIfs");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_ELSE);

		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "correctIfs");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "2");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_IF);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isCondition(), true);
		assertEquals(currentInstruction.getVariable0Name(), "intArray");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "2");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.COMPARATOR_SMALLER_EQUALS);
		assertEquals(currentInstruction.getVariable1Name(), "intArray");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "3");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "correctIfs");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_ELSE);

		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "correctIfs");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "2");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_IF);
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isFunctionCall(), true);
		assertEquals(currentInstruction.getFunctionName(), "Function");
		assertEquals(currentInstruction.getParameters().get(0), "int0");
		assertEquals(currentInstruction.getParameters().get(1), "intArray");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isFunctionCall(), true);
		assertEquals(currentInstruction.getFunctionName(), "Library_Function");
		assertEquals(currentInstruction.getParameters().get(0), "intArray");
		
		currentInstruction = start.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isFunctionCall(), true);
		assertEquals(currentInstruction.getFunctionName(), "DirectoryLibrary_Function");
		assertEquals(currentInstruction.getParameters().get(0), "intArray");
		
		currentInstructionIndex = 0;
		
		currentInstruction = function.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "fixed1");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "" + Math.round(25 * 255));
		assertEquals(currentInstruction.getValueType(), TYPE_FIXED);
		
		currentInstruction = function.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isCondition(), true);
		assertEquals(currentInstruction.getVariable0Name(), "fixed1");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.COMPARATOR_SMALLER_EQUALS);
		assertEquals(currentInstruction.getValue(),  "" + Math.round(80 * 255));
		assertEquals(currentInstruction.getValueType(), TYPE_FIXED);
		
		currentInstruction = function.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_FUNCTION_REPEAT);
		
		currentInstruction = function.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_IF);
		
		currentInstruction = function.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "_param0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "100");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = function.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "_param1");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "5");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getVariable1Name(), "_param0");
		
		currentInstruction = function.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "_param1");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "4");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getVariable1Name(), "_param1");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "5");
		
		currentInstruction = function.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "_param0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getVariable1Name(), "_param1");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "4");
		
		currentInstruction = function.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "_param1");
		assertEquals(currentInstruction.getVariable0ArrayIndex(), "int1");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ASSIGN);
		assertEquals(currentInstruction.getVariable1Name(), "int1");
		
		currentInstruction = function.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "_param0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_SUBTRACT);
		assertEquals(currentInstruction.getVariable1Name(), "_param1");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "int1");
		
		currentInstruction = function.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.isCondition(), true);
		assertEquals(currentInstruction.getVariable0Name(), "_param0");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.COMPARATOR_SMALLER);
		assertEquals(currentInstruction.getVariable1Name(), "_param1");
		assertEquals(currentInstruction.getVariable1ArrayIndex(), "5");
		
		currentInstruction = function.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "correctIfs");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "1");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = function.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_ELSE);

		currentInstruction = function.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getVariable0Name(), "correctIfs");
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.OPERATOR_ADD);
		assertEquals(currentInstruction.getValue(), "2");
		assertEquals(currentInstruction.getValueType(), TYPE_INT);
		
		currentInstruction = function.getInstructions().get(currentInstructionIndex++);
		assertEquals(currentInstruction.getOperator(), JooVirtualMachine.KEYWORD_IF);
	}
	
	@Test
	public void splitStringTest() throws Exception {
		String string = "This is a test string";
		String[] stringComponents = new JooCompiler().splitCodeLine(string);
		assertEquals(stringComponents[0], "This");
		assertEquals(stringComponents[1], "is");
		assertEquals(stringComponents[2], "a");
		assertEquals(stringComponents[3], "test");
		assertEquals(stringComponents[4], "string");
	}
	
	private char toIndex(int i) {
		return (char) (i + JooVirtualMachine.ARRAY_INDEXES_START);
	}
	
	private String toBytecodeNumber(String value) {
		for (int i = 0; i < 9; i++) {
			value = value.replace((char)('0' + i), (char)(JooVirtualMachine.NUMBER_0 + i));
		}
		return value;
	}
}
