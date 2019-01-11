package cn.ac.ict.ictips.locating.subPositioningEngines.LandMarkPositioningEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import cn.ac.ict.ictips.locating.subPositioningEngines.LandMarkPositioningEngine.Entity.FloorInformation;
import cn.ac.ict.ictips.locating.subPositioningEngines.LandMarkPositioningEngine.Entity.PracticeService;

public class LandmarkProvider {
	private Context mContext;
	Pipeline pipe;
	double real_floor = 0.0;
	int currentFloor = 0;// 当前传过来的结果的楼层
	int total = 0, correct = 0;
	int numberSum;
	private SensorManager mSensorManager;
	private MyReceiver receiver = null;

	private String pre_floor_description = "";

	String[] floorNameArray;
	int[] floorNumArray;
	long t1, t2;
	private Intent intent2;
	String landmarkString = "";
	SensorManager sensorManager;

	// 关于配置文件的变量
	ArrayList<double[]> landmarkList = new ArrayList<double[]>();
	ArrayList<Double> directionList = new ArrayList<Double>();
	ArrayList<Double> coordinateXList = new ArrayList<Double>();
	ArrayList<Double> coordinateYList = new ArrayList<Double>();
	ArrayList<Double> valueList = new ArrayList<Double>();
	ArrayList<String> magValueList = new ArrayList<String>();

	// 存最近previousNum个地磁数据的数值
	int previousNum = 100;
	long time = 0;// 判断为电梯和楼梯的时刻
	boolean findIt = false;

	private long lastTurnTime;// 上一次检测到landmark的时间

	public LandmarkProvider(Context ctx, LandMarkCallback callback) {
		mContext = ctx;
		mCallback = callback;
		init();
	}

