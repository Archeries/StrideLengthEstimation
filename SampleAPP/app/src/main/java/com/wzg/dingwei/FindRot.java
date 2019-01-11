package com.wzg.dingwei;
import java.util.ArrayList;

import android.graphics.PointF;



public class FindRot {
	
	float preRad=0;
	public float totalRad=0;
	int size=0;
	
	boolean isOverFlag=false;
	public boolean isOver(float lastRad,float threshRad)
	{
		size++;
		ArrayList<PointF> points=DataCenter.parsePoints;
		if(size>=2)
		{
			//PointF p1=points.get(0);
			//PointF p2=points.get(1);
			PointF pn1=points.get(points.size()-1);
			PointF pn2=points.get(points.size()-2);
			//float rad0=(float) Math.atan2(p2.y-p1.y, p2.x-p1.x);
			float rad1=(float) Math.atan2(pn1.y-pn2.y, pn1.x-pn2.x);
			double rad=Math.abs(Utility.getDeltaRad(lastRad, rad1));
			//rad1-=preRad;
			//if(rad1>Math.PI||(rad1<0&&rad1>-Math.PI))	rad *=-1;
			System.out.println("rot:"+rad*180/Math.PI);
			isOverFlag=isOverFlag||rad>threshRad;
			
		}
		return isOverFlag;
	}
	
	public void reset()
	{
		isOverFlag=false;
		totalRad=0;
		size=0;
	}
	
	public void updatePoint2()
	{
		size++;
		ArrayList<PointF> points=DataCenter.parsePoints;
		if(size>=2)
		{
			//PointF p1=points.get(0);
			//PointF p2=points.get(1);
			PointF pn1=points.get(points.size()-1);
			PointF pn2=points.get(points.size()-2);
			//float rad0=(float) Math.atan2(p2.y-p1.y, p2.x-p1.x);
			float rad1=(float) Math.atan2(pn1.y-pn2.y, pn1.x-pn2.x);
			
			if(size>2){
				double rad=Utility.getDeltaRad(preRad, rad1);
				//rad1-=preRad;
				//if(rad1>Math.PI||(rad1<0&&rad1>-Math.PI))	rad *=-1;
				System.out.println("rot:"+rad*180/Math.PI);
				totalRad+=rad;
			}
			
			preRad=rad1;
			
		}
	}
	
	
	
}
