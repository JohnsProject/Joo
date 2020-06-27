package com.johnsproject.joo;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.johnsproject.joo.util.FileUtil;

import static com.johnsproject.joo.JooCompiler.*;
import static org.junit.Assert.assertEquals;

public class JooCompilerTest {

	@Test
	public void parseSettingsTest() throws Exception {
		final String settingsData = FileUtil.read(PATH_COMPILER_SETTINGS);
		final JooCompiler compiler = new JooCompiler();
		final Settings settings = compiler.parseSettings(settingsData);
		assert(settings.hasSettingWithName("<="));
		assert(settings.getSettingWithName("<=").hasName((byte)1));
		assert(settings.getSettingWithName("<=").hasSettingWithType(TYPE_INT));
		assert(settings.getSettingWithName("<=").hasSettingWithType(TYPE_FIXED));
		assert(settings.hasSettingWithName("=="));
		assert(settings.getSettingWithName("==").hasName((byte)5));
		assert(settings.getSettingWithName("==").hasSettingWithType(TYPE_INT));
		assert(settings.getSettingWithName("==").hasSettingWithType(TYPE_FIXED));
		assert(settings.getSettingWithName("==").hasSettingWithType(TYPE_BOOL));
		assert(settings.getSettingWithName("==").hasSettingWithType(TYPE_CHAR));
		assert(settings.hasSettingWithName("%"));
		assert(settings.getSettingWithName("%").hasName((byte)15));
		assert(settings.getSettingWithName("%").hasSettingWithType(TYPE_INT));

		assert(settings.hasSettingWithName("Graphics_HasOutputSupport"));
		assert(settings.getSettingWithName("Graphics_HasOutputSupport").hasName((byte)1));
		assert(settings.getSettingWithName("Graphics_HasOutputSupport").getSettingWithName("param0").hasSettingWithType(TYPE_BOOL));
		assert(settings.hasSettingWithName("Graphics_SetColor"));
		assert(settings.getSettingWithName("Graphics_SetColor").hasName((byte)2));
		assert(settings.getSettingWithName("Graphics_SetColor").getSettingWithName("param0").hasSettingWithType(TYPE_INT));
		assert(settings.getSettingWithName("Graphics_SetColor").getSettingWithName("param1").hasSettingWithType(TYPE_INT));
		assert(settings.getSettingWithName("Graphics_SetColor").getSettingWithName("param2").hasSettingWithType(TYPE_INT));
		assert(settings.hasSettingWithName("Math_Max"));
		assert(settings.getSettingWithName("Math_Max").hasName((byte)22));
		assert(settings.getSettingWithName("Math_Max").getSettingWithName("param0").hasSettingWithType(TYPE_INT));
		assert(settings.getSettingWithName("Math_Max").getSettingWithName("param0").hasSettingWithType(TYPE_FIXED));
		assert(settings.getSettingWithName("Math_Max").getSettingWithName("param1").hasSettingWithType(TYPE_INT));
		assert(settings.getSettingWithName("Math_Max").getSettingWithName("param1").hasSettingWithType(TYPE_FIXED));
		assert(settings.getSettingWithName("Math_Max").getSettingWithName("param2").hasSettingWithType(TYPE_INT));
		assert(settings.getSettingWithName("Math_Max").getSettingWithName("param2").hasSettingWithType(TYPE_FIXED));
		assert(settings.hasSettingWithName("Storage_Clear"));
		assert(settings.getSettingWithName("Storage_Clear").hasName((byte)35));
		assert(!settings.getSettingWithName("Storage_Clear").hasSettingWithName("param0"));
		
		ParseException exception = null;
		try {
			final String testSettings = "@OPERATOR" + "\n"
										+ "01 + int|fixed" + "\n"
										+ "02 + int|fixed";
			compiler.parseSettings(testSettings);
		} catch (ParseException e) {
			// if the assert is here the test will pass if no exception is thrown
			exception = e;
		}
		assertEquals(exception.getMessage(), "Duplicate compiler setting, Name: +");
		
		try {
			final String testSettings = "@FUNCTION" + "\n"
										+ "MyFunction";
			compiler.parseSettings(testSettings);
		} catch (ParseException e) {
			exception = e;
		}
		assertEquals(exception.getMessage(), "Invalid setting declaration");
		
		try {
			final String testSettings = "@OPERATOR" + "\n"
										+ "0A + int|fixed";
			compiler.parseSettings(testSettings);
		} catch (ParseException e) {
			exception = e;
		}
		assertEquals(exception.getMessage(), "Invalid byte code name, Name: 0A");
		
		try {
			final String testSettings = "@OPERATOR" + "\n"
										+ "01 + int|fixed A";
			compiler.parseSettings(testSettings);
		} catch (ParseException e) {
			exception = e;
		}
		assertEquals(exception.getMessage(), "Invalid operator declaration");
		
		try {
			final String testSettings = "@FUNCTION" + "\n"
										+ "01 MyFunction bug";
			compiler.parseSettings(testSettings);
		} catch (ParseException e) {
			exception = e;
		}
		assertEquals(exception.getMessage(), "Invalid supported type, Type: bug");
	}
	
