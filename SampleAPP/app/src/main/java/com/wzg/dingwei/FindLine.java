package com.wzg.dingwei;

import java.util.ArrayList;

import android.graphics.PointF;

public class FindLine {

	float maxDist=0.2f;
	float sumxy=0,sumxx=0,sumyy=0,sumx=0,sumy=0,a,b,rad,curvDist,curvParam;;
	int dir=0;
	boolean lineFlag=false;
	public ArrayList<PointF> points = new ArrayList<PointF>();
	int posCount=0,nagCount=0;
	boolean changed=false;
	public void addPoint(float x,float y)
	{
		points.add(new PointF(x,y));
		if(points.size()==2) lineFlag=true;
		sumx+=x;
		sumy+=y;
		sumxy+=x*y;
		sumxx+=x*x;
		sumyy+=y*y;

			if(calc())
			{
				sumx-=points.get(0).x;
				sumy-=points.get(0).y;
				sumxy-=points.get(0).x*points.get(0).y;
				sumxx-=points.get(0).x*points.get(0).x;
				sumyy-=points.get(0).y*points.get(0).y;
				points.remove(0);
				changed=true;
				if(calc()) reset();
			}
	}
	
	public void reset()
	{
		sumy=sumx=sumxx=sumxy=sumyy=0;
		posCount=nagCount=0;
		
		if(points.size()>2)
		{
			PointF p0=points.get(points.size()-2);
			PointF p1=points.get(points.size()-1);
			points.clear();
			lineFlag=false;
			addPoint(p0.x,p0.y);
			addPoint(p1.x,p1.y);
		}else
		{
			points.clear();
			lineFlag=false;
		}
		changed=true;
	}

	public boolean calc()
	{
		float averagex,averagey;
		int size=points.size();
		if(size>2)
		{
			averagex = sumx/size;
			averagey = sumy/size;
			lineFlag=true;
			boolean resetFlag=false;
			if(Math.abs(points.get(size-1).x-points.get(0).x)>Math.abs(points.get(size-1).y-points.get(0).y))
			{
				dir=0;
				b = (sumxy - size*averagex*averagey)/(sumxx - size*averagex*averagex);
				a = averagey - b*averagex;
				float p0x=0,p0y=a;
				float p1x=1000,p1y=a+1000*b;
				float dx = 1000;
				float dy = 1000*b;
				float curMaxDist=-100000;
				for(int i=0;i<size;i++)
				{
					float px = points.get(i).x ;
					float py = points.get(i).y - a;
					float dist =(float) ((dx * py - dy * px) / Math.sqrt(dx * dx + dy * dy));
					if(Math.abs(dist)>curMaxDist) curMaxDist=Math.abs(dist);
					if(Math.abs(dist)>maxDist)
					{
						resetFlag=true;
						break;
					}
				}
				/*
				if(resetFlag==false&&size>=5)
				{
					int from=(size-1)/2;
					float curvedx=points.get(size-1).x-points.get(0).x;
					float curvedy=points.get(size-1).y-points.get(0).y;
					int dirCount=0;
					for(int i=from;i<from+3;i++)
					{
						float px = points.get(i).x - points.get(0).x ;
						float py = points.get(i).y - points.get(0).y;
						float dist =(float) ((curvedx * py - curvedy * px) / Math.sqrt(curvedx * curvedx + curvedy * curvedy));
						if(dist>0) dirCount++;
					}
					if(dirCount==0||dirCount==3) resetFlag=true;
					
				}
				*/
				rad = (float) Math.atan2(dx,dy);
				curvDist=curMaxDist;
				return resetFlag;
			}else
			{
				dir=1;
				b = (sumxy - size*averagex*averagey)/(sumyy - size*averagey*averagey);
				a = averagex - b*averagey;
				float p0x=0,p0y=a;
				float p1x=1000,p1y=a+1000*b;
				float dx = 1000;
				float dy = 1000*b;
				float curMaxDist=-100000;
				for(int i=0;i<size;i++)
				{
					float px = points.get(i).y ;
					float py = points.get(i).x - a;
					float dist =(float) ((dx * py - dy * px) / Math.sqrt(dx * dx + dy * dy));
					if(Math.abs(dist)>curMaxDist) curMaxDist=Math.abs(dist);
					if(Math.abs(dist)>maxDist)
					{
						resetFlag=true;
						break;
					}
				}
				/*
				if(resetFlag==false&&size>=5)
				{
					int from=(size-1)/2;
					float curvedx=points.get(size-1).x-points.get(0).x;
					float curvedy=points.get(size-1).y-points.get(0).y;
					int dirCount=0;
					for(int i=from;i<from+3;i++)
					{
						float px = points.get(i).x - points.get(0).x ;
						float py = points.get(i).y - points.get(0).y;
						float dist =(float) ((curvedx * py - curvedy * px) / Math.sqrt(curvedx * curvedx + curvedy * curvedy));
						if(dist>0) dirCount++;
					}
					if(dirCount==0||dirCount==3) resetFlag=true;
					
				}
				*/
				rad = (float) Math.atan2(dy,dx);
				curvDist=curMaxDist;
				return resetFlag;
			}
		}
		return false;
	}

	
	public float getLength()
	{
		if(points.size()==0) return 0;
		float deltax=points.get(points.size()-1).x-points.get(0).x;
		float deltay=points.get(points.size()-1).y-points.get(0).y;
		return (float) Math.sqrt(deltax*deltax + deltay*deltay);
	}

	public float getRad()
	{
		return (float) Math.atan2(points.get(points.size()-1).y-points.get(0).y,points.get(points.size()-1).x-points.get(0).x);
	}
	
	boolean isLine(){
		return lineFlag;
	}
	
	boolean isCurve()
	{
				
		int size=points.size();
		if(size>2)
		{
			PointF p0=points.get(0);
			PointF p1=points.get(1);
			PointF pn0=points.get(size-2);
			PointF pn1=points.get(size-1);	
			float rad0=(float) Math.atan2(p1.y-p0.y, p1.x-p0.x);
			float rad1=(float) Math.atan2(pn1.y-pn0.y, pn1.x-pn0.x);
			System.out.println(Utility.getDeltaRad(rad0, rad1)*180/Math.PI);
			/*
			if(Utility.getDeltaRad(rad0, rad1)>3*Math.PI/180.0f)
			{
				return true;
			}
			*/
		}
		return false;
	}
}
