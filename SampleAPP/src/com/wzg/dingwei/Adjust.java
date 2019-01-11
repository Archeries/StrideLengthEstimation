package com.wzg.dingwei;
import java.util.ArrayList;

import android.graphics.PointF;
import android.util.Log;

public class Adjust {


	public static class  Matrix4
	{
		public float m00,m01,m02,m03,m10,m11,m12,m13,m20,m21,m22,m23,m30,m31,m32,m33;
		
		public void copy(Matrix4 src)
	    {
			Matrix4 ret=this;
			ret.m00 = src.m00 ;
			ret.m01 = src.m01 ;
	        ret.m02 = src.m02 ;
	        ret.m03 = src.m03 ;

	        ret.m10 = src.m10 ;
	        ret.m11 = src.m11 ;
	        ret.m12 = src.m12 ;
	        ret.m13 = src.m13 ;

	        ret.m20 = src.m20 ;
	        ret.m21 = src.m21 ;
	        ret.m22 = src.m22 ;
	        ret.m23 = src.m23 ;

	        ret.m30 = src.m30 ;
	        ret.m31 = src.m31 ;
	        ret.m32 = src.m32 ;
	        ret.m33 = src.m33 ;
	    }
	
		public void make_trans(float tx, float ty, float tz )
	    {
			Matrix4 m=this;
	        m.m00 = 1.0f; m.m01 = 0.0f; m.m02 = 0.0f; m.m03 = tx;
	        m.m10 = 0.0f; m.m11 = 1.0f; m.m12 = 0.0f; m.m13 = ty;
	        m.m20 = 0.0f; m.m21 = 0.0f; m.m22 = 1.0f; m.m23 = tz;
	        m.m30 = 0.0f; m.m31 = 0.0f; m.m32 = 0.0f; m.m33 = 1.0f;
	    }
	
		public Matrix4 inverse() 
		 {
			Matrix4 m=this;
		        float m00 = m.m00; m01 = m.m01; m02 = m.m02; m03 = m.m03;
		        float m10 = m.m10; m11 = m.m11; m12 = m.m12; m13 = m.m13;
		        float m20 = m.m20; m21 = m.m21; m22 = m.m22; m23 = m.m23;
		        float m30 = m.m30; m31 = m.m31; m32 = m.m32; m33 = m.m33;

		        float v0 = m20 * m31 - m21 * m30;
		        float v1 = m20 * m32 - m22 * m30;
		        float v2 = m20 * m33 - m23 * m30;
		        float v3 = m21 * m32 - m22 * m31;
		        float v4 = m21 * m33 - m23 * m31;
		        float v5 = m22 * m33 - m23 * m32;

		        float t00 = + (v5 * m11 - v4 * m12 + v3 * m13);
		        float t10 = - (v5 * m10 - v2 * m12 + v1 * m13);
		        float t20 = + (v4 * m10 - v2 * m11 + v0 * m13);
		        float t30 = - (v3 * m10 - v1 * m11 + v0 * m12);

		        float invDet = 1 / (t00 * m00 + t10 * m01 + t20 * m02 + t30 * m03);

		        float d00 = t00 * invDet;
		        float d10 = t10 * invDet;
		        float d20 = t20 * invDet;
		        float d30 = t30 * invDet;

		        float d01 = - (v5 * m01 - v4 * m02 + v3 * m03) * invDet;
		        float d11 = + (v5 * m00 - v2 * m02 + v1 * m03) * invDet;
		        float d21 = - (v4 * m00 - v2 * m01 + v0 * m03) * invDet;
		        float d31 = + (v3 * m00 - v1 * m01 + v0 * m02) * invDet;

		        v0 = m10 * m31 - m11 * m30;
		        v1 = m10 * m32 - m12 * m30;
		        v2 = m10 * m33 - m13 * m30;
		        v3 = m11 * m32 - m12 * m31;
		        v4 = m11 * m33 - m13 * m31;
		        v5 = m12 * m33 - m13 * m32;

		        float d02 = + (v5 * m01 - v4 * m02 + v3 * m03) * invDet;
		        float d12 = - (v5 * m00 - v2 * m02 + v1 * m03) * invDet;
		        float d22 = + (v4 * m00 - v2 * m01 + v0 * m03) * invDet;
		        float d32 = - (v3 * m00 - v1 * m01 + v0 * m02) * invDet;

		        v0 = m21 * m10 - m20 * m11;
		        v1 = m22 * m10 - m20 * m12;
		        v2 = m23 * m10 - m20 * m13;
		        v3 = m22 * m11 - m21 * m12;
		        v4 = m23 * m11 - m21 * m13;
		        v5 = m23 * m12 - m22 * m13;

		        float d03 = - (v5 * m01 - v4 * m02 + v3 * m03) * invDet;
		        float d13 = + (v5 * m00 - v2 * m02 + v1 * m03) * invDet;
		        float d23 = - (v4 * m00 - v2 * m01 + v0 * m03) * invDet;
		        float d33 = + (v3 * m00 - v1 * m01 + v0 * m02) * invDet;
		        Matrix4 ret=new Matrix4();	
		        ret.m00=d00;
		        ret.m01=d01;
		        ret.m02=d02;
		        ret.m03=d03;
		        
		        ret.m10=d10;
		        ret.m11=d11;
		        ret.m12=d12;
		        ret.m13=d13;
		        
		        
		        ret.m20=d20;
		        ret.m21=d21;
		        ret.m22=d22;
		        ret.m23=d23;		        
		        
		        ret.m30=d30;
		        ret.m31=d31;
		        ret.m32=d32;
		        ret.m33=d33;
		        
		        return ret;
		}
		
		
		public void mat4_concatenate_vec(Vec3 ret,Vec3 pos )
		{
			   Matrix4 m=this;
		       float fInvW = 1.0f / ( m.m30 * pos.x + m.m31 * pos.y + m.m32 * pos.z+m.m33 );
		       ret.x= ( m.m00 * pos.x + m.m01 * pos.y + m.m02 * pos.z + m.m03 ) * fInvW;
		       ret.y= ( m.m10 * pos.x + m.m11 * pos.y + m.m12 * pos.z + m.m13 ) * fInvW;
		       ret.z= ( m.m20 * pos.x + m.m21 * pos.y + m.m22 * pos.z + m.m23 ) * fInvW;
		}
	
		public static Vec3 vec3=new Vec3(0,0,0);
		public void mat4_concatenate_vec_copy(Vec3 pos )
		{
			mat4_concatenate_vec(vec3,pos);
			pos.copy(vec3);
		}
		
