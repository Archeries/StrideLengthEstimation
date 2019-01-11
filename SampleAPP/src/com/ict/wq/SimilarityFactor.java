package com.ict.wq;

import java.text.DecimalFormat;

public class SimilarityFactor {


 


 
 



	@Override
	public String toString() {
		DecimalFormat df=new DecimalFormat("##0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
		return "SimilarityFactor [DTWDistance=" + df.format(DTWDistance)
				+ ", matchedLength=" + df.format(matchedLength)
				+ ", currentAccumulatedDistance=" + df.format(currentAccumulatedDistance)
				+ ", matchedAccumulatedDistance=" +df.format( matchedAccumulatedDistance)
				+ ", deltaLength=" +df.format( deltaLength) + ", deltaLengthInRatio="
				+ df.format(deltaLengthInRatio) + ", currentDirection=" + df.format(currentDirection)
				+ ", matchedDirection=" + df.format(matchedDirection)
				+ ", deltaDirection=" +df.format( deltaDirection)
				+ ", originDistanceOfStartPoint=" +df.format( originDistanceOfStartPoint)
				+ ", correctDistanceOfStartPoint="
				+ df.format(correctDistanceOfStartPoint) + ", isReverse=" + isReverse
				+ ", countOfsameSample=" + df.format(countOfsameSample) + ", DTWtime="
				+ df.format(DTWtime) + "]";
	}
	public SimilarityFactor(double dTWDistance, double matchedLength,
			double currentAccumulatedDistance,
			double matchedAccumulatedDistance, double deltaLength,
			double deltaLengthInRatio, double currentDirection,
			double matchedDirection, double deltaDirection,
			double originDistanceOfStartPoint,
			double correctDistanceOfStartPoint, boolean isReverse,
			int countOfsameSample, long dTWtime) {
		super();
		DTWDistance = dTWDistance;
		this.matchedLength = matchedLength;
		this.currentAccumulatedDistance = currentAccumulatedDistance;
		this.matchedAccumulatedDistance = matchedAccumulatedDistance;
		this.deltaLength = deltaLength;
		this.deltaLengthInRatio = deltaLengthInRatio;
		this.currentDirection = currentDirection;
		this.matchedDirection = matchedDirection;
		this.deltaDirection = deltaDirection;
		this.originDistanceOfStartPoint = originDistanceOfStartPoint;
		this.correctDistanceOfStartPoint = correctDistanceOfStartPoint;
		this.isReverse = isReverse;
		this.countOfsameSample = countOfsameSample;
		DTWtime = dTWtime;
	}
	public double getDTWDistance() {
		return DTWDistance;
	}
	public void setDTWDistance(double dTWDistance) {
		DTWDistance = dTWDistance;
	}
	public double getMatchedLength() {
		return matchedLength;
	}
	public void setMatchedLength(double matchedLength) {
		this.matchedLength = matchedLength;
	}
	public double getCurrentAccumulatedDistance() {
		return currentAccumulatedDistance;
	}
	public void setCurrentAccumulatedDistance(double currentAccumulatedDistance) {
		this.currentAccumulatedDistance = currentAccumulatedDistance;
	}
	public double getMatchedAccumulatedDistance() {
		return matchedAccumulatedDistance;
	}
	public void setMatchedAccumulatedDistance(double matchedAccumulatedDistance) {
		this.matchedAccumulatedDistance = matchedAccumulatedDistance;
	}
	public double getDeltaLength() {
		return deltaLength;
	}
	public void setDeltaLength(double deltaLength) {
		this.deltaLength = deltaLength;
	}
	public double getDeltaLengthInRatio() {
		return deltaLengthInRatio;
	}
	public void setDeltaLengthInRatio(double deltaLengthInRatio) {
		this.deltaLengthInRatio = deltaLengthInRatio;
	}
	public double getCurrentDirection() {
		return currentDirection;
	}
	public void setCurrentDirection(double currentDirection) {
		this.currentDirection = currentDirection;
	}
	public double getMatchedDirection() {
		return matchedDirection;
	}
	public void setMatchedDirection(double matchedDirection) {
		this.matchedDirection = matchedDirection;
	}
	public double getDeltaDirection() {
		return deltaDirection;
	}
	public void setDeltaDirection(double deltaDirection) {
		this.deltaDirection = deltaDirection;
	}
	public double getOriginDistanceOfStartPoint() {
		return originDistanceOfStartPoint;
	}
	public void setOriginDistanceOfStartPoint(double originDistanceOfStartPoint) {
		this.originDistanceOfStartPoint = originDistanceOfStartPoint;
	}
	public double getCorrectDistanceOfStartPoint() {
		return correctDistanceOfStartPoint;
	}
	public void setCorrectDistanceOfStartPoint(double correctDistanceOfStartPoint) {
		this.correctDistanceOfStartPoint = correctDistanceOfStartPoint;
	}
	public boolean isReverse() {
		return isReverse;
	}
	public void setReverse(boolean isReverse) {
		this.isReverse = isReverse;
	}
	public int getCountOfsameSample() {
		return countOfsameSample;
	}
	public void setCountOfsameSample(int countOfsameSample) {
		this.countOfsameSample = countOfsameSample;
	}
	public long getDTWtime() {
		return DTWtime;
	}
	public void setDTWtime(long dTWtime) {
		DTWtime = dTWtime;
	}
	private double DTWDistance = 1000000;// DTW距离
	private double matchedLength = 1000000;// 匹配序列的长度
	private double currentAccumulatedDistance = 1000000;// 当前序列每一步的累计步长
	private double matchedAccumulatedDistance = 1000000;// 匹配序列每一步的累计步长
	private double deltaLength = 1000000;// 累计步长之差
	private double deltaLengthInRatio = 1000000;// 步长差除以当前累计步长乘以10（放大10倍）
	private double currentDirection = 1000000;// 当前序列方向平均
	private double matchedDirection = 1000000;// 匹配序列方向平均
	private double deltaDirection = 1000000;// 方向之差
	private double originDistanceOfStartPoint = 1000000;// 匹配序列的起点与上段终点的距离
	private double correctDistanceOfStartPoint = 1000000;// 匹配序列的起点与上段终点的距离
	private boolean isReverse = true;// true 表示反向匹配，否则正向匹配
	private int countOfsameSample=0;
	private long DTWtime=0;
 

}
