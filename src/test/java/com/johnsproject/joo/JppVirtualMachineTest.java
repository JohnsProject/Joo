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
		
		assertEquals(jppVM.getCodeSize(), compiledJooCode.length());
		assertEquals(intIndex, 0);
		assertEquals(fixedIndex, 3);
		assertEquals(boolIndex, 5);
		assertEquals(charIndex, 8);
		assertEquals(intArrayIndex, 11);
		assertEquals(fixedArrayIndex, 12);
		assertEquals(boolArrayIndex, 13);
		assertEquals(charArrayIndex, 14);
		assertEquals(functionIndex, 15);
		
		assertEquals(jppVM.getComponents()[intIndex + 0], 0);
		assertEquals(jppVM.getComponents()[intIndex + 1], 10);
		assertEquals(jppVM.getComponents()[fixedIndex + 0], 0);
		assertEquals(jppVM.getComponents()[fixedIndex + 1], 25628);
		assertEquals(jppVM.getComponents()[boolIndex + 0], 0);
		assertEquals(jppVM.getComponents()[boolIndex + 1], 1);
		assertEquals(jppVM.getComponents()[boolIndex + 2], 0);
		assertEquals(jppVM.getComponents()[charIndex + 0], 0);
		assertEquals(jppVM.getComponents()[charIndex + 1], 65);
		assertEquals(jppVM.getComponents()[charIndex + 2], 67);
		assertEquals(jppVM.getComponents()[intArrayIndex + 0], 0);
		assertEquals(jppVM.getComponents()[fixedArrayIndex + 0], 10);
		assertEquals(jppVM.getComponents()[boolArrayIndex + 0], 15);
		assertEquals(jppVM.getComponents()[charArrayIndex + 0], 30);
		assertEquals(jppVM.getComponents()[functionIndex + 0], 75);
		assertEquals(jppVM.getComponents()[functionIndex + 1], 443);
	}
	
	@Test
	public void startTest() throws Exception {
		final String jooCode = FileUtil.readResource("TestCode.joo");
		final JppCompiler jppCompiler = new JppCompiler();
		final String compiledJooCode = jppCompiler.compile(jooCode);
		final JppVirtualMachine jppVM = new JppVirtualMachine();
		jppVM.initialize(compiledJooCode.toCharArray());
		jppVM.start();
		
		int intIndex = jppVM.getComponentIndexes()[TYPE_INT - TYPES_START];
		int fixedIndex = jppVM.getComponentIndexes()[TYPE_FIXED - TYPES_START];
		int boolIndex = jppVM.getComponentIndexes()[TYPE_BOOL - TYPES_START];
		int charIndex = jppVM.getComponentIndexes()[TYPE_CHAR - TYPES_START];
		int intArrayIndex = jppVM.getComponentIndexes()[TYPE_ARRAY_INT - TYPES_START];
		int fixedArrayIndex = jppVM.getComponentIndexes()[TYPE_ARRAY_FIXED - TYPES_START];
		int boolArrayIndex = jppVM.getComponentIndexes()[TYPE_ARRAY_BOOL - TYPES_START];
		int charArrayIndex = jppVM.getComponentIndexes()[TYPE_ARRAY_CHAR - TYPES_START];
		int functionIndex = jppVM.getComponentIndexes()[TYPE_FUNCTION - TYPES_START];
		
		assertEquals(jppVM.getComponents()[intIndex + 0], 20);
		assertEquals(jppVM.getComponents()[intIndex + 1], 20);
		assertEquals(jppVM.getComponents()[intIndex + 2], 12);
		assertEquals(jppVM.getComponents()[fixedIndex + 0], 12759);
		assertEquals(jppVM.getComponents()[fixedIndex + 1], 12750);
		assertEquals(jppVM.getComponents()[boolIndex + 0], 1);
		assertEquals(jppVM.getComponents()[boolIndex + 1], 0);
		assertEquals(jppVM.getComponents()[boolIndex + 2], 0);
		assertEquals(jppVM.getComponents()[charIndex + 0], 65);
		assertEquals(jppVM.getComponents()[charIndex + 1], 66);
		assertEquals(jppVM.getComponents()[charIndex + 2], 66);
		assertEquals(jppVM.getComponents()[intArrayIndex + 0], 0);
		assertEquals(jppVM.getComponents()[fixedArrayIndex + 0], 10);
		assertEquals(jppVM.getComponents()[boolArrayIndex + 0], 15);
		assertEquals(jppVM.getComponents()[charArrayIndex + 0], 30);
		assertEquals(jppVM.getComponents()[functionIndex + 0], 75);
		assertEquals(jppVM.getComponents()[functionIndex + 1], 443);
		assertEquals(jppVM.getArrays()[jppVM.getComponents()[intArrayIndex + 0] + 0], 30);
		assertEquals(jppVM.getArrays()[jppVM.getComponents()[intArrayIndex + 0] + 1], 3);
		assertEquals(jppVM.getArrays()[jppVM.getComponents()[intArrayIndex + 0] + 2], 100);
		assertEquals(jppVM.getArrays()[jppVM.getComponents()[intArrayIndex + 0] + 3], 100);
		assertEquals(jppVM.getArrays()[jppVM.getComponents()[intArrayIndex + 0] + 4], 118);
		assertEquals(jppVM.getArrays()[jppVM.getComponents()[intArrayIndex + 0] + 5], 118);
		assertEquals(jppVM.getArrays()[jppVM.getComponents()[fixedArrayIndex + 0] + 0], 8034);
		assertEquals(jppVM.getArrays()[jppVM.getComponents()[fixedArrayIndex + 0] + 1], 768);
		assertEquals(jppVM.getArrays()[jppVM.getComponents()[boolArrayIndex + 0] + 9], 1);
		assertEquals(jppVM.getArrays()[jppVM.getComponents()[boolArrayIndex + 0] + 10], 1);
		assertEquals(jppVM.getArrays()[jppVM.getComponents()[boolArrayIndex + 0] + 11], 1);
		assertEquals(jppVM.getArrays()[jppVM.getComponents()[charArrayIndex + 0] + 9], 65);
		assertEquals(jppVM.getArrays()[jppVM.getComponents()[charArrayIndex + 0] + 10], 67);
		assertEquals(jppVM.getArrays()[jppVM.getComponents()[charArrayIndex + 0] + 11], 67);
	}
}
