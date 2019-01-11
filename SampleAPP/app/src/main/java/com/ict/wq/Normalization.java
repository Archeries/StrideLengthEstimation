package com.ict.wq;

import java.util.Random;

public class Normalization {
	static double normalizationFactor = 45;
	static double lastDirection = 65;

	public static double doNormalization(double direction) {
		if (lastDirection == 100000) {// 鍒濆瑙掑害锛堢涓�涓�间笉鍋氬綊涓�鍖栧鐞嗭級
			return direction;
		}
		double tempDirection = direction;
		if (direction < lastDirection - 180) {
			direction += 360;
		}
		if (direction > lastDirection + 180) {
			direction -= 360;
		}
 
		if ((lastDirection - normalizationFactor) <= direction
				&& direction < (lastDirection + normalizationFactor)) {
			return lastDirection;
		} else if ((lastDirection + 90 - normalizationFactor) <= direction
				&& direction < (lastDirection + 90 + normalizationFactor)) {
			return lastDirection + 90;
		} else if ((lastDirection - 90 - normalizationFactor) <= direction
				&& direction < (lastDirection - 90 + normalizationFactor)) {
			return lastDirection - 90;
		} else if ((lastDirection - 180 - normalizationFactor) <= direction
				&& direction < (lastDirection - 180 + normalizationFactor)) {
			if ((lastDirection - 180) < -180) {
				return (lastDirection - 180) + 360;
			} else {
				return lastDirection - 180;
			}
		} else if ((lastDirection + 180 - normalizationFactor) <= direction
				&& direction < (lastDirection + 180 + normalizationFactor)) {
			if ((lastDirection + 180) > 180) {
				return (lastDirection + 180) - 360;
			} else {

				return lastDirection + 180;
			}
		}
		else{
			return direction;
		}

	}

	public static void main(String[] args) {
//		 int max=180;
//		 int min=-180;
//		 Random random = new Random();
//		
//		 for (int i = 0; i < 50; i++) {
//		 int direction = random.nextInt(max)%(max-min+1) + 0;
//		 double result=doNormalization(direction);
//		 System.out.println("lastDirection"+lastDirection+"direction"+direction+"  "+result);;
//		 lastDirection=result;
//		 }
		System.out.println(doNormalization(-135));
	}
}
