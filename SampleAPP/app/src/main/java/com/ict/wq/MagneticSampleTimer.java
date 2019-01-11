package com.ict.wq;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import Jama.Matrix;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.location.demo.FireManActivity;
import com.golshadi.orientationSensor.sensors.Orientation;
import com.golshadi.orientationSensor.utils.OrientationSensorInterface;
import com.wzg.dingwei.DataCenter;

public class MagneticSampleTimer    {

	private Context mContext;
	static ArrayList<Double> magList = new ArrayList<Double>();
	static ArrayList<Double> directionList = new ArrayList<Double>();
	Timer timer;
	private Handler mhandler;
	private AMap aMap;
	private DataAdaptor dataAdaptor = null;

	private MagCollectionProvider0603 magCollectionProvider;
	public final static int REALTIMERECORD = 100009;

	// 转弯之后的前5个地磁数据不要
	static int dropNum = 10;
	static boolean dropFlag = false;
	public static ArrayList<Double> magArr1 = new ArrayList<Double>();
	static ArrayList<Double> magArr2 = new ArrayList<Double>();

	private double[] orientationAccuracy = new double[3];
	private double orientationorigin = 0;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
    private Date date = new Date();

	public MagneticSampleTimer(Context ctx, Handler mHandler, AMap aMap) {
		mContext = ctx;
		this.mhandler = mHandler;
		this.aMap = aMap;
		magCollectionProvider = new MagCollectionProvider0603(ctx, mHandler);
		dataAdaptor = new DataAdaptor(ctx, mHandler, aMap);
	}

