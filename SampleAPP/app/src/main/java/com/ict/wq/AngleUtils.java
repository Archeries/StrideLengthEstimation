package com.ict.wq;

import java.util.ArrayList;
import java.util.List;

public class AngleUtils {
	public static double calculateDifference(double a1, double a2) {
		double b1;
		if (Math.abs(a1 - a2) <= 180) {
			b1 = Math.abs(a1 - a2);
		} else {
			b1 = 360 - Math.abs(a1 - a2);
		}
		return b1;
	}
	
	public static double getTheta2(double startx, double starty, double endx,
			double endy) {
		double theta;
		// System.out.println("与X夹角"
		// + Math.toDegrees(Math.atan(Math.abs(y - lastyIngetTheta)
		// / Math.abs(x - lastxIngetTheta))));
		if ((endx -startx ) > 0 && (endy -starty ) > 0) {// 1
			theta = Math
					.atan(Math.abs(endx -startx ) / Math.abs(endy -starty));
			// theta = Math.toDegrees(theta);
		} else if ((endx -startx ) > 0 && (endy -starty) < 0)// 4
			theta = (Math.PI / 2)
					+ Math.atan(Math.abs(endy -starty)
							/ Math.abs(endx -startx ));
		else if ((endx -startx ) < 0 && (endy -starty) > 0)// 2
			theta = -Math.atan(Math.abs(endx -startx )
					/ Math.abs(endy -starty));
		else
			// 3
			theta = -((Math.PI / 2) + Math.atan(Math.abs(endy -starty)
					/ Math.abs(endx -startx )));

		return theta;
	}
	
	private static double getMean(List<Double> data) {
		double m = 0;
		int count = 0;

		for (int i = 0; i < data.size(); i++) {
			m += data.get(i);
			count++;
		}

		if (count != 0) {
			m = m / count;
		}

		return m;
	}
	
	public static double calculateAngle325(ArrayList<Double> magArrayList) {
		int count = 0;// ����ͳ�ƴ���135��С��-135�ȵĸ���
		if (magArrayList.size() != 0) {

			ArrayList<Double> positiveList = new ArrayList<Double>();
			ArrayList<Double> negativeList = new ArrayList<Double>();
			for (int i = 0; i < magArrayList.size(); i++) {
				if (magArrayList.get(i) >= 0) {
					positiveList.add(magArrayList.get(i));
				} else {
					negativeList.add(magArrayList.get(i));
				}
				if (magArrayList.get(i) > 135 || magArrayList.get(i) < -135) {
					count++;
				}
			}
			double positive = positiveList.size();
			double sum = magArrayList.size();
			double negative = negativeList.size();
			double percent = (positive / sum);
			// System.out.println(percent);
			if (positive == 0 || positive == 1) {
				return calculateAvg(magArrayList);
			}
			if (count > magArrayList.size() * 0.3) {
				// Log.d(tag, "正负适量相当");
				double avgPositive = calculateAvg(positiveList);
				double avgNegative = calculateAvg(negativeList);

				double angleDifferenceABS = Math.abs(avgPositive - avgNegative);
				double angleDifference = 0;

				if (angleDifferenceABS >= 180) {
					angleDifference = 360 - angleDifferenceABS;
				} else {
					angleDifference = angleDifferenceABS;
				}
				double avgPiece = angleDifference
						/ (positiveList.size() + negativeList.size());

				if (Math.abs(avgNegative) + Math.abs(avgPositive) > 180) {
					double num = avgNegative - positiveList.size() * avgPiece;
					if (num < -180) {
						return num + 360;
					} else {
						return num;
					}
				} else {
					double num = avgNegative + positiveList.size() * avgPiece;
					return num;
				}

			} else {
				return calculateAvg(magArrayList);
			}
		} else {
			return 0;
		}

	}
	
	public static double calculateAngle428(ArrayList<Double> magArrayList) {
		 
		if (magArrayList.size() != 0) {

			ArrayList<Double> positiveList = new ArrayList<Double>();
			// ArrayList<Double> negativeList = new ArrayList<Double>();
			for (int i = 0; i < magArrayList.size(); i++) {
				if (magArrayList.get(i) >= 0) {
					positiveList.add(magArrayList.get(i));
				} else {
					positiveList.add(magArrayList.get(i) + 360);
				}

			}
			double avg = calculateAvg(positiveList);
			if (avg > 180) {
				avg = avg - 360;
			} else if (avg < -180) {
				avg = avg + 360;
			}
			return avg;
		}
		return 0;

	}
	public static double calculateAngleShao(ArrayList<Double> magArrayList) {
		ArrayList<Double> magABSArrayList = new ArrayList<Double>();
		if (magArrayList.size() != 0) {
			for (int i = 0; i < magArrayList.size(); i++) {
				if (magArrayList.get(i) < 0) {
					magABSArrayList.add(magArrayList.get(i) + 360);
				} else {

					magABSArrayList.add(magArrayList.get(i));

				}
			}
			double ABSavg = calculateAvg(magABSArrayList);
			if (ABSavg > 180) {
				ABSavg = ABSavg - 360;
			}
			return ABSavg;
		} else {
			return 0;
		}
	}
	public static double calculateAvg(ArrayList<Double> list) {
		if(list.size()==0){
			return 0;
		}else {
			double sum = 0.0;
			for (int i = 0; i < list.size(); i++) {
				sum = sum + list.get(i);
			}
			return sum / list.size();
		}
		
	}
}
