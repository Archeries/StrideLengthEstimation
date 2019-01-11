package com.ict.wq;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.NavigateArrowOptions;
import com.amap.location.demo.R;
import com.wzg.dingwei.DataCenter;

public class CalculateLocation2 {

	private Handler mhandler;


	private double lastX = 0;
	private double lastY = 0;
	private double lastZ = 0;
	private Sample lastSample = null;
	private double lastXInCorrect = 0;
	private double lastYInCorrect = 0;
	private double lastXInOrigin = 0;
	private double lastYInOrigin = 0;
	private AMap aMap;

	public double getStartlatitude() {
		return startlatitude;
	}

	public void setStartlatitude(double startlatitude) {
		this.startlatitude = startlatitude;
	}

	public double getStartlongitude() {
		return startlongitude;
	}

	public void setStartlongitude(double startlongitude) {
		this.startlongitude = startlongitude;
	}

	static double startlatitude = Constant.startlatitude;// 计算所工位
	static double startlongitude = Constant.startlongitude;
	public static double fusionlatitude = startlatitude;
	public static double fusionlongitude = startlongitude;
	public static double fusionlatitudelast = startlatitude;
	public static double fusionlongitudelast = startlongitude;

	static double lastEndY = startlatitude;// 上一次的终点
	static double lastEndX = startlongitude;

	public static void UpdateInitLocation(double newstartlatitude,
			double newstartlongitude, Marker mMarker) {
		startlatitude = newstartlatitude;
		startlongitude = newstartlongitude;
		fusionlatitude = newstartlatitude;
		fusionlongitude = newstartlongitude;
		fusionlatitudelast = newstartlatitude;
		fusionlongitudelast = newstartlongitude;
		// this.mMarker=mMarker;

		lastEndY = newstartlatitude;// 上一次的终点
		lastEndX = newstartlongitude;
	}

	// private SensorDataLogFile sensorDataLogFile;
	// float xInEarth = 0;
	// float yInEarth = 0;
	// 不同方式的当前位置
	double xInEarthrealtimeMagnetismWithoutNormalization = 0;
	double yInEarthrealtimeMagnetismWithoutNormalization = 0;
	double xInEarthrealtimeMagnetism = 0;
	double yInEarthrealtimeMagnetism = 0;
	double xInEarthrealtimeGyroscope = 0;
	double yInEarthrealtimeGyroscope = 0;

	double xInEarthrealtimeFusion = 0;
	double yInEarthrealtimeFusion = 0;

	public double getXForDTW() {
		return XForDTW;
	}

	public void setXForDTW(double xForDTW) {
		XForDTW = xForDTW;
	}

	public double getyForDTW() {
		return yForDTW;
	}

	public void setyForDTW(double yForDTW) {
		this.yForDTW = yForDTW;
	}

	double XForDTW = 0;// 用于跑DTW
	double yForDTW = 0;

	public double getXConsult() {
		return XConsult;
	}

	public void setXConsult(double xConsult) {
		XConsult = xConsult;
	}

	public double getYConsult() {
		return YConsult;
	}

	public void setYConsult(double yConsult) {
		YConsult = yConsult;
	}

	double XConsult = 0;// 用于方向参考
	double YConsult = 0;

	// 不同方式的上一位置
	double xInEarthrealtimeMagnetismWithoutNormalizationlast = 0;
	double yInEarthrealtimeMagnetismWithoutNormalizationlast = 0;
	double xInEarthrealtimeMagnetismlast = 0;
	double yInEarthrealtimeMagnetismlast = 0;
	double xInEarthrealtimeGyroscopelast = 0;
	double yInEarthrealtimeGyroscopelast = 0;
	double xInEarthrealtimeFusionlast = 0;
	double yInEarthrealtimeFusionlast = 0;

	// 不同方式的上一拐弯位置
	double xInEarthrealtimelastturnMagnetismWithoutNormalization = 0;
	double yInEarthrealtimelastturnMagnetismWithoutNormalization = 0;
	double xInEarthrealtimelastturnMagnetism = 0;
	double yInEarthrealtimelastturnMagnetism = 0;
	double xInEarthrealtimelastturnGyroscope = 0;
	double yInEarthrealtimelastturnGyroscope = 0;
	double xInEarthrealtimelastturnFusion = 0;
	double yInEarthrealtimelastturnFusion = 0;