	private double lastcoorx=0;
	private double lastcoory=0;
	private double lastcoorz=0;
	double stepLength=0;
	double lastStepLength=0;
	int stepcount=0;
	double walkDistance=0;
    DecimalFormat df = new DecimalFormat("00.00");
	public void start() {
		// clear();
		magCollectionProvider.start();
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				double currx = 0;
				double curry = 0;
				double currz = 0;
				// currx = DataCenter.pos[0];
				// curry = DataCenter.pos[1];
				currx = DataCenter.originX;
				curry = DataCenter.originy;
				currz = DataCenter.originz;

				Log.d("AAA",""+currx+" "+lastcoorx+" "+curry+" "+lastcoory+" "+currz+" "+lastcoorz);
				if (Math.abs(currx - lastcoorx) > 0.1 || Math.abs(curry - lastcoory) > 0.1||Math.abs(currz - lastcoorz) > 0.1) {
					Log.d("AAAA",""+Math.abs(currx - lastcoorx));

					 stepLength=Math.sqrt((currx-lastcoorx)*(currx-lastcoorx)+(curry-lastcoory)*(curry-lastcoory));
					Log.d("stepLength",""+stepLength);

					lastcoorx=currx;
					lastcoory=curry;
					lastcoorz=currz;


					//	SensorDataLogFilePedometer.trace(DataCenter.pos[0]+","+DataCenter.pos[1]+","+DataCenter.pos[2]+"\n");
				}

				double magneticValue = magCollectionProvider.getRealTimeMag();
				double heightValue = magCollectionProvider.getRealTimeHeight();
				double lightValue = magCollectionProvider.getRealTimeLight();
				float[] magneticFieldValues = magCollectionProvider
						.getmagneticFieldValues();
				float[] magneticFieldValuesInEarth = magCollectionProvider
						.getmagneticFieldValuesInEarth();
				float[] magneticFieldValuesInEarth2 = magCollectionProvider
						.getmagneticFieldValuesInEarth2();
				// String[] mag = magneticVictorValue.split(",");
				float[] accelerometerValues = magCollectionProvider
						.getAccelerometerValues();
				float[] gravityValues = magCollectionProvider
						.getGravityValues();
				float[] orientationOldValues = magCollectionProvider
						.getOrientationOldValues();
				float[] orientationNewValues = magCollectionProvider
						.getOrientationNewValues();
				float[] GroValues = magCollectionProvider
						.getGroValues();
				double realTimedirection = orientationOldValues[0];// android原生新方法获取方向
				if (realTimedirection > 180) {
					realTimedirection = realTimedirection - 360;
				}
				// double realTimedirection = orientationAccuracy;//
				// 第三方方法获取高精度方位
				double orientationorigin = magCollectionProvider
						.getOrientationOldValues()[0];// android原生老方法获取方向
				if (orientationorigin > 180) {
					orientationorigin = orientationorigin - 360;
				}
 
				// 根据论文 Implementing a Tilt-Compensated eCompass using
				// Accelerometer and Magnetometer Sensors 转换地磁和重力读数坐标
				double magx = magneticFieldValues[1];
				double magy = magneticFieldValues[0];
				double magz = -magneticFieldValues[2];
				double yaw = orientationOldValues[0];
				double pith = -orientationOldValues[1];
				double roll = -orientationOldValues[2];
				double[] Mag = RotaingMagToFlat.deRotaingMagToFlat(magx, magy, magz, yaw, pith,
						roll);
				double magXFlat = Mag[0];
				double magYFlat = Mag[1];
				double magZFlat = Mag[2];
				Sample sample = new Sample();
				sample.setCurrxOrigin(currx);
				sample.setCurryOrigin(curry);
				sample.setCurrzOrigin(currz);
				sample.setMagXOrigin(magneticFieldValues[0]);
				sample.setMagYOrigin(magneticFieldValues[1]);
				sample.setMagZOrigin(magneticFieldValues[2]);
			
				sample.setMagneticValue(magneticValue);
				sample.setMagXFlat(magXFlat);
				sample.setMagYFlat(magYFlat);
				sample.setMagZFlat(magZFlat);
				sample.setSpectrumXFlat(magneticFieldValuesInEarth[0]);
				sample.setSpectrumYFlat(magneticFieldValuesInEarth[1]);
				sample.setSpectrumZFlat(magneticFieldValuesInEarth[2]);
//				sample.setOrientationNewX(orientationNewValues[0]);
//				sample.setOrientationNewY(orientationNewValues[1]);
//				sample.setOrientationNewZ(orientationNewValues[2]);
				sample.setOrientationNewX(magneticFieldValuesInEarth2[0]);
				sample.setOrientationNewY(magneticFieldValuesInEarth2[1]);
				sample.setOrientationNewZ(magneticFieldValuesInEarth2[2]);
				if (orientationOldValues[0] > 180) {
					sample.setOrientationOldX(orientationOldValues[0] - 360);
				} else {
					sample.setOrientationOldX(orientationOldValues[0]);
				}
			//	sample.setOrientationOldX(orientationOldValues[0]);
				sample.setOrientationOldY(orientationOldValues[1]);
				sample.setOrientationOldZ(orientationOldValues[2]);
				sample.setOrientationAccuracyX(orientationAccuracy[0]);
				sample.setOrientationAccuracyY(orientationAccuracy[1]);
				sample.setOrientationAccuracyZ(orientationAccuracy[2]);
				sample.setGravityX(gravityValues[0]);
				sample.setGravityY(gravityValues[1]);
				sample.setGravityZ(gravityValues[2]);
				sample.setAccX(accelerometerValues[0]);
				sample.setAccY(accelerometerValues[1]);
				sample.setAccZ(accelerometerValues[2]);
				sample.setGPS(Constant.isGPS);
				sample.setStair(CalculateLocation2.isStair);
				sample.setElevator(CalculateLocation2.isElevator);
				sample.setHeight(heightValue);
				sample.setLight(lightValue);
				double[] IOResult=magCollectionProvider.getIODetectResult();
				sample.setIoLight(IOResult[0]);
				sample.setIoGpgsv(IOResult[1]);
				sample.setIndoorProbability(IOResult[2]);
				sample.setStepLength(stepLength);
				sample.setGyroX(GroValues[0]);
				sample.setGyroY(GroValues[1]);
				sample.setGyroZ(GroValues[2]);
//				if (FireManActivity.isStart()) {
//					SensorDataLogFileMagneticSamplerTimer
//					.trace( sample.toString()+"\n");
//
//
//					dataAdaptor.dataProcessing(sample);
//				}
				if (FireManActivity.isStart()) {
//					SensorDataLogFileMagneticSamplerTimerzhuji
//							.trace( System.currentTimeMillis()+" "+sample.getStepLength()+" "+sample.getAccX()+" "+sample.getAccY()+" "+sample.getAccZ()+" "+
//                                    sample.getGyroX()+" "+sample.getGyroY()+" "+sample.getGyroZ()+" "+
//                                    sample.getMagXOrigin()+" "+sample.getMagYOrigin()+" "+sample.getMagZOrigin()+" "+
//                                    sample.getGravityX()+" "+sample.getGravityY()+" "+sample.getGravityZ()+"\n");
//					SensorDataLogFileMagneticSamplerTimer
//					.trace( sample.toString()+"\n");
//
//					dataAdaptor.dataProcessing(sample);
					//将原始数据写入文件
//                    String res ="test" + " " + "1" + " " + acc[0] + " " + acc[1] + " " + acc[2] + " " + gyr[0] + " " + gyr[1] + " " + gyr[2] + " " + mag[0] + " " + mag[1] + " " + mag[2] + " " + pre + " " + "-1" + " "+ "-1";
//                    FileReadAndWrite.writeFile(Config.basePath+Config.rawDataPath + state + "_Raw_" + sdf.format(date) + ".txt", res, true);

					String res ="test" + " "+ accelerometerValues[0] + " " + accelerometerValues[1] + " " + accelerometerValues[2] + " " + GroValues[0] + " " + GroValues[1] + " " + GroValues[2] + " " +
							magneticFieldValues[0] + " " + magneticFieldValues[1] + " " + magneticFieldValues[2] + " " +  System.currentTimeMillis() + " " +stepLength + " " + stepcount + " " + walkDistance+ " " + currx+ " " + curry+ " " + yaw;
                    String  path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+"rawData" + File.separator;
					FileReadAndWrite.writeFile(path + "PDR_Raw_" + sdf.format(date) + ".txt", res, true);
					if(lastStepLength!=stepLength){
						lastStepLength=stepLength;
						stepcount++;
						walkDistance+=stepLength;
					}
				}
                Message message3 = new Message();
                message3.what = REALTIMERECORD;
                message3.obj =  "log "
                        + df.format(currx) + "," + df.format(-curry)  + ","
                        +  df.format(-currz)  + "\n "+df.format(yaw) + "," + df.format(stepLength)   + "," + df.format(stepcount)  + "," + df.format(walkDistance)   ;
                mhandler.sendMessage(message3);
			}

		}, 0, 10);
	}

	public void stop() {
	//	MagCollectionProvider.stop();
		magCollectionProvider.stop();
		timer.cancel();
	}

