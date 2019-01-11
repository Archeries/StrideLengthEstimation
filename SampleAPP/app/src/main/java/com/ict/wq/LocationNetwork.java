package com.ict.wq;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
 

/**
 * AMapV2地图中简单介绍混合定位
 */
public class LocationNetwork     implements
		AMapLocationListener, Runnable {
	private LocationManagerProxy aMapLocManager = null;
 
	private AMapLocation aMapLocation;// 用于判断定位超时
	 
private Context mcContext;
public LocationNetwork(Context mcContext, Handler mHandler) {
	super();
	this.mcContext = mcContext;
	this.mHandler = mHandler;
}

private Handler mHandler;
	 
 
	public final static int INIT_LOCATION = 100010;
	public void Start(   ) {
		aMapLocManager = LocationManagerProxy.getInstance(mcContext);
		/*
		 * mAMapLocManager.setGpsEnable(false);//
		 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
		 * API定位采用GPS和网络混合定位方式
		 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
		 */
		aMapLocManager.requestLocationData(
				LocationProviderProxy.AMapNetwork, 2000, 10, this);
	 
		mHandler.postDelayed(this, 12000);// 设置超过12秒还没有定位到就停止定位
	}

	protected void onPause() {
		stopLocation();// 停止定位
	}

	/**
	 * 销毁定位
	 */
	private void stopLocation() {
		if (aMapLocManager != null) {
			aMapLocManager.removeUpdates(this);
			aMapLocManager.destroy();;
		}
		aMapLocManager = null;
	}

	/**
	 * 此方法已经废弃
	 */
	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	/**
	 * 混合定位回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation location) {
		if (location != null) {
//			this.aMapLocation = location;// 判断超时机制
//			Double geoLat = location.getLatitude();
//			Double geoLng = location.getLongitude();
//			Log.d("wqlocation", "geoLat"+geoLat+"geoLng"+geoLng);
//			String cityCode = "";
//			String desc = "";
//			Bundle locBundle = location.getExtras();
//			if (locBundle != null) {
//				cityCode = locBundle.getString("citycode");
//				desc = locBundle.getString("desc");
//			}
//			String str = ("定位成功:(" + geoLng + "," + geoLat + ")"
//					+ "\n精    度    :" + location.getAccuracy() + "米"
//					+ "\n定位方式:" + location.getProvider() );
			if (location.getAccuracy()>0) {//定位精度为0 表示定位失败；
				Message message4 = new Message();
				message4.what = INIT_LOCATION;
				message4.obj = location;
				mHandler.sendMessage(message4);
				stopLocation();
				
			}
		}else {
			
			Message message4 = new Message();
			message4.what = INIT_LOCATION;
			message4.obj ="定位中。。。。。。";
			mHandler.sendMessage(message4);
		}
	}

	@Override
	public void run() {
		if (aMapLocation == null) {
	//		ToastUtil.show(mcContext, "12秒内还没有定位成功，停止定位");
			stopLocation();// 销毁掉定位
			Message message4 = new Message();
			message4.what = INIT_LOCATION;
			message4.obj ="12秒内还没有定位成功，停止定位";
			mHandler.sendMessage(message4);
		}
	}
}