		private static Vec3 rkAxis=new Vec3(0,0,1);
		public void mat3_from_axis_angle (float rad)
	    {
			Matrix4 m=this;
	        float fCos = (float) Math.cos(rad);
	        float fSin = (float) Math.sin(rad);
	        float fOneMinusCos = 1.0f-fCos;
	        float fX2 = rkAxis.x*rkAxis.x;
	        float fY2 = rkAxis.y*rkAxis.y;
	        float fZ2 = rkAxis.z*rkAxis.z;
	        float fXYM = rkAxis.x*rkAxis.y*fOneMinusCos;
	        float fXZM =rkAxis.x*rkAxis.z*fOneMinusCos;
	        float fYZM = rkAxis.y*rkAxis.z*fOneMinusCos;
	        float fXSin = rkAxis.x*fSin;
	        float fYSin = rkAxis.y*fSin;
	        float fZSin = rkAxis.z*fSin;

	        m.m00 = fX2*fOneMinusCos+fCos;
	        m.m01= fXYM-fZSin;
	        m.m02 = fXZM+fYSin;
	        m.m10 = fXYM+fZSin;
	        m.m11 = fY2*fOneMinusCos+fCos;
	        m.m12 = fYZM-fXSin;
	        m.m20 = fXZM-fYSin;
	        m.m21 = fYZM+fXSin;
	        m.m22 = fZ2*fOneMinusCos+fCos;
	    }
		
	

		public void mat4_concatenate(Matrix4 ret,Matrix4 m2)
	    {
			Matrix4 m1=this;
	        ret.m00 = m1.m00 * m2.m00 + m1.m01 * m2.m10 + m1.m02 * m2.m20 + m1.m03 * m2.m30;
	        ret.m01 = m1.m00 * m2.m01 + m1.m01 * m2.m11 + m1.m02 * m2.m21 + m1.m03 * m2.m31;
	        ret.m02 = m1.m00 * m2.m02 + m1.m01 * m2.m12 + m1.m02 * m2.m22 + m1.m03 * m2.m32;
	        ret.m03 = m1.m00 * m2.m03 + m1.m01 * m2.m13 + m1.m02 * m2.m23 + m1.m03 * m2.m33;

	        ret.m10 = m1.m10 * m2.m00 + m1.m11 * m2.m10 + m1.m12 * m2.m20 + m1.m13 * m2.m30;
	        ret.m11 = m1.m10 * m2.m01 + m1.m11 * m2.m11 + m1.m12 * m2.m21 + m1.m13 * m2.m31;
	        ret.m12 = m1.m10 * m2.m02 + m1.m11 * m2.m12 + m1.m12 * m2.m22 + m1.m13 * m2.m32;
	        ret.m13 = m1.m10 * m2.m03 + m1.m11 * m2.m13 + m1.m12 * m2.m23 + m1.m13 * m2.m33;

	        ret.m20 = m1.m20 * m2.m00 + m1.m21 * m2.m10 + m1.m22 * m2.m20 + m1.m23 * m2.m30;
	        ret.m21 = m1.m20 * m2.m01 + m1.m21 * m2.m11 + m1.m22 * m2.m21 + m1.m23 * m2.m31;
	        ret.m22 = m1.m20 * m2.m02 + m1.m21 * m2.m12 + m1.m22 * m2.m22 + m1.m23 * m2.m32;
	        ret.m23 = m1.m20 * m2.m03 + m1.m21 * m2.m13 + m1.m22 * m2.m23 + m1.m23 * m2.m33;

	        ret.m30 = m1.m30 * m2.m00 + m1.m31 * m2.m10 + m1.m32 * m2.m20 + m1.m33 * m2.m30;
	        ret.m31 = m1.m30 * m2.m01 + m1.m31 * m2.m11 + m1.m32 * m2.m21 + m1.m33 * m2.m31;
	        ret.m32 = m1.m30 * m2.m02 + m1.m31 * m2.m12 + m1.m32 * m2.m22 + m1.m33 * m2.m32;
	        ret.m33 = m1.m30 * m2.m03 + m1.m31 * m2.m13 + m1.m32 * m2.m23 + m1.m33 * m2.m33;
		
				
		}
		private static Matrix4 tempM=new Matrix4();
		public void mat4_concatenate_copy(Matrix4 ret,Matrix4 m2)
	    {
			Matrix4 m1=this;
			m1.mat4_concatenate(tempM,m2);
			ret.copy(tempM);
	    }
		
	}
	
	public static class Vec3
	{
		
		public float x,y,z;		
		
		Vec3(float x,float y,float z)
		{
			this.x = x;
			this.y = y;
			this.z = z;			
		}
		
		public void copy(Vec3 p)
		{
			x=p.x;
			y=p.y;
			z=p.z;
		}
		
		public void copyZ(Vec4 p)
		{
			x=p.x;
			y=p.y;
			z=p.z;
		}
		
		public void copy(Vec4 p)
		{
			x=p.x;
			y=p.y;
			z=p.w;
		}
		
		public float getLength(Vec3 p)
		{
			return (float) Math.sqrt((x-p.x)*(x-p.x)+(y-p.y)*(y-p.y)+(z-p.z)*(z-p.z));
		}
		

	}
	
	public static class Vec4
	{
		
		public float w,x,y,z;
		
		Vec4(float w,float x,float y,float z)
		{
			this.w = w;
			this.x = x;
			this.y = y;
			this.z = z;			
		}	
		
		public void copy(Vec4 p)
		{
			x=p.x;
			y=p.y;
			z=p.z;
			w=p.w;
		}
		
				
		public float getLengthZ(Vec4 p)
		{
			return (float) Math.sqrt((x-p.x)*(x-p.x)+(y-p.y)*(y-p.y)+(z-p.z)*(z-p.z));
		}
		
	}	
				
	
	
	
	public static class LineLineObj 
	{
		boolean has;
		PointF point=null;
	} 
	
	public static class PtoLineObj
	{
		boolean has=false;
		int inf;
		float dist;
		int index;
		PointF point;
	}

	public static class LineInf
	{

		int from,to;
		float a,b,rad;

		public void copy(LineInf rref)
		{
			from = rref.from;
			to = rref.to;
			a = rref.a;
			b = rref.b;
			rad=rref.rad;
		}
	}

	/*
	 * 校正记录，用于撤销错误的校正
	 */
	public static class AdjustInf
	{
		int from,to;
		Matrix4 invM4=null; 
	}
	
	public static class AdjustInfClient
	{
		
		public char type;
		public int from,to;
		public float rot;
		public int remFrom;
		
		public int writeData(byte[] bytes,int offset)
		{
			Utility.writeByte(bytes,offset,(byte)type);
	    	Utility.writeInt(bytes,offset+1,from,true);
	    	Utility.writeInt(bytes,offset+5,from,true);
	    	Utility.writeFloat(bytes,9,rot,true);
	    	Utility.writeInt(bytes,offset+13,remFrom,true);
	    	return offset+17;
		}
	}
	
	
	public float PointFtoLength(PointF a,PointF b)
	{
		return (float) Math.sqrt((a.x-b.x)*(a.x-b.x)+(a.y-b.y)*(a.y-b.y));
	}

