package com.ict.userstate;

/**
 * Class used to judge the state of the mobile when it is still. 
 * The main method is isLayingStill(), which will return true if the mobile is still and lay down, and false if not.
 * @author Archeries
 * @version 1.0 2015-11-12 17:30:00
 */

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;

public class StillStateJudger implements SensorEventListener {

	/*
	 * The threshold of the inclination angle(the angle included by the
	 * direction of gravity and the negative direction of y-axis), which is used
	 * to judge whether the mobile is lay down.
	 */
	private final double THRESHOLD_ANGLE_OF_INCLINATION = 60;
	/*
	 * The threshold of acceleration, which is used to judge whether the mobile
	 * is still.
	 */
	private final double THRESHOLD_ACCELERATION = 12; // 判断静止的临界总加速度。

	SensorManager sensorManager;
	Sensor proximitySensor;
	Sensor accelerationSensor;

	private boolean isProximate = false;
	private boolean isLayingDown = false;
	private boolean isUp = false;
	private Vibrator vibrator;
	private static Context mContext;
	private int count = 0;
	private int stillCount = 0;
	long lasttimeStamp = 0;
	private double lastx, lasty, lastz;

	/**
	 * Constructor method.
	 * 
	 * @param context
	 *            The context which the StillStateJudger is used in.
	 */
	public StillStateJudger(Context context) {
		mContext=context;
	}

	public void start() {
		sensorManager = (SensorManager) mContext
				.getSystemService(mContext.SENSOR_SERVICE);
		proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		accelerationSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, proximitySensor,
				SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, accelerationSensor,
				SensorManager.SENSOR_DELAY_NORMAL);
		vibrator = (Vibrator) mContext
				.getSystemService(mContext.VIBRATOR_SERVICE);
	}

	/**
	 * Method to judge whether the mobile is still and lay down.
	 * 
	 * @return a boolean, if the mobile is still and lay down, it returns true,
	 *         and if not returns false;
	 */
//	public boolean isLayingStill() {
//		if (isProximate && isLayingDown) {
//			System.out.println("躺着");
//			long timeStamp = System.currentTimeMillis();
//
//			if (timeStamp - lasttimeStamp > 20000) {
//				System.out.println("timeStamp" + timeStamp + " "
//						+ lasttimeStamp);
//				if (isUp) {
//					System.out.println("向上躺着");
//					vibrator.vibrate(new long[] { 20, 4000 }, 1);
//				} else {
//					System.out.println("向下躺着");
//					vibrator.vibrate(new long[] { 20, 6000, 2000, 6000 }, 1);
//				}
//
//				lasttimeStamp = timeStamp;
//			}
//			return true;
//		} else
//			System.out.println("正常");
//		vibrator.cancel();
//		return false;
//	}

	/**
	 * Method of SensorEventListener, will record the proximity and lay down
	 * status when the sensors change.
	 * 
	 * @param event
	 *            Event of sensor.
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
			System.out.println("event.values[0]" + event.values[0]);
			if (event.values[0] < 1)
				isProximate = true;
			else
				isProximate = false;
			System.out.println("Proximity " + isProximate);
		} else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			double x = event.values[0];
			double y = event.values[1];
			double z = event.values[2];
			count++;
			double g = Math.sqrt(x * x + y * y + z * z);
			if (g > 8 && g < 12) {// 静止
		//		System.out.println("G" + g + " 静止");
				stillCount++;
				if (stillCount > 1000) {
				
					if (event.values[1] < g
							* Math.cos(THRESHOLD_ANGLE_OF_INCLINATION / 180
									* Math.PI)) {// 平躺
					//	System.out.println("state  " + x + " " + y + " " + z);

						if (z > 0&&isProximate) {
							isUp = true;
							System.out.println("isUp" + isUp);
							long currentTime=System.currentTimeMillis();
							System.out.println("静止向上平躺");
							if ((currentTime-lasttimeStamp)>30000) {
								
								lasttimeStamp=System.currentTimeMillis();
								vibrator.vibrate(new long[] { 20, 15000 }, -1);
							}
						} else if (z<0&&isProximate) {
							isUp = false;
							System.out.println("isUp" + isUp);
							System.out.println("静止向下平躺");
							long currentTime=System.currentTimeMillis();
							if ((currentTime-lasttimeStamp)>30000) {
								lasttimeStamp=System.currentTimeMillis();
								vibrator.vibrate(new long[] { 1000, 5000,1000, 5000 ,1000, 5000  }, -1);
							}
						}
						isLayingDown = true;
					} else {
						isLayingDown = false;
						System.out.println("静止站立");
						long currentTime=System.currentTimeMillis();
						if ((currentTime-lasttimeStamp)>30000) {
							System.out.println("静止站立");
							
							lasttimeStamp=System.currentTimeMillis();
							vibrator.vibrate(new long[] { 20, 5000 }, -1);
						}
					}
				}
			} else {
				stillCount=0;
				System.out.println("G" + g + " 运动");
				vibrator.cancel();
			}
 
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	/**
	 * Method to unregister listeners.
	 */
	public void close() {
		if (sensorManager != null) {
			sensorManager.unregisterListener(this);
		}
	}

}