package com.johnsproject.joo;

/**
 * The JooBytecodeConverter bytecode converter class is used to convert human readable
 * bytecode into vm bytecode. The JooBytecodeConverter reads bytecode written using
 * compiler keywords and replaces them into vm keywords. This converter is thought to
 * be used for test purposes only.
 * 
 * @author John Ferraz Salomon
 *
 */
public class JooBytecodeConverter {
	
	public char[] convert(String code) {
		code = replaceKeywords(code);
		code = replaceTypes(code);
		code = replaceComparators(code);
		code = replaceOperators(code);
		code = replaceNames(code);
		code = code.replace(JooCompiler.LINE_BREAK, "" + JooVirtualMachine.LINE_BREAK);
		code = code.replace("" + JooVirtualMachine.LINE_BREAK + JooVirtualMachine.LINE_BREAK, "" + JooVirtualMachine.LINE_BREAK);
		code = code.replace(" ", "");
		code = code.replace(JooCompiler.KEYWORD_TRUE, "1");
		code = code.replace(JooCompiler.KEYWORD_FALSE, "0");
		return code.toCharArray();
	}
	
	/**
	 * variables and function names range from 'A' to 'Z' to make converter code readable. 
	 * This function replaces the converter code names to vm code names.
	 * 
	 * @param code
	 * @return
	 */
	String replaceNames(String code) {
		for (int i = 65, j = 1; i < 90; i++, j++) {
			code = code.replace((char) i, (char) j);
		}
		return code;
	}
	
	String replaceKeywords(String code) {
		code = code.replace(JooCompiler.KEYWORD_IF, "" + JooVirtualMachine.KEYWORD_IF);
		code = code.replace(JooCompiler.KEYWORD_ELSE, "" + JooVirtualMachine.KEYWORD_ELSE);
		code = code.replace(JooCompiler.KEYWORD_IF_END, "" + JooVirtualMachine.KEYWORD_IF);
		code = code.replace(JooCompiler.KEYWORD_FUNCTION, "" + JooVirtualMachine.KEYWORD_FUNCTION);
		code = code.replace(JooCompiler.KEYWORD_FUNCTION_END, "" + JooVirtualMachine.KEYWORD_FUNCTION);
		code = code.replace(JooCompiler.KEYWORD_FUNCTION_REPEAT, "" + JooVirtualMachine.KEYWORD_FUNCTION_REPEAT);
		code = code.replace(JooCompiler.KEYWORD_FUNCTION_CALL, "" + JooVirtualMachine.KEYWORD_FUNCTION_CALL);
		code = code.replace(JooCompiler.KEYWORD_PARAMETER, "" + JooVirtualMachine.KEYWORD_PARAMETER);
		return code;
	}
	
	String replaceTypes(String code) {
		code = code.replace(JooCompiler.TYPE_INT, "" + JooVirtualMachine.TYPE_INT);
		code = code.replace(JooCompiler.TYPE_FIXED, "" + JooVirtualMachine.TYPE_FIXED);
		code = code.replace(JooCompiler.TYPE_BOOL, "" + JooVirtualMachine.TYPE_BOOL);
		code = code.replace(JooCompiler.TYPE_CHAR, "" + JooVirtualMachine.TYPE_CHAR);
		code = code.replace(JooCompiler.TYPE_ARRAY_INT, "" + JooVirtualMachine.TYPE_ARRAY_INT);
		code = code.replace(JooCompiler.TYPE_ARRAY_FIXED, "" + JooVirtualMachine.TYPE_ARRAY_FIXED);
		code = code.replace(JooCompiler.TYPE_ARRAY_BOOL, "" + JooVirtualMachine.TYPE_ARRAY_BOOL);
		code = code.replace(JooCompiler.TYPE_ARRAY_CHAR, "" + JooVirtualMachine.TYPE_ARRAY_CHAR);
		code = code.replace("String", "" + JooVirtualMachine.FUNCTION_STRING);
		return code;
	}
	
	String replaceComparators(String code) {
		code = code.replace(JooCompiler.COMPARATOR_SMALLER_EQUALS, "" + JooVirtualMachine.SMALLER + JooVirtualMachine.EQUALS);
		code = code.replace(JooCompiler.COMPARATOR_BIGGER_EQUALS, "" + JooVirtualMachine.BIGGER + JooVirtualMachine.EQUALS);
		code = code.replace(JooCompiler.COMPARATOR_NOT_EQUALS, "" + JooVirtualMachine.NOT_EQUALS);
		code = code.replace(JooCompiler.COMPARATOR_SMALLER, "" + JooVirtualMachine.SMALLER);
		code = code.replace(JooCompiler.COMPARATOR_BIGGER, "" + JooVirtualMachine.BIGGER);
		code = code.replace(JooCompiler.COMPARATOR_EQUALS, "" + JooVirtualMachine.EQUALS);
		return code;
	}
	
	String replaceOperators(String code) {
		code = code.replace(JooCompiler.OPERATOR_ADD, "" + JooVirtualMachine.ADD);
		code = code.replace(JooCompiler.OPERATOR_SUBTRACT, "" + JooVirtualMachine.SUBTRACT);
		code = code.replace(JooCompiler.OPERATOR_MULTIPLY, "" + JooVirtualMachine.MULTIPLY);
		code = code.replace(JooCompiler.OPERATOR_DIVIDE, "" + JooVirtualMachine.DIVIDE);
		code = code.replace(JooCompiler.OPERATOR_SET_EQUALS, "" + JooVirtualMachine.EQUALS);
		return code;
	}
}
