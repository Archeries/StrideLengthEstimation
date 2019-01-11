package com.wzg.dingwei;

import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.telephony.TelephonyManager;

public class Utility {

	public static void writeFloat(byte[] bytes,int offset,float value,boolean reserve)
    {
		int intValue = Float.floatToIntBits(value);
		 for (int i = 0; i < 4; i++) {  
			 bytes[i+offset] = reserve?((byte) (intValue >> (24 - i * 8))):((byte) (intValue >> (i * 8)));  
		  } 
    }
	
	public static void writeDouble(byte[] bytes,int offset,double value,boolean reserve)
    {
		long intValue = Double.doubleToLongBits(value);
		 for (int i = 0; i < 8; i++) {  
			 bytes[i+offset] = reserve?((byte) (intValue >> (56 - i * 8))):((byte) (intValue >> (i * 8)));  
		  } 
    }
	
	public static void writeByte(byte[] bytes,int offset,byte value)
    {
		bytes[offset]=value;
    }
	
	public static void writeShort(byte[] bytes,int offset,int value,boolean reserve)
    {
		byte a=(byte)(value&0xff);
		byte b=(byte)((value&0xff00)>>8);
		bytes[offset]=reserve?b:a;
		bytes[offset+1]=reserve?a:b;
    }
	
	public static void writeInt(byte[] bytes,int offset,int value,boolean reserve)
    {
	    for (int i = 0; i < 4; i++) {  
	    	bytes[i+offset] = (byte) (value >> (reserve?(24 - i * 8):(i*8)));  
	    } 
    }	
	
	public static void writeCheckSum(byte[] bytes,int offset)
    {
		int sum=0;
		for(int i=0;i<offset;i++)
			sum+=bytes[i];
		int checksum=0xfa-(byte)sum;
		if(checksum<0) checksum+=0x100;
		bytes[offset]=(byte)checksum;
    }
	
	// http://tjmljw.iteye.com/blog/1767716	
	/**
	 * 浮点转换为字节
	 * 
	 * @param f
	 * @return
	 */
	public static byte[] float2byte(float f) {
		
		// 把float转换为byte[]
		int fbit = Float.floatToIntBits(f);
		
		byte[] b = new byte[4];  
	    for (int i = 0; i < 4; i++) {  
	        b[i] = (byte) (fbit >> (24 - i * 8));  
	    } 
	    
	    // 翻转数组
		int len = b.length;
		// 建立一个与源数组元素类型相同的数组
		byte[] dest = new byte[len];
		// 为了防止修改源数组，将源数组拷贝一份副本
		System.arraycopy(b, 0, dest, 0, len);
		byte temp;
		// 将顺位第i个与倒数第i个交换
		for (int i = 0; i < len / 2; ++i) {
			temp = dest[i];
			dest[i] = dest[len - i - 1];
			dest[len - i - 1] = temp;
		}
	    
	    return dest;
	    
	}
	
	/**
	 * 字节转换为浮点
	 * 
	 * @param b 字节（至少4个字节）
	 * @param index 开始位置
	 * @return
	 */
	public static float byte2float(byte[] b, int index) {  
	    int l;                                           
	    l = b[index + 0];                                
	    l &= 0xff;                                       
	    l |= ((long) b[index + 1] << 8);                 
	    l &= 0xffff;                                     
	    l |= ((long) b[index + 2] << 16);                
	    l &= 0xffffff;                                   
	    l |= ((long) b[index + 3] << 24);                
	    return Float.intBitsToFloat(l);                  
	}
	
	public static void  euler2quat(float[] quat, float [] euler){
		
		double[] rotmat={0,0,0,0,0,0,0,0,0};
		euler2rotation(rotmat,euler);
		double[] quatd={0,0,0,0};
		rotation2quat(quatd,rotmat);
		quat[0]=(float) quatd[0];
		quat[1]=(float) quatd[1];
		quat[2]=(float) quatd[2];
		quat[3]=(float) quatd[3];
	}
	
	
	public static void  euler2rotation(double[] rotmat, float [] euler){


		// Trigonometric value variables	
		double sin_phi = Math.sin(euler[0]);
		double cos_phi = Math.cos(euler[0]);
		double sin_theta =Math.sin(euler[1]);
		double cos_theta =Math.cos(euler[1]);
		double sin_psi = Math.sin(euler[2]);
		double cos_psi = Math.cos(euler[2]);

		rotmat[0]=(cos_psi*cos_theta);								//Matrix element [1,1]
		rotmat[3]=(sin_psi*cos_theta);								//Matrix element [1,2]
		rotmat[6]=-sin_theta;										//Matrix element [1,3]
		rotmat[1]=((-sin_psi*cos_phi) + cos_psi*(sin_theta*sin_phi));		//Matrix element [2,1]
		rotmat[4]=((cos_psi*cos_phi) + sin_psi*(sin_theta*sin_phi));		//Matrix element [2,2]
		rotmat[7]=(cos_theta*sin_phi);								//Matrix element [2,3] 
		rotmat[2]=((sin_psi*sin_phi) + cos_psi*(sin_theta*cos_phi));		//Matrix element [3,1]
		rotmat[5]=((-cos_psi*sin_phi) + sin_psi*(sin_theta*cos_phi));		//Matrix element [3,2]		          
		rotmat[8]= (cos_theta*cos_phi);								//Matrix element [3,3]	
	}


