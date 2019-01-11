package com.ict.wq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.apache.http.util.LangUtils;

import com.amap.api.maps.model.LatLng;

/**
 * @author wq
 * 
 */
public class StairCorrect2 {
	private static ArrayList<Stair> StairList = new ArrayList<Stair>();
	private static ArrayList<Stair> ElevatorList = new ArrayList<Stair>();
	private static double StairDistanceThreshold = 20;
	private static double ElevatorDistanceThreshold = 20;

//	public StairCorrect2() {
//		super();
//		LatLng EastStair = new LatLng(39.981942, 116.326971);
//		LatLng WestStair = new LatLng(39.981791, 116.326207);
//		StairList.add(EastStair);
//		StairList.add(WestStair);
//	}

	/**
	 * @param fusionlatitude
	 * @param fusionlongitude
	 * @return
	 */
	public static double[] stairCorrect(double stairY, double stairX) {

		double[] StairCorrectResult = new double[2];
		if (StairList.size() == 0) {
	 
			Stair stair=new Stair();
			stair.setStairY(stairY);
			stair.setStairX(stairX);
			StairList.add(stair);
			return null;
		} else {
			Hashtable hashtable = new Hashtable();
			for (int i = 0; i < StairList.size(); i++) {
				Stair stair = StairList.get(i);
                 double  distance=getDistance(stairX, stairY,
                		 stair.getStairX(), stair.getStairY());
				hashtable.put(distance
						, stair);
			}

			Map.Entry[] set = getSortedHashtableByKey(hashtable);

			double miniDistance = (Double) set[0].getKey();
			if (miniDistance < StairDistanceThreshold) {

				StairCorrectResult[0] = ((Stair) set[0].getValue()).getStairX();// 修正之后的经度
				StairCorrectResult[1] = ((Stair) set[0].getValue()).getStairY();// 修正之后的纬度
			} else {
				Stair stair=new Stair();
				stair.setStairX(stairX);
				stair.setStairY(stairY);
				StairList.add(stair);
				StairCorrectResult=null;
			}
		}
		return StairCorrectResult;

	}
	/**
	 * @param fusionlatitude
	 * @param fusionlongitude
	 * @return
	 */
	public static double[] elevatorCorrect(double stairY, double stairX) {
		
		double[] StairCorrectResult = new double[2];
		if (ElevatorList.size() == 0) {
			
			Stair stair=new Stair();
			stair.setStairY(stairY);
			stair.setStairX(stairX);
			ElevatorList.add(stair);
			return null;
		} else {
			Hashtable hashtable = new Hashtable();
			for (int i = 0; i < ElevatorList.size(); i++) {
				Stair stair = ElevatorList.get(i);
				double  distance=getDistance(stairX, stairY,
						stair.getStairX(), stair.getStairY());
				hashtable.put(distance
						, stair);
			}
			
			Map.Entry[] set = getSortedHashtableByKey(hashtable);
			
			double miniDistance = (Double) set[0].getKey();
			if (miniDistance < ElevatorDistanceThreshold) {
				
				StairCorrectResult[0] = ((Stair) set[0].getValue()).getStairX();// 修正之后的经度
				StairCorrectResult[1] = ((Stair) set[0].getValue()).getStairY();// 修正之后的纬度
			} else {
				Stair stair=new Stair();
				stair.setStairX(stairX);
				stair.setStairY(stairY);
				ElevatorList.add(stair);
				StairCorrectResult=null;
			}
		}
		return StairCorrectResult;
		
	}

	public static double getDistance(double long1, double lat1, double long2,
			double lat2) {
		return   Math.sqrt(Math.pow(long1 - long2, 2) + Math.pow(lat1 - lat2, 2));

	}

	/**
	 * 计算地球上任意两点(经纬度)距离
	 * 
	 * @param long1
	 *            第一点经度
	 * @param lat1
	 *            第一点纬度
	 * @param long2
	 *            第二点经度
	 * @param lat2
	 *            第二点纬度
	 * @return 返回距离 单位：米
	 */
	public static double Distance(double long1, double lat1, double long2,
			double lat2) {
		double a, b, R;
		R = 6378137; // 地球半径
		lat1 = lat1 * Math.PI / 180.0;
		lat2 = lat2 * Math.PI / 180.0;
		a = lat1 - lat2;
		b = (long1 - long2) * Math.PI / 180.0;
		double d;
		double sa2, sb2;
		sa2 = Math.sin(a / 2.0);
		sb2 = Math.sin(b / 2.0);
		d = 2
				* R
				* Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
						* Math.cos(lat2) * sb2 * sb2));
		return d;
	}

	/**
	 * 求arraylist最小值
	 * 
	 * @param sampleList
	 * @return
	 */
	public int getArrayListMinID(ArrayList sampleList) {
		int maxId = 0;
		double maxDevation = 0.0;
		try {
			int totalCount = sampleList.size();

			if (totalCount >= 1) {
				double min = Double.parseDouble(sampleList.get(0).toString());
				for (int i = 0; i < totalCount; i++) {
					double temp = Double.parseDouble(sampleList.get(i)
							.toString());
					if (temp <= min) {
						min = temp;
						maxId = i;
					}
				}
				maxDevation = min;
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return maxId;

	}

	/**
	 * 方法名称：getSortedHashtable 参数：Hashtable h 引入被处理的散列表
	 * 描述：将引入的hashtable.entrySet进行排序，并返回
	 */
	public static Map.Entry[] getSortedHashtableByKey(Hashtable h) {

		Set set = h.entrySet();

		Map.Entry[] entries = (Map.Entry[]) set.toArray(new Map.Entry[set
				.size()]);

		Arrays.sort(entries, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				Object key1 = ((Map.Entry) arg0).getKey();
				Object key2 = ((Map.Entry) arg1).getKey();
				return ((Comparable) key1).compareTo(key2);
			}

		});

		return entries;
	}
}
