/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wzg.dingwei;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ict.wq.SensorDataLogFilePedometer;
import com.ict.wq.model.OneTime;
import com.wzg.dingwei.util.Utils;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for incoming
 * connections, a thread for connecting with a device, and a thread for
 * performing data transmissions when connected.
 */
public class BluetoothService {
	static public final int connectFailure = 10008;
	static public final int connectNull = 10009;
	static public final int connectOK = 10010;
	static public final int connectLost = 10011;
	static public final int connectTimer = 10012;
	// Debugging
	private static final String TAG = "BluetoothChatService";
	private static final boolean D = true;

	// Name for the SDP record when creating server socket
	private static final String NAME_SECURE = "BluetoothChatSecure";
	private static final String NAME_INSECURE = "BluetoothChatInsecure";

	// Unique UUID for this application
	private static final UUID MY_UUID_SECURE = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final UUID MY_UUID_INSECURE = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	// Member fields
	private final BluetoothAdapter mAdapter;
	private final Handler mHandler;
	private AcceptThread mSecureAcceptThread;
	private AcceptThread mInsecureAcceptThread;
	private ConnectThread mConnectThread;
	private ConnectedReadThread mConnectedReadThread;
	private ConnectedWriteThread mConnectedWriteThread;
	private static int mState;

	// Constants that indicate the current connection state
	// public static final int STATE_NONE = 0; // we're doing nothing
	// public static final int STATE_LISTEN = 1; // now listening for incoming
	// // connections
	// public static final int STATE_CONNECTING = 2; // now initiating an
	// outgoing
	// // connection
	// public static final int STATE_CONNECTED = 3; // now connected to a remote
	// // device
	private Context ctx;

	Long lastTime = (long) 0;
	private static final String BLUETOOTH_STATE_CHANGE = "bluetooth.state.change";

	private String bluetoothData = "";
	private String mDeviceAddress = "";

	// wq start;
 
	private ArrayList<OneTime> OneTimeArrayList = new ArrayList<OneTime>();
	private ArrayList<ArrayList<OneTime>> segmentaArrayList = new ArrayList<ArrayList<OneTime>>();
	public static final int BluetoothServiceDTWMiniValue = 100;
	public static final int BluetoothServiceDTWReplace = 101;
	// public static final int BluetoothServiceDTWMiniValue=100;
	
	private float lastx=0;
	private float lasty=0;
	private float lastz=0;
 
	final String filepath;
 

	/**
	 * Constructor. Prepares a new BluetoothChat session.
	 * 
	 * @param context
	 *            The UI Activity Context
	 * @param handler
	 *            A Handler to send messages back to the UI Activity
	 */
	public BluetoothService(Context context, Handler handler) {
		ctx = context;
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mAdapter.enable();
		mState = BluetoothState.STATE_NONE;
		mHandler = handler;


		String sdcardPath = Environment.getExternalStorageDirectory()
				.toString();
		filepath = sdcardPath + File.separator + "Sensordata" + File.separator
				+ "SensorDataLogFile20151109100319.txt";
	}

	/**
	 * Set the current state of the chat connection
	 * 
	 * @param state
	 *            An integer defining the current connection state
	 */
	private synchronized void setState(int state) {
		if (D)
			Log.d(TAG, "setState() " + mState + " -> " + state);
		mState = state;

	}

	/**
	 * Return the current connection state.
	 */
	public synchronized int getState() {
		return mState;
	}

	/**
	 * Start the chat service. Specifically start AcceptThread to begin a
	 * session in listening (server) mode. Called by the Activity onResume()
	 */
	public synchronized void start() {
		if (D)
			Log.d(TAG, "start");

		// Cancel any thread attempting to make a connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mConnectedReadThread != null) {
			mConnectedReadThread.cancel();
			mConnectedReadThread = null;
		}
		// Cancel any thread currently running a connection
		if (mConnectedWriteThread != null) {
			mConnectedWriteThread.cancel();
			mConnectedWriteThread = null;
		}

		setState(BluetoothState.STATE_LISTEN);

		// Start the thread to listen on a BluetoothServerSocket
		if (mSecureAcceptThread == null) {
			mSecureAcceptThread = new AcceptThread(true);
			mSecureAcceptThread.start();
		}
		if (mInsecureAcceptThread == null) {
			mInsecureAcceptThread = new AcceptThread(false);
			mInsecureAcceptThread.start();
		}

