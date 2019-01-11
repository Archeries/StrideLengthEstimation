package cn.ac.ict.ictips.locating.subPositioningEngines.LandMarkPositioningEngine;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import cn.ac.ict.ictips.locating.subPositioningEngines.LandMarkPositioningEngine.pressurePredicting.Predict_press;

public class Pipeline {
	Predict_press press_pre, pure_press_pre, 
	press_pre_outdoor, pure_press_pre_outdoor;
	double real_floor, real_floor_outdoor, real_floor_user, deltaH;
	double floorNumOutdoor = 0.0;
	private SensorManager mSensorManager;
	private Sensor press_Sensor;
	private SensorEventListener press_SensorEventListener;
	private boolean ifManualOpen = false;// 是否人工输入开启了
	private int state;// 是楼梯1 电梯2还是不知道0
	Date starttime;
	long lasttime = 0;
	Handler mHandler;
	int press_floor, pure_pres_floor,is_recorrect = 0,
			press_floor_outdoor, pure_pres_floor_outdoor;
	double probability, height_ch_floor, height_calc, heightAvg;
	double height_calc_outdoor, heightAvg_outdoor, height_ch_floor_outdoor;
	private double currentFloor = -1;
	SimpleDateFormat f;
	
	public Pipeline(Handler h, double initfloor, Context context,
			SensorManager sManager) {
		mHandler = h;
		real_floor = initfloor;
		press_pre = new Predict_press(0);
		press_pre_outdoor = new Predict_press(0);
		f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		mSensorManager = sManager;
		press_Sensor = sManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		pure_press_pre = new Predict_press((int) initfloor);
	}

	void start() {
		starttime = new Date();
		press_start();
	}

	void press_start() {

		press_SensorEventListener = new SensorEventListener() {
			// 传感器数据变化
			public void onSensorChanged(SensorEvent event) {
				if (ifManualOpen) {
					re_correct_manual((int) real_floor);
					ifManualOpen = false;
				}
				double press = event.values[0];
				height_calc = (1 - Math.pow((press / 1013.25), (1 / 5.264))) / 2.256 * 100000;
				heightAvg = 0;
				Date dt = new Date();
				press_floor = press_pre.predict(height_calc, dt.getTime(),
						(int) real_floor);

				if (currentFloor != press_floor) {
					press_pre.setFastChangeNum(0);
					currentFloor = press_floor;
				}

				pure_pres_floor = pure_press_pre.predict(height_calc,
						dt.getTime(), (int) real_floor);
				String formattime = f.format(dt);

				if (press_floor != -1) {
					heightAvg = press_pre.get_avg_height();
					height_ch_floor = press_pre.get_height_floor();
					state = press_pre.getState();
				}
				JSONObject json = new JSONObject();

				try {
					json.put("heightAvg", heightAvg);
					json.put("height_ch_floor", height_ch_floor);
					if (press_floor <= 0) {
						json.put("press_floor", press_floor - 1);
					}
					json.put("press_floor", press_floor);
					json.put("pure_pres_floor", pure_pres_floor);
					json.put("state", state);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				long cur_time = dt.getTime();
				if (cur_time - lasttime > 1500) {
					lasttime = cur_time;
					savedata(2, formattime);
				}
				Message msg = new Message();
				msg.what = 0;
				msg.arg1 = 2; // 这个标志是2
				msg.obj = json.toString();
				mHandler.sendMessage(msg);
			}

			// 传感器精度变化
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		mSensorManager.registerListener(press_SensorEventListener,
				press_Sensor, SensorManager.SENSOR_DELAY_UI);
	}

	boolean re_correct(int floor) {
		return press_pre.setfloor(floor);
	}

	boolean re_correct_manual(int floor) {
		return press_pre.setfloorManual(floor);
	}

	public boolean isIfManualOpen() {
		return ifManualOpen;
	}

	public void setIfManualOpen(boolean ifManualOpen) {
		this.ifManualOpen = ifManualOpen;
	}

	void stop() {
		mSensorManager.unregisterListener(press_SensorEventListener);
	}

	void savedata(int kind, String time) {
		is_recorrect = 0;
	}

	void set_real_floor(double f) {
		real_floor = f;
	}

	void set_real_floor_user(double f) {
		real_floor_user = f;
	}
}
