/*
MIT License

Copyright (c) 2019 John´s Project

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package com.johnsproject.joo;

import com.johnsproject.joo.util.FileUtil;

import static com.johnsproject.joo.JooVirtualMachine.*;

public class JooConsoleApp {
	
	public static final String PATH_HELP = "Help.txt";
	public static final String PATH_DOC = "JooDoc.txt";
	public static final String PATH_TEMPLATE = "Template.joo";
	
	public static final String COMMAND_EXECUTE = "joo";
	public static final String COMMAND_COMPILE = "-compile";
	public static final String COMMAND_HELP = "-help";
	public static final String COMMAND_DOCUMENTATION = "-doc";
	public static final String COMMAND_CREATE = "-create";
	
	public static void main(String[] args) {
		if(args.length > 0) {
			if(args[0].equals(COMMAND_EXECUTE)) {
				if(args.length > 1) {
					if(args[1].equals(COMMAND_COMPILE)) {
						compileCode(args[args.length - 1]);
					}
					else if(args[1].equals(COMMAND_HELP)) {
						showHelp();
					}
					else if(args[1].equals(COMMAND_DOCUMENTATION)) {
						showDoc();
					}
					else if(args[1].equals(COMMAND_CREATE)) {
						createTemplate(args[args.length - 1]);
					} else {
						runCode(args[args.length - 1]);
					}
				} else {
					showHelp();
				}
			}
		} else {
			showHelp();
		}
	}
	
	public static void showHelp() {
		String fileContent = FileUtil.read(PATH_HELP);
		System.out.println(fileContent);
	}
	
	public static void showDoc() {
		String fileContent = FileUtil.read(PATH_DOC);
		System.out.println(fileContent);
	}
	
	public static void createTemplate(String path) {
		String fileContent = FileUtil.read(PATH_TEMPLATE);
		FileUtil.write(path, fileContent);
	} 
	
	public static JooVirtualMachine runCode(String path) {
		return runCode(path, true);
	}
	
	public static JooVirtualMachine runCode(String path, boolean startThread) {
		String compiledCode = "";
//		if(path.contains(JooCompiler.CODE_ENDING)) {
//			compiledCode = compileCode(path).getBytecode();
//		} else if(path.contains(JooCompiler.BYTECODE_ENDING)) {
//			compiledCode = FileUtil.read(path);
//		}
//		System.out.print("Starting vm... ");
//		final JooVirtualMachine jooVM = new JooVirtualMachine();
//		final char[] jooCode = compiledCode.toCharArray();
//		final char[] vmCode = jooVM.getCode();
//		jooVM.setJooCodeSize((short) jooCode.length);
//		for (int i = 0; i < jooCode.length; i++) {
//			vmCode[i] = jooCode[i];
//		}
//		System.out.println("succesfully started!");
//		jooVM.initialize();
//		if(startThread) {
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					jooVM.start();
//				}
//			}).start();
//		}
//		return jooVM;
		return new JooVirtualMachine();
	}
	
	public static JooCompiler compileCode(String path) {
		final JooCompiler compiler = new JooCompiler();
//		System.out.print("Compiling code... ");
//		final String compiledCode = compiler.compileProject(path);
//		System.out.println("succesfully compiled!");
//		System.out.println("Variables:\t" + compiler.getVariableCount() + " / " + (VARIABLES_END - VARIABLES_START));
//		System.out.println("Functions:\t" + compiler.getFunctionCount() + " / " + (FUNCTIONS_END - FUNCTIONS_START));
//		System.out.println("Arrays:\t\t" + compiler.getArrayCount() + " / " + (ARRAYS_END - ARRAYS_START));
//		System.out.println("Arrays size:\t" + compiler.getArraysSize() + " / 100");
//		System.out.println("Code size:\t" + compiledCode.length() + " / 1024 bytes");
		return compiler;
	}
}
