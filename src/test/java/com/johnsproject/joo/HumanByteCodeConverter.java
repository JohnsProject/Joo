package com.johnsproject.joo;

class HumanByteCodeConverter {

	private static class Keyword {
		
		private String keyword;
		private char byteCodeKeyword;
		
		public Keyword(String keyword, char byteCodeKeyword) {
			this.keyword = keyword;
			this.byteCodeKeyword = byteCodeKeyword;
		}

		public String getKeyword() {
			return keyword;
		}

		public char getByteCodeKeyword() {
			return byteCodeKeyword;
		}		
	}
	
	private Keyword[] keywords = new Keyword[] {
			new Keyword(JooCompiler.TYPE_INT, JooVirtualMachine.TYPE_INT),
			new Keyword(JooCompiler.TYPE_FIXED, JooVirtualMachine.TYPE_FIXED),
			new Keyword(JooCompiler.TYPE_BOOL, JooVirtualMachine.TYPE_BOOL),
			new Keyword(JooCompiler.TYPE_CHAR, JooVirtualMachine.TYPE_CHAR),
			new Keyword(JooCompiler.TYPE_ARRAY_INT, JooVirtualMachine.TYPE_ARRAY_INT),
			new Keyword(JooCompiler.TYPE_ARRAY_FIXED, JooVirtualMachine.TYPE_ARRAY_FIXED),
			new Keyword(JooCompiler.TYPE_ARRAY_BOOL, JooVirtualMachine.TYPE_ARRAY_BOOL),
			new Keyword(JooCompiler.TYPE_ARRAY_CHAR, JooVirtualMachine.TYPE_ARRAY_CHAR),
			new Keyword(JooCompiler.KEYWORD_FUNCTION, JooVirtualMachine.TYPE_FUNCTION),
	};
	
	/**
	 * Converts human readable byte code into virtual machine byte code.
	 * The human readable byte code allows easier testing of the compiled joo code.
	 * 
	 * @param compiler
	 * @param humanByteCode
	 * @return
	 */
	String convert(JooCompiler compiler, String humanByteCode) {
		final String[] byteCodeData = humanByteCode.replace("\r", "").split("[ \n]");
		String byteCode = "";
		for (String keyword : byteCodeData) {
			String convertedKeyword = "";
			if(convertedKeyword.isEmpty())
				convertedKeyword = convertKeyword(keyword);
			if(convertedKeyword.isEmpty())
				convertedKeyword = convertCharacter(keyword);
			if(convertedKeyword.isEmpty())
				convertedKeyword = convertNumber(compiler, keyword);
			if(convertedKeyword.isEmpty())
				convertedKeyword = convertName(compiler, keyword);
			if(!convertedKeyword.isEmpty())
				byteCode += convertedKeyword;
		}
		return byteCode;
	}
	
	private String convertKeyword(String keyword) {
		for (Keyword possibleKeyword : keywords) {
			if(keyword.equals(possibleKeyword.getKeyword())) {
				return "" + possibleKeyword.getByteCodeKeyword();
			}
		}
		return "";
	}
	
	private String convertCharacter(String character) {
		if(character.matches("[0-9]+c")) {
			character = character.replace("c", "");
			return "" + (char)Integer.parseInt(character);
		}
		return "";
	}
	
	private String convertNumber(JooCompiler compiler, String number) {
		if(number.matches("[0-9]+n")) {
			number = number.replace("n", "");
			return compiler.toByteCodeNumber(number);
		}
		return "";
	}
	
	private String convertName(JooCompiler compiler, String name) {
		final Code code = compiler.getCode();
		final CodeComponent typeRegistry = code.getComponentWithType(JooCompiler.KEYWORD_TYPE_REGISTRY);
		for (CodeComponent registry : typeRegistry.getComponents()) {
			if(registry.hasComponentWithName(name)) {
				final CodeComponent component = registry.getComponentWithName(name);
				return "" + (char) component.getByteCodeName();
			}
		}
		return "";
	}
}
