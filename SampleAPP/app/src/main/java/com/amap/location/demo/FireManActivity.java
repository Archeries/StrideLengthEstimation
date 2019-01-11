package com.amap.location.demo;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import cn.ac.ict.ictips.locating.subPositioningEngines.LandMarkPositioningEngine.LandmarkProvider;
import cn.ac.ict.ictips.locating.subPositioningEngines.LandMarkPositioningEngine.LandmarkProvider.LandMarkCallback;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.GroundOverlay;
import com.amap.api.maps.model.GroundOverlayOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.ict.gpsmodule.Coordinator;
import com.ict.gyroscopecalibrate.Calibrate;
import com.ict.userstate.StillStateJudger;
import com.ict.wq.CalculateLocation2;
import com.ict.wq.Constant;
import com.ict.wq.LocationNetwork;
import com.ict.wq.LogUtil;
import com.ict.wq.MagneticSampleTimer;
import com.ict.wq.MagneticSampleTimerReview;
import com.ict.wq.SensorDataLogFileRecord;
import com.ict.wq.SensorDataLogFileRecordCoordinate;
import com.ict.wq.StepDetectionProvider;
import com.ict.wq.StepDetectionProvider.StepDetectedCallBack;
import com.wzg.dingwei.BluetoothService;
import com.wzg.dingwei.BluetoothService232;
import com.wzg.dingwei.BluetoothState;
import com.wzg.dingwei.DataCenter;
import com.wzg.dingwei.DebugThread;

public class FireManActivity extends Activity {
	private Context mContext;
	private AMap aMap;
	private MapView mapView;
	private Marker mMarker;

	// private Handler myhandler;
	private EditText lengthThresholde, dtwThresholdeEditText,
			startPointThresholde, initDirection;
	private Button confirmButton, reviewbButton, initButton, calibrateButton;
	private int realtimeOrReapperCount;
	private TextView recordTextView, magneticdirectiontTextView;
	public BluetoothService bluetoothService = null;
	public BluetoothService232 bluetoothService232 = null;
	public DebugThread debugThread = null;
	private GroundOverlay groundoverlay;
	private static boolean isStart = false;
	public boolean initFlag = true;

