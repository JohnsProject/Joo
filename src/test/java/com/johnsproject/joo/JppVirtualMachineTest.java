package com.johnsproject.joo;

import static com.johnsproject.joo.JppVirtualMachine.*;
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
		
		int intIndex = jppVM.getComponentIndexes()[TYPE_INT - TYPES_START];
		int fixedIndex = jppVM.getComponentIndexes()[TYPE_FIXED - TYPES_START];
		int boolIndex = jppVM.getComponentIndexes()[TYPE_BOOL - TYPES_START];
		int charIndex = jppVM.getComponentIndexes()[TYPE_CHAR - TYPES_START];
		int intArrayIndex = jppVM.getComponentIndexes()[TYPE_ARRAY_INT - TYPES_START];
		int fixedArrayIndex = jppVM.getComponentIndexes()[TYPE_ARRAY_FIXED - TYPES_START];
		int boolArrayIndex = jppVM.getComponentIndexes()[TYPE_ARRAY_BOOL - TYPES_START];
		int charArrayIndex = jppVM.getComponentIndexes()[TYPE_ARRAY_CHAR - TYPES_START];
		int functionIndex = jppVM.getComponentIndexes()[TYPE_FUNCTION - TYPES_START];
		
		assertEquals(jppVM.getJooCodeSize(), compiledJooCode.length());
		assertEquals(intIndex, 0);
		assertEquals(fixedIndex, 2);
		assertEquals(boolIndex, 4);
		assertEquals(charIndex, 6);
		assertEquals(intArrayIndex, 8);
		assertEquals(fixedArrayIndex, 9);
		assertEquals(boolArrayIndex, 10);
		assertEquals(charArrayIndex, 11);
		assertEquals(functionIndex, 12);
		
		assertEquals(jppVM.getJooComponents()[intIndex + 0], 0);
		assertEquals(jppVM.getJooComponents()[intIndex + 1], 10);
		assertEquals(jppVM.getJooComponents()[fixedIndex + 0], 0);
		assertEquals(jppVM.getJooComponents()[fixedIndex + 1], 2588);
		assertEquals(jppVM.getJooComponents()[boolIndex + 0], 0);
		assertEquals(jppVM.getJooComponents()[boolIndex + 1], 1);
		assertEquals(jppVM.getJooComponents()[charIndex + 0], 0);
		assertEquals(jppVM.getJooComponents()[charIndex + 1], 65);
		assertEquals(jppVM.getJooComponents()[intArrayIndex + 0], 10);
		assertEquals(jppVM.getJooComponents()[fixedArrayIndex + 0], 5);
		assertEquals(jppVM.getJooComponents()[boolArrayIndex + 0], 15);
		assertEquals(jppVM.getJooComponents()[charArrayIndex + 0], 13);
		assertEquals(jppVM.getJooComponents()[functionIndex + 0], 66);
		assertEquals(jppVM.getJooComponents()[functionIndex + 1], 168);
	}
}
