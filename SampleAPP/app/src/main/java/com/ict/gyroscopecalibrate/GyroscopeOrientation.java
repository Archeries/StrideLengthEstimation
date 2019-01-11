package com.ict.gyroscopecalibrate;

import java.util.Arrays;

import org.apache.commons.math3.complex.Quaternion;

import Jama.Matrix;
import android.content.Context;
import android.hardware.Sensor;
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
 * A class that integrates rotation delta's provided by a gyroscope sensor and
 * outputs an orientation based on Euler angles in units of radians.
 * 
 * @author Kaleb
 *
 */
public class GyroscopeOrientation extends Orientation
{
	// Developer Note: The quaternions rely on the Apache Commons Math
	// Quaternion object. The dependency is already in the project because I use
	// it for statistical analysis else where. The object
	// creation is not idea, but I am not prioritizing performance. If you don't
	// want the dependency or want it to go faster, building your own quaternion
	// as a single array is trivial (and is already done in the gyroscope
	// integrations). Writing the quaternion functions (multiply and add) to use
	// the vectors is not as trivial as writing the vector, but is easy enough.

	private static final String tag = ImuOCfQuaternion.class.getSimpleName();

	// copy the new gyro values into the gyro array
	// convert the raw gyro data into a rotation vector
	private double[] deltaVGyroscope = new double[4];

	private float[] vOrientation = new float[3];
	private float[] qvOrientation = new float[4];

	// rotation matrix from gyro data
	private float[] rmGyroscope = new float[9];

	private Quaternion qGyroscopeDelta;
	private Quaternion qGyroscope;
	
	//ԭʼ��������
	private float[] Gyroscope= new float[3];
	private double[] GyroscopeTheta= new double[4];

 

	public double[] getGyroscopeTheta() {
		return GyroscopeTheta;
	}

	public void setGyroscopeTheta(double[] gyroscopeTheta) {
		GyroscopeTheta = gyroscopeTheta;
	}

	public void setGyroscope(float[] gyroscope) {
		Gyroscope = gyroscope;
	}

	public GyroscopeOrientation(Context context)
	{
		super(context);
	}

	/**
	 * The orientation of the device. Euler angles in units of radians.
	 * values[0]: azimuth, rotation around the Z axis. values[1]: pitch,
	 * rotation around the X axis. values[2]: roll, rotation around the Y axis.
	 */
	public float[] getOrientation()
	{
		if (isOrientationValidAccelMag)
		{
			// Now we get a structure we can pass to get a rotation matrix, and
			// then an orientation vector from Android.
			qvOrientation[0] = (float) qGyroscope.getVectorPart()[0];
			qvOrientation[1] = (float) qGyroscope.getVectorPart()[1];
			qvOrientation[2] = (float) qGyroscope.getVectorPart()[2];
			qvOrientation[3] = (float) qGyroscope.getScalarPart();

			// We need a rotation matrix so we can get the orientation vector...
			// Getting Euler
			// angles from a quaternion is not trivial, so this is the easiest
			// way,
			// but perhaps
			// not the fastest way of doing this.
			SensorManager.getRotationMatrixFromVector(rmGyroscope,
					qvOrientation);
	//		System.out.println("rmGyroscope"+Arrays.toString(rmGyroscope));
			// Get the fused orienatation
			SensorManager.getOrientation(rmGyroscope, vOrientation);
		}

		return rmGyroscope;
	}
	public float[] getMagnetic()
	{
		return vMagnetic;
	}
	public float[] getGyroscope(){
		return Gyroscope;
	}

	protected void calculateOrientationAccelMag()
	{
		super.calculateOrientationAccelMag();

		getRotationVectorFromAccelMag();

		// The acceleration and magnetic sensors are only required for the
		// initial orientation. We can stop listening for updates after we
		// obtain the initial orientation.
//		if (isOrientationValidAccelMag)
//		{
//			sensorManager.unregisterListener(this,
//					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
//			sensorManager.unregisterListener(this,
//					sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
//		}
	}

	/**
	 * Create an angle-axis vector, in this case a unit quaternion, from the
	 * provided Euler angle's (presumably from SensorManager.getOrientation()).
	 * 
	 * Equation from
	 * http://www.euclideanspace.com/maths/geometry/rotations/conversions
	 * /eulerToQuaternion/
	 * 
	 * @param orientation
	 */
	private void getRotationVectorFromAccelMag()
	{
		// Assuming the angles are in radians.

		// getOrientation() values:
		// values[0]: azimuth, rotation around the Z axis.
		// values[1]: pitch, rotation around the X axis.
		// values[2]: roll, rotation around the Y axis.

		// Heading, Azimuth, Yaw
		double c1 = Math.cos(vOrientationAccelMag[0] / 2);
		double s1 = Math.sin(vOrientationAccelMag[0] / 2);

		// Pitch, Attitude
		// The equation assumes the pitch is pointed in the opposite direction
		// of the orientation vector provided by Android, so we invert it.
		double c2 = Math.cos(-vOrientationAccelMag[1] / 2);
		double s2 = Math.sin(-vOrientationAccelMag[1] / 2);

		// Roll, Bank
		double c3 = Math.cos(vOrientationAccelMag[2] / 2);
		double s3 = Math.sin(vOrientationAccelMag[2] / 2);

		double c1c2 = c1 * c2;
		double s1s2 = s1 * s2;

		double w = c1c2 * c3 - s1s2 * s3;
		double x = c1c2 * s3 + s1s2 * c3;
		double y = s1 * c2 * c3 + c1 * s2 * s3;
		double z = c1 * s2 * c3 - s1 * c2 * s3;

		// The quaternion in the equation does not share the same coordinate
		// system as the Android gyroscope quaternion we are using. We reorder
		// it here.

		// Android X (pitch) = Equation Z (pitch)
		// Android Y (roll) = Equation X (roll)
		// Android Z (azimuth) = Equation Y (azimuth)

		qGyroscope = new Quaternion(w, z, x, y);

	}

