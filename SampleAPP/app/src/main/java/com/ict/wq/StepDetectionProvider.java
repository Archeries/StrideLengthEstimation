package com.ict.wq;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class StepDetectionProvider {

	private Context mContext;
	private SensorManager sensorManager;

	private boolean zhenjia = false;
	private final float FILTERING_VALAUE = 0.84f;
	private double A = 0.0, B = 0.0;
	private float lowX = 0, lowY = 0, lowZ = 0, X = 0, Y = 0, Z = 0;
	private int s = 0, sum = 0, x = 0, y = 0, z = 0, w = 0, b = 0,
			c = 0, d = 0, newsum = 0, time_s2, biaoji = 0, newsum2 = 0, o = 0,
			num = 0, flag = 0;
	double previousStep = 0;
	Sensor accelerometer;
	private long lastTurnTime;//上一步的时间

	public StepDetectionProvider(Context ctx,StepDetectedCallBack cb) {
		mContext = ctx;
		sensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		stepDetectionCallback = cb;
	}

	public void initValue() {
		zhenjia = false;
		A = 0.0;
		B = 0.0;
		lowX = 0;
		lowY = 0;
		lowZ = 0;
		X = 0; Y = 0; Z = 0;
		s = 0;sum = 0; x = 0; y = 0; z = 0; w = 0; b = 0;
		c = 0; d = 0; newsum = 0; biaoji = 0;newsum2 = 0;o = 0;
		num = 0; flag = 0;
		previousStep = 0;
	}
	
	public void start() {
		initValue();
		sensorManager.registerListener(accelerometerListener,
				accelerometer, 200);
	}

	public void stop() {
		sensorManager.unregisterListener(accelerometerListener);
	}

	private final SensorEventListener accelerometerListener = new SensorEventListener() {

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		public void onSensorChanged(SensorEvent sensorEvent) {
			int flag = 0;

			if (b == 0) {
				x++;
			} else if (c == 0) {
				y++;
			} else if (d == 0) {
				z++;
			} else {
				w++;
			}

			if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				X = sensorEvent.values[0];
				Y = sensorEvent.values[1];
				Z = sensorEvent.values[2];

				// Low-Pass Filter 
				lowX = lowX * FILTERING_VALAUE + X
						* (1.0f - FILTERING_VALAUE);
				lowY = lowY * FILTERING_VALAUE + Y
						* (1.0f - FILTERING_VALAUE);
				lowZ = lowZ * FILTERING_VALAUE + Z
						* (1.0f - FILTERING_VALAUE);

				A = Math.sqrt(lowX * lowX + lowY * lowY + lowZ * lowZ);
				B = A - 9.8;
				num++;
				if (B > 6 || B < -5.0) {
					s = 0;
					num = 0;
				} else {
					if (s == 0 && flag == 0) {
						flag = 1;
						biaoji = 0;
						num = 0;
						if (B < 0.5) {
							s = 0;
						} else {
							s = 1;
						}
					}

					if (s == 1 && flag == 0) {
						time_s2 = 0;
						flag = 1;
						if (B < 0.9 && B >= 0.5) {
							s = 1;
						}
						if (B >= 0.9) {
							s = 2;
						}
						if (B < 0.5) {
							s = 4;
						}

					}

					if (s == 4 && flag == 0) {
						flag = 1;
						if (biaoji >= 10) {
							s = 0;
						} else {
							if (B >= 0.5) {
								s = 1;
							} else {
								biaoji++;
							}
						}

					}

					if (s == 2 && flag == 0) {
						flag = 1;
						time_s2++;
						if (time_s2 > 100) {
							s = 0;
						} else {
							if (B >= 0.9) {
								s = 2;
							}
							if (B < -0.5) {
								s = 3;
							}
						}
					}

					if (s == 3 && flag == 0) {
						flag = 1;
						s = 6;

					}

					if (s == 6 && flag == 0) {
						flag = 1;

						s = 0;
						sum++;

						if (sum < 4) {
							newsum++;
							
							long currentTime=System.currentTimeMillis() ;
							if ((currentTime- lastTurnTime) / 1000> 1) {
								lastTurnTime = currentTime;
//								Toast.makeText(mContext, "检测到步子 "+newsum , Toast.LENGTH_SHORT).show();
								stepDetectionCallback.onStepDetected(newsum);
							}
							
							zhenjia = false;
							if (b == 0) {
								b++;
								y = x;
							} else if (c == 0) {
								c++;
								z = y;
							} else if (d == 0) {
								d++;
								w = z;
							} else {

							}
						} else {
							if ((w - x) < 300 && (w - x) > 20) {
								newsum++;
								long currentTime=System.currentTimeMillis() ;
								if ((currentTime- lastTurnTime) / 1000> 1) {
									lastTurnTime = currentTime;
//									Toast.makeText(mContext, "检测到步子 "+newsum , Toast.LENGTH_SHORT).show();
									stepDetectionCallback.onStepDetected(newsum);
								}
								zhenjia = true;
								b = 0;
								x = w;

							} else if ((x - y) < 300 && (x - y) > 20) {

								newsum++;
								long currentTime=System.currentTimeMillis() ;
								if ((currentTime- lastTurnTime) / 1000> 1) {
									lastTurnTime = currentTime;
//									Toast.makeText(mContext, "检测到步子 "+newsum , Toast.LENGTH_SHORT).show();
									stepDetectionCallback.onStepDetected(newsum);
								}
								zhenjia = true;
								c = 0;
								b = 1;
								y = x;
							} else if ((y - z) < 300 && (y - z) > 20) {
								newsum++;
								long currentTime=System.currentTimeMillis() ;
								if ((currentTime- lastTurnTime) / 1000> 1) {
									lastTurnTime = currentTime;
//									Toast.makeText(mContext, "检测到步子 "+newsum , Toast.LENGTH_SHORT).show();
									stepDetectionCallback.onStepDetected(newsum);
								}
								zhenjia = true;
								d = 0;
								c = 1;
								z = y;
							} else if ((z - w) < 300 && (z - w) > 20) {
								newsum++;
								long currentTime=System.currentTimeMillis() ;
								if ((currentTime- lastTurnTime) / 1000> 1) {
									lastTurnTime = currentTime;
//									Toast.makeText(mContext, "检测到步子 "+newsum , Toast.LENGTH_SHORT).show();
									stepDetectionCallback.onStepDetected(newsum);
								}
								zhenjia = true;
								w = z;
								d = 1;
							} else {
								newsum++;
								long currentTime=System.currentTimeMillis() ;
								if ((currentTime- lastTurnTime) / 1000> 1) {
									lastTurnTime = currentTime;
//									Toast.makeText(mContext, "检测到步子 "+newsum , Toast.LENGTH_SHORT).show();
									stepDetectionCallback.onStepDetected(newsum);
								}
								zhenjia = false;
								sum = 1;
								b = 1;
								c = 0;
								d = 0;
								x = 40;
								y = 40;
								z = 0;
								w = 0;
							}
						}

						if (zhenjia == false) {
							o = 0;
						} else {
							if (o == 0) {
								newsum2 = newsum2 + 3;
							}
							newsum2++;
							o = 1;
						}
					}
				}
			}
		}
	};

	public int getStepCnt() {
		return newsum;
	}
	private StepDetectedCallBack stepDetectionCallback;
	
	public interface StepDetectedCallBack {
		public void onStepDetected(int stepCount);
	}
}
