package com.ict.wq;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import android.os.Environment;
import android.util.Log;

public class SensorDataLogFileSpectrum {
	
	 private static File debugFile=null;
	 private static FileOutputStream fileStream=null;
		private WritableWorkbook wwb;
		private String excelPath;
		private String name,gender;
		private File excelFile;
	 public static void trace(String str) 
	 {
		 //if(DataCenter.debugFlag==false) return;
		 if(debugFile==null)
		 {
			 Date date=new Date();  
			 SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMddHHmmss");  
			 String time=formatter.format(date);  
			 debugFile=new File(Environment.getExternalStorageDirectory()+"//Sensordata//","Spectrum"+time+".txt");
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
	 
	 
	 
	 
	// 创建excel表.
		public void createExcel( ) {
			 Date date=new Date();  
			 SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMddHHmmss");  
			 String time=formatter.format(date);  
			excelPath = getExcelDir()+File.separator+"demo"+time+".csv";
			excelFile = new File(excelPath);
			
			WritableSheet ws = null;
			try {
				if (!excelFile.exists()) {
					wwb = Workbook.createWorkbook(excelFile);

					ws = wwb.createSheet("sheet1", 0);
					
//					ws = wwb.createSheet("sheet2", 1);
//					ws = wwb.createSheet("sheet3", 2);
					// 在指定单元格插入数据
//					Label lbl1 = new Label(0, 0, "姓名");
//					Label bll2 = new Label(1, 0, "性别");
//					Label bll3 = new Label(2, 0, "Z");
//
//					ws.addCell(lbl1);
//					ws.addCell(bll2);
//					ws.addCell(bll3);
					

					// 从内存中写入文件中
					wwb.write();
					wwb.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public   void writeToExcel(String X,String Y,String Z) {
            	try {
				Workbook oldWwb = Workbook.getWorkbook(excelFile);
				wwb = Workbook.createWorkbook(excelFile,
						oldWwb);
				WritableSheet ws = wwb.getSheet(0);
				// 当前行数
				int row = ws.getRows();
				Label lbl1 = new Label(0, row, X);
				Label bll2 = new Label(1,row,Y);
				Label bll3 = new Label(2,row,Z);

				ws.addCell(lbl1);
				ws.addCell(bll2);
				ws.addCell(bll3);

				// 从内存中写入文件中,只能刷一次.
				wwb.write();
				wwb.close();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		public   void writeToExcel(double X,double Y ,double Z) {
        	try {
			Workbook oldWwb = Workbook.getWorkbook(excelFile);
			wwb = Workbook.createWorkbook(excelFile,
					oldWwb);
			WritableSheet ws = wwb.getSheet(0);
			// 当前行数
			int row = ws.getRows();
			Label lbl1 = new Label(0, row, String.valueOf(X));
			Label bll2 = new Label(1,row,String.valueOf(Y));
			Label bll3 = new Label(2,row,String.valueOf(Z));

			ws.addCell(lbl1);
			ws.addCell(bll2);
			ws.addCell(bll3);

			// 从内存中写入文件中,只能刷一次.
			wwb.write();
			wwb.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
		
		// 获取Excel文件夹
			public String getExcelDir() {
				// SD卡指定文件夹
				String sdcardPath = Environment.getExternalStorageDirectory()
						.toString();
				File dir = new File(sdcardPath + File.separator + "Sensordata"
						 );

				if (dir.exists()) {
					return dir.toString();

				} else {
					dir.mkdirs();
					Log.d("BAG", "保存路径不存在,");
					return dir.toString();
				}
			}

	
	 
}
