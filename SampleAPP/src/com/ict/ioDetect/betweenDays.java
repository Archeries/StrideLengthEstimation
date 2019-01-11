package com.ict.ioDetect;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class betweenDays {

	/**
	 * @param args
	 * @throws ParseException
	 */

	/**
	 * ������������֮����������
	 * 
	 * @param smdate
	 *            ��С��ʱ��
	 * @param bdate
	 *            �ϴ��ʱ��
	 * @return �������
	 * @throws ParseException
	 */
	public static int daysBetween(Date smdate, Date bdate)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		smdate = sdf.parse(sdf.format(smdate));
		bdate = sdf.parse(sdf.format(bdate));
		Calendar cal = Calendar.getInstance();
		cal.setTime(smdate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bdate);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * �ַ��������ڸ�ʽ�ļ���
	 */
	public static int daysBetween(String smdate, String bdate)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse(smdate));
		long time1 = cal.getTimeInMillis();
		cal.setTime(sdf.parse(bdate));
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));
	}

	public long betWeenday() throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		betweenDays t16 = new betweenDays();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(new Date().getTime());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
	//	System.out.println("aaa" + dateFormat.format(c.getTime()));

		String D2 = dateFormat.format(c.getTime());


		//System.out.println(t16.daysBetween("2001-01-01 00:00:00", D2));
		
		long betWeenday=t16.daysBetween("2001-01-01 00:00:00", D2);
		
		
		return betWeenday;

	}

}