	@SuppressLint("SimpleDateFormat")
	private void init() {
		mSensorManager = (SensorManager) mContext
				.getSystemService(mContext.SENSOR_SERVICE);

		// String infoPath = DiskTools.getFolderBaseDir()+
		// Config.FILE_PATH_4_LANDMARK_FLOOR;
		// // 在这里读配置文件
		// readFloorInfor(infoPath);
		//
		// String trainPath = DiskTools.getFolderBaseDir()+
		// Config.FILE_PATH_4_LANDMARK_TRAININGDATA;
		// readfile(trainPath);
		InputStream is;
		try {
			is = mContext.getAssets().open("floorInfo.mix");
			// 在这里读配置文件
			readFloorInfor(is);

			is = mContext.getAssets().open("magData/train.txt");
			readfile(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 开启服务
		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.example.floor_identify_2015");
		mContext.registerReceiver(receiver, filter);

		intent2 = new Intent();
		intent2.setClass(mContext, PracticeService.class);
		mContext.startService(intent2);
		// 开启pipeline
		pipe = new Pipeline(mHandler, real_floor, mContext, mSensorManager);
		// 初始楼层设定
		real_floor = 7;
		pipe.setIfManualOpen(true);
		pipe.set_real_floor(real_floor);
		pipe.start();
	}

	public void stop() {
		mContext.stopService(intent2);
		mContext.unregisterReceiver(receiver);
		pipe.stop();
	}

	/**
	 * read trained landmark lists
	 * 
	 * @param filePath
	 */
	public void readfile(InputStream inputStream) {
		try {
			// File file = new File(filePath);
			// FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					inputStream));
			String temp = null;
			while ((temp = br.readLine()) != null) {
				String[] array = temp.split("\\|");
				System.out.println("---" + array[0] + "  " + array[1] + "  "
						+ array[2] + "  " + array[3]);
				directionList.add(Double.parseDouble(array[0]));
				valueList.add(Double.parseDouble(array[1]));
				coordinateXList.add(Double.parseDouble(array[2]));
				coordinateYList.add(Double.parseDouble(array[3]));
				magValueList.add(array[4]);

			}
			for (int i = 0; i < magValueList.size(); i++) {
				String[] landmark = magValueList.get(i).split(",");
				double[] landmarkArray = new double[landmark.length];
				for (int j = 0; j < landmark.length; j++) {
					landmarkArray[j] = Double.parseDouble(landmark[j]);
				}
				landmarkList.add(landmarkArray);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * read floor information
	 * 
	 * @param infoPath
	 */
	private void readFloorInfor(InputStream inputStream) {
		String str = "";
		try {
			InputStream is = inputStream;
			int length = is.available();
			byte[] buffer = new byte[length];
			is.read(buffer);
			str = new String(buffer);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] destArray = str.split("\\|");
		String[] s1 = destArray[0].split(",");
		String[] s2;
		if (destArray.length > 1) {
			s2 = destArray[1].split(",");
		} else {
			s2 = new String[0];
		}
		double[] num1Array = new double[s1.length];// 楼层高度的号码
		int[] num2Array = new int[s2.length];// 中空区域的号码
		int[] num3Array = new int[s1.length];// 楼层的号码
		floorNameArray = new String[s1.length];
		floorNumArray = new int[s1.length];

		for (int i = 0; i < s1.length; i++) {
			String[] d = s1[i].split(":");
			num1Array[i] = Double.parseDouble(d[1]);
			num3Array[i] = Integer.parseInt(d[0]);
			floorNameArray[i] = "Floor " + d[0];
			floorNumArray[i] = Integer.parseInt(d[0]);
		}
		for (int i = 0; i < s2.length; i++) {
			num2Array[i] = Integer.parseInt(s2[i]);
		}
		FloorInformation.setEachfloorHeight(num1Array);
		FloorInformation.setHollowFloor(num2Array);
		FloorInformation.setFloorNumArray(floorNumArray);
		FloorInformation.separateNegativePostive();
	}

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {

		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {
			try {
				JSONObject obj = new JSONObject(msg.obj.toString());
				currentFloor = obj.getInt("press_floor");
				if (pre_floor_description != ""
						&& !pre_floor_description.endsWith("initialization")) {
					int previousFloor = Integer.parseInt(pre_floor_description);
					int state = obj.getInt("state");
					int flag = 1;
					boolean flag2;
					if (time != 0) {
						Date curDate = new Date(System.currentTimeMillis());
						long current_time = curDate.getTime();
						if (current_time - time > 2000) {
							flag2 = true;
							time = 0;
						} else {
							flag2 = false;
						}
					} else {
						flag2 = true;
					}

					if (flag2) {
						if (currentFloor != -100 && currentFloor != -10) {
							if (previousFloor != floorNumArray[currentFloor]) {
								previousFloor = floorNumArray[currentFloor];
								Date curDate2 = new Date(
										System.currentTimeMillis());

								if (state == 1) {
									if (numberSum <= 8) {
										landmarkString = "elevator";

									} else {
										landmarkString = "stairs";
									}
									time = curDate2.getTime();
									flag = 0;
								}
								if (state == 2) {
									if (numberSum > 5) {
										landmarkString = "stairs";
									} else {
										landmarkString = "elevator";
									}
									time = curDate2.getTime();
									flag = 0;
								}
								if (flag != 0) {
									landmarkString = "No judgement";
									time = 0;
								}

							} else if (flag2) {
								landmarkString = "No judgement";
								time = 0;
							}

						} else {
							landmarkString = "No judgement";
							time = 0;
						}
					}
				}
				// landmark find
				// if (landmarkString.equals("stairs") ||
				// landmarkString.equals("elevator")) {
				// // 发送信号
				// Toast.makeText(mContext, "Landmarking Signal: " + "stairs",
				// Toast.LENGTH_SHORT).show();
				// JSONObject json = new JSONObject();
				// json.put("Landmarking", landmarkString);
				// mCallback.onData(json);
				// }
				long currentTime = System.currentTimeMillis();
				if ((currentTime - lastTurnTime) / 1000 > 10
						&& (landmarkString.equals("stairs") || landmarkString
								.equals("elevator"))) {
					lastTurnTime = currentTime;
					// Toast.makeText(mContext, "Landmarking Signal: " +
					// "stairs", Toast.LENGTH_SHORT).show();
					if (landmarkString.equals("stairs")) {

						Toast.makeText(mContext,
								"Landmarking Signal: " + "stairs",
								Toast.LENGTH_SHORT).show();
					}
					if (landmarkString.equals("elevator")) {
						Toast.makeText(mContext,
								"Landmarking Signal: " + "elevator",
								Toast.LENGTH_SHORT).show();

					}
					JSONObject json = new JSONObject();
					json.put("Landmarking", landmarkString);
					mCallback.onData(json);
				}

				if (currentFloor != -100 && currentFloor != -10) {
					int[] floorNumArray = FloorInformation.getFloorNumArray();
					pre_floor_description = "" + floorNumArray[currentFloor];
				} else {
					pre_floor_description = "initialization";
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};

	public class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			numberSum = bundle.getInt("numberSum");
		}
	}

	/**
	 * 采样数据回调
	 */
	private LandMarkCallback mCallback;

	/**
	 * 采集样本数据接口
	 * 
	 * @author ldm
	 * 
	 */
	public interface LandMarkCallback {
		/**
		 * 采到一条样本数据
		 */
		public void onData(JSONObject obj);
	}
}
