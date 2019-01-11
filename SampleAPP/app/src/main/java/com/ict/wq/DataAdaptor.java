package com.ict.wq;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.amap.api.maps.AMap;

public class DataAdaptor {
	public DataAdaptor(Context mContext, Handler mhandler, AMap aMap) {
		super();
		this.mContext = mContext;
		this.mhandler = mhandler;
		this.aMap = aMap;
		calculateLocation2 = new CalculateLocation2(mContext, mhandler, aMap);
		trajectoryMatchThread = new TrajectoryMatchThread(mContext, mhandler);

		// meanFilterAcc = new MeanFilterSmoothing();
		// meanFilterAcc.setTimeConstant((float) 1);

	}

	// private MeanFilterSmoothing meanFilterAcc;

	private Context mContext;
	private Handler mhandler;
	private AMap aMap;
 

	private static double lastX = 0;
	private static double lastY = 0;
	private static double lastZ = 0;
	private CalculateLocation2 calculateLocation2;

	public final static int REALTIMERECORD = 100009;
	public final static int DTWCORRENT = 100007;
	private static double lastxIngetTheta = 0;
	private static double lastyIngetTheta = 0;
	private static double lastthetaIngyroscope = 0;
	private static double thetaIngyroscope = 0;
	private static ArrayList<Double> windowsList = new ArrayList<Double>();
	// 转弯之后的前5个地磁数据不要
	static int dropNum = 10;
	static boolean dropFlag = false;
	public static ArrayList<Double> magArr1 = new ArrayList<Double>();
	public static ArrayList<Double> magArr2 = new ArrayList<Double>();
	private static double currentDirectionRecord = 0;// 当前的平均地磁方向
	private static double avgDirection = 0;// 当前的平均地磁方向

	private static ArrayList<Sample> subSampleList = new ArrayList<Sample>();
	private static ArrayList<Sample> subSampleListreverse = new ArrayList<Sample>();
	private static ArrayList<Sample> subSampleListTemp = new ArrayList<Sample>();
	private static ArrayList<Sample> initSampleList = new ArrayList<Sample>();
	private static ArrayList<Sample> subSampleListreverseTemp = new ArrayList<Sample>();

	public static int subSampleListSize = 440;
	public static int increasementSize = 110;

	private TrajectoryMatchThread trajectoryMatchThread = null;

	// 地磁修正
	private double calibratex = 0;
	private double calibratey = 0;
	private double calibratez = 0;
	private boolean isCalibrate = false;
	private int count = 0;
	private int subSampleListSizecount = 0;
	private int lineSampleCount = 0;