	public static boolean isStart() {
		return isStart;
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	private static boolean isReview = false;

	public static boolean isReview() {
		return isReview;
	}

	public static void setReview(boolean isReview) {
		FireManActivity.isReview = isReview;
	}

	private String tag = "MainActivity";
	private MagneticSampleTimer magneticSampleTimer;
	private MagneticSampleTimerReview magneticSampleTimerReview;
	private Button sampleButton;
	private CalculateLocation2 calculateLocation2;

	float startlatitude = (float) 39.981782;// 计算所工位
	float startlongitude = (float) 116.326611;

//	// GPS start
//	private int count = 1;
//
//	Thread GPSThread;
//	public final static int GPSUSABLE = 0;
//	public final static int GPSUNUSABLE = 1;
//	public final static int CHECKGPSISOPEN = 2;
//	// int count = 0;
//
//	private GPSBinder gpsBinder;
//	private ServiceConnection connection = new ServiceConnection() {
//
//		@Override
//		public void onServiceConnected(ComponentName name, IBinder service) {
//			gpsBinder = (GPSBinder) service;
//			GPSThread = new Thread() {
//
//				@Override
//				public void run() {
//					while (true) {
//						Message msg = myhandler.obtainMessage();
//						if ((count % 3 == 0) && (!gpsBinder.isGPSOpen())) {
//							msg.what = 2;
//							myhandler.sendMessage(msg);
//						} else {
//							if (gpsBinder.isGPSUsable()) {
//								msg.what = 0;
//								myhandler.sendMessage(msg);
//							} else {
//								msg.what = 1;
//								myhandler.sendMessage(msg);
//							}
//						}
//						count++;
//						try {
//							sleep(1000);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//					}
//				}
//			};
//			GPSThread.start();
//		}
//
//		@Override
//		public void onServiceDisconnected(ComponentName name) {
//		}
//	};
//	// GPS end
 
	private LandmarkProvider landmarkProvider = null;
	private StepDetectionProvider stepDetectionProvider = null;
 

	// 用户状态识别
	private StillStateJudger stillStateJudger;
	private Vibrator vibrator;

	// 用户状态识别

	// 地磁修正
	private Calibrate calibrate = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fire_man0613);
		Thread.setDefaultUncaughtExceptionHandler(LogUtil.handler);
		mContext = getApplicationContext();
		initview();
		ChecksensorAvailable();//检查传感器的可用性
		// initGPS();// gps相关初始化
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		if (aMap != null) {
			aMap.clear();

		} else {
			aMap = mapView.getMap();
		}
		magneticSampleTimer = new MagneticSampleTimer(mContext, myhandler, aMap);
//		// calculateLocation2=new CalculateLocation2(mContext, myhandler, aMap);
//		// 添加楼层图
		addOverlayToMap();
		// 链接计步器
		bluetoothService = new BluetoothService(mContext, myhandler);
		bluetoothService.connect(null, true);
		// 连接网关
//		bluetoothService232 = new BluetoothService232(mContext, myhandler);
//		bluetoothService232.connect(null, true);
		LandMarkCallback onDataCallback = new LandMarkCallback() {

			@Override
			public void onData(JSONObject json) {
				// TODO Auto-generated method stub
				String landmarking;
				try {
					landmarking = json.getString("Landmarking");
					if(landmarking.equals("stairs")) {
						CalculateLocation2.isStair = true;
						Toast.makeText(FireManActivity.this, "检测到楼梯", Toast.LENGTH_SHORT)
						.show();
					} else if(landmarking.equals("elevator")){
						CalculateLocation2.isElevator = true;
						Toast.makeText(FireManActivity.this, "检测到电梯", Toast.LENGTH_SHORT)
						.show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		};
		landmarkProvider = new LandmarkProvider(FireManActivity.this,
				onDataCallback);
		
//		StepDetectedCallBack stepDetectedCallBack = new StepDetectedCallBack() {
//
//			@Override
//			public void onStepDetected(int stepCount) {
//
//				// TODO Auto-generated method stub
//			 
//				Toast.makeText(FireManActivity.this, "检测到步子_" + stepCount,
//						Toast.LENGTH_SHORT).show();
//			}
//		};
//		stepDetectionProvider = new StepDetectionProvider(FireManActivity.this,
//				stepDetectedCallBack);
//		// 用户状态识别
//		stillStateJudger = new StillStateJudger(mContext);
//		stillStateJudger.start();
		// 地磁修正
		// calibrate=new Calibrate(myhandler, mContext);

		// 向外发数据
		debugThread = new DebugThread(this, myhandler);
		debugThread.start();

		LocationNetwork locationNetwork = new LocationNetwork(this, myhandler);
		locationNetwork.Start();
		// // 得到设备的分辨率
		// DisplayMetrics dm = new DisplayMetrics();
		// getWindowManager().getDefaultDisplay().getMetrics(dm);
		// int widthPixels = dm.widthPixels;
		// int heightPixels = dm.heightPixels;
		// float density = dm.density;
		// // 计算屏幕宽度和高度
		// int screenWidth = (int) (widthPixels * density);
		// int screenHeight = (int) (heightPixels * density);
		// // 计算屏幕中心点经纬度
		//
		// GeoPoint centerPoint = aMap.getProjection().fromPixels(screenWidth/2,
		// screenHeight/2);
		mMarker = aMap.addMarker(new MarkerOptions()

				.title("拖动初始化")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.location_marker))
				.draggable(true)
				// .setFlat(true)
				.anchor((float) 0.3, (float) 0.3));

		// MarkerOptions markerOption = new MarkerOptions();
		// markerOption.title("拖动初始化").draggable(true).anchor((float)0.3,(float)
		// 0.3);

		mMarker.setPositionByPixels(400, 400);
		//mMarker.setPosition(Constant.workPoint);
		mMarker.showInfoWindow();// 设置默认显示一个infowinfow

//		// magneticSampleTimer = new MagneticSampleTimer(mContext, myhandler,
//		// aMap);
//
//		//
//		// MyThread myThread=new MyThread(mContext, myhandler);
//		// myThread.start();
//		
//		createDumpFile(FireManActivity.this);
	}

