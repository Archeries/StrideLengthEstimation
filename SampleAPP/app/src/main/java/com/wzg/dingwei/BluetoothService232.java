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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.UUID;

import com.wzg.dingwei.TCPSocketClient.SocketState;
import com.wzg.dingwei.util.Utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.net.NetworkInfo.State;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for incoming
 * connections, a thread for connecting with a device, and a thread for
 * performing data transmissions when connected.
 */
public class BluetoothService232 {
	static public final int errDisconnType = 10005;
	static public final int connectNull = 10007;
	static public final int connectOK = 10002;
	static public final int disconnType = 10004;
	static public final int conningType = 10006;
	static public final int readType = 10003;
	static public final int sendType = 1000;
	static public final int sendTypeStr = 1001;
	
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

	
	private Context ctx;
	private int revLen=0;
	private int MaxBuffer=1024;
	private byte[] tempRevBuffer=new byte[MaxBuffer];
	private byte[] revBuffer=new byte[MaxBuffer];
	private OutputStream mmOutStream;
	
	Long lastTime=(long) 0;
	private static final String BLUETOOTH_STATE_CHANGE = "bluetooth.state.change";

	private String bluetoothData = "";
	private String mDeviceAddress="";
	/**
	 * Constructor. Prepares a new BluetoothChat session.
	 * 
	 * @param context
	 *            The UI Activity Context
	 * @param handler
	 *            A Handler to send messages back to the UI Activity
	 */
	public BluetoothService232(Context context, Handler handler) {
		ctx = context;
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = BluetoothState.STATE_NONE;
		mHandler = handler;
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
		if(device==null)
		{
			Set<BluetoothDevice> devices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
		    if(devices.size()>0){
		    	for(Iterator<BluetoothDevice> it = devices.iterator();it.hasNext();){
		    		BluetoothDevice mDevice = (BluetoothDevice)it.next();
		    	  
		    		if(mDevice.getName().contains("BT4-"))
		    		{
		    			device=mDevice;
		    			Utils.saveSharedPreferences(ctx,"BT_SOCKET_ADDRESS",mDevice.getAddress());
		    			break;
		    		}
		    	}
		    }
		}
		
		if(device==null)
		{
			
	    	 SharedPreferences preParams = PreferenceManager.getDefaultSharedPreferences(ctx);
			 mDeviceAddress=preParams.getString("BT_SOCKET_ADDRESS", mDeviceAddress);
			 device= BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mDeviceAddress);
			 
			 if(device.getBondState() != BluetoothDevice.BOND_BONDED){//判断给定地址下的device是否已经配对  
				 //Toast.makeText(DataCenter.global_context, "还没有已配对的远程蓝牙设备", 10).show();//System.out.println("还没有已配对的远程蓝牙设备！");
			 }
		}
		
