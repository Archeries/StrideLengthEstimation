package com.ict.ioDetect;

import java.util.Arrays;

import com.ict.wq.SensorDataLogFileGPGSV;

import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class GPGSVMode {
	private LocationManager lm;
	// private boolean isGetSatellite;

	private int THRESHOLD = 2;
	// when the number of the messages which GPGSV mode got exceed this
	// value,
	// we consider it as a steady result and return the profile data
	private static final int RECEIVED_MESSAGE_COUNT = 1;
	private int[] satelliteCountWindow = new int[RECEIVED_MESSAGE_COUNT];
	private int[] satelliteCountWindow22 = new int[10];

	private DetectionProfile[] listProfile = new DetectionProfile[3];
	private DetectionProfile indoor, semi, outdoor;

	private Context context;

	private boolean enable;

	/**
	 * @param context
	 * @param enable
	 */
	public GPGSVMode(Context context, boolean enable) {
		// isGetSatellite = false;
		this.enable = enable;

		indoor = new DetectionProfile("indoor");
		semi = new DetectionProfile("semi-outdoor");
		outdoor = new DetectionProfile("outdoor");
		listProfile[0] = indoor;
		listProfile[1] = semi;
		listProfile[2] = outdoor;

		for (int i = 0; i < RECEIVED_MESSAGE_COUNT; i++) {
			satelliteCountWindow[i] = 0;
		}
		for (int i = 0; i < 10; i++) {
			satelliteCountWindow22[i] = 0;
		}

		this.context = context;
		lm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
	}

	/**
	 * update the profile
	 */
	private void updateProfile() {
		double averageSatelliteCount = StatisticsUtil
				.getAverage(satelliteCountWindow);

	 

	//	System.out.println("averageSatelliteCount:" + averageSatelliteCount);
		if (averageSatelliteCount <= THRESHOLD) {
			indoor.setConfidence(1);
			semi.setConfidence(0);
			outdoor.setConfidence(0);
		} else {
			outdoor.setConfidence((averageSatelliteCount - THRESHOLD)
					/ averageSatelliteCount);
			semi.setConfidence(0);
			indoor.setConfidence(1 - (averageSatelliteCount - THRESHOLD)
					/ averageSatelliteCount);
		}
		SensorDataLogFileGPGSV.trace("" + indoor.getConfidence() + ","
				+ averageSatelliteCount + ",satelliteCountWindow "
				+ Arrays.toString(satelliteCountWindow) + "\n");
	}

	/**
	 * @author SuS locating
	 */
	private class LocateThread extends Thread implements
			GpsStatus.NmeaListener, LocationListener {
		private LocationManager lm;
		private int messageCount;
		private int satelliteCount;

		private int receivedCount;

		private long startTime;

		public LocateThread(LocationManager lm) {
			this.lm = lm;
			messageCount = 1;
			satelliteCount = 0;
			receivedCount = 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			Looper.prepare();
			// isGetSatellite = false;

			lm.addNmeaListener(this);
			startTime = last = System.currentTimeMillis();
			lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, this,
					Looper.myLooper());
			// lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 5,
			// this);
			Looper.loop();

		}

		private long current;
		private long last;

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.location.GpsStatus.NmeaListener#onNmeaReceived(long,
		 * java.lang.String)
		 */
		@Override
		public void onNmeaReceived(long timestamp, String nmea) {
			// TODO Auto-generated method stub
			current = System.currentTimeMillis();
			// System.out.println("time gap:" + (current - last));

			// if nema message contains the GPGSV, then get 10 GPGSV message
			// won't take more than 11s, so when 11s passed, just stop listening
//			Log.d("GPGSV", "time" + (current - startTime) + " receivedCount"
//					+ receivedCount);
			if (current - startTime > (RECEIVED_MESSAGE_COUNT + 1) * 1000
					|| receivedCount >= RECEIVED_MESSAGE_COUNT) {
				updateProfile();
				// isGetSatellite = true;
				// System.out.println("isGetSatellite:" + isGetSatellite);
//				Log.d("GPGSV",
//						"time111 " + (current - startTime)
//								+ " receivedCount" + receivedCount + " "
//								+ "satelliteCountWindow22"
//								+ Arrays.toString(satelliteCountWindow22));
				lm.removeNmeaListener(this);
				lm.removeUpdates(this);
				receivedCount = 0;

				Looper.myLooper().quit();
			} else {

				// System.out.println("nmea:" + nmea);
				last = current;
				if (nmea.startsWith("$GPGSV")) {
	//				System.out.println("nmea" + nmea);
					SensorDataLogFileGPGSV.trace("nmea" + nmea);
					String[] nmeaList = nmea.split(",");
					String[] lastSatellite;
					// nmeaText.setText(nmeaList[3]);
					int length = nmeaList.length;
					// long start=System.currentTimeMillis();
					// the srn of the first satellite
					for (int i = 7; i < length - 1; i += 4) {
						if (!nmeaList[i].equals(""))
							satelliteCount++;
					}
					// System.out.println("time:"+(System.currentTimeMillis()-start));
					lastSatellite = nmeaList[length - 1].split("\\*");
					if (!lastSatellite[0].equals("")) {
						satelliteCount++;
					}

					if (messageCount++ == Integer.parseInt(nmeaList[1])) {
						// nmeaText.setText(String.valueOf(satelliteCount));
						satelliteCountWindow[receivedCount++ % 10] = satelliteCount;
						satelliteCountWindow22[receivedCount++ % 10] = satelliteCount;

						// receivedCount++;
						// System.out.println("receivedCount:" + receivedCount);
//						System.out.println("satelliteCount " + satelliteCount
//								+ " satelliteCountWindow"
//								+ Arrays.toString(satelliteCountWindow)
//								+ "satelliteCountWindow22"
//								+ Arrays.toString(satelliteCountWindow22));
						satelliteCount = 0;
						messageCount = 1;
					}
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.location.LocationListener#onLocationChanged(android.location
		 * .Location)
		 */
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			// System.out.println("onLocationChanged");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.location.LocationListener#onStatusChanged(java.lang.String,
		 * int, android.os.Bundle)
		 */
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			// System.out.println("onStatusChanged");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.location.LocationListener#onProviderEnabled(java.lang.String)
		 */
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			// System.out.println("onProviderEnabled");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.location.LocationListener#onProviderDisabled(java.lang.String
		 * )
		 */
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			// System.out.println("onProviderDisabled");
		}
	}

	// thread to get the number of satellites in view
	private LocateThread locateThread;

	/**
	 * @return listProfile
	 */
	public DetectionProfile[] getProfile() {
		if (enable) {
			boolean isGpsOn = lm
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
	 
			if (isGpsOn) {
				// when the thread is still running, we wait for it completing,
				// and
				// just return the profile of last time

				if (locateThread == null || !locateThread.isAlive()) {
					locateThread = new LocateThread(lm);
					locateThread.start();
				} else {
					// System.out.println("locateThread is alive:"
					// + locateThread.isAlive());
				}
				// wait for the mode to get the reliable data
				// while (!isGetSatellite) {
				//
				// }
				//
				// isGetSatellite = false;
			} else {
				indoor.setConfidence(0);
				semi.setConfidence(0);
				outdoor.setConfidence(0);
			}
		}

		return listProfile;
	}

	/**
	 * @return THRESHOLD
	 */
	public int getTHRESHOLD() {
		return THRESHOLD;
	}

	/**
	 * @param tHRESHOLD
	 */
	public void setTHRESHOLD(int tHRESHOLD) {
		THRESHOLD = tHRESHOLD;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public int getSatelliteNum() {
		return satelliteCountWindow[0];
	}

}