	@Test
	public void parseVariableComponentTest() throws Exception {
		final JooCompiler compiler = new JooCompiler();
		String[] testCode;
		Code code;
		
		testCode = new String[] {"int", "test", "=", "10"};
		code = new Code();
		compiler.parseVariableComponent(code, testCode, 0);
		assert(code.hasComponentWithName("test"));
		assert(code.getComponentWithName("test").hasName((byte) 1));
		assert(code.getComponentWithName("test").hasType(TYPE_INT));
		assert(code.getComponentWithName("test").hasComponentWithName("10"));
		assert(code.getComponentWithName("test").getComponentWithName("10").hasType(TYPE_INT));
		
		testCode = new String[] {"fixed", "test", "=", "20.5"};
		code = new Code();
		compiler.parseVariableComponent(code, testCode, 0);
		assert(code.hasComponentWithName("test"));
		assert(code.getComponentWithName("test").hasName((byte) 2));
		assert(code.getComponentWithName("test").hasType(TYPE_FIXED));
		assert(code.getComponentWithName("test").hasComponentWithName("5228"));
		assert(code.getComponentWithName("test").getComponentWithName("5228").hasType(TYPE_FIXED));
		
		testCode = new String[] {"bool", "test", "=", "true"};
		code = new Code();
		compiler.parseVariableComponent(code, testCode, 0);
		assert(code.hasComponentWithName("test"));
		assert(code.getComponentWithName("test").hasName((byte) 3));
		assert(code.getComponentWithName("test").hasType(TYPE_BOOL));
		assert(code.getComponentWithName("test").hasComponentWithName("1"));
		assert(code.getComponentWithName("test").getComponentWithName("1").hasType(TYPE_BOOL));
		
		testCode = new String[] {"bool", "test", "=", "false"};
		code = new Code();
		compiler.parseVariableComponent(code, testCode, 0);
		assert(code.hasComponentWithName("test"));
		assert(code.getComponentWithName("test").hasName((byte) 4));
		assert(code.getComponentWithName("test").hasType(TYPE_BOOL));
		assert(code.getComponentWithName("test").hasComponentWithName("0"));
		assert(code.getComponentWithName("test").getComponentWithName("0").hasType(TYPE_BOOL));
		
		testCode = new String[] {"char", "test", "=", "'A'"};
		code = new Code();
		compiler.parseVariableComponent(code, testCode, 0);
		assert(code.hasComponentWithName("test"));
		assert(code.getComponentWithName("test").hasName((byte) 5));
		assert(code.getComponentWithName("test").hasType(TYPE_CHAR));
		assert(code.getComponentWithName("test").hasComponentWithName("65"));
		assert(code.getComponentWithName("test").getComponentWithName("65").hasType(TYPE_CHAR));
		
		testCode = new String[] {"int", "test"};
		code = new Code();
		compiler.parseVariableComponent(code, testCode, 0);
		assert(code.hasComponentWithName("test"));
		assert(code.getComponentWithName("test").hasName((byte) 6));
		assert(code.getComponentWithName("test").hasType(TYPE_INT));
		assert(!code.getComponentWithName("test").hasComponentWithName("10"));
		
		testCode = new String[] {"int:10", "test"};
		code = new Code();
		compiler.parseVariableComponent(code, testCode, 0);
		assert(code.hasComponentWithName("test"));
		assert(code.getComponentWithName("test").hasName((byte) 7));
		assert(code.getComponentWithName("test").hasType(TYPE_ARRAY_INT));
		assert(code.getComponentWithName("test").hasComponentWithName("10"));
		assert(code.getComponentWithName("test").getComponentWithName("10").hasType(TYPE_ARRAY_SIZE));
		
		ParseException exception = null;
		try {
			testCode = new String[] {"int", "test", "="};
			compiler.parseVariableComponent(code, testCode, 0);
		} catch (ParseException e) {
			// if the assert is here the test will pass if no exception is thrown
			exception = e;
		}
		assertEquals(exception.getMessage(), "Invalid variable declaration");
		
		try {
			testCode = new String[] {"int:A", "test"};
			compiler.parseVariableComponent(code, testCode, 0);
		} catch (ParseException e) {
			exception = e;
		}
		assertEquals(exception.getMessage(), "Invalid array size declaration, Size: A");
		
		try {
			testCode = new String[] {"int:15", "test", "=", "10"};
			compiler.parseVariableComponent(code, testCode, 0);
		} catch (ParseException e) {
			exception = e;
		}
		assertEquals(exception.getMessage(), "Invalid array declaration. Value can't be assigned to array");
		
		try {
			testCode = new String[] {"int", "test", "-", "10"};
			compiler.parseVariableComponent(code, testCode, 0);
		} catch (ParseException e) {
			exception = e;
		}
		assertEquals(exception.getMessage(), "Invalid assignment operator, Operator: -");
		
		try {
			testCode = new String[] {"char", "test", "=", "'A''"};
			compiler.parseVariableComponent(code, testCode, 0);
		} catch (ParseException e) {
			exception = e;
		}
		assertEquals(exception.getMessage(), "Invalid char value declaration");
		
		try {
			testCode = new String[] {"int", "test", "=", "A"};
			compiler.parseVariableComponent(code, testCode, 0);
		} catch (ParseException e) {
			exception = e;
		}
		assertEquals(exception.getMessage(), "The declared value type doesn't match the variable type");
	}
	