	public static void  rotation2quat(double[]  q,double[]  rotmat){
			
			// For checking robustness of DCM, diagonal elements
		double T = 1 + rotmat[0] + rotmat[4]+rotmat[8];  // 1+diag(R)
			
			// Calculate quaternion based on largest diagonal element
			if(T > (0.00000001)){
				double S = 0.5 / Math.sqrt(T);
				q[3] = 0.25f / S;
				q[0] =(rotmat[7]-rotmat[5]) * S;	//(R(3,2) - R(2,3))*S
				q[1] =(rotmat[2]-rotmat[6]) * S;  	//( R(1,3) - R(3,1) ) * S;
				q[2] =(rotmat[3]-rotmat[1]) * S;	// ( R(2,1) - R(1,2) ) * S;
				}
			else if( (rotmat[0] > rotmat[4] ) && (rotmat[0] > rotmat[8]) ) //(R(1,1) > R(2,2)) && (R(1,1) > R(3,3)) 
			{
				double S = Math.sqrt(1 + rotmat[0]-rotmat[4]-rotmat[8]) * 2; //S=sqrt_hf(1 + R(1,1) - R(2,2) - R(3,3)) * 2
				q[3] =(rotmat[6]-rotmat[5])/S;		//(R(3,1) - R(2,3)) / S;
				q[0] = 0.25 * S;
				q[1] = (rotmat[1]+rotmat[3])/S;		//(R(1,2) + R(2,1)) / S;
				q[2] = (rotmat[2]+rotmat[6])/S;		//(R(1,3) + R(3,1)) / S;
				}
			else if (rotmat[4]>rotmat[8])		//(R(2,2) > R(3,3))
			{
				double S = Math.sqrt(1+rotmat[4]-rotmat[0]-rotmat[8])*2;	//sqrt_hf( 1 + R(2,2) - R(1,1) - R(3,3) ) * 2; 
				q[3] = (rotmat[2]-rotmat[6])/S;						//(R(1,3) - R(3,1)) / S;
				q[0] =	(rotmat[1]+rotmat[3])/S;					//(R(1,2) + R(2,1)) / S;
				q[1] = 0.25 * S;
				q[2] = (rotmat[5]+rotmat[7])/S;						//(R(2,3) + R(3,2)) / S;
				}
			else{
				double S=Math.sqrt(1+rotmat[8]-rotmat[0]-rotmat[4])*2;		//S = sqrt_hf( 1 + R(3,3) - R(1,1) - R(2,2) ) * 2; 
				q[3] = (rotmat[3]-rotmat[1])/S;						//(R(2,1) - R(1,2)) / S;
				q[0] = (rotmat[2]+rotmat[6])/S;						//(R(1,3) + R(3,1)) / S;
				q[1] = (rotmat[1]+rotmat[3])/S;						//(R(1,2) + R(2,1)) / S;
				q[2] = 0.25 * S;
				}
			
			
			//Normalize the quaternion
			T=Math.sqrt(q[0]*q[0]+q[1]*q[1]+q[2]*q[2]+q[3]*q[3]);
			q[0]=q[0]/T;
			q[1]=q[1]/T;
			q[2]=q[2]/T;
			q[3]=q[3]/T;
		}
	
