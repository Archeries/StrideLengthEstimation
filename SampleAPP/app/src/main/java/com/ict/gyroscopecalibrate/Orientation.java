package com.ict.gyroscopecalibrate;

import java.util.Arrays;

import Jama.Matrix;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/*
 * Gyroscope Explorer
 * Copyright (C) 2013-2015, Kaleb Kircher - Kircher Engineering, LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * An abstract class that provides an interface for classes that deal with
 * gyroscope integration and filters. Takes care of a lot of the boiler plate
 * code.
 * 
 * @author Kaleb
 * 
 */
public abstract class Orientation implements OrientationInterface,
		SensorEventListener {
	private final static String tag = Orientation.class.getSimpleName();

	protected static final float EPSILON = 0.000000001f;

	// private static final float NS2S = 1.0f / 10000.0f;
	// Nano-second to second conversion
	protected static final float NS2S = 1.0f / 1000000000.0f;

	private boolean calibratedGyroscopeEnabled = true;

	protected boolean meanFilterSmoothingEnabled = false;
	protected boolean isOrientationValidAccelMag = false;

	protected float dT = 0;

	protected float meanFilterTimeConstant = 0.2f;

	// angular speeds from gyro
	protected float[] vGyroscope = new float[3];
	protected float[] vGyroscopeWithoutSmoothing = new float[3];
	// magnetic field vector
	protected float[] vMagnetic = new float[3];
	protected float[] vMagneticWithoutSmoothing = new float[3];
	// accelerometer vector
	protected float[] vAcceleration = new float[3];

	// accelerometer and magnetometer based rotation matrix
	protected float[] rmOrientationAccelMag = new float[9];

	protected float[] vOrientationAccelMag = new float[3];

	protected long timeStampGyroscope = 0;
	protected long timeStampGyroscopeOld = 0;
	protected long timeStampMagnetic = 0;
	protected long timeStampMagneticOld = 0;

	private Context context;

	private MeanFilterSmoothing meanFilterAcceleration;
	private MeanFilterSmoothing meanFilterMagnetic;
	private MeanFilterSmoothing meanFilterGyroscope;

	// We need the SensorManager to register for Sensor Events.
	protected SensorManager sensorManager;


	
	
	
	// 创建常量，把纳秒转换为秒。
		//	private static final float NS2S = 1.0f / 1000000000.0f;
			private final static float[] deltaRotationVector = new float[4];
			private static float timestamp;
			private float[] rotationCurrent= new float[9];;
			private float[] rotationdelta= new float[9];;
			Matrix rMatrix=new Matrix(3, 3);
			// double []rotationCurrentTodouble=flootArrToDoubleArr(rotationCurrent);
			  Matrix rotationCurrentMatrix=new Matrix(3, 3);

			private boolean isfirst=true;
			private float[] firstMagnetic = new float[3];
			private float[] finalMagnetic = new float[3];
			Matrix bMatrix =new Matrix(3,1);
	//	protected static final float EPSILON = 0.000000001f;
	public Orientation(Context context) {
		this.context = context;
		this.sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);

		initFilters();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			// Get a local copy of the raw magnetic values from the device
			// sensor.
			System.arraycopy(event.values, 0, this.vAcceleration, 0,
					this.vGyroscope.length);

			if (meanFilterSmoothingEnabled) {
				this.vAcceleration = meanFilterAcceleration
						.addSamples(this.vAcceleration);
			}

			// We fuse the orientation of the magnetic and acceleration sensor
			// based on acceleration sensor updates. It could be done when the
			// magnetic sensor updates or when they both have updated if you
			// want to spend the resources to make the checks.
			calculateOrientationAccelMag();
		}
		// System.out.println("event.sensor.getType()"+event.sensor.getType());
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			// Get a local copy of the raw magnetic values from the device
			// sensor.
			System.arraycopy(event.values, 0, this.vMagnetic, 0,
					this.vMagnetic.length);

			// System.out.println("vMagnetic"+Arrays.toString(vMagnetic));
			if (meanFilterSmoothingEnabled) {
				this.vMagnetic = meanFilterMagnetic.addSamples(this.vMagnetic);
			}
			timeStampMagnetic = event.timestamp;
			onMagneticChanged();
		}

		if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
			System.arraycopy(event.values, 0, this.vGyroscope, 0,
					this.vGyroscope.length);
			if (meanFilterSmoothingEnabled) {
				this.vGyroscope = meanFilterGyroscope
						.addSamples(this.vGyroscope);
			}

			timeStampGyroscope = event.timestamp;
			System.out.println("vGyroscope" + Arrays.toString(vGyroscope));

			onGyroscopeChanged();
			
			
			
			
			
			
			
			// 根据陀螺仪采样数据计算出此次时间间隔的偏移量后，它将与当前旋转向量相乘。
			  if (timestamp != 0) {
			    final float dT = (event.timestamp - timestamp) * NS2S;
			    // 未规格化的旋转向量坐标值，。
			    float axisX = event.values[0];
			    float axisY = event.values[1];
			    float axisZ = event.values[2];

			    // 计算角速度
			    float omegaMagnitude = (float) Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

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
		//	  float[] deltaRotationMatrix = new float[9];
			  SensorManager.getRotationMatrixFromVector(rotationdelta, deltaRotationVector);
			    // 为了得到旋转后的向量，用户代码应该把我们计算出来的偏移量与当前向量叠加。
			  double []deltaRotationMatrixTodouble=flootArrToDoubleArr(rotationdelta);
			  Matrix deltaMatrix=new Matrix(deltaRotationMatrixTodouble, 3);
			 if (isfirst) {
				 rotationCurrentMatrix=(Matrix) deltaMatrix.clone();
				 isfirst=false;
			}
			  rotationCurrentMatrix = rotationCurrentMatrix.times(deltaMatrix);
			//  rotationCurrentMatrix = (Matrix) deltaMatrix.clone();
			  double[][]rotationCurrentMatrixToarr=rotationCurrentMatrix.getArray();
			   System.out.print("rotationCurrentMatrix");
			   for (int i = 0; i < rotationCurrentMatrixToarr.length; i++) {
				for (int j = 0; j < rotationCurrentMatrixToarr[0].length; j++) {
					System.out.print(""+rotationCurrentMatrixToarr[i][j]+",");
				}
				System.out.println();
			}
			//  rMatrix=(Matrix) deltaMatrix.clone();
			   
			   calculateB();
		}

		if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE_UNCALIBRATED) {
			System.arraycopy(event.values, 0, this.vGyroscope, 0,
					this.vGyroscope.length);

			if (meanFilterSmoothingEnabled) {
				this.vGyroscope = meanFilterGyroscope
						.addSamples(this.vGyroscope);
			}

			timeStampGyroscope = event.timestamp;

			onGyroscopeChanged();
		}

	}

	public void onPause() {
		sensorManager.unregisterListener(this);
	}

	public void onResume() {
		// calibratedGyroscopeEnabled = getPrefCalibratedGyroscopeEnabled();
		// meanFilterSmoothingEnabled = getPrefMeanFilterSmoothingEnabled();
		// meanFilterTimeConstant = getPrefMeanFilterSmoothingTimeConstant();
		calibratedGyroscopeEnabled = true;
		meanFilterSmoothingEnabled = true;
		meanFilterTimeConstant = (float) 0.5;
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_FASTEST);

		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_FASTEST);

		if (calibratedGyroscopeEnabled) {
			sensorManager.registerListener(this,
					sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
					SensorManager.SENSOR_DELAY_FASTEST);
		} else {
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
				sensorManager.registerListener(this, sensorManager
						.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED),
						SensorManager.SENSOR_DELAY_FASTEST);
			}
		}
	}

	protected void calculateOrientationAccelMag() {
		// To get the orientation vector from the acceleration and magnetic
		// sensors, we let Android do the heavy lifting. This call will
		// automatically compensate for the tilt of the compass and fail if the
		// magnitude of the acceleration is not close to 9.82m/sec^2. You could
		// perform these steps yourself, but in my opinion, this is the best way
		// to do it.
		if (SensorManager.getRotationMatrix(rmOrientationAccelMag, null,
				vAcceleration, vMagnetic)) {
			SensorManager.getOrientation(rmOrientationAccelMag,
					vOrientationAccelMag);

			isOrientationValidAccelMag = true;
		}
	}

	/**
	 * Reinitialize the sensor and filter.
	 */
	public abstract void reset();

	protected abstract void onGyroscopeChanged();

	protected abstract void onMagneticChanged();

	// private boolean getPrefCalibratedGyroscopeEnabled()
	// {
	// SharedPreferences prefs = PreferenceManager
	// .getDefaultSharedPreferences(context);
	//
	// return prefs.getBoolean(
	// ConfigActivity.CALIBRATED_GYROSCOPE_ENABLED_KEY, true);
	// }
	//
	// private boolean getPrefMeanFilterSmoothingEnabled()
	// {
	// SharedPreferences prefs = PreferenceManager
	// .getDefaultSharedPreferences(context);
	//
	// return prefs.getBoolean(
	// ConfigActivity.MEAN_FILTER_SMOOTHING_ENABLED_KEY, false);
	// }
	//
	// private float getPrefMeanFilterSmoothingTimeConstant()
	// {
	// SharedPreferences prefs = PreferenceManager
	// .getDefaultSharedPreferences(context);
	//
	// return Float.valueOf(prefs.getString(
	// ConfigActivity.MEAN_FILTER_SMOOTHING_TIME_CONSTANT_KEY, "0.5"));
	// }

	/**
	 * Initialize the mean filters.
	 */
	private void initFilters() {
		meanFilterAcceleration = new MeanFilterSmoothing();
		meanFilterAcceleration.setTimeConstant(meanFilterTimeConstant);

		meanFilterMagnetic = new MeanFilterSmoothing();
		meanFilterMagnetic.setTimeConstant(meanFilterTimeConstant);

		meanFilterGyroscope = new MeanFilterSmoothing();
		meanFilterGyroscope.setTimeConstant(meanFilterTimeConstant);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
	

	public double[] flootArrToDoubleArr(float[] flootarr) {
		double[] doubleArr = new double[flootarr.length];
		for (int i = 0; i < flootarr.length; i++) {
			doubleArr[i] = flootarr[i];
		}
		return doubleArr;

	}
	
	
	public Matrix calculateB( ) {
		Matrix firstMagneticmMatrix = new Matrix(
				flootArrToDoubleArr(firstMagnetic), 3);
		Matrix finaltMagneticmMatrix = new Matrix(
				flootArrToDoubleArr(vMagnetic), 3);
 
		MRMpair mpair = new MRMpair(firstMagneticmMatrix,
				finaltMagneticmMatrix, rotationCurrentMatrix);
		// MRMpairList.add(mpair);

		System.arraycopy(finalMagnetic, 0, firstMagnetic, 0,
				finalMagnetic.length);

		  bMatrix = Calibrate.getbMatrix(mpair);
		if (bMatrix != null) {
			double[][] b = bMatrix.getArray();
			System.out.println("b matrix " + b[0][0] + " "
					+ b[1][0] + " " + b[2][0]);
//			bmatrix = "b matrix " + (float) b[0][0] + " "
//					+ (float) b[1][0] + " " + (float) b[2][0];
//		 bCenter=new Point((float) b[0][0], (float) b[1][0],(float) b[2][0]);

		}
		return bMatrix;

	}
	
}
