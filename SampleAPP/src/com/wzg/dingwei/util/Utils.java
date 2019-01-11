package com.wzg.dingwei.util;

import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.graphics.PointF;
import android.preference.PreferenceManager;

public class Utils {

	public static Random random = new Random(System.currentTimeMillis()); 
	

	
	public static PointF nextDisPoint() {
		Random random = new Random(System.currentTimeMillis());
	
		int x = random.nextInt(100);
//		if (x % 2 == 0) {
//			x = -x;
//		}
		
		int y = random.nextInt(100);
//		if (y % 2 == 0) {
//			y = -y;
//		}
		
		
		return new PointF(-x, -y);
	}
	
	
	public static int randamInt() {
		int result = random.nextInt(100);
		return result;
	}

	public static void saveSharedPreferences(Context context,String key,String value){  
    	SharedPreferences preParas = PreferenceManager.getDefaultSharedPreferences(context); 
        Editor editor = preParas.edit();  
        editor.putString(key, value);   
        editor.commit(); 
    } 
}