	float adjustThresh ;
	float lineThresh ;
	float lineThresh2;
	float pToLineThresh ;
	float lineCountThresh ;
	float adjustLen ;
	float adjustedLen ;
	float adjustIntervalRad;
	float lastAdjustRad=100;
	//float adjustHVRad ;
	//float adjustHVLineThresh ;
	float stepThresh;
	private Vec4 lastRealPos=new Vec4(0,0,0,0);
	private FindLine findLine=new FindLine();
	private FindRot findRot=new FindRot();
	private int lineRangeFrom=-1,lineRangeTo=-1;
	public ArrayList<LineInf> lineInfs = new ArrayList<LineInf>();
	public ArrayList<AdjustInf> adjustInfs = new ArrayList<AdjustInf>();
	public static final double M_PI=3.141593;
	Matrix4 adjustMat=new Matrix4();
	AdjustInfClient adjustInfClient=new AdjustInfClient();
	boolean enabled=true;
	//public String debugStr="";
	Adjust(boolean enabled)
	{
		adjustMat.make_trans(0, 0, 0);
		adjustThresh = Utility.toRad(20.0f);
		lineThresh = Utility.toRad(40.0f);
		lineThresh2 = 0.2f;//0.2f;
		pToLineThresh = 3.5f;
		lineCountThresh = 5;
		adjustLen =  15;//5;//10;//5;//5;
		adjustedLen = 15;
		stepThresh=0.3f;
		//adjustHVRad = Utility.toRad(40);
		//adjustHVLineThresh = adjustedLen;//5;//4;
		adjustIntervalRad = Utility.toRad(60);
		findLine.maxDist=lineThresh2;
		this.enabled=enabled;
	}
	
	float preHeight=-100;
	float curRawHeight;
	public void adjustHeight(float[] pos)
	{
		// log x y z DataCenter.PressureHeight DataCenter.lastPressure 
		LogFile.trace("2:"
					  +pos[0]+":"
					  +pos[1]+":"
					  +pos[2]+":"
					  +DataCenter.PressureHeight+":"
					  +DataCenter.lastPressure);
			float stepHeight=0.1f;
			if(preHeight==-100)
			{	curRawHeight=preHeight=pos[2];
				LogFile.trace(preHeight+"\n");
			}else
			{
				if(Math.abs(curRawHeight-pos[2])<stepHeight)
				{
					curRawHeight=pos[2];
					pos[2]=preHeight;
					LogFile.trace(preHeight+"\n");
				}else
				{
					float temp=pos[2];
					//preHeight+=(pos[2]-curRawHeight);// 添加模块高度差，可能不准
					preHeight= DataCenter.PressureHeight; // 使用气压高度
					
					pos[2]=preHeight;
					curRawHeight=temp;
					LogFile.trace(preHeight+"\n");
				}
			}
	}
	
	Vec4 curP=new Vec4(0,0,0,0);
	public float adjustAddPos(float[] pos)
	{
		adjustHeight(pos);
		curP.x=pos[0];curP.y=pos[1];curP.z=pos[2];curP.w=pos[3];
		if(DataCenter.parsePoints.size()==0)
			lastRealPos.copy(curP);
		
		float deltaLen =lastRealPos.getLengthZ(curP);
		float deltaLenHeight=Math.abs(curP.w-lastRealPos.w);
		if(DataCenter.parsePoints.size()==0)
		{
			 DataCenter.parsePoints.add(new PointF(curP.x,curP.y));
			 DataCenter.pointsIndex.add(DataCenter.parsePoints.size()-1);
			 DataCenter.heightIndex.add(pos[2]);
			 lastRealPos.copy(curP);
		}
		
	 	 if(DataCenter.parsePoints.size()<=1) 
		 {
		    Vec3 curPos=new Vec3(curP.x,curP.y,(float) DataCenter.PressureHeight);
		    DataCenter.stasticOldPos.copy(curPos);
		    DataCenter.stasticStartPos.copy(curPos);
		  }
		
		if(deltaLen<stepThresh&&deltaLenHeight<2) 
			return 0;
		
		lastRealPos.copy(curP);
		
		findLine.addPoint(curP.x,curP.y);
		
		Vec3 vec=new Vec3(curP.x,curP.y,0);
		adjustMat.mat4_concatenate_vec_copy(vec);
		DataCenter.parsePoints.add(new PointF(vec.x,vec.y));
		if(vec.x==0&&vec.y==0)
		{
			DataCenter.sendMagFlag=true;
		}
		DataCenter.pointsIndex.add(DataCenter.parsePoints.size()-1);
		DataCenter.heightIndex.add(pos[2]);
		if(enabled)
		{
			return AdjustDriftByLine(curP);
		}
		return 0;
	}
	
	// 检验是否把曲线当作了直线
	// 还原最近20m路径,如果前后两段的角度差过大，认为是曲线
	// 撤销这段路径上的所有校正
	void checkAdjust()
	{
		float curveRadThresh=(float) (Math.PI/180*5);
		int mutiply=10;
		float curveEndRadThresh=(float) (Math.PI/180*10);
		ArrayList<PointF> rawPoints=DataCenter.rawPoints;
		int rawSize=rawPoints.size();
		int size=DataCenter.parsePoints.size();
		
		if(rawSize>1)
		{
			LineInf lineInf=this.lineInfs.get(this.lineInfs.size()-1);
			PointF pfrom=DataCenter.rawPoints.get(rawSize-(size-lineInf.from));
			PointF pto=DataCenter.rawPoints.get(rawSize-(size-lineInf.to));
			float lineRad=(float) Math.atan2(pto.y-pfrom.y, pto.x-pfrom.x);
			////debugStr+="lineRad:"+lineRad+","+curveRadThresh+","+curveEndRadThresh+"\n";
			for(int k=2;k<mutiply;k++)
			{
				float curveLenThresh=adjustedLen*k;
				float total=PointFtoLength(pfrom,pto);
				PointF preP=rawPoints.get(rawSize-(size-lineInf.from));
				int i=lineInf.from-1;
				////debugStr+=pfrom.x+","+pfrom.y+","+pto.x+","+pto.y+"\n";
				for(;i>=0;i--)
				{
					PointF nxtP=rawPoints.get(rawSize-(size-i));
					float rad=(float) Math.atan2(preP.y-nxtP.y, preP.x-nxtP.x);
					////debugStr+="checkAdjust:"+rad+","+total+"\n";
					if(Utility.getDeltaRad(rad, lineRad)>curveEndRadThresh) 
						break;
					total+=PointFtoLength(nxtP,preP);
					preP=nxtP;
					if(total>curveLenThresh){
						break;
					}
				}
				PointF midP=findPointByLength(rawSize-1,total*.5f);
				PointF lastP=rawPoints.get(rawPoints.size()-1);
				float rad0=(float) Math.atan2(lastP.y-midP.y, lastP.x-midP.x);
				float rad1=(float) Math.atan2(preP.y-midP.y, preP.x-midP.x);
				float deltaRad=Utility.getDeltaRad(rad0, rad1);
				System.out.println("deltaRad"+deltaRad/Math.PI*180);
				////debugStr+="try:"+k+","+deltaRad+","+rad0+","+rad1+","+i+"\n";
				// i==lineInf.from-1是假设最后的直线直接是一个小拐角
				if(i!=lineInf.from-1&&(deltaRad>curveRadThresh&&deltaRad<Math.PI-curveRadThresh)){
					backByIndex(i);
					////debugStr+="backByIndex:"+k+","+deltaRad+","+rad0+","+rad1+","+i+"\n";
					break;
				}
			
			}
			
		}
	}
	
