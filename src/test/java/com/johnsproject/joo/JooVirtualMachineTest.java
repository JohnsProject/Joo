package com.johnsproject.joo;

import static com.johnsproject.joo.JooVirtualMachine.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.johnsproject.joo.util.FileUtil;

public class JooVirtualMachineTest {
	
	@Test
	public void initializeTest() throws Exception {
		final String jooCode = FileUtil.readResource("TestCode.joo");
		final JooCompiler jooCompiler = new JooCompiler();
		final String compiledJooCode = jooCompiler.compile(jooCode);
		final JooVirtualMachine jooVM = new JooVirtualMachine();
		jooVM.initialize(compiledJooCode.toCharArray());
		
		int intIndex = jooVM.getComponentIndexes()[TYPE_INT - TYPES_START];
		int fixedIndex = jooVM.getComponentIndexes()[TYPE_FIXED - TYPES_START];
		int boolIndex = jooVM.getComponentIndexes()[TYPE_BOOL - TYPES_START];
		int charIndex = jooVM.getComponentIndexes()[TYPE_CHAR - TYPES_START];
		int intArrayIndex = jooVM.getComponentIndexes()[TYPE_ARRAY_INT - TYPES_START];
		int fixedArrayIndex = jooVM.getComponentIndexes()[TYPE_ARRAY_FIXED - TYPES_START];
		int boolArrayIndex = jooVM.getComponentIndexes()[TYPE_ARRAY_BOOL - TYPES_START];
		int charArrayIndex = jooVM.getComponentIndexes()[TYPE_ARRAY_CHAR - TYPES_START];
		int functionIndex = jooVM.getComponentIndexes()[TYPE_FUNCTION - TYPES_START];
		
		assertEquals(jooVM.getCodeSize(), compiledJooCode.length());
		assertEquals(intIndex, 0);
		assertEquals(fixedIndex, 3);
		assertEquals(boolIndex, 5);
		assertEquals(charIndex, 8);
		assertEquals(intArrayIndex, 11);
		assertEquals(fixedArrayIndex, 12);
		assertEquals(boolArrayIndex, 13);
		assertEquals(charArrayIndex, 14);
		assertEquals(functionIndex, 15);
		
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
		assertEquals(jooVM.getComponents()[functionIndex + 0], 75);
		assertEquals(jooVM.getComponents()[functionIndex + 1], 442);
	}
	
	@Test
	public void startTest() throws Exception {
		final String jooCode = FileUtil.readResource("TestCode.joo");
		final JooCompiler jooCompiler = new JooCompiler();
		final String compiledJooCode = jooCompiler.compile(jooCode);
		final JooVirtualMachine jooVM = new JooVirtualMachine();
		jooVM.initialize(compiledJooCode.toCharArray());
		jooVM.start();
		
		int intIndex = jooVM.getComponentIndexes()[TYPE_INT - TYPES_START];
		int fixedIndex = jooVM.getComponentIndexes()[TYPE_FIXED - TYPES_START];
		int boolIndex = jooVM.getComponentIndexes()[TYPE_BOOL - TYPES_START];
		int charIndex = jooVM.getComponentIndexes()[TYPE_CHAR - TYPES_START];
		int intArrayIndex = jooVM.getComponentIndexes()[TYPE_ARRAY_INT - TYPES_START];
		int fixedArrayIndex = jooVM.getComponentIndexes()[TYPE_ARRAY_FIXED - TYPES_START];
		int boolArrayIndex = jooVM.getComponentIndexes()[TYPE_ARRAY_BOOL - TYPES_START];
		int charArrayIndex = jooVM.getComponentIndexes()[TYPE_ARRAY_CHAR - TYPES_START];
		int functionIndex = jooVM.getComponentIndexes()[TYPE_FUNCTION - TYPES_START];
		
		assertEquals(jooVM.getComponents()[intIndex + 0], 230);
		assertEquals(jooVM.getComponents()[intIndex + 1], 6);
		assertEquals(jooVM.getComponents()[intIndex + 2], 13);
		assertEquals(jooVM.getComponents()[fixedIndex + 0], 12759);
		assertEquals(jooVM.getComponents()[fixedIndex + 1], 12750);
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
		assertEquals(jooVM.getComponents()[functionIndex + 0], 75);
		assertEquals(jooVM.getComponents()[functionIndex + 1], 442);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[intArrayIndex + 0] + 0], 72);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[intArrayIndex + 0] + 1], 3);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[intArrayIndex + 0] + 2], 100);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[intArrayIndex + 0] + 3], 100);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[intArrayIndex + 0] + 4], 118);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[intArrayIndex + 0] + 5], 118);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[intArrayIndex + 0] + 6], 6);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[fixedArrayIndex + 0] + 0], 8034);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[fixedArrayIndex + 0] + 1], 768);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[boolArrayIndex + 0] + 9], 1);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[boolArrayIndex + 0] + 10], 1);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[boolArrayIndex + 0] + 11], 1);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[charArrayIndex + 0] + 9], 65);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[charArrayIndex + 0] + 10], 67);
		assertEquals(jooVM.getArrays()[jooVM.getComponents()[charArrayIndex + 0] + 11], 67);
	}
}
