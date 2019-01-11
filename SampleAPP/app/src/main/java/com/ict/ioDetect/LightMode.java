package com.ict.ioDetect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;

public class LightMode implements SensorEventListener {

	public static int hasRegister = 1;
	private SensorManager sensorManager;
	private Sensor lightSensor;
	private Sensor proxSensor;
	public static double outdoorConfiL = 0.5;
	
	public long modeStartingTime;

	

	private float lightIntensity;
	private boolean lightBlocked = false;
	private double LightAverage;

	private DetectionProfile[] listProfile = new DetectionProfile[3];
	private DetectionProfile indoor, semi, outdoor;

	//Data initialization
	private String sunRiseTime = "6:22";
	private String sunSetTime = "17:39";

	private boolean enable;

	//Initialize the light intensity threshold
	private int HIGH_THRESHOLD = 1000;
	 final int HIGH_THRESHOLD1 = 800;
	 final int HIGH_THRESHOLD2 = 600;
	private int LOW_THRESHOLD = 30;
	private int lightVariance = 1000;
	

	
	private double lightSumInAMinute = 0;
	// the average light intensity of one minute
	private double[] lightWindowInOneMinute = new double[5];
	// the light intensities during one detection
	private double[] lightWindowInOneDetection = new double[15];

	
	
	
	
	public double getLightAverage() {
		return LightAverage;
	}

	public void setLightAverage(double lightAverage) {
		LightAverage = lightAverage;
	}

	
	
	public int getLightVariance() {
		return lightVariance;
	}

	public void setLightVariance(int lightVariance) {
		this.lightVariance = lightVariance;
	}

	/**
	 * @param sManager
	 * @param context
	 * @param enable
	 */
	public LightMode(SensorManager sManager, Context context, boolean enable) {
		this.enable = enable;

		if (enable) {
			sensorManager = sManager;
			lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
			proxSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
			modeStartingTime = System.currentTimeMillis();
			for (int i = 0; i < lightWindowInOneMinute.length; i++) {
				lightWindowInOneMinute[i] = 0;
			}
			for (int i = 0; i < lightWindowInOneDetection.length; i++) {
				lightWindowInOneDetection[i] = 0;
			}
		
			sunRiseTime = (SunTimes.sun_Rise().equals(null))?sunRiseTime:SunTimes.sun_Rise();

			sunSetTime = (SunTimes.sun_Set().equals(null))?sunSetTime:SunTimes.sun_Set();
			
			

		}

		indoor = new DetectionProfile("indoor");
		semi = new DetectionProfile("semi-outdoor");
		outdoor = new DetectionProfile("outdoor");
		listProfile[0] = indoor;
		listProfile[1] = semi;
		listProfile[2] = outdoor;
	}

	// flag for terminating the window thread
	private boolean isRun;

	private int windowTimer = 0;
	private int timer = 0;
	private int oneDetectionTimer = 0;
	// put magnet value into the magnetism array every second
	private Thread windowThread = new Thread() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			isRun = true;
			while (isRun) {
				try {
					sleep(60000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				DecimalFormat df = new DecimalFormat("0.000");

				lightWindowInOneMinute[windowTimer % 5] = lightSumInAMinute
						/ timer;
				windowTimer++;
				if (windowTimer == lightWindowInOneMinute.length) {
					windowTimer = 0;
				}

				timer = 0;
				lightSumInAMinute = 0;
			}
		}
	};

