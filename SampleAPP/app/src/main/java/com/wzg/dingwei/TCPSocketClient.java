package com.wzg.dingwei;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class TCPSocketClient {
	// http://blog.csdn.net/qinpeng100423/article/details/8980423
	static public final int sendType = 1000;
	static public final int sendTypeStr = 1001;
	static public final int connectOK = 10002;
	static public final int readType = 10003;
	static public final int disconnType = 10004;
	static public final int errDisconnType = 10005;
	static public final int conningType = 10006;
    public enum SocketState{  
        UNKNOWN,CONNECTED,CONNECTING,DISCONNECTED,DISCONNECTING  
    }  
	
	/*static*/ Socket mSocket = null;
	/*static*/ public SocketState state=SocketState.DISCONNECTED;
	/*static*/ OutputStream mOutputStream = null;
	/*static*/ InputStream mInputStream = null;
	/*static*/ public String server="10.10.100.254";
	/*static*/ public int port=8899;
	/*static*/ int MaxBuffer=1024;
	/*static*/ byte[] sendBuffer=new byte[MaxBuffer];
	/*static*/ byte[] revBuffer=new byte[MaxBuffer];
	/*static*/ int revLen=0;
	/*static*/ byte[] tempRevBuffer=new byte[MaxBuffer];
	/*static*/ public Handler handlerActivity=null;
	ConnectThread connectThread=null;
	ReadThread readThread=null;
	
	
	public TCPSocketClient() 
	{
		LogFile.trace("TCPSocketClient\n");
	}
	
	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int type = msg.what;
			if(queryDisconFlag) 
			{
				disConnect();
				queryDisconFlag=false;
			}
			
			if (readType == type) {
					handlerActivity.obtainMessage(readType).sendToTarget();
			} else if (type == disconnType) {
				handlerActivity.obtainMessage(disconnType).sendToTarget();
			} else if (type == connectOK) {
				handlerActivity.obtainMessage(connectOK).sendToTarget();
			}else if (type == conningType) {
				handlerActivity.obtainMessage(conningType).sendToTarget();
			}else if (type == errDisconnType) {
				handlerActivity.obtainMessage(errDisconnType).sendToTarget();
			}
		};
	};
	
	public boolean	isValidateCfg	=false;
	/*static*/ public void connect() 
	{
		if(DataCenter.netState==State.CONNECTED&&DataCenter.dataStrength>=2&&state==SocketState.DISCONNECTED)
		{
			Log.d("log", "连接");
			LogFile.trace("enter connect\n");
			connectThread=new ConnectThread();
			connectThread.start();
		}
	}
	
	class ConnectThread extends Thread
	{
		
		public void run() {
			state=SocketState.CONNECTING;
			handler.obtainMessage(conningType).sendToTarget();
		try {
			mSocket = new Socket();   
			InetAddress theAddress = InetAddress.getByName(server);   
			InetSocketAddress inetSocketAddress = new InetSocketAddress(theAddress, port);   
	        mSocket.connect(inetSocketAddress, 500);
	        isValidateCfg=true;
			mOutputStream = mSocket.getOutputStream();
			mInputStream = mSocket.getInputStream();
			revLen=0;
			state=SocketState.CONNECTED;
			LogFile.trace("success connect\n");
			readThread=new ReadThread();
			readThread.start();
			handler.obtainMessage(connectOK).sendToTarget();
			LogFile.trace("connectOK\n");
		} catch (Exception e) {
			//queryDisconFlag=true;
			e.printStackTrace();
			disConnect();
			LogFile.trace("error connect\n");
			handler.obtainMessage(errDisconnType).sendToTarget();
			return;
		}
		
		}
		
		public void cancle() {
		}
	}
	
	
	
	class ReadThread extends Thread
	{
		public boolean runFlag=true;
		public void run() {
			while (runFlag) {
				try {
					if(mInputStream==null) return;
					int tempRevLen = mInputStream.read(tempRevBuffer);
					if (tempRevLen == -1) {
						queryDisconFlag=true;
						handler.obtainMessage(disconnType).sendToTarget();
						break;
					}
					Log.d("log", "Android 收到消息："+ " , len => " + tempRevLen);
					System.arraycopy(tempRevBuffer, 0, revBuffer, revLen, tempRevLen);
					revLen+=tempRevLen;
					int packetLen=0;
					if((packetLen=DataCenter.detectAndValidate(revBuffer))!=0)
					{
						DataCenter.parsePacket2(revBuffer);
						handler.obtainMessage(readType).sendToTarget();
						revLen-=packetLen;
						System.arraycopy(revBuffer, packetLen, revBuffer, 0, revLen);
						//for(int i=0;i<revLen;i++)	revBuffer[i]=revBuffer[i+packetLen];
					}
				}catch (Exception e) {
					e.printStackTrace();
					queryDisconFlag=true;
					LogFile.trace("error read\n");
					handler.obtainMessage(disconnType).sendToTarget();
					return;
				}

			}
		}
		
		public void cancle() {
			runFlag = false;
		}
		
	}
	
	
	
	public void disConnect() 
	{
		
		state=SocketState.DISCONNECTING;
		Log.d("log", "断开");
		LogFile.trace("disConnect\n");
		if(this.connectThread!=null ) connectThread.cancle();
		if(this.readThread!=null ) readThread.cancle();
		try {
			if (mSocket != null) {
				mSocket.close();
				mSocket = null;
				//Toast.makeText(MainActivity.this, "断开成功！",Toast.LENGTH_LONG).show();
				Log.d("log", "断开成功！开");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mSocket = null;
			
			if (mOutputStream != null) {
				try {
					mOutputStream.close();
					mOutputStream = null;
				} catch (Exception e) {
				}
			}
			if (mInputStream != null) {
				try {
					mInputStream.close();
					mInputStream = null;
				} catch (Exception e) {
				}
			}
			if (mSocket != null) {
				try {
					mSocket.close();
					mSocket = null;
				} catch (Exception e) {
				}
			}
		}
		
		queryDisconFlag=false;
		state=SocketState.DISCONNECTED;
	}
	
	boolean queryDisconFlag=false;
	public boolean isOpen() 
	{
		if(queryDisconFlag) 
		{
			disConnect();
			queryDisconFlag=false;
		}
		return DataCenter.netState==State.CONNECTED
				&&state==SocketState.CONNECTED;
	}
	
	public boolean isOpen2() 
	{
		return isOpen()&&DataCenter.dataStrength>=1;
	}
	
	public boolean send(byte[] data,int offset,int len) 
	{
			try {
				if(mOutputStream==null) { this.disConnect();return false;}
				mOutputStream.write(data,offset,len);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				LogFile.trace("error send\n");
				this.disConnect();
				return false;
			}
			
	}

	
}
