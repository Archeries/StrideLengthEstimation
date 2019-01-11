package com.ict.wq;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ict.fastDTW.dtw.TimeWarpInfo;
import com.ict.fastDTW.timeseries.TimeSeriesPoint;
import com.ict.fastDTW.util.DistanceFunction;
import com.ict.fastDTW.util.DistanceFunctionFactory;

/*
 * 先按长度筛选，再按方向筛选，只取同向或者相反方向的轨迹，最后通过DTW算出最相似的轨迹，如果足够相似，这用该轨迹的终点坐标校准当前拐点的坐标
 */
public class TrajectoryMatchThread  {

	private Context mContext;
	private Handler mhandler;

	public TrajectoryMatchThread(Context mContext, Handler mhandler) {
		super();
		this.mContext = mContext;
		this.mhandler = mhandler;
	}

//	private double Maglengthoffset = 0.6;// 地磁序列长度的百分之二十
//	private double Euclideanlengthoffset = 0.1;// 欧式距离长度的百分之二十
//	private double directionoffset = 45;// 东西南北方向
//	private double distanceThresholdRatioToSize = 3.57;// 轨迹相似度阈值
//	private double distanceOfStartThreshold = 6;// 起点距离阈值（起点距离平方++长度平方差）

	private static ArrayList<Sample> coarseSampleList = new ArrayList<Sample>();
	private static ArrayList<Sample> coarseSampleListReverse = new ArrayList<Sample>();
	private static ArrayList<Sample> fineSampleList = new ArrayList<Sample>();
//	private static ArrayList<ArrayList<Sample>> subSampleListDB = new ArrayList<ArrayList<Sample>>();
	public final static int DTWCORRENT = 100007;

	private static double[] EndPoint = null;

	public static double[] getEndPoint() {
		return EndPoint;
	}

	public void setEndPoint(double[] endPoint) {
		EndPoint = endPoint;
	}

	private static boolean hasMatched = false;
	public static boolean refresh = false;

	public static boolean isHasMatched() {
		return hasMatched;
	}

	public static void setHasMatched(boolean hasMatchednew) {
		hasMatched = hasMatchednew;
	}
//
//	private boolean isReverse = false;
//
//	private static boolean issuitable = false;// 起点相差不大于2m,长度的差不答应轨迹长度的百分之十
//	private static double deltaTheta = 30;
//	private static double deltaLengthRatioToSize = 0.1;
//	private static double miniTotalLength = 10000;
	private static double lastTrajectoryCoarseEndXOrigin = 0;
	private static double lastTrajectoryCoarseEndYOrigin = 0;
	private static double lastTrajectoryCoarseEndXCorrect = 0;
	private static double lastTrajectoryCoarseEndYCorrect = 0;
	public static double lastTrajectoryFineEndX = 0;
	public static double lastTrajectoryFineEndY = 0;
	private static ArrayList<Sample> resultArrayList = new ArrayList<Sample>();

	public static ArrayList<Sample> getResultArrayList() {
		return resultArrayList;
	}

	static double lastx = 0;
	static double lasty = 0;

	public static void setResultArrayList(ArrayList<Sample> resultArrayList) {
		TrajectoryMatchThread.resultArrayList = resultArrayList;
	}

	public ArrayList<Sample> getSubSampleList() {
		return subSampleList;
	}

	public void setSubSampleList(ArrayList<Sample> subSampleList) {
		this.subSampleList = subSampleList;
	}

	public ArrayList<Sample> getSubSampleListReverse() {
		return subSampleListReverse;
	}

	public void setSubSampleListReverse(ArrayList<Sample> subSampleListReverse) {
		this.subSampleListReverse = subSampleListReverse;
	}

	private ArrayList<Sample> subSampleList = new ArrayList<Sample>();
	private ArrayList<Sample> subSampleListReverse = new ArrayList<Sample>();
	private static long dTWtime = 0;
	public static boolean doOffSet = false;
	private static double offSet = 10000000;

	public static double getOffSet() {
		return offSet;
	}

	public static void setOffSet(double offSet) {
		TrajectoryMatchThread.offSet = offSet;
	}

	private boolean addAll = true;
	public static boolean isIncrement = true;
	public static boolean isFirstSegment = true;
	private static boolean isDTWWithWeight = true;
	public static int SpectrumSize = 11;
	private static int matchPointCount = 0;

//	@Override
//	public void run() {
//		ArrayList<Sample> subSampleList = getSubSampleList();
//		ArrayList<Sample> subSampleListReverse = getSubSampleList();
//		if (subSampleList != null && subSampleList.size() > 0
//				&& subSampleListReverse != null
//				&& subSampleListReverse.size() > 0) {
//			long start = System.currentTimeMillis();
//			// getEndPointByDTWSubsequenceTopFive(subSampleList);
//			coarsnessDTW(subSampleList, subSampleListReverse, 100000);
//			System.out.println("wq调用 getEndPointByDTWSubsequence"
//					+ (System.currentTimeMillis() - start));
//		}
//	}

	public void redo() {
		coarseSampleList.clear();
		coarseSampleListReverse.clear();
		fineSampleList.clear();
	}