	private boolean isremove;
	private boolean matchPoint = false;
	private boolean isInitHeight = false;
	private double initHeight = 0;
	private ArrayList<Double> heightList = new ArrayList<Double>();
	int countofMatch = 0;
	private boolean isFirstStep = true;
	private ArrayList<Double> GyroscopeList = new ArrayList<Double>();
	private double GyroscopeAVG = 0;
	private int stairSampleCount=0;
	public static boolean redoInit = false;
	public static double initDirection = 0;
private ArrayList<Long> mathtime=new ArrayList<Long>();
	public void stop() {
		calculateLocation2 = null;
		trajectoryMatchThread = null;
	}
	DecimalFormat df = new DecimalFormat("00.00");
	public void dataProcessing(Sample sample) {
		double magXOrigin = sample.getMagXOrigin();
		double magYOrigin = sample.getMagYOrigin();
		double magZOrigin = sample.getMagZOrigin();
		double magXFlat = sample.getMagXFlat();
		double magYFlat = sample.getMagYFlat();
		double magZFlat = sample.getMagZFlat();
		double magneticValue = sample.getMagneticValue();
		// double realTimedirection=sample.getOrientationOldX();
		double currx = sample.getCurrxOrigin();
		double curry = sample.getCurryOrigin();
		double currz = sample.getCurrzOrigin();
		boolean isStair = sample.isStair();
		boolean isGPS = sample.isGPS();
		double Accx = sample.getAccX();
		double Accy = sample.getAccY();
		double Accz = sample.getAccZ();
		double yaw = sample.getOrientationOldX();
		double pith = sample.getOrientationOldY();
		double roll = sample.getOrientationOldZ();
		double height = sample.getHeight();
		double light = sample.getLight();
		double ioLight = sample.getIoLight();
		double IndoorConfidence = sample.getIoGpgsv();
		double indoorProbability = sample.getIndoorProbability();
		
		//把检测到楼梯之后五秒的sample楼梯属性改为false,避免多次出现楼梯
		if (isStair) {
			if (stairSampleCount>trajectoryMatchThread.SpectrumSize) {//检测到楼梯之后
				sample.setStair(false);
				
			}
			stairSampleCount++;
		}else {
			stairSampleCount=0;
		}
//		Log.d("stairSampleCount",""+ stairSampleCount);
		if (!calculateLocation2.gyroscopeInitialized) {
			initSampleList.add( sample);
		}
		if (!isInitHeight) {
			heightList.add(height);
			if (heightList.size() > 100) {
				double sum = 0;
				for (int i = 0; i < heightList.size(); i++) {
					sum += heightList.get(i);
				}
				initHeight = sum / heightList.size();

				isInitHeight = true;
				heightList.clear();
			}

			height = 0;
		} else {

			height = height - initHeight;
			SensorDataLogFilePress.trace(height+"\n");
		}
		sample.setHeight(height);
		magArr2.add(yaw);
		if (yaw == 23.72638511657715) {
			System.out.println();
		}
		currentDirectionRecord =  AngleUtils.calculateAngle325(magArr2);
		lineSampleCount++;
		avgDirection = (avgDirection * lineSampleCount + yaw)
				/ (lineSampleCount + 1);

		String indoorOrOutdoor = "";
		if (IndoorConfidence < 0.5) {
			indoorOrOutdoor = "室外";
		} else {
			indoorOrOutdoor = "室内";
		}
		Message message3 = new Message();
		message3.what = REALTIMERECORD;
		message3.obj =  "坐标"
				+ df.format(currx) + "," + df.format(-curry)  + ","
				+  df.format(-currz)  + "\n 方向"+df.format(yaw)+"height" +  df.format(height)  + " ioGpgsv "
				+  df.format(IndoorConfidence)  ;
		mhandler.sendMessage(message3);

		float[] Acc = new float[] { (float) Accx, (float) Accy, (float) Accz };

 

		sample.setCurrxCorrect(calculateLocation2.getXForDTW());
		sample.setCurryCorrect(calculateLocation2.getyForDTW());
		sample.setCurrzCorrect(height);
		sample.setCurrxConsult(calculateLocation2.getXConsult());
		sample.setCurryConsult(calculateLocation2.getYConsult());
 

		subSampleListTemp.add(sample);

		// 反向序列需要改变XY分量的符合，正向不需要。
		Sample sample2 = DeepCopy.SampleCopy(sample);
		sample2.setMagXFlat(-sample.getMagXFlat());
		sample2.setMagYFlat(-sample.getMagYFlat());
		sample2.setMagZFlat(sample.getMagZFlat());
 
		sample2.setCurrxCorrect(calculateLocation2.getXForDTW());
		sample2.setCurryCorrect(calculateLocation2.getyForDTW());
		sample2.setCurrzCorrect(height);
		sample2.setCurrxConsult(calculateLocation2.getXConsult());
		sample2.setCurryConsult(calculateLocation2.getYConsult());
		 

		subSampleListreverseTemp.add(0, sample2);
		count++;
		subSampleListSizecount++;
		if (count == subSampleListSize) {
 
			double avgIndoorConfidence = 0;
			double IndoorConfidenceSum = 0;
			for (int i = 0; i < subSampleListTemp.size(); i++) {
				IndoorConfidenceSum += subSampleListTemp.get(i).getIoGpgsv();
			}
			avgIndoorConfidence = IndoorConfidenceSum
					/ subSampleListTemp.size();
	//		if (avgIndoorConfidence > 0.5 || avgIndoorConfidence == -1) {
				countofMatch++;
				
				long time2 = System.currentTimeMillis();
				if (subSampleListTemp.size()!=subSampleListreverseTemp.size()) {
					Log.d("size", subSampleListTemp.size()+" "+subSampleListreverseTemp.size());
				}else {
					Log.d("size", subSampleListTemp.size()+" ");
					
				}
			ArrayList<Sample> coarsnessDTWResult = trajectoryMatchThread

			.coarsnessDTW(subSampleListTemp, subSampleListreverseTemp,
					countofMatch);
				mathtime.add(System.currentTimeMillis()-time2);
				if (mathtime.size()>50) {
					long sumtime=0;
					for (int i = 0; i < mathtime.size(); i++) {
						sumtime+=mathtime.get(i);
					}
					System.out.println("AvgMathTime"+sumtime/mathtime.size());
				}
	//		}


			if (trajectoryMatchThread.isIncrement) {
				for (int i = 0; i < increasementSize; i++) {
					subSampleListTemp.remove(0);
					subSampleListreverseTemp.remove(subSampleListreverseTemp
							.size() - 1);
				}
				count = subSampleListSize - increasementSize;

				isremove = true;
			} else {

				subSampleListTemp.clear();
				subSampleListreverseTemp.clear();
				subSampleList.clear();
				subSampleListreverse.clear();
				count = 0;
			}
			if (subSampleListSizecount == subSampleListSize) {
				subSampleListSizecount = 0;
				matchPoint = true;
			}
		}

		if (Math.abs(currx - lastX) > 0.1 || Math.abs(curry - lastY) > 0.1
				|| Constant.isGPS) {
	//		System.out.println();
			currentDirectionRecord =  AngleUtils.calculateAngle325(magArr2);
			double tempy = -curry;// y反向
 
			thetaIngyroscope = AngleUtils.getTheta2( lastX, lastY,currx, curry);
			thetaIngyroscope = Math.toDegrees(thetaIngyroscope);
			if (isFirstStep) {
				lastthetaIngyroscope = thetaIngyroscope;
				isFirstStep = false;
				GyroscopeAVG = thetaIngyroscope;
			}
 
			double deltathetaIngyroscope = AngleUtils.calculateDifference(
					thetaIngyroscope, GyroscopeAVG);
			if (deltathetaIngyroscope > 20) {
				GyroscopeList.clear();
				magArr2.clear();
				currentDirectionRecord = yaw;
				avgDirection = yaw;
				lineSampleCount = 0;
			 
			}
			GyroscopeList.add(thetaIngyroscope);
			GyroscopeAVG =  AngleUtils.calculateAngle325(GyroscopeList);
			long time1 = System.currentTimeMillis();
			lastthetaIngyroscope = thetaIngyroscope;

			if (avgDirection > 180) {
				avgDirection = avgDirection - 360;
			}
 
			calculateLocation2.UpdateLocationAndMap0325(currentDirectionRecord,
					sample, matchPoint);
			matchPoint = false;
			long time2 = System.currentTimeMillis();
			Log.d("updateMap", "" + (time2 - time1));
			lastX = currx;
			lastY = curry;

		} else {

		}
		if (redoInit) {
			reDO();
			redoInit = false;
		}
	}