	public void initview() {
		mapView = (MapView) findViewById(R.id.map);
		lengthThresholde = (EditText) findViewById(R.id.Et_lengthThreshold);
		dtwThresholdeEditText = (EditText) findViewById(R.id.et_dtw_Threshold);
		startPointThresholde = (EditText) findViewById(R.id.et_startPointThreshold);
		initDirection = (EditText) findViewById(R.id.et_initDirection);
		recordTextView = (TextView) findViewById(R.id.TV_record);
		magneticdirectiontTextView = (TextView) findViewById(R.id.tv_magnetic_direction);
		DecimalFormat df = new DecimalFormat("#.00");
		magneticdirectiontTextView.setText("坐标" + 0.00 + "," + (-0.00) + ","
				+ (-0.00) + "\n 方向" + 0.00 + "height" + 0.00 + " indoor "
				+ 0.000);
		initButton = (Button) findViewById(R.id.btn_init);

		confirmButton = (Button) findViewById(R.id.btn_confirm);
		confirmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mMarker != null) {

					mMarker.hideInfoWindow();
					LatLng fusionlaLatLng = mMarker.getPosition();
					MarkerOptions markerOption = new MarkerOptions();
					markerOption.position(fusionlaLatLng);
					markerOption.title("起点");

					markerOption.draggable(false);
					// markerOption.icon(BitmapDescriptorFactory
					// .fromResource(R.drawable.location_marker));

					// 将Marker设置为贴地显示，可以双指下拉看效果
					markerOption.setFlat(true);
					markerOption.anchor((float) 0.3, (float) 0.3);
					mMarker = aMap.addMarker(markerOption);

					mMarker.showInfoWindow();
					Constant.startlatitude = mMarker.getPosition().latitude;
					Constant.startlongitude = mMarker.getPosition().longitude;
					// CalculateLocation2.fusionlatitude =
					// Constant.startlatitude;
					// CalculateLocation2.fusionlongitude =
					// Constant.startlongitude;
					// CalculateLocation2. fusionlatitudelast =
					// Constant.startlatitude;
					// CalculateLocation2. fusionlongitudelast =
					// Constant.startlongitude;
					CalculateLocation2.UpdateInitLocation(
							mMarker.getPosition().latitude,
							mMarker.getPosition().longitude, mMarker);
				}
				reviewbButton.setClickable(false);
				confirmButton.setClickable(false);
				System.out.println("mMarker" + mMarker.getPosition().latitude
						+ " " + mMarker.getPosition().longitude);
				// magneticSampleTimer = new MagneticSampleTimer(mContext,
				// myhandler, aMap);
				// magneticSampleTimer.start();
				// magneticSampleTimer.getOrientation();
				setStart(true);
				setReview(false);
				// recordTextView.append("\n\n");
				// float startlatitude = (float) 39.97979627927678;// 计算所工位
				// float startlongitude = (float) 116.31888256078656;

