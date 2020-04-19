/*
MIT License

Copyright (c) 2020 John´s Project

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
	 * Checks if the file at the given path is exists or not.
	 * 
	 * @param path
	 * @return
	 */
	public static boolean fileExists(String path) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    InputStream inputStream = classLoader.getResourceAsStream(path);
	    if(inputStream == null) {
	    	return new File(path).isFile();
	    }
	    return true;
	}
	
	/**
	 * Checks if the file at the given path is inside the resources folder or not.
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isResource(String path) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    InputStream inputStream = classLoader.getResourceAsStream(path);
	    if(inputStream == null) {
	    	return false;
	    }
	    return true;
	}
	
	/**
	 * Reads the string inside the file at the given path.
	 * First tries to read the file from resources folder, if file is not present
	 * tries to read the file from file system.
	 * 
	 * @param path
	 * @return
	 */
	public static String read(String path) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    InputStream inputStream = classLoader.getResourceAsStream(path);
	    if(inputStream == null) {
	    	return readFromFile(path);
	    }
		StringBuilder stringBuilder = new StringBuilder();
	    try {
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
	
	private static String readFromFile(String path) {
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
}