	public void start() {
		if (enable) {
			hasRegister = 2;

			sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
			sensorManager.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_NORMAL);
			windowThread.start();
		}
	}

	/**
	 * unregister the listener
	 */
	public void stop() {
		if (enable) {
			hasRegister = 3;
			sensorManager.unregisterListener(this, lightSensor);
			sensorManager.unregisterListener(this, proxSensor);
			isRun = false;
		}
	}
	
	public void start1() {
		if (enable) {
			hasRegister = 2;
			sensorManager.registerListener(this, lightSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
			sensorManager.registerListener(this, proxSensor,
					SensorManager.SENSOR_DELAY_NORMAL);

		}
	}

	/**
	 * unregister the listener
	 */
	public void stop1() {
		if (enable) {
			hasRegister = 3;
			sensorManager.unregisterListener(this, lightSensor);
			sensorManager.unregisterListener(this, proxSensor);

			
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.hardware.SensorEventListener#onAccuracyChanged(android.hardware
	 * .Sensor, int)
	 */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.hardware.SensorEventListener#onSensorChanged(android.hardware
	 * .SensorEvent)
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		// event.sensor.getMaximumRange()
		if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {

			if (event.values[0] == 0) {
				lightBlocked = true;
			} else {
				lightBlocked = false;
			}
		} else {

			lightIntensity = event.values[0];
			lightSumInAMinute += lightIntensity;
			timer++;
			lightWindowInOneDetection[oneDetectionTimer % 15] = lightIntensity;
			oneDetectionTimer++;
			if (oneDetectionTimer >= lightWindowInOneDetection.length) {
				oneDetectionTimer = 0;
			}

		}

	}

	/**
	 * @return lightIntensity
	 */
	public float getLightValue() {
		if (enable)
			return lightIntensity;
		else
			return 0;
	}

	/**
	 * @return listProfile
	 * @throws ParseException 
	 */
	public DetectionProfile[] getProfile() {
		if (enable) {

			if (!lightBlocked) {
				int flag = 0;
				double variance = 0.0;
				double Average=0.0;
				String log = "lightwindow:";
				// System.out.print("lightwindow:");
				int i = 0;
				// calculating how many reduction(>30) in previous 5 minutes
				for (i = 0; i < lightWindowInOneMinute.length - 1; i++) {
					log += lightWindowInOneMinute[i] + " ";
					if (lightWindowInOneMinute[i]
							- lightWindowInOneMinute[i + 1] >= 30)
						flag++;
				}
				log += lightWindowInOneMinute[i] + "\n";
				
				/*
				 * System.out.println("light window variance:" +
				 * Math.sqrt(StatisticsUtil.getVariation(
				 * lightWindowInOneDetection, 0)));
				 */
				
				
				DecimalFormat df = new DecimalFormat("0.00000");
				Average= Double.parseDouble(df.format(StatisticsUtil.getAverage(lightWindowInOneDetection, 0)));
				setLightAverage(Average);
				
				variance = Math.sqrt(StatisticsUtil.getVariation(
						lightWindowInOneDetection, 0));
				log += "light window variance:"
						+ Math.sqrt(StatisticsUtil.getVariation(
								lightWindowInOneDetection, 0)) + "\n";
				// System.out.println("light window flag:" + flag);
				log += "light window flag:" + flag + "\n";
				// if the variance of light intensity of this detection is more
				// than 150, we think it's outdoor
				if (variance > lightVariance ) {
					outdoorConfiL=1;
					indoor.setConfidence(0);
					semi.setConfidence(variance);
					outdoor.setConfidence(1);
					return listProfile;
					
				}

				
				
				// if reduction is more than 4, we think it's outdoor
				if (flag >= 4) {
					
					outdoorConfiL=1.0;
					
					indoor.setConfidence(0);
					semi.setConfidence(flag);
					outdoor.setConfidence(1);

					return listProfile;
				}
				
				

				Calendar calendar = Calendar.getInstance();

				int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
				int currentMinute = calendar.get(Calendar.MINUTE);

				int cerrentMonth=calendar.get(Calendar.MONTH);
				
				int diffMonth=3*Math.abs(cerrentMonth-6);
				
				int hourOfHalfAnHourBeforeSunset;
				int minuteOfHalfAnHourBeforeSunset;
				int hourOfHalfAnHourAfterSunrise;
				int minuteOfHalfAnHourAfterSunrise;
				
				
				
				
				

				if (getSunSetMin() >= 30) {
					hourOfHalfAnHourBeforeSunset = getSunSetHour();
					minuteOfHalfAnHourBeforeSunset = getSunSetMin() - 30;
				} else {
					hourOfHalfAnHourBeforeSunset = getSunSetHour() - 1;
					minuteOfHalfAnHourBeforeSunset = getSunSetMin() + 30;
				}
				if(getSunRiseMin()>=30){
					hourOfHalfAnHourAfterSunrise = getSunRiseHour()+1;
					minuteOfHalfAnHourAfterSunrise = getSunRiseMin()-30;
				}else{
					hourOfHalfAnHourAfterSunrise = getSunRiseHour();
					minuteOfHalfAnHourAfterSunrise = getSunRiseMin()+30;
				}

				double indoorConfidence = 0;
				// sun rise time to half an hour before sun set, if the
				// intensity>threshold,then the confidence of outdoor will be
				// 1, otherwise the confidence of indoor will reduce as the
				// intensity increases
				if (TimeCompareUtil.isLaterThan(currentHour, currentMinute,
						hourOfHalfAnHourAfterSunrise, minuteOfHalfAnHourAfterSunrise)
						&& !TimeCompareUtil.isLaterThan(currentHour,
								currentMinute, hourOfHalfAnHourBeforeSunset,
								minuteOfHalfAnHourBeforeSunset)) {
				
			
					
					if(readWeather())
					{
						  if(getLightAverage()>=1000) {
								indoorConfidence = 0;
							} else if(getLightAverage()>=500 &&getLightAverage()<1000) {
								indoorConfidence =  (0.0008*getLightAverage()); // daytime
							} else if(getLightAverage()>=200 &&getLightAverage()<500) {
								indoorConfidence = 0.5+(0.5/300)*(500-getLightAverage()); // daytime
							}else {
								indoorConfidence = 1;	
							}
					}
					
					/*	
					 The five levels are called Dazzling(2000 above), High Bright(1000 – 2000), 
					 Bright(500 – 1000), Normal(200 – 500) and Dark(0 – 200), and in no clear days 
					 only four levels are remained: High Bright(1000 above), Bright(500 – 1000),
					 Normal(200 – 500) and Dark(0 – 200). */
					
					else {
						 if ( getLightAverage()> 2000) {	
		                    	indoorConfidence = 0.0;
							} else if(getLightAverage()>=1000 &&getLightAverage()<2000) {
								indoorConfidence = 0.2-(0.0001*getLightAverage()); // daytime
							} else if(getLightAverage()>=500 &&getLightAverage()<1000) {
								indoorConfidence = 0.9 - (0.0008*getLightAverage()); // daytime
							} else if(getLightAverage()>=200 &&getLightAverage()<500) {
								indoorConfidence = 0.5+(0.5/300)*(500-getLightAverage()); // daytime
							}else {
								indoorConfidence = 1;	
							}
					}
					outdoorConfiL=(1-indoorConfidence);
                    semi.setConfidence((double)(int)getLightAverage());
					outdoor.setConfidence(1-indoorConfidence);
					indoor.setConfidence(indoorConfidence);

				}

				// after sun set, before sun rise, the confidence of indoor will
				// increase as the light intensity increases
				else if (TimeCompareUtil.isLaterThan(currentHour,
						currentMinute, getSunSetHour(), getSunSetMin())
						|| !TimeCompareUtil.isLaterThan(currentHour,
								currentMinute, getSunRiseHour(),
								getSunRiseMin())) {
					// when light intensity<=30, we can't detect indoor/outdoor,
					// but when the sun set, the probability of indoor
					// is much bigger, so we set the confidence of indoor to
					// 0.9. At the same time, we will reduce the weight of light
					// mode

					if ((System.currentTimeMillis() - modeStartingTime < 3 * 60 * 1000) && getLightAverage() <= 100) {
						indoorConfidence = 0.5;
					} else {
						float threshold = getThreshold(currentHour, currentMinute);
						if (currentHour > 22 || currentHour < 6) {
							if (getLightAverage()>LOW_THRESHOLD) {
								indoorConfidence = 0.8;
							}
							else if (getLightAverage()<=LOW_THRESHOLD&&getLightAverage()>20) {
								indoorConfidence = 0.7;
								
							} else if(getLightAverage()<=20&&getLightAverage()>10) {
								indoorConfidence = 0.6;
							}else if(getLightAverage()<=10&&getLightAverage()>=0)
							{
								indoorConfidence = 0.3;
							}
						} else {	
							if (getLightAverage()>LOW_THRESHOLD) {
								indoorConfidence = 0.9;
								
							}else {
								indoorConfidence=0.1;
							}					
						}
					}
				
						
					
					

					outdoorConfiL=(1-indoorConfidence);
					semi.setConfidence((double)(int)getLightAverage());
					outdoor.setConfidence(1-indoorConfidence);
					indoor.setConfidence(indoorConfidence);
				} 
				
				else {
					// half an hour before sun set to sun set, the light
					// intensity
					// of indoor and outdoor is indistinguishable, set the
					// confidence to 0
					outdoorConfiL=0.5;
					indoor.setConfidence(0.5);
					outdoor.setConfidence(0.5);
					semi.setConfidence((double)(int)getLightAverage());
				}
			}
		}
		
		
		return listProfile;
	}
	
	//Get a weather when the Internet 
	private boolean readWeather()  {
		// TODO Auto-generated method stub
		
		boolean Flg=false;
		
		  File file = new File(Environment
					 .getExternalStorageDirectory().getAbsolutePath()
					 + "/IODetector/" + "logWeather.txt");
	        FileReader reader;
			try {
				reader = new FileReader(file);
				int fileLen = (int)file.length();
			    char[] chars = new char[fileLen];
			    reader.read(chars);
			    String txt = String.valueOf(chars);
			        
			    String Weather1="Haze";
			    String Weather2="Smoke";
			        
		        if(txt.equals(Weather1)&&txt.equals(Weather2))
		        {
		        	Flg =true;
		        	
		        }
		        else {
		        	Flg=false;
					
				}
		        reader.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	       
	      return Flg;
        
        
	}

	
	
	//Get dynamic threshold
	private float getThreshold(int currentHour, int currentMinute) {
		float threshold = 0;
		int hourOfHalfAnHourBeforeSunset;
		int minuteOfHalfAnHourBeforeSunset;

		if (getSunSetMin() >= 30) {
			hourOfHalfAnHourBeforeSunset = getSunSetHour();
			minuteOfHalfAnHourBeforeSunset = getSunSetMin() - 30;
		} else {
			hourOfHalfAnHourBeforeSunset = getSunSetHour() - 1;
			minuteOfHalfAnHourBeforeSunset = getSunSetMin() + 30;
		}

		// sun rise to 2 hour after sun rise, the threshold increases from 0 to
		// 1000 by a linear
		// function f(t)=1000 / 12 * t, t is every 10 minutes
		if (TimeCompareUtil.isLaterThan(currentHour, currentMinute,
				getSunRiseHour(), getSunRiseMin())
				&& !TimeCompareUtil.isLaterThan(currentHour, currentMinute,
						getSunRiseHour() + 2, getSunRiseMin())) {
			
			//threshold = (float) (1000.0/12.0)
				//	* ((currentHour - getSunRiseHour()) * 6 + ((float) (currentMinute - getSunRiseMin())) / 10);
	          threshold =HIGH_THRESHOLD1;
			
			
		}
		// 2 hour after sun rise time to 2 hour before sun set, the threshold
		// should be 2000
		else if (TimeCompareUtil.isLaterThan(currentHour, currentMinute,
				getSunRiseHour() + 2, getSunRiseMin())
				&& !TimeCompareUtil.isLaterThan(currentHour, currentMinute,
						getSunSetHour() - 2, getSunSetMin())) {
		
			threshold = HIGH_THRESHOLD;
			
		}
		// 2 hour before sun set to half an hour before sun set, the threshold
		// decreases from 1000 to 30 by a linear function f(t)=1000 - (1000-30) / 9 * t, t is
		// every 10 minutes
		else if (TimeCompareUtil.isLaterThan(currentHour, currentMinute,
				getSunSetHour() - 2, getSunSetMin())
				&& !TimeCompareUtil.isLaterThan(currentHour, currentMinute,
						hourOfHalfAnHourBeforeSunset,
						minuteOfHalfAnHourBeforeSunset)) 
		{
		
		
			
			threshold=HIGH_THRESHOLD2;
			
		}

		return threshold;
	}

	/**
	 * @return HIGH_THRESHOLD
	 */
	public int getHIGH_THRESHOLD() {
		return HIGH_THRESHOLD;
	}

	/**
	 * @param hIGH_THRESHOLD
	 */
	public void setHIGH_THRESHOLD(int hIGH_THRESHOLD) {
		HIGH_THRESHOLD = hIGH_THRESHOLD;
	}

	/**
	 * @return LOW_THRESHOLD
	 */
	public int getLOW_THRESHOLD() {
		return LOW_THRESHOLD;
	}

	/**
	 * @param lOW_THRESHOLD
	 */
	public void setLOW_THRESHOLD(int lOW_THRESHOLD) {
		LOW_THRESHOLD = lOW_THRESHOLD;
	}

	
	//Get the sunrise and sunset time based on longitude and latitude
	public int getSunRiseMin() {
	
		
		String[] min = sunRiseTime.split(":");
	
		return Integer.parseInt(min[1]);
		
	}

	public int getSunSetMin() {
		
		
		String[] min = sunSetTime.split(":");
		return Integer.parseInt(min[1]);
	}

	public int getSunRiseHour() {
	
		String[] min = sunRiseTime.split(":");
		return Integer.parseInt(min[0]);
		
	}

	public int getSunSetHour() {
	
		String[] min = sunSetTime.split(":");
		return Integer.parseInt(min[0]);
	}

	public String getSunRiseTime() {
		return sunRiseTime;
	}

	public void setSunRiseTime(String sunRiseTime) {
		this.sunRiseTime = sunRiseTime;
	}

	public String getSunSetTime() {
		return sunSetTime;
	}

	public void setSunSetTime(String sunSetTime) {
		this.sunSetTime = sunSetTime;
	}
	
	
	//whether daytime or not
	public boolean getDayTimeJudge() {
		Calendar calendar = Calendar.getInstance();
		int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
		int currentMinute = calendar.get(Calendar.MINUTE);
		if (TimeCompareUtil.isLaterThan(currentHour, currentMinute,
				getSunRiseHour() + 2, getSunRiseMin())
				&& !TimeCompareUtil.isLaterThan(currentHour, currentMinute,
						getSunSetHour() - 2, getSunSetMin())) {
			return true;
		} else {
			return false;
		}
	}
	
	
	public double getIndoorsConfidenceOfLightMode() {
		return (1 - outdoorConfiL);
	}

}