		if(device==null) return;
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
		mHandler.obtainMessage(BluetoothService232.disconnType).sendToTarget();
		setState(BluetoothState.STATE_NONE);
	}
	
	
	public boolean send(byte[] data,int offset,int len) 
	{
			try {
				if(mmOutStream==null) {return false;}
				mmOutStream.write(data,offset,len);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				LogFile.trace("error send\n");
				return false;
			}
			
	}
	
	
	public boolean isOpen() 
	{
		return mState==BluetoothState.STATE_CONNECTED;
	}
	
	public boolean isOpen2() 
	{
		return isOpen()&&DataCenter.dataStrength>=1;
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
					synchronized (BluetoothService232.this) {
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
		private int failureCount=0;
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
			if(mmSocket==null)
			{
				mHandler.obtainMessage(BluetoothService232.connectNull).sendToTarget();
				BluetoothService232.this.stop();
				return;
			}
			Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
			setName("ConnectThread" + mSocketType);
			
			mAdapter.cancelDiscovery();

			while(true)
			try {
				// successful connection or an exception
				mmSocket.connect();
				break;
			} catch (IOException e) {
				
				failureCount++;
				mHandler.obtainMessage(BluetoothService232.errDisconnType,failureCount).sendToTarget();
				Log.e(TAG, "mmSocket.connect failed");
				//可能模块未开机,1000ms链接一次
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			// Reset the ConnectThread because we're done
			synchronized (BluetoothService232.this) {
				mConnectThread = null;
			}
			mHandler.obtainMessage(BluetoothService232.connectOK).sendToTarget();
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
			
			// Keep listening to the InputStream while connected
//			while (true) {
//				try {
//					
//					if(mmInStream==null) return;
//					int tempRevLen = mmInStream.read(tempRevBuffer);
//					if (tempRevLen == -1) {
//						mHandler.obtainMessage(connectNull).sendToTarget();
//						break;
//					}
//					if (tempRevLen == 56) {
//						break;
//					}
//					Log.d("log", "Android 收到消息："+ " , len => " + tempRevLen);
//					System.arraycopy(tempRevBuffer, 0, revBuffer, revLen, tempRevLen);
//					revLen+=tempRevLen;
//					int packetLen=0;
//					if((packetLen=DataCenter.detectAndValidate(revBuffer))!=0)
//					{
//						DataCenter.parsePacket2(revBuffer);//战友协同
//						mHandler.obtainMessage(readType).sendToTarget();
//						revLen-=packetLen;
//						System.arraycopy(revBuffer, packetLen, revBuffer, 0, revLen);
//						//for(int i=0;i<revLen;i++)	revBuffer[i]=revBuffer[i+packetLen];
//					}
//
//				} catch (IOException e) {
//					Log.e(TAG, "disconnected", e);
//					connectionLost();
//					break;
//				}
//			}
		}

		public void cancel() {
		
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
		
		
		
		public String reHexString(String strHex)
		{
			String result="";
			for (int i = 0; i < 4; i++) {  
			     String hex = strHex.substring(6-i*2,8-i*2);  
			     result=result+ hex;
			   }
			return result;
		}
		
		public  boolean isValidate(String data) {
			int sum = 0;
			if (data.toString().length()>=8) {

				String temp = new String(data.toString());

				for (int si = 0; si < temp.length() - 4; si += 2) {
					String itemStr = temp.substring(si, si + 2);
					int item = Integer.parseInt(itemStr, 16);
					sum += item;
				}
				StringBuilder hex = new StringBuilder(Integer
						.toHexString(sum & 0xFF));

				if (hex.length() == 1) {
					hex = new StringBuilder(hex.insert(0, '0').toString()
							.toUpperCase());
				}
				//X1 chengdu String dataSumValidate = data.substring(data.length() -4,data.length() -2);
				String dataSumValidate = data.substring(data.length() -2,data.length());

				return dataSumValidate.equalsIgnoreCase(hex.toString().toUpperCase());

			}
			return false;
		}
		
		
		public  boolean isValidateX1(String data) {
			int sum = 0;
			if (data.toString().length()>=8) {

				String temp = new String(data.toString());

				for (int si = 0; si < temp.length() - 4; si += 2) {
					String itemStr = temp.substring(si, si + 2);
					int item = Integer.parseInt(itemStr, 16);
					sum += item;
				}
				StringBuilder hex = new StringBuilder(Integer
						.toHexString(sum & 0xFF));

				if (hex.length() == 1) {
					hex = new StringBuilder(hex.insert(0, '0').toString()
							.toUpperCase());
				}

				//X1 chengdu 
				String dataSumValidate = data.substring(data.length() -4,data.length() -2);
				//MIMU
				//String dataSumValidate = data.substring(data.length() -2,data.length());

				return dataSumValidate.equalsIgnoreCase(hex.toString().toUpperCase());

			}
			return false;
		}
		
		public  double getDistance(double x1,double y1,double x2,double y2){
			double d = (x2-x1)*(x2-x1) - (y2-y1)*(y2-y1);
			return Math.sqrt(d);
		}
		
	}

	/**
	 * This thread runs during a connection with a remote device. It handles all
	 * incoming and outgoing transmissions.
	 */
	private class ConnectedWriteThread extends Thread {
		private final BluetoothSocket mmSocket;
		

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
