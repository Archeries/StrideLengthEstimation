package com.ict.wq;

import java.util.ArrayList;

import Jama.Matrix;

public class RotaingMagToFlat {
	/*
	 * 把倾斜态的地磁读数转到水平放置的读数
	 */
	public static double[] deRotaingMagToFlat(double magX, double magY,
			double magZ, double yaw, double pith, double roll) {
		// 角度转弧度
		yaw =   (yaw * Math.PI / 180);
		pith =   (pith * Math.PI / 180);
		roll =   (roll * Math.PI / 180);
		double[] MagToFlat = new double[3];
		Matrix pithMatrix = new Matrix(3, 3);
		pithMatrix.set(0, 0, Math.cos(pith));
		pithMatrix.set(0, 1, 0);
		pithMatrix.set(0, 2, Math.sin(pith));
		pithMatrix.set(1, 0, 0);
		pithMatrix.set(1, 1, 1);
		pithMatrix.set(1, 2, 0);
		pithMatrix.set(2, 0, -Math.sin(pith));
		pithMatrix.set(2, 1, 0);
		pithMatrix.set(2, 2, Math.cos(pith));

		Matrix rollMatrix = new Matrix(3, 3);
		rollMatrix.set(0, 0, 1);
		rollMatrix.set(0, 1, 0);
		rollMatrix.set(0, 2, 0);
		rollMatrix.set(1, 0, 0);
		rollMatrix.set(1, 1, Math.cos(roll));
		rollMatrix.set(1, 2, -Math.sin(roll));
		rollMatrix.set(2, 0, 0);
		rollMatrix.set(2, 1, Math.sin(roll));
		rollMatrix.set(2, 2, Math.cos(roll));

		Matrix originMagMatrix = new Matrix(3, 1);
		originMagMatrix.set(0, 0, magX);
		originMagMatrix.set(1, 0, magY);
		originMagMatrix.set(2, 0, magZ);

		Matrix flatMatrix = new Matrix(3, 1);
		flatMatrix = pithMatrix.times(rollMatrix).times(originMagMatrix);
		for (int i = 0; i < MagToFlat.length; i++) {
			MagToFlat[i] = (float) flatMatrix.get(i, 0);
		}
		return MagToFlat;
	}



	public double getMagV(double magx, double magy, double magz, double Ax,
			double Ay, double Az) {
		double magV = 0;
		magV = (magx * Ax + magy * Ay + magz * Az)
				/ Math.sqrt(Ax * Ax + Ay * Ay + Az * Az);
		return magV;
	}

	public double getMagH(double magx, double magy, double magz, double Ax,
			double Ay, double Az) {
		double magH = 0;
		magH = Math.sqrt((magx * magx + magy * magy + magz * magz)
				- getMagV(magx, magy, magz, Ax, Ay, Az)
				* getMagV(magx, magy, magz, Ax, Ay, Az));
		return magH;
	}
	

	// 去中心化处理
	public static void differenceMagVandMagH(ArrayList<Sample> list) {
		double sumV = 0.0;
		double sumH = 0.0;
		for (int i = 0; i < list.size(); i++) {
			sumV += list.get(i).getMagV();
			sumH += list.get(i).getMagH();
		}
		double averageV = sumV / list.size();
		double averageH = sumH / list.size();
		SensorDataLogFiledecc.trace("averageV " + averageV + "averageH "
				+ averageH);
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setMagV(list.get(i).getMagV() - averageV);
			list.get(i).setMagH(list.get(i).getMagH() - averageH);
		}

	}

	// 差分处理
	public static void decentralizationMagVandMagH2(
			ArrayList<Sample> subSampleList2) {
		for (int i = 0; i < subSampleList2.size() - 1; i++) {
			subSampleList2.get(i).setMagH(
					subSampleList2.get(i + 1).getMagH()
							- subSampleList2.get(i).getMagH());
			subSampleList2.get(i).setMagV(
					subSampleList2.get(i + 1).getMagV()
							- subSampleList2.get(i).getMagV());
		}
		subSampleList2.remove(subSampleList2.size() - 1);

	}
	public double getSpectrum(ArrayList<Double> magArrayList) {
		double Spectrum = 0;
		for (int i = 0; i < magArrayList.size(); i++) {
			Spectrum += Math.pow(magArrayList.get(i), 2);
		}
		return Spectrum;

	}
}