				if (aMap == null) {
					aMap = mapView.getMap();
					// setUpMap();
				}
				Constant.LengthThreshold = Integer.parseInt(lengthThresholde
						.getText().toString().trim());
				Constant.dtwThreshold = Float.parseFloat(dtwThresholdeEditText
						.getText().toString().trim());
				Constant.StartPointThreshold = Float
						.parseFloat(startPointThresholde.getText().toString()
								.trim());
				if (initDirection.getText().toString().trim().length() != 0) {

					Constant.initDirection = Float.parseFloat(initDirection
							.getText().toString().trim());
				}

			}
		});

		reviewbButton = (Button) findViewById(R.id.btn_review);
		reviewbButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mMarker != null) {

					mMarker.hideInfoWindow();
					LatLng fusionlaLatLng = mMarker.getPosition();
					MarkerOptions markerOption = new MarkerOptions();
					markerOption.position(fusionlaLatLng);
					markerOption.title("起点");

					markerOption.draggable(false);
					// markerOption.icon(BitmapDescriptorFactory
					// .fromResource(R.drawable.location_marker));

					// 将Marker设置为贴地显示，可以双指下拉看效果
					markerOption.setFlat(true);
					markerOption.anchor((float) 0.3, (float) 0.3);
					mMarker = aMap.addMarker(markerOption);
					mMarker.showInfoWindow();

					Constant.startlatitude = mMarker.getPosition().latitude;
					Constant.startlongitude = mMarker.getPosition().longitude;
					CalculateLocation2.UpdateInitLocation(
							mMarker.getPosition().latitude,
							mMarker.getPosition().longitude, mMarker);

				}
				reviewbButton.setClickable(false);
				confirmButton.setClickable(false);
				if (magneticSampleTimer != null) {

					magneticSampleTimer.stop();
				}
				if (magneticSampleTimerReview != null) {
					magneticSampleTimerReview = null;
				}
				// aMap.clear();
				// if (magneticSampleTimerReview!=null) {
				// magneticSampleTimerReview.
				// }else {
				//
				// }
				if (magneticSampleTimerReview != null) {
					magneticSampleTimerReview = null;

				}
				magneticSampleTimerReview = new MagneticSampleTimerReview(
						mContext, myhandler, aMap);
				magneticSampleTimerReview.start();

				// new Thread(new Runnable() {
				// public void run() {
				// magneticSampleTimerReview.start();
				// }
				// }).start();
				// 添加楼层图
				// addOverlayToMap();
				setReview(true);
				setStart(false);

				Constant.LengthThreshold = Integer.parseInt(lengthThresholde
						.getText().toString().trim());
				Constant.dtwThreshold = Float.parseFloat(dtwThresholdeEditText
						.getText().toString().trim());
				Constant.StartPointThreshold = Float
						.parseFloat(startPointThresholde.getText().toString()
								.trim());
				if (initDirection.getText().toString().trim().length() != 0) {

					Constant.initDirection = Float.parseFloat(initDirection
							.getText().toString().trim());
				}
			}
		});

		calibrateButton = (Button) findViewById(R.id.btn_calibrate);
		calibrateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// //地磁修正
				// calibrate.calculate();
				// calibrateButton.setClickable(false);

			}
		});

		ToggleButton tb = (ToggleButton) findViewById(R.id.toggleButton_normalize);
		tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					calculateLocation2.normalizeIsOpnen = true;
				} else {
					calculateLocation2.normalizeIsOpnen = false;

				}
			}
		});

		Button coordinateRecord = (Button) findViewById(R.id.btn_record);
		coordinateRecord.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// //地磁修正
				// calibrate.calculate();
				// calibrateButton.setClickable(false);
				SensorDataLogFileRecordCoordinate.trace(DataCenter.correctX
						+ "," + DataCenter.correcty + "\n");

			}
		});

	}

	private boolean ChecksensorAvailable() {
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_SENSOR_ACCELEROMETER)) {
			showGyroscopeNotAvailableAlert("ACCELEROMETER");
			return false;
		}
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_SENSOR_BAROMETER)) {
			showGyroscopeNotAvailableAlert("BAROMETER");
			return false;
		}
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_SENSOR_COMPASS)) {
			showGyroscopeNotAvailableAlert("COMPASS");
			return false;
		}
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_SENSOR_GYROSCOPE)) {
			showGyroscopeNotAvailableAlert("GYROSCOPE");
			return false;
		}
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_SENSOR_LIGHT)) {
			showGyroscopeNotAvailableAlert("LIGHT");
			return false;
		}
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_SENSOR_PROXIMITY)) {
			showGyroscopeNotAvailableAlert("PROXIMITY");
			return false;
		}
	 
		return true;
	}
	private void showGyroscopeNotAvailableAlert(String sensorName)
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set title
		alertDialogBuilder.setTitle(sensorName+" Not Available");

		// set dialog message
		alertDialogBuilder
				.setMessage(
						"Your device is not equipped with a "+sensorName+" or it is not responding. This is *NOT* a problem with the app, it is problem with your device.")
				.setCancelable(false)
				.setNegativeButton("I'll look around...",
						new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int id)
							{
								// if this button is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
//	private void initGPS() {
//		// locationInfoTxt = (TextView) findViewById(R.id.location_info_text);
//		if (!isOPen(mContext)) {
//			openGPS(mContext);
//		}
//		Intent startIntent = new Intent(this, GPSLocationService.class);
//		startService(startIntent);
//		Intent bindIntent = new Intent(this, GPSLocationService.class);
//		bindService(bindIntent, connection, BIND_AUTO_CREATE);
//	}

	private void addOverlayToMap() {
		aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.981893,
				116.32667), 17));// 设置当前地图显示为计算所
		LatLngBounds bounds = new LatLngBounds.Builder()
				.include(new LatLng(39.982036, 116.327046))
				.include(new LatLng(39.981708, 116.327078))
				.include(new LatLng(39.982012, 116.326101))
				.include(new LatLng(39.981695, 116.326069)).build();

		groundoverlay = aMap.addGroundOverlay(new GroundOverlayOptions()
				.anchor(0.5f, 0.5f).transparency(0.1f)
				.image(BitmapDescriptorFactory.fromResource(R.drawable.ict))
				.positionFromBounds(bounds));
	}

	// private Coordinator getGPSlocation() {
	// Coordinator location = gpsBinder.getLocation();
	// Constant.isGPS = true;
	// calculateLocation2.GPSlatitude = location.lat;
	// calculateLocation2.GPSlongitude = location.lon;
	// return location;
	// }
	//
	// private Coordinator setGPSlocation() {
	// Coordinator location = gpsBinder.getLocation();
	// Constant.isGPS = true;
	// calculateLocation2.GPSlatitude = location.lat;
	// calculateLocation2.GPSlongitude = location.lon;
	// calculateLocation2.SatelliteNumber = gpsBinder.getSatelliteNumber();
	// return location;
	// }

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();

		magneticSampleTimer.start();
	//	magneticSampleTimer.getOrientation();
		// //地磁修正
		// calibrate.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();

		mapView.onPause();

		// //地磁修正
		// calibrate.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
		if (magneticSampleTimer != null) {

			magneticSampleTimer.stop();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();

//		GPSThread.interrupt();
//		unbindService(connection);
//		Intent stopIntent = new Intent(this, GPSLocationService.class);
//		stopService(stopIntent);
	 
		android.os.Process.killProcess(android.os.Process.myPid());

	}

	@SuppressLint("HandlerLeak")
	Handler myhandler = new Handler() {
		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {

			// Context context=getActivity();
			try {
				switch (msg.what) {

				// case 1:
				// onLocationChanged((AMapLocation)msg.obj);
				// break;
				// case DebugData.sendUpdate:
				// draw();
				// break;
				// case BluetoothService232.readType:
				// draw();
				// break;
				case BluetoothService232.connectOK:
					// ((Button)(rootView.findViewById(R.id.connect_button))).setText(MainFrag.this.getString(R.string.connected));
					Toast.makeText(mContext, "链接成功.", 5).show();
					break;
				case BluetoothService232.disconnType:
					// ((Button)(rootView.findViewById(R.id.connect_button))).setText(MainFrag.this.getString(R.string.connect));
					Toast.makeText(mContext, "链接断开.", Toast.LENGTH_SHORT)
							.show();
					if (bluetoothService232 != null
							&& bluetoothService232.getState() == BluetoothState.STATE_NONE) {
						bluetoothService232.stop();
						bluetoothService232.connect(null, true);
					}
					break;
				case BluetoothService232.errDisconnType:
					// ((Button)(rootView.findViewById(R.id.connect_button))).setText(MainFrag.this.getString(R.string.connect));
					Toast.makeText(mContext, "链接错误.", Toast.LENGTH_SHORT)
							.show();
					if (bluetoothService232 != null
							&& bluetoothService232.getState() == BluetoothState.STATE_NONE) {
						bluetoothService232.stop();
						bluetoothService232.connect(null, true);
					}
					break;
				case BluetoothService232.conningType:
					// ((Button)(rootView.findViewById(R.id.connect_button))).setText(MainFrag.this.getString(R.string.connecting));
					break;

				case BluetoothService.connectFailure:
					// 未开机 匹配设备不对
					Toast.makeText(mContext,
							"定位模块未打开或配对错误" + "[" + msg.obj + "]",
							Toast.LENGTH_SHORT).show();
					break;
				case BluetoothService.connectNull:
					new AlertDialog.Builder(mContext)
							.setTitle("提示")
							.setMessage("蓝牙已失效，请重新设置蓝牙配对.")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialoginterface,
												int i) {
											FireManActivity.this.finish();
										}
									}).show();
					break;
				case BluetoothService.connectOK:
					// ((ImageView)rootView.findViewById(R.id.blueImg)).setImageDrawable(getResources().getDrawable(R.drawable.bluetooth32));
					Toast.makeText(mContext, "蓝牙链接成功.", Toast.LENGTH_SHORT)
							.show();
					break;
				case BluetoothService.connectLost:
					bluetoothService.stop();
					// bluetoothService.connect(null,true);
					// ((ImageView)rootView.findViewById(R.id.blueImg)).setImageDrawable(getResources().getDrawable(R.drawable.bluetoothgray32));
					Toast.makeText(mContext, "蓝牙链接丢失.", Toast.LENGTH_SHORT)
							.show();
					break;
				case BluetoothService.connectTimer:
					if (bluetoothService != null
							&& bluetoothService.getState() == BluetoothState.STATE_NONE) {
						bluetoothService.stop();
						bluetoothService.connect(null, true);
					}
					//
					// updateStatus();
					break;

				// case GPSUSABLE:
				// // if (!gpsBinder.isLocationUpdated())
				// // break;
				//
				// Coordinator location = gpsBinder.getLocation();
				// if (isLocationIdentical(
				// new Coordinator(location.lon, location.lat),
				// new Coordinator(calculateLocation2.GPSlongitudelast,
				// calculateLocation2.GPSlatitudelast))) {
				// break;
				// }
				//
				//
				// // if (calculateLocation2.GPSlongitude
				// // - calculateLocation2.GPSlongitudelast >0.0000001
				// // || calculateLocation2.GPSlatitude
				// // - calculateLocation2.GPSlatitudelast >0.00000009) {
				//
				// Constant.isGPS = true;
				// calculateLocation2.GPSlatitude = location.lat;
				// calculateLocation2.GPSlongitude = location.lon;
				// calculateLocation2.GPSlongitudelast=calculateLocation2.GPSlongitude;
				// calculateLocation2.GPSlatitudelast =
				// calculateLocation2.GPSlatitude;
				// calculateLocation2.SatelliteNumber = gpsBinder
				// .getSatelliteNumber();
				// SensorDataLogFile.trace("location.lon " + location.lon
				// + " location.lat " + location.lat
				// + " SatelliteNumber "
				// + gpsBinder.getSatelliteNumber() + "\n");
				//
				// SensorDataLogFile.trace(" LocationList "+gpsBinder.getLocationList()+
				// "\n");
				// Toast.makeText(
				// mContext,
				// "GPS Longitude: " + location.lon
				// + "\nLatitude: " + location.lat + "\n"
				// + count, Toast.LENGTH_SHORT).show();
				// ;
				// //
				// calculateLocation.UpdateLocationAndMap(realtimeMagnetismavg,
				// // CONTEXT_IGNORE_SECURITY, TRIM_MEMORY_BACKGROUND)
				// LatLng fusionlaLatLng = new LatLng(location.lat,
				// location.lon);
				// recordTextView.append("GPS Longitude: " + location.lon
				// + "\nLatitude: " + location.lat + "\n" + count
				// + "\n");
				//
				// if (mMarker != null) {
				// mMarker.setPosition(fusionlaLatLng);
				// } else {
				// MarkerOptions markerOption = new MarkerOptions();
				// markerOption.position(fusionlaLatLng);
				//
				// markerOption.draggable(true);
				// markerOption.icon(BitmapDescriptorFactory
				// .fromResource(R.drawable.location_marker));
				// // BitmapDescriptorFactory.fromBitmap(BitmapFactory
				// // .decodeResource(mContext.getResources(),
				// // R.drawable.location_marker)));
				// // 将Marker设置为贴地显示，可以双指下拉看效果
				// markerOption.setFlat(true);
				// markerOption.anchor((float) 0.3, (float) 0.3);
				// mMarker = aMap.addMarker(markerOption);
				// }
				// // }
				//
				// break;
				// case GPSUNUSABLE:
				// // calculateLocation.isGPS=false;
				// String s = "Can not get location.\nTried "
				// + Integer.toString(count) + " seconds.";
				// // Toast.makeText(MainActivity.this,
				// // s,Toast.LENGTH_SHORT).show();;
				// calculateLocation2.SatelliteNumber = gpsBinder
				// .getSatelliteNumber();
				// break;
				// case CHECKGPSISOPEN:
				// Toast.makeText(mContext, "请打开GPS开关", Toast.LENGTH_SHORT)
				// .show();
				// break;

				case CalculateLocation2.DTWCORRENT:

					String ssString6 = (String) msg.obj;
					;
					// Toast.makeText(mContext, ssString6, Toast.LENGTH_SHORT)
					// .show();
					;
					recordTextView.append(ssString6 + "\n");
					if (Constant.isDebug) {
						SensorDataLogFileRecord.trace(ssString6 + "\n");
					}
					break;
				case CalculateLocation2.STAIRCORRENT:

					String ssString8 = (String) msg.obj;
					;
					Toast.makeText(mContext, ssString8, Toast.LENGTH_SHORT)
							.show();
					;
					recordTextView.append(ssString8 + "\n");
					SensorDataLogFileRecord.trace(ssString8 + "\n");
					break;

				case MagneticSampleTimer.REALTIMERECORD:

					String ssString7 = (String) msg.obj;
					;
					// Toast.makeText(MainActivity.this,
					// ssString7,Toast.LENGTH_SHORT).show();;
					magneticdirectiontTextView.setText(ssString7);
					break;

				case Calibrate.CALIBRATE:
					String ssString9 = (String) msg.obj;
					calibrateButton.setText(ssString9);
					if (ssString9.equals("Done")) {
						calibrateButton.setClickable(true);

					}
					recordTextView.setText("" + ssString9);
					break;
				case LocationNetwork.INIT_LOCATION:

					AMapLocation location = (AMapLocation) msg.obj;
					Double geoLat = location.getLatitude();
					Double geoLng = location.getLongitude();
					String str = ("定位成功:(" + geoLng + "," + geoLat + ")"
							+ "\n精    度    :" + location.getAccuracy() + "米"
							+ "\n定位方式:" + location.getProvider());
					aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
							new LatLng(geoLat, geoLng), 17));// 设置当前地图显示为计算所
					// LatLng fusionlaLatLng = new LatLng(geoLat, geoLng);
					// if (mMarker != null) {
					// mMarker.setPosition(fusionlaLatLng);
					// }
					recordTextView.setText("" + str);

					break;
				}

			} catch (Exception e) {
				// LogFile.trace(e);
			}
		}

	};

	public boolean isLocationIdentical(Coordinator locA, Coordinator locB) {
		if (locA == null) {
			if (locB == null)
				return true;
			else
				return false;
		} else {
			if (locB == null)
				return false;
		}
		if (Math.abs(locA.lon + locA.lat - locB.lon - locB.lat) < 0.00000000000001)
			return true;
		else
			return false;
	}

	/**
	 * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
	 * 
	 * @param context
	 * @return true 表示开启
	 */
	public boolean isOPen(final Context context) {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
		boolean network = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (gps || network) {
			return true;
		}

		return false;
	}

	/**
	 * 强制帮用户打开GPS
	 * 
	 * @param context
	 */
	public void openGPS(Context context) {
		LocationManager alm = (LocationManager) FireManActivity.this
				.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			Toast.makeText(context, "GPS模块正常", Toast.LENGTH_SHORT).show();
		}
		Toast.makeText(context, "请开启GPS！", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		FireManActivity.this.startActivityForResult(intent, 0); // 此为设置完成后返回到获取界面
	}

	public static boolean createDumpFile(Context context) {  
	    String LOG_PATH = "/dump.gc/";  
	    boolean bool = false;  
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ssss");  
	    String createTime = sdf.format(new Date(System.currentTimeMillis()));  
	    String state = android.os.Environment.getExternalStorageState();  
	    // 判断SdCard是否存在并且是可用的  
	    if(android.os.Environment.MEDIA_MOUNTED.equals(state)){  
	        File file = new File(Environment.getExternalStorageDirectory().getPath() +LOG_PATH);  
	        if(!file.exists()) {  
	            file.mkdirs();  
	        }  
	        String hprofPath = file.getAbsolutePath();  
	        if(!hprofPath.endsWith("/")) {  
	            hprofPath+= "/";  
	        }                
	         
	        hprofPath+= createTime + ".hprof";  
	        try {  
	            android.os.Debug.dumpHprofData(hprofPath);  
	            bool= true;  
	            Log.d("ANDROID_LAB", "create dumpfile done!");  
	        }catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    } else {  
	        bool= false;  
	        Log.d("ANDROID_LAB", "nosdcard!");  
	    }  
	     
	    return bool;  
	}  
}
