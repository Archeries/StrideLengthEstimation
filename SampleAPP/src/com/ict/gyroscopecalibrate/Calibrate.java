package com.ict.gyroscopecalibrate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ict.wq.SensorDataLogFileOrigin;

import Jama.Matrix;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

public class Calibrate   {
	protected Handler handler;
	private Context  mcContext;
	public Calibrate(Handler handler,Context mcContext) {
		super();
		this.handler = handler;
		this.mcContext=mcContext;
		meanFilterMagnetic = new MeanFilterSmoothing();
		meanFilterMagnetic.setTimeConstant((float) 0.5);
		orientation = new GyroscopeOrientation(this.mcContext);
	}

	// Handler for the UI plots so everything plots smoothly

	private Orientation orientation;

	protected Runnable runable;
	private float[] rmGyroscope = new float[9];
	private float[] firstMagnetic = new float[3];
	private float[] finalMagnetic = new float[3];
	private float[] Gyroscope = new float[3];
	private double[] GyroscopeTheta = new double[4];
 
	private static ArrayList<MRMpair> MRMpairList = new ArrayList<MRMpair>();
	private MeanFilterSmoothing meanFilterMagnetic;

	public static final int CALIBRATE=1000000000;
	private boolean ISDONE=false;
	ArrayList<Point> magneticList = new ArrayList<Point>();
	ArrayList<Point> magneticListSmoothing = new ArrayList<Point>();
	public boolean isISDONE() {
		return ISDONE;
	}

	public void setISDONE(boolean iSDONE) {
		ISDONE = iSDONE;
	}

 
	public static Point getNoSmoothingcenter() {
		return NoSmoothingcenter;
	}

	public static void setNoSmoothingcenter(Point noSmoothingcenter) {
		NoSmoothingcenter = noSmoothingcenter;
	}

	public static Point getSmoothingcenter() {
		return Smoothingcenter;
	}

	public static void setSmoothingcenter(Point smoothingcenter) {
		Smoothingcenter = smoothingcenter;
	}

	public static Point getbCenter() {
		return bCenter;
	}

	public static void setbCenter(Point bCenter) {
		Calibrate.bCenter = bCenter;
	}

	private static Point NoSmoothingcenter=null;
	private static Point Smoothingcenter=null;
	private static Point bCenter=null;
	 

	public void onResume() {
		orientation.onResume();	 
	}
	public void calculate() {
		magneticList.clear();
		magneticListSmoothing.clear();
		setISDONE(false);
		reset();
		handler.post(runable);
		
	}
	

	public void onPause() {
		 

		orientation.onPause();

		handler.removeCallbacks(runable);
	}

	private void reset() {

		runable = new Runnable() {
			String bmatrix = " ";
			
			int count = 0;
			private boolean isSmoothingMagnetic = true;
			private String tag = "MainActivity";
			

			@Override
			public void run() {
				if (!ISDONE) {
					
					handler.postDelayed(this, 10);
				}
				count++;
				if (count % 100 == 0) {
					count = 0;
					System.out.println("time" + System.currentTimeMillis());
				}
				rmGyroscope = orientation.getOrientation();
				finalMagnetic = orientation.getMagnetic();
				
				Gyroscope = orientation.getGyroscope();
			 
				double Gyroscopemold = Math
						.sqrt(Math.pow(
								Gyroscope[0],
								2) + Math.pow(Gyroscope[1], 2)
										+ Math.pow(Gyroscope[2], 2));
				Log.d(tag, "" + Gyroscopemold);
				SensorDataLogFileReview.trace(finalMagnetic[0] + ","
						+ finalMagnetic[1] + "," + finalMagnetic[2] + "   "
						+ Gyroscope[0]+","+Gyroscope[1]+","+Gyroscope[2]+" "+Gyroscopemold + "\n");
				GyroscopeTheta = orientation.getGyroscopeTheta();
				double[][] bMatrixArr=orientation.getBMatrix().getArray();
				String bString=bMatrixArr[0][0]+" "+bMatrixArr[1][0]+" "+bMatrixArr[2][0]+"\n";
				System.out.println("bString"+bString);
				Message message2=new Message();
				message2.what=CALIBRATE;
				message2.obj=bString;
				handler.sendMessage(message2);
				

//				if (Gyroscopemold >0.1) {
//
//					// 最小包围球求地磁偏移
//					Point point = new Point(finalMagnetic[0], finalMagnetic[1],
//							finalMagnetic[2]);
//					magneticList.add(point);
//					if (magneticList.size() > 1200) {
//						List<Point> subArrayList = magneticList.subList(200, 1000);
//						NoSmoothingcenter = MinEnclosingBall
//								.getCenterOfMinEnclosingBall(subArrayList);
//						 
//						 
//				//		magneticList.clear();
//					}
//					if (isSmoothingMagnetic) {
//
//						float[] MagneticSmoothing = meanFilterMagnetic
//								.addSamples(finalMagnetic);
//						Point point2 = new Point(MagneticSmoothing[0],
//								MagneticSmoothing[1], MagneticSmoothing[2]);
//						magneticListSmoothing.add(point2);
//						if (magneticListSmoothing.size() > 1200) {
//							List<Point> subArrayList = magneticListSmoothing
//									.subList(200, 1000);
//							Smoothingcenter = MinEnclosingBall
//									.getCenterOfMinEnclosingBall(subArrayList);
//							
//						//	magneticListSmoothing.clear();
//						}
//
//					}
//
//					
//					
//					
//					Matrix firstMagneticmMatrix = new Matrix(
//							flootArrToDoubleArr(firstMagnetic), 3);
//					Matrix finaltMagneticmMatrix = new Matrix(
//							flootArrToDoubleArr(finalMagnetic), 3);
////					Matrix rotationMatrix = new Matrix(
////							flootArrToDoubleArr(rmGyroscope), 3);
//					Matrix rotationMatrix =orientation.getrMatrix();
//
//					MRMpair mpair = new MRMpair(firstMagneticmMatrix,
//							finaltMagneticmMatrix, rotationMatrix);
//					// MRMpairList.add(mpair);
//
//					System.arraycopy(finalMagnetic, 0, firstMagnetic, 0,
//							finalMagnetic.length);
//
//					Matrix bMatrix = getbMatrix(mpair);
//					if (bMatrix != null) {
//						double[][] b = bMatrix.getArray();
//						System.out.println("b matrix " + b[0][0] + " "
//								+ b[1][0] + " " + b[2][0]);
//						bmatrix = "b matrix " + (float) b[0][0] + " "
//								+ (float) b[1][0] + " " + (float) b[2][0];
//					 bCenter=new Point((float) b[0][0], (float) b[1][0],(float) b[2][0]);
//
//					}
//					Message message=handler.obtainMessage();
//					message.what=CALIBRATE;
//					message.obj=" "+magneticList.size();
//				
//					handler.sendMessage(message);
//					if (magneticList.size()>1201) {
//						setISDONE(true);
//						Message message2=new Message();
//						message2.what=CALIBRATE;
//						message2.obj="Done";
//						handler.sendMessage(message2);
//						
//						SensorDataLogFileOrigin.trace("不做平滑的最小包围球,"+NoSmoothingcenter.x+","+NoSmoothingcenter.y+","+NoSmoothingcenter.z+"\n");
//						SensorDataLogFileOrigin.trace("做平滑的最小包围球,"+Smoothingcenter.x+","+Smoothingcenter.y+","+Smoothingcenter.z+"\n");
//						SensorDataLogFileOrigin.trace("Bmatrix,"+bCenter.x+","+bCenter.y+","+bCenter.z+"\n");
//					}
//				}
//				logTextView.setText("R matrix " + Arrays.toString(rmGyroscope)
//						+ "\n Mag " + Arrays.toString(finalMagnetic) + "\n Gyr"
//						+ Arrays.toString(Gyroscope) + "\n Gyroscopemold"
//						+ Gyroscopemold + "\n" + "Gyr degr \n"
//						+ (float) Math.toDegrees(GyroscopeTheta[0]) + " "
//						+ (float) Math.toDegrees(GyroscopeTheta[1]) + " "
//						+ (float) Math.toDegrees(GyroscopeTheta[2]) + "\n"
//						+ bmatrix);

				//
				// updateText();
				// updateGauges();
			}
		};
	}