	// http://blog.csdn.net/vincent_czz/article/details/7387807	
	public static short  getDeviceID(Activity act)
	{
		TelephonyManager tm = (TelephonyManager)act.getSystemService(Context.TELEPHONY_SERVICE);
		String tmDevice, tmSerial, androidId;
		tmDevice = tm.getDeviceId();
		tmSerial = tm.getSimSerialNumber();
		androidId = android.provider.Settings.Secure.getString(act.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
		short ret=0;
		//int ret=androidId.hashCode()+tmDevice.hashCode()+tmSerial.hashCode();
		//return (short)ret;
		UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String uuidstr=deviceUuid.toString();
		for(int i=0;i<uuidstr.length();i++)
			ret+=uuidstr.charAt(i);
		return ret;
			
	}

	public static float  segmentDistance(PointF p,PointF l0,PointF l1)
	{
		float dx = (l1.x - l0.x);
		float dy = (l1.y - l0.y);
		float px = (p.x - l0.x);
		float py = (p.y - l0.y);
		return (float) ((((dx * py) - (dy * px)) / Math.sqrt(((dx * dx) + (dy * dy)))));// +90
		//return (float) ((((-dx * py) + (dy * px)) / Math.sqrt(((dx * dx) + (dy * dy)))));// -90
	}
	
	public static Adjust.PtoLineObj pToLine(PointF p,PointF p0,PointF p1,float radius)
	{
		Adjust.PtoLineObj ret=new Adjust.PtoLineObj();
		ret.has = true;
		PointF p2d0=new PointF(),p2d1=new PointF();
		boolean  debug = false;
	
		p2d0.x=p1.x-p0.x;p2d0.y=p1.y-p0.y;
		p2d1.x=p.x-p0.x;p2d1.y=p.y-p0.y;
		float len=p2d0.length();
		p2d0.x/=len;p2d0.y/=len;
		float cos=(p2d0.x*p2d1.x+p2d0.y*p2d1.y);
		if(cos<0){
			//"ang >90";
			float deltaX=p2d1.x;
			float deltaY=p2d1.y;
			ret.dist=(float) Math.sqrt(deltaX*deltaX+deltaY*deltaY);
			ret.inf = 0;
			if(ret.dist<=radius){
				p2d0.set(p0);
				ret.point = p2d0;
				return ret;
			}
		}else if(cos>len){
			//"ang <90","> line len";
			float deltaX=p.x-p1.x;
			float deltaY=p.y-p1.y;
			ret.dist=(float) Math.sqrt(deltaX*deltaX+deltaY*deltaY);
			ret.inf = 0;
			if(ret.dist<=radius){
				p2d0.set(p1);
				ret.point = p2d0;
				return ret;
			}
		}else{
			//if(debug) trace("in line");
			ret.dist=Math.abs(segmentDistance(p0,p1,p));
			if(ret.dist>radius){
				ret.has = false;
				return ret;
			}
			ret.inf = 2;
			p2d0.x*=cos;p2d0.y*=cos;
			p2d0.x+=p0.x;p2d0.y+=p0.y;
			ret.point = p2d0;
			return ret;
		}
		ret.has = false;
		return ret;
	}
	
	
	
	public static float getDelta(PointF p0,PointF p1,PointF p2,PointF p3)
	{
		double delta  =  Math.abs(Math.atan2(p1.y - p0.y, p1.x - p0.x) - Math.atan2(p3.y - p2.y, p3.x - p2.x));
		if(delta>Math.PI) delta = (Math.PI*2-delta);
		return (float) delta;
	}
	public static  double getDeltaRad(double raddir1 ,double raddir0)
	{
		double delta = Math.abs(raddir1 - raddir0);
		if(delta>Math.PI) delta = (float) (Math.PI*2-delta); 
		return delta;
	}
	public static  float getDeltaRad(float rad0 ,float rad1)
	{
		float delta = Math.abs(rad0 - rad1);
		if(delta>Math.PI) delta = (float) (Math.PI*2-delta); 
		return delta;
	}

	public static float getDeltaDir(float raddir1,float raddir0)
	{
		float rotRad=getDeltaRad(raddir1,raddir0);
		raddir0-=raddir1;
		if(raddir0>Math.PI||(raddir0<0&&raddir0>-Math.PI))
			rotRad *=-1;
		return rotRad;
	}
	public static double getDeltaDir(double raddir1,double raddir0)
	{
		double rotRad=getDeltaRad(raddir1,raddir0);
		raddir0-=raddir1;
		if(raddir0>Math.PI||(raddir0<0&&raddir0>-Math.PI))
			rotRad *=-1;
		return rotRad;
	}
	public static  float toRad(float ang)
	{
		return (float) (Math.PI*ang/180);
	}
	
	public static String printHexString( byte[] b,int offset,int len) { 
        StringBuffer returnValue = new StringBuffer(); 
        for (int i = 0; i < len; i++) { 
            String hex = Integer.toHexString(b[i+offset] & 0xFF); 
            if (hex.length() == 1) { 
                hex = '0' + hex; 
            } 
            returnValue.append(hex.toUpperCase()); 
        }
        String ret=returnValue.toString() ; 
        return ret;
    }
	
}
