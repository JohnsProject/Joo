package com.johnsproject.joo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.johnsproject.joo.util.FileUtil;

public class JppVirtualMachineTest {
	
	@Test
	public void initializeTest() throws Exception {
		final String jooCode = FileUtil.readResource("TestCode.joo");
		final JppCompiler jppCompiler = new JppCompiler();
		final String compiledJooCode = jppCompiler.compile(jooCode);
		final JppVirtualMachine jppVM = new JppVirtualMachine();
		jppVM.initialize(compiledJooCode.toCharArray());
		
		assertEquals(jppVM.getJooCodeSize(), compiledJooCode.length());
		assertEquals(jppVM.getComponentCounts()[8], 2);
	}

}
