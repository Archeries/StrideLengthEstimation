package com.ict.wq;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import com.amap.api.maps.AMap;
import com.ict.gyroscopecalibrate.MeanFilterSmoothing;
import com.wzg.dingwei.DataCenter;

public class MagneticSampleTimerReview {

	private Context mContext;

	private Handler mhandler;
	private AMap aMap;
	private DataAdaptor dataAdaptor = null;

	public MagneticSampleTimerReview(Context ctx, Handler mHandler, AMap aMap) {
		mContext = ctx;
		this.mhandler = mHandler;
		this.aMap = aMap;
		dataAdaptor = new DataAdaptor(ctx, mHandler, aMap);
	}

	public void start() {

		new Thread(new Runnable() {
			String sdcardpath = (Environment.getExternalStorageDirectory())
					.getAbsolutePath();
			final String filepath = sdcardpath
					+ "//Sensordata//MagneticSamplerTimer20180904175019.txt";
			BufferedReader br;
			double lastx = 0;
			double lasty = 0;
			double lastz = 0;
			private boolean reFlat=true;

			@Override
			public void run() {
				try {
					br = new BufferedReader(new FileReader(filepath));
					String line;
					MeanFilterSmoothing meanFilterOrient = new MeanFilterSmoothing();
					meanFilterOrient.setTimeConstant(1);
					
					
					MeanFilterSmoothing meanFilterGravity = new MeanFilterSmoothing();
					meanFilterGravity.setTimeConstant(1);
					while ((line = br.readLine()) != null) {
						double currx =0;
						double curry = 0;
						double currz =0;
						if (line.split("currx=").length==1) {
							  currx = Double.parseDouble(line.split("currxOrigin=")[1]
									.split(",")[0]);
							  curry = Double.parseDouble(line.split("curryOrigin=")[1]
									.split(",")[0]);
							  currz = Double.parseDouble(line.split("currzOrigin=")[1]
									.split(",")[0]);
							
						}else {
							  currx = Double.parseDouble(line.split("currx=")[1]
									.split(",")[0]);
							  curry = Double.parseDouble(line.split("curry=")[1]
									.split(",")[0]);
							  currz = Double.parseDouble(line.split("currz=")[1]
									.split(",")[0]);
						}
						DataCenter.originX = (float) currx;
						DataCenter.originy = (float) curry;
						DataCenter.originz = (float) currz;
						if (currx - lastx > 0.1 || curry - lasty > 0.1) {
							// System.out.println("MagneticSampleTimerReview"+currx);
//							try {
//								Thread.sleep(20);
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
						}

						double magXOrigin = Double.parseDouble(line
								.split("magXOrigin=")[1].split(",")[0]);
						double magYOrigin = Double.parseDouble(line
								.split("magYOrigin=")[1].split(",")[0]);
						double magZOrigin = Double.parseDouble(line
								.split("magZOrigin=")[1].split(",")[0]);
						
						double orientationNewX = Double.parseDouble(line
								.split("orientationNewX=")[1].split(",")[0]);
						double orientationNewY = Double.parseDouble(line
								.split("orientationNewY=")[1].split(",")[0]);
						double orientationNewZ = Double.parseDouble(line
								.split("orientationNewZ=")[1].split(",")[0]);
						double orientationOldX = Double.parseDouble(line
								.split("orientationOldX=")[1].split(",")[0]);
						double orientationOldY = Double.parseDouble(line
								.split("orientationOldY=")[1].split(",")[0]);
						double orientationOldZ = Double.parseDouble(line
								.split("orientationOldZ=")[1].split(",")[0]);
//						double orientationAccuracyX = Double.parseDouble(line
//								.split("orientationAccuracyX=")[1].split(",")[0]);
//						double orientationAccuracyY = Double.parseDouble(line
//								.split("orientationAccuracyY=")[1].split(",")[0]);
//						double orientationAccuracyZ = Double.parseDouble(line
//								.split("orientationAccuracyZ=")[1].split(",")[0]);
						double gravityX = Double.parseDouble(line
								.split("gravityX=")[1].split(",")[0]);
						double gravityY = Double.parseDouble(line
								.split("gravityY=")[1].split(",")[0]);
						double gravityZ = Double.parseDouble(line
								.split("gravityX=")[1].split(",")[0]);
//						double accX = Double.parseDouble(line.split("accX=")[1]
//								.split(",")[0]);
//						double accY = Double.parseDouble(line.split("accY=")[1]
//								.split(",")[0]);
//						double accZ = Double.parseDouble(line.split("accZ=")[1]
//								.split(",")[0]);
//						double gyroX = Double.parseDouble(line.split("gyroX=")[1]
//								.split(",")[0]);
//						double gyroY = Double.parseDouble(line.split("gyroY=")[1]
//								.split(",")[0]);
//						double gyroZ = Double.parseDouble(line.split("gyroZ=")[1]
//								.split(",")[0]);
						double height=0;
						double light=0;
						double ioLight=0;
						double ioGpgsv=0;
						if (line.split("height=").length>1) {
						  height = Double.parseDouble(line.split("height=")[1]
								.split(",")[0]);
						}
						if (line.split("light=").length>1) {
						  light = Double.parseDouble(line.split("light=")[1]
								.split(",")[0]);
						}
						if (line.split("ioLight=").length>1) {
							ioLight = Double.parseDouble(line.split("ioLight=")[1]
									.split(",")[0]);
						}
						if (line.split("ioGpgsv=").length>1) {
							ioGpgsv = Double.parseDouble(line.split("ioGpgsv=")[1]
									.split(",")[0]);
						}else {
							ioGpgsv=-1;//表示数据中没有室内外信息
						}
						double magXFlat = 0;
						double magYFlat =0;
						double magZFlat = 0;
						double magV =0;
						double magH = 0;
						if (reFlat) {
							double magx = magYOrigin;
							double magy = magXOrigin;
							double magz = -magZOrigin;
							double yaw = orientationOldX;//之前的做法，可能有错？
							double pith = -orientationOldY;
							double roll = -orientationOldZ;
//							double yaw = -orientationOldX;//是否改成这样？2016年5月8日18:36:47
//							double pith = -orientationOldY;
//							double roll = orientationOldZ;
							
//							float[] Orient = new float[] { (float) yaw, (float) pith, (float) roll };
//
//							Orient = meanFilterOrient.addSamples(Orient);
//							double[] Mag = RotaingMagToFlat.deRotaingMagToFlat(magx, magy, magz, Orient[0], Orient[1],
//									Orient[2]);
							double[] Mag = RotaingMagToFlat.deRotaingMagToFlat(magx, magy, magz, yaw, pith,
									roll);
							 magXFlat = Mag[0];
							 magYFlat = Mag[1];
							 magZFlat = Mag[2];
							 
								double gx = -gravityY;
								double gy = -gravityX;
								double gz = gravityZ;
								float[] gravity = new float[] { (float) gx, (float) gy, (float) gz };
								gravity=meanFilterGravity.addSamples(gravity);
								 magH=getMagH(magx, magy, magz, gravity[0], gravity[1], gravity[2]);
								 magV=getMagV(magx, magy, magz, gravity[0], gravity[1], gravity[2]);
						}else {
							
							 magXFlat = Double.parseDouble(line
									.split("magXFlat=")[1].split(",")[0]);
							 magYFlat = Double.parseDouble(line
									.split("magYFlat=")[1].split(",")[0]);
							 magZFlat = Double.parseDouble(line
									.split("magZFlat=")[1].split(",")[0]);
						}
						
			 
						Sample sample = new Sample();
						sample.setCurrxOrigin(currx);
 
						sample.setCurryOrigin(curry);
						sample.setCurrzOrigin(currz);
						sample.setMagXOrigin(magXOrigin);
						sample.setMagYOrigin(magYOrigin);
						sample.setMagZOrigin(magZOrigin);
						sample.setMagXFlat(magXFlat);
						sample.setMagYFlat(magYFlat);
						sample.setMagZFlat(magZFlat);
						sample.setMagH(magH);
						sample.setMagV(magV);
//						sample.setOrientationNewX(orientationNewX);
//						sample.setOrientationNewY(orientationNewY);
//						sample.setOrientationNewZ(orientationNewZ);
						sample.setOrientationOldX(orientationOldX);
						sample.setOrientationOldY(orientationOldY);
						sample.setOrientationOldZ(orientationOldZ);
//						sample.setOrientationAccuracyX(orientationAccuracyX);
//						sample.setOrientationAccuracyY(orientationAccuracyY);
//						sample.setOrientationAccuracyZ(orientationAccuracyZ);
//						sample.setGravityX(gravityX);
//						sample.setGravityY(gravityY);
//						sample.setGravityZ(gravityZ);
//						sample.setAccX(accX);
//						sample.setAccY(accY);
//						sample.setAccZ(accZ);
						sample.setHeight(height);
						sample.setLight(light);
						sample.setIoGpgsv(ioGpgsv);
						
						String isStair = line.split("isStair=")[1].split(",")[0];

						if (isStair.equals("true")) {
							sample.setStair(true);
							CalculateLocation2.isStair=true;
						} else {
							sample.setStair(false);
							
						}
						String isElevator="false";
						if (line.split("isElevator=").length>1) {
							isElevator = line.split("isElevator=")[1].split(",")[0];
						}
						if (isElevator.equals("true")) {
							sample.setElevator(true);
							CalculateLocation2.isElevator=true;
						} else {
							sample.setElevator(false);
							
						}
						String isGPS = line.split("isGPS=")[1].split(",")[0];
						if (isGPS.equals("true")) {
							sample.setGPS(true);
						} else {
							sample.setGPS(false);
						}

						// //加速度
						// dataAdaptor.dataProcessing(Double.parseDouble(line.split(",")[0]),Double.parseDouble(line.split(",")[1]),Double.parseDouble(line.split(",")[2]),magneticValue,
						// realTimedirection, currx,
						// curry,currz,CalculateLocation2.isStair,Constant.isGPS,Double.parseDouble(line.split(",")[9]),Double.parseDouble(line.split(",")[10]),Double.parseDouble(line.split(",")[11]));
						// 重力
						// dataAdaptor.dataProcessing(Double.parseDouble(line.split(",")[0]),Double.parseDouble(line.split(",")[1]),Double.parseDouble(line.split(",")[2]),magneticValue,
						// realTimedirection, currx,
						// curry,currz,CalculateLocation2.isStair,Constant.isGPS,Double.parseDouble(line.split(",")[15]),Double.parseDouble(line.split(",")[16]),Double.parseDouble(line.split(",")[17]),yaw,pith,roll);
						SensorDataLogFileResample.trace(sample.toString()+"\n");
						dataAdaptor.dataProcessing(sample);
						lastx = currx;
						lasty = curry;
						lastz = currz;
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

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
	public void stop(){
		if (dataAdaptor!=null) {
			dataAdaptor.stop();
			dataAdaptor=null;
		}
	}
}
