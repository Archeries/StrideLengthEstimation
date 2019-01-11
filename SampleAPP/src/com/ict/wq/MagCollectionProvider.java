package com.ict.wq;

import java.util.ArrayList;
import java.util.Arrays;

import com.ict.gyroscopecalibrate.MeanFilterSmoothing;
import com.ict.ioDetect.IndoorOrOutdoor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;
import android.view.View.MeasureSpec;

public class MagCollectionProvider {

	private static Context mContext;

	private static Handler mhandler;
	private static SensorManager sensorManager;
	private static Sensor accelerometer;
	private static Sensor gravity;
	private static Sensor mag;
	private static Sensor gyroscope;
	private static Sensor orientationold;
	private static Sensor press;
	private static Sensor light;
	private static float[] accelerometerValues = new float[3];
	private static float[] magneticFieldValues = new float[3];
	private static float[] gyroscopeValues = new float[3];
	private static float[] orientationOldValues = new float[3];
	private static float[] orientationNewValues = new float[3];
	private static float[] gravityValues = new float[3];
	private static double currentHeight = 0;
	private static double currentLight = 0;

	private static double currentMagRecord = 0;
	private static String currentMagRecordvector = "0,0,0";
	private static double currentDirectionRecord = 0;// 当前的平均地磁方向
	private static double realTimeMagDirection = 0;// 当前地磁的瞬时读数
	private static ArrayList<Double> windowsList = new ArrayList<Double>();
	private static int sensordatacount = 0;
	// 转弯之后的前5个地磁数据不要
	static int dropNum = 10;
	static boolean dropFlag = false;
	public static ArrayList<Double> magArr1 = new ArrayList<Double>();
	static ArrayList<Double> magArr2 = new ArrayList<Double>();

	private static double lastthetaIngyroscope = 0;
	private static double thetaIngyroscope = 0;
	private static float lastX = 0;
	private static float lastY = 0;
	private static double lastxIngetTheta = 0;
	private static double lastyIngetTheta = 0;
	public final static int DIRECTION = 100008;
	private static double currDirection;
	private static double indoorProbability;

	// 创建常量，把纳秒转换为秒。
	private static final float NS2S = 1.0f / 1000000000.0f;
	private final static float[] deltaRotationVector = new float[4];
	private static float timestamp;
	protected static final float EPSILON = 0.000000001f;
	static IndoorOrOutdoor indoorOrOutdoor = null;
	private static MeanFilterSmoothing meanFilterPress;

	public MagCollectionProvider(Context mContext, Handler mhandler) {
		super();
		this.mContext = mContext;
		this.mhandler = mhandler;
		meanFilterPress = new MeanFilterSmoothing();
		meanFilterPress.setTimeConstant(100);
	}

	public static void start() {
		// MagCollectionProvider.mContext = ctx;
		sensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		orientationold = sensorManager
				.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		press = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		init();

		// 室内外检测
		indoorOrOutdoor = new IndoorOrOutdoor(mContext, mhandler);
		indoorOrOutdoor.init();
		indoorOrOutdoor.start();
	}

