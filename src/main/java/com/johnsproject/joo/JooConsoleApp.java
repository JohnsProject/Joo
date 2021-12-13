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
	
	public static void createTemplate(String path) {
		String fileContent = FileUtil.read(PATH_TEMPLATE);
		FileUtil.write(path, fileContent);
	} 
	
	public static JooVirtualMachine runCode(String path) {
		return runCode(path, true);
	}
	
	public static JooVirtualMachine runCode(String path, boolean startThread) {
		String compiledCode = "";
		if(path.contains(JooCompiler.CODE_ENDING)) {
			compiledCode = new JooCompiler().compile(path);
			FileUtil.write(path.replace(JooCompiler.CODE_ENDING, JooCompiler.BYTECODE_ENDING), compiledCode);
		} else if(path.contains(JooCompiler.BYTECODE_ENDING)) {
			compiledCode = FileUtil.read(path);
		}
		final JooVirtualMachine jooVM = new JooVirtualMachine();
		jooVM.initialize(compiledCode.toCharArray());
		if(startThread) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					jooVM.start();
				}
			}).start();
		}
		return jooVM;
	}
	
	public static JooCompiler compileCode(String path) {
		final JooCompiler compiler = new JooCompiler();
		System.out.print("Compiling code... " + path);
		final String compiledCode = compiler.compile(path);
		FileUtil.write(path.replace(JooCompiler.CODE_ENDING, JooCompiler.BYTECODE_ENDING), compiledCode);
		System.out.println(" succesfully compiled!");
		System.out.println("Byte code size: " + compiledCode.length() + " / 1024 bytes");
		System.out.println("Variable and function count: " + compiler.getComponentMemoryUsage() + " / " + (COMPONENTS_END - COMPONENTS_START));
		System.out.println("Array memory usage: " + compiler.getArrayMemoryUsage() + " / " + (108));
		return compiler;
	}
}