	double xGyroscopeLast = 0;
	double yGyroscopeLast = 0;

	public double getxGyroscopecurr() {
		return xGyroscopecurr;
	}

	public void setxGyroscopecurr(double xGyroscopecurr) {
		this.xGyroscopecurr = xGyroscopecurr;
	}

	public double getyGyroscopecurr() {
		return yGyroscopecurr;
	}

	public void setyGyroscopecurr(double yGyroscopecurr) {
		this.yGyroscopecurr = yGyroscopecurr;
	}

	double xGyroscopecurr = 0;
	double yGyroscopecurr = 0;
	double xGyroscopeDTWcorrect = 0;
	double yGyroscopeDTWcorrect = 0;

	private double lastxIngetTheta = 0;
	private double lastyIngetTheta = 0;
	private double lastxIngetTheta1 = 0;
	private double lastyIngetTheta1 = 0;

	private static double lastthetaIngyroscope = 0;
	private static double thetaIngyroscope = 0;
	private static double lastCorrectTheta = 0;
	private static ArrayList<Double> correctThetaList = new ArrayList<Double>();
	double xcenter = 0;
	double ycenter = 0;
	// 实际距离放大10背
	double longitudefactor = 85000;
	double latitudefactor = 110000;

	public final static int UPDATEDIRECTION = 100005;
	public final static int FILTERDIRECTION = 100006;
	public final static int STAIRCORRENT = 100004;
	public final static int DTWCORRENT = 100007;
	public static boolean isStair = false;
	public static boolean isElevator = false;
	ArrayList<Double> fusionArrayList = new ArrayList<Double>();
	ArrayList<Double> MagnetismDirectionArrayList = new ArrayList<Double>();

	double realtimeFusion = 0;// 实时融合角度
	double FusionFactor = 0.5;

	private String tag = "CalculateLocationThread";
	double moldInEarth = 0;// 当前计步器坐标到上次拐弯位置坐标的距离
	double realtimeFilter = 0;// 拐弯过滤得到的角度
	// private boolean isFirstOfTurn = false;

	public static double GPSlatitude = 39.980931;
	public static double GPSlongitude = 116.327481;
	public static double GPSlatitudelast = 39.980931;
	public static double GPSlongitudelast = 116.327491;
	public static int SatelliteNumber = 0;
	private Context mContext;
	private Marker mMarker;
	private boolean foundMathTrajectory = false;
	private double initgyroscopeOffset = 0;// 陀螺仪角度偏移
	private double gyroscopeOffset = initgyroscopeOffset;// 陀螺仪角度偏移
	double delta = 0;
	double currXlast, currYlast;
	// double x, y;
	int LastGyroscopeListSize = 0;
	double lastGyroscopeAvg = 0;
	double lastfusionAvg = 0;
	double lastfusionAvgNormalization = 0;
	double fusionAvgNormalization = 0;
	double magnetismDirectionAvg = 0;
	double lastMagnetismDirectionAvg = 0;
	public static boolean normalizeIsOpnen = false;

	private double deltaX = 0;
	private double deltaY = 0;
	private double deltaXinit = 0;
	private double deltaYinit = 0;
	private double redTurnX = 0;
	private double redTurnY = 0;
	private boolean redGetDelta = false;

 

	public double getGyroscopeOffset() {
		return gyroscopeOffset;
	}

	public void setGyroscopeOffset(double delt) {
		this.gyroscopeOffset = gyroscopeOffset + delt;
		// this.gyroscopeOffset = delt;
	}

	public boolean gyroscopeInitialized = false;
	private boolean isFirstStep = true;
	private double firstStepDirection = 0;

	public static double getxInCorrect() {
		return xInCorrect;
	}

	public static void setxInCorrect(double xInCo) {
		xInCorrect = xInCo;
	}