	public static void init() {
		sensorManager.registerListener(accelerometerListener, accelerometer,
				SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(magListener, mag,
				SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(GyroscopeListener, gyroscope,
				SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(orientationoldListener, orientationold,
				SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(gravityListener, gravity,
				SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(pressListener, press,
				SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(lightListener, light,
				SensorManager.SENSOR_DELAY_FASTEST);

	}

	public static void stop() {
		sensorManager.unregisterListener(accelerometerListener);
		sensorManager.unregisterListener(magListener);
		sensorManager.unregisterListener(GyroscopeListener);
		sensorManager.unregisterListener(orientationoldListener);
		sensorManager.unregisterListener(gravityListener);
		sensorManager.unregisterListener(pressListener);
	}

	private final static SensorEventListener magListener = new SensorEventListener() {

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		public void onSensorChanged(SensorEvent sensorEvent) {
			magneticFieldValues = sensorEvent.values;
			currentMagRecord = Math.sqrt(magneticFieldValues[0]
					* magneticFieldValues[0] + magneticFieldValues[1]
					* magneticFieldValues[1] + magneticFieldValues[2]
					* magneticFieldValues[2]);
			currentMagRecordvector = magneticFieldValues[0] + ","
					+ magneticFieldValues[1] + "," + magneticFieldValues[2];
			calculateOrientation();
		}
	};

	private final static SensorEventListener accelerometerListener = new SensorEventListener() {
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		public void onSensorChanged(SensorEvent sensorEvent) {
			accelerometerValues = sensorEvent.values;
		}
	};
	private final static SensorEventListener GyroscopeListener = new SensorEventListener() {
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		public void onSensorChanged(SensorEvent event) {
			// 根据陀螺仪采样数据计算出此次时间间隔的偏移量后，它将与当前旋转向量相乘。
			if (timestamp != 0) {
				final float dT = (event.timestamp - timestamp) * NS2S;
				// 未规格化的旋转向量坐标值，。
				float axisX = event.values[0];
				float axisY = event.values[1];
				float axisZ = event.values[2];

				// 计算角速度
				float omegaMagnitude = (float) Math.sqrt(axisX * axisX + axisY
						* axisY + axisZ * axisZ);

				// 如果旋转向量偏移值足够大，可以获得坐标值，则规格化旋转向量
				// (也就是说，EPSILON 为计算偏移量的起步值。小于该值的偏移视为误差，不予计算。)
				if (omegaMagnitude > EPSILON) {
					axisX /= omegaMagnitude;
					axisY /= omegaMagnitude;
					axisZ /= omegaMagnitude;
				}

				// 为了得到此次取样间隔的旋转偏移量，需要把围绕坐标轴旋转的角速度与时间间隔合并表示。
				// 在转换为旋转矩阵之前，我们要把围绕坐标轴旋转的角度表示为四元组。
				float thetaOverTwo = omegaMagnitude * dT / 2.0f;
				float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
				float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
				deltaRotationVector[0] = sinThetaOverTwo * axisX;
				deltaRotationVector[1] = sinThetaOverTwo * axisY;
				deltaRotationVector[2] = sinThetaOverTwo * axisZ;
				deltaRotationVector[3] = cosThetaOverTwo;
			}
			timestamp = event.timestamp;
			float[] deltaRotationMatrix = new float[9];
			SensorManager.getRotationMatrixFromVector(deltaRotationMatrix,
					deltaRotationVector);
			// 为了得到旋转后的向量，用户代码应该把我们计算出来的偏移量与当前向量叠加。
			// rotationCurrent = rotationCurrent * deltaRotationMatrix;
		}

	};

	private final static SensorEventListener orientationoldListener = new SensorEventListener() {
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		public void onSensorChanged(SensorEvent event) {
			orientationOldValues = event.values;
		}
	};

	public static float[] getAccelerometerValues() {
		return accelerometerValues;
	}

	public static float[] getOrientationOldValues() {
		return orientationOldValues;
	}

	private final static SensorEventListener gravityListener = new SensorEventListener() {
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		public void onSensorChanged(SensorEvent event) {
			gravityValues = event.values;
		}
	};
	private final static SensorEventListener pressListener = new SensorEventListener() {
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		public void onSensorChanged(SensorEvent event) {
			double press = event.values[0];
			// ln(p2/p1)=-M*g(h2-h1)/(R*T) ,T为绝对温度
			// 令h1=0，即海平面，大气压为一个标准大气压，此时，h2=-R*T/(M*g)*ln(p2/p1)
			// 更新公式，不再使用温度传感器，温度与高度有关
			// 新的公式: P=P0*(1-(n-1)/n * ugH/R T0 )^(n/(n-1)) ,
			// P=1013.25*(1-2.252*10^-5*H)^5.264;
			// 参考文献：大气压强与高度的精密公式 陈竞余
			// double height =
			// -29.274589*(273.15+30)*Math.log(press/1013);

			currentHeight = (1 - Math.pow((press / 1013.25), (1 / 5.264))) / 2.256 * 100000;
			float[] pressArr = { (float) currentHeight };
			pressArr = meanFilterPress.addSamples(pressArr);
			currentHeight = pressArr[0];
			Log.d("currentHeight", "" + currentHeight);
			System.out.println("currentHeight" + currentHeight);

		}
	};
	private final static SensorEventListener lightListener = new SensorEventListener() {
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		public void onSensorChanged(SensorEvent event) {
			currentLight = event.values[0];

			// Log.d("currentLight", "" + currentLight);
		}
	};

	public static float[] getGravityValues() {
		return gravityValues;
	}

	public static float[] getOrientationNewValues() {
		return orientationNewValues;
	}

	private static void calculateOrientation() {
		float[] values = new float[3];
		float[] R = new float[9];
		SensorManager.getRotationMatrix(R, null, accelerometerValues,
				magneticFieldValues);
		SensorManager.getOrientation(R, values);
		currDirection = Math.toDegrees(values[0]);
		orientationNewValues[0] = (float) Math.toDegrees(values[0]);
		orientationNewValues[1] = (float) Math.toDegrees(values[1]);
		orientationNewValues[2] = (float) Math.toDegrees(values[2]);

	}

 
 

	public static double getRealTimeMag() {
		return currentMagRecord;
	}

	public static double getRealTimeHeight() {
		return currentHeight;
	}

	public static double getRealTimeLight() {
		return currentLight;
	}

	public static float[] getmagneticFieldValues() {
		return magneticFieldValues;
	}

	// public static double getcurrentDirection() {
	// return currentDirectionRecord;
	// }
	public static double getrealTimeDirection() {
		return currDirection;
	}

	public static double[] getIODetectResult() {
		return indoorOrOutdoor.getIODetectResult();
	}

	public static String getrealTimeandCurrentDirection() {
		return "瞬时读数" + realTimeMagDirection + " 均值方向" + currentDirectionRecord;
	}

	public static ArrayList<Double> getdirectionList() {
		return magArr2;
	}

 

}
