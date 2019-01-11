package com.ict.wq;

import java.util.ArrayList;
import java.util.Iterator;

import com.ict.fastDTW.dtw.TimeWarpInfo;
import com.ict.fastDTW.dtw.WarpPath;

public class ResuiltDisplay {

	// alist�൱�ڿ�
	// blist�൱�ڲ���
	public static void displayDtwResuilt2(ArrayList<Sample> DBList,
			ArrayList<Sample> testList, TimeWarpInfo info2) {
		int minJ = info2.getPath().minJ();
		int maxJ = info2.getPath().maxJ();
		WarpPath path = info2.getPath();
		double euclideanDistanceTest = Math.sqrt(Math.pow(
				testList.get(testList.size() - 1).getCurrxOrigin()
						- testList.get(0).getCurrxOrigin(), 2)
				+ Math.pow(testList.get(testList.size() - 1).getCurryOrigin()
						- testList.get(0).getCurryOrigin(), 2));
		double euclideanDistanceMatched = Math.sqrt(Math.pow(DBList.get(maxJ)
				.getCurrxOrigin() - DBList.get(minJ).getCurrxOrigin(), 2)
				+ Math.pow(DBList.get(maxJ).getCurryOrigin()
						- DBList.get(minJ).getCurryOrigin(), 2));

		double deltaLength = Math.abs(euclideanDistanceTest
				- euclideanDistanceMatched);
		double deltaLengthInRatio = (deltaLength / euclideanDistanceTest) * 10;// ���Ȳ�ı�ֵ�Ŵ�ʮ��,���������켣�ڳ����ϵ�������
		SensorDataLogFileDTWresuilt.trace("euclideanDistanceTest,"
				+ euclideanDistanceTest + ",euclideanDistanceMatched,"
				+ euclideanDistanceMatched + ",deltaLength," + deltaLength
				+ ",deltaLengthInRatio," + deltaLengthInRatio
				+ ",DTW distance ," + info2.getDistance() + ",matched Length,"
				+ (maxJ - minJ) + ",path" + info2.getPath() + "\n");
		SensorDataLogFileDTWresuilt
				.trace("DBcurrxOrigin,DBcurryOrigin,DBcurrzOrigin,TestcurrxOrigin,TestcurryOrigin,TestcurrzOrigin,"
						+ "DBcurrxConsult,DBcurryConsult,DBcurrzConsult,TestcurrxConsult,TestcurryConsult,TestcurrzConsult,"
						+ "DBcurrxCorrect,DBcurryCorrect,DBcurrzCorrect,TestcurrxCorrect,"
						+ "TestcurryCorrect,TestcurrzCorrect,DBmagXOrigin,DBmagYOrigin,"
						+ "DBmagZOrigin,TestmagXOrigin,TestmagYOrigin,TestmagZOrigin,DBMagMold,TestMagMold,DBmagXFlat,DBmagYFlat,"
						+ "DBmagZFlat,TestmagXFlat,TestmagYFlat,TestmagZFlat,DBMagXSpectrum,DBMagYSpectrum,"
						+ "DBMagZSpectrum,TestMagXSpectrum,TestMagYSpectrum,TestMagZSpectrum,DBmagH,"
						+ "DBmagV,TestMagH,TestmagV,"
						+ "DBDTWmagH,DBDTWmagV,TestDTWMagH,TestDTWmagV,DTWDBmagXFlat,DTWDBmagYFlat,"
						+ "DTWDBmagZFlat,DTWTestmagXFlat,DTWTestmagYFlat,DTWTestmagZFlat,DTWDBMagXSpectrum,"
						+ "DTWDBMagYSpectrum,DTWDBMagZSpectrum,DTWTestMagXSpectrum,DTWTestMagYSpectrum,"
						+ "DTWTestMagZSpectrum,DTWDBMagXdecc,"
						+ "DTWDBMagYdecc,DTWDBMagZdecc,DTWTestMagXdecc,DTWTestMagYdecc,"
						+ "DTWTestMagZdecc" + "\n");
		for (int i = 0; i < Math.max(testList.size(), info2.getPath().size()); i++) {
			if (i < testList.size()) {
				if (minJ + i <= maxJ) {
					SensorDataLogFileDTWresuilt.trace(""
							+ DBList.get(minJ + i).getCurrxOrigin() + ","
							+ DBList.get(minJ + i).getCurryOrigin() + ","
							+ DBList.get(minJ + i).getCurrzOrigin() + ","
							+ testList.get(i).getCurrxOrigin() + ","
							+ testList.get(i).getCurryOrigin() + ","
							+ testList.get(i).getCurrzOrigin() + ","

							+ DBList.get(minJ + i).getCurrxConsult() + ","
							+ DBList.get(minJ + i).getCurryConsult() + ","
							+ DBList.get(minJ + i).getCurrzConsult() + ","
							+ testList.get(i).getCurrxConsult() + ","
							+ testList.get(i).getCurryConsult() + ","
							+ testList.get(i).getCurrzConsult() + ","

							+ DBList.get(minJ + i).getCurrxCorrect() + ","
							+ DBList.get(minJ + i).getCurryCorrect() + ","
							+ DBList.get(minJ + i).getCurrzCorrect() + ","
							+ testList.get(i).getCurrxCorrect() + ","
							+ testList.get(i).getCurryCorrect() + ","
							+ testList.get(i).getCurrzCorrect() + ","

							+ DBList.get(minJ + i).getMagXOrigin() + ","
							+ DBList.get(minJ + i).getMagYOrigin() + ","
							+ DBList.get(minJ + i).getMagZOrigin() + ","
							+ testList.get(i).getMagXOrigin() + ","
							+ testList.get(i).getMagYOrigin() + ","
							+ testList.get(i).getMagZOrigin() + ","
							+ DBList.get(minJ + i).getMagneticValue() + ","
							+ testList.get(i).getMagneticValue() + ","

							+ DBList.get(minJ + i).getMagXFlat() + ","
							+ DBList.get(minJ + i).getMagYFlat() + ","
							+ DBList.get(minJ + i).getMagZFlat() + ","
							+ testList.get(i).getMagXFlat() + ","
							+ testList.get(i).getMagYFlat() + ","
							+ testList.get(i).getMagZFlat() + ","

							+ DBList.get(minJ + i).getSpectrumXFlat() + ","
							+ DBList.get(minJ + i).getSpectrumYFlat() + ","
							+ DBList.get(minJ + i).getSpectrumZFlat() + ","
							+ testList.get(i).getSpectrumXFlat() + ","
							+ testList.get(i).getSpectrumYFlat() + ","
							+ testList.get(i).getSpectrumZFlat() + ","

							+ DBList.get(minJ + i).getMagH() + ","
							+ DBList.get(minJ + i).getMagV() + ","
							+ testList.get(i).getMagH() + ","
							+ testList.get(i).getMagV() + ",");
				} else {
					SensorDataLogFileDTWresuilt.trace(",,,"
							+ +testList.get(i).getCurrxOrigin() + ","
							+ testList.get(i).getCurryOrigin() + ","
							+ testList.get(i).getCurrzOrigin() + "," + ","
							+ "," + "," + testList.get(i).getCurrxConsult()
							+ "," + testList.get(i).getCurryConsult() + ","
							+ testList.get(i).getCurrzConsult() + "," + ",,,"
							+ testList.get(i).getCurrxCorrect() + ","
							+ testList.get(i).getCurryCorrect() + ","
							+ testList.get(i).getCurrzCorrect() + "," + ",,,"
							+ +testList.get(i).getMagXOrigin() + ","
							+ testList.get(i).getMagYOrigin() + ","
							+ testList.get(i).getMagZOrigin() + "," + ","
							+ testList.get(i).getMagneticValue() + "," + ",,,"

							+ testList.get(i).getMagXFlat() + ","
							+ testList.get(i).getMagYFlat() + ","
							+ testList.get(i).getMagZFlat() + "," + "," + ","
							+ "," + testList.get(i).getSpectrumXFlat() + ","
							+ testList.get(i).getSpectrumYFlat() + ","
							+ testList.get(i).getSpectrumZFlat() + ", ,"
							+ testList.get(i).getMagH() + ", ,"
							+ testList.get(i).getMagV() + ", ");
				}
				SensorDataLogFileDTWresuilt.trace(DBList.get(
						path.get(i).getRow()).getMagH()
						+ ","
						+ DBList.get(path.get(i).getRow()).getMagV()
						+ ","
						+ testList.get(path.get(i).getCol()).getMagH()
						+ ","
						+ testList.get(path.get(i).getCol()).getMagV()
						+ ","
						+ DBList.get(path.get(i).getRow()).getMagXFlat()
						+ ","
						+ DBList.get(path.get(i).getRow()).getMagYFlat()
						+ ","
						+ DBList.get(path.get(i).getRow()).getMagZFlat()
						+ ","
						+ testList.get(path.get(i).getCol()).getMagXFlat()
						+ ","
						+ testList.get(path.get(i).getCol()).getMagYFlat()
						+ ","
						+ testList.get(path.get(i).getCol()).getMagZFlat()
						+ ","
						+ DBList.get(path.get(i).getRow()).getSpectrumXFlat()
						+ ","
						+ DBList.get(path.get(i).getRow()).getSpectrumYFlat()
						+ ","
						+ DBList.get(path.get(i).getRow()).getSpectrumZFlat()
						+ ","
						+ testList.get(path.get(i).getCol()).getSpectrumXFlat()
						+ ","
						+ testList.get(path.get(i).getCol()).getSpectrumYFlat()
						+ ","
						+ testList.get(path.get(i).getCol()).getSpectrumZFlat()
						+ ","
						+ DBList.get(path.get(i).getRow())
								.getMagXFlatdecc()
						+ ","
						+ DBList.get(path.get(i).getRow())
								.getMagYFlatdecc()
						+ ","
						+ DBList.get(path.get(i).getRow())
								.getMagZFlatdecc()
						+ ","
						+ testList.get(path.get(i).getCol())
								.getMagXFlatdecc()
						+ ","
						+ testList.get(path.get(i).getCol())
								.getMagYFlatdecc()
						+ ","
						+ testList.get(path.get(i).getCol())
								.getMagZFlatdecc()
								+ "\n");
			} else {
				SensorDataLogFileDTWresuilt
						.trace(" , , , , , , , , , ,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,"
								+ DBList.get(path.get(i).getRow()).getMagH()
								+ ","
								+ testList.get(path.get(i).getCol()).getMagH()
								+ ","
								+ DBList.get(path.get(i).getRow()).getMagV()
								+ ","
								+ testList.get(path.get(i).getCol()).getMagV()
								+ ","
								+ DBList.get(path.get(i).getRow())
										.getMagXFlat()
								+ ","
								+ DBList.get(path.get(i).getRow())
										.getMagYFlat()
								+ ","
								+ DBList.get(path.get(i).getRow())
										.getMagZFlat()
								+ ","
								+ testList.get(path.get(i).getCol())
										.getMagXFlat()
								+ ","
								+ testList.get(path.get(i).getCol())
										.getMagYFlat()
								+ ","
								+ testList.get(path.get(i).getCol())
										.getMagZFlat()
								+ ","
								+ DBList.get(path.get(i).getRow())
										.getSpectrumXFlat()
								+ ","
								+ DBList.get(path.get(i).getRow())
										.getSpectrumYFlat()
								+ ","
								+ DBList.get(path.get(i).getRow())
										.getSpectrumZFlat()
								+ ","
								+ testList.get(path.get(i).getCol())
										.getSpectrumXFlat()
								+ ","
								+ testList.get(path.get(i).getCol())
										.getSpectrumYFlat()
								+ ","
								+ testList.get(path.get(i).getCol())
										.getSpectrumZFlat() 
										
										+ ","
								+ DBList.get(path.get(i).getRow())
										.getMagXFlatdecc()
								+ ","
								+ DBList.get(path.get(i).getRow())
										.getMagYFlatdecc()
								+ ","
								+ DBList.get(path.get(i).getRow())
										.getMagZFlatdecc()
								+ ","
								+ testList.get(path.get(i).getCol())
										.getMagXFlatdecc()
								+ ","
								+ testList.get(path.get(i).getCol())
										.getMagYFlatdecc()
								+ ","
								+ testList.get(path.get(i).getCol())
										.getMagZFlatdecc() 
										+ "\n");
				
				
			}
		}

	}

	public static void displayDtwResuilt3(ArrayList<Sample> AList,
			ArrayList<Sample> BList,ArrayList<Sample> BListReverse) {

		for (int i = 0; i < Math.min(AList.size(), BList.size()); i++) {
			SensorDataLogFileResample.trace(AList.get(i).toString() + ",,,");
			SensorDataLogFileResample.trace(BList.get(i).toString() + ",,,");
			SensorDataLogFileResample.trace(BListReverse.get(i).toString() + "\n");

		}
		if (AList.size() >= BList.size()) {

			for (int j = Math.min(AList.size(), BList.size()); j < AList
					.size(); j++) {

				SensorDataLogFileResample
						.trace(AList.get(j).toString() + "\n");
			}
		} else {
			for (int j = Math.min(AList.size(), BList.size()); j < BList
					.size(); j++) {
				for (int i = 0; i < 89; i++) {

					SensorDataLogFileResample.trace(",");
				}
				SensorDataLogFileResample.trace("" + ",,,");
	 
				SensorDataLogFileResample.trace(BList.get(j).toString()
						);
				SensorDataLogFileResample.trace("" + ",,,");
				SensorDataLogFileResample.trace(BList.get(j).toString()
						+ "\n");
			}
		}

	}
}
