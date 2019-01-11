package com.wzg.dingwei;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Random;

import android.os.Environment;
import android.os.Handler;

import com.amap.location.demo.FireManActivity;

public class DebugThread extends Thread{ 

	static public final int sendUpdate = 10021;
	//static public MainFrag targetFrag=null;
	static public FireManActivity targetFrag=null;
	static public Handler handler =null;
	static String blueFile;
	static String receiveSocketFile;
	static String sendSocketFile;
	static ByteBuffer blueData=new ByteBuffer();//=new byte[1024*500];
	static ByteBuffer locaData=new ByteBuffer();//=new byte[1024*500];
	static byte[] sendSocketData=new byte[1024*500];
	static byte[] revSocketData=new byte[1024*500];
	static int sendSocketLen=0,revSocketLen=0;
	static int offsetBlue=0,sendOffsetSocket=0,revOffsetSocket=0;
	static int packetLenBlue=64;
	static byte[] packetBlueData=new byte[packetLenBlue];
	static byte[] packetSendData=new byte[DataCenter.packetSendLen];
	static double gpsOffsetLen=0;
	static long tickCount=0;
	static boolean debugFlag=false;
	
	public DebugThread(FireManActivity targetFrag,Handler handler) {
		
		this.targetFrag=targetFrag;
		this.handler=handler;
		if(debugFlag)
		{
			char[] value=new char[2];
			String fileName="rev_data_2015_05_28105457.txt";//"rev_data_2015_06_30123142.txt";//"rev_data_2015_05_28103730.txt";
			try{
					File file =new File(Environment.getExternalStorageDirectory(),fileName);
					InputStream in=new FileInputStream(file);
					blueData.readFrom(in);
		            in.close();
	
			}catch(Exception e) {
	            System.out.println("read error");
	        }
			
			Random rand = new Random();
			int i = rand.nextInt(1000);
			DecimalFormat df = new DecimalFormat("0000"); 
			DataCenter.deviceid=df.format(i);
					
		}
	}
	
	
	public void cancel() {
		
	}
	
	public double lng=0,lat=0;
	private int dataTimes=0;
	private int omittedDataLen=0;

	@Override  
	public void run()
	{ 
		
		while(true)
		{
			++tickCount;
			if(debugFlag)
			{
				if(targetFrag.initFlag&&tickCount%2==0)
				{
					if(offsetBlue<=blueData.totalDataLen-packetLenBlue){
						System.arraycopy(blueData.totalBuf, offsetBlue, packetBlueData, 0, packetLenBlue);
						offsetBlue+=packetLenBlue;
						parsePacketTSENS(packetBlueData);
						DataCenter.blueBuf.writeData(packetBlueData, -1, -1);
						
						DataCenter.setPressure(-(float)Math.sin(tickCount/100.0f*Math.PI));
					}
				}
			}		
			if(DataCenter.adjustFlag==false&&targetFrag.initFlag) 
			{
				try{
				DataCenter.pos[3]=(float) DataCenter.PressureHeight;			
				float[] temppos={-DataCenter.pos[0],DataCenter.pos[1],-DataCenter.pos[2],DataCenter.pos[3]};
				int oldSize=DataCenter.points.size();
				
				//2016年3月16日23:32:00新注释掉
//				// 自动校正
				DataCenter.adjust3.reAdjustAddPos4(DataCenter.adjust.adjustAddPos(temppos));
				
				
				// 手动校正
				//DataCenter.adjust.adjustAddPos(temppos);
				//DataCenter.adjust3.reAdjustAddPos2(DataCenter.manuDeltaRad);
				
				
				//不矫正
			//	DataCenter.adjust.adjustAddPos(temppos);
				
				if(oldSize!=DataCenter.points.size()||DataCenter.manuDeltaRad!=0)
				{
					
					if(oldSize!=DataCenter.points.size()&&DataCenter.manuDeltaRad!=0)
						DataCenter.adjustIndex=-2;
					else if(oldSize==DataCenter.points.size()&&DataCenter.manuDeltaRad!=0)
						DataCenter.adjustIndex=-3;
					
					float oldHeight=DataCenter.pos[2];
					DataCenter.pos[2]=temppos[2];
					byte[] packet=DataCenter.getDataPacket();
					DataCenter.pos[2]=oldHeight;
					
					DataCenter.sendBuf.writeData(packet,-1,-1);
					
					if(handler!=null) handler.obtainMessage(sendUpdate).sendToTarget(); 
				}
				DataCenter.manuDeltaRad=0;
				if(targetFrag.bluetoothService232.isOpen2())
				{
					/*
					DataCenter.sendBufOffset=DataCenter.receiveCount*DataCenter.packetSendLen;
					if(DataCenter.debugFlag==false&&omittedDataLen<DataCenter.sendBufOffset)
					{
						// 删除已发送的字节，节省空间
						DataCenter.sendBuf.offsetData(DataCenter.sendBufOffset-omittedDataLen);
						omittedDataLen+=DataCenter.sendBufOffset-omittedDataLen;
					}
					*/
					for(int i=0;i<2;i++)
					if(DataCenter.sendBufOffset<DataCenter.sendBuf.totalDataLen+omittedDataLen)
					{
						System.arraycopy(DataCenter.sendBuf.totalBuf, 
										DataCenter.sendBufOffset-omittedDataLen,
										packetSendData, 
										0,
										DataCenter.packetSendLen);
						
						
						//long currentTimeMillis = System.currentTimeMillis();
				    	//int deviceIndex=Integer.parseInt(DataCenter.deviceid,16)%4;
				    	//int md=(int)currentTimeMillis%4;
					    //if(deviceIndex==md)
					    if(targetFrag.bluetoothService232.send(packetSendData, 0,DataCenter.packetSendLen)==false) break;
						
						DataCenter.sendCount++;
						DataCenter.sendBufOffset+=DataCenter.packetSendLen;
						//*
						if(DataCenter.debugFlag==false)
						{
							DataCenter.sendBuf.offsetData(DataCenter.sendBufOffset);
							DataCenter.sendBufOffset=0;
						}
						//*/
						DataCenter.lastSendTime=System.currentTimeMillis();
					}
				}
				}catch(Exception e){
					LogFile.trace(e);
				}
			}
			if(handler!=null&&tickCount%2==0) handler.obtainMessage(BluetoothService.connectTimer,tickCount).sendToTarget(); 	

		}
		
		
	}
	
