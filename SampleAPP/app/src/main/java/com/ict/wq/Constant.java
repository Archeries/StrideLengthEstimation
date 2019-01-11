package com.ict.wq;

import java.util.ArrayList;

import com.amap.api.maps.model.LatLng;
import com.ict.wq.model.OneTime;

public class Constant {
	public static   int initFloorNo=0;
	public static   boolean isinitFloorAndState=false;
	public static final LatLng northwest = new LatLng(39.981995, 116.326187);// 北京市经纬度
	public static final LatLng northeast = new LatLng(39.982024, 116.326869);// 北京市经纬度
	public static final LatLng southwest = new LatLng(39.981703, 116.326198);// 北京市经纬度
	public static final LatLng southeast = new LatLng(39.981724, 116.326879);// 北京市经纬度
	public static final LatLng weststair= new LatLng(39.981765, 116.326134);// 北京市经纬度
	public static final LatLng eaststair  = new LatLng(39.982036, 116.32697);// 北京市经纬度
	public static int LengthThreshold=15;
	public static   float dtwThreshold=8000;
	public static   float StartPointThreshold=10;
	public static   float initDirection=1000;
	
	
	public static   double magneticDirectioninit=10000;
	public static   double magneticDirectionInPhone=magneticDirectioninit;
	public static   boolean isturned=false;
	 
	
	public static boolean isGPS = false;
	public static boolean isDebug = true;
	
	public static double startlatitude = 36.243308;// 计算所工位
	public static double startlongitude = 120.417269;
	public static final LatLng workPoint  = new LatLng(startlatitude,startlongitude);// 北京市经纬度
 
}
