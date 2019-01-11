package com.ict.ioDetect;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.ict.iodetector.service.conf.Configuration;
import com.ict.iodetector.service.conf.Mode;
import com.ict.iodetector.service.conf.Threshold;

public class IndoorOrOutdoor {
	private Context mContext;

	public IndoorOrOutdoor(Context mContext, Handler mHandler) {
		super();
		this.mContext = mContext;
		this.mHandler = mHandler;
	}

	private Handler mHandler;
	private LightMode lightMode;
	private GPGSVMode gpgsvMode;
	private SensorManager sensorManager;
	private static final String PHONE_MODEL = android.os.Build.MODEL;

	public void init() {
//		File f = new File(Configuration.CONFIG_FITH);
//		if (f.exists()) {
//			setupConfig();
//		} else {
			sensorManager = (SensorManager) mContext
					.getSystemService(Context.SENSOR_SERVICE);
			gpgsvMode = new GPGSVMode(mContext, true);
//			lightMode = new LightMode(sensorManager, mContext, true);
//			lightMode.start();
	//	}

	}

	/**
	 * reading the configuration file will consume more time, so do it in a
	 * newthread
	 */
	private void setupConfig() {
		new Thread() {
			@Override
			public void run() {
				super.run();
				Looper.prepare();
				Configuration conf = new Configuration();
				ArrayList<Mode> modes = conf.getModes();
				for (int i = 0; i < modes.size(); i++) {
					setupModes(modes.get(i));
				}
				mHandler.obtainMessage(MSG_START_DETECT).sendToTarget();
				Looper.loop();
			}
		}.start();
	}

	/**
	 * @param m
	 *            mode set up the mode and every mode's threshold
	 */
	private void setupModes(Mode m) {
		Threshold t = m.getThresholds().get(PHONE_MODEL);

		if (m.getName().equals("GPGSV")) {
			gpgsvMode = new GPGSVMode(mContext, m.isEnable());
			if (m.isEnable() && t != null && !t.getValue().equals("-1"))
				gpgsvMode.setTHRESHOLD(Integer.parseInt(t.getValue()));
		} 
//		else if (m.getName().equals("Light")) {
//			lightMode = new LightMode(sensorManager, mContext, m.isEnable());
//			if (m.isEnable() && t != null) {
//				if (!t.getHigh().equals("-1"))
//					lightMode.setHIGH_THRESHOLD(Integer.parseInt(t.getHigh()));
//				if (!t.getLow().equals("-1"))
//					lightMode.setLOW_THRESHOLD(Integer.parseInt(t.getLow()));
//				if (!t.getVariance().equals("-1"))
//					lightMode.setLightVariance(Integer.parseInt(t.getVariance()));
//			}
//			lightMode.start();
//		}

	}

	public static final int MSG_SET_WEATHER = 0;
	public static final int MSG_START_DETECT = 1;
	private DetectionProfile gpgsvProfile[];
	Timer timer;
	private DetectionProfile lightProfile[];
	// proportion of each mode
	private double LIGHT_WEI = 0;
	private double GPGSV_WEI = 0;
	private double indoorProbability;
	private double [] IODetectResult=new double[3];
	 

 

	public double[] getIODetectResult() {
		return IODetectResult;
	}

	public void start() {

		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				gpgsvProfile = gpgsvMode.getProfile();
//				  lightProfile = lightMode.getProfile();
//				Log.d("gpgsv", gpgsvProfile[0].getConfidence() + " "
//						+ gpgsvProfile[1].getConfidence() + " "
//						+ gpgsvProfile[2].getConfidence());
//				Log.d("gpgsv", "lightProfile"+lightProfile[0].getConfidence() + " "
//						+ lightProfile[1].getConfidence() + " "
//						+ lightProfile[2].getConfidence());
//	 
//					// daytime
//					LIGHT_WEI = 0.2;
			//		GPGSV_WEI = 0.8;
 
//				indoorProbability = lightProfile[0].getConfidence() * LIGHT_WEI
//						+ gpgsvProfile[0].getConfidence() * GPGSV_WEI
//						 ;
			//	 IODetectResult[0]=lightProfile[0].getConfidence();
				 IODetectResult[1]=gpgsvProfile[0].getConfidence();
			//	 IODetectResult[2]=indoorProbability;
	//			Log.d("gpgsv", "indoor"+indoorProbability);
			}
		}, 0, 1000);
	}
}