		// Intent bluetooth_connect_state = new Intent(
		// InitFunction.BLUETOOTH_STATE);
		// bluetooth_connect_state.putExtra(
		// BluetoothState.bluetooth_connect_state,
		// BluetoothState.STATE_CONNECT_FAIL);
		// ctx.sendBroadcast(bluetooth_connect_state);
		Log.d("log", "蓝牙连接失败");

	}

	/**
	 * Start the ConnectThread to initiate a connection to a remote device.
	 * 
	 * @param device
	 *            The BluetoothDevice to connect
	 * @param secure
	 *            Socket Security type - Secure (true) , Insecure (false)
	 */
	public synchronized void connect(BluetoothDevice device, boolean secure) {
		if (D)
			Log.d(TAG, "connect to: " + device);
		if (device == null) {
			Set<BluetoothDevice> devices = BluetoothAdapter.getDefaultAdapter()
					.getBondedDevices();
			if (devices.size() > 0) {
				for (Iterator<BluetoothDevice> it = devices.iterator(); it
						.hasNext();) {
					BluetoothDevice mDevice = (BluetoothDevice) it.next();
					// 打印出远程蓝牙设备的物理地址
					if (mDevice.getName().contains("Amp'ed")) {
						device = mDevice;
						DataCenter.deviceid = mDevice.getAddress()
								.substring(12).replace(":", "").toUpperCase();
						Utils.saveSharedPreferences(ctx, "BT_LOCATION_ADDRESS",
								mDevice.getAddress());
						Log.d(TAG, mDevice.getName());
						break;
					}
				}
			}
		} else {
			DataCenter.deviceid = device.getAddress().substring(12)
					.replace(":", "").toUpperCase();
			String[] deviceNameArr = device.getName().split("_");
			if (deviceNameArr.length > 0)
				DataCenter.deviceid = deviceNameArr[deviceNameArr.length - 1]
						.toUpperCase();
		}

		if (device == null) {
			SharedPreferences preParams = PreferenceManager
					.getDefaultSharedPreferences(ctx);
			mDeviceAddress = preParams.getString("BT_LOCATION_ADDRESS",
					mDeviceAddress);
			DataCenter.deviceid = mDeviceAddress.substring(12).replace(":", "")
					.toUpperCase();
			device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(
					mDeviceAddress);

			if (device.getBondState() != BluetoothDevice.BOND_BONDED) {// 判断给定地址下的device是否已经配对

			}
		}

		if (device == null)
			return;

		// device =
		// BluetoothAdapter.getDefaultAdapter().getRemoteDevice("00:0E:0E:00:0F:D5");
		// device =
		// BluetoothAdapter.getDefaultAdapter().getRemoteDevice(DataCenter.addressBT);

		Log.d("log", "开始连接蓝牙设备，mac地址 : " + device.getAddress());

		// Cancel any thread attempting to make a connection
		if (mState == BluetoothState.STATE_CONNECTING) {
			if (mConnectThread != null) {
				mConnectThread.cancel();
				mConnectThread = null;
			}

			Log.d("log", "蓝牙正在连接，请等等");
			return;
		}

		// Cancel any thread attempting to make a connection
		if (mState == BluetoothState.STATE_CONNECTED) {
			Log.d("log", "蓝牙已连接，无法再连接");
			return;
		}

		// Cancel any thread currently running a connection
		if (mConnectedReadThread != null) {
			mConnectedReadThread.cancel();
			mConnectedReadThread = null;
		}
		// Cancel any thread currently running a connection
		if (mConnectedWriteThread != null) {
			mConnectedWriteThread.cancel();
			mConnectedWriteThread = null;
		}

		// Start the thread to connect with the given device
		mConnectThread = new ConnectThread(device, secure);
		mConnectThread.start();
		setState(BluetoothState.STATE_CONNECTING);
	}

	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection
	 * 
	 * @param socket
	 *            The BluetoothSocket on which the connection was made
	 * @param device
	 *            The BluetoothDevice that has been connected
	 */
	public synchronized void connected(BluetoothSocket socket,
			BluetoothDevice device, final String socketType) {
		if (D)
			Log.d(TAG, "connected, Socket Type:" + socketType);

		// Cancel the thread that completed the connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mConnectedReadThread != null) {
			mConnectedReadThread.cancel();
			mConnectedReadThread = null;
		}
		// Cancel any thread currently running a connection
		if (mConnectedWriteThread != null) {
			mConnectedWriteThread.cancel();
			mConnectedWriteThread = null;
		}

		// Cancel the accept thread because we only want to connect to one
		// device
		if (mSecureAcceptThread != null) {
			mSecureAcceptThread.cancel();
			mSecureAcceptThread = null;
		}
		if (mInsecureAcceptThread != null) {
			mInsecureAcceptThread.cancel();
			mInsecureAcceptThread = null;
		}

		// Start the thread to manage the connection and perform transmissions
		mConnectedReadThread = new ConnectedReadThread(socket, socketType);
		mConnectedReadThread.start();
		// Start the thread to manage the connection and perform transmissions
		mConnectedWriteThread = new ConnectedWriteThread(socket, socketType);
		mConnectedWriteThread.start();

		Log.d("log", "蓝牙已连接成功！");

		setState(BluetoothState.STATE_CONNECTED);
	}

	/**
	 * Stop all threads
	 */
	public synchronized void stop() {
		if (D)
			Log.d(TAG, "stop");

		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mConnectedReadThread != null) {
			mConnectedReadThread.cancel();
			mConnectedReadThread = null;
		}

		if (mConnectedWriteThread != null) {
			mConnectedWriteThread.cancel();
			mConnectedWriteThread = null;
		}

		if (mSecureAcceptThread != null) {
			mSecureAcceptThread.cancel();
			mSecureAcceptThread = null;
		}

		if (mInsecureAcceptThread != null) {
			mInsecureAcceptThread.cancel();
			mInsecureAcceptThread = null;
		}
		setState(BluetoothState.STATE_NONE);
	}

	/**
	 * Indicate that the connection attempt failed and notify the UI Activity.
	 */
	private void connectionFailed() {
		Log.d("log", "bluetooth connectionFailed!!!");
		this.start();
		setState(BluetoothState.STATE_NONE);
	}

	/**
	 * Indicate that the connection was lost and notify the UI Activity.
	 */
	private void connectionLost() {
		Log.d("log", "bluetooth connectionLost!!!");
		DataCenter.commandToBT = "140014";
		mHandler.obtainMessage(BluetoothService.connectLost).sendToTarget();
		setState(BluetoothState.STATE_NONE);
	}

	/**
	 * This thread runs while listening for incoming connections. It behaves
	 * like a server-side client. It runs until a connection is accepted (or
	 * until cancelled).
	 */
	private class AcceptThread extends Thread {
		// The local server socket
		private BluetoothServerSocket mmServerSocket;
		private String mSocketType;

		@SuppressLint("NewApi")
		public AcceptThread(boolean secure) {
			BluetoothServerSocket tmp = null;
			mSocketType = secure ? "Secure" : "Insecure";

			// Create a new listening server socket
			try {
				if (secure) {
					tmp = mAdapter.listenUsingRfcommWithServiceRecord(
							NAME_SECURE, MY_UUID_SECURE);
				} else {
					tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(
							NAME_INSECURE, MY_UUID_INSECURE);
				}
			} catch (IOException e) {
				Log.e(TAG, "Socket Type: " + mSocketType + "listen() failed", e);
			}
			mmServerSocket = tmp;
		}

		public void run() {
			if (D)
				Log.d(TAG, "Socket Type: " + mSocketType
						+ "BEGIN mAcceptThread" + this);
			setName("AcceptThread" + mSocketType);

			BluetoothSocket socket = null;

			// Listen to the server socket if we're not connected
			while (mState != BluetoothState.STATE_CONNECTED) {
				try {
					// This is a blocking call and will only return on a
					// successful connection or an exception
					socket = mmServerSocket.accept();
				} catch (IOException e) {
					Log.e(TAG, "Socket Type: " + mSocketType
							+ "accept() failed", e);
					break;
				}

				// If a connection was accepted
				if (socket != null) {
					synchronized (BluetoothService.this) {
						switch (mState) {
						case BluetoothState.STATE_LISTEN:
						case BluetoothState.STATE_CONNECTING:
							// Situation normal. Start the connected thread.
							connected(socket, socket.getRemoteDevice(),
									mSocketType);
							break;
						case BluetoothState.STATE_NONE:
						case BluetoothState.STATE_CONNECTED:
							// Either not ready or already connected. Terminate
							// new socket.
							try {
								socket.close();
							} catch (IOException e) {
								Log.e(TAG, "Could not close unwanted socket", e);
							}
							break;
						}
					}
				}
			}
			if (D)
				Log.i(TAG, "END mAcceptThread, socket Type: " + mSocketType);

		}

		public void cancel() {

			if (D)
				Log.d(TAG, "Socket Type" + mSocketType + "cancel " + this);
			try {
				mmServerSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "Socket Type" + mSocketType
						+ "close() of server failed", e);
			}

		}
	}

	/**
	 * This thread runs while attempting to make an outgoing connection with a
	 * device. It runs straight through; the connection either succeeds or
	 * fails.
	 */
	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;
		private String mSocketType;
		private int failureCount = 0;

		@SuppressLint("NewApi")
		public ConnectThread(BluetoothDevice device, boolean secure) {
			mmDevice = device;
			BluetoothSocket tmp = null;
			mSocketType = secure ? "Secure" : "Insecure";

			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			try {
				if (secure) {
					tmp = device
							.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
				} else {
					tmp = device
							.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
				}
			} catch (IOException e) {
				Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
			}
			mmSocket = tmp;
		}

		public void run() {
			if (mmSocket == null) {
				mHandler.obtainMessage(BluetoothService.connectNull)
						.sendToTarget();
				BluetoothService.this.stop();
				return;
			}
			Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
			setName("ConnectThread" + mSocketType);

			mAdapter.cancelDiscovery();

			while (true)
				try {
					// successful connection or an exception
					mmSocket.connect();
					break;
				} catch (IOException e) {

					failureCount++;
					mHandler.obtainMessage(BluetoothService.connectFailure,
							failureCount).sendToTarget();
					Log.e(TAG, "mmSocket.connect failed");
					// 可能模块未开机,1000ms链接一次
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			// Reset the ConnectThread because we're done
			synchronized (BluetoothService.this) {
				mConnectThread = null;
			}
			mHandler.obtainMessage(BluetoothService.connectOK).sendToTarget();
			// Start the connected thread
			connected(mmSocket, mmDevice, mSocketType);
		}

		public void cancel() {

			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect " + mSocketType
						+ " socket failed", e);
			}
		}
	}

	/**
	 * This thread runs during a connection with a remote device. It handles all
	 * incoming and outgoing transmissions.
	 */
	private class ConnectedReadThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		float x = 0;
		float y = 0;

		public ConnectedReadThread(BluetoothSocket socket, String socketType) {
			Log.d(TAG, "create ConnectedThread: " + socketType);
			mmSocket = socket;
			InputStream tmpIn = null;

			// Get the BluetoothSocket input and output streams
			try {
				tmpIn = socket.getInputStream();
			} catch (IOException e) {
				Log.e(TAG, "temp sockets not created", e);
			}

			mmInStream = tmpIn;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectedThread");
			byte[] buffer = new byte[1024];
			int bytes;

			int[] header = new int[4];
			String[] packetDataHeader = new String[4];
			int packet_number_old = -1;
			double[] payload = new double[4];
			double[] delta = new double[3];

			// Keep listening to the InputStream while connected
			while (true) {
				try {
					// Read from the InputStream
					bytes = mmInStream.read(buffer);
					if (bytes == -1) {
						connectionLost();
						break;
					}
					String DataItem = BluetoothDataUtil.toHex(buffer, bytes);
					DataCenter.commandToBT = "";

					bluetoothData = bluetoothData + DataItem.toUpperCase();
					// bluetoothData="AA 00 01 3A 37 E2 67 6E 34 C5 83 52 B9 FE B0 52 39 80 BE 5B 37 1D D7 64 A8 C2 93 F8 AB ED 34 F3 2E 4D 0E A5 37 1D D7 65 AC A8 B1 A0 AF 22 71 18 37 1D 9D 6D 29 54 78 20 36 5E 8D AC 00 01 1B 6D ";
					bluetoothData = bluetoothData.replace(" ", "");
					int headerAAIndex = bluetoothData.indexOf("AA");
					// System.out.println(String.);

					System.out.println("headerAAIndex" + headerAAIndex);
					if (headerAAIndex != -1) {
						// bluetoothData=bluetoothData.substring(headerAAIndex);
						String bluetoothDataAA = bluetoothData
								.substring(headerAAIndex);
						if (bluetoothDataAA.length() >= 128) {
							String packetsData = bluetoothDataAA.substring(0,
									128);
							if (isValidate(packetsData)) {
								String x_pos = packetsData.substring(8, 16);
								String y_pos = packetsData.substring(16, 24);
								String z_pos = packetsData.substring(24, 32);
								String a_pos = packetsData.substring(32, 40);

								payload[0] = Float.intBitsToFloat((int) Long
										.parseLong(x_pos, 16));
								payload[1] = Float.intBitsToFloat((int) Long
										.parseLong(y_pos, 16));
								payload[2] = Float.intBitsToFloat((int) Long
										.parseLong(z_pos, 16));
								payload[3] = Float.intBitsToFloat((int) Long
										.parseLong(a_pos, 16));

								packetDataHeader[0] = packetsData.substring(0,
										2);
								packetDataHeader[1] = packetsData.substring(2,
										4);
								packetDataHeader[2] = packetsData.substring(4,
										6);
								packetDataHeader[3] = packetsData.substring(6,
										8);

								int packet_number_1 = (int) Long.parseLong(
										packetsData.substring(2, 4), 16);// new
																			// BigInteger(packetsData.substring(2,
																			// 4),
																			// 2).intValue();
								int packet_number_2 = (int) Long.parseLong(
										packetsData.substring(4, 6), 16);// new
																			// BigInteger(packetsData.substring(4,
																			// 6),
																			// 2).intValue();
								DataCenter.ack = createAck(packet_number_1,
										packet_number_2); // Acknowledgement
															// created
								int packet_number = packet_number_1 * 256
										+ packet_number_2; // PACKAGE NUMBER
															// ASSIGNED
								if (packet_number_old != -1
										&& packet_number_old != packet_number) // Perform
																				// Stepwise
																				// Dead
																				// Reckoning
																				// on
																				// receiving
																				// new
																				// data
																				// packet
								{
									// stepwise_dr_tu(); // Perform Stepwise
									// Dead Reckoning on the received data
									if (DataCenter.debugFlag)
										DataCenter.blueBuf.writeData(
												BluetoothDataUtil
														.hex2byte(packetsData),
												-1, -1);
									float sin_phi = (float) Math
											.sin(DataCenter.x_sw[3]); //
									float cos_phi = (float) Math
											.cos(DataCenter.x_sw[3]); //
									delta[0] = cos_phi * payload[0] - sin_phi
											* payload[1]; // Transform
															// /
															// rotate
															// two
															// dimensional
															// displacement
															// vector
															// (dx[0],
									delta[1] = sin_phi * payload[0] + cos_phi
											* payload[1]; // Transform
															// /
															// rotate
															// two
															// dimensional
															// displacement
															// vector
															// (dx[0],
									delta[2] = payload[2]; // Assuming only
															// linear
															// changes
															// along z-axis
									DataCenter.x_sw[0] += delta[0]; // Final
																	// X
																	// in
																	// the
																	// user’s
																	// reference
																	// frame
									DataCenter.x_sw[1] += delta[1]; // Final
																	// Y
																	// in
																	// the
																	// user’s
																	// reference
																	// frame
									DataCenter.x_sw[2] += delta[2]; // Final
																	// Z
																	// in
																	// the
																	// user’s
																	// reference
																	// frame
									DataCenter.x_sw[3] += payload[3]; // Cumulative
																		// change
																		// in
																		// orientation
																		// in
																		// the
																		// user’s
																		// reference
																		// frame

									packet_number_old = packet_number;

								}
								if (packet_number_old == -1)
									packet_number_old = packet_number;

								Intent bluetoothDataIntent = new Intent(
										"com.wzg.dingwei.XYZDataAction");
								StringBuilder item = new StringBuilder();
								item.append(DataCenter.x_sw[0]).append(",")
										.append(DataCenter.x_sw[1]).append(",")
										.append(DataCenter.x_sw[2]).append(",")
										.append(DataCenter.x_sw[3]);
								bluetoothDataIntent.putExtra("POS_XYZ_DATA",
										item.toString());
								ctx.sendBroadcast(bluetoothDataIntent);
								bluetoothData = bluetoothDataAA.substring(128);
//								if (Math.abs(DataCenter.x_sw[0] - x) > 0.1
//										|| Math.abs(DataCenter.x_sw[1] - y) > 0.1) {
//									System.out.println("移动了");
									DataCenter.pos[0] = (float) DataCenter.x_sw[0];


									DataCenter.pos[1] = (float) DataCenter.x_sw[1];
									DataCenter.pos[2] = (float) DataCenter.x_sw[2];
									
									DataCenter.originX = (float) DataCenter.x_sw[0];
									DataCenter.originy = (float) DataCenter.x_sw[1];
									DataCenter.originz = (float) DataCenter.x_sw[2];
								//	System.out.println("(float) DataCenter.x_sw[2]"+(float) DataCenter.x_sw[2]);
									//计步器原始数据存入文件，用于分析楼梯步长异常
//								Log.d("AAA",""+DataCenter.pos[0]+" "+ lastx+" "+DataCenter.pos[1]+" "+ lasty+" "+DataCenter.pos[2]+" "+ lastz);
//									if (Math.abs(DataCenter.pos[0] - lastx) > 1 || Math.abs(DataCenter.pos[1] - lasty) > 1||Math.abs(DataCenter.pos[2] - lastz) > 1) {
//										Log.d("AAA",""+Math.abs(DataCenter.pos[0] - lastx));
////										DataCenter.lastX=DataCenter.originX;
////										DataCenter.lastY=DataCenter.originy;
//										lastx=DataCenter.pos[0];
//										lasty=DataCenter.pos[1];
//										lastz=DataCenter.pos[2];
//
//
//										//	SensorDataLogFilePedometer.trace(DataCenter.pos[0]+","+DataCenter.pos[1]+","+DataCenter.pos[2]+"\n");
//									}

									
									//把距离缩小100当作经纬度被放到地图
//									Longitude longitude=new Longitude();
//									double[] arr=longitude.getLong(DataCenter.x_sw[0], DataCenter.x_sw[1]);
//									DataCenter.pos[0]=(float) arr[0];
//									DataCenter.pos[1]=(float) arr[1];
//									DataCenter.pos[2] = (float) DataCenter.x_sw[2];
									DataCenter.adjust3
											.adjustAddPos(DataCenter.pos);
									
									System.out.println("DataCenter.Arr "+DataCenter.pos[0]+"  "+DataCenter.pos[1]);
//								} else {
//								//	System.out.println("没有移动了");
//								}
								x = (float) DataCenter.x_sw[0];
								y = (float) DataCenter.x_sw[1];

								// verify();
								// reappear();
							} else {
								bluetoothData = bluetoothData
										.substring(headerAAIndex + 2);
							}
						}
					}

				} catch (IOException e) {
					Log.e(TAG, "disconnected", e);
					connectionLost();
					break;
				}
			}

		}

		public void cancel() {

			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}

		public byte[] createAck(int packet_number_1, int packet_number_2) {
			byte[] ack = new byte[5];
			ack[0] = 0x01; // 1st byte
			ack[1] = (byte) packet_number_1; // 2nd byte
			ack[2] = (byte) packet_number_2; // 3rd byte
			ack[3] = (byte) ((1 + packet_number_1 + packet_number_2 - (1 + packet_number_1 + packet_number_2) % 256) / 256); // 4th
																																// byte
																																// –
																																// Quotient
																																// of
																																// {(1+P1+P2)
																																// div
																																// 256}
			ack[4] = (byte) ((1 + packet_number_1 + packet_number_2) % 256); // 5th
																				// byte-
																				// Remainder
																				// of
																				// {(1+P1+P2)div
																				// 256}
			return ack;
		}

		public String reHexString(String strHex) {
			String result = "";
			for (int i = 0; i < 4; i++) {
				String hex = strHex.substring(6 - i * 2, 8 - i * 2);
				result = result + hex;
			}
			return result;
		}

		public boolean isValidate(String data) {
			int sum = 0;
			if (data.toString().length() >= 8) {

				String temp = new String(data.toString());

				for (int si = 0; si < temp.length() - 4; si += 2) {
					String itemStr = temp.substring(si, si + 2);
					int item = Integer.parseInt(itemStr, 16);
					sum += item;
				}
				StringBuilder hex = new StringBuilder(
						Integer.toHexString(sum & 0xFF));

				if (hex.length() == 1) {
					hex = new StringBuilder(hex.insert(0, '0').toString()
							.toUpperCase());
				}
				// X1 chengdu String dataSumValidate =
				// data.substring(data.length() -4,data.length() -2);
				String dataSumValidate = data.substring(data.length() - 2,
						data.length());

				return dataSumValidate.equalsIgnoreCase(hex.toString()
						.toUpperCase());

			}
			return false;
		}

		public boolean isValidateX1(String data) {
			int sum = 0;
			if (data.toString().length() >= 8) {

				String temp = new String(data.toString());

				for (int si = 0; si < temp.length() - 4; si += 2) {
					String itemStr = temp.substring(si, si + 2);
					int item = Integer.parseInt(itemStr, 16);
					sum += item;
				}
				StringBuilder hex = new StringBuilder(
						Integer.toHexString(sum & 0xFF));

				if (hex.length() == 1) {
					hex = new StringBuilder(hex.insert(0, '0').toString()
							.toUpperCase());
				}

				// X1 chengdu
				String dataSumValidate = data.substring(data.length() - 4,
						data.length() - 2);
				// MIMU
				// String dataSumValidate = data.substring(data.length()
				// -2,data.length());

				return dataSumValidate.equalsIgnoreCase(hex.toString()
						.toUpperCase());

			}
			return false;
		}

		public double getDistance(double x1, double y1, double x2, double y2) {
			double d = (x2 - x1) * (x2 - x1) - (y2 - y1) * (y2 - y1);
			return Math.sqrt(d);
		}

	}

	/**
	 * This thread runs during a connection with a remote device. It handles all
	 * incoming and outgoing transmissions.
	 */
	private class ConnectedWriteThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final OutputStream mmOutStream;

		public ConnectedWriteThread(BluetoothSocket socket, String socketType) {
			Log.d(TAG, "create ConnectedThread: " + socketType);
			mmSocket = socket;
			OutputStream tmpOut = null;

			// Get the BluetoothSocket input and output streams
			try {
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				Log.e(TAG, "temp sockets not created", e);
			}

			mmOutStream = tmpOut;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectedThread");

			// Keep listening to the InputStream while connected
			while (true) {
				if (mmOutStream != null) {

					if (DataCenter.commandToBT.length() != 0) {
						try {
							mmOutStream
									.write(hexStringToBytes(DataCenter.commandToBT));

						} catch (IOException e) {
							Log.e(TAG, "commandToBT failed", e);
							BluetoothService.this.connectionLost();
							break;
						}

					}

					if (DataCenter.ack != null) {
						try {
							mmOutStream.write(DataCenter.ack);
							DataCenter.ack = null;
						} catch (IOException e) {
							Log.e(TAG, "createAck failed", e);
							BluetoothService.this.connectionLost();
							return;
						}
					}

					// Date dt= new Date();dt.getTime();
					Long time = System.currentTimeMillis();

					// 隔200ms发一次数据包
					if (lastTime != 0 && time - lastTime > 200) {
						/*
						 * byte[] dataPacket=DataCenter.getDataPacket(); if
						 * (dataPacket!=null) { try {
						 * mmOutStream.write(dataPacket); // 保存发送数据
						 * DataCenter.sendBuf.writeData(dataPacket,-1,-1); }
						 * catch (IOException e) { Log.e(TAG,
						 * "send dataPacket failed", e);
						 * BluetoothService.this.connectionLost();
						 * 
						 * } }
						 */
					}
					lastTime = time;

				}
			}
		}

		public byte[] hexStringToBytes(String hexString) {
			if (hexString == null || hexString.equals("")) {
				return null;
			}
			hexString = hexString.toUpperCase();
			int length = hexString.length() / 2;
			char[] hexChars = hexString.toCharArray();
			byte[] d = new byte[length];
			for (int i = 0; i < length; i++) {
				int pos = i * 2;
				d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
			}
			return d;
		}

		private byte charToByte(char c) {
			return (byte) "0123456789ABCDEF".indexOf(c);
		}

		public void cancel() {

			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}
}
