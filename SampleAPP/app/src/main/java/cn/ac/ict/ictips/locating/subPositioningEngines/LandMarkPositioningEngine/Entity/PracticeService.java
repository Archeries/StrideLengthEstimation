package cn.ac.ict.ictips.locating.subPositioningEngines.LandMarkPositioningEngine.Entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.IBinder;

public class PracticeService extends Service implements SensorEventListener {

	String message = new String();

	public List<String> writeString = new ArrayList<String>();
	private boolean zhenjia = false;
	private SensorManager sm;
	private final float FILTERING_VALAUE = 0.84f;

	List<Long> stepTimeList = new ArrayList<Long>();

	private double A = 0.0, B = 0.0;
	private float lowX = 0, lowY = 0, lowZ = 0, X = 0, Y = 0, Z = 0;
	private int s = 0, sum = 0, x = 0, y = 0, z = 0, w = 0, b = 0, c = 0,
			d = 0, time_s2, biaoji = 0, newsum2 = 0, o = 0, kongzhi = 1;
	Double[] qArray = { 0.43436, 1.097482, 0.013878 };// 计算步长的参数
	// 为了气压设定的两个变量，用于储存计计步的步数还有时间
	List<Integer> stepNumListForPress = new ArrayList<Integer>();
	List<Long> stepTimeListForPress = new ArrayList<Long>();
	// 这个要记录每pressThr有多少步伐
	List<Integer> numberListForPress = new ArrayList<Integer>();
	int pressThr = 2000;// 设定的上面的两个list中储存多久的数据
	int numberThr = 4;// 这个规定了numberListForPress里面的个数
	long lastTime = 0;

	private BroadcastReceiver broadcastReceiver;

	public void onCreate() {
		super.onCreate();
		sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sm.registerListener(this,
				sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 200);
		Sensor msensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		sm.registerListener(this, msensor, SensorManager.SENSOR_DELAY_NORMAL);

	}

	/**
	 * unregister the listener
	 */
	public void stop() {
		unregisterReceiver(broadcastReceiver);
		kongzhi = 0;
		stopSelf();

	}

	@Override
	public ComponentName startService(Intent service) {
		return super.startService(service);
	}

	@Override
	public boolean stopService(Intent name) {
		stop();
		return super.stopService(name);
	}

	public void onSensorChanged(SensorEvent event) {

		if (kongzhi == 1) {
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

			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

				// 图解中已经解释三个值的含义
				X = event.values[0];
				Y = event.values[1];
				Z = event.values[2];
				// Low-Pass Filter 用低通滤波器剔除高频的干扰
				lowX = lowX * FILTERING_VALAUE + X * (1.0f - FILTERING_VALAUE);
				lowY = lowY * FILTERING_VALAUE + Y * (1.0f - FILTERING_VALAUE);
				lowZ = lowZ * FILTERING_VALAUE + Z * (1.0f - FILTERING_VALAUE);
				A = Math.sqrt(lowX * lowX + lowY * lowY + lowZ * lowZ);
				B = A - 9.8;
				if (B > 10 || B < -10.0) {
					s = 0;

				} else {
					if (s == 0 && flag == 0) {
						flag = 1;
						biaoji = 0;
						if (B < 0.6) {
							s = 0;
						} else {
							s = 1;
						}
					}
					if (s == 1 && flag == 0) {
						time_s2 = 0;
						flag = 1;
						if (B < 1.0 && B >= 0.6) {
							s = 1;
						}
						if (B >= 1.2) {
							s = 2;
						}
						if (B < 0.6) {
							s = 4;
						}
					}
					if (s == 4 && flag == 0) {
						flag = 1;
						if (biaoji >= 10) {
							s = 0;
						} else {
							if (B >= 0.6) {
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
							if (B >= 1) {
								s = 2;
							}
							if (B < -0.6) {
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
								zhenjia = true;
								b = 0;
								x = w;
							} else if ((x - y) < 300 && (x - y) > 20) {
								zhenjia = true;
								c = 0;
								b = 1;
								y = x;
							} else if ((y - z) < 300 && (y - z) > 20) {
								zhenjia = true;
								d = 0;
								c = 1;
								z = y;
							} else if ((z - w) < 300 && (z - w) > 20) {
								zhenjia = true;
								w = z;
								d = 1;
							} else {
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
			} else {
				return;
			}

			Date curDate = new Date(System.currentTimeMillis());
			long diff = curDate.getTime();

			// 如果list中的第一个和最后一个数据之间已经大于了设定的时间
			// 就把时间和
			if (stepTimeListForPress.size() != 0
					&& stepTimeListForPress
							.get(stepTimeListForPress.size() - 1)
							- stepTimeListForPress.get(0) > pressThr) {
				if (stepNumListForPress.get(stepNumListForPress.size() - 1)
						- stepNumListForPress.get(0) < 0) {
					numberListForPress.add(stepNumListForPress
							.get(stepNumListForPress.size() - 1));
				} else {
					numberListForPress.add(stepNumListForPress
							.get(stepNumListForPress.size() - 1)
							- stepNumListForPress.get(0));
				}
				stepTimeListForPress.clear();
				stepNumListForPress.clear();

			}
			if (numberListForPress.size() > numberThr) {
				numberListForPress.remove(0);
			}
			int numberSum = 0;
			if (numberListForPress.size() == numberThr) {

				for (int i = 0; i < numberListForPress.size(); i++) {

					numberSum += numberListForPress.get(i);
				}

			}
			stepTimeListForPress.add(diff);// 将时间记录进去
			stepNumListForPress.add(sum);// 将当前的步伐的号码记录进去

			Intent intent = new Intent();
			intent.putExtra("numberSum", numberSum);
			intent.setAction("com.example.floor_identify_2015");
			sendBroadcast(intent);

		}
	}

	public void onDestroy() {

		stop();
		super.onDestroy();
	}

	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		// 注册receiver，接收Activity发送的广播，停止线程，停止service
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.excample.stopService");
		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				stopSelf();// 在service中停止service
			}
		};
		registerReceiver(broadcastReceiver, filter);
		return super.onStartCommand(intent, flags, startId);
	}

}