	public ArrayList<Sample> coarsnessDTW(ArrayList<Sample> fineSubSampleList,
			ArrayList<Sample> fineSubSampleListReverse, int countofMatch) {
		ArrayList<Sample> coarseMatchedSampleList = new ArrayList<Sample>();
		ArrayList<Sample> fineMatchedSampleList = new ArrayList<Sample>();
		double distanceThreshold = 0;
		if (isIncrement) {
			// distanceThreshold=50000/SpectrumSize;
			distanceThreshold = 8000;
		} else {
			distanceThreshold = 8000;
		}
		distanceThreshold = Constant.dtwThreshold;
		double distanceOfStartPointThreshold = Constant.StartPointThreshold;// 可能会阻塞一小会儿
		double DeltaLengthInRatioThreshold = Constant.LengthThreshold;// 快慢百分之15
		double[] EndPoint = null;

		boolean isForwordMatched = false;
		boolean isReverseMatched = false;

		double distance = 1000000000;
		com.ict.fastDTW.dtw.TimeWarpInfo timeWarpInfoReverse = null;
		com.ict.fastDTW.dtw.TimeWarpInfo timeWarpInfo = null;
		SimilarityFactor similarityFactor = null;
		SimilarityFactor similarityFactorReverse = null;
		long time = System.currentTimeMillis();
		ArrayList<ArrayList<Sample>> coarse = getCoarsnessDTWData(
				fineSubSampleList, fineSubSampleListReverse);
		ArrayList<Sample> coarseSubSampleList = coarse.get(0);

		// Direction.getMatchedDirection(coarseSubSampleList);
		ArrayList<Sample> coarseSubSampleListReverse = coarse.get(1);
		Log.d("getCoarsnessDTWData", "" + (System.currentTimeMillis() - time));
		if (coarseSampleList.size() > 0) {
			if (Constant.isDebug) {
				SensorDataLogFileReview.trace("开始反向匹配" + "\n");
				SensorDataLogFileReview.trace("sampleList.size"
						+ coarseSampleList.size() + " "
						+ coarseSubSampleListReverse.size() + "\n");
			}
			final DistanceFunction distFn = DistanceFunctionFactory
					.getDistFnByName("EuclideanDistance");

			final com.ict.fastDTW.timeseries.TimeSeries tsI = new com.ict.fastDTW.timeseries.TimeSeries(
					gettsArray(coarseSampleList), getlabels(),
					gettimeReadings(coarseSampleList));
			final com.ict.fastDTW.timeseries.TimeSeries tsIReverse = new com.ict.fastDTW.timeseries.TimeSeries(
					gettsArray(coarseSampleList), getlabels(),
					gettimeReadings(coarseSampleList));
			final com.ict.fastDTW.timeseries.TimeSeries tsJReverse = new com.ict.fastDTW.timeseries.TimeSeries(
					gettsArray(coarseSubSampleListReverse), getlabels(),
					gettimeReadings(coarseSubSampleListReverse));
			final com.ict.fastDTW.timeseries.TimeSeries tsJ = new com.ict.fastDTW.timeseries.TimeSeries(
					gettsArray(coarseSubSampleList), getlabels(),
					gettimeReadings(coarseSubSampleList));
			long time2 = System.currentTimeMillis();
			if (isDTWWithWeight) {
				timeWarpInfoReverse = com.ict.fastDTW.dtw.DTW
						.getWarpInfoBetweenWithWeight(tsJReverse, tsIReverse,
								distFn);
				timeWarpInfo = com.ict.fastDTW.dtw.DTW
						.getWarpInfoBetweenWithWeight(tsJ, tsI, distFn);
			} else {
				timeWarpInfoReverse = com.ict.fastDTW.dtw.DTW
						.getWarpInfoBetween(tsJReverse, tsIReverse, distFn);
				timeWarpInfo = com.ict.fastDTW.dtw.DTW.getWarpInfoBetween(tsJ,
						tsI, distFn);
			}

			long time3 = System.currentTimeMillis();
			dTWtime = time3 - time2;
			Log.d("DTWtime", "" + (time3 - time2));
			similarityFactorReverse = getSimilarityFactor(timeWarpInfoReverse,
					coarseSubSampleListReverse, coarseSampleList);
			similarityFactor = getSimilarityFactor(timeWarpInfo,
					coarseSubSampleList, coarseSampleList);
			long time4 = System.currentTimeMillis();
			Log.d("SimilarityFactor", "" + (time4 - time3));
			if (coarseSubSampleList.get(0).getCurrxCorrect() == 10.628949165344238) {
				System.out.println();
			}

			double originDistanceOfStartPointForword = Math.sqrt(Math.pow(
					lastTrajectoryCoarseEndXOrigin
							- coarseSampleList.get(
									timeWarpInfo.getPath().minJ())
									.getCurrxOrigin(), 2)
					+ Math.pow(
							lastTrajectoryCoarseEndYOrigin
									- coarseSampleList.get(
											timeWarpInfo.getPath().minJ())
											.getCurryOrigin(), 2));
			double originDistanceOfStartPointReverse = Math.sqrt(Math.pow(
					lastTrajectoryCoarseEndXOrigin
							- coarseSampleList.get(
									timeWarpInfoReverse.getPath().maxJ())
									.getCurrxOrigin(), 2)
					+ Math.pow(
							lastTrajectoryCoarseEndYOrigin
									- coarseSampleList.get(
											timeWarpInfoReverse.getPath()
													.maxJ()).getCurryOrigin(),
							2));
			double correctDistanceOfStartPointForword = Math.sqrt(Math.pow(
					lastTrajectoryCoarseEndXCorrect
							- coarseSampleList.get(
									timeWarpInfo.getPath().minJ())
									.getCurrxCorrect(), 2)
					+ Math.pow(
							lastTrajectoryCoarseEndYCorrect
									- coarseSampleList.get(
											timeWarpInfo.getPath().minJ())
											.getCurryCorrect(), 2));
			double correctDistanceOfStartPointReverse = Math.sqrt(Math.pow(
					lastTrajectoryCoarseEndXCorrect
							- coarseSampleList.get(
									timeWarpInfoReverse.getPath().maxJ())
									.getCurrxCorrect(), 2)
					+ Math.pow(
							lastTrajectoryCoarseEndYCorrect
									- coarseSampleList.get(
											timeWarpInfoReverse.getPath()
													.maxJ()).getCurryCorrect(),
							2));
			if (Constant.isDebug) {

				SensorDataLogFileDTWresuilt.trace("DBSize,"
						+ coarseSampleList.size() + ",testSize,"
						+ coarseSubSampleList.size() + "\n");

				SensorDataLogFileDTWresuilt
						.trace("reverse,lastTrajectoryCoarseEndXCorrect,"
								+ lastTrajectoryCoarseEndXCorrect
								+ ",coarseSampleList.get(timeWarpInfo.getPath().maxJ()).getCurrxOrigin(),"
								+ coarseSampleList.get(
										timeWarpInfoReverse.getPath().maxJ())
										.getCurrxCorrect()
								+ ",lastTrajectoryCoarseEndYCorrect,"
								+ lastTrajectoryCoarseEndYCorrect
								+ ",coarseSampleList.get(timeWarpInfo.getPath().maxJ()).getCurryOrigin(),"
								+ coarseSampleList.get(
										timeWarpInfoReverse.getPath().maxJ())
										.getCurryCorrect() + "\n");
				SensorDataLogFileDTWresuilt
						.trace("forword,lastTrajectoryCoarseEndXCorrect,"
								+ lastTrajectoryCoarseEndXCorrect
								+ ",coarseSampleList.get(timeWarpInfo.getPath().minJ()).getCurrxCorrect(),"
								+ coarseSampleList.get(
										timeWarpInfo.getPath().minJ())
										.getCurrxCorrect()
								+ ",lastTrajectoryCoarseEndYCorrect,"
								+ lastTrajectoryCoarseEndYCorrect
								+ ",coarseSampleList.get(timeWarpInfo.getPath().minJ()).getCurryCorrect(),"
								+ coarseSampleList.get(
										timeWarpInfo.getPath().minJ())
										.getCurryCorrect() + "\n");
			}
			similarityFactor
					.setOriginDistanceOfStartPoint(originDistanceOfStartPointForword);
			similarityFactorReverse
					.setOriginDistanceOfStartPoint(originDistanceOfStartPointReverse);
			similarityFactor
					.setCorrectDistanceOfStartPoint((float) correctDistanceOfStartPointForword);
			similarityFactorReverse
					.setCorrectDistanceOfStartPoint((float) correctDistanceOfStartPointReverse);
			Log.d("similarityFactor", similarityFactor.toString());
			Log.d("similarityFactorReverse", similarityFactorReverse.toString());
 
			if (distanceThreshold > similarityFactorReverse.getDTWDistance()
					&& distanceThreshold < similarityFactor.getDTWDistance()) {
				if (similarityFactorReverse.getCorrectDistanceOfStartPoint() < distanceOfStartPointThreshold
						&& similarityFactorReverse.getDeltaLengthInRatio() < DeltaLengthInRatioThreshold) {
					for (int i = timeWarpInfoReverse.getPath().maxJ(); i > timeWarpInfoReverse
							.getPath().minJ(); i--) {
						Sample sample = new Sample();
						sample = coarseSampleList.get(i);
						coarseMatchedSampleList.add(sample);
					}
					isReverseMatched = true;
				} else {
					isReverseMatched = false;
				}

			} else if (distanceThreshold < similarityFactorReverse
					.getDTWDistance()
					&& distanceThreshold > similarityFactor.getDTWDistance()) {
				if (similarityFactor.getCorrectDistanceOfStartPoint() < distanceOfStartPointThreshold
						&& similarityFactor.getDeltaLengthInRatio() < DeltaLengthInRatioThreshold
						&& similarityFactor.getCountOfsameSample() < 20) {
					for (int i = timeWarpInfo.getPath().minJ(); i < timeWarpInfo
							.getPath().maxJ(); i++) {
						Sample sample = new Sample();
						sample = coarseSampleList.get(i);
						coarseMatchedSampleList.add(sample);
					}
					isForwordMatched = true;
				} else {
					isForwordMatched = false;
				}

			} else if (distanceThreshold > similarityFactorReverse
					.getDTWDistance()
					&& distanceThreshold > similarityFactor.getDTWDistance()) {

				if (similarityFactorReverse.getDTWDistance() > similarityFactor
						.getDTWDistance()) {
					if (similarityFactor.getCorrectDistanceOfStartPoint() < distanceOfStartPointThreshold
							&& similarityFactor.getDeltaLengthInRatio() < DeltaLengthInRatioThreshold) {
						for (int i = timeWarpInfo.getPath().minJ(); i < timeWarpInfo
								.getPath().maxJ(); i++) {
							Sample sample = new Sample();
							sample = coarseSampleList.get(i);
							coarseMatchedSampleList.add(sample);
						}
						isForwordMatched = true;
					} else if (similarityFactorReverse
							.getCorrectDistanceOfStartPoint() < distanceOfStartPointThreshold
							&& similarityFactorReverse.getDeltaLengthInRatio() < DeltaLengthInRatioThreshold) {
						for (int i = timeWarpInfoReverse.getPath().maxJ(); i > timeWarpInfoReverse
								.getPath().minJ(); i--) {
							Sample sample = new Sample();
							sample = coarseSampleList.get(i);
							coarseMatchedSampleList.add(sample);
						}
						isReverseMatched = true;
					} else {
						isReverseMatched = false;
						isForwordMatched = false;
					}
				} else {
					if (similarityFactorReverse
							.getCorrectDistanceOfStartPoint() < distanceOfStartPointThreshold
							&& similarityFactorReverse.getDeltaLengthInRatio() < DeltaLengthInRatioThreshold) {
						for (int i = timeWarpInfoReverse.getPath().maxJ(); i > timeWarpInfoReverse
								.getPath().minJ(); i--) {
							Sample sample = new Sample();
							sample = coarseSampleList.get(i);
							coarseMatchedSampleList.add(sample);
						}
						isReverseMatched = true;
					} else if (similarityFactor
							.getCorrectDistanceOfStartPoint() < distanceOfStartPointThreshold
							&& similarityFactor.getDeltaLengthInRatio() < DeltaLengthInRatioThreshold) {
						for (int i = timeWarpInfo.getPath().minJ(); i < timeWarpInfo
								.getPath().maxJ(); i++) {
							Sample sample = new Sample();
							sample = coarseSampleList.get(i);
							coarseMatchedSampleList.add(sample);
						}
						isForwordMatched = true;
					} else {
						isReverseMatched = false;
						isForwordMatched = false;
					}
				}

			} else {
				isReverseMatched = false;
				isForwordMatched = false;
			}
			long time5 = System.currentTimeMillis();
			Log.d("select", "" + (time5 - time4));
			EndPoint = new double[4];
			// EndPoint[0] = lastTrajectoryCoarseEndXCorrect;
			// EndPoint[1] = lastTrajectoryCoarseEndYCorrect;
			if (Constant.isDebug) {
				SensorDataLogFileDTWresuilt.trace("开始粗粒度反向校准\n");
			}
			if (isReverseMatched) {
				if (isIncrement) {
					int reverseStartNo = DataAdaptor.subSampleListSize
							/ SpectrumSize - DataAdaptor.increasementSize/SpectrumSize;
					lastTrajectoryCoarseEndXCorrect = coarseSampleList.get(
							timeWarpInfoReverse.getPath().get(reverseStartNo)
									.getRow()).getCurrxCorrect();
					lastTrajectoryCoarseEndYCorrect = coarseSampleList.get(
							timeWarpInfoReverse.getPath().get(reverseStartNo)
									.getRow()).getCurryCorrect();
					lastTrajectoryCoarseEndXOrigin = coarseSampleList.get(
							timeWarpInfoReverse.getPath().get(reverseStartNo)
									.getRow()).getCurrxOrigin();
					lastTrajectoryCoarseEndYOrigin = coarseSampleList.get(
							timeWarpInfoReverse.getPath().get(reverseStartNo)
									.getRow()).getCurryOrigin();
				} else {

					lastTrajectoryCoarseEndXCorrect = coarseSampleList.get(
							timeWarpInfoReverse.getPath().minJ())
							.getCurrxCorrect();
					lastTrajectoryCoarseEndYCorrect = coarseSampleList.get(
							timeWarpInfoReverse.getPath().minJ())
							.getCurryCorrect();
					lastTrajectoryCoarseEndXOrigin = coarseSampleList.get(
							timeWarpInfoReverse.getPath().minJ())
							.getCurrxOrigin();
					lastTrajectoryCoarseEndYOrigin = coarseSampleList.get(
							timeWarpInfoReverse.getPath().minJ())
							.getCurryOrigin();
				}
				EndPoint[0] = coarseSampleList.get(
						timeWarpInfoReverse.getPath().minJ()).getCurrxCorrect();
				EndPoint[1] = coarseSampleList.get(
						timeWarpInfoReverse.getPath().minJ()).getCurryCorrect();
				EndPoint[2] = coarseSampleList.get(
						timeWarpInfoReverse.getPath().minJ()).getCurrxConsult();
				EndPoint[3] = coarseSampleList.get(
						timeWarpInfoReverse.getPath().minJ()).getCurryConsult();
				if (Constant.isDebug) {
					SensorDataLogFileDTWresuilt.trace("粗粒度反向校准到," + EndPoint[0]
							+ "," + EndPoint[1] + "\n");
				}
			}
			if (Constant.isDebug) {
				SensorDataLogFileDTWresuilt.trace(similarityFactorReverse
						.toString() + "\n");

				ResuiltDisplay.displayDtwResuilt2(coarseSampleList,
						coarseSubSampleListReverse, timeWarpInfoReverse);

				SensorDataLogFileDTWresuilt.trace("\n\n");

				SensorDataLogFileDTWresuilt.trace("开始粗粒度正向校准\n\n");
			}
			if (isForwordMatched) {
				int StartNo = DataAdaptor.increasementSize/SpectrumSize;
				if (isIncrement) {
					lastTrajectoryCoarseEndXCorrect = coarseSampleList.get(
							timeWarpInfo.getPath().get(StartNo).getRow())
							.getCurrxCorrect();
					lastTrajectoryCoarseEndYCorrect = coarseSampleList.get(
							timeWarpInfo.getPath().get(StartNo).getRow())
							.getCurryCorrect();
					lastTrajectoryCoarseEndXOrigin = coarseSampleList.get(
							timeWarpInfo.getPath().get(StartNo).getRow())
							.getCurrxOrigin();
					lastTrajectoryCoarseEndYOrigin = coarseSampleList.get(
							timeWarpInfo.getPath().get(StartNo).getRow())
							.getCurryOrigin();
				} else {

					lastTrajectoryCoarseEndXCorrect = coarseSampleList.get(
							timeWarpInfo.getPath().maxJ()).getCurrxCorrect();
					lastTrajectoryCoarseEndYCorrect = coarseSampleList.get(
							timeWarpInfo.getPath().maxJ()).getCurryCorrect();
					lastTrajectoryCoarseEndXOrigin = coarseSampleList.get(
							timeWarpInfo.getPath().maxJ()).getCurrxOrigin();
					lastTrajectoryCoarseEndYOrigin = coarseSampleList.get(
							timeWarpInfo.getPath().maxJ()).getCurryOrigin();
				}
				EndPoint[0] = coarseSampleList.get(
						timeWarpInfo.getPath().maxJ()).getCurrxCorrect();
				EndPoint[1] = coarseSampleList.get(
						timeWarpInfo.getPath().maxJ()).getCurryCorrect();
				EndPoint[2] = coarseSampleList.get(
						timeWarpInfo.getPath().maxJ()).getCurrxConsult();
				EndPoint[3] = coarseSampleList.get(
						timeWarpInfo.getPath().maxJ()).getCurryConsult();
				if (Constant.isDebug) {
					SensorDataLogFileDTWresuilt.trace("粗粒度正向校准到," + EndPoint[0]
							+ "," + EndPoint[1] + "\n");
				}
			}
			if (Constant.isDebug) {
				SensorDataLogFileDTWresuilt.trace(similarityFactor.toString()
						+ "\n");

				ResuiltDisplay.displayDtwResuilt2(coarseSampleList,
						coarseSubSampleList, timeWarpInfo);

				SensorDataLogFileDTWresuilt.trace("\n\n\n\n\n");
			}
			

			if (!isReverseMatched&&!isForwordMatched) {
				if (isIncrement) {
					int StartNo = DataAdaptor.increasementSize/SpectrumSize;
					lastTrajectoryCoarseEndXCorrect = coarseSampleList.get(
							timeWarpInfo.getPath().get(StartNo).getRow())
							.getCurrxCorrect();
					lastTrajectoryCoarseEndYCorrect = coarseSampleList.get(
							timeWarpInfo.getPath().get(StartNo).getRow())
							.getCurryCorrect();
					lastTrajectoryCoarseEndXOrigin = coarseSampleList.get(
							timeWarpInfo.getPath().get(StartNo).getRow())
							.getCurrxOrigin();
					lastTrajectoryCoarseEndYOrigin = coarseSampleList.get(
							timeWarpInfo.getPath().get(StartNo).getRow())
							.getCurryOrigin();
				}
			}
			long time6 = System.currentTimeMillis();
			Log.d("writeTime", "" + (time6 - time5));
		} else {

		}
		DecimalFormat df = new DecimalFormat("##0.00");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
		// String p=df.format(price);//format 返回的是字符串
		int startNum = DataAdaptor.increasementSize / SpectrumSize;
		if (coarseMatchedSampleList.size() == 0) {
			if (isIncrement) {
				if (isFirstSegment) {
					lastTrajectoryCoarseEndXOrigin = coarseSubSampleList.get(
							startNum).getCurrxOrigin();
					lastTrajectoryCoarseEndYOrigin = coarseSubSampleList.get(
							startNum).getCurryOrigin();
					lastTrajectoryCoarseEndXCorrect = coarseSubSampleList.get(
							startNum).getCurrxCorrect();
					lastTrajectoryCoarseEndYCorrect = coarseSubSampleList.get(
							startNum).getCurryCorrect();
					for (int i = 0; i < coarseSubSampleList.size(); i++) {
						coarseSampleList.add(coarseSubSampleList.get(i));
						// SensorDataLogFileDB.trace(""
						// + coarseSubSampleList.get(i).toString() + "\n");
					}
					// SensorDataLogFileDB.trace("\n\n");

					isFirstSegment = false;
				} else {
					lastTrajectoryCoarseEndXCorrect = coarseSubSampleList.get(
							startNum).getCurrxCorrect();
					lastTrajectoryCoarseEndYCorrect = coarseSubSampleList.get(
							startNum).getCurryCorrect();
					lastTrajectoryCoarseEndXOrigin = coarseSubSampleList.get(
							startNum).getCurrxOrigin();
					lastTrajectoryCoarseEndYOrigin = coarseSubSampleList.get(
							startNum).getCurryOrigin();
					for (int i = coarseSubSampleList.size() - startNum; i < coarseSubSampleList
							.size(); i++) {
						coarseSampleList.add(coarseSubSampleList.get(i));
						// SensorDataLogFileDB.trace(""
						// + coarseSubSampleList.get(i).toString() + "\n");
					}

					Message message3 = new Message();
					message3.what = DTWCORRENT;
					message3.obj = "反向 ds "
							+ df.format(similarityFactorReverse
									.getDTWDistance())
							+ " ti "
							+ df.format(similarityFactorReverse.getDTWtime())
							+ " sd "
							+ df.format(similarityFactorReverse
									.getCorrectDistanceOfStartPoint())
							+ " lr "
							+ df.format(similarityFactorReverse
									.getDeltaLengthInRatio())
							+ " ss "
							+ df.format(similarityFactorReverse
									.getCountOfsameSample())
							+ "\n正向 ds "
							+ df.format(similarityFactor.getDTWDistance())
							+ " ti "
							+ df.format(similarityFactor.getDTWtime())
							+ " sd "
							+ df.format(similarityFactor
									.getCorrectDistanceOfStartPoint())
							+ " lr "
							+ df.format(similarityFactor
									.getDeltaLengthInRatio())
							+ " ss "
							+ df.format(similarityFactor.getCountOfsameSample());
					mhandler.sendMessage(message3);
				}
			} else {

				lastTrajectoryCoarseEndXOrigin = coarseSubSampleList.get(
						coarseSubSampleList.size() - 1).getCurrxOrigin();
				lastTrajectoryCoarseEndYOrigin = coarseSubSampleList.get(
						coarseSubSampleList.size() - 1).getCurryOrigin();
				lastTrajectoryCoarseEndXCorrect = coarseSubSampleList.get(
						coarseSubSampleList.size() - 1).getCurrxCorrect();
				lastTrajectoryCoarseEndYCorrect = coarseSubSampleList.get(
						coarseSubSampleList.size() - 1).getCurryCorrect();
				for (int i = 0; i < coarseSubSampleList.size(); i++) {
					coarseSampleList.add(coarseSubSampleList.get(i));
					// SensorDataLogFileDB.trace(""
					// + coarseSubSampleList.get(i).toString() + "\n");
				}

				Message message3 = new Message();
				message3.what = DTWCORRENT;
				message3.obj = "反向 ds "
						+ df.format(similarityFactorReverse.getDTWDistance())
						+ " ti "
						+ df.format(similarityFactorReverse.getDTWtime())
						+ " sd "
						+ df.format(similarityFactorReverse
								.getCorrectDistanceOfStartPoint())
						+ " lr "
						+ df.format(similarityFactorReverse
								.getDeltaLengthInRatio())
						+ " ss "
						+ df.format(similarityFactorReverse
								.getCountOfsameSample())
						+ "\n正向 ds "
						+ df.format(similarityFactor.getDTWDistance())
						+ " ti "
						+ df.format(similarityFactor.getDTWtime())
						+ " sd "
						+ df.format(similarityFactor
								.getCorrectDistanceOfStartPoint()) + " lr "
						+ df.format(similarityFactor.getDeltaLengthInRatio())
						+ " ss "
						+ df.format(similarityFactor.getCountOfsameSample());
				mhandler.sendMessage(message3);
			}

			for (int i = 0; i < fineSubSampleList.size(); i++) {
				fineSampleList.add(fineSubSampleList.get(i));
			}

		} else {

			Message message3 = new Message();
			message3.what = DTWCORRENT;
			message3.obj = "反向 ds "
					+ df.format(similarityFactorReverse.getDTWDistance())
					+ " ti "
					+ df.format(similarityFactorReverse.getDTWtime())
					+ " sd "
					+ df.format(similarityFactorReverse
							.getCorrectDistanceOfStartPoint())
					+ " lr "
					+ df.format(similarityFactorReverse.getDeltaLengthInRatio())
					+ " ss "
					+ df.format(similarityFactorReverse.getCountOfsameSample())
					+ "\n正向 ds "
					+ df.format(similarityFactor.getDTWDistance())
					+ " ti "
					+ df.format(similarityFactor.getDTWtime())
					+ " sd "
					+ df.format(similarityFactor
							.getCorrectDistanceOfStartPoint()) + " lr "
					+ df.format(similarityFactor.getDeltaLengthInRatio())
					+ " ss "
					+ df.format(similarityFactor.getCountOfsameSample());
			mhandler.sendMessage(message3);
			// EndPoint = new double[2];
			// EndPoint[0] = lastTrajectoryCoarseEndXCorrect;
			// EndPoint[1] = lastTrajectoryCoarseEndYCorrect;
			setEndPoint(EndPoint);
			setHasMatched(true);
			matchPointCount++;
			if (isReverseMatched) {

				Message message4 = new Message();
				message4.what = DTWCORRENT;
				message4.obj = matchPointCount + " 反向粗校准到  "
						+ df.format(EndPoint[0]) + " " + df.format(EndPoint[1]);
				mhandler.sendMessage(message4);
				if (addAll) {

					if (isIncrement) {
						ArrayList startArrayList = timeWarpInfoReverse
								.getPath().getMatchingIndexesForI(startNum);
						ArrayList endArrayList = timeWarpInfoReverse.getPath()
								.getMatchingIndexesForI(0);
						int startId = (Integer) startArrayList
								.get(startArrayList.size() - 1);
						int endId = (Integer) endArrayList.get(0);
						ArrayList<Sample> matchedList = new ArrayList<Sample>();
						for (int i = startId; i > endId; i--) {
							Sample sample = coarseSampleList.get(i);
							Sample sample2 = DeepCopy.SampleCopy(sample);
							sample2.setMagXOrigin(-sample.getMagXOrigin());
							sample2.setMagYOrigin(-sample.getMagYOrigin());
							sample2.setMagZOrigin(sample.getMagZOrigin());
							sample2.setMagXFlat(-sample.getMagXFlat());
							sample2.setMagYFlat(-sample.getMagYFlat());
							sample2.setMagZFlat(sample.getMagZFlat());
							sample2.setMagXFlatdecc(-sample.getMagXFlatdecc());
							sample2.setMagYFlatdecc(-sample.getMagYFlatdecc());
							sample2.setMagZFlatdecc(sample.getMagZFlatdecc());

							matchedList.add(sample2);

						}
						coarseSampleList.addAll(matchedList);
						// }
					} else {
						for (int i = 0; i < coarseMatchedSampleList.size(); i++) {
							coarseSampleList
									.add(coarseMatchedSampleList.get(i));
						}
					}

				}
			}
			if (isForwordMatched) {
				Message message4 = new Message();
				message4.what = DTWCORRENT;
				message4.obj = matchPointCount + " 正向粗校准到  "
						+ df.format(EndPoint[0]) + " " + df.format(EndPoint[1]);
				mhandler.sendMessage(message4);
				if (addAll) {

					if (isIncrement) {
						ArrayList startArrayList = timeWarpInfo.getPath()
								.getMatchingIndexesForI(
										coarseSubSampleList.size() - 1
												- startNum);
						ArrayList endArrayList = timeWarpInfo.getPath()
								.getMatchingIndexesForI(
										coarseSubSampleList.size() - 1);
						int startId = (Integer) startArrayList.get(0);
						int endId = (Integer) endArrayList.get(endArrayList
								.size() - 1);
						ArrayList<Sample> matchedList = new ArrayList<Sample>();
						for (int i = startId; i < endId; i++) {

							Sample sample = coarseSampleList.get(i);
							matchedList.add(sample);

						}
						coarseSampleList.addAll(matchedList);
					} else {
						for (int i = 0; i < coarseMatchedSampleList.size(); i++) {
							coarseSampleList
									.add(coarseMatchedSampleList.get(i));
						}
					}

				}
			}

			int minIndex = fineSampleList.indexOf(coarseMatchedSampleList
					.get(0));
			int maxIndex = fineSampleList.indexOf(coarseMatchedSampleList
					.get(coarseMatchedSampleList.size() - 1));
//			for (int i = minIndex - 5; i < maxIndex + 5; i++) {
//				fineMatchedSampleList.add(fineSampleList.get(i));
//			}

			double avgDelta = Direction.getMatchedDirection(
					coarseSubSampleList, coarseMatchedSampleList);
			if (avgDelta != -10000) {
				doOffSet = true;
				setOffSet(avgDelta);
				Message message5 = new Message();
				message5.what = DTWCORRENT;
				message5.obj = "" + df.format(avgDelta);
				mhandler.sendMessage(message5);
				// Toast.makeText(mContext, "红线旋转"+avgDelta,
				// Toast.LENGTH_LONG).show();
			}

		}
		if (countofMatch == 66) {

			for (int i = 0; i < coarseSampleList.size(); i++) {
				SensorDataLogFileOrigin.trace(coarseSampleList.get(i)
						.toString() + "\n");
			}
		}
		return fineMatchedSampleList;
	}

