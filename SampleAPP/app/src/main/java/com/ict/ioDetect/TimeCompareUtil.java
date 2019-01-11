package com.ict.ioDetect;

public class TimeCompareUtil {
	public static boolean isLaterThan(int currentHour, int currentMinute,
			int hour, int minute) {

		if (currentHour > hour)
			return true;
		else if (currentHour < hour)
			return false;
		else {
			if (currentMinute > minute)
				return true;
			else
				return false;
		}
	}
}
