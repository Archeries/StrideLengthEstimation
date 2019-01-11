package com.ict.wq;

import java.io.Serializable;

public class Sample implements Serializable {
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return super.equals(o);
	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}
	public double getCurrxOrigin() {
		return currxOrigin;
	}
	public void setCurrxOrigin(double currxOrigin) {
		this.currxOrigin = currxOrigin;
	}
	public double getCurryOrigin() {
		return curryOrigin;
	}
	public void setCurryOrigin(double curryOrigin) {
		this.curryOrigin = curryOrigin;
	}
	public double getCurrzOrigin() {
		return currzOrigin;
	}
	public void setCurrzOrigin(double currzOrigin) {
		this.currzOrigin = currzOrigin;
	}
	public double getMagXOrigin() {
		return magXOrigin;
	}
	public void setMagXOrigin(double magXOrigin) {
		this.magXOrigin = magXOrigin;
	}
	public double getMagYOrigin() {
		return magYOrigin;
	}
	public void setMagYOrigin(double magYOrigin) {
		this.magYOrigin = magYOrigin;
	}
	public double getMagZOrigin() {
		return magZOrigin;
	}
	public void setMagZOrigin(double magZOrigin) {
		this.magZOrigin = magZOrigin;
	}
	public double getMagneticValue() {
		return magneticValue;
	}
	public void setMagneticValue(double magneticValue) {
		this.magneticValue = magneticValue;
	}
	public double getMagXFlat() {
		return magXFlat;
	}
	public void setMagXFlat(double magXFlat) {
		this.magXFlat = magXFlat;
	}
	public double getMagYFlat() {
		return magYFlat;
	}
	public void setMagYFlat(double magYFlat) {
		this.magYFlat = magYFlat;
	}
	public double getMagZFlat() {
		return magZFlat;
	}
	public void setMagZFlat(double magZFlat) {
		this.magZFlat = magZFlat;
	}
	public double getOrientationNewX() {
		return orientationNewX;
	}
	public void setOrientationNewX(double orientationNewX) {
		this.orientationNewX = orientationNewX;
	}
	public double getOrientationNewY() {
		return orientationNewY;
	}
	public void setOrientationNewY(double orientationNewY) {
		this.orientationNewY = orientationNewY;
	}
	public double getOrientationNewZ() {
		return orientationNewZ;
	}
	public void setOrientationNewZ(double orientationNewZ) {
		this.orientationNewZ = orientationNewZ;
	}
	public double getOrientationOldX() {
		return orientationOldX;
	}
	public void setOrientationOldX(double orientationOldX) {
		this.orientationOldX = orientationOldX;
	}
	public double getOrientationOldY() {
		return orientationOldY;
	}
	public void setOrientationOldY(double orientationOldY) {
		this.orientationOldY = orientationOldY;
	}
	public double getOrientationOldZ() {
		return orientationOldZ;
	}
	public void setOrientationOldZ(double orientationOldZ) {
		this.orientationOldZ = orientationOldZ;
	}
	public double getOrientationAccuracyX() {
		return orientationAccuracyX;
	}
	public void setOrientationAccuracyX(double orientationAccuracyX) {
		this.orientationAccuracyX = orientationAccuracyX;
	}
	public double getOrientationAccuracyY() {
		return orientationAccuracyY;
	}
	public void setOrientationAccuracyY(double orientationAccuracyY) {
		this.orientationAccuracyY = orientationAccuracyY;
	}
	public double getOrientationAccuracyZ() {
		return orientationAccuracyZ;
	}
	public void setOrientationAccuracyZ(double orientationAccuracyZ) {
		this.orientationAccuracyZ = orientationAccuracyZ;
	}
	public double getGravityX() {
		return gravityX;
	}
	public void setGravityX(double gravityX) {
		this.gravityX = gravityX;
	}
	public double getGravityY() {
		return gravityY;
	}
	public void setGravityY(double gravityY) {
		this.gravityY = gravityY;
	}
	public double getGravityZ() {
		return gravityZ;
	}
	public void setGravityZ(double gravityZ) {
		this.gravityZ = gravityZ;
	}
	public double getAccX() {
		return accX;
	}
	public void setAccX(double accX) {
		this.accX = accX;
	}
	public double getAccY() {
		return accY;
	}
	public void setAccY(double accY) {
		this.accY = accY;
	}
	public double getAccZ() {
		return accZ;
	}
	public void setAccZ(double accZ) {
		this.accZ = accZ;
	}
	public double getGyroX() {
		return gyroX;
	}
	public void setGyroX(double gyroX) {
		this.gyroX = gyroX;
	}
	public double getGyroY() {
		return gyroY;
	}
	public void setGyroY(double gyroY) {
		this.gyroY = gyroY;
	}
	public double getGyroZ() {
		return gyroZ;
	}
	public void setGyroZ(double gyroZ) {
		this.gyroZ = gyroZ;
	}
	public boolean isStair() {
		return isStair;
	}
	public void setStair(boolean isStair) {
		this.isStair = isStair;
	}
	public boolean isGPS() {
		return isGPS;
	}
	public void setGPS(boolean isGPS) {
		this.isGPS = isGPS;
	}
	public double getMagH() {
		return MagH;
	}
	public void setMagH(double magH) {
		MagH = magH;
	}
	public double getMagV() {
		return MagV;
	}
	public void setMagV(double magV) {
		MagV = magV;
	}
	public double getSpectrumH() {
		return spectrumH;
	}
	public void setSpectrumH(double spectrumH) {
		this.spectrumH = spectrumH;
	}
	public double getSpectrumV() {
		return spectrumV;
	}
	public void setSpectrumV(double spectrumV) {
		this.spectrumV = spectrumV;
	}

 


	public double getCurrxCorrect() {
		return currxCorrect;
	}
	public void setCurrxCorrect(double currxCorrect) {
		this.currxCorrect = currxCorrect;
	}
	public double getCurryCorrect() {
		return curryCorrect;
	}
	public void setCurryCorrect(double curryCorrect) {
		this.curryCorrect = curryCorrect;
	}
	public double getCurrzCorrect() {
		return currzCorrect;
	}
	public void setCurrzCorrect(double currzCorrect) {
		this.currzCorrect = currzCorrect;
	}
 
	public double getCurrxConsult() {
		return currxConsult;
	}
	public void setCurrxConsult(double currxConsult) {
		this.currxConsult = currxConsult;
	}
	public double getCurryConsult() {
		return curryConsult;
	}
	public void setCurryConsult(double curryConsult) {
		this.curryConsult = curryConsult;
	}
	public double getCurrzConsult() {
		return currzConsult;
	}
	public void setCurrzConsult(double currzConsult) {
		this.currzConsult = currzConsult;
	}
	private double currxOrigin;
	@Override
	public String toString() {
		return "Sample [currxOrigin=" + currxOrigin + ", curryOrigin="
				+ curryOrigin + ", stepLength=" + stepLength + ", currzOrigin="
				+ currzOrigin + ", isStair=" + isStair + ", isElevator="
				+ isElevator + ", ThetaOrigin=" + ThetaOrigin
				+ ", currxConsult=" + currxConsult + ", curryConsult="
				+ curryConsult + ", currzConsult=" + currzConsult
				+ ", currxCorrect=" + currxCorrect + ", curryCorrect="
				+ curryCorrect + ", currzCorrect=" + currzCorrect
				+ ", magXFlat=" + magXFlat + ", magYFlat=" + magYFlat
				+ ", magZFlat=" + magZFlat + ", spectrumXFlat=" + spectrumXFlat
				+ ", spectrumYFlat=" + spectrumYFlat + ", spectrumZFlat="
				+ spectrumZFlat + ", ioGpgsv=" + ioGpgsv + ", magXOrigin="
				+ magXOrigin + ", magYOrigin=" + magYOrigin + ", magZOrigin="
				+ magZOrigin + ", magneticValue=" + magneticValue
				+ ", orientationNewX=" + orientationNewX + ", orientationNewY="
				+ orientationNewY + ", orientationNewZ=" + orientationNewZ
				+ ", orientationOldX=" + orientationOldX + ", orientationOldY="
				+ orientationOldY + ", orientationOldZ=" + orientationOldZ
				+ ", orientationAccuracyX=" + orientationAccuracyX
				+ ", orientationAccuracyY=" + orientationAccuracyY
				+ ", orientationAccuracyZ=" + orientationAccuracyZ
				+ ", gravityX=" + gravityX + ", gravityY=" + gravityY
				+ ", gravityZ=" + gravityZ + ", accX=" + accX + ", accY="
				+ accY + ", accZ=" + accZ + ", gyroX=" + gyroX + ", gyroY="
				+ gyroY + ", gyroZ=" + gyroZ + ", height=" + height
				+ ", light=" + light + ", isGPS=" + isGPS + ", MagH=" + MagH
				+ ", MagV=" + MagV + ", spectrumH=" + spectrumH
				+ ", spectrumV=" + spectrumV + ", ioLight=" + ioLight
				+ ", indoorProbability=" + indoorProbability + "]";
	}
	private double curryOrigin;
	private double stepLength;
	public double getStepLength() {
		return stepLength;
	}
	public void setStepLength(double stepLength) {
		this.stepLength = stepLength;
	}
	public boolean isElevator() {
		return isElevator;
	}
	public void setElevator(boolean isElevator) {
		this.isElevator = isElevator;
	}
	private double currzOrigin = 0;
	private boolean isStair;
	private boolean isElevator;
	private double ThetaOrigin;
	private double currxConsult;
	private double curryConsult;
	private double currzConsult = 0;
	private double currxCorrect;
	private double curryCorrect;
	private double currzCorrect = 0;
	private double magXFlat;
	private double magYFlat;
	private double magZFlat;
	private double spectrumXFlat;
	private double spectrumYFlat;
	private double spectrumZFlat;
	private double  ioGpgsv;
	private double magXOrigin;
	private double magYOrigin;
	private double magZOrigin;
	private double magneticValue;

	private double magXFlatdecc;
	private double magYFlatdecc;
	private double magZFlatdecc;
	private double orientationNewX;
	private double orientationNewY;
	private double orientationNewZ;
	private double orientationOldX;
	private double orientationOldY;
	private double orientationOldZ;
	private double orientationAccuracyX;
	private double orientationAccuracyY;
	private double orientationAccuracyZ;
	private double gravityX;
	private double gravityY;
	private double gravityZ;
	public double getMagXFlatdecc() {
		return magXFlatdecc;
	}
	public void setMagXFlatdecc(double magXFlatdecc) {
		this.magXFlatdecc = magXFlatdecc;
	}
	public double getMagYFlatdecc() {
		return magYFlatdecc;
	}
	public void setMagYFlatdecc(double magYFlatdecc) {
		this.magYFlatdecc = magYFlatdecc;
	}
	public double getMagZFlatdecc() {
		return magZFlatdecc;
	}
	public void setMagZFlatdecc(double magZFlatdecc) {
		this.magZFlatdecc = magZFlatdecc;
	}
	private double accX;
	private double accY;
	private double accZ;
	private double gyroX;
	private double gyroY;
	private double gyroZ;
	private double height;
	private double light;
	public double getThetaOrigin() {
		return ThetaOrigin;
	}
	public void setThetaOrigin(double thetaOrigin) {
		ThetaOrigin = thetaOrigin;
	}
	private boolean isGPS;
	private double MagH;
	private double MagV;
	private double spectrumH;
	private double spectrumV;
	private double  ioLight;
	
	private double  indoorProbability;
	
	
	public double getIoLight() {
		return ioLight;
	}
	public void setIoLight(double ioLight) {
		this.ioLight = ioLight;
	}
	public double getIoGpgsv() {
		return ioGpgsv;
	}
	public void setIoGpgsv(double ioGpgsv) {
		this.ioGpgsv = ioGpgsv;
	}
	
	public double getIndoorProbability() {
		return indoorProbability;
	}
	public void setIndoorProbability(double indoorProbability) {
		this.indoorProbability = indoorProbability;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	public double getLight() {
		return light;
	}
	public void setLight(double light) {
		this.light = light;
	}
	
	
	public double getSpectrumXFlat() {
		return spectrumXFlat;
	}
	public void setSpectrumXFlat(double spectrumXFlat) {
		this.spectrumXFlat = spectrumXFlat;
	}
	public double getSpectrumYFlat() {
		return spectrumYFlat;
	}
	public void setSpectrumYFlat(double spectrumYFlat) {
		this.spectrumYFlat = spectrumYFlat;
	}
	public double getSpectrumZFlat() {
		return spectrumZFlat;
	}
	public void setSpectrumZFlat(double spectrumZFlat) {
		this.spectrumZFlat = spectrumZFlat;
	}



}
