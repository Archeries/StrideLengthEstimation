package com.ict.wq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileReadAndWrite {
	
	private final static boolean debug = true;
	private final static String logFile = "log";
	
	public static String readFile(String fileName) {
		File file = new File(fileName);
		return readFile(file);
	}
	
	private static String readFile(File file) {
		BufferedReader reader = null;
		String res = "";
		try {
			reader = new BufferedReader(new FileReader(file));
			String tmp = null;
			while((tmp = reader.readLine()) != null)
				res += tmp;
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return res;	
	}
	
	public static void writeFile(String fileName, String content) {
		writeFile(fileName, content, false);
	}
	
	public static void writeFile(String fileName, String content, boolean append) {
		try {
			File file = new File(fileName);
			if(!file.exists()) {
				System.out.println("file " + fileName + " not exists, create it!");
				File dir = new File(file.getParent());
				if(!dir.exists()) {
					System.out.println("parent directory not exits, create it");
					dir.mkdirs();
				}
				file.createNewFile();
			}
			FileWriter writer = new FileWriter(fileName, append);
			writer.write(content + "\n");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void log(String content, String tag) {
		if(!debug) return;
		File fi = new File(logFile);
		if(!fi.exists()) {
			try {
				fi.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd-HH-mm:ss:ms");
		String str = tag + " " + time.format(new Date()) + "\n" + content + "\n";
		writeFile(logFile, str, true);	
	}
	
	public static boolean checkExist(String fileName) {
		File files = new File(fileName);
		return files.exists();
	}
}