	public void fineDTW(ArrayList<Sample> fineSampleList,
			ArrayList<Sample> fineSubSampleList,
			ArrayList<Sample> fineSubSampleListReverse) {
		double distanceThreshold = 10000;
		double distanceOfStartPointThreshold = 5;
		double DeltaLengthInRatioThreshold = 1.5;
		double[] EndPoint = null;

		boolean isForwordMatched = false;
		boolean isReverseMatched = false;

		com.ict.fastDTW.dtw.TimeWarpInfo timeWarpInfoReverse = null;
		com.ict.fastDTW.dtw.TimeWarpInfo timeWarpInfo = null;
		long time2 = System.currentTimeMillis();
		final DistanceFunction distFn = DistanceFunctionFactory
				.getDistFnByName("EuclideanDistance");
		final com.ict.fastDTW.timeseries.TimeSeries tsI = new com.ict.fastDTW.timeseries.TimeSeries(
				gettsArray(fineSampleList), getlabels(),
				gettimeReadings(fineSampleList));
		final com.ict.fastDTW.timeseries.TimeSeries tsJReverse = new com.ict.fastDTW.timeseries.TimeSeries(
				gettsArray(fineSubSampleListReverse), getlabels(),
				gettimeReadings(fineSubSampleListReverse));
		final com.ict.fastDTW.timeseries.TimeSeries tsJ = new com.ict.fastDTW.timeseries.TimeSeries(
				gettsArray(fineSubSampleList), getlabels(),
				gettimeReadings(fineSubSampleList));
		if (isDTWWithWeight) {
			timeWarpInfoReverse = com.ict.fastDTW.dtw.DTW
					.getWarpInfoBetweenWithWeight(tsJReverse, tsI, distFn);
			timeWarpInfo = com.ict.fastDTW.dtw.DTW
					.getWarpInfoBetweenWithWeight(tsJ, tsI, distFn);
		} else {
			timeWarpInfoReverse = com.ict.fastDTW.dtw.DTW.getWarpInfoBetween(
					tsJReverse, tsI, distFn);
			timeWarpInfo = com.ict.fastDTW.dtw.DTW.getWarpInfoBetween(tsJ, tsI,
					distFn);
		}
		long time3 = System.currentTimeMillis();
		Message message3 = new Message();
		message3.what = DTWCORRENT;
		message3.obj = "细匹配耗时  " + (time3 - time2) + " 反向"
				+ (float) timeWarpInfoReverse.getDistance() + "正向"
				+ (float) timeWarpInfo.getDistance();
		mhandler.sendMessage(message3);
		SimilarityFactor similarityFactorReverse = getSimilarityFactor(
				timeWarpInfoReverse, fineSubSampleListReverse, fineSampleList);
		SimilarityFactor similarityFactor = getSimilarityFactor(timeWarpInfo,
				fineSubSampleList, fineSampleList);
		double distanceOfStartPointForword = Math.sqrt(Math.pow(
				lastTrajectoryFineEndX
						- fineSampleList.get(timeWarpInfo.getPath().minJ())
								.getCurrxOrigin(), 2)
				+ Math.pow(
						lastTrajectoryFineEndY
								- fineSampleList.get(
										timeWarpInfo.getPath().minJ())
										.getCurryOrigin(), 2));
		double distanceOfStartPointReverse = Math.sqrt(Math.pow(
				lastTrajectoryFineEndX
						- fineSampleList.get(
								timeWarpInfoReverse.getPath().minJ())
								.getCurrxOrigin(), 2)
				+ Math.pow(
						lastTrajectoryFineEndY
								- fineSampleList.get(
										timeWarpInfoReverse.getPath().minJ())
										.getCurryOrigin(), 2));
		similarityFactor
				.setOriginDistanceOfStartPoint(distanceOfStartPointForword);
		similarityFactorReverse
				.setOriginDistanceOfStartPoint(distanceOfStartPointReverse);
		Log.d("finesimilarityFactor", similarityFactor.toString());
		Log.d("finesimilarityFactorReverse", similarityFactorReverse.toString());
		if (distanceThreshold > similarityFactorReverse.getDTWDistance()
				&& distanceThreshold < similarityFactor.getDTWDistance()) {
			if (similarityFactorReverse.getOriginDistanceOfStartPoint() < distanceOfStartPointThreshold
					&& similarityFactorReverse.getDeltaLengthInRatio() < DeltaLengthInRatioThreshold) {
				lastTrajectoryFineEndX = fineSampleList.get(
						timeWarpInfoReverse.getPath().maxJ()).getCurrxOrigin();
				lastTrajectoryFineEndY = fineSampleList.get(
						timeWarpInfoReverse.getPath().maxJ()).getCurryOrigin();
				EndPoint = new double[2];
				EndPoint[0] = lastTrajectoryFineEndX;
				EndPoint[1] = lastTrajectoryFineEndY;
				isReverseMatched = true;
			} else {
				lastTrajectoryFineEndX = fineSampleList.get(
						fineSampleList.size() - 1).getCurrxOrigin();
				lastTrajectoryFineEndY = fineSampleList.get(
						fineSampleList.size() - 1).getCurryOrigin();
				isReverseMatched = false;
			}

		} else if (distanceThreshold < similarityFactorReverse.getDTWDistance()
				&& distanceThreshold > similarityFactor.getDTWDistance()) {
			if (similarityFactor.getOriginDistanceOfStartPoint() < distanceOfStartPointThreshold
					&& similarityFactor.getDeltaLengthInRatio() < DeltaLengthInRatioThreshold) {
				lastTrajectoryFineEndX = fineSampleList.get(
						timeWarpInfo.getPath().maxJ()).getCurrxOrigin();
				lastTrajectoryFineEndY = fineSampleList.get(
						timeWarpInfo.getPath().maxJ()).getCurryOrigin();
				EndPoint = new double[2];
				EndPoint[0] = lastTrajectoryFineEndX;
				EndPoint[1] = lastTrajectoryFineEndY;
				isForwordMatched = true;
			} else {
				lastTrajectoryFineEndX = fineSampleList.get(
						fineSampleList.size() - 1).getCurrxOrigin();
				lastTrajectoryFineEndY = fineSampleList.get(
						fineSampleList.size() - 1).getCurryOrigin();
				isForwordMatched = false;
			}

		} else if (distanceThreshold > similarityFactorReverse.getDTWDistance()
				&& distanceThreshold > similarityFactor.getDTWDistance()) {
			if (similarityFactorReverse.getDTWDistance() > similarityFactor
					.getDTWDistance()) {
				if (similarityFactor.getOriginDistanceOfStartPoint() < distanceOfStartPointThreshold
						&& similarityFactor.getDeltaLengthInRatio() < DeltaLengthInRatioThreshold) {
					lastTrajectoryFineEndX = fineSampleList.get(
							timeWarpInfo.getPath().maxJ()).getCurrxOrigin();
					lastTrajectoryFineEndY = fineSampleList.get(
							timeWarpInfo.getPath().maxJ()).getCurryOrigin();
					EndPoint = new double[2];
					EndPoint[0] = lastTrajectoryFineEndX;
					EndPoint[1] = lastTrajectoryFineEndY;
					isForwordMatched = true;
				} else if (similarityFactorReverse
						.getOriginDistanceOfStartPoint() < distanceOfStartPointThreshold
						&& similarityFactorReverse.getDeltaLengthInRatio() < DeltaLengthInRatioThreshold) {
					lastTrajectoryFineEndX = fineSampleList.get(
							timeWarpInfoReverse.getPath().maxJ())
							.getCurrxOrigin();
					lastTrajectoryFineEndY = fineSampleList.get(
							timeWarpInfoReverse.getPath().maxJ())
							.getCurryOrigin();
					EndPoint = new double[2];
					EndPoint[0] = lastTrajectoryFineEndX;
					EndPoint[1] = lastTrajectoryFineEndY;
					isReverseMatched = true;
				} else {
					lastTrajectoryFineEndX = fineSampleList.get(
							fineSampleList.size() - 1).getCurrxOrigin();
					lastTrajectoryFineEndY = fineSampleList.get(
							fineSampleList.size() - 1).getCurryOrigin();
					isForwordMatched = false;
					isReverseMatched = false;
				}
			} else {

				if (similarityFactorReverse.getOriginDistanceOfStartPoint() < distanceOfStartPointThreshold
						&& similarityFactorReverse.getDeltaLengthInRatio() < DeltaLengthInRatioThreshold) {
					lastTrajectoryFineEndX = fineSampleList.get(
							timeWarpInfoReverse.getPath().maxJ())
							.getCurrxOrigin();
					lastTrajectoryFineEndY = fineSampleList.get(
							timeWarpInfoReverse.getPath().maxJ())
							.getCurryOrigin();
					EndPoint = new double[2];
					EndPoint[0] = lastTrajectoryFineEndX;
					EndPoint[1] = lastTrajectoryFineEndY;
				} else if (similarityFactor.getOriginDistanceOfStartPoint() < distanceOfStartPointThreshold
						&& similarityFactor.getDeltaLengthInRatio() < DeltaLengthInRatioThreshold) {
					lastTrajectoryFineEndX = fineSampleList.get(
							timeWarpInfo.getPath().maxJ()).getCurrxOrigin();
					lastTrajectoryFineEndY = fineSampleList.get(
							timeWarpInfo.getPath().maxJ()).getCurryOrigin();
					EndPoint = new double[2];
					EndPoint[0] = lastTrajectoryFineEndX;
					EndPoint[1] = lastTrajectoryFineEndY;
				} else {
					lastTrajectoryFineEndX = fineSampleList.get(
							fineSampleList.size() - 1).getCurrxOrigin();
					lastTrajectoryFineEndY = fineSampleList.get(
							fineSampleList.size() - 1).getCurryOrigin();
					isForwordMatched = false;
					isReverseMatched = false;
				}
			}

		} else {
			lastTrajectoryFineEndX = fineSampleList.get(
					fineSampleList.size() - 1).getCurrxOrigin();
			lastTrajectoryFineEndY = fineSampleList.get(
					fineSampleList.size() - 1).getCurryOrigin();
			isForwordMatched = false;
			isReverseMatched = false;
		}

		long time = System.currentTimeMillis();
		SensorDataLogFileDTWresuilt.trace("开始细粒度反向校准\n");
		if (isReverseMatched) {
			SensorDataLogFileDTWresuilt.trace("细粒度反向校准到," + EndPoint[0] + ","
					+ EndPoint[1] + "\n");
		}
		SensorDataLogFileDTWresuilt.trace(similarityFactorReverse.toString()
				+ "\n");
		ResuiltDisplay.displayDtwResuilt2(fineSampleList, fineSubSampleList,
				timeWarpInfoReverse);
		SensorDataLogFileDTWresuilt.trace("\n\n");

		SensorDataLogFileDTWresuilt.trace("开始细粒度正向校准\n");
		if (isForwordMatched) {
			SensorDataLogFileDTWresuilt.trace("细粒度正向校准到," + EndPoint[0] + ","
					+ EndPoint[1] + "\n");
		}
		SensorDataLogFileDTWresuilt.trace(similarityFactor.toString() + "\n");
		ResuiltDisplay.displayDtwResuilt2(fineSampleList, fineSubSampleList,
				timeWarpInfoReverse);
		SensorDataLogFileDTWresuilt.trace("\n\n\n\n\n");
		long time4 = System.currentTimeMillis();
		Log.d("WriteFineTime", "" + (time4 - time));
		if (EndPoint != null) {
			Message message4 = new Message();
			message4.what = DTWCORRENT;
			message4.obj = "细校准到  " + (float) EndPoint[0] + " "
					+ (float) EndPoint[1];
			mhandler.sendMessage(message4);
			setEndPoint(EndPoint);
			setHasMatched(true);

		} else {
			lastTrajectoryCoarseEndXOrigin = fineSubSampleList.get(
					fineSubSampleList.size() - 1).getCurrxOrigin();
			lastTrajectoryCoarseEndYOrigin = fineSubSampleList.get(
					fineSubSampleList.size() - 1).getCurryOrigin();
		}

	}

