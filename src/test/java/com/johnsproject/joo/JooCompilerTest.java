package com.johnsproject.joo;

import java.text.ParseException;

import org.junit.Test;

import com.johnsproject.joo.util.FileUtil;

import static com.johnsproject.joo.JooCompiler.*;
import static org.junit.Assert.assertArrayEquals;
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
	public void addIncludedCodeTest() throws Exception {
		final JooCompiler compiler = new JooCompiler();
		
		String[] testCode = new String[] {"include TestLibrary.joo"};
		String[] fileCode = compiler.getLines(FileUtil.read("TestLibrary.joo"));
		testCode = compiler.addIncludedCode(testCode);
		
		for (int i = 0; i < fileCode.length; i++) {
			assertEquals(testCode[i + 1], fileCode[i]);
		}
		
		try {
			testCode = new String[] {"include"};
			testCode = compiler.addIncludedCode(testCode);
			fail("Invalid inclusion declaration exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid inclusion declaration");
		}
	}
	
	@Test
	public void parseConstantsTest() throws Exception {
		final JooCompiler compiler = new JooCompiler();
		
		String[] codeLines = new String[] {
				"constant TEST_CONSTANT = 10",
				"int int0 = TEST_CONSTANT"
				};
		
		Code code = new Code();
		compiler.parseConstants(code, codeLines);
		assertEquals(codeLines[1], "int int0 = 10");
		
		int constantIndex = 0;
		CodeComponent constantComponent = code.getComponent(constantIndex++);
		assert(constantComponent.hasName("TEST_CONSTANT"));
		assert(constantComponent.hasType(KEYWORD_CONSTANT));
		assert(constantComponent.hasComponent("10", KEYWORD_CONSTANT));
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"constant TEST_CONSTANT = "};
			compiler.parseConstants(code, codeLines);
			fail("Invalid constant declaration exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid constant declaration");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"constant TEST_CONSTANT + 10 "};
			compiler.parseConstants(code, codeLines);
			fail("Invalid assignment operator exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid constant assignment operator, Operator: +, Constant: TEST_CONSTANT");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {
					"constant TEST_CONSTANT = 10",
					"constant TEST_CONSTANT = 5"
					};
			compiler.parseConstants(code, codeLines);
			fail("Duplicate constant exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Duplicate constant, Constant: TEST_CONSTANT");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"constant TeST_CONSTANT = 10"};
			compiler.parseConstants(code, codeLines);
			fail("Constant names should only contain alphanumeric uppercase characters exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Constant names should contain only uppercase alphanumeric characters, Constant: TeST_CONSTANT");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"constant 0EST_CONSTANT = 10"};
			compiler.parseConstants(code, codeLines);
			fail("Constant names should start with alphabetic characters exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Constant names should start with a alphabetic character, Constant: 0EST_CONSTANT");
		}
	}
	
	@Test
	public void parseOperatorsTest() throws Exception {
		final JooCompiler compiler = new JooCompiler();
		
		String[] codeLines = new String[] {
				"operator + int|fixed",
				"operator = int|fixed|char|bool"
				};
		
		Code code = new Code();
		compiler.parseOperators(code, codeLines);
		
		int operatorIndex = 0, typeIndex = 0;
		CodeComponent operatorComponent = code.getComponent(operatorIndex++);
		assert(operatorComponent.hasName("+"));
		assert(operatorComponent.hasType(KEYWORD_OPERATOR));
		assert(operatorComponent.hasName((byte) 1));

		CodeComponent typeComponent = operatorComponent.getComponent(typeIndex++);
		assert(typeComponent.hasName(TYPE_INT));
		assert(typeComponent.hasName((byte)VM_TYPE_INT));

		typeComponent = operatorComponent.getComponent(typeIndex++);
		assert(typeComponent.hasName(TYPE_FIXED));
		assert(typeComponent.hasName((byte)VM_TYPE_FIXED));

		typeIndex = 0;
		operatorComponent = code.getComponent(operatorIndex++);
		assert(operatorComponent.hasName("="));
		assert(operatorComponent.hasType(KEYWORD_OPERATOR));
		assert(operatorComponent.hasName((byte) 2));

		typeComponent = operatorComponent.getComponent(typeIndex++);
		assert(typeComponent.hasName(TYPE_INT));
		assert(typeComponent.hasName((byte)VM_TYPE_INT));

		typeComponent = operatorComponent.getComponent(typeIndex++);
		assert(typeComponent.hasName(TYPE_FIXED));
		assert(typeComponent.hasName((byte)VM_TYPE_FIXED));

		typeComponent = operatorComponent.getComponent(typeIndex++);
		assert(typeComponent.hasName(TYPE_CHAR));
		assert(typeComponent.hasName((byte)VM_TYPE_CHAR));

		typeComponent = operatorComponent.getComponent(typeIndex++);
		assert(typeComponent.hasName(TYPE_BOOL));
		assert(typeComponent.hasName((byte)VM_TYPE_BOOL));
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"operator *"};
			compiler.parseOperators(code, codeLines);
			fail("Invalid operator declaration exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid operator declaration");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {
					"operator * int",
					"operator * bool"
					};
			compiler.parseOperators(code, codeLines);
			fail("Duplicate operator exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Duplicate operator, Operator: *");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"operator - a"};
			compiler.parseOperators(code, codeLines);
			fail("Invalid supported type exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid supported type, Type: a");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"operator a int"};
			compiler.parseOperators(code, codeLines);
			fail("Operators shouldn't contain alphanumeric characters exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Operators shouldn't contain alphanumeric characters, Operator: a");
		}
	}
	
	@Test
	public void parseNativesTest() throws Exception {
		final JooCompiler compiler = new JooCompiler();
		
		String[] codeLines = new String[] {
				"native HelloWorld int|fixed bool",
				"native WorldHello int|bool"
				};
		
		Code code = new Code();
		compiler.parseNatives(code, codeLines);
	
		int nativeIndex = 0, parameterIndex = 0, typeIndex = 0;
		CodeComponent nativeComponent = code.getComponent(nativeIndex++);
		assert(nativeComponent.hasName("HelloWorld"));
		assert(nativeComponent.hasType(KEYWORD_NATIVE));
		assert(nativeComponent.hasName((byte) 1));

		CodeComponent parameterComponent = nativeComponent.getComponent(parameterIndex++);
		assert(parameterComponent.hasName("param0"));
		assert(parameterComponent.hasType(TYPE_PARAMETER));
		assert(parameterComponent.hasName((byte)1));

		CodeComponent typeComponent = parameterComponent.getComponent(typeIndex++);
		assert(typeComponent.hasName(TYPE_INT));
		assert(typeComponent.hasName((byte)VM_TYPE_INT));
		
		typeComponent = parameterComponent.getComponent(typeIndex++);
		assert(typeComponent.hasName(TYPE_FIXED));
		assert(typeComponent.hasName((byte)VM_TYPE_FIXED));

		typeIndex = 0;
		parameterComponent = nativeComponent.getComponent(parameterIndex++);
		assert(parameterComponent.hasName("param1"));
		assert(parameterComponent.hasType(TYPE_PARAMETER));
		assert(parameterComponent.hasName((byte)2));
		
		typeComponent = parameterComponent.getComponent(typeIndex++);
		assert(typeComponent.hasName(TYPE_BOOL));
		assert(typeComponent.hasName((byte)VM_TYPE_BOOL));
		
		parameterIndex = 0;
		nativeComponent = code.getComponent(nativeIndex++);
		assert(nativeComponent.hasName("WorldHello"));
		assert(nativeComponent.hasType(KEYWORD_NATIVE));
		assert(nativeComponent.hasName((byte) 2));
		
		typeIndex = 0;
		parameterComponent = nativeComponent.getComponent(parameterIndex++);
		assert(parameterComponent.hasName("param0"));
		assert(parameterComponent.hasType(TYPE_PARAMETER));
		assert(parameterComponent.hasName((byte)1));

		typeComponent = parameterComponent.getComponent(typeIndex++);
		assert(typeComponent.hasName(TYPE_INT));
		assert(typeComponent.hasName((byte)VM_TYPE_INT));

		typeComponent = parameterComponent.getComponent(typeIndex++);
		assert(typeComponent.hasName(TYPE_BOOL));
		assert(typeComponent.hasName((byte)VM_TYPE_BOOL));
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"native"};
			compiler.parseNatives(code, codeLines);
			fail("Invalid native declaration exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid native declaration");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {
					"native Hello",
					"native Hello"
					};
			compiler.parseNatives(code, codeLines);
			fail("Duplicate native exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Duplicate native, Native: Hello");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {
					"native Hello a"
					};
			compiler.parseNatives(code, codeLines);
			fail("Invalid supported type exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid supported type, Type: a");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"native 0ello"};
			compiler.parseNatives(code, codeLines);
			fail("Native names should start with alphabetic character exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Native names should start with a alphabetic character, Native: 0ello");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"native hello"};
			compiler.parseNatives(code, codeLines);
			fail("Native names should start with uppercase character exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Native names should start with a uppercase character, Native: hello");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"native Hell@"};
			compiler.parseNatives(code, codeLines);
			fail("Native names should only contain alphanumeric characters exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Native names should contain only alphanumeric characters, Native: Hell@");
		}
	}
	
	@Test
	public void parseVariablesTest() throws Exception {
		final JooCompiler compiler = new JooCompiler();
		String[] codeLines = new String[] {
				"int testInt = 10",
				"int withoutValue",
				"fixed testFixed = 20.5",
				"bool testBoolTrue = true",
				"bool testBoolFalse = false",
				"char testChar = 'A'",
				"int:10 testIntArray",
				"fixed:15 testFixedArray",
				"bool:5 testBoolArray",
				"char:3 testCharArray",
				};
		
		Code code = new Code();		
		compiler.parseVariables(code, codeLines);
		
		int variableIndex = 0;
		CodeComponent variableComponent = code.getComponent(variableIndex++);
		assert(variableComponent.hasName("testInt"));
		assert(variableComponent.hasType(TYPE_INT));
		assert(variableComponent.hasName((byte) 1));
		assert(variableComponent.hasType((byte) VM_TYPE_INT));
		assert(variableComponent.hasComponent("10", TYPE_INT));

		variableComponent = code.getComponent(variableIndex++);
		assert(variableComponent.hasName("withoutValue"));
		assert(variableComponent.hasType(TYPE_INT));
		assert(variableComponent.hasName((byte) 2));
		assert(variableComponent.hasType((byte) VM_TYPE_INT));

		variableComponent = code.getComponent(variableIndex++);
		assert(variableComponent.hasName("testFixed"));
		assert(variableComponent.hasType(TYPE_FIXED));
		assert(variableComponent.hasName((byte) 1));
		assert(variableComponent.hasType((byte) VM_TYPE_FIXED));
		assert(variableComponent.hasComponent("5228", TYPE_FIXED));

		variableComponent = code.getComponent(variableIndex++);
		assert(variableComponent.hasName("testBoolTrue"));
		assert(variableComponent.hasType(TYPE_BOOL));
		assert(variableComponent.hasName((byte) 1));
		assert(variableComponent.hasType((byte) VM_TYPE_BOOL));
		assert(variableComponent.hasComponent("1", TYPE_BOOL));

		variableComponent = code.getComponent(variableIndex++);
		assert(variableComponent.hasName("testBoolFalse"));
		assert(variableComponent.hasType(TYPE_BOOL));
		assert(variableComponent.hasName((byte) 2));
		assert(variableComponent.hasType((byte) VM_TYPE_BOOL));
		assert(variableComponent.hasComponent("0", TYPE_BOOL));

		variableComponent = code.getComponent(variableIndex++);
		assert(variableComponent.hasName("testChar"));
		assert(variableComponent.hasType(TYPE_CHAR));
		assert(variableComponent.hasName((byte) 1));
		assert(variableComponent.hasType((byte) VM_TYPE_CHAR));
		assert(variableComponent.hasComponent("65", TYPE_CHAR));

		variableComponent = code.getComponent(variableIndex++);
		assert(variableComponent.hasName("testIntArray"));
		assert(variableComponent.hasType(TYPE_ARRAY_INT));
		assert(variableComponent.hasName((byte) 1));
		assert(variableComponent.hasType((byte) VM_TYPE_ARRAY_INT));
		assert(variableComponent.hasComponent("10", KEYWORD_ARRAY));

		variableComponent = code.getComponent(variableIndex++);
		assert(variableComponent.hasName("testFixedArray"));
		assert(variableComponent.hasType(TYPE_ARRAY_FIXED));
		assert(variableComponent.hasName((byte) 1));
		assert(variableComponent.hasType((byte) VM_TYPE_ARRAY_FIXED));
		assert(variableComponent.hasComponent("15", KEYWORD_ARRAY));

		variableComponent = code.getComponent(variableIndex++);
		assert(variableComponent.hasName("testBoolArray"));
		assert(variableComponent.hasType(TYPE_ARRAY_BOOL));
		assert(variableComponent.hasName((byte) 1));
		assert(variableComponent.hasType((byte) VM_TYPE_ARRAY_BOOL));
		assert(variableComponent.hasComponent("5", KEYWORD_ARRAY));

		variableComponent = code.getComponent(variableIndex++);
		assert(variableComponent.hasName("testCharArray"));
		assert(variableComponent.hasType(TYPE_ARRAY_CHAR));
		assert(variableComponent.hasName((byte) 1));
		assert(variableComponent.hasType((byte) VM_TYPE_ARRAY_CHAR));
		assert(variableComponent.hasComponent("3", KEYWORD_ARRAY));
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"int test ="};
			compiler.parseVariables(code, codeLines);
			fail("Invalid variable declaration exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid variable declaration");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"int:A test"};
			compiler.parseVariables(code, codeLines);
			fail("Invalid array size declaration exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid array size declaration, Size: A");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"int:15 test = 10"};
			compiler.parseVariables(code, codeLines);
			fail("Invalid array declaration exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid array declaration. Value can't be assigned to array");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"int test - 10"};
			compiler.parseVariables(code, codeLines);
			fail("Invalid assignment operator exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid assignment operator, Operator: -");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"char test = 'A''"};
			compiler.parseVariables(code, codeLines);
			fail("Invalid char value declaration exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid char value declaration");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"int test = A"};
			compiler.parseVariables(code, codeLines);
			fail("The declared value type doesn't match the variable type exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "The declared value type doesn't match the variable type");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"int 0test = 10"};
			compiler.parseVariables(code, codeLines);
			fail("Variable names should start with a alphabetic character exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Variable names should start with a alphabetic character, Variable: 0test");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"int Test = 10"};
			compiler.parseVariables(code, codeLines);
			fail("Variable names should start with a lowercase character exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Variable names should start with a lowercase character, Variable: Test");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"int my_test = 10"};
			compiler.parseVariables(code, codeLines);
			fail("Variable names should contain only alphanumeric characters exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Variable names should contain only alphanumeric characters, Variable: my_test");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {
					"int abc = 10",
					"int abc = 10"
					};
			compiler.parseVariables(code, codeLines);
			fail("Duplicate variable exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Duplicate variable, Variable: abc");
		}
	}
	
	@Test
	public void parseFunctionsTest() throws Exception {
		final JooCompiler compiler = new JooCompiler();
		String[] codeLines = new String[] {
				"function TestPlain",
				"endFunction",
				"function TestParam int _intParam fixed _fixedParam bool _boolParam char _charParam int: _intArrayParam",
				"endFunction",
				};
		
		Code code = new Code();		
		compiler.parseFunctions(code, codeLines);
		
		int functionIndex = 0, paramIndex = 0;		
		CodeComponent functionComponent = code.getComponent(functionIndex++);
		assert(functionComponent.hasName("TestPlain"));
		assert(functionComponent.hasType(KEYWORD_FUNCTION));
		assert(functionComponent.hasName((byte) 1));
		assert(functionComponent.hasType((byte) VM_TYPE_FUNCTION));
		
		functionComponent = code.getComponent(functionIndex++);
		assert(functionComponent.hasName("endFunction"));
		assert(functionComponent.hasType(KEYWORD_FUNCTION_END));

		functionComponent = code.getComponent(functionIndex++);
		assert(functionComponent.hasName("TestParam"));
		assert(functionComponent.hasType(KEYWORD_FUNCTION));
		assert(functionComponent.hasName((byte) 2));
		assert(functionComponent.hasType((byte) VM_TYPE_FUNCTION));
		
		functionComponent = code.getComponent(functionIndex++);
		assert(functionComponent.hasName("endFunction"));
		assert(functionComponent.hasType(KEYWORD_FUNCTION_END));

		functionComponent = code.getComponentWithName("TestParam");
		CodeComponent paramComponent = functionComponent.getComponent(paramIndex++);
		assert(paramComponent.hasName("_intParam"));
		assert(paramComponent.hasType(TYPE_INT));
		assert(paramComponent.hasName((byte) (0 + VM_PARAMETERS_START)));
		assert(paramComponent.hasType((byte) VM_TYPE_INT));

		paramComponent = functionComponent.getComponent(paramIndex++);
		assert(paramComponent.hasName("_fixedParam"));
		assert(paramComponent.hasType(TYPE_FIXED));
		assert(paramComponent.hasName((byte) (1 + VM_PARAMETERS_START)));
		assert(paramComponent.hasType((byte) VM_TYPE_FIXED));

		paramComponent = functionComponent.getComponent(paramIndex++);
		assert(paramComponent.hasName("_boolParam"));
		assert(paramComponent.hasType(TYPE_BOOL));
		assert(paramComponent.hasName((byte) (2 + VM_PARAMETERS_START)));
		assert(paramComponent.hasType((byte) VM_TYPE_BOOL));

		paramComponent = functionComponent.getComponent(paramIndex++);
		assert(paramComponent.hasName("_charParam"));
		assert(paramComponent.hasType(TYPE_CHAR));
		assert(paramComponent.hasName((byte) (3 + VM_PARAMETERS_START)));
		assert(paramComponent.hasType((byte) VM_TYPE_CHAR));

		paramComponent = functionComponent.getComponent(paramIndex++);
		assert(paramComponent.hasName("_intArrayParam"));
		assert(paramComponent.hasType(TYPE_ARRAY_INT));
		assert(paramComponent.hasName((byte) (4 + VM_PARAMETERS_START)));
		assert(paramComponent.hasType((byte) VM_TYPE_ARRAY_INT));
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"function", "endFunction"};
			compiler.parseFunctions(code, codeLines);
			fail("Invalid function declaration exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid function declaration");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"function 0est", "endFunction"};
			compiler.parseFunctions(code, codeLines);
			fail("Function names should start with a alphabetic character exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Function names should start with a alphabetic character, Function: 0est");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"function test", "endFunction"};
			compiler.parseFunctions(code, codeLines);
			fail("Function names should start with a uppercase character exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Function names should start with a uppercase character, Function: test");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"function My_Test", "endFunction"};
			compiler.parseFunctions(code, codeLines);
			fail("Function names should contain only alphanumeric characters exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Function names should contain only alphanumeric characters, Function: My_Test");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"function Test"};
			compiler.parseFunctions(code, codeLines);
			fail("Endless function exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Endless function, Function: Test");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"function Test", "function Test1", "endFunction"};
			compiler.parseFunctions(code, codeLines);
			fail("Endless function exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Endless function, Function: Test");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"function Test", "endFunction", "function Test1"};
			compiler.parseFunctions(code, codeLines);
			fail("Endless function exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Endless function, Function: Test1");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"endFunction"};
			compiler.parseFunctions(code, codeLines);
			fail("endFunction should have a corresponding function exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "endFunction should have a corresponding function");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"function Test", "endFunction", "endFunction"};
			compiler.parseFunctions(code, codeLines);
			fail("endFunction should have a corresponding function exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "endFunction should have a corresponding function");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {
					"function Hi", "endFunction",
					"function Hi", "endFunction"
					};
			compiler.parseFunctions(code, codeLines);
			fail("Duplicate function exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Duplicate function, Function: Hi");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"function Test a _param", "endFunction"};
			compiler.parseFunctions(code, codeLines);
			fail("Invalid parameter type declaration exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid parameter type declaration, Function: Test");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"function Test int param", "endFunction"};
			compiler.parseFunctions(code, codeLines);
			fail("Parameter names should start with a _ exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Parameter names should start with a _, Param: param, Function: Test");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"function Test int _0param", "endFunction"};
			compiler.parseFunctions(code, codeLines);
			fail("Parameter names should start with a alphabetic character exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Parameter names should start with a alphabetic character, Param: _0param, Function: Test");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"function Test int _Param", "endFunction"};
			compiler.parseFunctions(code, codeLines);
			fail("Parameter names should start with a lowercase character exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Parameter names should start with a lowercase character, Param: _Param, Function: Test");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"function Test int _my_param", "endFunction"};
			compiler.parseFunctions(code, codeLines);
			fail("Parameter names should contain only alphanumeric characters exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Parameter names should contain only alphanumeric characters, Param: _my_param, Function: Test");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"function Test int", "endFunction"};
			compiler.parseFunctions(code, codeLines);
			fail("Invalid parameter declaration exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Invalid parameter declaration, Function: Test");
		}
		
		try {
			code.getComponents().clear();
			codeLines = new String[] {"function Test int _param fixed _param", "endFunction"};
			compiler.parseFunctions(code, codeLines);
			fail("Duplicate parameter exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Duplicate parameter, Param: _param, Function: Test");
		}
	}
	
	@Test
	public void parseInstructionsTest() throws Exception {
		final JooCompiler compiler = new JooCompiler();
		String[] codeLines = new String[] {
				"int var = 0",
				
				"function TestPlain",
					"call TestParam var",
				"endFunction",
				
				"function TestParam int _param",
					"call TestParam _param",
					"repeatFunction",
				"endFunction",
				};
		
		Code code = new Code();		
		compiler.parseVariables(code, codeLines);
		compiler.parseFunctions(code, codeLines);
		compiler.parseInstructions(code, codeLines);
		
		String function = "TestPlain";
		assert(code.hasComponent(function, KEYWORD_FUNCTION));
		assert(code.getComponentWithName(function).hasName((byte) 1));
		assert(code.getComponentWithName(function).hasType((byte) VM_TYPE_FUNCTION));
		
		String function = "TestPlain";
		assert(code.hasComponent(function, KEYWORD_FUNCTION));
		assert(code.getComponentWithName(function).hasName((byte) 1));
		assert(code.getComponentWithName(function).hasType((byte) VM_TYPE_FUNCTION));
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
		
		testCode = new String[] {"function", "Test", "int", "_param0", "fixed", "_param1"};
		code = new Code();
		createTypeRegistry(code);
		compiler.parseFunctionComponent(code, testCode, 0);
		assert(code.hasComponentWithName("Test"));
		assert(code.getComponentWithName("Test").hasName((byte) 1));
		assert(code.getComponentWithName("Test").hasType(KEYWORD_FUNCTION));
		assert(code.getComponentWithName("Test").hasComponentWithName("_param0"));
		assert(code.getComponentWithName("Test").getComponentWithName("_param0").hasName((byte) (0 + VM_PARAMETERS_START)));
		assert(code.getComponentWithName("Test").getComponentWithName("_param0").hasType(TYPE_INT));
		assert(code.getComponentWithName("Test").hasComponentWithName("_param1"));
		assert(code.getComponentWithName("Test").getComponentWithName("_param1").hasName((byte) (1 + VM_PARAMETERS_START)));
		assert(code.getComponentWithName("Test").getComponentWithName("_param1").hasType(TYPE_FIXED));
		
		testCode = new String[] {"call", "Test"};
		code = new Code();
		createTypeRegistry(code);
		compiler.parseFunctionComponent(code, testCode, 0);
		assert(code.hasComponentWithName("Test"));
		assert(code.getComponentWithName("Test").hasType(KEYWORD_FUNCTION_CALL));
		
		/*try {
			compiler.parseFunctionComponent(code, testCode, 0);
			fail("Invalid function exception not thrown");
		} catch (Exception e) {
			assertEquals(e.getMessage(), "Invalid function, Name: Test");
			assert(code.hasComponentWithName("Test"));
			assert(code.getComponentWithName("Test").hasType(KEYWORD_FUNCTION_CALL));
		}*/
		
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
		
		/*try {
			compiler.parseFunctionComponent(code, testCode, 0);
			fail("Invalid function exception not thrown");
		} catch (Exception e) {
			assertEquals(e.getMessage(), "Invalid function, Name: Test");
			assert(code.hasComponentWithName("Test"));
			assert(code.getComponentWithName("Test").hasType(KEYWORD_FUNCTION_CALL));
			assert(code.getComponentWithName("Test").hasComponentWithName("variable0"));
			assert(code.getComponentWithName("Test").getComponentWithName("variable0").hasName((byte) 1));
			assert(code.getComponentWithName("Test").getComponentWithName("variable0").hasType(TYPE_INT));
			assert(code.getComponentWithName("Test").hasComponentWithName("variable1"));
			assert(code.getComponentWithName("Test").getComponentWithName("variable1").hasName((byte) 2));
			assert(code.getComponentWithName("Test").getComponentWithName("variable1").hasType(TYPE_FIXED));
		}*/
		
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
			testCode = new String[] {"function", "0Test"};
			compiler.parseFunctionComponent(code, testCode, 0);
			fail("Function names should start with a alphabetic character exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Function names should start with a alphabetic character, Name: 0Test");
		}
		
		try {
			testCode = new String[] {"function", "test"};
			compiler.parseFunctionComponent(code, testCode, 0);
			fail("Function names should start with a uppercase character exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Function names should start with a uppercase character, Name: test");
		}
		
		try {
			testCode = new String[] {"function", "My_Test"};
			compiler.parseFunctionComponent(code, testCode, 0);
			fail("Function names should contain only alphanumeric characters exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Function names should contain only alphanumeric characters, Name: My_Test");
		}
		
		try {
			testCode = new String[] {"function", "ABC"};
			compiler.parseFunctionComponent(code, testCode, 0);
			testCode = new String[] {"function", "ABC"};
			compiler.parseFunctionComponent(code, testCode, 0);
			fail("Duplicate function exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Duplicate function, Name: ABC");
		}
		
		try {
			testCode = new String[] {"function", "Test", "int", "parameter"};
			compiler.parseFunctionComponent(code, testCode, 0);
			fail("Parameter names should start with a _ exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Parameter names should start with a _, Name: parameter");
		}
		
		try {
			testCode = new String[] {"function", "Test0", "int", "_0parameter"};
			compiler.parseFunctionComponent(code, testCode, 0);
			fail("Parameter names should start with a alphabetic character exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Parameter names should start with a alphabetic character, Name: _0parameter");
		}
		
		try {
			testCode = new String[] {"function", "Test1", "int", "_Parameter"};
			compiler.parseFunctionComponent(code, testCode, 0);
			fail("Parameter names should start with a lowercase character exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Parameter names should start with a lowercase character, Name: _Parameter");
		}
		
		try {
			testCode = new String[] {"function", "Test2", "int", "_my_parameter"};
			compiler.parseFunctionComponent(code, testCode, 0);
			fail("Parameter names should contain only alphanumeric characters exception not thrown");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Parameter names should contain only alphanumeric characters, Name: _my_parameter");
		}
		
		try {
			testCode = new String[] {"function", "Test3", "int", "_parameter", "int", "_parameter"};
			compiler.parseFunctionComponent(code, testCode, 0);
			fail("Duplicate parameter");
		} catch (ParseException e) {
			assertEquals(e.getMessage(), "Duplicate parameter, Name: _parameter");
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
		final String settingsData = FileUtil.read("StandartLibrary.joo");
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
		final String settingsData = FileUtil.read("StandartLibrary.joo");
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
		assert(code.getComponentWithName("=").hasType(TYPE_OPERATOR));
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
		final String settingsData = FileUtil.read("StandartLibrary.joo");
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
		
		System.out.println(compiledByteCode.length());
		
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
		final char libraryFunction = name++;
		final char directoryLibraryFunction = name++;
		
		char parameter = VM_PARAMETERS_START;
		final char param0 = parameter++;
		final char param1 = parameter++;
		
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
		assertEquals(lines[line++], "" + VM_TYPE_FUNCTION + (char)4);
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
		assertEquals(lines[line++], "" + VM_KEYWORD_ARGUMENT + intArray);
		assertEquals(lines[line++], "" + libraryFunction);
		assertEquals(lines[line++], "" + VM_KEYWORD_ARGUMENT + intArray);
		assertEquals(lines[line++], "" + directoryLibraryFunction);		
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
		assertEquals(lines[line++], "" + VM_KEYWORD_FUNCTION);
		assertEquals(lines[line++], "" + param0 + compiler.toByteCodeNumber("0") + OPERATOR_ADD + compiler.toByteCodeNumber("10"));
		assertEquals(lines[line++], "" + VM_KEYWORD_FUNCTION);
		assertEquals(lines[line++], "" + param0 + compiler.toByteCodeNumber("0") + OPERATOR_ADD + compiler.toByteCodeNumber("20"));
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
