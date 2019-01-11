package com.ict.wq;

import java.util.ArrayList;
import java.util.Collections;

import android.util.Log;

public class Direction {

	public static void main(String[] args) {

	}

	private static double lastXInConsult = 0;
	private static double lastYInConsult = 0;
	private static double lastYInCorrect = 0;

	private static double lastthetaInCorrect = 0;
	private static double lastxInCorrect = 0;
	private static double lastyInCorrect = 0;
	private static double lastthetaInConsult = 0;

	private static double lastxInConsult = 0;
	private static double lastyInConsult = 0;
	private static int count = 0;

	public void calculateDirection(double currx, double curry,
			double realTimeDirection) {

	}

	public static void getrealTimeMagDirection() {

	}

	public static double getMatchedDirection(
			ArrayList<Sample> coarseSubSampleList,
			ArrayList<Sample> coarseMatchedSampleList) {
		count++;
		double xInConsult = 0;
		double yInConsult = 0;
		double xInCorrect = 0;
		double yInCorrect = 0;
		double thetaInCorrect = 0;
		double thetaInConsult = 0;

		ArrayList<Double> consultDirectionList = new ArrayList<Double>();
		ArrayList<Double> correctDirectionList = new ArrayList<Double>();

		ArrayList<AtomLine> consultAtomLineList = new ArrayList<AtomLine>();
		ArrayList<AtomLine> correctAtomLineList = new ArrayList<AtomLine>();
		double correctDirectionAVG = 10000;
		for (int i = 0; i < coarseMatchedSampleList.size(); i++) {
//			SensorDataLogFiledecc.trace(""
//					+ coarseMatchedSampleList.get(i).toString() + "\n");
			Sample sample = coarseMatchedSampleList.get(i);
			// xInCorrect = sample.getCurrxCorrect();
			// yInCorrect = sample.getCurryCorrect();
			xInCorrect = sample.getCurrxConsult();
			yInCorrect = sample.getCurryConsult();
			if ((Math.abs(xInCorrect - lastxInCorrect) > 0.1 || Math
					.abs(yInCorrect - lastYInCorrect) > 0.1)) {
				yInCorrect = yInCorrect;
				thetaInCorrect = getThetaInCOrrect(xInCorrect, yInCorrect);
				thetaInCorrect = Math.toDegrees(thetaInCorrect);
				double deltathetaInCorrect = calculateDifference(
						thetaInCorrect, lastthetaInCorrect);
				// correctDirectionList.add(thetaInCorrect);
				if (deltathetaInCorrect > 20) {
					ArrayList<Double> correctDirectionListTemp = new ArrayList<Double>();
					for (int j = 0; j < correctDirectionList.size(); j++) {
						correctDirectionListTemp.add(correctDirectionList
								.get(j));
					}
					correctDirectionAVG =  AngleUtils.calculateAngle325(correctDirectionListTemp);
					AtomLine CorrectAtomLine = new AtomLine();
					CorrectAtomLine.setLength(correctDirectionListTemp.size());
					CorrectAtomLine.setDirection(correctDirectionAVG);
					CorrectAtomLine.setConsultList(correctDirectionListTemp);
					correctAtomLineList.add(CorrectAtomLine);
					correctDirectionList.clear();
					correctDirectionList.add(thetaInCorrect);
				} else {
					correctDirectionList.add(thetaInCorrect);
				}

				lastthetaInCorrect = thetaInCorrect;
				lastXInConsult = xInCorrect;
				lastYInCorrect = yInCorrect;

			}
		}
		ArrayList<Double> correctDirectionListTemp = new ArrayList<Double>();
		for (int j = 0; j < correctDirectionList.size(); j++) {
			correctDirectionListTemp.add(correctDirectionList.get(j));
		}
		correctDirectionAVG =  AngleUtils.calculateAngle325(correctDirectionListTemp);
		AtomLine CorrectAtomLine = new AtomLine();
		CorrectAtomLine.setLength(correctDirectionListTemp.size());
		CorrectAtomLine.setDirection(correctDirectionAVG);
		CorrectAtomLine.setConsultList(correctDirectionListTemp);
		correctAtomLineList.add(CorrectAtomLine);
		if (count == 4) {
			System.out.println();
		}
		double consultDirectionAVG = 10000;
		for (int i = 0; i < coarseSubSampleList.size(); i++) {
//			SensorDataLogFiledecc.trace(""
//					+ coarseSubSampleList.get(i).toString() + "\n");
			Sample sample = coarseSubSampleList.get(i);
			xInConsult = sample.getCurrxConsult();
			yInConsult = sample.getCurryConsult();
			if ((Math.abs(xInConsult - lastxInConsult) > 0.1 || Math
					.abs(yInConsult - lastYInConsult) > 0.1)) {
				yInConsult = yInConsult;// y反向
				thetaInConsult = getThetaInConsult(xInConsult, yInConsult);
				thetaInConsult = Math.toDegrees(thetaInConsult);
				double deltathetaInConsult = calculateDifference(
						thetaInConsult, lastthetaInConsult);
				// consultDirectionList.add(thetaInConsult);
				if (deltathetaInConsult > 20) {
					ArrayList<Double> consultDirectionListTemp = new ArrayList<Double>();
					for (int j = 0; j < consultDirectionList.size(); j++) {
						consultDirectionListTemp.add(consultDirectionList
								.get(j));
					}
					consultDirectionAVG =  AngleUtils.calculateAngle325(consultDirectionListTemp);
					AtomLine consultAtomLine = new AtomLine();
					consultAtomLine.setLength(consultDirectionListTemp.size());
					consultAtomLine.setConsultList(consultDirectionListTemp);
					consultAtomLine.setDirection(consultDirectionAVG);
					consultAtomLineList.add(consultAtomLine);
					consultDirectionList.clear();
					consultDirectionList.add(thetaInConsult);
				} else {
					consultDirectionList.add(thetaInConsult);
				}

				lastthetaInConsult = thetaInConsult;
				lastxInConsult = xInConsult;
				lastYInConsult = yInConsult;

			}
		}
		if (count == 4) {
			System.out.println();
		}
	//	SensorDataLogFiledecc.trace("\n\n\n");
		ArrayList<Double> consultDirectionListTemp = new ArrayList<Double>();
		for (int j = 0; j < consultDirectionList.size(); j++) {
			consultDirectionListTemp.add(consultDirectionList.get(j));
		}
		consultDirectionAVG =  AngleUtils.calculateAngle325(consultDirectionListTemp);
		AtomLine consultAtomLine = new AtomLine();
		consultAtomLine.setLength(consultDirectionListTemp.size());
		consultAtomLine.setConsultList(consultDirectionListTemp);
		consultAtomLine.setDirection(consultDirectionAVG);
		consultAtomLineList.add(consultAtomLine);
		// 降序排序
		Collections.sort(consultAtomLineList);
		Collections.sort(correctAtomLineList);
		Log.d("deltaAVG",
				"consultAtomLineList" + consultAtomLineList.toString());
		Log.d("deltaAVG",
				"correctAtomLineList" + correctAtomLineList.toString());
		double avgDelta = 10000;

		if (consultAtomLineList.size() > 0 && correctAtomLineList.size() > 0) {

			// int consultmaxSize = consultAtomLineList.get(0).getLength();
			// int correctmaxSize = correctAtomLineList.get(0).getLength();
			// double avgConsult = consultAtomLineList.get(0).getDirection();
			// double avgCorrect = correctAtomLineList.get(0).getDirection();
			// Log.d("deltaAVG", "avgCorrect" + avgCorrect +"avgConsult"
			// +avgConsult);
			// if (consultmaxSize > 5 && correctmaxSize > 5) {
			//
			// if (Math.abs(avgDelta) > 45&&avgDelta!=10000) {
			// System.out.println();
			// } else {
			// avgDelta =avgConsult - avgCorrect;
			//
			// }
			// }
			ArrayList<AtomLine> consultAtomLineListTemp = new ArrayList<AtomLine>();
			for (int i = 0; i < consultAtomLineList.size(); i++) {
				AtomLine atomLine = consultAtomLineList.get(i);
				if (atomLine.getLength() > 5) {
					consultAtomLineListTemp.add(atomLine);
				}
			}
			ArrayList<AtomLine> correctAtomLineListTemp = new ArrayList<AtomLine>();
			for (int i = 0; i < correctAtomLineList.size(); i++) {
				AtomLine atomLine = correctAtomLineList.get(i);
				if (atomLine.getLength() > 5) {
					correctAtomLineListTemp.add(atomLine);
				}
			}
			ArrayList<CorrectAndConsultDirection> correctAndConsultDirectionList = new ArrayList<CorrectAndConsultDirection>();
			for (int i = 0; i < correctAtomLineListTemp.size(); i++) {
				for (int j = 0; j < consultAtomLineListTemp.size(); j++) {
				double	correctDirection=correctAtomLineListTemp.get(i).getDirection();
				double consultDirection=consultAtomLineListTemp.get(j).getDirection();
					 CorrectAndConsultDirection correctAndConsultDirection=new CorrectAndConsultDirection();
					 correctAndConsultDirection.setCorrectDirection(correctDirection);
					 correctAndConsultDirection.setConsultDirection(consultDirection);
					 correctAndConsultDirection.setABSdeltaCorrectAndConsult(calculateDifference(correctDirection, consultDirection));
					 correctAndConsultDirection.setCorrectMinusConsult(AminusB(correctDirection, consultDirection));
					 correctAndConsultDirectionList.add(correctAndConsultDirection);
				}
			}
			if (correctAndConsultDirectionList.size()>0) {
				
				Collections.sort(correctAndConsultDirectionList);
				Log.d("deltaAVG",
						"correctAndConsultDirectionList" + correctAndConsultDirectionList.toString());
				double deltaDirection=correctAndConsultDirectionList.get(0).getCorrectMinusConsult();
				if (Math.abs(deltaDirection)<45) {
					
					avgDelta=deltaDirection;
				}
			}
		}
		return -avgDelta;

	}

