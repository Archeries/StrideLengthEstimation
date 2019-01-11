package com.wzg.dingwei;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class ByteBuffer {
	public byte[] totalBuf=new byte[1024];
	public int totalDataLen=0;
	private File dataFile=null;
	private boolean isText=false;
	private FileOutputStream file=null;
	private OutputStreamWriter osw = null;
	
	public ByteBuffer()
	{
		
	}
	
	public void reset()
    {
		totalDataLen=0;
    }
	
	public void writeDouble(double value)
    {
		byte[] temp={0,0,0,0,0,0,0,0};
		Utility.writeDouble(temp, 0, value, true);
		writeData(temp,0,8);
    }
	
	// long l=Long.parseLong("bf37ceae32cece18",16);??
	public double readDouble(int offset)
    {
		
		return getDouble(totalBuf,offset);
		//return Double.longBitsToDouble(Long.parseLong(DataCenter.printHexString(totalBuf, offset, 8),16));
    }
	
	
	
	public static double getDouble(byte[] b,int offset) { 
	    long l; 
	    l = b[offset+7]; 
	    l &= 0xff; 
	    l |= ((long) b[6+offset] << 8); 
	    l &= 0xffff; 
	    l |= ((long) b[5+offset] << 16); 
	    l &= 0xffffff; 
	    l |= ((long) b[4+offset] << 24); 
	    l &= 0xffffffffl; 
	    l |= ((long) b[3+offset] << 32); 
	    l &= 0xffffffffffl; 
	    l |= ((long) b[2+offset] << 40); 
	    l &= 0xffffffffffffl; 
	    l |= ((long) b[1+offset] << 48); 
	    l &= 0xffffffffffffffl; 
	    l |= ((long) b[offset] << 56); 
	    return Double.longBitsToDouble(l); 
	} 
	
	
	public void writeString(String str)
    {
		writeData(str.getBytes(),-1,-1);
    }
	
	public void writeInt(int value)
    {
		byte[] temp={0,0,0,0};
		Utility.writeInt(temp, 0, value, true);
		writeData(temp,0,4);
    }
	
	public int readInt(int offset)
    {
		return Integer.parseInt(DataCenter.printHexString(totalBuf, offset, 4),16);
    }
	
	public void writeFloat(float value,boolean reserve)
    {
		
		byte[] temp={0,0,0,0};
		Utility.writeFloat(temp, 0, value, reserve);
		writeData(temp,0,4);
		
    }
	
	public void writeData(byte[] buffer,int offset,int length) 
	{
		if(offset==-1) offset=0;
		if(length==-1) length=buffer.length;
		
		if(totalBuf.length<totalDataLen+length)
		{
			int temp=totalBuf.length*2;
			while(temp<totalDataLen+length) temp*=2;
			byte[] newBuf=new byte[temp];
			System.arraycopy(totalBuf, 0, newBuf, 0, totalDataLen);
			totalBuf=newBuf;
		}
		System.arraycopy(buffer,offset,totalBuf,totalDataLen,length);
		totalDataLen+=length;
		
		if(dataFile!=null)
		{
			try{
				if(isText){
					for(int i=0;i<length;i++){
						osw.write((char)buffer[i+offset]);
					}
				}	
				else
				{
					for(int i=0;i<length;i++){
						String hexStr=Integer.toString(buffer[i+offset]&0xff,16);
						if(hexStr.length()==1) osw.write("0");
						osw.write(hexStr);
						osw.write(" ");
					}
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	public void offsetData(int offset)
    {
		if(totalDataLen-offset>0)
		{
			System.arraycopy(totalBuf, offset,totalBuf, 0,totalDataLen-offset);
		}
		if(totalDataLen<offset)
		{
			Log.d("TAG", String.valueOf(totalDataLen));
		}		
		totalDataLen-=offset;
		
    }
	
	public void setFile(File revDataFile,boolean isText)
	{
		try{
			this.dataFile=revDataFile;
			this.isText=isText;
			file=new FileOutputStream(dataFile);
			osw = new OutputStreamWriter(file);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public void saveToFile(File revDataFile,boolean isText) 
	{
	if(totalDataLen>0) 
	{
		try{
		if(revDataFile.exists()==false)
			revDataFile.createNewFile();
		}catch(IOException e)
		{
			Log.d("IOException", "error at create createNewFile");
		}
		try{
			FileOutputStream file=new FileOutputStream(revDataFile);
			//file.write(DataCenter.totalBuf,0,DataCenter.totalBufLen);
			OutputStreamWriter osw = new OutputStreamWriter(file);
			if(isText){
				for(int i=0;i<totalDataLen;i++){
					osw.write((char)totalBuf[i]);
				}
			}	
			else
			{
				for(int i=0;i<totalDataLen;i++){
					String hexStr=Integer.toString(totalBuf[i]&0xff,16);
					if(hexStr.length()==1) osw.write("0");
					osw.write(hexStr);
					osw.write(" ");
				}
			}
			osw.flush();
		}catch(IOException e){
			Log.d("IOException", "error at write createNewFile");
		}
	}
	}
	
	
	public void readFrom(InputStream in) throws IOException 
	{
		 byte[] locaData=new byte[in.available()];
         in.read(locaData);
         int c0,c1,temp,blueDataLen=0;
        
         for(int i=0;i+1<locaData.length;i+=3)
         {
         	c0=locaData[i]&0xff;
         	c0-=(c0>=97?87:48);
         	c1=locaData[i+1]&0xff;
         	c1-=(c1>=97?87:48);
         	temp=(c0&0xff)*16+(c1&0xff);
         	locaData[blueDataLen]=(byte) (temp&0xff);
         	blueDataLen++;
         }
        this.writeData(locaData, 0, blueDataLen);
        
	}
	
	
}