	PointF findPointByLength(int start,float length)
	{
		PointF ret=new PointF();
		ArrayList<PointF> points=DataCenter.rawPoints;
		if(start>0)
		{
			float total=0;
			PointF preP=points.get(start);
			int i=start-1;
			for(;i>=0;i--)
			{
				PointF nxtP=points.get(i);
				float lastLen=PointFtoLength(nxtP,preP);
				total+=lastLen;
				if(total>length){
					float temp=(lastLen-(total-length))/lastLen;
					ret.x=(nxtP.x-preP.x)*temp+preP.x;
					ret.y=(nxtP.y-preP.y)*temp+preP.y;
					break;
				}
				preP=nxtP;
			}
		}	
		return ret;
	}
	
	private  float AdjustDriftByLine(Vec4 p)
	{
		
		 // 除非上下楼，角度变化太大不校正
		 // 距离太短不校正
		 float heightDelta=Math.abs(p.z-DataCenter.heightIndex.get(DataCenter.adjust3.lastAdjustIndex));
		 if(heightDelta>1)
		 {
			 adjustedLen =8;
			 adjustThresh=Utility.toRad(40.0f);
		 }else
		 {
			 if(DataCenter.pointsIndex.get(DataCenter.pointsIndex.size()-1)-DataCenter.adjust3.lastAdjustIndex<20)
			 return 0;
			 adjustedLen =12;
			 adjustThresh=Utility.toRad(35.0f);
		 }
		/**/
		
		int lineInfSize=lineInfs.size();
		PointF pfrom,pto;
		if(findLine.changed)	
		{
				if(lineInfSize>0)//&&lineRangeFrom==lineInfs.get(lineInfSize-1).from)
				{
					PointF p0=DataCenter.parsePoints.get(lineInfs.get(lineInfSize-1).from);
					PointF p1=DataCenter.parsePoints.get(lineInfs.get(lineInfSize-1).to);
					if((p1.x-p0.x)*(p1.x-p0.x)+(p1.y-p0.y)*(p1.y-p0.y)<adjustLen*adjustLen)
					{
						lineInfs.remove(lineInfSize-1);
					}
				}
				lineRangeFrom=lineRangeTo=-1;
				findLine.changed= false;
		}
		lineInfSize=lineInfs.size();
		boolean addLineFlag=false;
		if(findLine.isLine())
		{
			lineRangeFrom=DataCenter.parsePoints.size()-findLine.points.size();//+1;
			lineRangeTo=DataCenter.parsePoints.size()-1;
			pfrom = DataCenter.parsePoints.get(lineRangeFrom);
			pto = DataCenter.parsePoints.get(lineRangeTo);
			float deltax=pto.x-pfrom.x;
			float deltay=pto.y-pfrom.y;
			float len2=deltax*deltax+deltay*deltay;
			if(len2>adjustedLen*adjustedLen&&lineRangeFrom!=lineRangeTo-1)//防止跳跃lineRangeFrom!=lineRangeTo-1
			{
				if(lineInfSize>0&&lineInfs.get(lineInfSize-1).from==lineRangeFrom)
					lineInfs.get(lineInfSize-1).to=lineRangeTo;
				else if(lineInfs.size()==0||lineRangeFrom>=lineInfs.get(lineInfSize-1).to)
				{
					LineInf posInf=new LineInf();
					posInf.from = lineRangeFrom;
					posInf.to = lineRangeTo;
					posInf.rad=(float) Math.atan2(pto.y-pfrom.y, pto.x-pfrom.x);
					lineInfs.add(posInf);
					addLineFlag=true;
				}else 
				{
					PointF p00=DataCenter.parsePoints.get(lineRangeFrom);
					PointF p01=DataCenter.parsePoints.get(lineRangeTo);
					PointF p10=DataCenter.parsePoints.get(lineInfs.get(lineInfSize-1).from);
					PointF p11=DataCenter.parsePoints.get(lineInfs.get(lineInfSize-1).to);
					if((p00.x-p01.x)*(p00.x-p01.x)+(p00.y-p01.y)*(p00.y-p01.y)>
						(p10.x-p11.x)*(p10.x-p11.x)+(p10.y-p11.y)*(p10.y-p11.y))
					{
						lineInfs.get(lineInfSize-1).from=lineRangeFrom;
						lineInfs.get(lineInfSize-1).to=lineRangeTo;
					}
				}
			}
		}else
		{
			lineRangeFrom=lineRangeTo=-1;
		}
		
		float rotRad = 0;
		if(lineInfSize>1
		&&lineRangeFrom==lineInfs.get(lineInfs.size()-1).from
		&&addLineFlag==false)
		{
			int curAdjustMode=-1;
			int detMode = -1;
			// 使用当前最长的校正线
			float maxLen=0;
			int curIndex=-1;
			for(int i=0;i<lineInfSize;i++)
			{
				int from = lineInfs.get(i).from;
				int to = lineInfs.get(i).to;
				if(from==lineRangeFrom)  {  continue;}
				PointF p1=DataCenter.parsePoints.get(from);
				PointF p2=DataCenter.parsePoints.get(to);
				float ptopLen=(float) Math.sqrt((p1.x-p2.x)*(p1.x-p2.x)+(p1.y-p2.y)*(p1.y-p2.y));
				if(ptopLen>maxLen)
				{
					maxLen=ptopLen;
					curIndex=i;
				}
			}
			
			if(curIndex!=-1)
			{
				int from = lineInfs.get(curIndex).from;
				int to = lineInfs.get(curIndex).to;
				//if(from==lineRangeFrom)  {  continue;}
				PointF p1=DataCenter.parsePoints.get(from);
				PointF p2=DataCenter.parsePoints.get(to);
				float ptopLen=(float) Math.sqrt((p1.x-p2.x)*(p1.x-p2.x)+(p1.y-p2.y)*(p1.y-p2.y));
				boolean findFlag = false;
				int adjustMode = -1;

				/*
				if(findFlag==false)
				{
					
					p0.x=(DataCenter.parsePoints.get(lineRangeFrom).x+DataCenter.parsePoints.get(lineRangeFrom+1).x)*.5f;
					p0.y=(DataCenter.parsePoints.get(lineRangeFrom).y+DataCenter.parsePoints.get(lineRangeFrom+1).y)*.5f;
					ptoLineObj = Utility.pToLine(p0,p1,p2,pToLineThresh);
					findFlag = ptoLineObj.has&&(ptoLineObj.inf==2);
					if(findFlag) adjustMode = 0;
					detMode = 2;
				}

				if(findFlag==false)
				{
					p0.set(thisPos.x,thisPos.y);
					ptoLineObj = Utility.pToLine(p0,p1,p2,pToLineThresh);
					findFlag = ptoLineObj.has&&(ptoLineObj.inf==2);
					if(findFlag) adjustMode = 0;
					detMode = 1;
				}
				*/
				if(findFlag==false)
				{
					PointF pLocal1=DataCenter.parsePoints.get(lineRangeFrom);
					PointF pLocal2=DataCenter.parsePoints.get(lineRangeTo);
					if(ptopLen>adjustLen&&PointFtoLength(pLocal1,pLocal2)>adjustedLen)
					{
						float targetRad = (float) Math.atan2(p2.x-p1.x,p2.y-p1.y);//begin->rad;//
						float rad = (float) Math.atan2(pLocal2.x - pLocal1.x,pLocal2.y-pLocal1.y);//findLine.rad;//
						float deltaRad = Utility.getDeltaRad(targetRad,rad);
						if(deltaRad<adjustThresh||deltaRad>Utility.toRad(180)-adjustThresh)  adjustMode = 0;
						if(Math.abs(deltaRad-Utility.toRad(90))<adjustThresh)  adjustMode = 1;
						findFlag = adjustMode!=-1;
						detMode = 0;
					}
				}
			
			if(findFlag)
			{
				PointF pLocal1=DataCenter.parsePoints.get(lineRangeFrom);
				PointF pLocal2=DataCenter.parsePoints.get(lineRangeTo);
				float targetRad = (float) Math.atan2(p2.x-p1.x,p2.y-p1.y);//begin->rad;//
					float rad = (float) Math.atan2(pLocal2.x-pLocal1.x,pLocal2.y-pLocal1.y);//findLine.rad;//
					if(adjustMode == 1) 
					{
						targetRad+=M_PI*.5f;
						if(targetRad>M_PI) 
							targetRad -=M_PI*2;
					}
					float deltaRad = Utility.getDeltaRad(targetRad,rad);
					rotRad = deltaRad;
					boolean reverse=false;
					if(deltaRad>Utility.toRad(180)-(detMode!=0?adjustThresh:adjustThresh))//濡傛灉瑙掑害鍋忓樊澶ぇ鏄矾寰勯敊璇??
					{
						targetRad+=M_PI;
						if(targetRad>M_PI*2)	targetRad-=M_PI*2;
						rotRad = Utility.getDeltaRad(targetRad,rad);
						reverse = true;
					}else if(deltaRad>(detMode!=0?adjustThresh:adjustThresh)) 
					{ 
						return 0;
					}
					curAdjustMode = adjustMode;
									
			}else
				return 0;
		}
		
			if(curIndex!=-1)
			{
					int from = lineInfs.get(curIndex).from;
					int to = lineInfs.get(curIndex).to;
					PointF p1=DataCenter.parsePoints.get(from);
					PointF p2=DataCenter.parsePoints.get(to);	
					float targetRad = (float) Math.atan2(p2.x-p1.x,p2.y-p1.y);//lineInfs[minIndex].rad;//
					PointF pLocal1=DataCenter.parsePoints.get(lineRangeFrom);
					PointF pLocal2=DataCenter.parsePoints.get(lineRangeTo);
					float rad = (float) Math.atan2(pLocal2.x-pLocal1.x,pLocal2.y-pLocal1.y);//findLine.rad;
					if(curAdjustMode == 1) {
						targetRad+=M_PI*.5;
						if(targetRad>M_PI) 
							targetRad = (float) (-M_PI*2+targetRad);
					}
					float deltaRad = Utility.getDeltaRad(targetRad,rad);
					rotRad = deltaRad;
					
					boolean reverse=false;
					if(deltaRad>Utility.toRad(180)-(detMode!=0?adjustThresh:adjustThresh))
					{
						targetRad+=M_PI;
						if(targetRad>M_PI*2)	targetRad-=M_PI*2;
						rotRad = Utility.getDeltaRad(targetRad,rad);
						reverse = true;
					}
					
					targetRad-=rad;
					if(targetRad>M_PI||(targetRad<0&&targetRad>-M_PI))
							rotRad *=-1;
						int adjustIndex = lineRangeFrom;
						if(DataCenter.adjust3.lastAdjustIndex==-1)
							DataCenter.adjust3.lastAdjustIndex=this.lineInfs.get(0).to;
						Log.d("tag",String.valueOf(-rotRad*180/Math.PI));
							
						return -rotRad;
					
			}
		}
		
		return 0;
	}

	
	private  void AdjustDriftByLineOld(Vec4 p)
	{
		//findLine.addPoint(p.x,p.y);
		findRot.isOver(lastAdjustRad, adjustIntervalRad);
		AdjustCurve(false);
		Vec4 thisPos=new Vec4(0,0,0,0);
		Vec3 temp0=new Vec3(0,0,0),temp1=new Vec3(0,0,0);
		temp1.copyZ(p);
		adjustMat.mat4_concatenate_vec(temp0,temp1);
		thisPos.x=temp0.x;
		thisPos.y=temp0.y;
		thisPos.z=temp0.z;
		thisPos.w= p.w;
		
		int lineInfSize=lineInfs.size();
		PointF pfrom,pto;
		if(findLine.changed)	
		{
				if(lineInfSize>0)//&&lineRangeFrom==lineInfs.get(lineInfSize-1).from)
				{
					PointF p0=DataCenter.parsePoints.get(lineInfs.get(lineInfSize-1).from);
					PointF p1=DataCenter.parsePoints.get(lineInfs.get(lineInfSize-1).to);
					if((p1.x-p0.x)*(p1.x-p0.x)+(p1.y-p0.y)*(p1.y-p0.y)<adjustLen*adjustLen)
					{
						////debugStr+="remove line:"+lineInfs.get(lineInfSize-1).from+","+lineInfs.get(lineInfSize-1).to+"\n";
						lineInfs.remove(lineInfSize-1);
					}
				}
				lineRangeFrom=lineRangeTo=-1;
				findLine.changed= false;
		}
		lineInfSize=lineInfs.size();
		System.out.println(DataCenter.parsePoints.size()+","+findLine.points.size());
		boolean addLineFlag=false;
		if(findLine.isLine())
		{
			lineRangeFrom=DataCenter.parsePoints.size()-findLine.points.size();//+1;
			lineRangeTo=DataCenter.parsePoints.size()-1;
			pfrom = DataCenter.parsePoints.get(lineRangeFrom);
			pto = DataCenter.parsePoints.get(lineRangeTo);
			float deltax=pto.x-pfrom.x;
			float deltay=pto.y-pfrom.y;
			float len2=deltax*deltax+deltay*deltay;
			if(len2>adjustedLen*adjustedLen&&lineRangeFrom!=lineRangeTo-1)//防止跳跃lineRangeFrom!=lineRangeTo-1
			{
				if(lineInfSize>0&&lineInfs.get(lineInfSize-1).from==lineRangeFrom)
					lineInfs.get(lineInfSize-1).to=lineRangeTo;
				else if(lineInfs.size()==0||lineRangeFrom>=lineInfs.get(lineInfSize-1).to)
				{
					LineInf posInf=new LineInf();
					posInf.from = lineRangeFrom;
					posInf.to = lineRangeTo;
					posInf.rad=(float) Math.atan2(pto.y-pfrom.y, pto.x-pfrom.x);
					lineInfs.add(posInf);
					addLineFlag=true;
					////debugStr+="add line:"+lineRangeFrom+","+lineRangeTo+"\n";
				}else 
				{
					PointF p00=DataCenter.parsePoints.get(lineRangeFrom);
					PointF p01=DataCenter.parsePoints.get(lineRangeTo);
					PointF p10=DataCenter.parsePoints.get(lineInfs.get(lineInfSize-1).from);
					PointF p11=DataCenter.parsePoints.get(lineInfs.get(lineInfSize-1).to);
					if((p00.x-p01.x)*(p00.x-p01.x)+(p00.y-p01.y)*(p00.y-p01.y)>
						(p10.x-p11.x)*(p10.x-p11.x)+(p10.y-p11.y)*(p10.y-p11.y))
					{
						lineInfs.get(lineInfSize-1).from=lineRangeFrom;
						lineInfs.get(lineInfSize-1).to=lineRangeTo;
					}

				}
			}
		}else
		{
			lineRangeFrom=lineRangeTo=-1;
		}
		

		float rotRad = 0;
		if(lineInfSize>1
		&&lineRangeFrom==lineInfs.get(lineInfs.size()-1).from
		&&(addLineFlag==false||findRot.isOverFlag))
		{
			int curAdjustMode=-1;
			int detMode = -1;
			// 使用当前最长的校正线
			float maxLen=0;
			int curIndex=-1;
			for(int i=0;i<lineInfSize;i++)
			{
				int from = lineInfs.get(i).from;
				int to = lineInfs.get(i).to;
				if(from==lineRangeFrom)  {  continue;}
				PointF p1=DataCenter.parsePoints.get(from);
				PointF p2=DataCenter.parsePoints.get(to);
				float ptopLen=(float) Math.sqrt((p1.x-p2.x)*(p1.x-p2.x)+(p1.y-p2.y)*(p1.y-p2.y));
				if(ptopLen>maxLen)
				{
					maxLen=ptopLen;
					curIndex=i;
				}
			}
			
			if(curIndex!=-1)
			{
				int from = lineInfs.get(curIndex).from;
				int to = lineInfs.get(curIndex).to;
				//if(from==lineRangeFrom)  {  continue;}
				PointF p1=DataCenter.parsePoints.get(from);
				PointF p2=DataCenter.parsePoints.get(to);
				float ptopLen=(float) Math.sqrt((p1.x-p2.x)*(p1.x-p2.x)+(p1.y-p2.y)*(p1.y-p2.y));
				boolean findFlag = false;
				int adjustMode = -1;

				/*
				if(findFlag==false)
				{
					
					p0.x=(DataCenter.parsePoints.get(lineRangeFrom).x+DataCenter.parsePoints.get(lineRangeFrom+1).x)*.5f;
					p0.y=(DataCenter.parsePoints.get(lineRangeFrom).y+DataCenter.parsePoints.get(lineRangeFrom+1).y)*.5f;
					ptoLineObj = Utility.pToLine(p0,p1,p2,pToLineThresh);
					findFlag = ptoLineObj.has&&(ptoLineObj.inf==2);
					if(findFlag) adjustMode = 0;
					detMode = 2;
				}

				if(findFlag==false)
				{
					p0.set(thisPos.x,thisPos.y);
					ptoLineObj = Utility.pToLine(p0,p1,p2,pToLineThresh);
					findFlag = ptoLineObj.has&&(ptoLineObj.inf==2);
					if(findFlag) adjustMode = 0;
					detMode = 1;
				}
				*/
				if(findFlag==false)
				{
					PointF pLocal1=DataCenter.parsePoints.get(lineRangeFrom);
					PointF pLocal2=DataCenter.parsePoints.get(lineRangeTo);
					if(ptopLen>adjustLen&&PointFtoLength(pLocal1,pLocal2)>adjustedLen)
					{
						float targetRad = (float) Math.atan2(p2.x-p1.x,p2.y-p1.y);//begin->rad;//
						float rad = (float) Math.atan2(pLocal2.x - pLocal1.x,pLocal2.y-pLocal1.y);//findLine.rad;//
						float deltaRad = Utility.getDeltaRad(targetRad,rad);
						if(deltaRad<adjustThresh||deltaRad>Utility.toRad(180)-adjustThresh)  adjustMode = 0;
						if(Math.abs(deltaRad-Utility.toRad(90))<adjustThresh)  adjustMode = 1;
						findFlag = adjustMode!=-1;
						detMode = 0;
					}
				}
			
			if(findFlag)
			{
				PointF pLocal1=DataCenter.parsePoints.get(lineRangeFrom);
				PointF pLocal2=DataCenter.parsePoints.get(lineRangeTo);
				float targetRad = (float) Math.atan2(p2.x-p1.x,p2.y-p1.y);//begin->rad;//
					float rad = (float) Math.atan2(pLocal2.x-pLocal1.x,pLocal2.y-pLocal1.y);//findLine.rad;//
					if(adjustMode == 1) 
					{
						targetRad+=M_PI*.5f;
						if(targetRad>M_PI) 
							targetRad -=M_PI*2;
					}
					float deltaRad = Utility.getDeltaRad(targetRad,rad);
					rotRad = deltaRad;
					boolean reverse=false;
					if(deltaRad>Utility.toRad(180)-(detMode!=0?adjustThresh:adjustThresh))//濡傛灉瑙掑害鍋忓樊澶ぇ鏄矾寰勯敊璇??
					{
						targetRad+=M_PI;
						if(targetRad>M_PI*2)	targetRad-=M_PI*2;
						rotRad = Utility.getDeltaRad(targetRad,rad);
						reverse = true;
					}else if(deltaRad>(detMode!=0?adjustThresh:adjustThresh)) 
					{ 
						return;
					}
					curAdjustMode = adjustMode;
									
			}else
				return;
		}
		
			if(curIndex!=-1)
			{
					int from = lineInfs.get(curIndex).from;
					int to = lineInfs.get(curIndex).to;
					PointF p1=DataCenter.parsePoints.get(from);
					PointF p2=DataCenter.parsePoints.get(to);	
					float targetRad = (float) Math.atan2(p2.x-p1.x,p2.y-p1.y);//lineInfs[minIndex].rad;//
					PointF pLocal1=DataCenter.parsePoints.get(lineRangeFrom);
					PointF pLocal2=DataCenter.parsePoints.get(lineRangeTo);
					float rad = (float) Math.atan2(pLocal2.x-pLocal1.x,pLocal2.y-pLocal1.y);//findLine.rad;
					if(curAdjustMode == 1) {
						targetRad+=M_PI*.5;
						if(targetRad>M_PI) 
							targetRad = (float) (-M_PI*2+targetRad);
					}
					float deltaRad = Utility.getDeltaRad(targetRad,rad);
					rotRad = deltaRad;
					
					boolean reverse=false;
					if(deltaRad>Utility.toRad(180)-(detMode!=0?adjustThresh:adjustThresh))
					{
						targetRad+=M_PI;
						if(targetRad>M_PI*2)	targetRad-=M_PI*2;
						rotRad = Utility.getDeltaRad(targetRad,rad);
						reverse = true;
					}
					
					targetRad-=rad;
					if(targetRad>M_PI||(targetRad<0&&targetRad>-M_PI))
							rotRad *=-1;
						{
						//adjustFlag = true;
						int adjustIndex = lineRangeFrom;//adjustPoints.size()?adjustPoints.back():lineRangeFrom;
						float offsetx=DataCenter.parsePoints.get(adjustIndex).x;
						float offsety=DataCenter.parsePoints.get(adjustIndex).y;
						
						Matrix4 m40=new Matrix4(),m41=new Matrix4();
						m40.make_trans( 0, 0, 0);
						m41.make_trans( 0, 0, 0);
						
						m40.make_trans(-offsetx,-offsety,0);
						m41.mat3_from_axis_angle(-rotRad);// 和c++相反
						m41.mat4_concatenate_copy(m41, m40);
						m40.make_trans(offsetx, offsety, 0);
						m40.mat4_concatenate_copy(m41, m41);
						Vec3 vec=new Vec3(0,0,0);
						for(int j=adjustIndex;j<=lineRangeTo;j++)
						{
							vec.x=DataCenter.parsePoints.get(j).x;
							vec.y=DataCenter.parsePoints.get(j).y;
							m41.mat4_concatenate_vec_copy(vec);
							DataCenter.parsePoints.get(j).x=vec.x; 
							DataCenter.parsePoints.get(j).y=vec.y; 
						}
						m41.mat4_concatenate_copy(adjustMat,adjustMat);
						
						AdjustInf inf=new AdjustInf();
						adjustInfs.add(inf);
						inf.from=adjustIndex;
						inf.to=DataCenter.parsePoints.size()-1;//lineRangeTo+1
						inf.invM4=m41.inverse();//adjustMat.inverse();
						{
							PointF linep1=DataCenter.parsePoints.get(adjustIndex);
							PointF linep2=DataCenter.parsePoints.get(lineRangeTo);	
							lastAdjustRad=lineInfs.get(lineInfs.size()-1).rad = (float) Math.atan2(linep2.y-linep1.y,linep2.x-linep1.x);//lineInfs[minIndex].rad;//
						}
						findRot.reset();
						//checkAdjust();
						AdjustCurve(true);
					}
			}
		}
	}

	
	int curveIndex=-1;
	float curveLength=0;
	public  void AdjustCurve(boolean clear)
	{
		clear=true;
		if(clear)
		{
			curveIndex=-1;
			curveLength=0;
			return;
		}
		float curveThresh=this.adjustedLen*2;
		
		int pointsNum=DataCenter.parsePoints.size();
		if(pointsNum>2)
		{
			PointF lastP1=DataCenter.parsePoints.get(pointsNum-1);
			PointF lastP2=DataCenter.parsePoints.get(pointsNum-2);
			PointF lastP3=DataCenter.parsePoints.get(pointsNum-3);
			float rad=Utility.getDeltaRad((float)Math.atan2(lastP1.y-lastP2.y, lastP1.x-lastP2.x),
									(float)Math.atan2(lastP2.y-lastP3.y, lastP2.x-lastP3.x));
			System.out.println("rad:"+rad*180/Math.PI);
			if(rad>Math.PI*175/180.0f)
			{
				curveIndex=pointsNum-2;
				curveLength=0;
			}else if(curveIndex!=-1&&rad<Math.PI*90/180.0f)
			{
				curveLength+=this.PointFtoLength(lastP1, lastP2);
				if(curveLength>curveThresh)
				{
					PointF curveP=DataCenter.parsePoints.get(curveIndex);
					PointF reverseP=findPointByLength(curveIndex,curveLength);
					float rad0=(float) Math.atan2(reverseP.y-curveP.y,reverseP.x-curveP.x);
					float rad1=(float) Math.atan2(lastP1.y-curveP.y,lastP1.x-curveP.x);
					float deltaRad=Utility.getDeltaRad(rad0, rad1);
					if(deltaRad<this.adjustThresh)
					{
						rad0-=rad1;
						if(rad0>M_PI||(rad0<0&&rad0>-M_PI)) deltaRad*=-1;
						
						Matrix4 m40=new Matrix4(),m41=new Matrix4();
						m40.make_trans( 0, 0, 0);
						m41.make_trans( 0, 0, 0);
						
						m40.make_trans(-curveP.x,-curveP.y,0);
						m41.mat3_from_axis_angle(-deltaRad*0.5f);// 和c++相反
						m41.mat4_concatenate_copy(m41, m40);
						m40.make_trans(curveP.x, curveP.y, 0);
						m40.mat4_concatenate_copy(m41, m41);
						Vec3 vec=new Vec3(0,0,0);
						for(int j=curveIndex+1;j<pointsNum;j++)
						{
							vec.x=DataCenter.parsePoints.get(j).x;
							vec.y=DataCenter.parsePoints.get(j).y;
							m41.mat4_concatenate_vec_copy(vec);
							DataCenter.parsePoints.get(j).x=vec.x; 
							DataCenter.parsePoints.get(j).y=vec.y; 
						}
						m41.mat4_concatenate_copy(adjustMat,adjustMat);
						
						AdjustInf inf=new AdjustInf();
						adjustInfs.add(inf);
						inf.from=curveIndex+1;
						inf.to=DataCenter.parsePoints.size()-1;
						inf.invM4=m41.inverse();
						AdjustCurve(true);
					}
				}
			}else{
				curveIndex=-1;
				curveLength=0;
			}
		}
	}
	
