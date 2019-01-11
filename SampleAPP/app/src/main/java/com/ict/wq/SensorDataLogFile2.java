package com.ict.wq;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

public class SensorDataLogFile2 {
	
	 private static File debugFile=null;
	 private static FileOutputStream fileStream=null;
	
	 public static void trace(String str) 
	 {
		 //if(DataCenter.debugFlag==false) return;
		 if(debugFile==null)
		 {
			 Date date=new Date();  
			 SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMddHHmmss");  
			 String time=formatter.format(date);  
			 debugFile=new File(Environment.getExternalStorageDirectory()+"//Sensordata//");
			 if (!debugFile.exists()) {
				 debugFile.mkdirs();
			 }
			 debugFile=new File(Environment.getExternalStorageDirectory()+"//Sensordata//","origindata"+time+".txt");
			 
			 try {
				fileStream=new FileOutputStream(debugFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		try {
			 Date date=new Date();  
			 SimpleDateFormat formatter=new SimpleDateFormat("HH:mm:ss");  
			 String time=formatter.format(date);  
			 fileStream.write((str).getBytes());
			 fileStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	 }
	 
	 public static void trace( Exception e) 
	 {
		 
		 if(fileStream!=null)
		 {
			 PrintWriter p = new PrintWriter(fileStream);
			 e.printStackTrace(p);
			 p.flush();
		 }
		 
	 }
	 
	
	 
}
