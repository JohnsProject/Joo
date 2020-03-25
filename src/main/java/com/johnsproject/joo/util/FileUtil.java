package com.johnsproject.joo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class FileUtil {
	
	/**
	 * Writes the given content into the file at the given path.
	 * 
	 * @param path
	 * @param content
	 */
	public static void write(String path, String content) {
    	FileWriter fileWriter;
		try {
			File file = new File(path);
			if(!file.exists()) {
				file.createNewFile();
			}
			fileWriter = new FileWriter(path);
	    	fileWriter.write(content);
	    	fileWriter.flush();
	    	fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
	/**
	 * Reads the string inside the file at the given path.
	 * 
	 * @param path
	 * @return
	 */
	public static String read(String path) {
    	StringBuilder stringBuilder = new StringBuilder();
    	try {
    		File file = new File(path);
			if(!file.isFile()) {
				return "";
			}
			FileReader fileReader = new FileReader(path);
			int ch = fileReader.read();
		    while(ch != -1) {
		    	stringBuilder.append((char)ch);
		        ch = fileReader.read();
		    }
		    fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return stringBuilder.toString();
    }
	
	/**
	 * Reads the string inside file the has the given path in the resources folder.
	 * 
	 * @param path
	 * @return
	 */
	public static String readResource(String path) {
		StringBuilder stringBuilder = new StringBuilder();
	    try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		    InputStream inputStream = classLoader.getResourceAsStream(path);
		    InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
		    BufferedReader reader = new BufferedReader(streamReader);
			for (String line; (line = reader.readLine()) != null;) {
				stringBuilder.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringBuilder.toString();
	}
}