	private static double calculateDifference(double a1, double a2) {
		double b1;
		if (Math.abs(a1 - a2) <= 180) {
			b1 = Math.abs(a1 - a2);
		} else {
			b1 = 360 - Math.abs(a1 - a2);
		}
		return b1;
	}

	private static double AminusB(double a1, double a2) {
		double b1;
		if (a1 - a2 > 180) {
			b1 = a1 - a2 - 360;
		} else if (a1 - a2 < -180) {

			b1 = a1 - a2 + 360;
		} else {
			b1 = a1 - a2;
		}
		return b1;
	}

	public static double getThetaInConsult(double x, double y) {
		double theta;
		if (x == -11.582008842985962) {
			System.out.println();
		}
		// System.out.println("与X夹角"
		// + Math.toDegrees(Math.atan(Math.abs(y - lastyIngetTheta)
		// / Math.abs(x - lastxIngetTheta))));
		if ((x - lastxInConsult) > 0 && (y - lastyInConsult) > 0) {// 1
			theta = Math.atan(Math.abs(x - lastxInConsult)
					/ Math.abs(y - lastyInConsult));
			// theta = Math.toDegrees(theta);
		} else if ((x - lastxInConsult) > 0 && (y - lastyInConsult) < 0)// 4
			theta = (Math.PI / 2)
					+ Math.atan(Math.abs(y - lastyInConsult)
							/ Math.abs(x - lastxInConsult));
		else if ((x - lastxInConsult) < 0 && (y - lastyInConsult) > 0)// 2
			theta = -Math.atan(Math.abs(x - lastxInConsult)
					/ Math.abs(y - lastyInConsult));
		else
			// 3
			theta = -((Math.PI / 2) + Math.atan(Math.abs(y - lastyInConsult)
					/ Math.abs(x - lastxInConsult)));
		Log.d("getThetaInConsult", lastxInConsult + "-" + x + "  "
				+ lastyInConsult + "-" + y);
		lastxInConsult = x;
		lastyInConsult = y;
		if (Double.compare(theta, Double.NaN) == 0) {
			System.out.println();
		}
		return theta;
	}

