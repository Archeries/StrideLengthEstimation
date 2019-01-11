package com.ict.gpsmodule;

 
import com.amap.location.demo.R;
import com.ict.gpsmodule.GPSLocationService.GPSBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.TextView;

public class MainActivity extends Activity {

	public boolean isUsable = false;
	public final static int GPSUSABLE=0;
	public final static  int GPSUNUSABLE=1;
	TextView locationInfoTxt;
	Thread GPSThread;
	
	int count = 0;
	
	private GPSBinder gpsBinder;
	
	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			gpsBinder = (GPSBinder) service;
			GPSThread = new Thread() {

				@Override
				public void run() {
					while (true) {
						Message msg = mHandler.obtainMessage();
						if (gpsBinder.isGPSUsable()) {
							msg.what = GPSUSABLE;
							mHandler.sendMessage(msg);
						}
						else {
							msg.what = GPSUNUSABLE;
							mHandler.sendMessage(msg);
						}
						count++;
						try {
							sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};
	    	GPSThread.start();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
	};
	
	Handler mHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GPSUSABLE: 
					Coordinator location = gpsBinder.getLocation();
					locationInfoTxt.setText("Longitude: " + location.lon + "\nLatitude: " + location.lat + "\n" + count);
					break;
				case GPSUNUSABLE:
					String s = "Can not get location.\nTried " + Integer.toString(count) + " seconds.";
					locationInfoTxt.setText(s);
					break;
			}
		};
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        initGPS();
    }
    
    private void initGPS() {
  //  	locationInfoTxt = (TextView) findViewById(R.id.location_info_text);
    	
    	Intent startIntent = new Intent(this, GPSLocationService.class);
    	startService(startIntent);
    	Intent bindIntent = new Intent(this, GPSLocationService.class);
    	bindService(bindIntent, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
    	GPSThread.interrupt();
    	unbindService(connection);
    	Intent stopIntent = new Intent(this, GPSLocationService.class);
    	stopService(stopIntent);
    	super.onDestroy();
    	android.os.Process.killProcess(android.os.Process.myPid());
    }
}
