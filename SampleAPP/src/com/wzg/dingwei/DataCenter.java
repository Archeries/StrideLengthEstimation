package com.wzg.dingwei;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Vector;

import com.ict.wq.SensorDataLogFileReview;
import com.wzg.dingwei.Adjust.Vec3;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.net.NetworkInfo.State;
import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DataCenter {
	public static ArrayList<PointF> points = new ArrayList<PointF>();
	public static ArrayList<PointF> parsePoints=points;// = new ArrayList<PointF>();
	public static ArrayList<PointF> rawPoints = new ArrayList<PointF>();
	public static ArrayList<float[]> rawPoints2 = new ArrayList<float[]>();
	public static Vector<Integer> pointsIndex = new Vector<Integer>();
	public static Vector<Float> heightIndex = new Vector<Float>();
	public static ArrayList<ManPos> manPos = new ArrayList<ManPos>();
	public static boolean adjustFlag=false;
	public static float viewWidth;
	public static float viewHeight;
	public static boolean usbOpen=false;
	public static float centerX = 0;
	public static float centerY = 0;
	public static float lastX = 0;
	public static float lastY = 0;

	public static EditText heightText;
	public static TextView edtLen,edtHeight,edtRad,edtStatus;
	
	public static String deviceid="ffff";
	public static String targetid;
	public static int receiveCount=0;
	//public static String addressBT="00:80:E1:B2:EA:67";
	public static String commandToBT="140014";
	public static byte[] ack=null;
		
	public static double[] x_sw={0,0,0,0};
	public static float[] pos ={0,0,0,0};
	public static float[] lastPos ={0,0,0};
	public static float[] quat ={1,0,0,0};
	
	public static boolean setOutFlag=false;
	public static float[] Magnetic ={0,0,0};
	public static float[] Gyroscope ={0,0,0};
	public static float[] Orientation ={0,0,0};
	public static float[] Accelerometer ={0,1,0};
	public static float Angle=0;
	public static float HeightFromOSM=0;
	public static float Temprature=0;
	public static float firstPressure=0,lastPressure=0;	
	public static float[] Pressure = new float[20];
	public static int PressureIndex=0,PressureCount=0;
	public static float PressureHeight=0;	
	
	public static int bat0=0,bat1=0;
	public static float[] campassM=new float[9];
	public static float[] campass={0,0,0};
	public static float gpsRot=0;
	final public static int packetSendLen=56;//73+4;
	private  static byte[] packet=new byte[packetSendLen];
	public static byte[] dataPacket=null;
	public static float initLng=-1,initLat=-1;
	public static boolean sendMagFlag=false;
	// 保存接收,发送的所有数据
	public static ByteBuffer blueBuf=new ByteBuffer();
	public static ByteBuffer sendBuf=new ByteBuffer();
	public static int sendBufOffset=0;
	public static float totalDistance=0;
	public static float radiusDistance=0;
	public static final int segMaxNum=5000;
	public static Vec3 stasticOldPos=new Adjust.Vec3(0,0,0),stasticStartPos=new Adjust.Vec3(0,0,0); 
	public static boolean pathRotChanged=false;
	static public Adjust adjust=new Adjust(true);
	static public Adjust3 adjust3=new Adjust3();
	public static float adjustRad=0;
	public static int adjustIndex=-1;
	static public State netState=State.UNKNOWN;
	static public int dataStrength=2;
	//static public boolean debugFlag=false;
	static public boolean debugFlag=true;
	static public long lastSendTime=0;
	static public float manuDeltaRad=0; 
	
	public static float correctX=0;
	public static float correcty=0;
	public static float correctz=0;
	public static float originX=0;
	public static float originy=0;
	public static float originz=0;
	
	
	public static void clear() 
	{
		points.clear();
		parsePoints.clear();
	}

	public static void initData(int width, int height) {
		viewWidth = width;
		viewHeight = height;
		//centerX = (int) (viewWidth / 2 * scales);
		//centerY = (int) (viewHeight / 2 * scales);
		lastX = centerX;
		lastY = centerY;
	}

    public static void  setPressure(float value)
    {
		Pressure[PressureIndex]=value;
		PressureIndex++;
		PressureIndex%=Pressure.length;
		if(PressureCount<Pressure.length) 
			PressureCount++;
		{
			if(DataCenter.firstPressure==0) DataCenter.firstPressure=value;
			if(DataCenter.points.size()<=1) DataCenter.firstPressure=getPressure();
			PressureHeight=-(DataCenter.getPressure()-DataCenter.firstPressure)*100*(12.0f/133.0f);
		}
		lastPressure=value;
//		LogFile.trace("1:"+PressureHeight+":"+lastPressure+"\n");
    }
    
    public static float  getPressure()
    {
		float sum=0;
		for(int i=0;i<PressureCount;i++)
			sum+=Pressure[i];
		return sum/PressureCount;
    }
    
    
    public static byte[] getDataPacket()
    {
    	quat[0]=Accelerometer[0];
    	quat[1]=Accelerometer[1];
    	quat[2]=Accelerometer[2];
    	
    	float roty=quat[1]+9.8f;
    	float rotz=quat[2]-9.8f;
		double cof=1/Math.sqrt(quat[0]*quat[0]+quat[1]*quat[1]+quat[2]*quat[2]);
		//TRACE("%f,%f,%f,%f,%f\n",pos.rot.x,pos.rot.y,pos.rot.z,Radian(asin(pos.rot.z*cof)).valueDegrees(),Radian(atan2(pos.rot.x,pos.rot.y)).valueDegrees());
		int pitch =(int)(Math.asin(quat[2]*cof)*180/Math.PI);
		int roll=(int)(Math.atan2(quat[0],quat[1]));
		if(roll>90) roll-=90;
		else if(roll<-90) roll+=90;
    	
    	Utility.writeByte(packet,0,(byte)0xfa);
    	Utility.writeByte(packet,1,(byte)0xff);
    	Utility.writeByte(packet,2,(byte)0x32);
    	Utility.writeShort(packet,3,DataCenter.points.size()%0x100,true);						// 包号
    	Utility.writeShort(packet,5,Integer.parseInt(deviceid,16),true);					// 设备编号
    	Utility.writeByte(packet,7,(byte)(packetSendLen-5));									// 数据长度
    	Utility.writeShort(packet,8,Integer.parseInt(deviceid,16),true);
//    	Utility.writeFloat(packet,10,DataCenter.points.get(DataCenter.points.size()-1).x,true); // x
//    	Utility.writeFloat(packet,14,DataCenter.points.get(DataCenter.points.size()-1).y,true);	// y
    	Utility.writeFloat(packet,10,DataCenter.correctX,true); // x
    	System.out.println("DataCenter.correctX"+DataCenter.correctX);
    	Utility.writeFloat(packet,14,DataCenter.correcty,true);	// y
   //	Utility.writeFloat(packet,18,pos[2],true);												// z
    	Utility.writeFloat(packet,18,DataCenter.correctz,true);		
    	Log.d("coordinate", "X"+DataCenter.correctX+" Y "+DataCenter.correcty+" Z "+DataCenter.correctz);// z
  //  	SensorDataLogFileReview.trace("X,"+DataCenter.correctX+",Y, "+DataCenter.correcty+",Z,"+DataCenter.correctz+"\n");
    	Utility.writeShort(packet,22,(short)(campass[0]*180/Math.PI),true);
    	Utility.writeShort(packet,24,(short)(gpsRot*180/Math.PI)/*campass[0]*/,true);
    	Utility.writeShort(packet,26,(short)Temprature,true);								    //x -2
    	Utility.writeByte(packet,28,(byte)bat0);  								
    	//Utility.writeInt(packet,52,(int)(lastPressure*100),true);					  			//x
    	//Utility.writeFloat(packet,40,DataCenter.PressureHeight/*campass[1]*/,true);   		//x
    	Utility.writeByte(packet,29,(byte)pitch);												// pitch
    	Utility.writeByte(packet,30,(byte)roll);												// roll
    	Utility.writeFloat(packet,31,initLng,true);
    	Utility.writeFloat(packet,35,initLat,true);
    	Utility.writeInt(packet,39,DataCenter.pointsIndex.get(DataCenter.pointsIndex.size()-1),true);
    	Utility.writeFloat(packet,43,adjustRad,true);
    	Utility.writeInt(packet,47,adjustIndex,true);
    	Utility.writeFloat(packet,51,DataCenter.lastPressure,true);
    	Utility.writeCheckSum(packet,55);
    	
//    	LogFile.trace("3:"
//    				  +DataCenter.points.get(DataCenter.points.size()-1).x+":"
//    				  +DataCenter.points.get(DataCenter.points.size()-1).y+":"
//    				  +pos[2]+":"
//    				  +PressureHeight+":"
//    				  +lastPressure+"\n");
    	
		return packet;
    }    
    
 /*   
    public static byte[] getDataPacket()
    {
    	quat[0]=Accelerometer[0];
    	quat[1]=Accelerometer[1];
    	quat[2]=Accelerometer[2];
        	
    	Utility.writeByte(packet,0,(byte)0xfa);
    	Utility.writeByte(packet,1,(byte)0xff);
    	Utility.writeByte(packet,2,(byte)0x32);
    	Utility.writeInt(packet,3,DataCenter.points.size()%0x1000000,true);
    	Utility.writeByte(packet,7,(byte)(packetSendLen-5));
    	Utility.writeFloat(packet,8,DataCenter.points.get(DataCenter.points.size()-1).x,true);
    	Utility.writeFloat(packet,12,DataCenter.points.get(DataCenter.points.size()-1).y,true);
    	Utility.writeFloat(packet,16,pos[2],true);
    	Utility.writeFloat(packet,20,quat[0],true);			// quat改成1个字节
    	Utility.writeFloat(packet,24,quat[1],true);
    	Utility.writeFloat(packet,28,quat[2],true);
    	Utility.writeFloat(packet,32,adjustRad,true);
    	Utility.writeFloat(packet,36,gpsRot,true);
    	Utility.writeFloat(packet,40,DataCenter.PressureHeight,true);   //x
    	Utility.writeFloat(packet,44,campass[0],true);
    	Utility.writeFloat(packet,48,Temprature,true);								  //x -2
    	Utility.writeInt(packet,52,(int)(lastPressure*100),true);					  //x
    	Utility.writeByte(packet,53,(byte)bat0);  									  
    	Utility.writeByte(packet,54,(byte)bat1);									  //x
    	Utility.writeShort(packet,56,Integer.parseInt(deviceid,16),true);
    	Utility.writeFloat(packet,60,initLng,true);
    	Utility.writeFloat(packet,64,initLat,true);
    	Utility.writeInt(packet,68,DataCenter.pointsIndex.get(DataCenter.pointsIndex.size()-1),true);
    	Utility.writeInt(packet,72,adjustIndex,true);
    	Utility.writeCheckSum(packet,76);
		return packet;
    }
*/    
 /*   
    public static byte[] getDataPacket()
    {
    	
    	quat[0]=Accelerometer[0];
    	quat[1]=Accelerometer[1];
    	quat[2]=Accelerometer[2];
    	
    	Utility.writeByte(packet,0,(byte)0xfa);
    	Utility.writeByte(packet,1,(byte)0xff);
    	Utility.writeByte(packet,2,(byte)0x32);
    	Utility.writeByte(packet,3,(byte)(packetSendLen-5));
    	Utility.writeFloat(packet,4,DataCenter.points.get(DataCenter.points.size()-1).x,true);
    	Utility.writeFloat(packet,8,DataCenter.points.get(DataCenter.points.size()-1).y,true);
    	Utility.writeFloat(packet,12,pos[2],true);
    	Utility.writeFloat(packet,16,quat[0],true);
    	Utility.writeFloat(packet,20,quat[1],true);
    	Utility.writeFloat(packet,24,quat[2],true);
    	Utility.writeFloat(packet,28,adjustRad,true);
    	Utility.writeFloat(packet,32,gpsRot,true);
    	Utility.writeFloat(packet,36,DataCenter.PressureHeight,true); 
    	Utility.writeFloat(packet,40,campass[0],true);
    	Utility.writeFloat(packet,44,Temprature,true);
    	Utility.writeInt(packet,48,(int)(lastPressure*100),true);
    	Utility.writeByte(packet,52,(byte)bat0);
    	Utility.writeByte(packet,53,(byte)bat1);
    	Utility.writeShort(packet,54,Integer.parseInt(deviceid,16),true);
    	Utility.writeFloat(packet,56,initLng,true);
    	Utility.writeFloat(packet,60,initLat,true);
    	//Utility.writeCheckSum(packet,64);
    	Utility.writeInt(packet,64,DataCenter.pointsIndex.get(DataCenter.pointsIndex.size()-1),true);
    	Utility.writeInt(packet,68,adjustIndex,true);
    	Utility.writeCheckSum(packet,72);
		return packet;
    }
 */   
    
    
	// 解析发送的数据
	public static void parsePacket2(byte[] data) {
		    int extraOffset=4;
			int count=new BigInteger(Utility.printHexString(data,5+extraOffset, 1), 16).intValue();
			int sizeStruc=25;
			for(int i=0;i<count;i++)
			{
				int baseAdd=6+i*sizeStruc+extraOffset;
				String id=BluetoothDataUtil.reHeigHexString(Utility.printHexString(data,baseAdd, 2).toUpperCase());  
				ManPos pos=null;
				for(int j=0;j<manPos.size();j++)
				{
					if(manPos.get(j).id.equals(id))
					{
						pos=manPos.get(j);
						break;
					}
				}
				if(pos==null)
				{
					pos=new ManPos();
					manPos.add(pos);
				}
				pos.id=id;
				pos.status=new BigInteger(Utility.printHexString(data,baseAdd+2, 1), 16).intValue(); 
				pos.rad=Float.intBitsToFloat(new BigInteger(Utility.printHexString(data,baseAdd+3, 4), 16).intValue()); 
				pos.x=Float.intBitsToFloat(new BigInteger(Utility.printHexString(data,baseAdd+7, 4), 16).intValue()); 
				pos.y=Float.intBitsToFloat(new BigInteger(Utility.printHexString(data,baseAdd+11, 4), 16).intValue()); 
				pos.z=Float.intBitsToFloat(new BigInteger(Utility.printHexString(data,baseAdd+15, 4), 16).intValue()); 
				pos.targetid=BluetoothDataUtil.reHeigHexString(Utility.printHexString(data,baseAdd+19, 2).toUpperCase()); 
				pos.receiveCount=new BigInteger(Utility.printHexString(data,baseAdd+21, 4),16).intValue();
				if(DataCenter.deviceid.equals(id)){
					if(pos.rad!=DataCenter.gpsRot)
					{
						DataCenter.gpsRot=pos.rad;
						pathRotChanged=true;
					}
					DataCenter.targetid=pos.targetid;
					DataCenter.receiveCount=pos.receiveCount;
				}
			}
	}
	
	
    public static int sendCount=0;
	
	
    public static void reset(boolean allFlag)
	{
    	rawPoints.clear();
    	if(allFlag) rawPoints2.clear();
    	DataCenter.blueBuf.reset();
    	adjust.reset();
    	adjust3.reset();
    	DataCenter.adjustFlag=false;    	
    	if(allFlag)
    	{
	    	x_sw[0]=x_sw[1]=x_sw[2]=x_sw[3]=0;
	    	pos[0]=pos[1]=pos[2]=0;
    	}
    	parsePoints.clear();
    	points.clear();
	}
 
	
	
    
    public static byte[] hex2byte(String hexString) {
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
    
    private static byte charToByte(char c) {  
	    return (byte) "0123456789ABCDEF".indexOf(c);  
	}
    
    
    
    public static String printHexString( byte[] b,int offset,int len) { 
		return printHexString(b,offset,len,true);
    }
	
	
	public static String printHexString( byte[] b,int offset,int len,boolean reverse) { 
        StringBuffer returnValue = new StringBuffer(); 
        for (int i = 0; i < len; i++) { 
            String hex = Integer.toHexString(b[(reverse?i:(len-1-i))+offset] & 0xFF); 
            if (hex.length() == 1) { 
                hex = '0' + hex;
            } 
            returnValue.append(hex.toUpperCase()); 
        }
        String ret=returnValue.toString();
        return ret;
    }
    
    public static String hexToAscii(String s) throws IllegalArgumentException
	{
		  int n = s.length();
		  StringBuilder sb = new StringBuilder(n / 2);
		  for (int i = 0; i < n; i += 2)
		  {
		    char a = s.charAt(i);
		    char b = s.charAt(i + 1);
		    sb.append((char) ((hexToInt(a) << 4) | hexToInt(b)));
		  }
		  return sb.toString();
	}
    
    public static int hexToInt(char ch)
	{
		  if ('a' <= ch && ch <= 'f') { return ch - 'a' + 10; }
		  if ('A' <= ch && ch <= 'F') { return ch - 'A' + 10; }
		  if ('0' <= ch && ch <= '9') { return ch - '0'; }
		  throw new IllegalArgumentException(String.valueOf(ch));
	}
    
    public static String decToAscii(String s) throws IllegalArgumentException
	{	
		int n = s.length();
		boolean pause = false;
		StringBuilder sb = new StringBuilder(n / 2);
		for (int i = 0; i < n; i += 3)
		{
			char a = s.charAt(i);
			char b = s.charAt(i + 1);
			char c = s.charAt(i + 2);
			int val = decToInt(a)*100 + decToInt(b)*10 + decToInt(c);
			if(0 <= val && val <= 255)
			{
				sb.append((char) val);
			}
			else
			{
				pause = true;
				break;
			}
		}
		
		if(false == pause)
			return sb.toString();
		throw new IllegalArgumentException("ex_b");
	}
    
    public static int decToInt(char ch)
	{
		  if ('0' <= ch && ch <= '9') { return ch - '0'; }
		  throw new IllegalArgumentException("ex_a");
	}
    
	
    public static float[] getQuat(){
     	quat[0]=Accelerometer[0];
    	quat[1]=Accelerometer[1];
    	quat[2]=Accelerometer[2];
    	
    	return quat;
    }
    
    public static String getCheckSum(String data) {
		int sum = 0;
		if (data.toString().length()>=8) {

			String temp = new String(data.toString());

			for (int si = 0; si < temp.length(); si += 2) {
				String itemStr = temp.substring(si, si + 2);
				int item = Integer.parseInt(itemStr, 16);
				sum += item;
			}
			
			sum=Integer.parseInt("FA", 16)-sum;
			
			StringBuilder hex = new StringBuilder(Integer
					.toHexString(sum & 0xFF));

			if (hex.length() == 1) {
				hex = new StringBuilder(hex.insert(0, '0').toString()
						.toUpperCase());
			}

			return hex.toString().toUpperCase();

		}
		return "";
	}
    
    public static boolean isValidate(String data) {
		int sum = 0;
		if (data.toString().length()>=8) {

			String temp = new String(data.toString());

			for (int si = 0; si < temp.length(); si += 2) {
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

			String dataSumValidate = "FA";

			return dataSumValidate.equalsIgnoreCase(hex.toString().toUpperCase());

		}
		return false;
	}
    
    
    // 判断是否是发往PC端的定位数据
    public static int isData2PC(byte[] data)
	{
    		int extraOffset=4;
    		int len=0;
			if(data.length>=6)
			{
				if((data[0]&0xff)==0xfa&&(data[1]&0xff)==0xff&&(data[2]&0xff)==0x32)
				{
					len=(data[3+extraOffset]&0xff)+6+extraOffset;
					return (data.length==len&&validate(data,len))?len:0;
				}
			}
			return 0;
	}
    
    // 返回包的字节数
    public static int detectAndValidate(byte[] data)
	{
    		int extraOffset=4;
    		int len=0;
			if(data.length>=6)
			{
				if((data[0]&0xff)==0xfa&&(data[1]&0xff)==0xff&&(data[2]&0xff)==0x32)
				{
					len=((data[3+extraOffset]&0xff)+(data[4+extraOffset]&0xff)*0x100)+6+extraOffset;
					return (data.length>=len&&validate(data,len))?len:0;
				}
			}
			return 0;
	}
    
    public static boolean validate(byte[] data,int len)
   	{
   			int sum = 0;
   			for(int i=0;i<len;i++)
   			{
   				sum+=data[i];
   			}
   			return (sum&0xff)==0xfa;
   	}
    
	
	
    //电量
    public static int batteryPercent=100;
    
    //语音播报
    public static Speek mSpeek=new Speek();
    public static boolean  goOutClick=false;
    public static boolean  findClick=false;
    public static boolean  goBackClick=false;
    public static float lastTotalDistance=0;
    
    
    
}
