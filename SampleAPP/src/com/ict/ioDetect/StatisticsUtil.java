package com.ict.ioDetect;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StatisticsUtil {
	/**
	 * calculate the average of a double array, ignore the initial value like -1
	 * 
	 * @param values
	 * @param ignoreValue
	 * @return ave
	 */
	public static double getAverage(double[] values, double ignoreValue) {
		double ave = 0;
		int count = 0;

		for (int i = 0; i < values.length; i++) {
			if (values[i] != ignoreValue) {
				ave += values[i];
				count++;
			}
		}
		if (count != 0)
			ave /= count;

		return ave;
	}

	public static float getAverage(float[] values, float ignoreValue) {
		float ave = 0;
		int count = 0;

		for (int i = 0; i < values.length; i++) {
			if (values[i] != ignoreValue) {
				ave += values[i];
				count++;
			}
		}
		//System.out.println("count:" + count);
		if (count != 0)
			ave /= count;

		return ave;
	}

	/**
	 * calculate the average of a double array
	 * 
	 * @param values
	 * @return ave
	 */
	public static double getAverage(int[] values) {
		double ave = 0;
		int count = 0;

		for (int i = 0; i < values.length; i++) {
			ave += values[i];
			count++;
		}
		if (count == 0)
			return 0;
		ave /= count;

		return ave;
	}

	/**
	 * calculate the variation of a double array, ignore the initial value like
	 * -1
	 * 
	 * @param values
	 * @param ignoreValue
	 * @return variation
	 */
	public static double getVariation(double[] values, double ignoreValue) {
		double ave = getAverage(values, ignoreValue);
		int count = 0;

		double variation = 0;
		for (int i = 0; i < values.length; i++) {
			if (values[i] != ignoreValue) {
				variation += (values[i] - ave) * (values[i] - ave);
				count++;
			}
		}
		if (count == 0)
			return 0;
		variation /= count;

		return variation;
	}

	/**
	 * calculate the max value of a double array
	 * 
	 * @param array
	 * @param ignoreValue
	 * @return max
	 */
	public static double getMax(double[] array, double ignoreValue) {
		double max = array[0];
		for (int i = 0; i < array.length; i++) {
			if (array[i] > max && array[i] != ignoreValue)
				max = array[i];
		}
		return max;

	}

	/**
	 * calculate the min value of a double array
	 * 
	 * @param array
	 * @param ignoreValue
	 * @return
	 */
	public static double getMin(double[] array, double ignoreValue) {
		double min = array[0];
		for (int i = 0; i < array.length; i++) {
			if (array[i] < min && array[i] != ignoreValue)
				min = array[i];
		}
		return min;

	}

	public static int getMaxCountDetection(int[] array) {
		int maxCountDetection = 3;
		int count = 0;
		Arrays.sort(array);
		int temp = array[0];
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i = 0; i < array.length; i++) {
			if (temp != array[i]) {
				temp = array[i];
				count = 1;
			} else {
				count++;
			}
			map.put(array[i], count);

		}
		map = sortByValue(map);
		Set<Integer> key = map.keySet();
		/*
		 * for (Iterator it = key.iterator(); it.hasNext();) { Integer s =
		 * (Integer) it.next(); System.out.println(s+"出现了"+map.get(s)); }
		 */
		Integer s = (Integer) key.iterator().next();
		if (s != 3 && map.get(s) >= 2)
			maxCountDetection = s;

		return maxCountDetection;
	}

	public static Map sortByValue(Map<Integer, Integer> map) {
		List list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue())
						.compareTo(((Map.Entry) (o1)).getValue());
			}
		});
		Map result = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

}