	public static double getyInCorrect() {
		return yInCorrect;
	}

	public static void setyInCorrect(double yInCo) {
		 yInCorrect = yInCo;
	}

	private static double xInCorrect = 0;
	private static double yInCorrect = 0;

	private int counter;

	private boolean ismatched;

	private double LastStepLength;

	// private TrajectoryMatchThread trajectoryMatchThread = null;

	public CalculateLocation2() {
		super();
	}

	public CalculateLocation2(Context ctx, Handler mHandler, AMap aMap) {
		super();

		this.mhandler = mHandler;
		this.aMap = aMap;
		this.mContext = ctx;
 
	}



	public void Redo() {
		lastX = lastY = 0;
		currXlast = currYlast = 0;
		xInCorrect = yInCorrect = 0;
		lastXInCorrect = lastYInCorrect = 0;
		lastxIngetTheta = lastyIngetTheta = 0;
		lastxIngetTheta1 = lastyIngetTheta1 = 0;

		setXForDTW(0);
		setyForDTW(0);
		setXConsult(0);
		setYConsult(0);
		deltaX = deltaY = 0;
		deltaXinit = deltaYinit = 0;

		fusionlongitudelast = startlongitude;
		fusionlatitudelast = startlatitude;
		fusionlongitude = startlongitude;
		fusionlatitude = startlatitude;
		// aMap.clear();
	}

	/**
	 * 先拿瞬时方向传感器方向作为初始方向走一段，拐弯之后，把之前一段的方向均值作为初始方向
	 * 
	 * @param MagnetismDirection2
	 * @param sample
	 * @param matchPoint
	 */
	private int stairSampleCount = 0;
	private int elevatorSampleCount = 0;

