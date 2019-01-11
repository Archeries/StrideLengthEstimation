package com.ict.wq;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.Application;
import android.os.Environment;
import android.util.Log;

public class LogUtil {
    private static final String LOG_DIR = Environment
            .getExternalStorageDirectory() + "//RecordedLog//";

    private static final String LOG_NAME = getCurrentDateString() + ".txt";
 
    public static UncaughtExceptionHandler handler = new UncaughtExceptionHandler() {
 
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            writeErrorLog(ex);
            exit();
        }
    };
 
    /**
     * 打印错误日志
     * 
     * @param ex
     */
    protected static void writeErrorLog(Throwable ex) {
        String info = null;
        ByteArrayOutputStream baos = null;
        PrintStream printStream = null;
        try {
            baos = new ByteArrayOutputStream();
            printStream = new PrintStream(baos);
            ex.printStackTrace(printStream);
            byte[] data = baos.toByteArray();
            info = new String(data);
            data = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (printStream != null) {
                    printStream.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d("example", "崩溃信息\n" + info);
        File dir = new File(LOG_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, LOG_NAME);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write(info.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
 
    }
 
    /**
     * 获取当前日期
     * 
     * @return
     */
    private static String getCurrentDateString() {
        String result = null;
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
//                Locale.getDefault());
//        Date nowDate = new Date();
//        result = sdf.format(nowDate);
        
        Date date=new Date();  
   	 SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMddHHmmss");  
   	 String time=formatter.format(date);  
        return "Log"+time;
    }
 
    /**
     * 杀死该应用进程
     */
    public static void exit() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