	public double[] flootArrToDoubleArr(float[] flootarr) {
		double[] doubleArr = new double[flootarr.length];
		for (int i = 0; i < flootarr.length; i++) {
			doubleArr[i] = flootarr[i];
		}
		return doubleArr;

	}

	public static Matrix getHMatrix(List<MRMpair> MRMlist) {

		Matrix IMatrix = Matrix.identity(3, 3);
		Matrix HMatrix = new Matrix(3 * MRMlist.size(), 3);

		for (int i = 0; i < MRMlist.size(); i++) {
			Matrix rotationmMatrix = MRMlist.get(i).getRotationMatrix();
			Matrix iminusRMatrix = IMatrix.minus(rotationmMatrix);

			HMatrix.setMatrix(3 * i, 3 * i + 2, 0, 2, iminusRMatrix);
		}
		return HMatrix;

	}

	public static Matrix getPMatrix(Matrix H) {
		Matrix PMatrix = ((H.transpose()).times(H)).inverse();
		return PMatrix;
	}

	public static Matrix getSMatrix(List<MRMpair>  MRMlist) {
		Matrix SMatrix = new Matrix(3 * MRMlist.size(), 1);

		for (int i = 0; i < MRMlist.size(); i++) {
			Matrix finalMatrix = MRMlist.get(i).getFinalMagneticmMatrix();
			Matrix firstMatrix = MRMlist.get(i).getFirstMagneticmMatrix();
			Matrix rotationMatrix = MRMlist.get(i).getRotationMatrix();
			Matrix RmultiMMatrix = rotationMatrix.times(firstMatrix);
			Matrix MminusRMMatrix = finalMatrix.minus(RmultiMMatrix);

			SMatrix.setMatrix(3 * i, 3 * i + 2, 0, 0, MminusRMMatrix);
		}
		return SMatrix;
	}

	public  static Matrix getbMatrix(MRMpair mrMpair) {
		double temp = mrMpair.getFinalMagneticmMatrix().get(0, 0);
		if (temp != 0) {

			MRMpairList.add(mrMpair);
		}
		if (MRMpairList.size() > 80) {
            List<MRMpair> mrMpairs=MRMpairList.subList(1, 70);
			Matrix hMatrix = getHMatrix(mrMpairs);
			Matrix pMatrix = getPMatrix(hMatrix);
			Matrix sMatrix = getSMatrix(mrMpairs);
			Matrix bMatrix = (pMatrix.times(hMatrix.transpose()))
					.times(sMatrix);
			UpdateBMatrix updateBMatrix = new UpdateBMatrix(pMatrix, bMatrix);
			bMatrix = updateBMatrix.doUpdateB(mrMpairs.get(50));
			mrMpairs.remove(0);
			return bMatrix;
		} else {

			return null;
		}
	}

}
