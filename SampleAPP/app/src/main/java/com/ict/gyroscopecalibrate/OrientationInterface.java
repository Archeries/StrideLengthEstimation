package com.ict.gyroscopecalibrate;

import Jama.Matrix;

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
 * An interface for linear acceleration filters.
 * 
 * @author Kaleb
 *
 */
public interface OrientationInterface  
{
	/**
	 * Get the orientation of the device. This method can be called *only* after
	 * setAcceleration(), setMagnetic() and getGyroscope() have been called.
	 * 
	 * @return float[] an array containing the linear acceleration of the device
	 *         where values[0]: azimuth, rotation around the Z axis. values[1]:
	 *         pitch, rotation around the X axis. values[2]: roll, rotation
	 *         around the Y axis. with respect to the Android coordinate system.
	 */
	public float[] getOrientation();

	/**
	 * The complementary filter coefficient, a floating point value between 0-1,
	 * exclusive of 0, inclusive of 1.
	 * 
	 * @param filterCoefficient
	 */
	public void setFilterCoefficient(float filterCoefficient);
	
	public float[] getMagnetic();
	public float[] getMagneticWithoutSmoothing();
	public float[] getGyroscope();
	public float[] getGyroscopeWithoutSmoothing();
	public double[] getGyroscopeTheta() ;
	public Matrix getrMatrix();
	public Matrix getBMatrix();
}