	public static double getThetaInCOrrect(double x, double y) {
		double theta;
		// System.out.println("与X夹角"
		// + Math.toDegrees(Math.atan(Math.abs(y - lastyIngetTheta)
		// / Math.abs(x - lastxIngetTheta))));
		if ((x - lastxInCorrect) > 0 && (y - lastyInCorrect) > 0) {// 1
			theta = Math.atan(Math.abs(x - lastxInCorrect)
					/ Math.abs(y - lastyInCorrect));
			// theta = Math.toDegrees(theta);
		} else if ((x - lastxInCorrect) > 0 && (y - lastyInCorrect) < 0)// 4
			theta = (Math.PI / 2)
					+ Math.atan(Math.abs(y - lastyInCorrect)
							/ Math.abs(x - lastxInCorrect));
		else if ((x - lastxInCorrect) < 0 && (y - lastyInCorrect) > 0)// 2
			theta = -Math.atan(Math.abs(x - lastxInCorrect)
					/ Math.abs(y - lastyInCorrect));
		else
			// 3
			theta = -((Math.PI / 2) + Math.atan(Math.abs(y - lastyInCorrect)
					/ Math.abs(x - lastxInCorrect)));
		Log.d("getThetaInCOrrect", lastxInCorrect + "-" + x + "  "
				+ lastyInCorrect + "-" + y);
		lastxInCorrect = x;
		lastyInCorrect = y;
		if (Double.compare(theta, Double.NaN) == 0) {
			System.out.println();
		}
		return theta;
	}
}