	public void UpdateLocationAndMap0325(double MagnetismDirection2,
			Sample sample, boolean matchPoint) {
		if (Constant.isDebug) {
			SensorDataLogFileReview.trace(sample.toString() + "\n");
		}
		// 手工指定初始方向
		if (Constant.initDirection != 1000 && !gyroscopeInitialized) {
//			double gyroscopeinitDirection = Math.toDegrees(getInitTheta(
//					sample.getCurrxOrigin(), -sample.getCurryOrigin()));
			double gyroscopeinitDirection = Math.toDegrees(AngleUtils.getTheta2(0,0,
					sample.getCurrxOrigin(), -sample.getCurryOrigin()));
			delta = gyroscopeinitDirection - Constant.initDirection;
			setGyroscopeOffset(delta);
			gyroscopeInitialized = true;
		}
		// if (isFirstStep) {
		//
		// lastSample=SampleCopy(sample);
		//
		// }

		if (!gyroscopeInitialized && isFirstStep) {// 初始方向
//			double gyroscopeinitDirection = Math.toDegrees(getInitTheta(
//					sample.getCurrxOrigin(), -sample.getCurryOrigin()));
			double gyroscopeinitDirection = Math.toDegrees(AngleUtils.getTheta2(0,0,
					sample.getCurrxOrigin(), -sample.getCurryOrigin()));
			delta = gyroscopeinitDirection - MagnetismDirection2;
			setGyroscopeOffset(delta);
			firstStepDirection = MagnetismDirection2;

		}
		// if (sample.getCurrxOrigin()==1.2957597970962524) {
		// System.out.println();
		// }
		if (TrajectoryMatchThread.doOffSet && matchPoint) {
			if (TrajectoryMatchThread.getOffSet() != 10000000) {
				delta = TrajectoryMatchThread.getOffSet();
				Log.d("delta", "" + delta);
				if (Double.isNaN(delta)) {
					System.out.println();
				}
				System.out.println("matchPoint " + delta);
				setGyroscopeOffset(delta);
				TrajectoryMatchThread.doOffSet = false;
				redTurnX = lastX;
				redTurnY = lastY;
				redGetDelta = true;

				lastxIngetTheta = (currXlast
						* Math.cos(gyroscopeOffset * Math.PI / 180) + currYlast
						* Math.sin(gyroscopeOffset * Math.PI / 180));
				lastyIngetTheta = (-currXlast
						* Math.sin(gyroscopeOffset * Math.PI / 180) + currYlast
						* Math.cos(gyroscopeOffset * Math.PI / 180));
				lastyIngetTheta = -lastyIngetTheta;
				deltaX = lastxIngetTheta - redTurnX;
				deltaY = lastyIngetTheta - redTurnY;

				lastxIngetTheta = lastxIngetTheta - deltaX;
				lastyIngetTheta = lastyIngetTheta - deltaY;

			}
		}
		if (Double.isNaN(gyroscopeOffset)) {
			System.out.println();
		}
		double x = (sample.getCurrxOrigin()
				* Math.cos(gyroscopeOffset * Math.PI / 180) + sample
				.getCurryOrigin() * Math.sin(gyroscopeOffset * Math.PI / 180));
		double y = (-sample.getCurrxOrigin()
				* Math.sin(gyroscopeOffset * Math.PI / 180) + sample
				.getCurryOrigin() * Math.cos(gyroscopeOffset * Math.PI / 180));


		y = -y;// y反向
		x = x - deltaX;
		y = y - deltaY;

	//	double thetaIngyroscope11 = getTheta2(x, y);
		double thetaIngyroscope11 = AngleUtils.getTheta2(lastX,lastY,x, y);

		thetaIngyroscope = Math.toDegrees(thetaIngyroscope11);
		if (Double.compare(thetaIngyroscope, Double.NaN) == 0) {
			thetaIngyroscope = lastthetaIngyroscope;
		}
		double stepLength = Math.sqrt(Math.pow((x - lastX), 2)
				+ Math.pow((y - lastY), 2));
		if (stepLength > 2) {
			System.out.println("");
		}
		// double stepLength2 = Math.sqrt(Math.pow((sample.getCurrxOrigin()-
		// lastSample.getCurrxOrigin()), 2)
		// + Math.pow((sample.getCurrxOrigin() - lastSample.getCurryOrigin()),
		// 2));
		double stepLength2 = Math.sqrt(Math.pow(
				(sample.getCurrxOrigin() - lastXInOrigin), 2)
				+ Math.pow((sample.getCurryOrigin() - lastYInOrigin), 2));
		;
		double xInCorrecttemp = lastXInCorrect + stepLength
				* Math.sin(thetaIngyroscope * Math.PI / 180);
		double yInCorrecttemp = lastYInCorrect + stepLength
				* Math.cos(thetaIngyroscope * Math.PI / 180);

	

		// 开始GPS校准
		if (sample.isGPS()) {
			fusionlongitude = GPSlongitude;
			fusionlatitude = GPSlatitude;
			xInCorrect = 0;
			yInCorrect = 0;
			startlongitude = GPSlongitude;
			startlatitude = GPSlatitude;
			Constant.isGPS = false;
		}

		// 结束GPS校准
		if (x == 43.20865762532165) {
			System.out.println();
		}
		if (TrajectoryMatchThread.isHasMatched()) {
			ismatched = true;
			double[] matchedPoint = TrajectoryMatchThread.getEndPoint();
			
			xInCorrect = matchedPoint[0];
			yInCorrect = matchedPoint[1];
			
			TrajectoryMatchThread.setHasMatched(false);
			counter = 0;
			LatLng latLng = new LatLng(
					(startlatitude + (yInCorrect / latitudefactor)),
					(startlongitude + xInCorrect / longitudefactor));
			DecimalFormat df = new DecimalFormat("#.00");
			ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
			giflist.add(BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
			MarkerOptions markerOption1 = new MarkerOptions()
					.anchor(0.5f, 0.5f)
					.position(latLng)
					.title("当前位置")
					.snippet(
							"" + df.format(xInCorrect) + ","
									+ df.format(yInCorrect)).icons(giflist)
					.draggable(true).period(50);
			aMap.addMarker(markerOption1);
			if (Constant.isDebug) {
				SensorDataLogFileReview.trace("DTW校准,lastXInCorrect="
						+ lastXInCorrect + ",lastYInCorrect=" + lastYInCorrect
						+ ",xInCorrect=" + xInCorrect + ",yInCorrect="
						+ yInCorrect + ",x=" + x + ",y=" + y + ",stepLength="
						+ stepLength + ",stepLength2=" + stepLength2 + "\n");
			}
		} else {
			ismatched = false;

			// xInCorrect=lastXInCorrect + stepLength
			// * Math.sin(thetaIngyroscope * Math.PI / 180);
			// yInCorrect= lastYInCorrect + stepLength
			// * Math.cos(thetaIngyroscope * Math.PI / 180);
			xInCorrect = lastXInCorrect + stepLength
					* Math.sin(thetaIngyroscope * Math.PI / 180);
			yInCorrect = lastYInCorrect + stepLength
					* Math.cos(thetaIngyroscope * Math.PI / 180);
			if (Double.isNaN(thetaIngyroscope)) {
				System.out.println();
			}
			if (Constant.isDebug) {
				SensorDataLogFileReview.trace("未校准,lastXInCorrect="
						+ lastXInCorrect + ",lastYInCorrect=" + lastYInCorrect
						+ ",xInCorrect=" + xInCorrect + ",yInCorrect="
						+ yInCorrect + ",x=" + x + ",y=" + y + ",stepLength="
						+ stepLength + ",stepLength2=" + stepLength2 + "\n");
			}
		}

		// 开始楼梯校准
		 isStair=false;//先不校准，目前楼梯检测不准
		if (isStair) {

			if (stairSampleCount == 0) {
				Message message4 = new Message();
				message4.what = STAIRCORRENT;
				message4.obj = "检测到楼梯" + (float) xInCorrect + ","
						+ (float) yInCorrect;
				mhandler.sendMessage(message4);
				// Toast.makeText(mContext, "楼梯校准" + correctResult[0] +
				// correctResult[1], Toast.LENGTH_LONG).show();
				if (Constant.isDebug) {
					SensorDataLogFileReview.trace("检测到楼梯" + (float) xInCorrect
							+ "," + (float) yInCorrect + "\n");
				}
				// Toast.makeText(mContext, "楼梯校准" , Toast.LENGTH_LONG).show();
				double[] correctResult = StairCorrect2.stairCorrect(yInCorrect,
						xInCorrect);
				if (correctResult == null) {
					Message message3 = new Message();
					message3.what = STAIRCORRENT;
					message3.obj = "新楼梯" + (float) xInCorrect + ","
							+ (float) yInCorrect;
					mhandler.sendMessage(message3);
					// Toast.makeText(mContext, "楼梯校准" + correctResult[0] +
					// correctResult[1], Toast.LENGTH_LONG).show();
					if (Constant.isDebug) {
						SensorDataLogFileReview.trace("新楼梯," + lastXInCorrect
								+ "," + lastYInCorrect + "," + xInCorrect + ","
								+ yInCorrect + "," + stepLength + ","
								+ stepLength2 + "\n");
					}
				} else {
					//先不校准，目前楼梯检测不准
					xInCorrect = correctResult[0];
					yInCorrect = correctResult[1];
					fusionlongitude = startlongitude + correctResult[0]
							/ longitudefactor;
					fusionlatitude = startlatitude + correctResult[1]
							/ latitudefactor;
					
		 

					System.out.println("楼梯校准" + correctResult[0] + ","
							+ correctResult[1]);
					Message message3 = new Message();
					message3.what = STAIRCORRENT;
					message3.obj = "楼梯校准" + (float) correctResult[0] + ","
							+ (float) correctResult[1];
					mhandler.sendMessage(message3);
					// Toast.makeText(mContext, "楼梯校准" + correctResult[0] +
					// correctResult[1], Toast.LENGTH_LONG).show();
					if (Constant.isDebug) {
						SensorDataLogFileReview.trace("楼梯校准,lastXInCorrect="
								+ lastXInCorrect + ",lastYInCorrect="
								+ lastYInCorrect + ",xInCorrect=" + xInCorrect
								+ ",yInCorrect=" + yInCorrect + ",x=" + x
								+ ",y=" + y + ",stepLength=" + stepLength
								+ ",stepLength2=" + stepLength2 + "\n");
					}
				}

				LatLng latLng = new LatLng(
						(startlatitude + (yInCorrect / latitudefactor)),
						(startlongitude + xInCorrect / longitudefactor));
				DecimalFormat df = new DecimalFormat("#.00");
				ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
				giflist.add(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
				MarkerOptions markerOption1 = new MarkerOptions()
						.anchor(0.1f, 0.1f)
						.position(latLng)
						.title("楼梯")
						.snippet(
								"" + df.format(xInCorrect) + ","
										+ df.format(yInCorrect)).icons(giflist)
						.draggable(true).period(50);
				aMap.addMarker(markerOption1);

//				LatLng latLng2 = new LatLng(
//						(startlatitude + (y / latitudefactor)),
//						(startlongitude + x / longitudefactor));
//
//				MarkerOptions markerOption2 = new MarkerOptions()
//						.anchor(0.1f, 0.1f).position(latLng2).title("楼梯")
//						.snippet("" + df.format(x) + "," + df.format(y))
//						.icons(giflist).draggable(true).period(50);
//				aMap.addMarker(markerOption2);
			} else {
			}
			stairSampleCount++;
			isStair = false;
			// setStair(false);
		} else {
			stairSampleCount = 0;
		}
		// 结束楼梯校准
		
		// 开始电梯校准
		isElevator=false;//先不校准，目前楼梯检测不准
		if (isElevator) {
			
			if (elevatorSampleCount == 0) {
				Message message4 = new Message();
				message4.what = STAIRCORRENT;
				message4.obj = "检测到电梯" + (float) xInCorrect + ","
						+ (float) yInCorrect;
				mhandler.sendMessage(message4);
				// Toast.makeText(mContext, "楼梯校准" + correctResult[0] +
				// correctResult[1], Toast.LENGTH_LONG).show();
				if (Constant.isDebug) {
					SensorDataLogFileReview.trace("检测到电梯" + (float) xInCorrect
							+ "," + (float) yInCorrect + "\n");
				}
				// Toast.makeText(mContext, "楼梯校准" , Toast.LENGTH_LONG).show();
				double[] correctResult = StairCorrect2.elevatorCorrect(yInCorrect,
						xInCorrect);
				if (correctResult == null) {
					Message message3 = new Message();
					message3.what = STAIRCORRENT;
					message3.obj = "新电梯" + (float) xInCorrect + ","
							+ (float) yInCorrect;
					mhandler.sendMessage(message3);
					// Toast.makeText(mContext, "楼梯校准" + correctResult[0] +
					// correctResult[1], Toast.LENGTH_LONG).show();
					if (Constant.isDebug) {
						SensorDataLogFileReview.trace("新电梯," + lastXInCorrect
								+ "," + lastYInCorrect + "," + xInCorrect + ","
								+ yInCorrect + "," + stepLength + ","
								+ stepLength2 + "\n");
					}
				} else {
					//先不校准，目前楼梯检测不准
					xInCorrect = correctResult[0];
					yInCorrect = correctResult[1];
					fusionlongitude = startlongitude + correctResult[0]
							/ longitudefactor;
					fusionlatitude = startlatitude + correctResult[1]
							/ latitudefactor;
					
					
					
					System.out.println("电梯校准" + correctResult[0] + ","
							+ correctResult[1]);
					Message message3 = new Message();
					message3.what = STAIRCORRENT;
					message3.obj = "电梯校准" + (float) correctResult[0] + ","
							+ (float) correctResult[1];
					mhandler.sendMessage(message3);
					// Toast.makeText(mContext, "楼梯校准" + correctResult[0] +
					// correctResult[1], Toast.LENGTH_LONG).show();
					if (Constant.isDebug) {
						SensorDataLogFileReview.trace("电梯校准,lastXInCorrect="
								+ lastXInCorrect + ",lastYInCorrect="
								+ lastYInCorrect + ",xInCorrect=" + xInCorrect
								+ ",yInCorrect=" + yInCorrect + ",x=" + x
								+ ",y=" + y + ",stepLength=" + stepLength
								+ ",stepLength2=" + stepLength2 + "\n");
					}
				}
				
				LatLng latLng = new LatLng(
						(startlatitude + (yInCorrect / latitudefactor)),
						(startlongitude + xInCorrect / longitudefactor));
				DecimalFormat df = new DecimalFormat("#.00");
				ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
				giflist.add(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
				MarkerOptions markerOption1 = new MarkerOptions()
				.anchor(0.1f, 0.1f)
				.position(latLng)
				.title("电梯")
				.snippet(
						"" + df.format(xInCorrect) + ","
								+ df.format(yInCorrect)).icons(giflist)
								.draggable(true).period(50);
				aMap.addMarker(markerOption1);
				
//				LatLng latLng2 = new LatLng(
//						(startlatitude + (y / latitudefactor)),
//						(startlongitude + x / longitudefactor));
//
//				MarkerOptions markerOption2 = new MarkerOptions()
//						.anchor(0.1f, 0.1f).position(latLng2).title("楼梯")
//						.snippet("" + df.format(x) + "," + df.format(y))
//						.icons(giflist).draggable(true).period(50);
//				aMap.addMarker(markerOption2);
			} else {
			}
			elevatorSampleCount++;
			isElevator = false;
			// setStair(false);
		} else {
			elevatorSampleCount = 0;
		}
		// 结束电梯校准
		
		
		// xInCorrect=lastXInCorrect + stepLength
		// * Math.sin(thetaIngyroscope * Math.PI / 180);
		// yInCorrect= lastYInCorrect + stepLength
		// * Math.cos(thetaIngyroscope * Math.PI / 180);
		// double theta = Math.toDegrees(getTheta1(xInCorrect, yInCorrect));
//		double correctTheta = Math.toDegrees(AngleUtils.getTheta2(
//				lastXInCorrect, lastYInCorrect, xInCorrect, yInCorrect));
		double correctTheta = Math.toDegrees(AngleUtils.getTheta2(
				lastXInCorrect, lastYInCorrect, xInCorrect, yInCorrect));
		if (thetaIngyroscope == correctTheta) {
			Log.d("thetacompare", "true");

		} else {
			Log.d("thetacompare", "false " + thetaIngyroscope + " "
					+ correctTheta);
			counter++;

		}
		if (counter > 1) {
			System.out.println();

		}
 

		if (isFirstStep) {
			lastCorrectTheta = correctTheta;

			isFirstStep = false;
		}
		double DeltaThetaCorrect = AngleUtils.calculateDifference(correctTheta,
				lastCorrectTheta);
		if (!gyroscopeInitialized) {

			if (DeltaThetaCorrect > Constant.LengthThreshold) {
				double MagnetismDirectionAVG = AngleUtils.calculateAngle325(MagnetismDirectionArrayList);
				delta = firstStepDirection - MagnetismDirectionAVG;
				setGyroscopeOffset(delta);
				redTurnX = lastX;
				redTurnY = lastY;
				redGetDelta = true;

				lastxIngetTheta = (currXlast
						* Math.cos(gyroscopeOffset * Math.PI / 180) + currYlast
						* Math.sin(gyroscopeOffset * Math.PI / 180));
				lastyIngetTheta = (-currXlast
						* Math.sin(gyroscopeOffset * Math.PI / 180) + currYlast
						* Math.cos(gyroscopeOffset * Math.PI / 180));
				lastyIngetTheta = -lastyIngetTheta;
				deltaXinit = lastxIngetTheta - redTurnX;
				deltaYinit = lastyIngetTheta - redTurnY;

				lastxIngetTheta = lastxIngetTheta - deltaXinit;
				lastyIngetTheta = lastyIngetTheta - deltaYinit;

				DataAdaptor.redoInit = true;
				DataAdaptor.initDirection = MagnetismDirectionAVG;
				// if (aMap!=null) {
				//
				// aMap.clear();
				// }
				gyroscopeInitialized = true;
			} else {
				correctThetaList.add(correctTheta);
				MagnetismDirectionArrayList.add(MagnetismDirection2);
			}
		}
		double xInCorrect2 = xInCorrect + deltaXinit;
		double yInCorrect2 = yInCorrect + deltaYinit;

		setXForDTW(xInCorrect);
		setyForDTW(yInCorrect);
		setXConsult(x);
		setYConsult(y);
		if(Constant.isDebug){
		SensorDataLogFileReview.trace("综合结果,lastXInCorrect=" + lastXInCorrect
				+ ",lastYInCorrect=" + lastYInCorrect + ",xInCorrect="
				+ xInCorrect + ",yInCorrect=" + yInCorrect + ",x=" + x + ",y="
				+ y + ",stepLength=" + stepLength + ",stepLength2="
				+ stepLength2 + "\n");
		}
		DataCenter.correctX = (float) xInCorrect;
		DataCenter.correcty = (float) yInCorrect;
		DataCenter.correctz = (float) sample.getHeight();

		fusionlongitude = startlongitude + xInCorrect / longitudefactor;
		fusionlatitude = startlatitude + yInCorrect / latitudefactor;
		// 添加当前位置小箭头
		double mm = Double.parseDouble("" + fusionlatitude);
		if (Double.isNaN(mm)) {
			System.out.println();
		}
		LatLng fusionlaLatLng = new LatLng(fusionlatitude, fusionlongitude);
		if (mMarker != null) {
			mMarker.setPosition(fusionlaLatLng);

			mMarker.setRotateAngle((float) -thetaIngyroscope);
		} else {
			MarkerOptions markerOption = new MarkerOptions();
			markerOption.position(fusionlaLatLng);

			markerOption.draggable(true);
			markerOption.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.location_marker));

			// 将Marker设置为贴地显示，可以双指下拉看效果
			markerOption.setFlat(true);
			markerOption.anchor((float) 0.3, (float) 0.3);
			mMarker = aMap.addMarker(markerOption);
			mMarker.setRotateAngle((float) -thetaIngyroscope);
		}

		// 原始陀螺数据地磁DTW方向修正
		// aMap.addNavigateArrow(new NavigateArrowOptions()
		// .add(new LatLng(startlatitude + (lastY / latitudefactor),
		// startlongitude + lastX / longitudefactor),
		// new LatLng((startlatitude + (y / latitudefactor)),
		// (startlongitude + x / longitudefactor)))
		// .width(5).topColor(Color.RED));
		// 原始数据不做归一化
		 aMap.addNavigateArrow(new NavigateArrowOptions()
		 .add(new LatLng(startlatitude + (-currYlast / latitudefactor),
		 startlongitude + currXlast / longitudefactor),
		 new LatLng(
		 (startlatitude + (-sample.getCurryOrigin() / latitudefactor)),
		 (startlongitude + sample.getCurrxOrigin()
		 / longitudefactor))).width(5)
		 .topColor(Color.BLUE));

		// // 画每一步的地磁陀螺角度融合均值

		Log.d("fusionlatitude", fusionlatitudelast + " " + fusionlongitudelast
				+ " " + fusionlatitude + " " + fusionlongitude);
		aMap.addNavigateArrow(new NavigateArrowOptions()
				.add(new LatLng(fusionlatitudelast, fusionlongitudelast),
						new LatLng(fusionlatitude, fusionlongitude)).width(5)
				.topColor(Color.GREEN).sideColor(Color.RED));
		lastX = x;
		lastY = y;
		currXlast = sample.getCurrxOrigin();
		currYlast = sample.getCurryOrigin();
		lastSample = DeepCopy.SampleCopy(sample);
		lastXInCorrect = xInCorrect;
		lastYInCorrect = yInCorrect;
		fusionlatitudelast = fusionlatitude;
		fusionlongitudelast = fusionlongitude;
		lastthetaIngyroscope = thetaIngyroscope;
		LastStepLength = stepLength;
		lastCorrectTheta = correctTheta;
		lastXInOrigin = sample.getCurrxOrigin();
		lastYInOrigin = sample.getCurryOrigin();
	}
}