	@Test
	public void parseFunctionComponentTest() throws Exception {
		final JooCompiler compiler = new JooCompiler();
		String[] testCode;
		Code code;
		
		testCode = new String[] {"repeatFunction"};
		code = new Code();
		compiler.parseFunctionComponent(code, testCode, 0);
		assert(code.hasComponentWithName("repeatFunction"));
		assert(code.getComponentWithName("repeatFunction").hasType(KEYWORD_FUNCTION_REPEAT));
		
		testCode = new String[] {"endFunction"};
		code = new Code();
		compiler.parseFunctionComponent(code, testCode, 0);
		assert(code.hasComponentWithName("endFunction"));
		assert(code.getComponentWithName("endFunction").hasType(KEYWORD_FUNCTION_END));
		
		testCode = new String[] {"function", "Test"};
		code = new Code();
		compiler.parseFunctionComponent(code, testCode, 0);
		assert(code.hasComponentWithName("Test"));
		assert(code.getComponentWithName("Test").hasName((byte) 1));
		assert(code.getComponentWithName("Test").hasType(TYPE_FUNCTION));
		
		testCode = new String[] {"function", "Test", "int", "param0", "fixed", "param1"};
		code = new Code();
		compiler.parseFunctionComponent(code, testCode, 0);
		assert(code.hasComponentWithName("Test"));
		assert(code.getComponentWithName("Test").hasName((byte) 2));
		assert(code.getComponentWithName("Test").hasType(TYPE_FUNCTION));
		assert(code.getComponentWithName("Test").hasComponentWithName("param0"));
		assert(code.getComponentWithName("Test").getComponentWithName("param0").hasName((byte) 1));
		assert(code.getComponentWithName("Test").getComponentWithName("param0").hasType(TYPE_INT));
		assert(code.getComponentWithName("Test").hasComponentWithName("param1"));
		assert(code.getComponentWithName("Test").getComponentWithName("param1").hasName((byte) 2));
		assert(code.getComponentWithName("Test").getComponentWithName("param1").hasType(TYPE_FIXED));
		
		testCode = new String[] {"call", "Test"};
		code = new Code();
		compiler.parseFunctionComponent(code, testCode, 0);
		assert(code.hasComponentWithName("Test"));
		assert(code.getComponentWithName("Test").hasType(KEYWORD_FUNCTION_CALL));
		
		testCode = new String[] {"call", "Test", "variable0", "variable1"};
		code = new Code();
		code.addComponent(new CodeComponent("variable0", (byte) 1, TYPE_INT, (byte )JooVirtualMachine.TYPE_INT));
		code.addComponent(new CodeComponent("variable1", (byte) 2, TYPE_FIXED, (byte )JooVirtualMachine.TYPE_FIXED));
		compiler.parseFunctionComponent(code, testCode, 0);
		assert(code.hasComponentWithName("Test"));
		assert(code.getComponentWithName("Test").hasType(KEYWORD_FUNCTION_CALL));
		assert(code.getComponentWithName("Test").hasComponentWithName("variable0"));
		assert(code.getComponentWithName("Test").getComponentWithName("variable0").hasName((byte) 1));
		assert(code.getComponentWithName("Test").getComponentWithName("variable0").hasType(TYPE_INT));
		assert(code.getComponentWithName("Test").hasComponentWithName("variable1"));
		assert(code.getComponentWithName("Test").getComponentWithName("variable1").hasName((byte) 2));
		assert(code.getComponentWithName("Test").getComponentWithName("variable1").hasType(TYPE_FIXED));
		
		ParseException exception = null;
		try {
			testCode = new String[] {"function"};
			compiler.parseFunctionComponent(code, testCode, 0);
		} catch (ParseException e) {
			// if the assert is here the test will pass if no exception is thrown
			exception = e;
		}
		assertEquals(exception.getMessage(), "Invalid function declaration");
		
		try {
			testCode = new String[] {"function", "Test", "a", "param0", "fixed", "param1"};
			compiler.parseFunctionComponent(code, testCode, 0);
		} catch (ParseException e) {
			exception = e;
		}
		assertEquals(exception.getMessage(), "Invalid parameter type declaration, Type: a");
		
		try {
			testCode = new String[] {"function", "Test", "int", "param0", "param1"};
			compiler.parseFunctionComponent(code, testCode, 0);
		} catch (ParseException e) {
			exception = e;
		}
		assertEquals(exception.getMessage(), "Invalid parameter declaration");
		
		try {
			testCode = new String[] {"call"};
			compiler.parseFunctionComponent(code, testCode, 0);
		} catch (ParseException e) {
			exception = e;
		}
		assertEquals(exception.getMessage(), "Invalid function call");
		
		try {
			// if argument is a unknown variable
			testCode = new String[] {"call", "Test", "variable2"};
			compiler.parseFunctionComponent(code, testCode, 0);
		} catch (ParseException e) {
			exception = e;
		}
		assertEquals(exception.getMessage(), "Invalid variable, Name: variable2");
	}
	