	/*
	 * 传入两个原始样本序列，返回两个粗粒度的样本序列
	 */
	public ArrayList<ArrayList<Sample>> getCoarsnessDTWData(
			ArrayList<Sample> subSampleListOrigin,
			ArrayList<Sample> subSampleListReverseOrigin) {
		ArrayList<Double> xSpectrumArrayList = new ArrayList<Double>();
		ArrayList<Double> ySpectrumArrayList = new ArrayList<Double>();
		ArrayList<Double> zSpectrumArrayList = new ArrayList<Double>();
		ArrayList<Double> xSpectrumArrayListreverse = new ArrayList<Double>();
		ArrayList<Double> ySpectrumArrayListreverse = new ArrayList<Double>();
		ArrayList<Double> zSpectrumArrayListreverse = new ArrayList<Double>();
		Sample SpectrumSample = null;
		Sample SpectrumSampleReverse = null;
		ArrayList<Sample> spectrumSampleList = new ArrayList<Sample>();
		ArrayList<Sample> SpectrumSampleReverseList = new ArrayList<Sample>();
		ArrayList<ArrayList<Sample>> SpectrumList = new ArrayList<ArrayList<Sample>>();
		SensorDataLogFileSize.trace("subSampleListOrigin "
				+ subSampleListOrigin.size() + " subSampleListReverseOrigin"
				+ subSampleListReverseOrigin.size()+"\n");
		for (int i = 0; i < Math.min(subSampleListOrigin.size(),subSampleListReverseOrigin.size()); i++) {
			if (xSpectrumArrayList.size() == SpectrumSize / 2) {
				SpectrumSample = subSampleListOrigin.get(i);
				SpectrumSampleReverse = subSampleListReverseOrigin.get(i);

			}
			Sample sample3 = subSampleListOrigin.get(i);
			xSpectrumArrayList.add(sample3.getMagXFlat());
			ySpectrumArrayList.add(sample3.getMagYFlat());
			zSpectrumArrayList.add(sample3.getMagZFlat());

			Sample sample4 = subSampleListReverseOrigin.get(i);
			if(sample4==null){
				System.out.print("");
			}else {
				xSpectrumArrayListreverse.add(sample4.getMagXFlat());
				ySpectrumArrayListreverse.add(sample4.getMagYFlat());
				zSpectrumArrayListreverse.add(sample4.getMagZFlat());
			}

			if (xSpectrumArrayList.size() == SpectrumSize) {
				SpectrumSample
						.setSpectrumXFlat(getSpectrum(xSpectrumArrayList));
				SpectrumSample
						.setSpectrumYFlat(getSpectrum(ySpectrumArrayList));
				SpectrumSample
						.setSpectrumZFlat(getSpectrum(zSpectrumArrayList));
				spectrumSampleList.add(SpectrumSample);
				SpectrumSampleReverse
						.setSpectrumXFlat(getSpectrum(xSpectrumArrayListreverse));
				SpectrumSampleReverse
						.setSpectrumYFlat(getSpectrum(ySpectrumArrayListreverse));
				SpectrumSampleReverse
						.setSpectrumZFlat(getSpectrum(zSpectrumArrayListreverse));
				SpectrumSampleReverseList.add(SpectrumSampleReverse);

				xSpectrumArrayList.clear();
				ySpectrumArrayList.clear();
				zSpectrumArrayList.clear();
				xSpectrumArrayListreverse.clear();
				ySpectrumArrayListreverse.clear();
				zSpectrumArrayListreverse.clear();
			}
		}
		SpectrumList.add(spectrumSampleList);// 正向序列的功率谱
		SpectrumList.add(SpectrumSampleReverseList);// 反向序列的功率谱
		return SpectrumList;

	}

