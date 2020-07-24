package com.johnsproject.joo;

import java.text.ParseException;

import org.junit.Test;

import com.johnsproject.joo.util.FileUtil;

import static com.johnsproject.joo.JooCompiler.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import static com.johnsproject.joo.JooVirtualMachine.COMPARATOR_SMALLER_EQUALS;
import static com.johnsproject.joo.JooVirtualMachine.COMPARATOR_BIGGER_EQUALS;
import static com.johnsproject.joo.JooVirtualMachine.COMPARATOR_SMALLER;
import static com.johnsproject.joo.JooVirtualMachine.COMPARATOR_BIGGER;
import static com.johnsproject.joo.JooVirtualMachine.COMPARATOR_EQUALS;
import static com.johnsproject.joo.JooVirtualMachine.COMPARATOR_NOT_EQUALS;
import static com.johnsproject.joo.JooVirtualMachine.OPERATOR_ASSIGN;
import static com.johnsproject.joo.JooVirtualMachine.OPERATOR_ASSIGN_POSITIVE;
import static com.johnsproject.joo.JooVirtualMachine.OPERATOR_ASSIGN_NEGATIVE;
import static com.johnsproject.joo.JooVirtualMachine.OPERATOR_ASSIGN_INVERSE;
import static com.johnsproject.joo.JooVirtualMachine.OPERATOR_ADD;
import static com.johnsproject.joo.JooVirtualMachine.OPERATOR_SUBTRACT;
import static com.johnsproject.joo.JooVirtualMachine.OPERATOR_MULTIPLY;
import static com.johnsproject.joo.JooVirtualMachine.OPERATOR_DIVIDE;
import static com.johnsproject.joo.JooVirtualMachine.OPERATOR_REMAINDER;
import static com.johnsproject.joo.JooVirtualMachine.OPERATOR_BITWISE_AND;
import static com.johnsproject.joo.JooVirtualMachine.OPERATOR_BITWISE_XOR;
import static com.johnsproject.joo.JooVirtualMachine.OPERATOR_BITWISE_OR;
import static com.johnsproject.joo.JooVirtualMachine.OPERATOR_BITWISE_NOT;
import static com.johnsproject.joo.JooVirtualMachine.OPERATOR_BITSHIFT_LEFT;
import static com.johnsproject.joo.JooVirtualMachine.OPERATOR_BITSHIFT_RIGHT;

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
		
		try {
			final String testSettings = "@operator" + "\n"
										+ "01 + int|fixed" + "\n"
										+ "02 + int|fixed";
			compiler.parseSettings(testSettings);
			fail("Duplicate compiler setting exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Duplicate compiler setting, Name: +");
		}
		
		
		try {
			final String testSettings = "@function" + "\n"
										+ "MyFunction";
			compiler.parseSettings(testSettings);
			fail("Invalid setting declaration exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid setting declaration");
		}
		
		try {
			final String testSettings = "@operator" + "\n"
										+ "0A + int|fixed";
			compiler.parseSettings(testSettings);
			fail("Invalid byte code name exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid byte code name, Name: 0A");
		}
		
		try {
			final String testSettings = "@operator" + "\n"
										+ "01 + int|fixed A";
			compiler.parseSettings(testSettings);
			fail("Invalid operator declaration exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid operator declaration");
		}
		
		try {
			final String testSettings = "@function" + "\n"
										+ "01 MyFunction bug";
			compiler.parseSettings(testSettings);
			fail("Invalid supported type exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid supported type, Type: bug");
		}
	}
	
	@Test
	public void replaceDefinesTest() throws Exception {
		final JooCompiler compiler = new JooCompiler();
		
		String testCode = "define TEST_CONSTANT = 10" + "\n" +
								"int int0 = TEST_CONSTANT";
		
		testCode = compiler.replaceDefines(testCode);
		assertEquals(testCode, "int int0 = 10");
		
		try {
			testCode = "define TEST_CONSTANT = ";
			testCode = compiler.replaceDefines(testCode);
			fail("Invalid constant declaration exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid constant declaration");
		}
		
		try {
			testCode = "define TEST_CONSTANT + 10";
			testCode = compiler.replaceDefines(testCode);
			fail("Invalid assignment operator exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid assignment operator, Operator: +");
		}
		
		try {
			testCode = "define TEST_CONSTANT = 10" + "\n" +
					"define TEST_CONSTANT = 5";
			testCode = compiler.replaceDefines(testCode);
			fail("Duplicate constant exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Duplicate constant, Name: TEST_CONSTANT");
		}
	}
	
	@Test
	public void parseVariableComponentTest() throws Exception {
		final JooCompiler compiler = new JooCompiler();
		String[] testCode;
		Code code;
		
		testCode = new String[] {"int", "test", "=", "10"};
		code = new Code();
		createTypeRegistry(code);
		compiler.parseVariableComponent(code, testCode, 0);
		assert(code.hasComponentWithName("test"));
		assert(code.getComponentWithName("test").hasName((byte) 1));
		assert(code.getComponentWithName("test").hasType(TYPE_INT));
		assert(code.getComponentWithName("test").hasComponentWithName("10"));
		assert(code.getComponentWithName("test").getComponentWithName("10").hasType(TYPE_INT));
		
		testCode = new String[] {"fixed", "test", "=", "20.5"};
		code = new Code();
		createTypeRegistry(code);
		compiler.parseVariableComponent(code, testCode, 0);
		assert(code.hasComponentWithName("test"));
		assert(code.getComponentWithName("test").hasName((byte) 1));
		assert(code.getComponentWithName("test").hasType(TYPE_FIXED));
		assert(code.getComponentWithName("test").hasComponentWithName("5228"));
		assert(code.getComponentWithName("test").getComponentWithName("5228").hasType(TYPE_FIXED));
		
		testCode = new String[] {"bool", "test", "=", "true"};
		code = new Code();
		createTypeRegistry(code);
		compiler.parseVariableComponent(code, testCode, 0);
		assert(code.hasComponentWithName("test"));
		assert(code.getComponentWithName("test").hasName((byte) 1));
		assert(code.getComponentWithName("test").hasType(TYPE_BOOL));
		assert(code.getComponentWithName("test").hasComponentWithName("1"));
		assert(code.getComponentWithName("test").getComponentWithName("1").hasType(TYPE_BOOL));
		
		testCode = new String[] {"bool", "test", "=", "false"};
		code = new Code();
		createTypeRegistry(code);
		compiler.parseVariableComponent(code, testCode, 0);
		assert(code.hasComponentWithName("test"));
		assert(code.getComponentWithName("test").hasName((byte) 1));
		assert(code.getComponentWithName("test").hasType(TYPE_BOOL));
		assert(code.getComponentWithName("test").hasComponentWithName("0"));
		assert(code.getComponentWithName("test").getComponentWithName("0").hasType(TYPE_BOOL));
		
		testCode = new String[] {"char", "test", "=", "'A'"};
		code = new Code();
		createTypeRegistry(code);
		compiler.parseVariableComponent(code, testCode, 0);
		assert(code.hasComponentWithName("test"));
		assert(code.getComponentWithName("test").hasName((byte) 1));
		assert(code.getComponentWithName("test").hasType(TYPE_CHAR));
		assert(code.getComponentWithName("test").hasComponentWithName("65"));
		assert(code.getComponentWithName("test").getComponentWithName("65").hasType(TYPE_CHAR));
		
		testCode = new String[] {"int", "test"};
		code = new Code();
		createTypeRegistry(code);
		compiler.parseVariableComponent(code, testCode, 0);
		assert(code.hasComponentWithName("test"));
		assert(code.getComponentWithName("test").hasName((byte) 1));
		assert(code.getComponentWithName("test").hasType(TYPE_INT));
		assert(!code.getComponentWithName("test").hasComponentWithName("10"));
		
		testCode = new String[] {"int:10", "test"};
		code = new Code();
		createTypeRegistry(code);
		compiler.parseVariableComponent(code, testCode, 0);
		assert(code.hasComponentWithName("test"));
		assert(code.getComponentWithName("test").hasName((byte) 1));
		assert(code.getComponentWithName("test").hasType(TYPE_ARRAY_INT));
		assert(code.getComponentWithName("test").hasComponentWithName("10"));
		assert(code.getComponentWithName("test").getComponentWithName("10").hasType(KEYWORD_ARRAY));
		
		try {
			testCode = new String[] {"int", "test", "="};
			compiler.parseVariableComponent(code, testCode, 0);
			fail("Invalid variable declaration exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid variable declaration");
		}
		
		try {
			testCode = new String[] {"int:A", "test"};
			compiler.parseVariableComponent(code, testCode, 0);
			fail("Invalid array size declaration exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid array size declaration, Size: A");
		}
		
		try {
			testCode = new String[] {"int:15", "test", "=", "10"};
			compiler.parseVariableComponent(code, testCode, 0);
			fail("Invalid array declaration exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid array declaration. Value can't be assigned to array");
		}
		
		try {
			testCode = new String[] {"int", "test", "-", "10"};
			compiler.parseVariableComponent(code, testCode, 0);
			fail("Invalid assignment operator exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid assignment operator, Operator: -");
		}
		
		try {
			testCode = new String[] {"char", "test", "=", "'A''"};
			compiler.parseVariableComponent(code, testCode, 0);
			fail("Invalid char value declaration exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid char value declaration");
		}
		
		try {
			testCode = new String[] {"int", "test", "=", "A"};
			compiler.parseVariableComponent(code, testCode, 0);
			fail("The declared value type doesn't match the variable type exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "The declared value type doesn't match the variable type");
		}
	}
	
	@Test
	public void parseFunctionComponentTest() throws Exception {
		final JooCompiler compiler = new JooCompiler();
		String[] testCode;
		Code code;
		
		testCode = new String[] {"repeatFunction"};
		code = new Code();
		createTypeRegistry(code);
		compiler.parseFunctionComponent(code, testCode, 0);
		assert(code.hasComponentWithName("repeatFunction"));
		assert(code.getComponentWithName("repeatFunction").hasName(KEYWORD_FUNCTION_REPEAT));
		assert(code.getComponentWithName("repeatFunction").hasType(KEYWORD_FUNCTION_REPEAT));
		assert(code.getComponentWithName("repeatFunction").hasName((byte)VM_KEYWORD_FUNCTION_REPEAT));
		assert(code.getComponentWithName("repeatFunction").hasType((byte)VM_KEYWORD_FUNCTION_REPEAT));
		
		testCode = new String[] {"endFunction"};
		code = new Code();
		createTypeRegistry(code);
		compiler.parseFunctionComponent(code, testCode, 0);
		assert(code.hasComponentWithName("endFunction"));
		assert(code.getComponentWithName("endFunction").hasType(KEYWORD_FUNCTION_END));
		assert(code.getComponentWithName("endFunction").hasType(KEYWORD_FUNCTION_END));
		assert(code.getComponentWithName("endFunction").hasName((byte)VM_KEYWORD_FUNCTION));
		assert(code.getComponentWithName("endFunction").hasType((byte)VM_KEYWORD_FUNCTION));
		
		testCode = new String[] {"function", "Test"};
		code = new Code();
		createTypeRegistry(code);
		compiler.parseFunctionComponent(code, testCode, 0);
		assert(code.hasComponentWithName("Test"));
		assert(code.getComponentWithName("Test").hasName((byte) 1));
		assert(code.getComponentWithName("Test").hasType(KEYWORD_FUNCTION));
		
		testCode = new String[] {"function", "Test", "int", "param0", "fixed", "param1"};
		code = new Code();
		createTypeRegistry(code);
		compiler.parseFunctionComponent(code, testCode, 0);
		assert(code.hasComponentWithName("Test"));
		assert(code.getComponentWithName("Test").hasName((byte) 1));
		assert(code.getComponentWithName("Test").hasType(KEYWORD_FUNCTION));
		assert(code.getComponentWithName("Test").hasComponentWithName("param0"));
		assert(code.getComponentWithName("Test").getComponentWithName("param0").hasName((byte) (0 + VM_PARAMETERS_START)));
		assert(code.getComponentWithName("Test").getComponentWithName("param0").hasType(TYPE_INT));
		assert(code.getComponentWithName("Test").hasComponentWithName("param1"));
		assert(code.getComponentWithName("Test").getComponentWithName("param1").hasName((byte) (1 + VM_PARAMETERS_START)));
		assert(code.getComponentWithName("Test").getComponentWithName("param1").hasType(TYPE_FIXED));
		
		testCode = new String[] {"call", "Test"};
		code = new Code();
		createTypeRegistry(code);
		compiler.parseFunctionComponent(code, testCode, 0);
		assert(code.hasComponentWithName("Test"));
		assert(code.getComponentWithName("Test").hasType(KEYWORD_FUNCTION_CALL));
		
		testCode = new String[] {"call", "Test", "variable0", "variable1"};
		code = new Code();
		createTypeRegistry(code);
		code.addComponent(new CodeComponent("variable0", (byte) 1, TYPE_INT, (byte )VM_TYPE_INT, 0));
		code.addComponent(new CodeComponent("variable1", (byte) 2, TYPE_FIXED, (byte )VM_TYPE_FIXED, 0));
		compiler.parseFunctionComponent(code, testCode, 0);
		assert(code.hasComponentWithName("Test"));
		assert(code.getComponentWithName("Test").hasType(KEYWORD_FUNCTION_CALL));
		assert(code.getComponentWithName("Test").hasComponentWithName("variable0"));
		assert(code.getComponentWithName("Test").getComponentWithName("variable0").hasName((byte) 1));
		assert(code.getComponentWithName("Test").getComponentWithName("variable0").hasType(TYPE_INT));
		assert(code.getComponentWithName("Test").hasComponentWithName("variable1"));
		assert(code.getComponentWithName("Test").getComponentWithName("variable1").hasName((byte) 2));
		assert(code.getComponentWithName("Test").getComponentWithName("variable1").hasType(TYPE_FIXED));
		
		try {
			testCode = new String[] {"function"};
			compiler.parseFunctionComponent(code, testCode, 0);
			fail("Invalid function declaration exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid function declaration");
		}
		
		try {
			testCode = new String[] {"function", "Test", "a", "param0", "fixed", "param1"};
			compiler.parseFunctionComponent(code, testCode, 0);
			fail("Invalid parameter type declaration exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid parameter type declaration, Type: a");
		}
		
		try {
			testCode = new String[] {"function", "Test", "int", "param0", "param1"};
			compiler.parseFunctionComponent(code, testCode, 0);
			fail("Invalid parameter declaration exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid parameter declaration");
		}
		
		try {
			testCode = new String[] {"call"};
			compiler.parseFunctionComponent(code, testCode, 0);
			fail("Invalid function call exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid function call");
		}
		
		try {
			// if argument is a unknown variable
			testCode = new String[] {"call", "Test", "variable2"};
			compiler.parseFunctionComponent(code, testCode, 0);
			fail("Invalid variable exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid variable, Name: variable2");
		}
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
		code.addComponent(new CodeComponent("variable0", (byte) 1, TYPE_INT, (byte)VM_TYPE_INT, 0));
		code.addComponent(new CodeComponent("variable1", (byte) 2, TYPE_FIXED, (byte)VM_TYPE_FIXED, 0));
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
		code.addComponent(new CodeComponent("variable0", (byte) 1, TYPE_INT, (byte )VM_TYPE_INT, 0));
		code.addComponent(new CodeComponent("variable1", (byte) 2, TYPE_FIXED, (byte )VM_TYPE_FIXED, 0));
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
		code.addComponent(new CodeComponent("variable0", (byte) 1, TYPE_ARRAY_INT, (byte)VM_TYPE_ARRAY_INT, 0));
		code.addComponent(new CodeComponent("variable1", (byte) 2, TYPE_ARRAY_FIXED, (byte)VM_TYPE_ARRAY_FIXED, 0));
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
		code.addComponent(new CodeComponent("variable0", (byte) 1, TYPE_ARRAY_INT, (byte)VM_TYPE_ARRAY_INT, 0));
		code.addComponent(new CodeComponent("variable1", (byte) 2, TYPE_INT, (byte)VM_TYPE_INT, 0));
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
	
		try {
			testCode = new String[] {"if", "variable0", "==", "variable1", "A"};
			compiler.parseConditionComponent(code, testCode, 0);
			fail("Invalid condition not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid condition declaration");
		}
		
		try {
			testCode = new String[] {"if", "variable0", "A", "variable1"};
			compiler.parseConditionComponent(code, testCode, 0);
			fail("Invalid comparison operator not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid comparison operator, Operator: A");
		}
		
		try {
			testCode = new String[] {"if", "variable0:a", "==", "variable1"};
			compiler.parseConditionComponent(code, testCode, 0);
			fail("Invalid array index exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid array index, Index: a");
		}
		
		try {
			testCode = new String[] {"if", "variable1:5", "==", "variable0"};
			compiler.parseConditionComponent(code, testCode, 0);
			fail("A variable that is not of type array has an index exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "A variable that is not of type array has an index, Name: variable1");
		}
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
		code.addComponent(new CodeComponent("variable0", (byte) 1, TYPE_INT, (byte )VM_TYPE_INT, 0));
		code.addComponent(new CodeComponent("variable1", (byte) 2, TYPE_FIXED, (byte )VM_TYPE_FIXED, 0));
		compiler.parseOperationComponent(code, testCode, 0);
		assert(code.hasComponentWithName("="));
		assert(code.getComponentWithName("=").hasName((byte) 7));
		assert(code.getComponentWithName("=").hasType(KEYWORD_OPERATOR));
		assert(code.getComponentWithName("=").hasComponentWithName("variable0"));
		assert(code.getComponentWithName("=").getComponentWithName("variable0").hasName((byte) 1));
		assert(code.getComponentWithName("=").getComponentWithName("variable0").hasType(TYPE_INT));
		assert(code.getComponentWithName("=").hasComponentWithName("variable1"));
		assert(code.getComponentWithName("=").getComponentWithName("variable1").hasName((byte) 2));
		assert(code.getComponentWithName("=").getComponentWithName("variable1").hasType(TYPE_FIXED));
	
		try {
			testCode = new String[] {"variable0", "a", "variable1"};
			compiler.parseOperationComponent(code, testCode, 0);
			fail("Invalid operator exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid operator, Operator: a");
		}
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
		
		// line 0 is the type registry
		int line = 1;
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
		assert(code.getComponent(line).getComponentWithName("10").hasType(KEYWORD_ARRAY));

		line++;
		assert(code.getComponent(line).hasName("fixedArray"));
		assert(code.getComponent(line).hasName((byte) 6));
		assert(code.getComponent(line).hasType(TYPE_ARRAY_FIXED));
		assert(code.getComponent(line).hasComponentWithName("15"));
		assert(code.getComponent(line).getComponentWithName("15").hasType(KEYWORD_ARRAY));

		line++;
		assert(code.getComponent(line).hasName("boolArray"));
		assert(code.getComponent(line).hasName((byte) 7));
		assert(code.getComponent(line).hasType(TYPE_ARRAY_BOOL));
		assert(code.getComponent(line).hasComponentWithName("5"));
		assert(code.getComponent(line).getComponentWithName("5").hasType(KEYWORD_ARRAY));

		line++;
		assert(code.getComponent(line).hasName("charArray"));
		assert(code.getComponent(line).hasName((byte) 8));
		assert(code.getComponent(line).hasType(TYPE_ARRAY_CHAR));
		assert(code.getComponent(line).hasComponentWithName("7"));
		assert(code.getComponent(line).getComponentWithName("7").hasType(KEYWORD_ARRAY));

		line++;
		assert(code.getComponent(line).hasName("Start"));
		assert(code.getComponent(line).hasName((byte) 9));
		assert(code.getComponent(line).hasType(KEYWORD_FUNCTION));
		
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
		assert(code.getComponent(line).hasType(KEYWORD_OPERATOR));
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
		assert(code.getComponent(line).hasType(KEYWORD_OPERATOR));
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
		assert(code.getComponent(line).hasType(KEYWORD_OPERATOR));
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
	public void analyseCodeTest() throws Exception {
		final String settingsData = FileUtil.read(PATH_COMPILER_SETTINGS);
		final JooCompiler compiler = new JooCompiler();
		final Settings settings = compiler.parseSettings(settingsData);
		
		compiler.setSettings(settings);
		
		try {
			Code code = new Code();
			code.addComponent(new CodeComponent("0variable", (byte) 1, TYPE_INT, (byte) 0, 0));
			compiler.analyseCode(code);
			fail("Variable names should start with a alphabetic character exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Variable names should start with a alphabetic character, Name: 0variable");
		}
		
		try {
			Code code = new Code();
			code.addComponent(new CodeComponent("Variable", (byte) 1, TYPE_INT, (byte) 0, 0));
			compiler.analyseCode(code);
			fail("Variable names should start with a lowercase character exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Variable names should start with a lowercase character, Name: Variable");
		}
		
		try {
			Code code = new Code();
			code.addComponent(new CodeComponent("my_variable", (byte) 1, TYPE_INT, (byte) 0, 0));
			compiler.analyseCode(code);
			fail("Variable names should contain only alphanumeric characters exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Variable names should contain only alphanumeric characters, Name: my_variable");
		}
		
		try {
			Code code = new Code();
			code.addComponent(new CodeComponent("variable", (byte) 1, TYPE_INT, (byte) 0, 0));
			code.addComponent(new CodeComponent("variable", (byte) 2, TYPE_INT, (byte) 0, 0));
			compiler.analyseCode(code);
			fail("Duplicate variable exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Duplicate variable, Name: variable");
		}
		
		try {
			Code code = new Code();
			code.addComponent(new CodeComponent("0Function", (byte) 1, KEYWORD_FUNCTION, (byte) 0, 0));
			compiler.analyseCode(code);
			fail("Function names should start with a alphabetic character exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Function names should start with a alphabetic character, Name: 0Function");
		}
		
		try {
			Code code = new Code();
			code.addComponent(new CodeComponent("function", (byte) 1, KEYWORD_FUNCTION, (byte) 0, 0));
			compiler.analyseCode(code);
			fail("Function names should start with a uppercase character exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Function names should start with a uppercase character, Name: function");
		}
		
		try {
			Code code = new Code();
			code.addComponent(new CodeComponent("My_Function", (byte) 1, KEYWORD_FUNCTION, (byte) 0, 0));
			compiler.analyseCode(code);
			fail("Function names should contain only alphanumeric characters exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Function names should contain only alphanumeric characters, Name: My_Function");
		}
		
		try {
			Code code = new Code();
			code.addComponent(new CodeComponent("Function", (byte) 1, KEYWORD_FUNCTION, (byte) 0, 0));
			code.addComponent(new CodeComponent("Function", (byte) 2, KEYWORD_FUNCTION, (byte) 0, 0));
			compiler.analyseCode(code);
			fail("Duplicate function exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Duplicate function, Name: Function");
		}
		
		try {
			Code code = new Code();
			CodeComponent function = new CodeComponent("Function", (byte) 1, KEYWORD_FUNCTION, (byte) 0, 0);
			function.addComponent(new CodeComponent("parameter", (byte) 1, KEYWORD_PARAMETER, (byte) 0, 0));
			code.addComponent(function);
			compiler.analyseCode(code);
			fail("Parameter names should start with a _ exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Parameter names should start with a _, Name: parameter");
		}
		
		try {
			Code code = new Code();
			CodeComponent function = new CodeComponent("Function", (byte) 1, KEYWORD_FUNCTION, (byte) 0, 0);
			function.addComponent(new CodeComponent("_0parameter", (byte) 1, KEYWORD_PARAMETER, (byte) 0, 0));
			code.addComponent(function);
			compiler.analyseCode(code);
			fail("Parameter names should start with a alphabetic character exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Parameter names should start with a alphabetic character, Name: _0parameter");
		}
		
		try {
			Code code = new Code();
			CodeComponent function = new CodeComponent("Function", (byte) 1, KEYWORD_FUNCTION, (byte) 0, 0);
			function.addComponent(new CodeComponent("_Parameter", (byte) 1, KEYWORD_PARAMETER, (byte) 0, 0));
			code.addComponent(function);
			compiler.analyseCode(code);
			fail("Parameter names should start with a lowercase character exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Parameter names should start with a lowercase character, Name: _Parameter");
		}
		
		try {
			Code code = new Code();
			CodeComponent function = new CodeComponent("Function", (byte) 1, KEYWORD_FUNCTION, (byte) 0, 0);
			function.addComponent(new CodeComponent("_my_parameter", (byte) 1, KEYWORD_PARAMETER, (byte) 0, 0));
			code.addComponent(function);
			compiler.analyseCode(code);
			fail("Parameter names should contain only alphanumeric characters exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Parameter names should contain only alphanumeric characters, Name: _my_parameter");
		}
		
		try {
			Code code = new Code();
			CodeComponent function = new CodeComponent("Function", (byte) 1, KEYWORD_FUNCTION, (byte) 0, 0);
			function.addComponent(new CodeComponent("_parameter", (byte) 1, KEYWORD_PARAMETER, (byte) 0, 0));
			function.addComponent(new CodeComponent("_parameter", (byte) 2, KEYWORD_PARAMETER, (byte) 0, 0));
			code.addComponent(function);
			compiler.analyseCode(code);
			fail("Duplicate parameter");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Duplicate parameter, Name: _parameter");
		}
		
		try {
			Code code = new Code();
			code.addComponent(new CodeComponent("Function", (byte) 1, KEYWORD_FUNCTION_CALL, (byte) 0, 0));
			compiler.analyseCode(code);
			fail("Invalid function exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid function, Name: Function");
		}
	}
	
	@Test
	public void createTypeRegistryTest() throws Exception {
		final JooCompiler compiler = new JooCompiler();
		final String codeData = FileUtil.read("TestCode.joo");
		final String[] codeLines = compiler.getLines(codeData);
		final Code code = new Code();
		compiler.createTypeRegistry(code, codeLines);
		
		assert(code.hasComponentWithType(KEYWORD_TYPE_REGISTRY));
		final CodeComponent typeRegistry = code.getComponentWithType(KEYWORD_TYPE_REGISTRY);
		
		int type = 0;
		assert(typeRegistry.getComponent(type).hasType(TYPE_INT));
		assert(typeRegistry.getComponent(type).hasType((byte) VM_TYPE_INT));
		assert(typeRegistry.getComponent(type).hasName("3"));
		type++;
		assert(typeRegistry.getComponent(type).hasType(TYPE_FIXED));
		assert(typeRegistry.getComponent(type).hasType((byte) VM_TYPE_FIXED));
		assert(typeRegistry.getComponent(type).hasName("5"));
		type++;
		assert(typeRegistry.getComponent(type).hasType(TYPE_BOOL));
		assert(typeRegistry.getComponent(type).hasType((byte) VM_TYPE_BOOL));
		assert(typeRegistry.getComponent(type).hasName("8"));
		type++;
		assert(typeRegistry.getComponent(type).hasType(TYPE_CHAR));
		assert(typeRegistry.getComponent(type).hasType((byte) VM_TYPE_CHAR));
		assert(typeRegistry.getComponent(type).hasName("11"));
		type++;
		assert(typeRegistry.getComponent(type).hasType(TYPE_ARRAY_INT));
		assert(typeRegistry.getComponent(type).hasType((byte) VM_TYPE_ARRAY_INT));
		assert(typeRegistry.getComponent(type).hasName("12"));
		type++;
		assert(typeRegistry.getComponent(type).hasType(TYPE_ARRAY_FIXED));
		assert(typeRegistry.getComponent(type).hasType((byte) VM_TYPE_ARRAY_FIXED));
		assert(typeRegistry.getComponent(type).hasName("13"));
		type++;
		assert(typeRegistry.getComponent(type).hasType(TYPE_ARRAY_BOOL));
		assert(typeRegistry.getComponent(type).hasType((byte) VM_TYPE_ARRAY_BOOL));
		assert(typeRegistry.getComponent(type).hasName("14"));
		type++;
		assert(typeRegistry.getComponent(type).hasType(TYPE_ARRAY_CHAR));
		assert(typeRegistry.getComponent(type).hasType((byte) VM_TYPE_ARRAY_CHAR));
		assert(typeRegistry.getComponent(type).hasName("15"));
		type++;
		assert(typeRegistry.getComponent(type).hasType(KEYWORD_FUNCTION));
		assert(typeRegistry.getComponent(type).hasType((byte) VM_TYPE_FUNCTION));
		assert(typeRegistry.getComponent(type).hasName("17"));
	}
	
	@Test
	public void compileTest() throws Exception {
		final JooCompiler compiler = new JooCompiler();
		final String compiledByteCode = compiler.compile("TestCode.joo");
		final String[] lines = compiledByteCode.split("" + VM_LINE_BREAK);
		
		// uncomment to print the byte code character as numbers
		for (int l = 0; l < lines.length; l++) {
			final char[] line = lines[l].toCharArray();
			for (int i = 0; i < line.length; i++) {
				System.out.print((int)line[i] + "|");
			}
			System.out.println();
		}
		
		char name = VM_COMPONENTS_START;
		final char int0 = name++;
		final char int1 = name++;
		final char correctIfs = name++;
		final char fixed0 = name++;
		final char fixed1 = name++;
		final char bool0 = name++;
		final char bool1 = name++;
		final char bool2 = name++;
		final char char0 = name++;
		final char char1 = name++;
		final char char2 = name++;
		final char intArray = name++;
		final char fixedArray = name++;
		final char boolArray = name++;
		final char charArray = name++;
		final char start = name++;
		final char function = name++;
		
		char parameter = VM_PARAMETERS_START;
		final char param0 = parameter++;
		final char param1 = parameter++;
		
		// TODO unary operators
		// that can be customized in the compiler settings and will be called to modify the value before applying it to the variable in front of the operator
		int line = 0;
		assertEquals(lines[line++], "" + VM_TYPE_INT + (char)3);
		assertEquals(lines[line++], "" + int0);
		assertEquals(lines[line++], "" + int1 + compiler.toByteCodeNumber("10"));
		assertEquals(lines[line++], "" + correctIfs);
		assertEquals(lines[line++], "" + VM_TYPE_FIXED + (char)2);
		assertEquals(lines[line++], "" + fixed0);
		assertEquals(lines[line++], "" + fixed1 + compiler.toByteCodeNumber("" + 100.5f * FIXED_POINT_ONE));
		assertEquals(lines[line++], "" + VM_TYPE_BOOL + (char)3);
		assertEquals(lines[line++], "" + bool0);
		assertEquals(lines[line++], "" + bool1 + compiler.toByteCodeNumber("1"));
		assertEquals(lines[line++], "" + bool2 + compiler.toByteCodeNumber("0"));
		assertEquals(lines[line++], "" + VM_TYPE_CHAR + (char)3);
		assertEquals(lines[line++], "" + char0);
		assertEquals(lines[line++], "" + char1 + compiler.toByteCodeNumber("65"));
		assertEquals(lines[line++], "" + char2 + compiler.toByteCodeNumber("67"));
		assertEquals(lines[line++], "" + VM_TYPE_ARRAY_INT + (char)1);
		assertEquals(lines[line++], "" + intArray + compiler.toByteCodeNumber("10"));
		assertEquals(lines[line++], "" + VM_TYPE_ARRAY_FIXED + (char)1);
		assertEquals(lines[line++], "" + fixedArray + compiler.toByteCodeNumber("5"));
		assertEquals(lines[line++], "" + VM_TYPE_ARRAY_BOOL + (char)1);
		assertEquals(lines[line++], "" + boolArray + compiler.toByteCodeNumber("15"));
		assertEquals(lines[line++], "" + VM_TYPE_ARRAY_CHAR + (char)1);
		assertEquals(lines[line++], "" + charArray + compiler.toByteCodeNumber("13"));
		assertEquals(lines[line++], "" + VM_TYPE_FUNCTION + (char)2);
		assertEquals(lines[line++], "" + start);
		assertEquals(lines[line++], "" + function);
		assertEquals(lines[line++], "" + VM_KEYWORD_FUNCTION);
		assertEquals(lines[line++], "" + int0 + OPERATOR_ADD + compiler.toByteCodeNumber("100"));
		assertEquals(lines[line++], "" + int0 + OPERATOR_SUBTRACT + int1);
		assertEquals(lines[line++], "" + int0 + OPERATOR_MULTIPLY + compiler.toByteCodeNumber("2"));
		assertEquals(lines[line++], "" + int0 + OPERATOR_DIVIDE + compiler.toByteCodeNumber("10"));
		assertEquals(lines[line++], "" + int1 + OPERATOR_ASSIGN + compiler.toByteCodeNumber("6"));
		assertEquals(lines[line++], "" + fixed0 + OPERATOR_ADD + fixed1);
		assertEquals(lines[line++], "" + fixed0 + OPERATOR_SUBTRACT + compiler.toByteCodeNumber("" + 0.5f * FIXED_POINT_ONE));
		assertEquals(lines[line++], "" + fixed0 + OPERATOR_MULTIPLY + compiler.toByteCodeNumber("" + 2.5f * FIXED_POINT_ONE));
		assertEquals(lines[line++], "" + fixed0 + OPERATOR_DIVIDE + compiler.toByteCodeNumber("" + 5f * FIXED_POINT_ONE));
		assertEquals(lines[line++], "" + fixed1 + OPERATOR_ASSIGN + compiler.toByteCodeNumber("" + 50f * FIXED_POINT_ONE));
		assertEquals(lines[line++], "" + bool0 + OPERATOR_ASSIGN + compiler.toByteCodeNumber("1"));
		assertEquals(lines[line++], "" + bool1 + OPERATOR_ASSIGN + compiler.toByteCodeNumber("0"));
		assertEquals(lines[line++], "" + bool2 + OPERATOR_ASSIGN + bool1);
		assertEquals(lines[line++], "" + char0 + OPERATOR_ASSIGN + compiler.toByteCodeNumber("65"));
		assertEquals(lines[line++], "" + char1 + OPERATOR_ASSIGN + compiler.toByteCodeNumber("66"));
		assertEquals(lines[line++], "" + char2 + OPERATOR_ASSIGN + char1);
		assertEquals(lines[line++], "" + intArray + compiler.toByteCodeNumber("0") + OPERATOR_ASSIGN + compiler.toByteCodeNumber("30"));
		assertEquals(lines[line++], "" + intArray + compiler.toByteCodeNumber("1") + OPERATOR_ADD + compiler.toByteCodeNumber("15"));
		assertEquals(lines[line++], "" + intArray + compiler.toByteCodeNumber("0") + OPERATOR_SUBTRACT + int1);
		assertEquals(lines[line++], "" + intArray + compiler.toByteCodeNumber("1") + OPERATOR_DIVIDE + compiler.toByteCodeNumber("5"));
		assertEquals(lines[line++], "" + intArray + compiler.toByteCodeNumber("0") + OPERATOR_MULTIPLY + intArray + compiler.toByteCodeNumber("1"));
		assertEquals(lines[line++], "" + intArray + compiler.toByteCodeNumber("7") + OPERATOR_ASSIGN + compiler.toByteCodeNumber("25"));
		assertEquals(lines[line++], "" + fixedArray + compiler.toByteCodeNumber("0") + OPERATOR_ASSIGN + compiler.toByteCodeNumber("" + 60.5f * FIXED_POINT_ONE));
		assertEquals(lines[line++], "" + fixedArray + compiler.toByteCodeNumber("1") + OPERATOR_ADD + compiler.toByteCodeNumber("" + 15f * FIXED_POINT_ONE));
		assertEquals(lines[line++], "" + fixedArray + compiler.toByteCodeNumber("0") + OPERATOR_SUBTRACT + fixed1);
		assertEquals(lines[line++], "" + fixedArray + compiler.toByteCodeNumber("1") + OPERATOR_DIVIDE + compiler.toByteCodeNumber("" + 5f * FIXED_POINT_ONE));
		assertEquals(lines[line++], "" + fixedArray + compiler.toByteCodeNumber("0") + OPERATOR_MULTIPLY + fixedArray + compiler.toByteCodeNumber("1"));
		assertEquals(lines[line++], "" + fixedArray + compiler.toByteCodeNumber("2") + OPERATOR_ASSIGN_NEGATIVE + compiler.toByteCodeNumber("" + 10f * FIXED_POINT_ONE));
		assertEquals(lines[line++], "" + fixedArray + compiler.toByteCodeNumber("3") + OPERATOR_ASSIGN_POSITIVE + fixedArray + compiler.toByteCodeNumber("2"));
		assertEquals(lines[line++], "" + fixedArray + compiler.toByteCodeNumber("4") + OPERATOR_ASSIGN_INVERSE + fixedArray + compiler.toByteCodeNumber("3"));
		assertEquals(lines[line++], "" + fixedArray + compiler.toByteCodeNumber("5") + OPERATOR_ASSIGN + compiler.toByteCodeNumber("" + 25.25f * FIXED_POINT_ONE));
		assertEquals(lines[line++], "" + boolArray + compiler.toByteCodeNumber("9") + OPERATOR_ASSIGN + bool0);
		assertEquals(lines[line++], "" + boolArray + compiler.toByteCodeNumber("10") + OPERATOR_ASSIGN + compiler.toByteCodeNumber("1"));
		assertEquals(lines[line++], "" + boolArray + compiler.toByteCodeNumber("11") + OPERATOR_ASSIGN + boolArray + compiler.toByteCodeNumber("10"));
		assertEquals(lines[line++], "" + boolArray + compiler.toByteCodeNumber("2") + OPERATOR_ASSIGN_NEGATIVE + compiler.toByteCodeNumber("1"));	
		assertEquals(lines[line++], "" + boolArray + compiler.toByteCodeNumber("3") + OPERATOR_ASSIGN_POSITIVE + boolArray + compiler.toByteCodeNumber("2"));	
		assertEquals(lines[line++], "" + boolArray + compiler.toByteCodeNumber("4") + OPERATOR_ASSIGN_INVERSE + boolArray + compiler.toByteCodeNumber("3"));	
		assertEquals(lines[line++], "" + boolArray + compiler.toByteCodeNumber("5") + OPERATOR_ASSIGN + compiler.toByteCodeNumber("1"));
		assertEquals(lines[line++], "" + charArray + compiler.toByteCodeNumber("9") + OPERATOR_ASSIGN + char0);
		assertEquals(lines[line++], "" + charArray + compiler.toByteCodeNumber("10") + OPERATOR_ASSIGN + compiler.toByteCodeNumber("67"));
		assertEquals(lines[line++], "" + charArray + compiler.toByteCodeNumber("11") + OPERATOR_ASSIGN + charArray + compiler.toByteCodeNumber("10"));
		assertEquals(lines[line++], "" + charArray + compiler.toByteCodeNumber("0") + OPERATOR_ASSIGN + compiler.toByteCodeNumber("65"));
		assertEquals(lines[line++], "" + VM_KEYWORD_IF + int0 + COMPARATOR_EQUALS + compiler.toByteCodeNumber("18"));
		assertEquals(lines[line++], "" + correctIfs + OPERATOR_ADD + compiler.toByteCodeNumber("1"));
		assertEquals(lines[line++], "" + VM_KEYWORD_IF + int1 + COMPARATOR_EQUALS + compiler.toByteCodeNumber("6"));
		assertEquals(lines[line++], "" + correctIfs + OPERATOR_ADD + compiler.toByteCodeNumber("1"));
		assertEquals(lines[line++], "" + VM_KEYWORD_ELSE);
		assertEquals(lines[line++], "" + correctIfs + OPERATOR_ADD + compiler.toByteCodeNumber("2"));
		assertEquals(lines[line++], "" + VM_KEYWORD_IF);
		assertEquals(lines[line++], "" + correctIfs + OPERATOR_ADD + compiler.toByteCodeNumber("1"));
		assertEquals(lines[line++], "" + VM_KEYWORD_ELSE);
		assertEquals(lines[line++], "" + correctIfs + OPERATOR_ADD + compiler.toByteCodeNumber("2"));
		assertEquals(lines[line++], "" + VM_KEYWORD_IF);
		assertEquals(lines[line++], "" + VM_KEYWORD_IF + bool0 + COMPARATOR_EQUALS + compiler.toByteCodeNumber("0"));
		assertEquals(lines[line++], "" + correctIfs + OPERATOR_ADD + compiler.toByteCodeNumber("1"));
		assertEquals(lines[line++], "" + VM_KEYWORD_ELSE_IF + bool0 + COMPARATOR_EQUALS + compiler.toByteCodeNumber("1"));
		assertEquals(lines[line++], "" + correctIfs + OPERATOR_ADD + compiler.toByteCodeNumber("2"));
		assertEquals(lines[line++], "" + VM_KEYWORD_IF);
		assertEquals(lines[line++], "" + VM_KEYWORD_IF + int0 + COMPARATOR_NOT_EQUALS + int1);
		assertEquals(lines[line++], "" + correctIfs + OPERATOR_ADD + compiler.toByteCodeNumber("1"));
		assertEquals(lines[line++], "" + VM_KEYWORD_ELSE);
		assertEquals(lines[line++], "" + correctIfs + OPERATOR_ADD + compiler.toByteCodeNumber("2"));
		assertEquals(lines[line++], "" + VM_KEYWORD_IF);
		assertEquals(lines[line++], "" + intArray + compiler.toByteCodeNumber("2") + OPERATOR_ASSIGN + compiler.toByteCodeNumber("100"));
		assertEquals(lines[line++], "" + VM_KEYWORD_IF + int0 + COMPARATOR_SMALLER + intArray + compiler.toByteCodeNumber("2"));
		assertEquals(lines[line++], "" + correctIfs + OPERATOR_ADD + compiler.toByteCodeNumber("1"));
		assertEquals(lines[line++], "" + VM_KEYWORD_ELSE);
		assertEquals(lines[line++], "" + correctIfs + OPERATOR_ADD + compiler.toByteCodeNumber("2"));
		assertEquals(lines[line++], "" + VM_KEYWORD_IF);
		assertEquals(lines[line++], "" + VM_KEYWORD_IF + intArray + compiler.toByteCodeNumber("2") + COMPARATOR_BIGGER + int0);
		assertEquals(lines[line++], "" + correctIfs + OPERATOR_ADD + compiler.toByteCodeNumber("1"));
		assertEquals(lines[line++], "" + VM_KEYWORD_ELSE);
		assertEquals(lines[line++], "" + correctIfs + OPERATOR_ADD + compiler.toByteCodeNumber("2"));
		assertEquals(lines[line++], "" + VM_KEYWORD_IF);
		assertEquals(lines[line++], "" + VM_KEYWORD_IF + int0 + COMPARATOR_SMALLER_EQUALS + intArray + compiler.toByteCodeNumber("2"));
		assertEquals(lines[line++], "" + correctIfs + OPERATOR_ADD + compiler.toByteCodeNumber("1"));
		assertEquals(lines[line++], "" + VM_KEYWORD_ELSE);
		assertEquals(lines[line++], "" + correctIfs + OPERATOR_ADD + compiler.toByteCodeNumber("2"));
		assertEquals(lines[line++], "" + VM_KEYWORD_IF);
		assertEquals(lines[line++], "" + VM_KEYWORD_IF + intArray + compiler.toByteCodeNumber("2") + COMPARATOR_BIGGER_EQUALS + int0);
		assertEquals(lines[line++], "" + correctIfs + OPERATOR_ADD + compiler.toByteCodeNumber("1"));
		assertEquals(lines[line++], "" + VM_KEYWORD_ELSE);
		assertEquals(lines[line++], "" + correctIfs + OPERATOR_ADD + compiler.toByteCodeNumber("2"));
		assertEquals(lines[line++], "" + VM_KEYWORD_IF);
		assertEquals(lines[line++], "" + intArray + compiler.toByteCodeNumber("3") + OPERATOR_ASSIGN + intArray + compiler.toByteCodeNumber("2"));
		assertEquals(lines[line++], "" + VM_KEYWORD_IF + intArray + compiler.toByteCodeNumber("2") + COMPARATOR_BIGGER_EQUALS + intArray + compiler.toByteCodeNumber("3"));
		assertEquals(lines[line++], "" + correctIfs + OPERATOR_ADD + compiler.toByteCodeNumber("1"));
		assertEquals(lines[line++], "" + VM_KEYWORD_ELSE);
		assertEquals(lines[line++], "" + correctIfs + OPERATOR_ADD + compiler.toByteCodeNumber("2"));
		assertEquals(lines[line++], "" + VM_KEYWORD_IF);
		assertEquals(lines[line++], "" + VM_KEYWORD_IF + intArray + compiler.toByteCodeNumber("2") + COMPARATOR_SMALLER_EQUALS + intArray + compiler.toByteCodeNumber("3"));
		assertEquals(lines[line++], "" + correctIfs + OPERATOR_ADD + compiler.toByteCodeNumber("1"));
		assertEquals(lines[line++], "" + VM_KEYWORD_ELSE);
		assertEquals(lines[line++], "" + correctIfs + OPERATOR_ADD + compiler.toByteCodeNumber("2"));
		assertEquals(lines[line++], "" + VM_KEYWORD_IF);
		assertEquals(lines[line++], "" + VM_KEYWORD_ARGUMENT + int0);
		assertEquals(lines[line++], "" + VM_KEYWORD_ARGUMENT + intArray);
		assertEquals(lines[line++], "" + function);
//		assertEquals(lines[line++], "" + libraryFunction + VM_KEYWORD_PARAMETER + intArray);
//		assertEquals(lines[line++], "" + directoryLibraryFunction + VM_KEYWORD_PARAMETER + intArray);		
		assertEquals(lines[line++], "" + VM_KEYWORD_FUNCTION);
		assertEquals(lines[line++], "" + fixed1 + OPERATOR_ADD + compiler.toByteCodeNumber("" + 25f * FIXED_POINT_ONE));
		assertEquals(lines[line++], "" + VM_KEYWORD_IF + fixed1 + COMPARATOR_SMALLER_EQUALS + compiler.toByteCodeNumber("" + 80f * FIXED_POINT_ONE));
		assertEquals(lines[line++], "" + VM_KEYWORD_FUNCTION_REPEAT);
		assertEquals(lines[line++], "" + VM_KEYWORD_IF);
		assertEquals(lines[line++], "" + param0 + OPERATOR_ADD + compiler.toByteCodeNumber("100"));
		assertEquals(lines[line++], "" + param1 + compiler.toByteCodeNumber("5") + OPERATOR_ASSIGN + param0);
		assertEquals(lines[line++], "" + param1 + compiler.toByteCodeNumber("4") + OPERATOR_ASSIGN + param1 + compiler.toByteCodeNumber("5"));
		assertEquals(lines[line++], "" + param0 + OPERATOR_ADD + param1 + compiler.toByteCodeNumber("4"));
		assertEquals(lines[line++], "" + param1 + int1 + OPERATOR_ASSIGN + int1);
		assertEquals(lines[line++], "" + param0 + OPERATOR_SUBTRACT + param1 + int1);
		assertEquals(lines[line++], "" + VM_KEYWORD_IF + param0 + COMPARATOR_SMALLER + param1 + compiler.toByteCodeNumber("5"));
		assertEquals(lines[line++], "" + correctIfs + OPERATOR_ADD + compiler.toByteCodeNumber("1"));
		assertEquals(lines[line++], "" + VM_KEYWORD_ELSE);
		assertEquals(lines[line++], "" + correctIfs + OPERATOR_ADD + compiler.toByteCodeNumber("2"));
		assertEquals(lines[line++], "" + VM_KEYWORD_IF);		
//		assertEquals(lines[line++], "" + VM_KEYWORD_FUNCTION + libraryFunction);
//		assertEquals(lines[line++], "" + param0 + jooCompiler.toByteCodeNumber("0") + VM_OPERATOR_ADD + jooCompiler.toByteCodeNumber("10"));
//		assertEquals(lines[line++], "" + VM_KEYWORD_FUNCTION + directoryLibraryFunction);
//		assertEquals(lines[line++], "" + param0 + jooCompiler.toByteCodeNumber("0") + VM_OPERATOR_ADD + jooCompiler.toByteCodeNumber("20"));
		
		
		
		
//		final char int0 = 0 + VM_COMPONENTS_START;
//		final char int1 = 1 + VM_COMPONENTS_START;
//		final char correctIfs = 2 + VM_COMPONENTS_START;
//		final char fixed0 = 3 + VM_COMPONENTS_START;
//		final char fixed1 = 4 + VM_COMPONENTS_START;
//		final char bool0 = 5 + VM_COMPONENTS_START;
//		final char bool1 = 6 + VM_COMPONENTS_START;
//		final char bool2 = 7 + VM_COMPONENTS_START;
//		final char char0 = 8 + VM_COMPONENTS_START;
//		final char char1 = 9 + VM_COMPONENTS_START;
//		final char char2 = 10 + VM_COMPONENTS_START;
//		
//		final char intArray = 11 + VM_COMPONENTS_START;
//		final char fixedArray = 12 + VM_COMPONENTS_START;
//		final char boolArray = 13 + VM_COMPONENTS_START;
//		final char charArray = 14 + VM_COMPONENTS_START;
//		
//		final char start = 15 + VM_COMPONENTS_START;
//		final char function = 16 + VM_COMPONENTS_START;
//		final char libraryFunction = 17 + VM_COMPONENTS_START;
//		final char directoryLibraryFunction = 18 + VM_COMPONENTS_START;
//		
//		final char param0 = 0 + VM_PARAMETERS_START;
//		final char param1 = 1 + VM_PARAMETERS_START;
//
//		int line = 0;
//		assertEquals(jooLines[line++], "" + VM_TYPE_INT + (char)3);
//		assertEquals(jooLines[line++], "" + int0);
//		assertEquals(jooLines[line++], "" + int1 + toBytecodeNumber("10"));
//		assertEquals(jooLines[line++], "" + correctIfs);
//		assertEquals(jooLines[line++], "" + VM_TYPE_FIXED + (char)2);
//		assertEquals(jooLines[line++], "" + fixed0);
//		assertEquals(jooLines[line++], "" + fixed1 + toBytecodeNumber("" + 100.5f * FIXED_POINT_ONE));
//		assertEquals(jooLines[line++], "" + VM_TYPE_BOOL + (char)3);
//		assertEquals(jooLines[line++], "" + bool0);
//		assertEquals(jooLines[line++], "" + bool1 + toBytecodeNumber("1"));
//		assertEquals(jooLines[line++], "" + bool2 + toBytecodeNumber("0"));
//		assertEquals(jooLines[line++], "" + VM_TYPE_CHAR + (char)3);
//		assertEquals(jooLines[line++], "" + char0);
//		assertEquals(jooLines[line++], "" + char1 + 'A');
//		assertEquals(jooLines[line++], "" + char2 + 'C');
//		assertEquals(jooLines[line++], "" + VM_TYPE_ARRAY_INT + (char)1);
//		assertEquals(jooLines[line++], "" + intArray + (char)10);
//		assertEquals(jooLines[line++], "" + VM_TYPE_ARRAY_FIXED + (char)1);
//		assertEquals(jooLines[line++], "" + fixedArray + (char)5);
//		assertEquals(jooLines[line++], "" + VM_TYPE_ARRAY_BOOL + (char)1);
//		assertEquals(jooLines[line++], "" + boolArray + (char)15);
//		assertEquals(jooLines[line++], "" + VM_TYPE_ARRAY_CHAR + (char)1);
//		assertEquals(jooLines[line++], "" + charArray + (char)13);
//		assertEquals(jooLines[line++], "" + VM_TYPE_FUNCTION + (char)4);
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_FUNCTION + start);
//		assertEquals(jooLines[line++], "" + int0 + VM_OPERATOR_ADD + toBytecodeNumber("100"));
//		assertEquals(jooLines[line++], "" + int0 + VM_OPERATOR_SUBTRACT + int1);
//		assertEquals(jooLines[line++], "" + int0 + VM_OPERATOR_MULTIPLY + toBytecodeNumber("2"));
//		assertEquals(jooLines[line++], "" + int0 + VM_OPERATOR_DIVIDE + toBytecodeNumber("10"));
//		assertEquals(jooLines[line++], "" + int1 + VM_OPERATOR_ASSIGN + toBytecodeNumber("6"));
//		assertEquals(jooLines[line++], "" + fixed0 + VM_OPERATOR_ADD + fixed1);
//		assertEquals(jooLines[line++], "" + fixed0 + VM_OPERATOR_SUBTRACT + toBytecodeNumber("" + 0.5f * FIXED_POINT_ONE));
//		assertEquals(jooLines[line++], "" + fixed0 + VM_OPERATOR_MULTIPLY + toBytecodeNumber("" + 2.5f * FIXED_POINT_ONE));
//		assertEquals(jooLines[line++], "" + fixed0 + VM_OPERATOR_DIVIDE + toBytecodeNumber("" + 5f * FIXED_POINT_ONE));
//		assertEquals(jooLines[line++], "" + fixed1 + VM_OPERATOR_ASSIGN + toBytecodeNumber("" + 50f * FIXED_POINT_ONE));
//		assertEquals(jooLines[line++], "" + bool0 + VM_OPERATOR_ASSIGN + toBytecodeNumber("1"));
//		assertEquals(jooLines[line++], "" + bool1 + VM_OPERATOR_ASSIGN + toBytecodeNumber("0"));
//		assertEquals(jooLines[line++], "" + bool2 + VM_OPERATOR_ASSIGN + bool1);
//		assertEquals(jooLines[line++], "" + char0 + VM_OPERATOR_ASSIGN + VM_TYPE_CHAR + 'A');
//		assertEquals(jooLines[line++], "" + char1 + VM_OPERATOR_ASSIGN + VM_TYPE_CHAR + 'B');
//		assertEquals(jooLines[line++], "" + char2 + VM_OPERATOR_ASSIGN + char1);
//		assertEquals(jooLines[line++], "" + intArray + toIndex(0) + VM_OPERATOR_ASSIGN + toBytecodeNumber("30"));
//		assertEquals(jooLines[line++], "" + intArray + toIndex(1) + VM_OPERATOR_ADD + toBytecodeNumber("15"));
//		assertEquals(jooLines[line++], "" + intArray + toIndex(0) + VM_OPERATOR_SUBTRACT + int1);
//		assertEquals(jooLines[line++], "" + intArray + toIndex(1) + VM_OPERATOR_DIVIDE + toBytecodeNumber("5"));
//		assertEquals(jooLines[line++], "" + intArray + toIndex(0) + VM_OPERATOR_MULTIPLY + intArray + toIndex(1));
//		assertEquals(jooLines[line++], "" + intArray + toIndex(7) + VM_OPERATOR_ASSIGN + toBytecodeNumber("25"));
//		assertEquals(jooLines[line++], "" + fixedArray + toIndex(0) + VM_OPERATOR_ASSIGN + toBytecodeNumber("" + 60.5f * FIXED_POINT_ONE));
//		assertEquals(jooLines[line++], "" + fixedArray + toIndex(1) + VM_OPERATOR_ADD + toBytecodeNumber("" + 15f * FIXED_POINT_ONE));
//		assertEquals(jooLines[line++], "" + fixedArray + toIndex(0) + VM_OPERATOR_SUBTRACT + fixed1);
//		assertEquals(jooLines[line++], "" + fixedArray + toIndex(1) + VM_OPERATOR_DIVIDE + toBytecodeNumber("" + 5f * FIXED_POINT_ONE));
//		assertEquals(jooLines[line++], "" + fixedArray + toIndex(0) + VM_OPERATOR_MULTIPLY + fixedArray + toIndex(1));
//		assertEquals(jooLines[line++], "" + fixedArray + toIndex(2) + VM_OPERATOR_ASSIGN_NEGATIVE + toBytecodeNumber("" + 10f * FIXED_POINT_ONE));
//		assertEquals(jooLines[line++], "" + fixedArray + toIndex(3) + VM_OPERATOR_ASSIGN_POSITIVE + fixedArray + toIndex(2));
//		assertEquals(jooLines[line++], "" + fixedArray + toIndex(4) + VM_OPERATOR_ASSIGN_INVERSE + fixedArray + toIndex(3));
//		assertEquals(jooLines[line++], "" + fixedArray + toIndex(5) + VM_OPERATOR_ASSIGN + toBytecodeNumber("" + 25.25f * FIXED_POINT_ONE));
//		assertEquals(jooLines[line++], "" + boolArray + toIndex(9) + VM_OPERATOR_ASSIGN + bool0);
//		assertEquals(jooLines[line++], "" + boolArray + toIndex(10) + VM_OPERATOR_ASSIGN + toBytecodeNumber("1"));
//		assertEquals(jooLines[line++], "" + boolArray + toIndex(11) + VM_OPERATOR_ASSIGN + boolArray + toIndex(10));
//		assertEquals(jooLines[line++], "" + boolArray + toIndex(2) + VM_OPERATOR_ASSIGN_NEGATIVE + toBytecodeNumber("1"));	
//		assertEquals(jooLines[line++], "" + boolArray + toIndex(3) + VM_OPERATOR_ASSIGN_POSITIVE + boolArray + toIndex(2));	
//		assertEquals(jooLines[line++], "" + boolArray + toIndex(4) + VM_OPERATOR_ASSIGN_INVERSE + boolArray + toIndex(3));	
//		assertEquals(jooLines[line++], "" + boolArray + toIndex(5) + VM_OPERATOR_ASSIGN + toBytecodeNumber("1"));
//		assertEquals(jooLines[line++], "" + charArray + toIndex(9) + VM_OPERATOR_ASSIGN + char0);
//		assertEquals(jooLines[line++], "" + charArray + toIndex(10) + VM_OPERATOR_ASSIGN + VM_TYPE_CHAR + 'C');
//		assertEquals(jooLines[line++], "" + charArray + toIndex(11) + VM_OPERATOR_ASSIGN + charArray + toIndex(10));
//		assertEquals(jooLines[line++], "" + charArray + toIndex(0) + VM_OPERATOR_ASSIGN + VM_TYPE_CHAR + 'd');
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_IF + int0 + VM_COMPARATOR_EQUALS + toBytecodeNumber("18"));
//		assertEquals(jooLines[line++], "" + correctIfs + VM_OPERATOR_ADD + toBytecodeNumber("1"));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_IF + int1 + VM_COMPARATOR_EQUALS + toBytecodeNumber("6"));
//		assertEquals(jooLines[line++], "" + correctIfs + VM_OPERATOR_ADD + toBytecodeNumber("1"));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_ELSE);
//		assertEquals(jooLines[line++], "" + correctIfs + VM_OPERATOR_ADD + toBytecodeNumber("2"));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_IF);
//		assertEquals(jooLines[line++], "" + correctIfs + VM_OPERATOR_ADD + toBytecodeNumber("1"));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_ELSE);
//		assertEquals(jooLines[line++], "" + correctIfs + VM_OPERATOR_ADD + toBytecodeNumber("2"));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_IF);
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_IF + bool0 + VM_COMPARATOR_EQUALS + toBytecodeNumber("0"));
//		assertEquals(jooLines[line++], "" + correctIfs + VM_OPERATOR_ADD + toBytecodeNumber("1"));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_ELSE_IF + bool0 + VM_COMPARATOR_EQUALS + toBytecodeNumber("1"));
//		assertEquals(jooLines[line++], "" + correctIfs + VM_OPERATOR_ADD + toBytecodeNumber("2"));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_IF);
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_IF + int0 + VM_COMPARATOR_NOT_EQUALS + int1);
//		assertEquals(jooLines[line++], "" + correctIfs + VM_OPERATOR_ADD + toBytecodeNumber("1"));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_ELSE);
//		assertEquals(jooLines[line++], "" + correctIfs + VM_OPERATOR_ADD + toBytecodeNumber("2"));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_IF);
//		assertEquals(jooLines[line++], "" + intArray + toIndex(2) + VM_OPERATOR_ASSIGN + toBytecodeNumber("100"));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_IF + int0 + VM_COMPARATOR_SMALLER + intArray + toIndex(2));
//		assertEquals(jooLines[line++], "" + correctIfs + VM_OPERATOR_ADD + toBytecodeNumber("1"));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_ELSE);
//		assertEquals(jooLines[line++], "" + correctIfs + VM_OPERATOR_ADD + toBytecodeNumber("2"));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_IF);
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_IF + intArray + toIndex(2) + VM_COMPARATOR_BIGGER + int0);
//		assertEquals(jooLines[line++], "" + correctIfs + VM_OPERATOR_ADD + toBytecodeNumber("1"));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_ELSE);
//		assertEquals(jooLines[line++], "" + correctIfs + VM_OPERATOR_ADD + toBytecodeNumber("2"));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_IF);
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_IF + int0 + VM_COMPARATOR_SMALLER_EQUALS + intArray + toIndex(2));
//		assertEquals(jooLines[line++], "" + correctIfs + VM_OPERATOR_ADD + toBytecodeNumber("1"));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_ELSE);
//		assertEquals(jooLines[line++], "" + correctIfs + VM_OPERATOR_ADD + toBytecodeNumber("2"));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_IF);
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_IF + intArray + toIndex(2) + VM_COMPARATOR_BIGGER_EQUALS + int0);
//		assertEquals(jooLines[line++], "" + correctIfs + VM_OPERATOR_ADD + toBytecodeNumber("1"));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_ELSE);
//		assertEquals(jooLines[line++], "" + correctIfs + VM_OPERATOR_ADD + toBytecodeNumber("2"));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_IF);
//		assertEquals(jooLines[line++], "" + intArray + toIndex(3) + VM_OPERATOR_ASSIGN + intArray + toIndex(2));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_IF + intArray + toIndex(2) + VM_COMPARATOR_BIGGER_EQUALS + intArray + toIndex(3));
//		assertEquals(jooLines[line++], "" + correctIfs + VM_OPERATOR_ADD + toBytecodeNumber("1"));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_ELSE);
//		assertEquals(jooLines[line++], "" + correctIfs + VM_OPERATOR_ADD + toBytecodeNumber("2"));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_IF);
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_IF + intArray + toIndex(2) + VM_COMPARATOR_SMALLER_EQUALS + intArray + toIndex(3));
//		assertEquals(jooLines[line++], "" + correctIfs + VM_OPERATOR_ADD + toBytecodeNumber("1"));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_ELSE);
//		assertEquals(jooLines[line++], "" + correctIfs + VM_OPERATOR_ADD + toBytecodeNumber("2"));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_IF);
//		assertEquals(jooLines[line++], "" + function + VM_KEYWORD_PARAMETER + int0 + VM_KEYWORD_PARAMETER + intArray);
//		assertEquals(jooLines[line++], "" + libraryFunction + VM_KEYWORD_PARAMETER + intArray);
//		assertEquals(jooLines[line++], "" + directoryLibraryFunction + VM_KEYWORD_PARAMETER + intArray);		
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_FUNCTION + function);
//		assertEquals(jooLines[line++], "" + fixed1 + VM_OPERATOR_ADD + toBytecodeNumber("" + 25f * FIXED_POINT_ONE));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_IF + fixed1 + VM_COMPARATOR_SMALLER_EQUALS + toBytecodeNumber("" + 80f * FIXED_POINT_ONE));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_FUNCTION_REPEAT);
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_IF);
//		assertEquals(jooLines[line++], "" + param0 + VM_OPERATOR_ADD + toBytecodeNumber("100"));
//		assertEquals(jooLines[line++], "" + param1 + toIndex(5) + VM_OPERATOR_ASSIGN + param0);
//		assertEquals(jooLines[line++], "" + param1 + toIndex(4) + VM_OPERATOR_ASSIGN + param1 + toIndex(5));
//		assertEquals(jooLines[line++], "" + param0 + VM_OPERATOR_ADD + param1 + toIndex(4));
//		assertEquals(jooLines[line++], "" + param1 + int1 + VM_OPERATOR_ASSIGN + int1);
//		assertEquals(jooLines[line++], "" + param0 + VM_OPERATOR_SUBTRACT + param1 + int1);
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_IF + param0 + VM_COMPARATOR_SMALLER + param1 + toIndex(5));
//		assertEquals(jooLines[line++], "" + correctIfs + VM_OPERATOR_ADD + toBytecodeNumber("1"));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_ELSE);
//		assertEquals(jooLines[line++], "" + correctIfs + VM_OPERATOR_ADD + toBytecodeNumber("2"));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_IF);
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_FUNCTION + libraryFunction);
//		assertEquals(jooLines[line++], "" + param0 + toIndex(0) + VM_OPERATOR_ADD + toBytecodeNumber("10"));
//		assertEquals(jooLines[line++], "" + VM_KEYWORD_FUNCTION + directoryLibraryFunction);
//		assertEquals(jooLines[line++], "" + param0 + toIndex(0) + VM_OPERATOR_ADD + toBytecodeNumber("20"));
	}
	
	private void createTypeRegistry(Code code) {
		final JooCompiler compiler = new JooCompiler();
		final CodeComponent typeRegistry = new CodeComponent(KEYWORD_TYPE_REGISTRY, (byte) 0, KEYWORD_TYPE_REGISTRY, (byte) 0, 0);
		for (int i = 0; i <= 9; i++) {
			final byte typeCount = (byte) 0;
			final byte byteCodeType = (byte) (VM_TYPE_INT - i);
			final String type = compiler.toType(byteCodeType); 
			final CodeComponent typeComponent = new CodeComponent("" + typeCount, typeCount, type, byteCodeType, 0);
			typeRegistry.addComponent(typeComponent);
		}
		code.addComponent(typeRegistry);
	}
}