//	/*
//	 * 第三方的方法获取高精度的方位
//	 */
//	public void getOrientation() {
//
//		Orientation orientationSensor = new Orientation(mContext, this);
//
//		// ------Turn Orientation sensor ON-------
//		// set tolerance for any directions
//		orientationSensor.init(1.0, 1.0, 1.0);
//
//		// set output speed and turn initialized sensor on
//		// 0 Normal
//		// 1 UI
//		// 2 GAME
//		// 3 FASTEST
//		orientationSensor.on(3);
//		// ---------------------------------------
//
//		// turn orientation sensor off
//		// orientationSensor.off();
//
//		// return true or false
//		orientationSensor.isSupport();
//
//	}
//
//	@Override
//	public void orientation(Double AZIMUTH, Double PITCH, Double ROLL) {
//		// TODO Auto-generated method stub
////		Log.d("Azimuth", String.valueOf(AZIMUTH));
////		Log.d("PITCH", String.valueOf(PITCH));
////		Log.d("ROLL", String.valueOf(ROLL));
//
//		if (AZIMUTH >= 0 && AZIMUTH <= 180) {
//			orientationAccuracy[0] = AZIMUTH;
//		} else {
//			orientationAccuracy[0] = AZIMUTH - 360;
//		}
//		orientationAccuracy[1] = PITCH;
//		orientationAccuracy[2] = ROLL;
//	}
// 

}
