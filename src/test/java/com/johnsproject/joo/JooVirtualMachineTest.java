package com.johnsproject.joo;

import static com.johnsproject.joo.JooVirtualMachine.*;
import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class JooVirtualMachineTest {
	
	@Test
	public void initializeTest() throws Exception {
		final String compiledJooCode = JooCompiler.compile("TestCode.joo");
		final JooVirtualMachine jooVM = new JooVirtualMachine();
		jooVM.setCode(compiledJooCode);
		jooVM.initialize();
		
		int intIndex = jooVM.getComponentIndexes()[TYPE_INT - TYPES_START];
		int fixedIndex = jooVM.getComponentIndexes()[TYPE_FIXED - TYPES_START];
		int boolIndex = jooVM.getComponentIndexes()[TYPE_BOOL - TYPES_START];
		int charIndex = jooVM.getComponentIndexes()[TYPE_CHAR - TYPES_START];
		int intArrayIndex = jooVM.getComponentIndexes()[TYPE_ARRAY_INT - TYPES_START];
		int fixedArrayIndex = jooVM.getComponentIndexes()[TYPE_ARRAY_FIXED - TYPES_START];
		int boolArrayIndex = jooVM.getComponentIndexes()[TYPE_ARRAY_BOOL - TYPES_START];
		int charArrayIndex = jooVM.getComponentIndexes()[TYPE_ARRAY_CHAR - TYPES_START];
		int functionIndex = jooVM.getComponentIndexes()[TYPE_FUNCTION - TYPES_START];
		
		assertEquals(jooVM.getCodeSize(), 633);
		assertEquals(intIndex, 0);
		assertEquals(fixedIndex, 5);
		assertEquals(boolIndex, 7);
		assertEquals(charIndex, 10);
		assertEquals(intArrayIndex, 13);
		assertEquals(fixedArrayIndex, 14);
		assertEquals(boolArrayIndex, 15);
		assertEquals(charArrayIndex, 16);
		assertEquals(functionIndex, 17);
		
		assertEquals(jooVM.getComponents()[intIndex + 0], 0);
		assertEquals(jooVM.getComponents()[intIndex + 1], 10);
		assertEquals(jooVM.getComponents()[fixedIndex + 0], 0);
		assertEquals(jooVM.getComponents()[fixedIndex + 1], 25628);
		assertEquals(jooVM.getComponents()[boolIndex + 0], 0);
		assertEquals(jooVM.getComponents()[boolIndex + 1], 1);
		assertEquals(jooVM.getComponents()[boolIndex + 2], 0);
		assertEquals(jooVM.getComponents()[charIndex + 0], 0);
		assertEquals(jooVM.getComponents()[charIndex + 1], 65);
		assertEquals(jooVM.getComponents()[charIndex + 2], 67);
		assertEquals(jooVM.getComponents()[intArrayIndex + 0], 0);
		assertEquals(jooVM.getComponents()[fixedArrayIndex + 0], 10);
		assertEquals(jooVM.getComponents()[boolArrayIndex + 0], 15);
		assertEquals(jooVM.getComponents()[charArrayIndex + 0], 30);
		assertEquals(jooVM.getComponents()[functionIndex + 0], 81);
		assertEquals(jooVM.getComponents()[functionIndex + 1], 546);
	}
	
	@Test
	public void SingleAppTest() throws Exception {
		final String compiledJooCode = JooCompiler.compile("SingleApp" + File.separator + "TestCode.joo");
		final JooVirtualMachine jooVM = new JooVirtualMachine();
		jooVM.setCode(compiledJooCode);
		jooVM.start();
		
		int intIndex = jooVM.getComponentIndexes()[TYPE_INT - TYPES_START];
		int fixedIndex = jooVM.getComponentIndexes()[TYPE_FIXED - TYPES_START];
		int boolIndex = jooVM.getComponentIndexes()[TYPE_BOOL - TYPES_START];
		int charIndex = jooVM.getComponentIndexes()[TYPE_CHAR - TYPES_START];
		int intArrayIndex = jooVM.getComponentIndexes()[TYPE_ARRAY_INT - TYPES_START];
		int fixedArrayIndex = jooVM.getComponentIndexes()[TYPE_ARRAY_FIXED - TYPES_START];
		int boolArrayIndex = jooVM.getComponentIndexes()[TYPE_ARRAY_BOOL - TYPES_START];
		int charArrayIndex = jooVM.getComponentIndexes()[TYPE_ARRAY_CHAR - TYPES_START];
		
		assertEquals(jooVM.getComponents()[intIndex + 0], 240);
		assertEquals(jooVM.getComponents()[intIndex + 1], 6);
		assertEquals(jooVM.getComponents()[intIndex + 2], 14);
		assertEquals(jooVM.getComponents()[fixedIndex + 0], 12759);
		assertEquals(jooVM.getComponents()[fixedIndex + 1], 25500);
		assertEquals(jooVM.getComponents()[boolIndex + 0], 1);
		assertEquals(jooVM.getComponents()[boolIndex + 1], 0);
		assertEquals(jooVM.getComponents()[boolIndex + 2], 0);
		assertEquals(jooVM.getComponents()[charIndex + 0], 65);
		assertEquals(jooVM.getComponents()[charIndex + 1], 66);
		assertEquals(jooVM.getComponents()[charIndex + 2], 66);
		assertEquals(jooVM.getComponents()[intArrayIndex + 0], 0);
		assertEquals(jooVM.getComponents()[fixedArrayIndex + 0], 10);
		assertEquals(jooVM.getComponents()[boolArrayIndex + 0], 15);
		assertEquals(jooVM.getComponents()[charArrayIndex + 0], 30);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[intArrayIndex + 0] + 0], 92);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[intArrayIndex + 0] + 1], 3);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[intArrayIndex + 0] + 2], 100);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[intArrayIndex + 0] + 3], 100);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[intArrayIndex + 0] + 4], 118);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[intArrayIndex + 0] + 5], 118);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[intArrayIndex + 0] + 6], 6);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[intArrayIndex + 0] + 7], 25);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[fixedArrayIndex + 0] + 0], 8034);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[fixedArrayIndex + 0] + 1], 768);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[fixedArrayIndex + 0] + 2], -2550);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[fixedArrayIndex + 0] + 3], 2550);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[fixedArrayIndex + 0] + 4], -2550);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[fixedArrayIndex + 0] + 5], 6396);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[boolArrayIndex + 0] + 9], 1);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[boolArrayIndex + 0] + 10], 1);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[boolArrayIndex + 0] + 11], 1);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[boolArrayIndex + 0] + 2], 0);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[boolArrayIndex + 0] + 3], 1);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[boolArrayIndex + 0] + 4], 0);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[boolArrayIndex + 0] + 5], 1);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[charArrayIndex + 0] + 9], 'A');
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[charArrayIndex + 0] + 10], 'C');
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[charArrayIndex + 0] + 11], 'C');
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[charArrayIndex + 0] + 0], 'd');
	}
	
	@Test
	public void MultiAppTest() throws Exception {
		final String compiledJooCode = JooCompiler.compile("TestCode.joo");
		final JooVirtualMachine jooVM = new JooVirtualMachine();
		jooVM.setCode(compiledJooCode);
		jooVM.start();
		
		int intIndex = jooVM.getComponentIndexes()[TYPE_INT - TYPES_START];
		
		assertEquals(jooVM.getComponents()[intIndex + 0], 150);
	}
}