	static public  int packet_number_old=0;
	static public  void parsePacketTSENS(byte[] data) {
		
		double[] delta=new double[3];
    	float[] pos=new float[3];
    	pos[0]=Float.intBitsToFloat(new BigInteger(DataCenter.printHexString(data,4, 4,true), 16).intValue());   
    	pos[1]=Float.intBitsToFloat(new BigInteger(DataCenter.printHexString(data,8, 4,true), 16).intValue());  
    	pos[2]=Float.intBitsToFloat(new BigInteger(DataCenter.printHexString(data,12, 4,true), 16).intValue());
    	float rot=Float.intBitsToFloat(new BigInteger(DataCenter.printHexString(data,16, 4,true), 16).intValue());
    	float[] payload={pos[0],pos[1],pos[2],rot};
    	
    	int packet_number_1=(int)Long.parseLong(DataCenter.printHexString(data,1, 1), 16);//new BigInteger(packetsData.substring(2, 4), 2).intValue();
		int packet_number_2=(int)Long.parseLong(DataCenter.printHexString(data,2, 1), 16);//new BigInteger(packetsData.substring(4, 6), 2).intValue();
		int packet_number = packet_number_1*256 + packet_number_2; //PACKAGE NUMBER ASSIGNED
		if(packet_number_old!=-1 && packet_number_old != packet_number) // Perform Stepwise Dead Reckoning on receiving new data packet
		{
			//stepwise_dr_tu(); // Perform Stepwise Dead Reckoning on the received data
			
			float sin_phi=(float) Math.sin(DataCenter.x_sw[3]); //
			float cos_phi=(float) Math.cos(DataCenter.x_sw[3]); //
			delta[0]=cos_phi*payload[0]-sin_phi*payload[1]; // Transform / rotate two dimensional displacement vector (dx[0],
			delta[1]=sin_phi*payload[0]+cos_phi*payload[1]; // Transform / rotate two dimensional displacement vector (dx[0],
			delta[2]=payload[2]; // Assuming only linear changes along z-axis
			DataCenter.x_sw[0]+=delta[0]; // Final X in the user’s reference frame
			DataCenter.x_sw[1]+=delta[1]; // Final Y in the user’s reference frame
			DataCenter.x_sw[2]+=delta[2]; // Final Z in the user’s reference frame
			DataCenter.x_sw[3]+=payload[3]; // Cumulative change in orientation in the user’s reference frame
			pos[0]=(float) DataCenter.x_sw[0];
			pos[1]=(float) DataCenter.x_sw[1];
			pos[2]=(float) DataCenter.x_sw[2];
			
			//distance+=Math.sqrt((delta[0]*delta[0]+delta[1]*delta[1]+delta[2]*delta[2]));
			packet_number_old=packet_number;
		}	
    	
    	float[] quat=new float[4];
     	quat[0]=0;//Float.intBitsToFloat(new BigInteger(printHexString(data,16, 4), 16).intValue());   
    	quat[1]=0;//Float.intBitsToFloat(new BigInteger(printHexString(data,20, 4), 16).intValue());  
    	quat[2]=0;//Float.intBitsToFloat(new BigInteger(printHexString(data,24, 4), 16).intValue());  
    	quat[3]=0;//Float.intBitsToFloat(new BigInteger(printHexString(data,28, 4), 16).intValue());      	
    	float[] Magnetic=new float[3];
    	Magnetic[0]=0;//Float.intBitsToFloat(new BigInteger(printHexString(data,32, 4), 16).intValue());    
    	Magnetic[1]=0;//Float.intBitsToFloat(new BigInteger(printHexString(data,36, 4), 16).intValue());  
    	Magnetic[2]=0;//Float.intBitsToFloat(new BigInteger(printHexString(data,40, 4), 16).intValue());  
    	
    	float Temprature=0;//Float.intBitsToFloat(new BigInteger(printHexString(data,44, 4), 16).intValue());
    	int Pressure=0;//new BigInteger(printHexString(data,48, 4), 16).intValue();
    	float bat0 =0;//new BigInteger(printHexString(data,52, 1), 16).intValue();
    	float bat1 =0;//new BigInteger(printHexString(data,53, 1), 16).intValue();
    	int deviceid =0;//  new BigInteger(printHexString(data,54, 2), 16).intValue();
    	int checksum=0;//new BigInteger(printHexString(data,56, 1), 16).intValue();
    	
    	DataCenter.pos[0]=(float) DataCenter.x_sw[0];
		DataCenter.pos[1]=(float) DataCenter.x_sw[1];
		DataCenter.pos[2]=(float) DataCenter.x_sw[2];

		DataCenter.adjust3.adjustAddPos(DataCenter.pos);
}
	

	static float lastStepX=0,lastStepY=0;
	
	
	public static void reset()
	{
		tickCount=0;
		locaData.reset();
		sendSocketLen=0;
		revSocketLen=0;
		offsetBlue=sendOffsetSocket=revOffsetSocket=0;
	}

}
