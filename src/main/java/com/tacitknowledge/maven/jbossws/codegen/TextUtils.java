package com.tacitknowledge.maven.jbossws.codegen;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Utility class to manipulate text.
 * 
 * @author vkozlov@tacitknowledge.com
 * 
 */
public final class TextUtils {

	/**
	 * Hides default constructor
	 */
	private TextUtils() {
		// Auto-generated constructor stub
	}

	public static void replaceString(File folder, String extension, String search,
			String replace) throws IOException {
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				if (listOfFiles[i].getAbsolutePath().toUpperCase().endsWith(extension)) {
					replaceString(listOfFiles[i].getAbsolutePath(), search, replace);
				}
			}
		}
	}
	
	public static void replaceString(String fileName, String search, String replace) throws IOException {	
		File f = new File(fileName);
		String content = getText(f);
		String updatedContent = content.replace(search, replace);
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(fileName));
			out.write(updatedContent);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
	
	private static String getText(File f) throws IOException {
		if (f.length() > Runtime.getRuntime().freeMemory()) {
		    throw new IOException("Not enough memory to load " + f.getAbsolutePath());
		}
		byte[] data = new byte[(int) f.length()];
		FileInputStream is = null;
		try {
			is = new FileInputStream(f);
			new DataInputStream(new FileInputStream(f)).readFully(data);
		} finally {
			if (is != null) {
				is.close();
			}
		}
		return new String(data);
	}

}