class AtomLine implements Comparable<AtomLine>, Cloneable {

	public AtomLine() {
		super();
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public double getDirection() {
		return directionAVG;
	}

	public void setDirection(double direction) {
		this.directionAVG = direction;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public String toString() {
		return "AtomLine [length=" + length + ", directionAVG=" + directionAVG
				+ ", consultList=" + consultList + "]";
	}

	private int length;

	public AtomLine(int length, double directionAVG,
			ArrayList<Double> consultList) {
		super();
		this.length = length;
		this.directionAVG = directionAVG;
		this.consultList = consultList;
	}

	private double directionAVG;
	private ArrayList<Double> consultList = new ArrayList<Double>();

	public ArrayList<Double> getConsultList() {
		return consultList;
	}

	public void setConsultList(ArrayList<Double> consultList) {
		this.consultList = consultList;
	}

	/*
	 * 降序排序
	 */
	public int compareTo(AtomLine o) {
		if (o != null) {
			if (this.getLength() > o.getLength()) {

				return -1;
			} else if (this.getLength() == o.getLength()) {

				return 0;
			}else {
				return 1;
			}

		}
		return 1;
	}

}

class CorrectAndConsultDirection implements
		Comparable<CorrectAndConsultDirection> {
 

	public double getABSdeltaCorrectAndConsult() {
		return ABSdeltaCorrectAndConsult;
	}

	public void setABSdeltaCorrectAndConsult(double aBSdeltaCorrectAndConsult) {
		ABSdeltaCorrectAndConsult = aBSdeltaCorrectAndConsult;
	}

	public double getCorrectDirection() {
		return correctDirection;
	}

	public void setCorrectDirection(double correctDirection) {
		this.correctDirection = correctDirection;
	}

	public double getConsultDirection() {
		return consultDirection;
	}

	public void setConsultDirection(double consultDirection) {
		this.consultDirection = consultDirection;
	}

	public double getCorrectMinusConsult() {
		return correctMinusConsult;
	}

	public void setCorrectMinusConsult(double correctMinusConsult) {
		this.correctMinusConsult = correctMinusConsult;
	}

	@Override
	public String toString() {
		return "CorrectAndConsultDirection [ABSdeltaCorrectAndConsult="
				+ ABSdeltaCorrectAndConsult + ", correctDirection="
				+ correctDirection + ", consultDirection=" + consultDirection
				+ ", correctMinusConsult=" + correctMinusConsult + "]";
	}

	double ABSdeltaCorrectAndConsult;
	double correctDirection;
	double consultDirection;
	double correctMinusConsult;

	@Override
	public int compareTo(CorrectAndConsultDirection another) {
		if (another != null) {
			if (this.getABSdeltaCorrectAndConsult() > another
					.getABSdeltaCorrectAndConsult()) {

				return 1;
			} else if (this.getABSdeltaCorrectAndConsult() == another
					.getABSdeltaCorrectAndConsult()) {

				return 0;
			}else {
				return -1;
			}

		}
		return 1;
	}

}