	/*
	 * 每个元素平方相加，求均值，再开二次方
	 */
	public double getSpectrum(ArrayList<Double> magArrayList) {
		double Spectrum = 0;
		int nagetivecount = 0;
		for (int i = 0; i < magArrayList.size(); i++) {
			Spectrum += Math.pow(magArrayList.get(i), 2);
			if (magArrayList.get(i) < 0) {
				nagetivecount++;
			}
		}
		Spectrum = Math.sqrt(Spectrum) / 5;
		if (nagetivecount > magArrayList.size() * 0.5) {
			Spectrum = -Spectrum;
		}
		return Spectrum;

	}

	public String getArrayListMinIDAndValue(ArrayList sampleList) {
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
		return maxId + "and" + maxDevation;

	}

	public static double[] ArraylstToArrays(ArrayList<Double> data) {
		double[] doubledata = new double[data.size()];
		for (int i = 0; i < doubledata.length; i++) {
			doubledata[i] = data.get(i);
		}
		return doubledata;
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

	public static SimilarityFactor getSimilarityFactor(
			TimeWarpInfo timeWarpInfo, ArrayList<Sample> subSampleList,
			ArrayList<Sample> SampleList) {

		double deltaLengthInRatio = 1000000;
		double currentAccumulatedDistance = 0;
		double matchedAccumulatedDistance = 0;
		double dTWDistance = timeWarpInfo.getDistance();

		ArrayList<Double> currentDirectionList = new ArrayList<Double>();
		ArrayList<Double> matchedDirectionList = new ArrayList<Double>();

		for (int i = 0; i < subSampleList.size() - 1; i++) {
			currentAccumulatedDistance += Math.sqrt((Math.pow(subSampleList
					.get(i).getCurrxOrigin()
					- subSampleList.get(i + 1).getCurrxOrigin(), 2) + Math.pow(
					subSampleList.get(i).getCurryOrigin()
							- subSampleList.get(i + 1).getCurryOrigin(), 2)));
			currentDirectionList.add(subSampleList.get(i).getOrientationOldX());
		}
		int maxJ = timeWarpInfo.getPath().maxJ();
		int minj = timeWarpInfo.getPath().minJ();
		double matchedLength = maxJ - minj;
		for (int i = minj; i < maxJ - 1; i++) {
			matchedAccumulatedDistance += Math.sqrt(Math.pow(SampleList.get(i)
					.getCurrxOrigin() - SampleList.get(i + 1).getCurrxOrigin(),
					2)
					+ Math.pow(SampleList.get(i).getCurryOrigin()
							- SampleList.get(i + 1).getCurryOrigin(), 2));
			matchedDirectionList.add(SampleList.get(i).getOrientationOldX());
		}
		double currentDirection =  AngleUtils.calculateAngle325(currentDirectionList);
		double matchedDirection = AngleUtils.calculateAngle325(matchedDirectionList);
		double deltaDirection = AngleUtils.calculateDifference(
				currentDirection, matchedDirection);
		double deltaLength = Math.abs(currentAccumulatedDistance
				- matchedAccumulatedDistance);

		deltaLengthInRatio = (deltaLength / currentAccumulatedDistance) * 100;// 长度差的比值放大十倍,度量两条轨迹在长度上的相似性

		int countOfsameSample = countOfsameSample(subSampleList, SampleList,
				timeWarpInfo);

		SimilarityFactor similarityFactor = new SimilarityFactor(dTWDistance,
				matchedLength, currentAccumulatedDistance,
				matchedAccumulatedDistance, deltaLength, deltaLengthInRatio,
				currentDirection, matchedDirection, deltaDirection, 10000,
				10000, false, countOfsameSample, dTWtime);
		return similarityFactor;
	}

	public static double getNormalizationDirection(double direction) {
		double NormalizationDirection = 0;
		if (-45 <= direction && direction <= 45) {
			NormalizationDirection = 0;
		} else if (45 < direction && direction <= 135) {
			NormalizationDirection = 90;
		} else if (-135 < direction && direction < -45) {
			NormalizationDirection = -90;
		} else {
			NormalizationDirection = 180;
		}
		return NormalizationDirection;

	}

	public ArrayList gettsArray(ArrayList<Sample> samplelist) {
		ArrayList tsArray = new ArrayList();

		for (int i = 0; i < samplelist.size(); i++) {
			final ArrayList currentLineValues = new ArrayList();
			Sample sample = samplelist.get(i);
			// currentLineValues.add(Double.valueOf(sample.getMagH()));
			// currentLineValues.add(Double.valueOf(sample.getMagV()));
			// currentLineValues.add(Double.valueOf(sample.getSpectrumH()));
			// currentLineValues.add(Double.valueOf(sample.getSpectrumV()));
			currentLineValues.add(Double.valueOf(sample.getMagXFlat()));
			currentLineValues.add(Double.valueOf(sample.getMagYFlat()));
			currentLineValues.add(Double.valueOf(sample.getMagZFlat()));
			// currentLineValues.add(Double.valueOf(sample.getSpectrumXFlat()));
			// currentLineValues.add(Double.valueOf(sample.getSpectrumYFlat()));
			// currentLineValues.add(Double.valueOf(sample.getSpectrumZFlat()));
			// SensorDataLogFileReview.trace(sample.getMagH()+","
			// +sample.getMagV()+"\n");
			final TimeSeriesPoint readings = new TimeSeriesPoint(
					currentLineValues.subList(0, currentLineValues.size()));
			tsArray.add(readings);
		}
		return tsArray;
	}

	public ArrayList gettimeReadings(ArrayList<Sample> segment) {
		ArrayList timeReadings = new ArrayList();
		for (int i = 0; i < segment.size(); i++) {

			timeReadings.add(new Double(i));
		}
		return timeReadings;

	}

	public ArrayList getlabels() {
		ArrayList labels = new ArrayList();
		labels.add("Time");
		for (int c = 0; c < 3; c++) {
			labels.add(new String("c" + c));
		}
		return labels;
	}

	// 去中心化处理
	public double[] decentralization(double[] list) {
		double sum = 0.0;
		for (int i = 0; i < list.length; i++) {
			sum += list[i];
		}
		double average = sum / list.length;
		for (int i = 0; i < list.length; i++) {
			list[i] -= average;
		}

		return list;
	}

	/*
	 * 列表A的地磁FLat包含列表B的所有
	 */
	public static int countOfsameSample(ArrayList<Sample> testList,
			ArrayList<Sample> dBList, TimeWarpInfo info2) {
		int count = 0;
		int minJ = info2.getPath().minJ();
		int maxJ = info2.getPath().maxJ();
		for (int i = 0; i < Math.min(maxJ - minJ, testList.size()); i++) {
			if (testList.get(i).getSpectrumXFlat() == dBList.get(minJ + i)
					.getSpectrumXFlat()
					&& testList.get(i).getSpectrumYFlat() == dBList.get(
							minJ + i).getSpectrumYFlat()
					&& testList.get(i).getSpectrumZFlat() == dBList.get(
							minJ + i).getSpectrumZFlat()) {
				count++;
			}
		}
		return count;
	}
}