	@Test
	public void parseConditionComponentTest() throws Exception {
		final String settingsData = FileUtil.read(PATH_COMPILER_SETTINGS);
		final JooCompiler compiler = new JooCompiler();
		final Settings settings = compiler.parseSettings(settingsData);
		String[] testCode;
		Code code;
		
		compiler.setSettings(settings);
		
		testCode = new String[] {"else"};
		code = new Code();
		compiler.parseConditionComponent(code, testCode, 0);
		assert(code.hasComponentWithName("else"));
		assert(code.getComponentWithName("else").hasType(KEYWORD_ELSE));
		
		testCode = new String[] {"endIf"};
		code = new Code();
		compiler.parseConditionComponent(code, testCode, 0);
		assert(code.hasComponentWithName("endIf"));
		assert(code.getComponentWithName("endIf").hasType(KEYWORD_IF_END));
		
		testCode = new String[] {"elseIf", "variable0", "==", "variable1"};
		code = new Code();
		code.addComponent(new CodeComponent("variable0", (byte) 1, TYPE_INT, (byte )JooVirtualMachine.TYPE_INT));
		code.addComponent(new CodeComponent("variable1", (byte) 2, TYPE_FIXED, (byte )JooVirtualMachine.TYPE_FIXED));
		compiler.parseConditionComponent(code, testCode, 0);
		assert(code.hasComponentWithName("=="));
		assert(code.getComponentWithName("==").hasName((byte) 5));
		assert(code.getComponentWithName("==").hasType(KEYWORD_ELSE_IF));
		assert(code.getComponentWithName("==").hasComponentWithName("variable0"));
		assert(code.getComponentWithName("==").getComponentWithName("variable0").hasName((byte) 1));
		assert(code.getComponentWithName("==").getComponentWithName("variable0").hasType(TYPE_INT));
		assert(code.getComponentWithName("==").hasComponentWithName("variable1"));
		assert(code.getComponentWithName("==").getComponentWithName("variable1").hasName((byte) 2));
		assert(code.getComponentWithName("==").getComponentWithName("variable1").hasType(TYPE_FIXED));
		
		testCode = new String[] {"if", "variable0", "==", "variable1"};
		code = new Code();
		code.addComponent(new CodeComponent("variable0", (byte) 1, TYPE_INT, (byte )JooVirtualMachine.TYPE_INT));
		code.addComponent(new CodeComponent("variable1", (byte) 2, TYPE_FIXED, (byte )JooVirtualMachine.TYPE_FIXED));
		compiler.parseConditionComponent(code, testCode, 0);
		assert(code.hasComponentWithName("=="));
		assert(code.getComponentWithName("==").hasName((byte) 5));
		assert(code.getComponentWithName("==").hasType(KEYWORD_IF));
		assert(code.getComponentWithName("==").hasComponentWithName("variable0"));
		assert(code.getComponentWithName("==").getComponentWithName("variable0").hasName((byte) 1));
		assert(code.getComponentWithName("==").getComponentWithName("variable0").hasType(TYPE_INT));
		assert(code.getComponentWithName("==").hasComponentWithName("variable1"));
		assert(code.getComponentWithName("==").getComponentWithName("variable1").hasName((byte) 2));
		assert(code.getComponentWithName("==").getComponentWithName("variable1").hasType(TYPE_FIXED));
		
		testCode = new String[] {"if", "variable0:10", "==", "variable1:5"};
		code = new Code();
		code.addComponent(new CodeComponent("variable0", (byte) 1, TYPE_ARRAY_INT, (byte )JooVirtualMachine.TYPE_ARRAY_INT));
		code.addComponent(new CodeComponent("variable1", (byte) 2, TYPE_ARRAY_FIXED, (byte )JooVirtualMachine.TYPE_ARRAY_FIXED));
		compiler.parseConditionComponent(code, testCode, 0);
		assert(code.hasComponentWithName("=="));
		assert(code.getComponentWithName("==").hasName((byte) 5));
		assert(code.getComponentWithName("==").hasType(KEYWORD_IF));
		assert(code.getComponentWithName("==").hasComponentWithName("variable0"));
		assert(code.getComponentWithName("==").getComponentWithName("variable0").hasName((byte) 1));
		assert(code.getComponentWithName("==").getComponentWithName("variable0").hasType(TYPE_ARRAY_INT));
		assert(code.getComponentWithName("==").getComponentWithName("variable0").hasComponentWithName("10"));
		assert(code.getComponentWithName("==").getComponentWithName("variable0").hasComponentWithName((byte) 11));
		assert(code.getComponentWithName("==").hasComponentWithName("variable1"));
		assert(code.getComponentWithName("==").getComponentWithName("variable1").hasName((byte) 2));
		assert(code.getComponentWithName("==").getComponentWithName("variable1").hasType(TYPE_ARRAY_FIXED));
		assert(code.getComponentWithName("==").getComponentWithName("variable1").hasComponentWithName("5"));
		assert(code.getComponentWithName("==").getComponentWithName("variable1").hasComponentWithName((byte) 6));
		
		testCode = new String[] {"if", "variable0:variable1", "==", "150"};
		code = new Code();
		code.addComponent(new CodeComponent("variable0", (byte) 1, TYPE_ARRAY_INT, (byte )JooVirtualMachine.TYPE_ARRAY_INT));
		code.addComponent(new CodeComponent("variable1", (byte) 2, TYPE_INT, (byte )JooVirtualMachine.TYPE_INT));
		compiler.parseConditionComponent(code, testCode, 0);
		assert(code.hasComponentWithName("=="));
		assert(code.getComponentWithName("==").hasName((byte) 5));
		assert(code.getComponentWithName("==").hasType(KEYWORD_IF));
		assert(code.getComponentWithName("==").hasComponentWithName("variable0"));
		assert(code.getComponentWithName("==").getComponentWithName("variable0").hasName((byte) 1));
		assert(code.getComponentWithName("==").getComponentWithName("variable0").hasType(TYPE_ARRAY_INT));
		assert(code.getComponentWithName("==").getComponentWithName("variable0").hasComponentWithName("variable1"));
		assert(code.getComponentWithName("==").getComponentWithName("variable0").hasComponentWithName((byte) 2));
		assert(code.getComponentWithName("==").hasComponentWithName("150"));
		assert(code.getComponentWithName("==").getComponentWithName("150").hasType(TYPE_ARRAY_INT));
	
		ParseException exception = null;
		try {
			testCode = new String[] {"if", "variable0", "==", "variable1", "A"};
			compiler.parseConditionComponent(code, testCode, 0);
		} catch (ParseException e) {
			// if the assert is here the test will pass if no exception is thrown
			exception = e;
		}
		assertEquals(exception.getMessage(), "Invalid condition declaration");
		
		try {
			testCode = new String[] {"if", "variable0", "A", "variable1"};
			compiler.parseConditionComponent(code, testCode, 0);
		} catch (ParseException e) {
			exception = e;
		}
		assertEquals(exception.getMessage(), "Invalid comparison operator, Operator: A");
		
		try {
			testCode = new String[] {"if", "variable0:a", "==", "variable1"};
			compiler.parseConditionComponent(code, testCode, 0);
		} catch (ParseException e) {
			exception = e;
		}
		assertEquals(exception.getMessage(), "Invalid array index, Index: a");
		
		try {
			testCode = new String[] {"if", "variable1:5", "==", "variable0"};
			compiler.parseConditionComponent(code, testCode, 0);
		} catch (ParseException e) {
			exception = e;
		}
		assertEquals(exception.getMessage(), "A variable that is not of type array has an index, Name: variable1");
	}
	
