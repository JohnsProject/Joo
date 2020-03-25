package com.johnsproject.joo;

import org.junit.Test;

import com.johnsproject.joo.util.FileUtil;

public class JooBytecodeConverterTest {

	@Test
	public void compileTest() throws Exception {
		System.out.println(new JooBytecodeConverter().convert(FileUtil.readResource("BytecodeConverter/ConverterCode.joo")));
		System.out.println(new JooCompiler().compile(FileUtil.readResource("BytecodeConverter/CompilerCode.joo")));
	}
	
}
