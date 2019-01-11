package com.ict.gyroscopecalibrate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
 * Implements a mean filter designed to smooth the data points based on a time
 * constant in units of seconds. The mean filter will average the samples that
 * occur over a period defined by the time constant... the number of samples
 * that are averaged is known as the filter window. The approach allows the
 * filter window to be defined over a period of time, instead of a fixed number
 * of samples. This is important on Android devices that are equipped with
 * different hardware sensors that output samples at different frequencies and
 * also allow the developer to generally specify the output frequency. Defining
 * the filter window in terms of the time constant allows the mean filter to
 * applied to all sensor outputs with the same relative filter window,
 * regardless of sensor frequency.
 * 
 * @author Kaleb
 * @version %I%, %G%
 * 
 */
public class MeanFilterSmoothing
{
	private static final String tag = MeanFilterSmoothing.class.getSimpleName();

	private float timeConstant = 1;
	private float startTime = 0;
	private float timestamp = 0;
	private float hz = 0;

	private int count = 0;
	// The size of the mean filters rolling window.
	private int filterWindow = 10;

	private boolean dataInit;

	private ArrayList<LinkedList<Double>> dataLists;

	/**
	 * Initialize a new MeanFilter object.
	 */
	public MeanFilterSmoothing()
	{
		dataLists = new ArrayList<LinkedList<Double>>();
		dataInit = false;
	}

	public void setTimeConstant(float timeConstant)
	{
		this.timeConstant = timeConstant;
	}

	public void reset()
	{
		startTime = 0;
		timestamp = 0;
		count = 0;
		hz = 0;
	}

	/**
	 * Filter the data.
	 * 
	 * @param iterator
	 *            contains input the data.
	 * @return the filtered output data.
	 */
	public float[] addSamples(float[] acc)
	{
		// Initialize the start time.
		if (startTime == 0)
		{
			startTime = System.nanoTime();
		}

		timestamp = System.nanoTime();

		// Find the sample period (between updates) and convert from
		// nanoseconds to seconds. Note that the sensor delivery rates can
		// individually vary by a relatively large time frame, so we use an
		// averaging technique with the number of sensor updates to
		// determine the delivery rate.
		hz = (count++ / ((timestamp - startTime) / 1000000000.0f));

	//	filterWindow = (int) (hz * timeConstant);
		filterWindow = 40;
		for (int i = 0; i < acc.length; i++)
		{
			// Initialize the data structures for the data set.
			if (!dataInit)
			{
				dataLists.add(new LinkedList<Double>());
			}

			dataLists.get(i).addLast((double) acc[i]);

			if (dataLists.get(i).size() > filterWindow)
			{
				dataLists.get(i).removeFirst();
			}
		}

		dataInit = true;

		float[] means = new float[dataLists.size()];

		for (int i = 0; i < dataLists.size(); i++)
		{
			means[i] =   getMean(dataLists.get(i));
		}

		return means;
	}

	/**
	 * Get the mean of the data set.
	 * 
	 * @param data
	 *            the data set.
	 * @return the mean of the data set.
	 */
	private float getMean(List<Double> data)
	{
		float m = 0;
		float count = 0;

		for (int i = 0; i < data.size(); i++)
		{
			m +=  data.get(i);
			count++;
		}

		if (count != 0)
		{
			m = m / count;
		}

		return m;
	}

}
