package com.ict.wq;

import java.util.ArrayList;

public class DeepCopy {
	// 序列深拷贝
		public static ArrayList<Sample> SampleListCopy(ArrayList<Sample> SampleList) {
			ArrayList<Sample> SampleListClone = new ArrayList<Sample>();
			for (int i = 0; i < SampleList.size(); i++) {
				Sample sampleDist = new Sample();
				Sample sampleSrc = SampleList.get(i);
				sampleDist.setCurrxOrigin(sampleSrc.getCurrxOrigin());
				sampleDist.setCurryOrigin(sampleSrc.getCurryOrigin());
				sampleDist.setCurrzOrigin(sampleSrc.getCurrzOrigin());
				sampleDist.setCurrxCorrect(sampleSrc.getCurrxCorrect());
				sampleDist.setCurryCorrect(sampleSrc.getCurryCorrect());
				sampleDist.setCurrzCorrect(sampleSrc.getCurrzCorrect());
				sampleDist.setMagXOrigin(sampleSrc.getMagXOrigin());
				sampleDist.setMagYOrigin(sampleSrc.getMagYOrigin());
				sampleDist.setMagZOrigin(sampleSrc.getMagZOrigin());
				sampleDist.setMagXFlat(sampleSrc.getMagXFlat());
				sampleDist.setMagYFlat(sampleSrc.getMagYFlat());
				sampleDist.setMagZFlat(sampleSrc.getMagZFlat());
				sampleDist.setOrientationAccuracyX(sampleSrc
						.getOrientationAccuracyX());
				sampleDist.setOrientationAccuracyY(sampleSrc
						.getOrientationAccuracyY());
				sampleDist.setOrientationAccuracyZ(sampleSrc
						.getOrientationAccuracyZ());
				sampleDist.setOrientationNewX(sampleSrc.getOrientationNewX());
				sampleDist.setOrientationNewY(sampleSrc.getOrientationNewY());
				sampleDist.setOrientationNewZ(sampleSrc.getOrientationNewZ());
				sampleDist.setOrientationOldX(sampleSrc.getOrientationOldX());
				sampleDist.setOrientationOldY(sampleSrc.getOrientationOldY());
				sampleDist.setOrientationOldZ(sampleSrc.getOrientationOldZ());
				sampleDist.setGravityX(sampleSrc.getGravityX());
				sampleDist.setGravityY(sampleSrc.getGravityY());
				sampleDist.setGravityZ(sampleSrc.getGravityZ());
				sampleDist.setAccX(sampleSrc.getAccX());
				sampleDist.setAccY(sampleSrc.getAccY());
				sampleDist.setAccZ(sampleSrc.getAccZ());
				sampleDist.setGyroX(sampleSrc.getGyroX());
				sampleDist.setGyroY(sampleSrc.getGyroY());
				sampleDist.setGyroZ(sampleSrc.getGyroZ());
				sampleDist.setGPS(sampleSrc.isGPS());
				sampleDist.setStair(sampleSrc.isStair());
				sampleDist.setMagH(sampleSrc.getMagH());
				sampleDist.setMagV(sampleSrc.getMagV());
				sampleDist.setSpectrumH(sampleSrc.getSpectrumH());
				sampleDist.setSpectrumV(sampleSrc.getSpectrumV());
				sampleDist.setSpectrumXFlat(sampleSrc.getSpectrumXFlat());
				sampleDist.setSpectrumYFlat(sampleSrc.getSpectrumYFlat());
				sampleDist.setSpectrumZFlat(sampleSrc.getSpectrumZFlat());
				SampleListClone.add(sampleDist);
			}
			return SampleListClone;
		}

		// 对象深拷贝
		public static Sample SampleCopy(Sample sampleSrc) {

			Sample sampleDist = new Sample();

			sampleDist.setCurrxOrigin(sampleSrc.getCurrxOrigin());
			sampleDist.setCurryOrigin(sampleSrc.getCurryOrigin());
			sampleDist.setCurrzOrigin(sampleSrc.getCurrzOrigin());
			sampleDist.setCurrxCorrect(sampleSrc.getCurrxCorrect());
			sampleDist.setCurryCorrect(sampleSrc.getCurryCorrect());
			sampleDist.setCurrzCorrect(sampleSrc.getCurrzCorrect());
			sampleDist.setMagXOrigin(sampleSrc.getMagXOrigin());
			sampleDist.setMagYOrigin(sampleSrc.getMagYOrigin());
			sampleDist.setMagZOrigin(sampleSrc.getMagZOrigin());
			sampleDist.setMagXFlat(sampleSrc.getMagXFlat());
			sampleDist.setMagYFlat(sampleSrc.getMagYFlat());
			sampleDist.setMagZFlat(sampleSrc.getMagZFlat());
			sampleDist.setOrientationAccuracyX(sampleSrc.getOrientationAccuracyX());
			sampleDist.setOrientationAccuracyY(sampleSrc.getOrientationAccuracyY());
			sampleDist.setOrientationAccuracyZ(sampleSrc.getOrientationAccuracyZ());
			sampleDist.setOrientationNewX(sampleSrc.getOrientationNewX());
			sampleDist.setOrientationNewY(sampleSrc.getOrientationNewY());
			sampleDist.setOrientationNewZ(sampleSrc.getOrientationNewZ());
			sampleDist.setOrientationOldX(sampleSrc.getOrientationOldX());
			sampleDist.setOrientationOldY(sampleSrc.getOrientationOldY());
			sampleDist.setOrientationOldZ(sampleSrc.getOrientationOldZ());
			sampleDist.setGravityX(sampleSrc.getGravityX());
			sampleDist.setGravityY(sampleSrc.getGravityY());
			sampleDist.setGravityZ(sampleSrc.getGravityZ());
			sampleDist.setAccX(sampleSrc.getAccX());
			sampleDist.setAccY(sampleSrc.getAccY());
			sampleDist.setAccZ(sampleSrc.getAccZ());
			sampleDist.setGyroX(sampleSrc.getGyroX());
			sampleDist.setGyroY(sampleSrc.getGyroY());
			sampleDist.setGyroZ(sampleSrc.getGyroZ());
			sampleDist.setGPS(sampleSrc.isGPS());
			sampleDist.setStair(sampleSrc.isStair());
			sampleDist.setMagH(sampleSrc.getMagH());
			sampleDist.setMagV(sampleSrc.getMagV());
			sampleDist.setSpectrumH(sampleSrc.getSpectrumH());
			sampleDist.setSpectrumV(sampleSrc.getSpectrumV());
			sampleDist.setSpectrumXFlat(sampleSrc.getSpectrumXFlat());
			sampleDist.setSpectrumYFlat(sampleSrc.getSpectrumYFlat());
			sampleDist.setSpectrumZFlat(sampleSrc.getSpectrumZFlat());

			return sampleDist;
		}

}