	Vec4 thisPos=new Vec4(0,0,0,0);
	public   void AdjustDrift(Vec4 p)
	{
		
		float sceneScale=1;
		float pToLineThresh2=5;
		float pToLineHeightThresh2=200;
		float stepDelta=1f;
		boolean adjustWeightAuto2=false;
		float adjustAngWeight2=0.1f;
		float adjustOffsetWeight2=0.1f;
		float adjustThresh2 = Utility.toRad(30);
		
		int pointsNum=DataCenter.parsePoints.size();
		
			
		PointF lastP=DataCenter.parsePoints.get(pointsNum-1);

		Vec3 temp0=new Vec3(0,0,0),temp1=new Vec3(p.x,p.y,0);
		adjustMat.mat4_concatenate_vec(temp0,temp1);
		thisPos.x=temp0.x;
		thisPos.y=temp0.y;
		thisPos.z=temp0.z;
		/*
		if((thisPos.x-lastP.x)*(thisPos.x-lastP.x)+(thisPos.y-lastP.y)*(thisPos.y-lastP.y)<stepDelta*stepDelta)
			return;
		*/
		debugP=debugP0=debugP1=debugP2=null;
		float distThresh = sceneScale* pToLineThresh2;
		float heightThresh = sceneScale* pToLineHeightThresh2;
		stepDelta = sceneScale*stepDelta;
		
		Vec3 pLocall=new Vec3(lastP.x,lastP.y,0);
		Vec3 pLocal2=new Vec3(thisPos.x,thisPos.y,0);
		float rad = (float) Math.atan2(pLocal2.x-pLocall.x,pLocal2.y-pLocall.y);
		
		PointF p0=new PointF(thisPos.x,thisPos.y);//,thisPos.z);
		for(int i=0;i<pointsNum-1;i++)
		{
			PointF curP=DataCenter.parsePoints.get(i);
			PointF nxtP=DataCenter.parsePoints.get(i+1);
			if(Math.abs((i-pointsNum))>=2&&
				Math.abs(thisPos.x-curP.x)<distThresh&&
				Math.abs(thisPos.y-curP.y)<distThresh)/*&&
				Math.abs(thisPos.w-curP.w)<heightThresh&&
				Math.abs(thisPos.y-positions.back().y)<stepDelta)// 保证在平地上，上下楼的误差太大*/
			{
					float targetRad = (float) Math.atan2(nxtP.x-curP.x,nxtP.y-curP.y);
					float deltaRad = Utility.getDeltaRad(targetRad,rad);
					float rotRad = deltaRad;
					// 决定旋转方向
					if(deltaRad>M_PI-adjustThresh2)
					{
						targetRad+=M_PI;
						if(targetRad>M_PI*2)	targetRad-=M_PI*2;
						rotRad = Utility.getDeltaRad(targetRad,rad);
					}else if(deltaRad>adjustThresh2) 
					{ 
						//begin++; 
						continue;
					}
					PtoLineObj ptoLineObj = Utility.pToLine(p0,curP,nxtP,distThresh);
					if(ptoLineObj.has==false||ptoLineObj.inf!=2)
					{
						//begin++; 
						continue;
					}
					//以rad为零,targetRad是
					targetRad-=rad;
					if(targetRad>M_PI||(targetRad<0&&targetRad>-M_PI))
							rotRad *=-1;
					float driftAngFac = 0.1f ;
					float driftOffsetFac = 0.1f;
					if(adjustWeightAuto2)
					{
						float temp = 1-Math.abs(rotRad)/adjustThresh2;
						driftOffsetFac=driftAngFac=temp;//(float) Math.pow(temp, 0.5);
					}else{
						driftAngFac = adjustAngWeight2;
						driftOffsetFac = adjustOffsetWeight2;
					}
					{
						Matrix4 m40=new Matrix4(),m41=new Matrix4();
						m40.make_trans(0, 0, 0);
						m41.make_trans(0, 0, 0);
						m40.make_trans(-lastP.x,-lastP.y,0);
						m41.mat3_from_axis_angle(-rotRad*driftAngFac);
						m41.mat4_concatenate_copy(m41,m40);
						//Utility.pToLine(p0,curP,nxtP,distThresh);
						m40.make_trans(lastP.x+(ptoLineObj.point.x-p0.x)*driftOffsetFac,
										lastP.y+(ptoLineObj.point.y-p0.y)*driftOffsetFac,
									   0);
						m40.mat4_concatenate_copy(m41,m41);
						m41.mat4_concatenate_copy(adjustMat,adjustMat);
						System.out.println("driftOffsetFac:"+driftOffsetFac);
						AdjustInf inf=new AdjustInf();
						adjustInfs.add(inf);
						// 只有最后一个点
						inf.from=DataCenter.parsePoints.size();
						inf.to=DataCenter.parsePoints.size();
						inf.invM4=m41.inverse();//adjustMat.inverse();
						checkAdjust();
					}
					debugP=p0;
					debugP0=curP;
					debugP1=nxtP;
					debugP2=ptoLineObj.point;
				break;
			}
		}
	}
	
