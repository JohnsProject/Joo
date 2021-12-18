package com.johnsproject.joo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	 * Checks if the file at the given path is exists.
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
	 * Checks if the resource at the given path exists.
	 * 
	 * @param path
	 * @return
	 */
	public static boolean resourceExists(String path) {
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
	
	
	/**
	 * Returns a list with the names of the files in the specified directory.
	 * First tries to read the file from resources folder, if file is not present
	 * tries to read the file from file system.
	 * 
	 * @param path
	 * @return
	 */
	public static List<String> list(String path) {
		final List<String> files = new ArrayList<>();
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    InputStream inputStream = classLoader.getResourceAsStream(path);
	    if(inputStream == null) {
	    	File diretory = new File(path);
	    	files.addAll(Arrays.asList(diretory.list()));
	    } else {
			try {
			    InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
			    BufferedReader reader = new BufferedReader(streamReader);
			    
				for (String line; (line = reader.readLine()) != null;) {
					files.add(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
		
		return files;
	} 
}