	/**
	 * Calculates a rotation vector from the gyroscope angular speed values.
	 * 
	 * @param gyroValues
	 * @param deltaRotationVector
	 * @param timeFactor
	 * @see http://developer.android
	 *      .com/reference/android/hardware/SensorEvent.html#values
	 */
	private void getRotationVectorFromGyro()
	{
		// Calculate the angular speed of the sample
		float magnitude = (float) Math.sqrt(Math.pow(vGyroscope[0], 2)
				+ Math.pow(vGyroscope[1], 2) + Math.pow(vGyroscope[2], 2));
		System.arraycopy(vGyroscope, 0, Gyroscope, 0,
				this.vGyroscope.length);
         setGyroscope(Gyroscope);
     	System.out.println("vGyroscope22222222" + Arrays.toString(vGyroscope));
		// Normalize the rotation vector if it's big enough to get the axis
		if (magnitude > EPSILON)
		{
			vGyroscope[0] /= magnitude;
			vGyroscope[1] /= magnitude;
			vGyroscope[2] /= magnitude;
		}

		// Integrate around this axis with the angular speed by the timestep
		// in order to get a delta rotation from this sample over the timestep
		// We will convert this axis-angle representation of the delta rotation
		// into a quaternion before turning it into the rotation matrix.
		float thetaOverTwo = magnitude * dT / 2.0f;
		float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
		float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
		
	
		deltaVGyroscope[0] = sinThetaOverTwo * vGyroscope[0];
		deltaVGyroscope[1] = sinThetaOverTwo * vGyroscope[1];
		deltaVGyroscope[2] = sinThetaOverTwo * vGyroscope[2];
		deltaVGyroscope[3] = cosThetaOverTwo;
		
		System.arraycopy(deltaVGyroscope, 0, GyroscopeTheta, 0,
				this.deltaVGyroscope.length);
		setGyroscopeTheta(GyroscopeTheta);
		
		// Create a new quaternion object base on the latest rotation
		// measurements...
		qGyroscopeDelta = new Quaternion(deltaVGyroscope[3],
				Arrays.copyOfRange(deltaVGyroscope, 0, 3));

		// Since it is a unit quaternion, we can just multiply the old rotation
		// by the new rotation delta to integrate the rotation.
		qGyroscope = qGyroscope.multiply(qGyroscopeDelta);
	}

	@Override
	protected void onGyroscopeChanged()
	{
		// Don't start until accelerometer/magnetometer orientation has
		// been calculated. We need that initial orientation to base our
		// gyroscope rotation off of.
		if (!isOrientationValidAccelMag)
		{
			return;
		}

		if (this.timeStampGyroscopeOld != 0)
		{
			dT = (this.timeStampGyroscope - this.timeStampGyroscopeOld) * NS2S;

			getRotationVectorFromGyro();
		}

		// measurement done, save current time for next interval
		this.timeStampGyroscopeOld = this.timeStampGyroscope;
	}
	@Override
	protected void onMagneticChanged() {
		// TODO Auto-generated method stub
		if (!isOrientationValidAccelMag)
		{
			return;
		}

		if (this.timeStampMagneticOld != 0)
		{
			dT = (this.timeStampMagnetic - this.timeStampMagneticOld) * NS2S;

			 getMagnetic();
		}

		// measurement done, save current time for next interval
		this.timeStampMagneticOld = this.timeStampMagnetic;
	}
	/**
	 * Reinitialize the sensor and filter.
	 */
	public void reset()
	{
		// copy the new gyro values into the gyro array
		// convert the raw gyro data into a rotation vector
		deltaVGyroscope = new double[4];

		vOrientation = new float[3];
		qvOrientation = new float[4];

		// rotation matrix from gyro data
		rmGyroscope = new float[9];

		qGyroscopeDelta = null;
		qGyroscope = null;

		isOrientationValidAccelMag = false;

		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_FASTEST);

		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	/**
	 * The complementary filter coefficient, a floating point value between 0-1,
	 * exclusive of 0, inclusive of 1.
	 * 
	 * @param filterCoefficient
	 */
	public void setFilterCoefficient(float filterCoefficient)
	{

	}

	@Override
	public float[] getMagneticWithoutSmoothing() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float[] getGyroscopeWithoutSmoothing() {
		// TODO Auto-generated method stub
		return null;
	}
	public Matrix getBMatrix() {
		return bMatrix;
	}

	public void setBMatrix(Matrix rMatrix) {
		this.bMatrix = rMatrix;
	}

	@Override
	public Matrix getrMatrix() {
		// TODO Auto-generated method stub
		return null;
	}


}