	public PointF debugP,debugP0,debugP1,debugP2;
	

	
	public void back(int step)
	{
		ArrayList<PointF> points=DataCenter.parsePoints;
		Vec3 pos=new Vec3(0,0,0);
		int preTo=points.size()-1;
		int fromIndex=1000000;
		while(--step>=0&&adjustInfs.size()>0)
		{
			AdjustInf adjustInf= adjustInfs.remove(adjustInfs.size()-1);
			Matrix4 invM4=adjustInf.invM4;
			for(int j=adjustInf.from;j<=preTo;j++)
			{
				pos.x=points.get(j).x;
				pos.y=points.get(j).y;
				invM4.mat4_concatenate_vec_copy(pos);
				points.get(j).x=pos.x;
				points.get(j).y=pos.y;
				
			}
			invM4.mat4_concatenate_copy(adjustMat,adjustMat);
			fromIndex=adjustInf.from;
		}
			
		for(int k=lineInfs.size()-1;k>=0;k--)
			if(lineInfs.get(k).from>=fromIndex)
				lineInfs.remove(k);
		
	}
	
	public void backByIndex(int from)
	{
		ArrayList<PointF> points=DataCenter.parsePoints;
		Vec3 pos=new Vec3(0,0,0);
		int preTo=points.size()-1;
		int fromIndex=1000000;
		while(adjustInfs.size()>0)
		{
			AdjustInf adjustInf= adjustInfs.remove(adjustInfs.size()-1);
			if(adjustInf.to>=from)
			{
				Matrix4 invM4=adjustInf.invM4;
				for(int j=adjustInf.from;j<=preTo;j++)
				{
					pos.x=points.get(j).x;
					pos.y=points.get(j).y;
					invM4.mat4_concatenate_vec_copy(pos);
					points.get(j).x=pos.x;
					points.get(j).y=pos.y;
				}
				invM4.mat4_concatenate_copy(adjustMat,adjustMat);
				fromIndex=adjustInf.from;
			}else{
				adjustInfs.add(adjustInf);
				break;
			}
		}
		for(int k=lineInfs.size()-1;k>=0;k--)
			if(lineInfs.get(k).from>=fromIndex)
				lineInfs.remove(k);
			else
				break;
		
	}
	
	public int writeData(byte[] bytes,int offset)
	{
		return this.adjustInfClient.writeData(bytes, offset);
	}
	
	
	public void reset()
	{
		this.lineInfs.clear();
		this.adjustInfs.clear();
		adjustMat.make_trans(0, 0, 0);
		lastRealPos.x=lastRealPos.y=lastRealPos.z=lastRealPos.w=0;
		preHeight=-100;
	}
	
}