	@Test
	public void parseOperationComponentTest() throws Exception {
		final String settingsData = FileUtil.read(PATH_COMPILER_SETTINGS);
		final JooCompiler compiler = new JooCompiler();
		final Settings settings = compiler.parseSettings(settingsData);
		String[] testCode;
		Code code;
		
		compiler.setSettings(settings);
		
		// no need to test every possibility here as it's already tested in parseConditionComponent
		testCode = new String[] {"variable0", "=", "variable1"};
		code = new Code();
		code.addComponent(new CodeComponent("variable0", (byte) 1, TYPE_INT, (byte )JooVirtualMachine.TYPE_INT));
		code.addComponent(new CodeComponent("variable1", (byte) 2, TYPE_FIXED, (byte )JooVirtualMachine.TYPE_FIXED));
		compiler.parseOperationComponent(code, testCode, 0);
		assert(code.hasComponentWithName("="));
		assert(code.getComponentWithName("=").hasName((byte) 7));
		assert(code.getComponentWithName("=").hasType(TYPE_OPERATOR));
		assert(code.getComponentWithName("=").hasComponentWithName("variable0"));
		assert(code.getComponentWithName("=").getComponentWithName("variable0").hasName((byte) 1));
		assert(code.getComponentWithName("=").getComponentWithName("variable0").hasType(TYPE_INT));
		assert(code.getComponentWithName("=").hasComponentWithName("variable1"));
		assert(code.getComponentWithName("=").getComponentWithName("variable1").hasName((byte) 2));
		assert(code.getComponentWithName("=").getComponentWithName("variable1").hasType(TYPE_FIXED));
	
		ParseException exception = null;
		try {
			testCode = new String[] {"variable0", "a", "variable1"};
			compiler.parseOperationComponent(code, testCode, 0);
		} catch (ParseException e) {
			// if the assert is here the test will pass if no exception is thrown
			exception = e;
		}
		assertEquals(exception.getMessage(), "Invalid operator, Operator: a");
	}
	
