package cn.ac.ict.ictips.locating.subPositioningEngines.LandMarkPositioningEngine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;
import cn.ac.ict.ictips.locating.subPositioningEngines.LandMarkPositioningEngine.LandmarkProvider.LandMarkCallback;

import com.amap.location.demo.FireManActivity;
import com.ict.wq.CalculateLocation2;
import com.ict.wq.Stair;
import com.ict.wq.StepDetectionProvider.StepDetectedCallBack;

public class LandmarkCorrectProvider {
	
	private static ArrayList<Stair> StairList = new ArrayList<Stair>();
	private static ArrayList<Stair> ElevatorList = new ArrayList<Stair>();
	private static double StairDistanceThreshold = 20;
	private static double ElevatorDistanceThreshold = 20;
	private Context mContext = null;
	private LandmarkProvider landmarkProvider = null;
	private CalculateLocation2 calculateLocation2=null;
	public LandmarkCorrectProvider(Context ctx,LandmarkCorrectCallBack lCCB) {
		// TODO Auto-generated constructor stub
		this.mContext=ctx;
		landmarkCorrectCallBack=lCCB;
		calculateLocation2=new CalculateLocation2();
		LandMarkCallback onDataCallback = new LandMarkCallback() {

			@Override
			public void onData(JSONObject json) {
				// TODO Auto-generated method stub
				String landmarking;
				try {
					landmarking = json.getString("Landmarking");
					if(landmarking.equals("stairs")) {
						CalculateLocation2.isStair = true;
						Toast.makeText(mContext, "检测到楼梯", Toast.LENGTH_SHORT)
						.show();
					//	stairCorrect(stairY, stairX);
					} else if(landmarking.equals("elevator")){
						CalculateLocation2.isElevator = true;
						Toast.makeText(mContext, "检测到电梯", Toast.LENGTH_SHORT)
						.show();
					//	elevatorCorrect(stairY, stairX);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		};
		landmarkProvider = new LandmarkProvider(mContext,
				onDataCallback);
	}
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
	private LandmarkCorrectCallBack landmarkCorrectCallBack;
	
	public interface LandmarkCorrectCallBack {
		public void onLandmarkCorrected(int stepCount);
	}
}
