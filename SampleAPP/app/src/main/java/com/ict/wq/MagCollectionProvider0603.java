package com.ict.wq;

import java.util.ArrayList;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;

import com.ict.gyroscopecalibrate.MeanFilterSmoothing;
import com.ict.ioDetect.IndoorOrOutdoor;

public class MagCollectionProvider0603 implements SensorEventListener {
	private static Context mContext;
	private static Handler mhandler;
	private static SensorManager sensorManager;
 
	private static float[] accelerometerValues = new float[3];
	private static float[] magneticFieldValues = new float[3];
	private static float[] magneticFieldValuesInEarth = new float[4];
	private static float[] gyroscopeValues = new float[3];
	private static float[] orientationOldValues = new float[3];
	private static float[] orientationNewValues = new float[3];
	private static float[] gravityValues = new float[3];
	private static double currentHeight = 0;
	private static double currentLight = 0;
	private static double currentMagRecord = 0;
	private static double currentDirectionRecord = 0;// 当前的平均地磁方向
	private static double realTimeMagDirection = 0;// 当前地磁的瞬时读数
	private static float[] R = new float[9];
	// 转弯之后的前5个地磁数据不要
	static int dropNum = 10;
	static boolean dropFlag = false;
	public static ArrayList<Double> magArr1 = new ArrayList<Double>();
	static ArrayList<Double> magArr2 = new ArrayList<Double>();

 
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

	public MagCollectionProvider0603(Context mContext, Handler mhandler) {
		super();
		this.mContext = mContext;
		this.mhandler = mhandler;
		sensorManager = (SensorManager) mContext
				.getSystemService(mContext.SENSOR_SERVICE);
		meanFilterPress = new MeanFilterSmoothing();
		meanFilterPress.setTimeConstant(100);
	}

	public void start() {
		init();

		// 室内外检测
		indoorOrOutdoor = new IndoorOrOutdoor(mContext, mhandler);
		indoorOrOutdoor.init();
		indoorOrOutdoor.start();
	}

	public void init() {
		int rateUs = SensorManager.SENSOR_DELAY_FASTEST;
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				rateUs);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				rateUs);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), rateUs);
		sensorManager
				.registerListener(
						this,
						sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
						rateUs);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), rateUs);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE), rateUs);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), rateUs);

	}

	public void stop() {
		sensorManager.unregisterListener(this);

	}

	public static float[] getAccelerometerValues() {
		return accelerometerValues;
	}

	public static float[] getOrientationOldValues() {
		return orientationOldValues;
	}

	public static float[] getGravityValues() {
		return gravityValues;
	}

	public static float[] getOrientationNewValues() {
		return orientationNewValues;
	}
	public static float[] getGroValues() {
		return gyroscopeValues;
	}

	private static void calculateOrientation() {
		float[] values = new float[3];
	
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
	public static float[] getmagneticFieldValuesInEarth() {
		return magneticFieldValuesInEarth;
	}
	public   float[] getmagneticFieldValuesInEarth2() {
		return fixedMagValues;
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
	   /** 存储旋转矩阵的值 */
    private float[] r = new float[9];
    /** 存储大地坐标方向的加速度值 */
    private float[] fixedAccValues = new float[3];
    /** 存储地磁指纹的Y方向值和Z方向值 */
    private float[] fixedMagValues = new float[3];
    /** 用于存储加速度传感器的值 */
    private float[] aValues = new float[3];
    /** 用于存储地磁传感器的值 */
    private float[] mValues = new float[3];
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			accelerometerValues = event.values;
			aValues=event.values;
		}
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			magneticFieldValues = event.values;
			mValues=event.values;
			currentMagRecord = Math.sqrt(magneticFieldValues[0]
					* magneticFieldValues[0] + magneticFieldValues[1]
					* magneticFieldValues[1] + magneticFieldValues[2]
					* magneticFieldValues[2]);
	//		calculateOrientation();
			
			float[] deviceMAGNETIC_FIELD = new float[4];
			deviceMAGNETIC_FIELD[0] = event.values[0];
			deviceMAGNETIC_FIELD[1] = event.values[1];
			deviceMAGNETIC_FIELD[2] = event.values[2];
			deviceMAGNETIC_FIELD[3] = 0;
			float[] R = new float[16], I = new float[16], earthAcc = new float[4];

			SensorManager.getRotationMatrix(R, I, accelerometerValues, magneticFieldValues);
			android.opengl.Matrix.multiplyMV(magneticFieldValuesInEarth, 0, R, 0,
					deviceMAGNETIC_FIELD, 0);
			
			   if (SensorManager.getRotationMatrix(r, null, aValues, mValues)) {
		            fixedAccValues[0] = r[0] * aValues[0] + r[1] * aValues[1] + r[2] * aValues[2];
		            fixedAccValues[1] = r[3] * aValues[0] + r[4] * aValues[1] + r[5] * aValues[2];
		            fixedAccValues[2] = r[6] * aValues[0] + r[7] * aValues[1] + r[8] * aValues[2];
		            fixedAccValues[0] = r[0] * aValues[0] + r[1] * aValues[1] + r[2] * aValues[2];
		            fixedMagValues[1] = r[3] * mValues[0] + r[4] * mValues[1] + r[5] * mValues[2];
		            fixedMagValues[2] = r[6] * mValues[0] + r[7] * mValues[1] + r[8] * mValues[2];
		        }
		}
		if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
			gyroscopeValues=event.values.clone();
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

		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			orientationOldValues = event.values;
		}
		if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
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
//			Log.d("currentHeight", "" + currentHeight);
//			System.out.println("currentHeight" + currentHeight);
		}
		if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
			currentLight = event.values[0];
		}
		if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
			gravityValues = event.values.clone();
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

}