	@Test
	public void parseCodeTest() throws Exception {
		final String settingsData = FileUtil.read(PATH_COMPILER_SETTINGS);
		final JooCompiler compiler = new JooCompiler();
		final Settings settings = compiler.parseSettings(settingsData);
		
		compiler.setSettings(settings);
		
		final String testCode = 
				"	# This is a comment			" + "\n" +
				"	int int0 = 10 # comment		" + "\n" +
				"	fixed fixed0 = 10.5			" + "\n" +
				"	bool bool0 = true			" + "\n" +
				"	char char0 = 'a'			" + "\n" +
				"	int:10 intArray				" + "\n" +
				"	fixed:15 fixedArray			" + "\n" +
				"	bool:5 boolArray			" + "\n" +
				"	char:7 charArray			" + "\n" +
				"	function Start				" + "\n" +
				"		if int0 == 5			" + "\n" +
				"			int0 + 5			" + "\n" +
				"		elseIf fixed0 == 10.5	" + "\n" +
				"			fixed0 + 1.5		" + "\n" +
				"		else					" + "\n" +
				"			bool0 = false		" + "\n" +
				"		endIf					" + "\n" +
				"	endFunction					" + "\n"
		;
		
		final Code code = compiler.parseCode(testCode);
		
		int line = 0;
		assert(code.getComponent(line).hasName("int0"));
		assert(code.getComponent(line).hasName((byte) 1));
		assert(code.getComponent(line).hasType(TYPE_INT));
		assert(code.getComponent(line).hasComponentWithName("10"));
		assert(code.getComponent(line).getComponentWithName("10").hasType(TYPE_INT));

		line++;
		assert(code.getComponent(line).hasName("fixed0"));
		assert(code.getComponent(line).hasName((byte) 2));
		assert(code.getComponent(line).hasType(TYPE_FIXED));
		assert(code.getComponent(line).hasComponentWithName("2678"));
		assert(code.getComponent(line).getComponentWithName("2678").hasType(TYPE_FIXED));

		line++;
		assert(code.getComponent(line).hasName("bool0"));
		assert(code.getComponent(line).hasName((byte) 3));
		assert(code.getComponent(line).hasType(TYPE_BOOL));
		assert(code.getComponent(line).hasComponentWithName("1"));
		assert(code.getComponent(line).getComponentWithName("1").hasType(TYPE_BOOL));

		line++;
		assert(code.getComponent(line).hasName("char0"));
		assert(code.getComponent(line).hasName((byte) 4));
		assert(code.getComponent(line).hasType(TYPE_CHAR));
		assert(code.getComponent(line).hasComponentWithName("97"));
		assert(code.getComponent(line).getComponentWithName("97").hasType(TYPE_CHAR));

		line++;
		assert(code.getComponent(line).hasName("intArray"));
		assert(code.getComponent(line).hasName((byte) 5));
		assert(code.getComponent(line).hasType(TYPE_ARRAY_INT));
		assert(code.getComponent(line).hasComponentWithName("10"));
		assert(code.getComponent(line).getComponentWithName("10").hasType(TYPE_ARRAY_SIZE));

		line++;
		assert(code.getComponent(line).hasName("fixedArray"));
		assert(code.getComponent(line).hasName((byte) 6));
		assert(code.getComponent(line).hasType(TYPE_ARRAY_FIXED));
		assert(code.getComponent(line).hasComponentWithName("15"));
		assert(code.getComponent(line).getComponentWithName("15").hasType(TYPE_ARRAY_SIZE));

		line++;
		assert(code.getComponent(line).hasName("boolArray"));
		assert(code.getComponent(line).hasName((byte) 7));
		assert(code.getComponent(line).hasType(TYPE_ARRAY_BOOL));
		assert(code.getComponent(line).hasComponentWithName("5"));
		assert(code.getComponent(line).getComponentWithName("5").hasType(TYPE_ARRAY_SIZE));

		line++;
		assert(code.getComponent(line).hasName("charArray"));
		assert(code.getComponent(line).hasName((byte) 8));
		assert(code.getComponent(line).hasType(TYPE_ARRAY_CHAR));
		assert(code.getComponent(line).hasComponentWithName("7"));
		assert(code.getComponent(line).getComponentWithName("7").hasType(TYPE_ARRAY_SIZE));

		line++;
		assert(code.getComponent(line).hasName("Start"));
		assert(code.getComponent(line).hasName((byte) 9));
		assert(code.getComponent(line).hasType(TYPE_FUNCTION));
		
		line++;
		assert(code.getComponent(line).hasName("=="));
		assert(code.getComponent(line).hasName((byte) 5)); // byte code name from compiler settings
		assert(code.getComponent(line).hasType(KEYWORD_IF));
		assert(code.getComponent(line).hasComponentWithName("int0"));
		assert(code.getComponent(line).getComponentWithName("int0").hasName((byte) 1));
		assert(code.getComponent(line).getComponentWithName("int0").hasType(TYPE_INT));
		assert(code.getComponent(line).hasComponentWithName("5"));
		assert(code.getComponent(line).getComponentWithName("5").hasType(TYPE_INT));
		
		line++;
		assert(code.getComponent(line).hasName("+"));
		assert(code.getComponent(line).hasName((byte) 11));
		assert(code.getComponent(line).hasType(TYPE_OPERATOR));
		assert(code.getComponent(line).hasComponentWithName("int0"));
		assert(code.getComponent(line).getComponentWithName("int0").hasName((byte) 1));
		assert(code.getComponent(line).getComponentWithName("int0").hasType(TYPE_INT));
		assert(code.getComponent(line).hasComponentWithName("5"));
		assert(code.getComponent(line).getComponentWithName("5").hasType(TYPE_INT));
		
		line++;
		assert(code.getComponent(line).hasName("=="));
		assert(code.getComponent(line).hasName((byte) 5));
		assert(code.getComponent(line).hasType(KEYWORD_ELSE_IF));
		assert(code.getComponent(line).hasComponentWithName("fixed0"));
		assert(code.getComponent(line).getComponentWithName("fixed0").hasName((byte) 2));
		assert(code.getComponent(line).getComponentWithName("fixed0").hasType(TYPE_FIXED));
		assert(code.getComponent(line).hasComponentWithName("2678"));
		assert(code.getComponent(line).getComponentWithName("2678").hasType(TYPE_FIXED));
		
		line++;
		assert(code.getComponent(line).hasName("+"));
		assert(code.getComponent(line).hasName((byte) 11));
		assert(code.getComponent(line).hasType(TYPE_OPERATOR));
		assert(code.getComponent(line).hasComponentWithName("fixed0"));
		assert(code.getComponent(line).getComponentWithName("fixed0").hasName((byte) 2));
		assert(code.getComponent(line).getComponentWithName("fixed0").hasType(TYPE_FIXED));
		assert(code.getComponent(line).hasComponentWithName("383"));
		assert(code.getComponent(line).getComponentWithName("383").hasType(TYPE_FIXED));
		
		line++;
		assert(code.getComponent(line).hasName("else"));
		assert(code.getComponent(line).hasType(KEYWORD_ELSE));
		
		line++;
		assert(code.getComponent(line).hasName("="));
		assert(code.getComponent(line).hasName((byte) 7));
		assert(code.getComponent(line).hasType(TYPE_OPERATOR));
		assert(code.getComponent(line).hasComponentWithName("bool0"));
		assert(code.getComponent(line).getComponentWithName("bool0").hasName((byte) 3));
		assert(code.getComponent(line).getComponentWithName("bool0").hasType(TYPE_BOOL));
		assert(code.getComponent(line).hasComponentWithName("0"));
		assert(code.getComponent(line).getComponentWithName("0").hasType(TYPE_BOOL));
		
		line++;
		assert(code.getComponent(line).hasName("endIf"));
		assert(code.getComponent(line).hasType(KEYWORD_IF_END));

		line++;
		assert(code.getComponent(line).hasName("endFunction"));
		assert(code.getComponent(line).hasType(KEYWORD_FUNCTION_END));
	}
	
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
		for (int i = 0; i <= 9; i++) {
			value = value.replace((char)('0' + i), (char)(JooVirtualMachine.NUMBER_0 + i));
		}
		return value;
	}
}