	public void reDO() {
		Sample sample = null;

		trajectoryMatchThread.redo();
		calculateLocation2.Redo();
		trajectoryMatchThread.isFirstSegment = true;

		ArrayList<Sample> subSampleListTemp = new ArrayList<Sample>();
		ArrayList<Sample> subSampleListreverseTemp = new ArrayList<Sample>();
		ArrayList<Sample> subSampleList = new ArrayList<Sample>();
		ArrayList<Sample> subSampleListreverse = new ArrayList<Sample>();
		int count = 0;
		int countofMatch = 0;
		int subSampleListSizecount = 0;
		boolean isremove;
		boolean matchPoint = false;
		double lastX = 0;
		double lastY = 0;
		double lastZ = 0;
		for (int k = 0; k < initSampleList.size(); k++) {

			sample = initSampleList.get(k);

			;
			double currx = sample.getCurrxOrigin();
			double curry = sample.getCurryOrigin();

			sample.setCurrxCorrect(calculateLocation2.getXForDTW());
			sample.setCurryCorrect(calculateLocation2.getyForDTW());
			sample.setCurrxConsult(calculateLocation2.getXConsult());
			sample.setCurryConsult(calculateLocation2.getYConsult());

			subSampleListTemp.add(sample);

			// 反向序列需要改变XY分量的符合，正向不需要。
			Sample sample2 = DeepCopy.SampleCopy(sample);
			sample2.setMagXFlat(-sample.getMagXFlat());
			sample2.setMagYFlat(-sample.getMagYFlat());
			sample2.setMagZFlat(sample.getMagZFlat());
			// sample2.setMagneticValue(magneticValue);
			// sample2.setRealTimedirection(realTimedirection);
			// CalculateLocation2 calculateLocation2=new CalculateLocation2();
			sample2.setCurrxCorrect(calculateLocation2.getXForDTW());
			sample2.setCurryCorrect(calculateLocation2.getyForDTW());
			sample2.setCurrxConsult(calculateLocation2.getXConsult());
			sample2.setCurryConsult(calculateLocation2.getYConsult());

			subSampleListreverseTemp.add(0, sample2);
			count++;
			subSampleListSizecount++;
			if (count == subSampleListSize) {

				double avgIndoorConfidence = 0;
				double IndoorConfidenceSum = 0;
				for (int i = 0; i < subSampleListTemp.size(); i++) {
					IndoorConfidenceSum += subSampleListTemp.get(i)
							.getIoGpgsv();
				}
				avgIndoorConfidence = IndoorConfidenceSum
						/ subSampleListTemp.size();
		//		if (avgIndoorConfidence > 0.5 || avgIndoorConfidence == -1) {
					countofMatch++;
					ArrayList<Sample> coarsnessDTWResult = trajectoryMatchThread

					.coarsnessDTW(subSampleListTemp, subSampleListreverseTemp,
							countofMatch);
		//		}

				if (trajectoryMatchThread.isIncrement) {
					for (int i = 0; i < increasementSize; i++) {
						subSampleListTemp.remove(0);
						subSampleListreverseTemp
								.remove(subSampleListreverseTemp.size() - 1);
					}
					count = subSampleListSize - increasementSize;

					isremove = true;
				} else {

					subSampleListTemp.clear();
					subSampleListreverseTemp.clear();
					subSampleList.clear();
					subSampleListreverse.clear();
					count = 0;
				}
				if (subSampleListSizecount == subSampleListSize) {
					subSampleListSizecount = 0;
					matchPoint = true;
				}
			}

			if (Math.abs(currx - lastX) > 0.1 || Math.abs(curry - lastY) > 0.1
					|| Constant.isGPS) {
				calculateLocation2.UpdateLocationAndMap0325(initDirection,
						sample, matchPoint);
				matchPoint = false;
				lastX = currx;
				lastY = curry;

			} else {

			}
		}
	}

}
